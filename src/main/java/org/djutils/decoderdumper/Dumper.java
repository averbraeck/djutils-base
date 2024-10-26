package org.djutils.decoderdumper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Common code for all (decoder-) Dumpers.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Jan 3, 2019 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <T> Type of dumper
 */
public class Dumper<T>
{
    /** The (currently active) decoders. */
    private List<Decoder> decoders = new ArrayList<>();

    /** The address of the next byte. */
    private int address = 0;

    /** Output stream for completed output lines. */
    private OutputStream outputStream = System.out;

    /** If true, 3 or more output lines containing the same 16 bytes are compressed. */
    private boolean suppressMultipleIdenticalLines = false;

    /** Number of identical lines in output. */
    private int suppressedCount = 0;

    /** Used in conjunction with <code>suppressMultipleIdenticalLines</code>. */
    private String lastPattern = "";

    /** Used in conjunction with <code>suppressMultipleIdenticalLines</code>. */
    private String lastOutput = "";

    /** Line that is output to indicate where one or more output lines were suppressed. */
    private static final String SUPPRESSEDOUTPUTINDICATORLINE = "*\n";

    /**
     * Construct a new Dumper.
     * @param addressOffset int; address for the first byte that will be appended
     */
    public Dumper(final int addressOffset)
    {
        this.address = addressOffset;
    }

    /**
     * Construct a new Dumper with addressOffset 0.
     */
    public Dumper()
    {
        this(0);
    }

    /**
     * Set or replace the active output stream. (The default output stream is <code>System.out</code>.)
     * @param newOutputStream OutputStream; the new output stream
     * @return Dumper&lt;T&gt;; this Dumper object (for method chaining)
     */
    public Dumper<T> setOutputStream(final OutputStream newOutputStream)
    {
        this.outputStream = newOutputStream;
        return this;
    }

    /**
     * Set the output compression mode.
     * @param newState boolean; if true; groups of three or more output lines with the significant content are compressed; if
     *            false; no output is suppressed
     * @return Dumper&lt;T&gt;; this Dumper object (for method chaining)
     */
    public Dumper<T> setSuppressMultipleIdenticalLines(final boolean newState)
    {
        this.suppressMultipleIdenticalLines = newState;
        return this;
    }

    /**
     * Add a Decoder at the end of the current list of decoders.
     * @param decoder Decoder; the decoder to add or insert
     */
    public void addDecoder(final Decoder decoder)
    {
        this.decoders.add(decoder);
    }

    /**
     * Add a Decoder at a specified index.
     * @param index int; the position where the Decoder must be added (inserted)
     * @param decoder Decoder; the decoder to add or insert
     * @return Dumper&lt;T&gt;; this Dumper object (for method chaining)
     * @throws IndexOutOfBoundsException when the provided index is invalid
     */
    public Dumper<T> addDecoder(final int index, final Decoder decoder) throws IndexOutOfBoundsException
    {
        this.decoders.add(index, decoder);
        return this;
    }

    /**
     * Write some output.
     * @param outputText String; text to write.
     * @throws IOException when an outputStream has been set and it throws an IOException
     */
    private void writeOutput(final String outputText) throws IOException
    {
        this.outputStream.write(outputText.getBytes("UTF-8"));
    }

    /**
     * Write some output, applying suppression of multiple lines with the same dumped bytes (if that option is active).
     * @param outputText String; text to write.
     * @param pattern String; pattern that should be used to check for multiple identical output lines
     * @throws IOException when an outputStream has been set and it throws an IOException
     */
    private void writeFilteringOutput(final String outputText, final String pattern) throws IOException
    {
        if (this.suppressedCount > 0 && ((!this.suppressMultipleIdenticalLines) || (!pattern.equals(this.lastPattern))))
        {
            // We have suppressed output lines AND (suppressing is now OFF OR pattern != lastPattern)
            if (!outputText.equals(this.lastPattern))
            {
                writeOutput(this.lastOutput);
            }
            this.suppressedCount = 0;
        }
        this.lastOutput = outputText;
        if ((!this.suppressMultipleIdenticalLines) || (!pattern.equals(this.lastPattern)))
        {
            writeOutput(outputText);
        }
        else
        {
            // Suppress this output
            if (1 == this.suppressedCount++)
            {
                // Write the suppressed output indicator line
                writeOutput(SUPPRESSEDOUTPUTINDICATORLINE);
            }
        }
        this.lastPattern = pattern;
    }

