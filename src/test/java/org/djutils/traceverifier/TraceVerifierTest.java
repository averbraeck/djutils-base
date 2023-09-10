package org.djutils.traceverifier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test the TraceVerifier class. <br>
 * <br>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TraceVerifierTest
{

    /** Temporary directory that should be deleted by Junit at end of test. */
    @TempDir
    private Path testDir;
    
    /**
     * Test the TraceVerifier class.
     * @throws IOException if that happens uncaught, this test has failed.
     */
    @SuppressWarnings("resource")
    @Test
    public void testTraceVerifier() throws IOException
    {
        try
        {
            new TraceVerifier("/Non/Existent/Path/And/File/Name");
            fail("non existent path should have thrown an exception");
        }
        catch (IOException ioe)
        {
            // Ignore expected exception
        }

        // System.out.println("testdir is " + this.testDir.getRoot());
        String traceFileName = this.testDir.toString() + File.separator + "traceFile.txt";

        TraceVerifier tv = new TraceVerifier(traceFileName);
        assertTrue(tv.toString().startsWith("TraceVerifier"), "toString returns something descriptive");
        // Trace some things
        tv.sample("sample 1", "state 1");
        tv.sample("sample 2", "state 2");
        tv.sample("sample 3", "state 3");
        tv.close();

        tv = new TraceVerifier(traceFileName); // Now it should be open for verify
        try
        {
            tv.sample("sample 1", "not state 1");
            fail("tv.sample(...) should have raised an exception");
        }
        catch (TraceVerifierException re)
        {
            // exception expected
            assertTrue(re.getMessage().startsWith("Discrepancy found"),
                    "Got wrong message: " + re.getMessage() + " of exception " + re.getClass());
        }
        try
        {
            tv.sample("not sample 2", "state 2");
            fail("tv.sample(...) should have raised an exception");
        }
        catch (TraceVerifierException re)
        {
            // exception expected
            assertTrue(re.getMessage().startsWith("Discrepancy found"),
                    "Got wrong message: " + re.getMessage() + " of exception " + re.getClass());
        }
        tv.sample("sample 3", "state 3");
        tv.close();
    }

}
