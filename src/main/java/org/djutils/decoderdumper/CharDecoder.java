package org.djutils.decoderdumper;

import java.io.IOException;

/**
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class CharDecoder implements Decoder
{
    /** Prototype output line. */
    private final String prototypeLine;

    /** Maximum number of fields on one output line. */
    private final int fieldsPerLine;

    /** Insert one extra space every N fields. */
    private final int extraSpaceAfterEvery;

    /**
     * Construct a new CharDecoder.
     * @param fieldsPerLine maximum number of fields on one output line
     * @param extraSpaceAfterEvery insert an extra space after every N fields
     */
    public CharDecoder(final int fieldsPerLine, final int extraSpaceAfterEvery)
    {
        this.fieldsPerLine = fieldsPerLine;
        this.extraSpaceAfterEvery = extraSpaceAfterEvery > 0 ? extraSpaceAfterEvery : Integer.MAX_VALUE;
        String format = String.format("%%%ds", fieldsPerLine + (fieldsPerLine - 1) / this.extraSpaceAfterEvery);
        this.prototypeLine = String.format(format, "");
    }

    /** String builder for current output line. */
    private StringBuilder buffer = new StringBuilder();

    @Override
    public String getResult()
    {
        String result = this.buffer.toString();
        this.buffer.setLength(0);
        return result;
    }

    @Override
    public int getMaximumWidth()
    {
        return this.prototypeLine.length();
    }

    @Override
    public boolean append(final int address, final byte theByte) throws IOException
    {
        if (this.buffer.length() == 0)
        {
            this.buffer.append(this.prototypeLine);
        }
        int lineByte = address % this.fieldsPerLine;
        int index = lineByte + lineByte / this.extraSpaceAfterEvery;
        this.buffer.replace(index, index + 1, theByte >= 32 && theByte < 127 ? "" + (char) theByte : ".");
        return lineByte == this.fieldsPerLine - 1;
    }

    @Override
    public boolean ignoreForIdenticalOutputCheck()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "CharDecoder [buffer=" + this.buffer + "]";
    }

}