    /**
     * Append one byte to this dump.
     * @param theByte byte; the byte to append
     * @return boolean; true if output was generated; false if the byte was accumulated, but did not result in immediate output
     * @throws IOException when output was generated and writing to the output stream generated an IOException
     */
    public boolean append(final byte theByte) throws IOException
    {
        boolean needFlush = false;
        for (Decoder decoder : this.decoders)
        {
            needFlush |= decoder.append(this.address, theByte);
        }
        this.address++;
        if (needFlush)
        {
            return flush();
        }
        return false;
    }

    /**
     * Append an array of bytes.
     * @param bytes byte[]; the bytes to append
     * @return Dumper&lt;T&gt;; this Dumper object (for method chaining)
     * @throws IOException when an outputStream has been set and it throws an IOException
     */
    public Dumper<T> append(final byte[] bytes) throws IOException
    {
        return append(bytes, 0, bytes.length);
    }

    /**
     * Append a slice of an array of bytes.
     * @param bytes byte[]; byte array from which to take the bytes to append
     * @param start int; index of first byte in <code>bytes</code> to append (NB. using non-zero does <b>not</b> cause a jump in
     *            the address that is printed before the dumped bytes)
     * @param len int; number of bytes to append
     * @return Dumper&lt;T&gt;; this Dumper object (for method chaining)
     * @throws IOException when an outputStream has been set and it throws an IOException
     */
    public Dumper<T> append(final byte[] bytes, final int start, final int len) throws IOException
    {
        for (int pos = start; pos < start + len; pos++)
        {
            append(bytes[pos]);
        }
        return this;
    }

    /**
     * Consume an entire input stream and append what it produces to this Dumpmer. The input stream is <b>not</b> closed by this
     * <code>append</code> method. This method does not return until the <code>inputStream</code> returns end of file, or throws
     * an IOException (which is - actually - not a return to the caller, but a jump to the closest handler for that exception).
     * @param inputStream InputStream; the input stream that is to be consumed
     * @return Dumper&lt;T&gt;; this Dumper object (for method chaining)
     * @throws IOException when the <code>inputStream</code> throws that exception, or when an output stream has been set and
     *             that throws an IOException
     */
    public Dumper<T> append(final InputStream inputStream) throws IOException
    {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) >= 0)
        {
            append(buffer, 0, read);
        }
        return this;
    }

    /**
     * Force the currently assembled output to be written (write partial result if the output line currently being assembled is
     * not full).
     * @return boolean; true if output was generated; false if no output was generated
     * @throws IOException when output was generated and writing to the output stream generated an IOException
     */
    public boolean flush() throws IOException
    {
        StringBuilder result = new StringBuilder();
        StringBuilder pattern = new StringBuilder();
        int totalReturnedWidth = 0;
        for (Decoder decoder : this.decoders)
        {
            String part = decoder.getResult();
            totalReturnedWidth += part.length();
            if (part.length() < decoder.getMaximumWidth())
            {
                String format = String.format("%%-%ds", decoder.getMaximumWidth());
                part = String.format(format, part);
            }
            result.append(part);
            if (!decoder.ignoreForIdenticalOutputCheck())
            {
                pattern.append(part);
            }
        }
        writeFilteringOutput(totalReturnedWidth == 0 ? "" : result.toString(), pattern.toString());
        return totalReturnedWidth > 0;
    }

    /**
     * Return the maximum width of an output line.
     * @return int; the maximum width of an output line
     */
    public int getMaximumWidth()
    {
        int result = 0;
        for (Decoder decoder : this.decoders)
        {
            result += decoder.getMaximumWidth();
        }
        return result;
    }

    @Override
    public String toString()
    {
        return "Dumper [decoders=" + this.decoders + ", address=" + this.address + ", outputStream=" + this.outputStream
                + ", suppressMultipleIdenticalLines=" + this.suppressMultipleIdenticalLines + ", suppressedCount="
                + this.suppressedCount + ", lastPattern=" + this.lastPattern + "]";
    }

}
