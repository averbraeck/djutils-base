package org.djutils.decoderdumper;

/**
 * Keep track of the address of the decoder-dumper and call flushLine when the last possible address of a line is received.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class HexAddressDecoder implements Decoder
{
    /** Round all printed addresses down to a multiple of this value. */
    private final int roundToMultiple;

    /**
     * Construct a new HexAddressDecoder.
     * @param roundToMultiple if &gt; 1 round addresses down to the nearest (lower) multiple of this value and the append
     *            method will return true when the last byte before such a multiple is added.
     */
    public HexAddressDecoder(final int roundToMultiple)
    {
        this.roundToMultiple = roundToMultiple > 0 ? roundToMultiple : 1;
    }

    /** Result returned by getResult. */
    private String result = "";

    @Override
    public String getResult()
    {
        String retVal = this.result;
        this.result = "";
        return retVal;
    }

    @Override
    public int getMaximumWidth()
    {
        return 8;
    }

    @Override
    public boolean append(final int address, final byte theByte)
    {
        this.result = String.format("%08x", address / this.roundToMultiple * this.roundToMultiple);
        return this.roundToMultiple > 1 && address % this.roundToMultiple == this.roundToMultiple - 1;
    }

    @Override
    public boolean ignoreForIdenticalOutputCheck()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "HexAddressDecoder [result=" + this.result + "]";
    }

}
