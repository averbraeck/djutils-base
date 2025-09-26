package org.djutils.logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.spi.CallerBoundaryAware;
import org.slf4j.spi.LoggingEventBuilder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

/**
 * The CategoryLogger can log for specific Categories. The way to call the logger for messages that always need to be logged,
 * such as an error with an exception is:
 * 
 * <pre>
 * CategoryLogger.always().error(exception, "Parameter {} did not initialize correctly", param1.toString());
 * </pre>
 * 
 * It is also possible to indicate the category / categories for the message, which will only be logged if at least one of the
 * indicated categories is turned on with addLogCategory() or setLogCategories(), or if one of the added or set LogCategories is
 * LogCategory.ALL:
 * 
 * <pre>
 * CategoryLogger.filter(Cat.BASE).debug("Parameter {} initialized correctly", param1.toString());
 * </pre>
 * <p>
 * Copyright (c) 2018-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
@SuppressWarnings("checkstyle:needbraces")
public final class CategoryLogger
{
    /** Has the CategoryLogger been initialized? */
    private static volatile boolean initialized = false;

    /** The LoggerContext to store settings, appenders, etc. */
    private static final LoggerContext CTX = (LoggerContext) LoggerFactory.getILoggerFactory();

    /** The default message format. */
    public static final String DEFAULT_PATTERN = "%date{HH:mm:ss} %-5level %-6logger{0} %class.%method:%line - %msg%n";

    /** The current message format. */
    private static String defaultPattern = DEFAULT_PATTERN;

    /** The default logging level. */
    private static final Level DEFAULT_LEVEL = Level.INFO;

    /** The current default logging level for new category loggers. */
    private static Level defaultLevel = DEFAULT_LEVEL;

    /** The levels and pattern used per LogCategory. */
    private static final Map<LogCategory, CategoryConfig> CATEGORY_CFG = new LinkedHashMap<>();

    /** The logger and appenders used per LogCategory. */
    private static final Map<LogCategory, CategoryState> CATEGORY_STATE = new LinkedHashMap<>();

    /** The factory for appenders, with an id for later removal. */
    private static final Map<String, CategoryAppenderFactory> APPENDER_FACTORIES = new LinkedHashMap<>();

    /** The delegate loggers per category. */
    private static final Map<LogCategory, DelegateLogger> DELEGATES = new ConcurrentHashMap<>();

    /** The log category for the always() method. */
    public static final LogCategory CAT_ALWAYS = new LogCategory("ALWAYS");

    /** The base DelegateLogger for the always() method. */
    private static final DelegateLogger BASE_DELEGATE = new DelegateLogger(LoggerFactory.getLogger(CAT_ALWAYS.toString()));

    /** The NO_LOGGER is the DelegateLogger that does not output anything after when() or filter(). */
    private static final DelegateLogger NO_LOGGER = new DelegateLogger(null, CategoryLogger.DelegateLogger.class, false);

    /** */
    private CategoryLogger()
    {
        // Utility class.
    }

    /* ---------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------- INTERNAL HELPER METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------------- */

    /**
     * Check if the CategoryLogger has been initialized, and initialize the class when not.
     */
    private static void ensureInit()
    {
        if (initialized)
            return;
        synchronized (CategoryLogger.class)
        {
            if (initialized)
                return;
            // Bootstrap default category so .always() works immediately
            initialized = true;
            addLogCategory(CAT_ALWAYS);
            setLogLevel(CAT_ALWAYS, Level.TRACE);
            addLogCategory(LogCategory.ALL);
            setLogLevel(LogCategory.ALL, defaultLevel);
        }
    }

    /**
     * Prepare a Logger for the provided log category, and give it at least a console appender.
     * @param category the log category
     * @param cfg the record with the configuration for this category
     */
    private static void wireCategoryLogger(final LogCategory category, final CategoryConfig cfg)
    {
        Logger logger = getOrCreateLogger(category);
        logger.setAdditive(false);
        logger.setLevel(cfg.level);
        CategoryState st = new CategoryState(logger);
        CATEGORY_STATE.put(category, st);

        // Create per-category instances for every registered factory
        for (CategoryAppenderFactory f : APPENDER_FACTORIES.values())
        {
            Appender<ILoggingEvent> app = f.create(f.id(), category, cfg.pattern, CTX);
            app.start();
            logger.addAppender(app);
            st.appendersByFactoryId.put(f.id(), app);
        }

        // If no factories yet, at least wire a default console appender for visibility
        if (APPENDER_FACTORIES.isEmpty())
        {
            CategoryAppenderFactory fallback = new ConsoleAppenderFactory("CONSOLE");
            APPENDER_FACTORIES.putIfAbsent("CONSOLE", fallback);
            Appender<ILoggingEvent> app = fallback.create("CONSOLE", category, cfg.pattern, CTX);
            app.start();
            logger.addAppender(app);
            st.appendersByFactoryId.put("CONSOLE", app);
        }
    }

    /**
     * Give a logger for a log category its appenders.
     * @param category the log category
     */
    private static void rebuildCategoryAppenders(final LogCategory category)
    {
        CategoryState st = CATEGORY_STATE.get(category);
        CategoryConfig cfg = CATEGORY_CFG.get(category);
        if (st == null || cfg == null)
            return;

        // detach & stop existing
        st.appendersByFactoryId.forEach((id, app) ->
        {
            st.logger.detachAppender(app);
            safeStop(app);
        });
        st.appendersByFactoryId.clear();

        // build with new pattern
        for (CategoryAppenderFactory f : APPENDER_FACTORIES.values())
        {
            Appender<ILoggingEvent> app = f.create(f.id(), category, cfg.pattern, CTX);
            app.start();
            st.logger.addAppender(app);
            st.appendersByFactoryId.put(f.id(), app);
        }
    }

    /**
     * Get an existing logger based on its name, or create it when it does not exist yet.
     * @param category the category with the name under which the logger is registered
     * @return an existing logger based on its name, or create it when it does not exist yet
     */
    private static Logger getOrCreateLogger(final LogCategory category)
    {
        Logger logger = CTX.getLogger(category.toString());
        return logger;
    }

    /**
     * Stop an appender for when we change the configuration.
     * @param appender the appender to stop
     */
    private static void safeStop(final Appender<ILoggingEvent> appender)
    {
        try
        {
            appender.stop();
        }
        catch (RuntimeException ignore)
        {
        }
    }

    /* ---------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ CATEGORYLOGGER API METHODS ------------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------------------- */

    /**
     * Always log to the registered appenders, still observing the default log level.
     * @return the DelegateLogger for method chaining, e.g., CategoryLogger.always().info("message");
     */
    public static DelegateLogger always()
    {
        ensureInit();
        return BASE_DELEGATE;
    }

    /**
     * Only log when the condition is true.
     * @param condition the condition to check
     * @return the DelegateLogger for method chaining, e.g., CategoryLogger.when(condition).info("message");
     */
    public static DelegateLogger when(final boolean condition)
    {
        ensureInit();
        return condition ? BASE_DELEGATE : NO_LOGGER;
    }

    /**
     * Only log when the boolean supplier provides a true value.
     * @param booleanSupplier the supplier that provides true or false
     * @return the DelegateLogger for method chaining, e.g., CategoryLogger.when(() -> condition()).info("message");
     */
    public static DelegateLogger when(final BooleanSupplier booleanSupplier)
    {
        return when(booleanSupplier.getAsBoolean());
    }

    /**
     * Only log when the category has been registered in the CategoryLogger.
     * @param category the category to check
     * @return the DelegateLogger for method chaining, e.g., CategoryLogger.filter(Cat.BASE).info("message");
     */
    public static DelegateLogger filter(final LogCategory category)
    {
        ensureInit();
        return DELEGATES.getOrDefault(category, NO_LOGGER);
    }

    /**
     * Register a log category that can log with the CategoryLogger. Note that unregistered loggers for which you use filter()
     * do not log.
     * @param category the log category to register.
     */
    public static synchronized void addLogCategory(final LogCategory category)
    {
        ensureInit();
        if (CATEGORY_CFG.containsKey(category))
            return;
        CategoryConfig cfg = new CategoryConfig(defaultLevel, defaultPattern);
        CATEGORY_CFG.put(category, cfg);
        org.slf4j.Logger slf = LoggerFactory.getLogger(category.toString());
        var delegate = new DelegateLogger(slf);
        DELEGATES.put(category, delegate);
        wireCategoryLogger(category, cfg);
    }

    /**
     * Remove a log category from logging with the CategoryLogger. Note that unregistered loggers for which you use filter() do
     * not log.
     * @param category the log category to unregister.
     */
    public static synchronized void removeLogCategory(final LogCategory category)
    {
        ensureInit();
        CategoryState st = CATEGORY_STATE.remove(category);
        CATEGORY_CFG.remove(category);
        if (st != null)
        {
            // detach & stop this category's appenders
            Logger logger = st.logger;
            st.appendersByFactoryId.values().forEach(app ->
            {
                logger.detachAppender(app);
                safeStop(app);
            });
            // silence the logger
            logger.setLevel(Level.OFF);
            logger.setAdditive(false);
        }
        DELEGATES.remove(category);
    }

    /**
     * Return the registered appenders for the LogCategory.
     * @param category the category to look up
     * @return the appenders for the LogCategory
     */
    public static Collection<Appender<ILoggingEvent>> getAppenders(final LogCategory category)
    {
        ensureInit();
        var st = CATEGORY_STATE.get(category);
        return st == null ? new HashSet<>() : st.appendersByFactoryId.values();
    }

    /**
     * Set the log category for a single log category.
     * @param category the log category
     * @param level the new log level
     */
    public static synchronized void setLogLevel(final LogCategory category, final Level level)
    {
        ensureInit();
        addLogCategory(category); // create if missing
        CATEGORY_CFG.get(category).level = level;
        Logger logger = CATEGORY_STATE.get(category).logger;
        logger.setLevel(level);
    }

    /**
     * Set the log category for all log categories, except ALWAYS.
     * @param level the new log level for all log categories, except ALWAYS
     */
    public static synchronized void setLogLevelAll(final Level level)
    {
        ensureInit();
        defaultLevel = level;
        for (var cat : CATEGORY_CFG.keySet())
        {
            if (cat.equals(CAT_ALWAYS))
                continue;
            CATEGORY_CFG.get(cat).level = level;
            CATEGORY_STATE.get(cat).logger.setLevel(level);
        }
    }
    
    /**
     * Return the log level for a log category.
     * @param category the log category
     * @return the log level for the given category
     */
    public static Level getLogLevel(final LogCategory category)
    {
        ensureInit();
        if (CATEGORY_CFG.containsKey(category))
            return CATEGORY_CFG.get(category).level;
        return DEFAULT_LEVEL;
    }

    /**
     * Set the pattern for a single log category.
     * 
     * <pre>
     * %date{HH:mm:ss.SSS}   Timestamp (default format shown; many options like ISO8601)
     * %level / %-5level     Log level (pad to fixed width with %-5level)
     * %logger / %logger{0}  Logger name (full or last component only; {n} = # of segments)
     * %thread               Thread name
     * %msg / %message       The actual log message
     * %n                    Platform-specific newline
     * %class / %class{1}    Calling class (full or just last segment with {1})
     * %method               Calling method
     * %line                 Source line number
     * %file                 Source file name
     * %caller               Shortcut for class, method, file, and line in one
     * %marker               SLF4J marker (if present)
     * %X{key}               MDC value for given key
     * %replace(p){r,e}      Apply regex replacement to pattern part p
     * %highlight(%msg)      ANSI colored message (useful on console)
     * </pre>
     *
     * Example:
     * 
     * <pre>
     * "%date{HH:mm:ss} %-5level %-6logger{0} %class{1}.%method:%line - %msg%n"
     *   → 12:34:56 INFO  http   HttpHandler.handle:42 - GET /users -> 200
     * </pre>
     * 
     * @param category the log category
     * @param pattern the new pattern
     */
    public static synchronized void setPattern(final LogCategory category, final String pattern)
    {
        ensureInit();
        addLogCategory(category); // create if missing
        CATEGORY_CFG.get(category).pattern = Objects.requireNonNull(pattern);
        // Rebuild this category's appenders with the new pattern
        rebuildCategoryAppenders(category);
    }

    /**
     * Set the pattern for a all log categories.
     * 
     * <pre>
     * %date{HH:mm:ss.SSS}   Timestamp (default format shown; many options like ISO8601)
     * %level / %-5level     Log level (pad to fixed width with %-5level)
     * %logger / %logger{0}  Logger name (full or last component only; {n} = # of segments)
     * %thread               Thread name
     * %msg / %message       The actual log message
     * %n                    Platform-specific newline
     * %class / %class{1}    Calling class (full or just last segment with {1})
     * %method               Calling method
     * %line                 Source line number
     * %file                 Source file name
     * %caller               Shortcut for class, method, file, and line in one
     * %marker               SLF4J marker (if present)
     * %X{key}               MDC value for given key
     * %replace(p){r,e}      Apply regex replacement to pattern part p
     * %highlight(%msg)      ANSI colored message (useful on console)
     * </pre>
     *
     * Example:
     * 
     * <pre>
     * "%date{HH:mm:ss} %-5level %-6logger{0} %class{1}.%method:%line - %msg%n"
     *   → 12:34:56 INFO  http   HttpHandler.handle:42 - GET /users -> 200
     * </pre>
     * 
     * @param pattern the new pattern
     */
    public static synchronized void setPatternAll(final String pattern)
    {
        ensureInit();
        defaultPattern = Objects.requireNonNull(pattern);
        CATEGORY_CFG.replaceAll((c, cfg) ->
        {
            cfg.pattern = pattern;
            return cfg;
        });
        CATEGORY_CFG.keySet().forEach(CategoryLogger::rebuildCategoryAppenders);
    }

    /**
     * Return the pattern for a log category.
     * @param category the log category
     * @return the pattern for the given category
     */
    public static String getPattern(final LogCategory category)
    {
        ensureInit();
        if (CATEGORY_CFG.containsKey(category))
            return CATEGORY_CFG.get(category).pattern;
        return DEFAULT_PATTERN;
    }

    /**
     * Register a global appender factory. A separate Appender instance will be created for each registered category.
     * @param id the id to register the appender on, so it can be removed later
     * @param factory the factory that creates the appender with a create(..) method
     */
    public static synchronized void addAppender(final String id, final CategoryAppenderFactory factory)
    {
        ensureInit();
        if (APPENDER_FACTORIES.containsKey(id))
            throw new IllegalArgumentException("factory id exists: " + id);
        APPENDER_FACTORIES.put(id, factory);
        // Create & attach instances for all existing categories
        for (var e : CATEGORY_CFG.entrySet())
        {
            LogCategory cat = e.getKey();
            CategoryConfig cfg = e.getValue();
            CategoryState st = CATEGORY_STATE.get(cat);
            Appender<ILoggingEvent> app = factory.create(id, cat, cfg.pattern, CTX);
            app.start();
            st.logger.addAppender(app);
            st.appendersByFactoryId.put(id, app);
        }
    }

    /**
     * Remove a global appender factory; detaches and stops per-category instances.
     * @param id the id the appender was registered with
     */
    public static synchronized void removeAppender(final String id)
    {
        ensureInit();
        if (APPENDER_FACTORIES.remove(id) == null)
            return;
        for (CategoryState st : CATEGORY_STATE.values())
        {
            Appender<ILoggingEvent> app = st.appendersByFactoryId.remove(id);
            if (app != null)
            {
                st.logger.detachAppender(app);
                safeStop(app);
            }
        }
    }

    /**
     * Add a find-replace formatter on the delegate logger for this category.
     * @param category the category to use
     * @param find the string to find in the pattern
     * @param replaceSupplier the supplier for the replacement string
     */
    public static synchronized void addFormatter(final LogCategory category, final String find,
            final Supplier<String> replaceSupplier)
    {
        ensureInit();
        DELEGATES.get(category).addFormatter(find, replaceSupplier);
    }

    /**
     * Remove a find-replace formatter on the delegate logger for this category.
     * @param category the category to use
     * @param find the string to find in the pattern
     */
    public static synchronized void removeFormatter(final LogCategory category, final String find)
    {
        ensureInit();
        DELEGATES.get(category).removeFormatter(find);
    }

    /**
     * Add a callback method for the delegate logger for this category.
     * @param category the category to use
     * @param callback the runnable that is called just before logging
     */
    public static synchronized void setCallback(final LogCategory category, final Runnable callback)
    {
        ensureInit();
        DELEGATES.get(category).setCallback(callback);
    }

    /* ---------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- HELPER CLASSES AND RECORDS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------------- */

    /**
     * Class to store the logging level and pattern for a log category.
     */
    private static final class CategoryConfig
    {
        /** the logging level for a category. */
        private Level level;

        /** the String pattern to use for a category. */
        private String pattern;

        /**
         * Create a record for storing the logging level and pattern for a log category.
         * @param level the logging level for a category
         * @param pattern the pattern for a category
         */
        private CategoryConfig(final Level level, final String pattern)
        {
            this.level = Objects.requireNonNull(level);
            this.pattern = Objects.requireNonNull(pattern);
        }
    }

    /**
     * Class to store the logger and the appenders for a log category.
     */
    private static final class CategoryState
    {
        /** The logger to use. */
        private final Logger logger;

        /** The appenders for this log category. */
        private final Map<String, Appender<ILoggingEvent>> appendersByFactoryId = new HashMap<>();

        /**
         * Instantiate a category state.
         * @param logger the logger to use for the category that is connected to this state
         */
        CategoryState(final Logger logger)
        {
            this.logger = logger;
        }
    }

    /* ---------------------------------------------------------------------------------------------------------------- */
    /* ----------------------------------------------- APPENDER FACTORIES --------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------------- */

    /**
     * The interface for the appender instance per category. The id is used for later removal.
     */
    public interface CategoryAppenderFactory
    {
        /**
         * Return the id to be used for later removal.
         * @return the id to be used for later removal
         */
        String id();

        /**
         * Create an appender instance for a category.
         * @param id the id to be used for later removal
         * @param category the logging category
         * @param messageFormat the pattern to use for printing the log message
         * @param ctx the context to use
         * @return an appender with the above features
         */
        Appender<ILoggingEvent> create(String id, LogCategory category, String messageFormat, LoggerContext ctx);
    }

    /** Console appender factory (uses the category's pattern). */
    public static final class ConsoleAppenderFactory implements CategoryAppenderFactory
    {
        /** the id to be used for later removal. */
        private final String id;

        /**
         * Instantiate the factory for the console appender.
         * @param id the id to be used for later removal
         */
        public ConsoleAppenderFactory(final String id)
        {
            this.id = id;
        }

        @Override
        public String id()
        {
            return this.id;
        }

        @Override
        @SuppressWarnings("checkstyle:hiddenfield")
        public Appender<ILoggingEvent> create(final String id, final LogCategory category, final String messageFormat,
                final LoggerContext ctx)
        {
            PatternLayoutEncoder enc = new PatternLayoutEncoder();
            enc.setContext(ctx);
            enc.setPattern(messageFormat);
            enc.start();

            ch.qos.logback.core.ConsoleAppender<ILoggingEvent> app = new ch.qos.logback.core.ConsoleAppender<>();
            app.setName(id + "@" + category.toString());
            app.setContext(ctx);
            app.setEncoder(enc);
            return app;
        }
    }

    /** Rolling file appender factory (per-category file pattern). */
    public static final class RollingFileAppenderFactory implements CategoryAppenderFactory
    {
        /** the id to be used for later removal. */
        private final String id;

        /** The filename pattern, e.g. "logs/%s-%d{yyyy-MM-dd}.log.gz" (use %s for category). */
        private final String fileNamePattern;

        /**
         * Instantiate the factory for the rolling file appender.
         * @param id the id to be used for later removal
         * @param fileNamePattern the filename pattern, e.g. "logs/%s-%d{yyyy-MM-dd}.log.gz" (use %s for category)
         */
        public RollingFileAppenderFactory(final String id, final String fileNamePattern)
        {
            this.id = id;
            this.fileNamePattern = fileNamePattern;
        }

        @Override
        public String id()
        {
            return this.id;
        }

        @Override
        @SuppressWarnings("checkstyle:hiddenfield")
        public Appender<ILoggingEvent> create(final String id, final LogCategory category, final String messageFormat,
                final LoggerContext ctx)
        {
            PatternLayoutEncoder enc = new PatternLayoutEncoder();
            enc.setContext(ctx);
            enc.setPattern(messageFormat);
            enc.start();

            RollingFileAppender<ILoggingEvent> file = new RollingFileAppender<>();
            file.setName(id + "@" + category.toString());
            file.setContext(ctx);
            file.setEncoder(enc);

            final TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
            policy.setContext(ctx);
            policy.setParent(file);

            // IMPORTANT: replace only the category placeholder; keep %d{...} intact for Logback
            final String effectivePattern = this.fileNamePattern.replace("%s", category.toString());
            policy.setFileNamePattern(effectivePattern);

            policy.start();
            file.setRollingPolicy(policy);

            return file; // caller will start() it
        }
    }

    /* ---------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------ DELEGATE LOGGER ----------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------------------- */

    /**
     * DelegateLogger class that takes care of actually logging the message and/or exception. <br>
     * <p>
     * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights
     * reserved.<br>
     * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static final class DelegateLogger
    {
        /** The logger facade from slf4j. */
        private final org.slf4j.Logger logger;

        /** The fully-qualified class name that defines the class to hide in the call stack. */
        private final String boundaryFqcn;

        /** Whether we should log or not (the NO_LOGGER does not log). */
        private final boolean log;

        /** the formatters with find-replace strings. */
        private final Map<String, Supplier<String>> formatters = new HashMap<>();

        /** a callback method that is called just before logging for e.g., MDC. */
        private Runnable callback = null;

        /**
         * Create a DelegateLogger with a class that indicates what to hide in the call stack.
         * @param slf4jLogger the logger facade from slf4j, can be null in case no logging is done
         * @param callerBoundary class that defines what to hide in the call stack
         * @param log whether we should log or not (the NO_LOGGER does not log)
         */
        private DelegateLogger(final org.slf4j.Logger slf4jLogger, final Class<?> callerBoundary, final boolean log)
        {
            this.logger = slf4jLogger;
            this.boundaryFqcn = Objects.requireNonNull(callerBoundary).getName();
            this.log = log;
        }

        /**
         * Create a DelegateLogger with DelegateLogger as the only class to hide from the call stack.
         * @param slf4jLogger the logger facade from slf4j, can be null in case no logging is done
         */
        private DelegateLogger(final org.slf4j.Logger slf4jLogger)
        {
            this(slf4jLogger, CategoryLogger.DelegateLogger.class, true);
        }

        /**
         * The conditional filter that will result in the usage of a DelegateLogger.
         * @param condition the condition that should be evaluated
         * @return the logger that further processes logging (DelegateLogger)
         */
        public DelegateLogger when(final boolean condition)
        {
            if (condition)
                return this;
            return CategoryLogger.NO_LOGGER;
        }

        /**
         * The conditional filter that will result in the usage of a DelegateLogger.
         * @param supplier the function evaluating the condition
         * @return the logger that further processes logging (DelegateLogger)
         */
        public DelegateLogger when(final BooleanSupplier supplier)
        {
            if (supplier.getAsBoolean())
                return this;
            return CategoryLogger.NO_LOGGER;
        }

        /**
         * helper method to return the LoggingEventBuilder WITH a boundary to leave out part of the call stack.
         * @param leb The original LoggingEventBuilder
         * @return the boundary-based LoggerEventBuilder
         */
        private LoggingEventBuilder withBoundary(final LoggingEventBuilder leb)
        {
            if (leb instanceof CallerBoundaryAware cba)
                cba.setCallerBoundary(this.boundaryFqcn);
            return leb;
        }

        /**
         * Add a find-replace formatter on the delegate logger for this delegate logger.
         * @param find the string to find in the pattern
         * @param replaceSupplier the supplier for the replacement string
         */
        public void addFormatter(final String find, final Supplier<String> replaceSupplier)
        {
            this.formatters.put(find, replaceSupplier);
        }

        /**
         * Remove a find-replace formatter on the delegate logger for this delegate logger.
         * @param find the string to find in the pattern
         */
        public void removeFormatter(final String find)
        {
            this.formatters.remove(find);
        }

        /**
         * Apply the formatter suppliers to the mdc context.
         */
        protected void mdc()
        {
            for (var find : this.formatters.keySet())
            {
                MDC.put(find, this.formatters.get(find).get());
            }
        }

        /**
         * Set the callback.
         * @param callback the callback runnable
         */
        public void setCallback(final Runnable callback)
        {
            this.callback = callback;
        }

        /**
         * Carry out the callback.
         */
        protected void doCallback()
        {
            if (this.callback != null)
            {
                this.callback.run();
            }
        }

        /* ---------------------------------------------------------------------------------------------------------------- */
        /* ----------------------------------------------------- TRACE ---------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------- */

        /**
         * Create a debug log entry that will be output if TRACE is enabled for this DelegateLogger.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void trace(final Object object)
        {
            if (!this.log || !this.logger.isTraceEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atTrace()).log(object.toString());
        }

        /**
         * Create a trace log entry that will be output if TRACE is enabled for this DelegateLogger.
         * @param message the message to log
         */
        public void trace(final String message)
        {
            if (!this.log || !this.logger.isTraceEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atTrace()).log(message);
        }

        /**
         * Create a trace log entry that will be output if TRACE is enabled for this DelegateLogger.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void trace(final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isTraceEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atTrace()).log(message, arguments);
        }

        /**
         * Create a trace log entry that will be output if TRACE is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         */
        public void trace(final Throwable throwable)
        {
            if (!this.log || !this.logger.isTraceEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atTrace()).setCause(throwable)
                .log((() -> throwable.getClass().getSimpleName() + "(" + Objects.requireNonNullElse(throwable.getMessage(), "")
                        + ")"));
        }

        /**
         * Create a trace log entry that will be output if TRACE is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         * @param message the message to log
         */
        public void trace(final Throwable throwable, final String message)
        {
            if (!this.log || !this.logger.isTraceEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atTrace()).setCause(throwable).log(message);
        }

        /**
         * Create a trace log entry that will be output if TRACE is enabled for this DelegateLogger.
         * @param throwable the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void trace(final Throwable throwable, final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isTraceEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atTrace()).setCause(throwable).log(message, arguments);
        }

        /* ---------------------------------------------------------------------------------------------------------------- */
        /* ----------------------------------------------------- DEBUG ---------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------- */

        /**
         * Create a debug log entry that will be output if DEBUG is enabled for this DelegateLogger.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void debug(final Object object)
        {
            if (!this.log || !this.logger.isDebugEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atDebug()).log(object.toString());
        }

        /**
         * Create a debug log entry that will be output if DEBUG is enabled for this DelegateLogger.
         * @param message the message to log
         */
        public void debug(final String message)
        {
            if (!this.log || !this.logger.isDebugEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atDebug()).log(message);
        }

        /**
         * Create a debug log entry that will be output if DEBUG is enabled for this DelegateLogger.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void debug(final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isDebugEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atDebug()).log(message, arguments);
        }

        /**
         * Create a debug log entry that will be output if DEBUG is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         */
        public void debug(final Throwable throwable)
        {
            if (!this.log || !this.logger.isDebugEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atDebug()).setCause(throwable)
                .log((() -> throwable.getClass().getSimpleName() + "(" + Objects.requireNonNullElse(throwable.getMessage(), "")
                        + ")"));
        }

        /**
         * Create a debug log entry that will be output if DEBUG is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         * @param message the message to log
         */
        public void debug(final Throwable throwable, final String message)
        {
            if (!this.log || !this.logger.isDebugEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atDebug()).setCause(throwable).log(message);
        }

        /**
         * Create a debug log entry that will be output if DEBUG is enabled for this DelegateLogger.
         * @param throwable the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void debug(final Throwable throwable, final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isDebugEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atDebug()).setCause(throwable).log(message, arguments);
        }

        /* ---------------------------------------------------------------------------------------------------------------- */
        /* ----------------------------------------------------- INFO ----------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------- */

        /**
         * Create a info log entry that will be output if INFO is enabled for this DelegateLogger.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void info(final Object object)
        {
            if (!this.log || !this.logger.isInfoEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atInfo()).log(object.toString());
        }

        /**
         * Create a info log entry that will be output if INFO is enabled for this DelegateLogger.
         * @param message the message to log
         */
        public void info(final String message)
        {
            if (!this.log || !this.logger.isInfoEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atInfo()).log(message);
        }

        /**
         * Create a info log entry that will be output if INFO is enabled for this DelegateLogger.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void info(final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isInfoEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atInfo()).log(message, arguments);
        }

        /**
         * Create a info log entry that will be output if INFO is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         */
        public void info(final Throwable throwable)
        {
            if (!this.log || !this.logger.isInfoEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atInfo()).setCause(throwable)
                .log((() -> throwable.getClass().getSimpleName() + "(" + Objects.requireNonNullElse(throwable.getMessage(), "")
                        + ")"));
        }

        /**
         * Create a info log entry that will be output if INFO is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         * @param message the message to log
         */
        public void info(final Throwable throwable, final String message)
        {
            if (!this.log || !this.logger.isInfoEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atInfo()).setCause(throwable).log(message);
        }

        /**
         * Create a info log entry that will be output if INFO is enabled for this DelegateLogger.
         * @param throwable the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void info(final Throwable throwable, final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isInfoEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atInfo()).setCause(throwable).log(message, arguments);
        }

        /* ---------------------------------------------------------------------------------------------------------------- */
        /* ----------------------------------------------------- WARN ----------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------- */

        /**
         * Create a warn log entry that will be output if WARN is enabled for this DelegateLogger.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void warn(final Object object)
        {
            if (!this.log || !this.logger.isWarnEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atWarn()).log(object.toString());
        }

        /**
         * Create a warn log entry that will be output if WARN is enabled for this DelegateLogger.
         * @param message the message to log
         */
        public void warn(final String message)
        {
            if (!this.log || !this.logger.isWarnEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atWarn()).log(message);
        }

        /**
         * Create a warn log entry that will be output if WARN is enabled for this DelegateLogger.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void warn(final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isWarnEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atWarn()).log(message, arguments);
        }

        /**
         * Create a warn log entry that will be output if WARN is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         */
        public void warn(final Throwable throwable)
        {
            if (!this.log || !this.logger.isWarnEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atWarn()).setCause(throwable)
                .log((() -> throwable.getClass().getSimpleName() + "(" + Objects.requireNonNullElse(throwable.getMessage(), "")
                        + ")"));
        }

        /**
         * Create a warn log entry that will be output if WARN is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         * @param message the message to log
         */
        public void warn(final Throwable throwable, final String message)
        {
            if (!this.log || !this.logger.isWarnEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atWarn()).setCause(throwable).log(message);
        }

        /**
         * Create a warn log entry that will be output if WARN is enabled for this DelegateLogger.
         * @param throwable the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void warn(final Throwable throwable, final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isWarnEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atWarn()).setCause(throwable).log(message, arguments);
        }

        /* ---------------------------------------------------------------------------------------------------------------- */
        /* ----------------------------------------------------- ERROR ---------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------- */

        /**
         * Create a error log entry that will be output if ERROR is enabled for this DelegateLogger.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void error(final Object object)
        {
            if (!this.log || !this.logger.isErrorEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atError()).log(object.toString());
        }

        /**
         * Create a error log entry that will be output if ERROR is enabled for this DelegateLogger.
         * @param message the message to log
         */
        public void error(final String message)
        {
            if (!this.log || !this.logger.isErrorEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atError()).log(message);
        }

        /**
         * Create a error log entry that will be output if ERROR is enabled for this DelegateLogger.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void error(final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isErrorEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atError()).log(message, arguments);
        }

        /**
         * Create a error log entry that will be output if ERROR is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         */
        public void error(final Throwable throwable)
        {
            if (!this.log || !this.logger.isErrorEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atError()).setCause(throwable)
                .log((() -> throwable.getClass().getSimpleName() + "(" + Objects.requireNonNullElse(throwable.getMessage(), "")
                        + ")"));
        }

        /**
         * Create a error log entry that will be output if ERROR is enabled for this DelegateLogger.
         * @param throwable the throwable to log
         * @param message the message to log
         */
        public void error(final Throwable throwable, final String message)
        {
            if (!this.log || !this.logger.isErrorEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atError()).setCause(throwable).log(message);
        }

        /**
         * Create a error log entry that will be output if ERROR is enabled for this DelegateLogger.
         * @param throwable the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void error(final Throwable throwable, final String message, final Object... arguments)
        {
            if (!this.log || !this.logger.isErrorEnabled())
                return;
            mdc();
            doCallback();
            withBoundary(this.logger.atError()).setCause(throwable).log(message, arguments);
        }
    }

}
