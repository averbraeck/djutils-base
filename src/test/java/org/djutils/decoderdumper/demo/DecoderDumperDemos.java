package org.djutils.decoderdumper.demo;

import java.io.IOException;

import org.djutils.decoderdumper.HexDumper;

/**
 * Demonstration code.
 * @author pknoppers
 */
public final class DecoderDumperDemos
{
    /**
     * Do not instantiate.
     */
    private DecoderDumperDemos()
    {
        // Do not instantiate.
    }

    /**
     * Run the demonstation(s).
     * @param args can be left blank
     * @throws IOException on error
     */
    public static void main(final String[] args) throws IOException
    {
        byte[] bytes = new byte[] {10, 20, 0x04, (byte) 0xff, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'};
        System.out.println(HexDumper.hexDumper(bytes));

        System.out.println(HexDumper.hexDumper(123, bytes));

        HexDumper hexDumper = new HexDumper();
        hexDumper.setOutputStream(System.err); // from now on, all output is sent to the error output
        hexDumper.append(bytes); // will output the first complete line; buffer byte 17 as first byte of the next output line
        System.err.println("before flush");
        hexDumper.flush(); // force buffered output to printed
        System.err.println("adding more data");
        byte[] moreBytes = new byte[] {-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, -16, -17};
        hexDumper.append(moreBytes); // will output complete line with gap for previously output data and buffer two bytes
        System.err.println("before 2nd flush");
        hexDumper.flush(); // force the two buffered bytes to be output
    }

}
