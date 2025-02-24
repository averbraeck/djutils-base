package org.djutils.logger;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.Immutable;
import org.djutils.immutablecollections.ImmutableLinkedHashSet;
import org.djutils.immutablecollections.ImmutableSet;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntryForwarder;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.Writer;

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
    /** The default message format. */
    public static final String DEFAULT_MESSAGE_FORMAT = "{class_name}.{method}:{line} {message|indent=4}";

    /** The current message format. */
    private static String defaultMessageFormat = DEFAULT_MESSAGE_FORMAT;

    /** The current logging level. */
    private static Level defaultLevel = Level.INFO;

    /** The writers registered with this CategoryLogger. */
    private static final Set<Writer> WRITERS = new LinkedHashSet<>();

    /** The log level per Writer. */
    private static final Map<Writer, Level> WRITER_LEVELS = new LinkedHashMap<>();

    /** The message format per Writer. */
    private static final Map<Writer, String> WRITER_FORMATS = new LinkedHashMap<>();

    /** The categories to log. */
    private static final Set<LogCategory> LOG_CATEGORIES = new LinkedHashSet<>(256);

    /** A cached immutable copy of the log categories to return to `extending` classes. */
    private static ImmutableSet<LogCategory> immutableLogCategories;

    /** The delegate logger instance that does the actual logging work, after a positive filter outcome. */
    public static final DelegateLogger DELEGATE_LOGGER = new DelegateLogger(true);

    /** The delegate logger that returns immediately after a negative filter outcome. */
    public static final DelegateLogger NO_LOGGER = new DelegateLogger(false);

    /** */
    private CategoryLogger()
    {
        // Utility class.
    }

    static
    {
        create();
    }

    /**
     * Create a new logger for the system console. Note that this REPLACES current writers. Note that the initial LogCategory is
     * LogCategory.ALL, so all categories will be logged. This category has to be explicitly removed (or new categories have to
     * be set) to log a limited set of categories.
     */
    protected static void create()
    {
        Logger.getConfiguration().removeAllWriters().activate();
        addWriter(new ConsoleWriter());
        LOG_CATEGORIES.add(LogCategory.ALL);
        immutableLogCategories = new ImmutableLinkedHashSet<>(LOG_CATEGORIES, Immutable.COPY);
    }

    /**
     * Set a new logging format for the message lines of all writers. The default message format is:<br>
     * {class_name}.{method}:{line} {message|indent=4}<br>
     * <br>
     * A few popular placeholders that can be used:<br>
     * - {class} Fully-qualified class name where the logging request is issued<br>
     * - {class_name} Class name (without package) where the logging request is issued<br>
     * - {date} Date and time of the logging request, e.g. {date:yyyy-MM-dd HH:mm:ss} [SimpleDateFormat]<br>
     * - {level} Logging level of the created log entry<br>
     * - {line} Line number from where the logging request is issued<br>
     * - {message} Associated message of the created log entry<br>
     * - {method} Method name from where the logging request is issued<br>
     * - {package} Package where the logging request is issued<br>
     * Because all individual writers get a log level at which they log, the overall log level in the Configurator is
     * Level.TRACE, which means that all messages are passed through on a generic level and it is up to the individual Writers
     * to decide when to log.
     * @see <a href="https://tinylog.org/configuration#format">https://tinylog.org/configuration</a>
     * @param newMessageFormat the new formatting pattern to use for all registered writers
     */
    public static void setAllLogMessageFormat(final String newMessageFormat)
    {
        Configurator configurator = Logger.getConfiguration();
        defaultMessageFormat = newMessageFormat;
        configurator.formatPattern(defaultMessageFormat).level(Level.TRACE);
        for (Writer writer : WRITERS)
        {
            configurator.removeWriter(writer).activate();
            WRITER_FORMATS.put(writer, newMessageFormat);
            configurator.addWriter(writer, WRITER_LEVELS.get(writer), defaultMessageFormat);
        }
        configurator.activate();
    }

    /**
     * Set a new logging level for all registered writers. Because all individual writers get a log level at which they log, the
     * overall log level in the Configurator is Level.TRACE, which means that all messages are passed through on a generic level
     * and it is up to the individual Writers to decide when to log.
     * @param newLevel the new log level for all registered writers
     */
    public static void setAllLogLevel(final Level newLevel)
    {
        Configurator configurator = Logger.getConfiguration();
        defaultLevel = newLevel;
        configurator.formatPattern(defaultMessageFormat).level(Level.TRACE);
        for (Writer writer : WRITERS)
        {
            configurator.removeWriter(writer).activate();
            WRITER_LEVELS.put(writer, newLevel);
            configurator.addWriter(writer, newLevel, WRITER_FORMATS.get(writer));
        }
        configurator.activate();
    }

    /**
     * Set a new logging format for the message lines of a writer. The default message format is:<br>
     * {class_name}.{method}:{line} {message|indent=4}<br>
     * <br>
     * A few popular placeholders that can be used:<br>
     * - {class} Fully-qualified class name where the logging request is issued<br>
     * - {class_name} Class name (without package) where the logging request is issued<br>
     * - {date} Date and time of the logging request, e.g. {date:yyyy-MM-dd HH:mm:ss} [SimpleDateFormat]<br>
     * - {level} Logging level of the created log entry<br>
     * - {line} Line number from where the logging request is issued<br>
     * - {message} Associated message of the created log entry<br>
     * - {method} Method name from where the logging request is issued<br>
     * - {package} Package where the logging request is issued<br>
     * @see <a href="https://tinylog.org/configuration#format">https://tinylog.org/configuration</a>
     * @param writer the writer to change the message format for
     * @param newMessageFormat the new formatting pattern to use for all registered writers
     */
    public static void setLogMessageFormat(final Writer writer, final String newMessageFormat)
    {
        Configurator configurator = Logger.getConfiguration();
        configurator.removeWriter(writer);
        WRITER_FORMATS.put(writer, newMessageFormat);
        configurator.addWriter(writer, WRITER_LEVELS.get(writer), newMessageFormat);
        configurator.activate();
    }

    /**
     * Set a new logging level for one of the registered writers.
     * @param writer the writer to change the log level for
     * @param newLevel the new log level for the writer
     */
    public static void setLogLevel(final Writer writer, final Level newLevel)
    {
        Configurator configurator = Logger.getConfiguration();
        configurator.removeWriter(writer);
        WRITER_LEVELS.put(writer, newLevel);
        configurator.addWriter(writer, newLevel, WRITER_FORMATS.get(writer));
        configurator.activate();
    }

    /**
     * Add a writer to the CategoryLogger, using the current default for the log level and for the message format.
     * @param writer the writer to add
     * @return true when the writer was added; false when the writer was already registered
     */
    public static boolean addWriter(final Writer writer)
    {
        Throw.whenNull(writer, "writer may not be null");
        Configurator configurator = Logger.getConfiguration();
        boolean result = WRITERS.add(writer);
        WRITER_LEVELS.put(writer, defaultLevel);
        WRITER_FORMATS.put(writer, defaultMessageFormat);
        configurator.addWriter(writer, defaultLevel, defaultMessageFormat);
        configurator.activate();
        return result;
    }

    /**
     * Remove a writer from the CategoryLogger.
     * @param writer the writer to remove
     * @return true if the writer was removed; false if the writer was not registered (and thus could not be removed)
     */
    public static boolean removeWriter(final Writer writer)
    {
        Throw.whenNull(writer, "writer may not be null");
        Configurator configurator = Logger.getConfiguration();
        boolean result = WRITERS.remove(writer);
        WRITER_LEVELS.remove(writer);
        WRITER_FORMATS.remove(writer);
        configurator.removeWriter(writer);
        configurator.activate();
        return result;
    }

    /**
     * Return the set of all registered writers.
     * @return the set of all registered writers
     */
    public static ImmutableSet<Writer> getWriters()
    {
        return new ImmutableLinkedHashSet<>(WRITERS, Immutable.WRAP);
    }

    /**
     * Add a category to be logged to the Writers.
     * @param logCategory the LogCategory to add
     */
    public static void addLogCategory(final LogCategory logCategory)
    {
        LOG_CATEGORIES.add(logCategory);
        immutableLogCategories = new ImmutableLinkedHashSet<>(LOG_CATEGORIES, Immutable.COPY);
    }

    /**
     * Remove a category to be logged to the Writers.
     * @param logCategory the LogCategory to remove
     */
    public static void removeLogCategory(final LogCategory logCategory)
    {
        LOG_CATEGORIES.remove(logCategory);
        immutableLogCategories = new ImmutableLinkedHashSet<>(LOG_CATEGORIES, Immutable.COPY);
    }

    /**
     * Set the categories to be logged to the Writers.
     * @param newLogCategories the LogCategories to set, replacing the previous ones
     */
    public static void setLogCategories(final LogCategory... newLogCategories)
    {
        LOG_CATEGORIES.clear();
        LOG_CATEGORIES.addAll(Arrays.asList(newLogCategories));
        immutableLogCategories = new ImmutableLinkedHashSet<>(LOG_CATEGORIES, Immutable.COPY);
    }

    /**
     * Return the set of all log categories (cached immutable copy).
     * @return the set of all registered writers
     */
    public static ImmutableSet<LogCategory> getLogCategories()
    {
        return immutableLogCategories;
    }

    /* ****************************************** FILTER ******************************************/

    /**
     * The "pass" filter that will result in always trying to log.
     * @return the logger that tries to execute logging (delegateLogger)
     */
    public static DelegateLogger always()
    {
        return DELEGATE_LOGGER;
    }

    /**
     * Check whether the provided category needs to be logged. Note that when LogCategory.ALL is contained in the categories,
     * filter will return true.
     * @param logCategory the category to check for.
     * @return the logger that either tries to log (delegateLogger), or returns without logging (noLogger)
     */
    public static DelegateLogger filter(final LogCategory logCategory)
    {
        if (LOG_CATEGORIES.contains(LogCategory.ALL))
            return DELEGATE_LOGGER;
        if (LOG_CATEGORIES.contains(logCategory))
            return DELEGATE_LOGGER;
        return NO_LOGGER;
    }

    /**
     * Check whether the provided categories contain one or more categories that need to be logged. Note that when
     * LogCategory.ALL is contained in the categories, filter will return true.
     * @param logCategories elements or array with the categories to check for
     * @return the logger that either tries to log (delegateLogger), or returns without logging (noLogger)
     */
    public static DelegateLogger filter(final LogCategory... logCategories)
    {
        if (LOG_CATEGORIES.contains(LogCategory.ALL))
            return DELEGATE_LOGGER;
        for (LogCategory logCategory : logCategories)
        {
            if (LOG_CATEGORIES.contains(logCategory))
                return DELEGATE_LOGGER;
        }
        return NO_LOGGER;
    }

    /**
     * Check whether the provided categories contain one or more categories that need to be logged. Note that when
     * LogCategory.ALL is contained in the categories, filter will return true.
     * @param logCategories the categories to check for
     * @return the logger that either tries to log (delegateLogger), or returns without logging (noLogger)
     */
    public static DelegateLogger filter(final Set<LogCategory> logCategories)
    {
        if (LOG_CATEGORIES.contains(LogCategory.ALL))
            return DELEGATE_LOGGER;
        for (LogCategory logCategory : logCategories)
        {
            if (LOG_CATEGORIES.contains(logCategory))
                return DELEGATE_LOGGER;
        }
        return NO_LOGGER;
    }

    /**
     * The conditional filter that will result in the usage of a DelegateLogger.
     * @param condition the condition that should be evaluated
     * @return the logger that further processes logging (DelegateLogger)
     */
    public static DelegateLogger when(final boolean condition)
    {
        if (condition)
            return DELEGATE_LOGGER;
        return NO_LOGGER;
    }

    /**
     * The conditional filter that will result in the usage of a DelegateLogger.
     * @param supplier the function evaluating the condition
     * @return the logger that further processes logging (DelegateLogger)
     */
    public static DelegateLogger when(final BooleanSupplier supplier)
    {
        if (supplier.getAsBoolean())
            return DELEGATE_LOGGER;
        return NO_LOGGER;
    }

    /* ************************************ DELEGATE LOGGER ***************************************/

    /**
     * DelegateLogger class that takes care of actually logging the message and/or exception. <br>
     * <p>
     * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights
     * reserved.<br>
     * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
     * </p>
     * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     */
    public static class DelegateLogger
    {
        /** Should we try to log or not? */
        private final boolean log;

        /**
         * @param log indicate whether we should log or not.
         */
        public DelegateLogger(final boolean log)
        {
            this.log = log;
        }

        /**
         * The conditional filter that will result in the usage of a DelegateLogger.
         * @param condition the condition that should be evaluated
         * @return the logger that further processes logging (DelegateLogger)
         */
        public DelegateLogger when(final boolean condition)
        {
            if (this.log && condition)
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
            if (this.log && supplier.getAsBoolean())
                return this;
            return CategoryLogger.NO_LOGGER;
        }

        /* ****************************************** TRACE ******************************************/

        /**
         * Create a trace log entry that will always be output, independent of LogCategory settings.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void trace(final Object object)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.TRACE, object);
        }

        /**
         * Create a trace log entry that will always be output, independent of LogCategory settings.
         * @param message the message to log
         */
        public void trace(final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.TRACE, message);
        }

        /**
         * Create a trace log entry that will always be output, independent of LogCategory settings.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void trace(final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.TRACE, message, arguments);
        }

        /**
         * Create a trace log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         */
        public void trace(final Throwable exception)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.TRACE, exception);
        }

        /**
         * Create a trace log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log
         */
        public void trace(final Throwable exception, final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.TRACE, exception, message);
        }

        /**
         * Create a trace log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void trace(final Throwable exception, final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.TRACE, exception, message, arguments);
        }

        /* ****************************************** DEBUG ******************************************/

        /**
         * Create a debug log entry that will always be output, independent of LogCategory settings.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void debug(final Object object)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.DEBUG, object);
        }

        /**
         * Create a debug log entry that will always be output, independent of LogCategory settings.
         * @param message the message to log
         */
        public void debug(final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.DEBUG, message);
        }

        /**
         * Create a debug log entry that will always be output, independent of LogCategory settings.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void debug(final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.DEBUG, message, arguments);
        }

        /**
         * Create a debug log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         */
        public void debug(final Throwable exception)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.DEBUG, exception);
        }

        /**
         * Create a debug log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log
         */
        public void debug(final Throwable exception, final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.DEBUG, exception, message);
        }

        /**
         * Create a debug log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void debug(final Throwable exception, final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.DEBUG, exception, message, arguments);
        }

        /* ****************************************** INFO ******************************************/

        /**
         * Create a info log entry that will always be output, independent of LogCategory settings.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void info(final Object object)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.INFO, object);
        }

        /**
         * Create a info log entry that will always be output, independent of LogCategory settings.
         * @param message the message to log
         */
        public void info(final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.INFO, message);
        }

        /**
         * Create a info log entry that will always be output, independent of LogCategory settings.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void info(final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.INFO, message, arguments);
        }

        /**
         * Create a info log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         */
        public void info(final Throwable exception)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.INFO, exception);
        }

        /**
         * Create a info log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log
         */
        public void info(final Throwable exception, final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.INFO, exception, message);
        }

        /**
         * Create a info log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void info(final Throwable exception, final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.INFO, exception, message, arguments);
        }

        /* ****************************************** WARN ******************************************/

        /**
         * Create a warn log entry that will always be output, // TODO: explain better independent of LogCategory settings.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void warn(final Object object)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.WARNING, object);
        }

        /**
         * Create a warn log entry that will always be output, independent of LogCategory settings.
         * @param message the message to log
         */
        public void warn(final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.WARNING, message);
        }

        /**
         * Create a warn log entry that will always be output, independent of LogCategory settings.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void warn(final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.WARNING, message, arguments);
        }

        /**
         * Create a warn log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         */
        public void warn(final Throwable exception)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.WARNING, exception);
        }

        /**
         * Create a warn log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log
         */
        public void warn(final Throwable exception, final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.WARNING, exception, message);
        }

        /**
         * Create a warn log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void warn(final Throwable exception, final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.WARNING, exception, message, arguments);
        }

        /* ****************************************** ERROR ******************************************/

        /**
         * Create a error log entry that will always be output, independent of LogCategory settings.
         * @param object the result of the <code>toString()</code> method of <code>object</code> will be logged
         */
        public void error(final Object object)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.ERROR, object);
        }

        /**
         * Create a error log entry that will always be output, independent of LogCategory settings.
         * @param message the message to log
         */
        public void error(final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.ERROR, message);
        }

        /**
         * Create a error log entry that will always be output, independent of LogCategory settings.
         * @param message the message to be logged, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void error(final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.ERROR, message, arguments);
        }

        /**
         * Create a error log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         */
        public void error(final Throwable exception)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.ERROR, exception);
        }

        /**
         * Create a error log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log
         */
        public void error(final Throwable exception, final String message)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.ERROR, exception, message);
        }

        /**
         * Create a error log entry that will always be output, independent of LogCategory settings.
         * @param exception the exception to log
         * @param message the message to log, where {} entries will be replaced by arguments
         * @param arguments the arguments to substitute for the {} entries in the message string
         */
        public void error(final Throwable exception, final String message, final Object... arguments)
        {
            if (this.log)
                LogEntryForwarder.forward(1, Level.ERROR, exception, message, arguments);
        }
    }
}
