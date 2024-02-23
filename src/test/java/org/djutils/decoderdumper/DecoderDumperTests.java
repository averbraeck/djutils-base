package org.djutils.decoderdumper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests for the decoder/dumper package.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Jan 3, 2019 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class DecoderDumperTests
{

    /**
     * Test the Hex decoder and dumper classes.
     * @throws InterruptedException if that happens; this test has failed.
     * @throws IOException if that happens; this test has failed.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public final void testHexDumper() throws InterruptedException, IOException
    {
        Locale.setDefault(Locale.US);
        assertEquals("", HexDumper.hexDumper(new byte[] {}), "Empty input yields empty output");
        byte[] input = new byte[] {1, 2};
        String output = HexDumper.hexDumper(input);
        assertTrue(output.startsWith("00000000: "), "Output starts with address \"00000000: \"");
        for (int length = 1; length < 100; length++)
        {
            input = new byte[length];
            assertTrue(HexDumper.hexDumper(input).endsWith("\n"), "Output ends on newline");
        }
        input = new byte[1];
        for (int value = 0; value < 256; value++)
        {
            input[0] = (byte) value;
            output = HexDumper.hexDumper(input);
            // System.out.print(String.format("%3d -> %s", value, output));
            assertTrue(output.contains(String.format(" %02x ", value)),
                    "Output contains hex value of the only input byte embedded between spaces");
        }
        assertEquals(1, HexDumper.hexDumper(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}).split("\n").length,
                "output of 16 byte input fills one lines");
        assertEquals(2, HexDumper.hexDumper(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}).split("\n").length,
                "output of 17 byte input fills two lines");
        assertTrue(HexDumper.hexDumper(0x12345, new byte[] {0, 1}).startsWith("00012340"),
                "address offset is printed at start of output");
        Dumper<HexDumper> hd = new HexDumper(0x12345);
        assertTrue(hd.toString().startsWith("HexDumper"), "toString makes some sense");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        hd = new HexDumper().setOutputStream(baos);
        for (int i = 0; i < 100; i++)
        {
            hd.append((byte) i);
            // System.out.println("i=" + i + ", hd=" + hd + " baos=" + baos);
            assertEquals(Math.max(1, (i + 1) / 16), baos.toString().split("\n").length, "Number of lines check");
        }
        // System.out.println(hd.getDump());
        for (int i = 33; i < 127; i++)
        {
            String dump = HexDumper.hexDumper(new byte[] {(byte) i});
            String letter = "" + (char) i;
            String trimmed = dump.trim();
            String lastLetter = trimmed.substring(trimmed.length() - 1);
            // System.out.print("i=" + i + " letter=" + letter + ", output is: " + dump);
            assertEquals(letter, lastLetter, "Output ends with the provided printable character");
        }
        baos.reset();
        hd = new HexDumper().addDecoder(0, new TimeStamper()).setOutputStream(baos);
        long startTimeStamp = System.currentTimeMillis();
        hd.append((byte) 10);
        long endTimeStamp = System.currentTimeMillis();
        Thread.sleep(100);
        hd.append((byte) 20);
        hd.flush();
        String result = baos.toString();
        int spacePosition = result.indexOf(" ");
        long recorded = Long.parseLong(result.substring(0, spacePosition).replace(".", "").replace(",", ""));
        assertTrue(startTimeStamp <= recorded, "Time stamp should be within interval");
        assertTrue(endTimeStamp >= recorded, "Time stamp should be within interval");
        hd = new HexDumper().setOutputStream(new OutputStream()
        {

            @Override
            public void write(final int b) throws IOException
            {
                throw new IOException("testing exception handling");
            }
        });
        try
        {
            hd.append(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
            fail("Writing sufficient number of bytes to output that throws an exception should have thrown an exception");
        }
        catch (Exception exception)
        {
            // Ignore expected exception
        }
        baos.reset();
        hd = new HexDumper().setOutputStream(baos);
        hd.append(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
        // By now there should be something in the ByteArrayOutputStream
        assertTrue(baos.toString().startsWith("00000000: 00 "), "ByteArrayOutputStream contains start of hex dump");
        baos.reset();
        PrintStream oldErrOutput = System.err;
        PrintStream ps = new PrintStream(new BufferedOutputStream(baos));
        System.setErr(ps);
        // Redirect the output to System.err
        hd = new HexDumper().setOutputStream(new OutputStream()
        {
            /** The string builder. */
            private StringBuilder sb = new StringBuilder();

            @Override
            public void write(final int b) throws IOException
            {
                if ('\n' == b)
                {
                    System.err.println(this.sb.toString());
                    this.sb.setLength(0);
                }
                else
                {
                    this.sb.append((char) b);
                }
            }
        });
        for (int value = 0; value < 256; value++)
        {
            input[0] = (byte) value;
            hd.append(input);
        }
        Thread.sleep(200);
        ps.close();
        System.setErr(oldErrOutput);
        result = baos.toString();
        assertEquals(16, result.split("\n").length, "Result should be 16 lines");
        // System.out.print("baos contains:\n" + result);
        baos.reset();
        hd = new HexDumper().setOutputStream(baos).append(new byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 4, 2);
        hd.flush();
        result = baos.toString();
        assertTrue(result.startsWith("00000000: 05 06    "), "start and length parameter select the correct bytes");
        baos.reset();
        hd = new HexDumper().setOutputStream(baos);
        hd.append(new InputStream()
        {
            private int callCount = 0;

            @Override
            public int read() throws IOException
            {
                if (this.callCount < 10)
                {
                    return this.callCount++;
                }
                return -1;
            }
        });
        hd.flush();
        result = baos.toString();
        // System.out.println(result);
        assertTrue(result.startsWith("00000000: 00 01 02 03 04 05 06 07  08 09    "), "Ten bytes should have been accumulated");
        baos.reset();
        hd = new HexDumper().setSuppressMultipleIdenticalLines(true).setOutputStream(baos);
        for (int line = 0; line < 20; line++)
        {
            hd.append(new byte[] {42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57});
        }
        hd.flush();
        // System.out.println(baos);
        assertEquals(3, baos.toString().split("\n").length, "Suppression reduced the output to three lines");
        // System.out.println(baos);
        baos.reset();
        hd = new HexDumper().setSuppressMultipleIdenticalLines(true).setOutputStream(baos);
        for (int line = 0; line < 20; line++)
        {
            hd.append(new byte[] {42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57});
        }
        hd.append(new byte[] {42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56});
        hd.flush();
        assertEquals(4, baos.toString().split("\n").length, "Suppression reduced the output to four lines");
        // System.out.println(baos);
        baos.reset();
        hd = new HexDumper().setSuppressMultipleIdenticalLines(true).setOutputStream(baos);
        for (int line = 0; line < 20; line++)
        {
            hd.append(new byte[] {42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57});
        }
        hd.append(new byte[] {42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 99});
        hd.flush();
        assertEquals(4, baos.toString().split("\n").length, "Suppression reduced the output to four lines");
        // System.out.println(baos);
        // FIXME: not exhaustively testing switching compression on and off between append calls.
        assertTrue(new TimeStamper().toString().startsWith("TimeStamper ["), "TimeStamper had decent toString method");
    }

    /**
     * Test the Base64 decoder and dumper classes.
     */
    @Test
    public void testBase64Dumper()
    {
        assertEquals("", Base64Dumper.base64Dumper(new byte[] {}), "Empty input yields empty output");
        byte[] input = new byte[] {1, 2};
        String output = HexDumper.hexDumper(input);
        assertTrue(output.startsWith("00000000: "), "Output starts with address \"00000000: \"");
        for (int length = 1; length < 100; length++)
        {
            input = new byte[length];
            assertTrue(HexDumper.hexDumper(input).endsWith("\n"), "Output ends on newline (even though the input is invalid)");
        }
        // Generate many possible 24-bit values; then construct the base64 string that would generate the 3 bytes
        for (int pattern = 0; pattern < 256 * 256 * 256; pattern += 259)
        {
            input = new byte[4];
            for (int index = 0; index < 4; index++)
            {
                input[index] = encode((pattern >> (18 - 6 * index)) & 0x3f);
            }
            output = Base64Dumper.base64Dumper(input).substring(30);
            // System.out.println("input \"" + pattern +"\", output \"" + output + "\"");
            for (int index = 0; index < 3; index++)
            {
                int theByte = Integer.parseInt(output.substring(index * 3, index * 3 + 2), 16);
                int expectedByte = (pattern >> (16 - 8 * index)) & 0xff;
                assertEquals(expectedByte, theByte, "Reconstructed byte matches corresponding byte in pattern");
            }
        }
        // Generate all possible 8-bit values and pad with two = signs
        for (int pattern = 0; pattern < 255; pattern++)
        {
            input = new byte[4];
            for (int index = 0; index < 2; index++)
            {
                input[index] = encode(((pattern << 16) >> (18 - 6 * index)) & 0x3f);
            }
            input[2] = (byte) 61;
            input[3] = (byte) 61;
            output = Base64Dumper.base64Dumper(input).substring(30);
            // System.out.println("input " + pattern + ", base64=" + Arrays.toString(input) + ", output \"" + output + "\"");
            int theByte = Integer.parseInt(output.substring(0, 2), 16);
            assertEquals(pattern, theByte, "Reconstructed byte matches corresponding byte in patten");
            assertTrue(output.substring(2).startsWith("          "), "Rest of result starts with at least 10 spaces");
        }
        // Generate all possible 16-bit values and pad with one = sign
        for (int pattern = 0; pattern < 255 * 255; pattern++)
        {
            input = new byte[4];
            for (int index = 0; index < 3; index++)
            {
                input[index] = encode(((pattern << 8) >> (18 - 6 * index)) & 0x3f);
            }
            input[3] = (byte) 61;
            output = Base64Dumper.base64Dumper(input).substring(30);
            // System.out.println("input " + pattern + ", base64=" + Arrays.toString(input) + ", output \"" + output + "\"");
            for (int index = 0; index < 2; index++)
            {
                int theByte = Integer.parseInt(output.substring(index * 3, index * 3 + 2), 16);
                int expectedByte = (pattern >> (8 - 8 * index)) & 0xff;
                assertEquals(expectedByte, theByte, "Reconstructed byte matches corresponding byte in pattern");
            }
            assertTrue(output.substring(5).startsWith("          "), "Rest of result starts with at least 10 spaces");
        }
        assertTrue(new Base64Dumper().toString().startsWith("Base64Dumper"), "toString makes some sense");
        // White space in base64 encoded data should be ignored
        String base64 = "c3VyZS4=";
        String expectedResult = Base64Dumper.base64Dumper(base64.getBytes()).substring(30);
        // System.out.print("reference: " + expectedResult);
        for (int pos = 0; pos <= base64.length(); pos++)
        {
            for (String insert : new String[] {" ", "\t", "\n", "\n\t"})
            {
                String modified = base64.substring(0, pos) + insert + base64.substring(pos);
                String result = Base64Dumper.base64Dumper(modified.getBytes()).substring(30);
                // System.out.print("result: " + result);
                assertEquals(expectedResult, result, "Extra space in input does not change output");
            }
        }
        // Error character in input is (currently) silently ignored
        String result = Base64Dumper.base64Dumper(("!" + base64).getBytes()).substring(30);
        // System.out.print("result: " + result);
        assertEquals(expectedResult, result, "bad char in input is (currently) ignored");
    }

    /**
     * Base64 encode one 6-bit value.
     * @param value int; value in the range 0..63
     * @return byte; the encoded value
     */
    private byte encode(final int value)
    {
        if (value < 0 || value > 63)
        {
            throw new Error("Bad input: " + value);
        }
        if (value < 26)
        {
            return (byte) (value + 65);
        }
        else if (value < 52)
        {
            return (byte) (value - 26 + 97);
        }
        else if (value < 62)
        {
            return (byte) (value - 52 + 48);
        }
        else if (value == 62)
        {
            return (byte) 43;
        }
        else if (value == 63)
        {
            return (byte) 47;
        }
        throw new Error("Bad input: " + value);
    }

}
