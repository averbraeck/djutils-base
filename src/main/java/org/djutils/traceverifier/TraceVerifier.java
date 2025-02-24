package org.djutils.traceverifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Create or verify identity of a trace of states of a system. <br>
 * E.g., A simulation that is run with the same initial conditions (including random seed) should behave in a 100% predictable
 * manner. If it does not, there is some source of randomness that probably should be eliminated. This package can help locate
 * where the various runs of the simulation start to deviate from one another. <br>
 * The simulation must be instrumented with calls to the <code>sample</code> method. The sample method either records these
 * samples in a file, or compares these samples with the values that were stored in the file in a previous run. When a
 * difference occurs, the sample method throws an exception. <br>
 * <br>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TraceVerifier implements Closeable
{
    /** Name of the output file (when in writing mode). */
    private final String outputFileName;

    /** Reader for existing trace file. */
    private final BufferedReader reader;

    /**
     * Create a new TraceVerifier.
     * @param fileName name of the file for the trace
     * @throws IOException when reading or writing fails
     */
    public TraceVerifier(final String fileName) throws IOException
    {
        // System.out.println("Creating TraceVerifier; file name is \"" + fileName + "\"");
        File traceFile = new File(fileName);
        if (traceFile.exists())
        {
            // Verify mode
            this.reader = new BufferedReader(new FileReader(fileName));
            this.outputFileName = null;
        }
        else
        {
            // Record mode
            this.outputFileName = fileName;
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.close();
            this.reader = null;
        }
    }

    /**
     * Add or compare one sample.
     * @param description some kind of description of the sample (usually some kind of time stamp).
     * @param state summary of the state of the process that is sampled.
     * @throws IOException when reading or writing fails
     * @throws TraceVerifierException on detection of a sample discrepancy
     */
    public void sample(final String description, final String state) throws IOException
    {
        String got = String.format("%s: %s", description, state);
        if (this.reader != null)
        {
            String expected = this.reader.readLine();
            if (expected.equals(got))
            {
                return;
            }
            int indexOfFirstDifference = 0;
            while (got.charAt(indexOfFirstDifference) == expected.charAt(indexOfFirstDifference))
            {
                indexOfFirstDifference++;
            }
            String format =
                    indexOfFirstDifference == 0 ? "Discrepancy found.\n%%-8.8s: \"%%s\"\n%%-8.8s: \"%%s\"\n%%-8.8s:  %%s^"
                            : String.format("Discrepancy found.\n%%-8.8s: \"%%s\"\n%%-8.8s: \"%%s\"\n%%-8.8s:  %%%d.%ds^",
                                    indexOfFirstDifference, indexOfFirstDifference);
            String error = String.format(format, "Got", got, "Expected", expected, "1st diff", "");
            throw new TraceVerifierException(error);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFileName, true));
        writer.append(got);
        writer.append('\n');
        writer.close();
    }

    @Override
    public void close() throws IOException
    {
        if (null != this.reader)
        {
            this.reader.close();
        }
    }

    @Override
    public String toString()
    {
        return "TraceVerifier [reader=" + this.reader + ", outputFileName=" + this.outputFileName + "]";
    }

}
