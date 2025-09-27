package org.djutils.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.qos.logback.classic.Level;

/**
 * RollingFileAppenderTest.java.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class RollingFileAppenderTest
{
    /** The temporary path. */
    @TempDir
    private Path tempDir;

    /**
     * Test the rolling file appender.
     * @throws Exception on error
     */
    @Test
    void testRollingFileAppender() throws Exception
    {
        LogCategory httpCat = new LogCategory("http");
        CategoryLogger.addLogCategory(httpCat);
        CategoryLogger.setLogLevel(httpCat, Level.INFO);
        CategoryLogger.removeAppender("CONSOLE");

        try
        {
            // install RollingFileAppenderFactory using the CategoryLogger API
            // File pattern must include %d{...}, and %s will be replaced with category
            String filePattern = this.tempDir.resolve("%s-%d{yyyy-MM-dd}.log").toString();
            var fileAppender = new CategoryLogger.RollingFileAppenderFactory("FILE", filePattern);
            CategoryLogger.addAppender("FILE", fileAppender);
            assertEquals("FILE", fileAppender.id());

            // --- act ---
            CategoryLogger.with(httpCat).info("GET {} -> {}", "/users", 200);
            CategoryLogger.with(httpCat).warn("Latency {} ms", 123);

            // stop appenders so data is flushed
            CategoryLogger.removeAppender("FILE");

            // --- assert ---
            Path logFile = this.tempDir.resolve("http-" + java.time.LocalDate.now() + ".log");
            List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);

            assertTrue(lines.stream().anyMatch(l -> l.contains("INFO") && l.contains("GET /users -> 200")));
            assertTrue(lines.stream().anyMatch(l -> l.contains("WARN") && l.contains("Latency 123 ms")));
        }
        finally
        {
            CategoryLogger.removeAppender("FILE");
            CategoryLogger.removeLogCategory(httpCat);
            CategoryLogger.addAppender("CONSOLE", new CategoryLogger.ConsoleAppenderFactory("CONSOLE"));
            CategoryLogger.setLogLevelAll(Level.INFO);
            CategoryLogger.setPatternAll(CategoryLogger.DEFAULT_PATTERN);
        }
    }

}
