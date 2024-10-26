package org.djutils.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;
import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.Writer;

/**
 * LoggerTest.java. <br>
 * <br>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LoggerTest
{
    /** Records last output of logger. */
    private StringWriter stringWriter = new StringWriter();

    /**
     * Helper method.
     * @param expectedMessage String; expected subString in result of stringWriter. If null; there should be no message recorded
     *            in the stringWriter.
     */
    private void verifyLogMessage(final String expectedMessage)
    {
        String actualMessage = this.stringWriter.getResult();
        if (expectedMessage != null)
        {
            assertNotNull(actualMessage);
            assertTrue(actualMessage.contains(expectedMessage));
        }
        else
        {
            assertNull(actualMessage);
        }
        this.stringWriter.clear();
    }

    /**
     * (Temporary) remove the ConsoleWriter to avoid lots of clutter on the screen during the test.
     */
    private void removeConsoleWriter()
    {
        Writer consoleWriter = null;
        for (Writer writer : CategoryLogger.getWriters())
        {
            if (writer instanceof ConsoleWriter)
            {
                consoleWriter = writer;
            }
        }
        if (consoleWriter != null)
        {
            CategoryLogger.removeWriter(consoleWriter);
        }
    }

    /**
     * Add the ConsoleWriter again.
     */
    private void addConsoleWriter()
    {
        CategoryLogger.addWriter(new ConsoleWriter());
    }

    /**
     * Test whether Logger works correctly.
     */
    @Test
    public final void loggerTest()
    {
        removeConsoleWriter();
        CategoryLogger.addWriter(this.stringWriter);
        CategoryLogger.setAllLogLevel(Level.DEBUG);
        String testMessage = "test message";
        CategoryLogger.always().error(testMessage);
        verifyLogMessage(testMessage);
        CategoryLogger.when(false).error(testMessage);
        verifyLogMessage(null);
        CategoryLogger.when(true).error(testMessage);
        verifyLogMessage(testMessage);

        LogCategory testLogCategory = new LogCategory("TEST");
        CategoryLogger.removeLogCategory(LogCategory.ALL);
        CategoryLogger.filter(testLogCategory).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.addLogCategory(testLogCategory);
        CategoryLogger.filter(testLogCategory).info(testMessage);
        verifyLogMessage(testMessage);
        CategoryLogger.removeLogCategory(testLogCategory);
        CategoryLogger.filter(testLogCategory).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.filter(LogCategory.ALL).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.addLogCategory(LogCategory.ALL);
        CategoryLogger.filter(LogCategory.ALL).info(testMessage);
        verifyLogMessage(testMessage);
        CategoryLogger.always().info(testMessage);
        verifyLogMessage(testMessage);

        CategoryLogger.removeLogCategory(LogCategory.ALL);
        CategoryLogger.filter(testLogCategory).when(false).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.addLogCategory(testLogCategory);
        CategoryLogger.filter(testLogCategory).when(false).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.removeLogCategory(testLogCategory);
        CategoryLogger.filter(testLogCategory).when(false).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.filter(LogCategory.ALL).when(false).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.addLogCategory(LogCategory.ALL);
        CategoryLogger.filter(LogCategory.ALL).when(false).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.always().when(false).info(testMessage);
        verifyLogMessage(null);

        CategoryLogger.removeLogCategory(LogCategory.ALL);
        CategoryLogger.filter(testLogCategory).when(true).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.addLogCategory(testLogCategory);
        CategoryLogger.filter(testLogCategory).when(true).info(testMessage);
        verifyLogMessage(testMessage);
        CategoryLogger.removeLogCategory(testLogCategory);
        CategoryLogger.filter(testLogCategory).when(true).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.filter(LogCategory.ALL).when(true).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.addLogCategory(LogCategory.ALL);
        CategoryLogger.filter(LogCategory.ALL).when(true).info(testMessage);
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
        verifyLogMessage(testMessage);
        CategoryLogger.when(trueSupplier).when(true).info(testMessage);
        verifyLogMessage(testMessage);
        CategoryLogger.when(trueSupplier).when(trueSupplier).info(testMessage);
        verifyLogMessage(testMessage);
        CategoryLogger.when(trueSupplier).when(falseSupplier).when(trueSupplier).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.when(falseSupplier).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.when(trueSupplier).when(false).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.when(false).when(true).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.when(false).when(trueSupplier).info(testMessage);
        verifyLogMessage(null);
        CategoryLogger.when(false).when(falseSupplier).info(testMessage);
        verifyLogMessage(null);

        CategoryLogger.removeWriter(this.stringWriter);
        addConsoleWriter();
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
        CategoryLogger.addWriter(this.stringWriter);
        CategoryLogger.setAllLogLevel(Level.DEBUG);
        removeConsoleWriter();
        String[] methodNames = new String[] {"trace", "debug", "info", "warn", "error"};
        Level[] logLevels = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARNING, Level.ERROR, Level.OFF};
        for (int levelIndex = 0; levelIndex < logLevels.length; levelIndex++)
        {
            CategoryLogger.setAllLogLevel(logLevels[levelIndex]);
            for (int methodIndex = 0; methodIndex < methodNames.length; methodIndex++)
            {
                // String; no additional arguments
                String message = "test message";
                String methodName = methodNames[methodIndex];
                Method method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, String.class);
                method.invoke(CategoryLogger.always(), message);
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
                method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Object.class);
                method.invoke(CategoryLogger.always(), message);
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
                method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Throwable.class);
                method.invoke(CategoryLogger.always(), exception);
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
                method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Throwable.class, String.class);
                method.invoke(CategoryLogger.always(), exception, extraMessage);
                if (methodIndex < levelIndex)
                {
                    verifyLogMessage(null);
                }
                else
                {
                    assertTrue(this.stringWriter.getResult().contains(extraMessage));
                    verifyLogMessage(exceptionMessage);
                }
                method.invoke(CategoryLogger.when(false), exception, extraMessage);
                verifyLogMessage(null);

                // String, with arguments
                message = "test message arg1={}, arg2={}";
                int arg1 = 1;
                String arg2 = "2";
                String expectedMessage = message.replaceFirst("\\{\\}", String.valueOf(arg1)).replaceFirst("\\{\\}", arg2);
                method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, String.class, Object[].class);
                method.invoke(CategoryLogger.always(), message, new Object[] {arg1, arg2});
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
                method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Throwable.class, String.class,
                        Object[].class);
                method.invoke(CategoryLogger.always(), exception, message, new Object[] {arg1, arg2});
                if (methodIndex < levelIndex)
                {
                    verifyLogMessage(null);
                }
                else
                {
                    assertTrue(this.stringWriter.getResult().contains(exceptionMessage));
                    verifyLogMessage(expectedMessage);
                }
                method.invoke(CategoryLogger.when(false), exception, message, new Object[] {arg1, arg2});
                verifyLogMessage(null);

            }
        }
        addConsoleWriter();
        CategoryLogger.setAllLogLevel(Level.DEBUG);
        CategoryLogger.removeWriter(this.stringWriter);
    }

    /**
     * Test varying the logging level per writer, when AllLogLevels is different. The way CategoryLogger has been set up, the
     * Writer's log level should always take precedence of the default log level, independent on the relative ranking of the
     * writer's log level and the default log level.
     * @throws SecurityException when a logging method can not be found (should not happen)
     * @throws NoSuchMethodException when a logging method can not be found (should not happen)
     * @throws InvocationTargetException when calling a logging method through reflection fails (should not happen)
     * @throws IllegalArgumentException when calling a logging method through reflection fails (should not happen)
     * @throws IllegalAccessException when calling a logging method through reflection fails (should not happen)
     */
    @Test
    public void testWriterLogLevels() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        CategoryLogger.addWriter(this.stringWriter);
        CategoryLogger.setAllLogLevel(Level.DEBUG);
        removeConsoleWriter();
        String[] methodNames = new String[] {"trace", "debug", "info", "warn", "error"};
        Level[] logLevels = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARNING, Level.ERROR, Level.OFF};
        for (int allLevelIndex = 0; allLevelIndex < logLevels.length; allLevelIndex++)
        {
            for (int writerLevelIndex = 0; writerLevelIndex < logLevels.length; writerLevelIndex++)
            {
                CategoryLogger.setAllLogLevel(logLevels[allLevelIndex]);
                CategoryLogger.setLogLevel(this.stringWriter, logLevels[writerLevelIndex]);
                for (int methodIndex = 0; methodIndex < methodNames.length; methodIndex++)
                {
                    // String; no additional arguments
                    String message = "test message";
                    String methodName = methodNames[methodIndex];
                    Method method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, String.class);
                    method.invoke(CategoryLogger.always(), message);
                    if (methodIndex < writerLevelIndex)
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
                    method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Object.class);
                    method.invoke(CategoryLogger.always(), message);
                    if (methodIndex < writerLevelIndex)
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
                    method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Throwable.class);
                    method.invoke(CategoryLogger.always(), exception);
                    if (methodIndex < writerLevelIndex)
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
                    method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Throwable.class,
                            String.class);
                    method.invoke(CategoryLogger.always(), exception, extraMessage);
                    if (methodIndex < writerLevelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        assertTrue(this.stringWriter.getResult().contains(extraMessage));
                        verifyLogMessage(exceptionMessage);
                    }
                    method.invoke(CategoryLogger.when(false), exception, extraMessage);
                    verifyLogMessage(null);

                    // String, with arguments
                    message = "test message arg1={}, arg2={}";
                    int arg1 = 1;
                    String arg2 = "2";
                    String expectedMessage = message.replaceFirst("\\{\\}", String.valueOf(arg1)).replaceFirst("\\{\\}", arg2);
                    method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, String.class,
                            Object[].class);
                    method.invoke(CategoryLogger.always(), message, new Object[] {arg1, arg2});
                    if (methodIndex < writerLevelIndex)
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
                    method = CategoryLogger.DELEGATE_LOGGER.getClass().getDeclaredMethod(methodName, Throwable.class,
                            String.class, Object[].class);
                    method.invoke(CategoryLogger.always(), exception, message, new Object[] {arg1, arg2});
                    if (methodIndex < writerLevelIndex)
                    {
                        verifyLogMessage(null);
                    }
                    else
                    {
                        assertTrue(this.stringWriter.getResult().contains(exceptionMessage));
                        verifyLogMessage(expectedMessage);
                    }
                    method.invoke(CategoryLogger.when(false), exception, message, new Object[] {arg1, arg2});
                    verifyLogMessage(null);

                }
            }
        }
        addConsoleWriter();
        CategoryLogger.setAllLogLevel(Level.DEBUG);
        CategoryLogger.removeWriter(this.stringWriter);
    }

    /**
     * Filter with multiple categories.
     */
    @Test
    public void testFilterOnCategories()
    {
        removeConsoleWriter();
        String message = "Test message";
        CategoryLogger.setAllLogLevel(Level.DEBUG);
        CategoryLogger.addWriter(this.stringWriter);
        LogCategory one = new LogCategory("ONE");
        LogCategory two = new LogCategory("TWO");
        LogCategory three = new LogCategory("THREE");
        Set<LogCategory> set0 = setOf();
        Set<LogCategory> set1 = setOf(one);
        Set<LogCategory> set12 = setOf(one, two);
        Set<LogCategory> set123 = setOf(one, two, three);
        Set<LogCategory> set23 = setOf(two, three);
        Set<LogCategory> set3 = setOf(three);

        CategoryLogger.setLogCategories();
        CategoryLogger.always().info(message);
        verifyLogMessage(message);
        CategoryLogger.filter().info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(one).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(one, two).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(one, two, three).info(message);
        verifyLogMessage(null);

        CategoryLogger.filter(set0).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(set1).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(set12).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(set123).info(message);
        verifyLogMessage(null);

        CategoryLogger.setLogCategories(one);
        CategoryLogger.always().info(message);
        verifyLogMessage(message);
        CategoryLogger.filter().info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(one).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one, two).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one, two, three).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(two, three).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(three).info(message);
        verifyLogMessage(null);

        CategoryLogger.filter(set0).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(set1).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set12).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set123).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set23).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(set3).info(message);
        verifyLogMessage(null);

        CategoryLogger.setLogCategories(one, two);
        CategoryLogger.always().info(message);
        verifyLogMessage(message);
        CategoryLogger.filter().info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(one).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one, two).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one, two, three).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(two, three).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(three).info(message);
        verifyLogMessage(null);

        CategoryLogger.filter(set0).info(message);
        verifyLogMessage(null);
        CategoryLogger.filter(set1).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set12).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set123).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set23).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set3).info(message);
        verifyLogMessage(null);

        CategoryLogger.setLogCategories(one, LogCategory.ALL);
        CategoryLogger.always().info(message);
        verifyLogMessage(message);
        CategoryLogger.filter().info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one, two).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(one, two, three).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(two, three).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(three).info(message);
        verifyLogMessage(message);

        CategoryLogger.filter(set0).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set1).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set12).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set123).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set23).info(message);
        verifyLogMessage(message);
        CategoryLogger.filter(set3).info(message);
        verifyLogMessage(message);

        CategoryLogger.setLogCategories(LogCategory.ALL);
        CategoryLogger.removeWriter(this.stringWriter);
        addConsoleWriter();
    }

    /**
     * Test varying the default message format for all writers.
     */
    @Test
    public void testAllLogMessageFormat()
    {
        CategoryLogger.addWriter(this.stringWriter);
        removeConsoleWriter();

        CategoryLogger.setAllLogMessageFormat("");
        CategoryLogger.always().info("Test message");
        assertEquals("", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertEquals("", this.stringWriter.getResult().trim());
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat("Logger message:");
        CategoryLogger.always().info("Test message");
        assertEquals("Logger message:", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertEquals("Logger message:", this.stringWriter.getResult().trim());
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat("Logger message: {level}");
        CategoryLogger.always().info("Test message");
        assertEquals("Logger message: INFO", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertEquals("Logger message: ERROR", this.stringWriter.getResult().trim());
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat("Logger message: {message}");
        CategoryLogger.always().info("Test message");
        assertEquals("Logger message: Test message", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertTrue(this.stringWriter.getResult().contains("Logger message:"));
        assertTrue(this.stringWriter.getResult().contains("NullPointerException"));
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat(CategoryLogger.DEFAULT_MESSAGE_FORMAT);
        CategoryLogger.removeWriter(this.stringWriter);
        addConsoleWriter();
    }

    /**
     * Test varying the message format for individual writers. The writer's message format always takes precedence over the
     * default message format.
     */
    @Test
    public void testWriterLogMessageFormat()
    {
        CategoryLogger.addWriter(this.stringWriter);
        removeConsoleWriter();

        CategoryLogger.setAllLogMessageFormat("");
        CategoryLogger.setLogMessageFormat(this.stringWriter, "");
        CategoryLogger.always().info("Test message");
        assertEquals("", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertEquals("", this.stringWriter.getResult().trim());
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat("xyz");
        CategoryLogger.setLogMessageFormat(this.stringWriter, "Logger message:");
        CategoryLogger.always().info("Test message");
        assertEquals("Logger message:", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertEquals("Logger message:", this.stringWriter.getResult().trim());
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat(CategoryLogger.DEFAULT_MESSAGE_FORMAT);
        CategoryLogger.setLogMessageFormat(this.stringWriter, "Logger message: {level}");
        CategoryLogger.always().info("Test message");
        assertEquals("Logger message: INFO", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertEquals("Logger message: ERROR", this.stringWriter.getResult().trim());
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat("");
        CategoryLogger.setLogMessageFormat(this.stringWriter, "Logger message: {message}");
        CategoryLogger.always().info("Test message");
        assertEquals("Logger message: Test message", this.stringWriter.getResult().trim());
        this.stringWriter.clear();
        CategoryLogger.always().error(new NullPointerException("NPE"));
        assertTrue(this.stringWriter.getResult().contains("Logger message:"));
        assertTrue(this.stringWriter.getResult().contains("NullPointerException"));
        this.stringWriter.clear();

        CategoryLogger.setAllLogMessageFormat(CategoryLogger.DEFAULT_MESSAGE_FORMAT);
        CategoryLogger.removeWriter(this.stringWriter);
        addConsoleWriter();
    }

    /** ... */
    protected static class StringWriter implements Writer
    {
        /** Last output. */
        private String result = null;

        @Override
        public Set<LogEntryValue> getRequiredLogEntryValues()
        {
            return EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY);
        }

        @Override
        public void init(final Configuration configuration) throws Exception
        {
            // Nothing to do
        }

        @Override
        public void write(final LogEntry logEntry) throws Exception
        {
            this.result = logEntry.getRenderedLogEntry();
        }

        @Override
        public void flush() throws Exception
        {
            // Nothing to do
        }

        @Override
        public void close() throws Exception
        {
            // Nothing to do
        }

        /**
         * Return the last logged message.
         * @return String; the last logged message
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

    /**
     * For Java before java 9: return a set of the given members.
     * @param <T> the set member type
     * @param members the members to add
     * @return the set with the members
     */
    @SuppressWarnings("unchecked")
    private <T> Set<T> setOf(final T... members)
    {
        Set<T> result = new LinkedHashSet<T>();
        for (T member : members)
        {
            result.add(member);
        }
        return result;
    }

}
