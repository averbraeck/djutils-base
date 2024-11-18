package org.djutils.decoderdumper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Dump data in hexadecimal format and (insofar possible) as characters.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class HexDumper extends Dumper<HexDumper>
{

    /**
     * Construct a new HexDumper.
     * @param addressOffset address of the first byte that will be appended
     */
    public HexDumper(final int addressOffset)
    {
        super(addressOffset);
        addDecoder(new HexAddressDecoder(16));
        addDecoder(new FixedString(": "));
        addDecoder(new HexDecoder(16, 8));
        addDecoder(new FixedString("  "));
        addDecoder(new CharDecoder(16, 8));
        addDecoder(new FixedString("\n"));
    }

    /**
     * Construct a new HexDumper.
     */
    public HexDumper()
    {
        this(0);
    }

    /**
     * Create a HexDumper object; use it to dump an array of bytes and return the dump as a String.
     * @param addressOffset address of the first byte
     * @param bytes the bytes to hex-dump
     * @return the hexadecimal and character dump of the <code>bytes</code>
     */
    public static String hexDumper(final int addressOffset, final byte[] bytes)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            new HexDumper(addressOffset).setOutputStream(baos).append(bytes).flush();
            return baos.toString("UTF-8");
        }
        catch (IOException exception)
        {
            // Cannot happen because ByteOutputStream.write(byte[]) cannot fail and UTF-8 exists
            return "";
        }
    }

    /**
     * Create a HexDumper object with addressOffset 0; use it to dump an array of bytes and return the dump as a String.
     * @param bytes the bytes to hex-dump
     * @return the hexadecimal and character dump of the <code>bytes</code>
     */
    public static String hexDumper(final byte[] bytes)
    {
        return hexDumper(0, bytes);
    }

    @Override
    public String toString()
    {
        return "HexDumper [super=" + super.toString() + "]";
    }

}
