package org.djutils.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import org.djutils.logger.CategoryLogger.CategoryAppenderFactory;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

/**
 * LoggerTest.java. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LoggerTest
{
    /** The string appender factory to use. */
    private StringAppenderFactory stringAppenderFactory = new StringAppenderFactory("string");

    /** Last logged result. */
    private static String lastLoggedResult = null;

    /**
     * Check if the appender for a log category has written a String that contains the expectedMessage.
     * @param expectedMessage expected subString in result of stringWriter. If null; there should be no message recorded in the
     *            stringWriter.
     */
    private void verifyLogMessage(final String expectedMessage)
    {
        if (expectedMessage != null)
        {
            assertNotNull(lastLoggedResult);
            assertTrue(lastLoggedResult.contains(expectedMessage));
        }
        else
        {
            assertNull(lastLoggedResult);
        }
        lastLoggedResult = null;
    }

    /**
     * Check if the appender for the log category has written a String that contains the expectedMessage.
     * @param category The category to look up the result for.
     * @param expectedMessage expected subString in result of stringWriter. If null; there should be no message recorded in the
     *            stringWriter.
     */
    private void verifyLogMessage(final LogCategory category, final String expectedMessage)
    {
        String actualMessage = getLogMessage(category);
        if (expectedMessage != null)
        {
            assertNotNull(actualMessage);
            assertTrue(actualMessage.contains(expectedMessage));
        }
        else
        {
            assertNull(actualMessage);
        }
        CategoryLogger.getAppenders(category).forEach((app) ->
        {
            if (app instanceof StringAppender sap)
            {
                sap.clear();
            }
        });
    }

    /**
     * Check if the appender for the log category has written a String that contains the expectedMessage.
     * @param category The category to look up the result for.
     * @return the log message for the log category
     */
    private String getLogMessage(final LogCategory category)
    {
        String result = null;
        for (var app : CategoryLogger.getAppenders(category))
        {
            if (app instanceof StringAppender sap)
            {
                result = sap.getResult();
            }
        }
        return result;
    }

    /**
     * Test whether Logger works correctly.
     */
    @Test
    public final void loggerTest()
    {
        CategoryLogger.addAppender("string", this.stringAppenderFactory);
        CategoryLogger.removeAppender("CONSOLE");

        try
        {
            CategoryLogger.setLogLevelAll(Level.INFO);
            String testMessage = "test message";
            CategoryLogger.always().error(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.when(false).error(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
            CategoryLogger.when(true).error(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, testMessage);
            verifyLogMessage(testMessage);

            LogCategory testCat = new LogCategory("TEST");
            CategoryLogger.removeLogCategory(LogCategory.ALL);
            CategoryLogger.with(testCat).info(testMessage);
            verifyLogMessage(testCat, null);
            verifyLogMessage(null);
            CategoryLogger.addLogCategory(testCat);
            CategoryLogger.with(testCat).info(testMessage);
            verifyLogMessage(testCat, testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.removeLogCategory(testCat);
            CategoryLogger.with(testCat).info(testMessage);
            verifyLogMessage(testCat, null);
            verifyLogMessage(null);
            CategoryLogger.with(LogCategory.ALL).info(testMessage);
            verifyLogMessage(testCat, null);
            verifyLogMessage(null);
            CategoryLogger.addLogCategory(LogCategory.ALL);
            CategoryLogger.with(LogCategory.ALL).info(testMessage);
            verifyLogMessage(LogCategory.ALL, testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.always().info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, testMessage);
            verifyLogMessage(testMessage);

            CategoryLogger.removeLogCategory(LogCategory.ALL);
            CategoryLogger.with(testCat).when(false).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.addLogCategory(testCat);
            CategoryLogger.with(testCat).when(false).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.removeLogCategory(testCat);
            CategoryLogger.with(testCat).when(false).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.with(LogCategory.ALL).when(false).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.addLogCategory(LogCategory.ALL);
            CategoryLogger.with(LogCategory.ALL).when(false).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.always().when(false).info(testMessage);
            verifyLogMessage(null);

            CategoryLogger.removeLogCategory(LogCategory.ALL);
            CategoryLogger.with(testCat).when(true).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.addLogCategory(testCat);
            CategoryLogger.with(testCat).when(true).info(testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.removeLogCategory(testCat);
            CategoryLogger.with(testCat).when(true).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.with(LogCategory.ALL).when(true).info(testMessage);
            verifyLogMessage(null);
            CategoryLogger.addLogCategory(LogCategory.ALL);
            CategoryLogger.with(LogCategory.ALL).when(true).info(testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.always().when(true).info(testMessage);
            verifyLogMessage(testMessage);

            // test when(...) with booleanSupplier
            BooleanSupplier trueSupplier = new BooleanSupplier()
            {
                @Override
                public boolean getAsBoolean()
                {
                    return true;
                }
            };
            BooleanSupplier falseSupplier = new BooleanSupplier()
            {
                @Override
                public boolean getAsBoolean()
                {
                    return false;
                }
            };
            CategoryLogger.when(trueSupplier).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.when(trueSupplier).when(true).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.when(trueSupplier).when(trueSupplier).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, testMessage);
            verifyLogMessage(testMessage);
            CategoryLogger.when(trueSupplier).when(falseSupplier).when(trueSupplier).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
            CategoryLogger.when(falseSupplier).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
            CategoryLogger.when(trueSupplier).when(false).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
            CategoryLogger.when(false).when(true).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
            CategoryLogger.when(false).when(trueSupplier).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
            CategoryLogger.when(false).when(falseSupplier).info(testMessage);
            verifyLogMessage(CategoryLogger.CAT_ALWAYS, null);
            verifyLogMessage(null);
        }
        finally
        {
            CategoryLogger.removeAppender("string");
            CategoryLogger.addAppender("CONSOLE", new CategoryLogger.ConsoleAppenderFactory("CONSOLE"));
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

    /**
     * Test varying the logging level for the AllLogLevel levels.
     * @throws SecurityException when a logging method can not be found (should not happen)
     * @throws NoSuchMethodException when a logging method can not be found (should not happen)
     * @throws InvocationTargetException when calling a logging method through reflection fails (should not happen)
     * @throws IllegalArgumentException when calling a logging method through reflection fails (should not happen)
     * @throws IllegalAccessException when calling a logging method through reflection fails (should not happen)
     */
    @Test
    public void testAllLogLevels() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        CategoryLogger.addAppender("string", this.stringAppenderFactory);
        CategoryLogger.removeAppender("CONSOLE");

        try
        {
            CategoryLogger.setLogLevelAll(Level.DEBUG);
            String[] methodNames = new String[] {"trace", "debug", "info", "warn", "error"};
            Level[] logLevels = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.OFF};
            for (int levelIndex = 0; levelIndex < logLevels.length; levelIndex++)
            {
                CategoryLogger.setLogLevelAll(logLevels[levelIndex]);
                for (int methodIndex = 0; methodIndex < methodNames.length; methodIndex++)
                {
                    // String; no additional arguments
                    String message = "ALL LEVELS TEST";
                    String methodName = methodNames[methodIndex];
                    Method method = CategoryLogger.DelegateLogger.class.getDeclaredMethod(methodName, String.class);
                    method.invoke(CategoryLogger.with(LogCategory.ALL), message);
                    if (methodIndex < levelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        verifyLogMessage(message);
                    }
                    method.invoke(CategoryLogger.when(false), message);
                    verifyLogMessage(null);

                    // Object (no arguments - of course)
                    method = CategoryLogger.DelegateLogger.class.getDeclaredMethod(methodName, Object.class);
                    method.invoke(CategoryLogger.with(LogCategory.ALL), message);
                    if (methodIndex < levelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        verifyLogMessage(message);
                    }
                    method.invoke(CategoryLogger.when(false), message);
                    verifyLogMessage(null);

                    // Throwable
                    String exceptionMessage = "ExceptionMessage";
                    Exception exception = new Exception(exceptionMessage);
                    method = CategoryLogger.DelegateLogger.class.getDeclaredMethod(methodName, Throwable.class);
                    method.invoke(CategoryLogger.with(LogCategory.ALL), exception);
                    if (methodIndex < levelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        verifyLogMessage(exceptionMessage);
                    }
                    method.invoke(CategoryLogger.when(false), exception);
                    verifyLogMessage(null);

                    // Throwable with message
                    String extraMessage = "Extra Message";
                    method = CategoryLogger.DelegateLogger.class.getDeclaredMethod(methodName, Throwable.class, String.class);
                    method.invoke(CategoryLogger.with(LogCategory.ALL), exception, extraMessage);
                    if (methodIndex < levelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        assertTrue(getLogMessage(LogCategory.ALL).contains(extraMessage));
                        verifyLogMessage(extraMessage);
                    }
                    method.invoke(CategoryLogger.when(false), exception, extraMessage);
                    verifyLogMessage(null);

                    // String, with arguments
                    message = "test message arg1={}, arg2={}";
                    int arg1 = 1;
                    String arg2 = "2";
                    String expectedMessage = message.replaceFirst("\\{\\}", String.valueOf(arg1)).replaceFirst("\\{\\}", arg2);
                    method = CategoryLogger.DelegateLogger.class.getDeclaredMethod(methodName, String.class, Object[].class);
                    method.invoke(CategoryLogger.with(LogCategory.ALL), message, new Object[] {arg1, arg2});
                    if (methodIndex < levelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        verifyLogMessage(expectedMessage);
                    }
                    method.invoke(CategoryLogger.when(false), message, new Object[] {arg1, arg2});
                    verifyLogMessage(null);

                    // Throwable with message and arguments
                    method = CategoryLogger.DelegateLogger.class.getDeclaredMethod(methodName, Throwable.class, String.class,
                            Object[].class);
                    method.invoke(CategoryLogger.with(LogCategory.ALL), exception, message, new Object[] {arg1, arg2});
                    if (methodIndex < levelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        assertTrue(getLogMessage(LogCategory.ALL).contains(expectedMessage));
                        verifyLogMessage(expectedMessage);
                    }
                    method.invoke(CategoryLogger.when(false), exception, message, new Object[] {arg1, arg2});
                    verifyLogMessage(null);

                }
            }
            CategoryLogger.setLogLevelAll(Level.DEBUG);
        }
        finally
        {
            CategoryLogger.removeAppender("string");
            CategoryLogger.addAppender("CONSOLE", new CategoryLogger.ConsoleAppenderFactory("CONSOLE"));
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

    /**
     * Test varying the default message format for all writers.
     */
    @Test
    public void testAllLogMessageFormat()
    {
        CategoryLogger.addAppender("string", this.stringAppenderFactory);
        CategoryLogger.removeAppender("CONSOLE");

        try
        {
            CategoryLogger.setPatternAll("");
            CategoryLogger.always().info("Test message");
            assertEquals("", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertEquals("", lastLoggedResult.trim());
            lastLoggedResult = null;

            CategoryLogger.setPatternAll("Logger message:");
            CategoryLogger.always().info("Test message");
            assertEquals("Logger message:", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertTrue(lastLoggedResult.trim().contains("Logger message:"));
            lastLoggedResult = null;

            CategoryLogger.setPatternAll("Logger message: %level");
            CategoryLogger.always().info("Test message");
            assertEquals("Logger message: INFO", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertTrue(lastLoggedResult.trim().contains("Logger message: ERROR"));
            lastLoggedResult = null;

            CategoryLogger.setPatternAll("Logger message: %message");
            CategoryLogger.always().info("Test message");
            assertEquals("Logger message: Test message", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertTrue(lastLoggedResult.contains("Logger message:"));
            assertTrue(lastLoggedResult.contains("NullPointerException"));
            lastLoggedResult = null;

            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
        finally
        {
            CategoryLogger.removeAppender("string");
            CategoryLogger.addAppender("CONSOLE", new CategoryLogger.ConsoleAppenderFactory("CONSOLE"));
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

    /**
     * Test varying the message format for individual writers. The writer's message format always takes precedence over the
     * default message format.
     */
    @Test
    public void testWriterLogMessageFormat()
    {
        CategoryLogger.addAppender("string", this.stringAppenderFactory);
        CategoryLogger.removeAppender("CONSOLE");

        try
        {
            CategoryLogger.setPatternAll("");
            CategoryLogger.setPattern(CategoryLogger.CAT_ALWAYS, "");
            CategoryLogger.always().info("Test message");
            assertEquals("", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertEquals("", lastLoggedResult.trim());
            lastLoggedResult = null;

            CategoryLogger.setPatternAll("xyz");
            CategoryLogger.setPattern(CategoryLogger.CAT_ALWAYS, "Logger message:");
            CategoryLogger.always().info("Test message");
            assertEquals("Logger message:", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertTrue(lastLoggedResult.trim().contains("Logger message:"));
            lastLoggedResult = null;

            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
            CategoryLogger.setPattern(CategoryLogger.CAT_ALWAYS, "Logger message: %level");
            CategoryLogger.always().info("Test message");
            assertEquals("Logger message: INFO", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertTrue(lastLoggedResult.trim().contains("Logger message: ERROR"));
            lastLoggedResult = null;

            CategoryLogger.setPatternAll("");
            CategoryLogger.setPattern(CategoryLogger.CAT_ALWAYS, "Logger message: %message");
            CategoryLogger.always().info("Test message");
            assertEquals("Logger message: Test message", lastLoggedResult.trim());
            lastLoggedResult = null;
            CategoryLogger.always().error(new NullPointerException("NPE"));
            assertTrue(lastLoggedResult.contains("Logger message:"));
            assertTrue(lastLoggedResult.contains("NullPointerException"));
            lastLoggedResult = null;

            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
        finally
        {
            CategoryLogger.removeAppender("string");
            CategoryLogger.addAppender("CONSOLE", new CategoryLogger.ConsoleAppenderFactory("CONSOLE"));
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

    /**
     * Test setters and getters for log level and pattern.
     */
    @Test
    public void testGetSetPerCategory()
    {
        var cat1 = new LogCategory("CAT1");
        var cat2 = new LogCategory("CAT2");
        var cat3 = new LogCategory("CAT3");
        
        try
        {
            CategoryLogger.addLogCategory(cat1);
            CategoryLogger.addLogCategory(cat2);
            
            CategoryLogger.setLogLevel(cat1, Level.INFO);
            CategoryLogger.setLogLevel(cat2, Level.WARN);
            assertEquals(Level.INFO, CategoryLogger.getLogLevel(cat1));
            assertEquals(Level.WARN, CategoryLogger.getLogLevel(cat2));
            assertEquals(CategoryLogger.DEFAULT_LEVEL, CategoryLogger.getLogLevel(cat3));
            CategoryLogger.setLogLevelAll(Level.DEBUG);
            assertEquals(Level.DEBUG, CategoryLogger.getLogLevel(cat1));
            assertEquals(Level.DEBUG, CategoryLogger.getLogLevel(cat2));
            CategoryLogger.setLogLevelAll(Level.INFO);
            
            CategoryLogger.setPattern(cat1, "%msg%n");
            CategoryLogger.setPattern(cat2, CategoryLogger.DEFAULT_PATTERN);
            assertEquals("%msg%n", CategoryLogger.getPattern(cat1));
            assertEquals(CategoryLogger.DEFAULT_PATTERN, CategoryLogger.getPattern(cat2));
            assertEquals(CategoryLogger.DEFAULT_PATTERN, CategoryLogger.getPattern(cat3));
            String m = "%msg at %class.%method:%line %n";
            CategoryLogger.setPatternAll(m);
            assertEquals(m, CategoryLogger.getPattern(cat1));
            assertEquals(m, CategoryLogger.getPattern(cat2));
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
        finally
        {
            CategoryLogger.removeLogCategory(cat1);
            CategoryLogger.removeLogCategory(cat2);
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

    /**
     * Test formatter and callback.
     */
    @Test
    public void testFormatterCallback()
    {
        var cat1 = new LogCategory("CAT1");
        var cat2 = new LogCategory("CAT2");
        var cat3 = new LogCategory("CAT3");
        try
        {
            CategoryLogger.addAppender("string", this.stringAppenderFactory);
            CategoryLogger.removeAppender("CONSOLE");

            CategoryLogger.addLogCategory(cat1);
            CategoryLogger.addLogCategory(cat2);
            
            CategoryLogger.addFormatter(cat2, "simTime", () -> "123.456");
            CategoryLogger.setPattern(cat2, "[%X{simTime}] %msg%n");
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.with(cat2).info("xyz");
            assertTrue(lastLoggedResult.trim().startsWith("[123.456]"));
            assertTrue(lastLoggedResult.trim().contains("xyz"));
            lastLoggedResult = null;
            CategoryLogger.with(cat1).info("xyz");
            assertFalse(lastLoggedResult.trim().startsWith("[123.456]"));
            assertTrue(lastLoggedResult.trim().contains("xyz"));
            lastLoggedResult = null;
            CategoryLogger.with(cat3).info("xyz");
            assertNull(lastLoggedResult);
            
            CategoryLogger.removeFormatter(cat2, "simTime");
            CategoryLogger.with(cat2).info("xyz");
            assertFalse(lastLoggedResult.trim().startsWith("[123.456]"));
            assertTrue(lastLoggedResult.trim().contains("xyz"));
            lastLoggedResult = null;
            
            final AtomicInteger ai = new AtomicInteger(0);
            CategoryLogger.addFormatter(cat2, "ai", () -> String.valueOf(ai.get()));
            CategoryLogger.setPattern(cat2, "[%X{ai}] %msg%n");
            CategoryLogger.setCallback(cat2, () -> ai.incrementAndGet());
            lastLoggedResult = null;
            CategoryLogger.with(cat2).info("xyz");
            assertTrue(lastLoggedResult.trim().startsWith("[1]"));
            assertTrue(lastLoggedResult.trim().contains("xyz"));
            lastLoggedResult = null;
            CategoryLogger.with(cat1).info("xyz");
            assertEquals(1, ai.get());
            CategoryLogger.with(cat2).info("abc");
            assertTrue(lastLoggedResult.trim().startsWith("[2]"));
            assertTrue(lastLoggedResult.trim().contains("abc"));
            lastLoggedResult = null;
            
            CategoryLogger.removeCallback(cat2);
            CategoryLogger.with(cat2).info("def");
            assertTrue(lastLoggedResult.trim().startsWith("[2]"));
            assertTrue(lastLoggedResult.trim().contains("def"));
            lastLoggedResult = null;
            CategoryLogger.removeFormatter(cat2, "ai");
            CategoryLogger.with(cat2).info("ghi");
            assertFalse(lastLoggedResult.trim().startsWith("[2]"));
            assertTrue(lastLoggedResult.trim().contains("ghi"));
            lastLoggedResult = null;
            assertEquals(2, ai.get());
        }
        finally
        {
            CategoryLogger.removeAppender("string");
            CategoryLogger.addAppender("CONSOLE", new CategoryLogger.ConsoleAppenderFactory("CONSOLE"));
            CategoryLogger.removeLogCategory(cat1);
            CategoryLogger.removeLogCategory(cat2);
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

    /**
     * The in-memory StringAppender class for testing whether the correct information has been logged.
     */
    protected static class StringAppender extends AppenderBase<ILoggingEvent>
    {
        /** Last output. */
        private String result = null;

        /** the last used pattern. */
        private final String pattern;

        /** The logger context. */
        private final LoggerContext ctx;

        /**
         * @param pattern the formatting pattern
         * @param ctx the logger context
         */
        public StringAppender(final String pattern, final LoggerContext ctx)
        {
            this.pattern = pattern;
            this.ctx = ctx;
        }

        @Override
        protected void append(final ILoggingEvent event)
        {
            PatternLayout layout = new PatternLayout();
            layout.setContext(this.ctx);
            layout.setPattern(this.pattern);
            layout.start();
            LoggerTest.lastLoggedResult = layout.doLayout(event);
            this.result = LoggerTest.lastLoggedResult;
        }

        /**
         * Return the last logged message.
         * @return the last logged message
         */
        public String getResult()
        {
            return this.result;
        }

        /**
         * Nullify the last logged message (so we can distinguish a newly received message even when it is the empty string.
         */
        public void clear()
        {
            this.result = null;
        }
    }

    /** The factory to return a StringAppender. */
    protected static class StringAppenderFactory implements CategoryAppenderFactory
    {
        /** the id to be used for later removal. */
        private final String id;

        /**
         * Instantiate the factory for the string appender.
         * @param id the id to be used for later removal
         */
        public StringAppenderFactory(final String id)
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
        public Appender<ILoggingEvent> create(final String id, final LogCategory category, final String pattern,
                final LoggerContext ctx)
        {
            StringAppender app = new StringAppender(pattern, ctx);
            app.setContext(ctx);
            app.setName(id + "@" + category.toString());
            return app;
        }
    }

}
