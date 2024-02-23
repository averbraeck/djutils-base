package org.djutils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

/**
 * CompressedFileWriterTest tests the CompressedFileWriter class.
 * <p>
 * Copyright (c) 2023-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class CompressedFileWriterTest
{
    /**
     * Test the CompressedFileWriter class with a filename with .zip.
     * @throws IOException on errors with the temporary zip file
     */
    @Test
    public void testCompressedFileWriter() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + UUID.randomUUID().toString() + ".zip";
        try (CompressedFileWriter compressedFileWriter = new CompressedFileWriter(tempFilePath))
        {
            BufferedWriter bufferedWriter = compressedFileWriter.next("data_2023.csv");
            bufferedWriter.write("a,b,c\n1,2,3\n4,5,6\n");
            compressedFileWriter.next("data_2024.csv");
            bufferedWriter.write("a,b,c\n7,8,9\n10,11,12\n13,14,15\n");
        }

        checkZip(tempFilePath);

        new File(tempFilePath).delete();
    }

    /**
     * Test the CompressedFileWriter class, where the .zip extension is not given.
     * @throws IOException on errors with the temporary zip file
     */
    @Test
    public void testCompressedFileWriterNoExtension() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + UUID.randomUUID().toString();
        try (CompressedFileWriter compressedFileWriter = new CompressedFileWriter(tempFilePath))
        {
            BufferedWriter bufferedWriter = compressedFileWriter.next("data_2023.csv");
            bufferedWriter.write("a,b,c\n1,2,3\n4,5,6\n");
            compressedFileWriter.next("data_2024.csv");
            bufferedWriter.write("a,b,c\n7,8,9\n10,11,12\n13,14,15\n");
        }

        checkZip(tempFilePath + ".zip");

        new File(tempFilePath + ".zip").delete();
    }

    /**
     * Test the CompressedFileWriter class for the create() method.
     * @throws IOException on errors with the temporary zip file
     */
    @Test
    public void testCompressedFileWriterCreateExtension() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + UUID.randomUUID().toString() + "_data_2023.csv.zip";
        try (BufferedWriter bufferedWriter = CompressedFileWriter.create(tempFilePath, true))
        {
            bufferedWriter.write("a,b,c\n1,2,3\n4,5,6\n");
            bufferedWriter.close();
        }

        checkZip(tempFilePath);

        new File(tempFilePath).delete();
    }

    /**
     * Test the CompressedFileWriter class for the create() method.
     * @throws IOException on errors with the temporary zip file
     */
    @Test
    public void testCompressedFileWriterCreateNoExtension() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + UUID.randomUUID().toString() + "_data_2023.csv";
        try (BufferedWriter bufferedWriter = CompressedFileWriter.create(tempFilePath, true))
        {
            bufferedWriter.write("a,b,c\n1,2,3\n4,5,6\n");
            bufferedWriter.close();
        }

        checkZip(tempFilePath + ".zip");

        new File(tempFilePath + ".zip").delete();
    }

    /**
     * Test the CompressedFileWriter class for the create() method, where the file is not zipped.
     * @throws IOException on errors with the temporary zip file
     */
    @Test
    public void testCompressedFileWriterCreateNoZip() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + UUID.randomUUID().toString() + "_data_2023.csv";
        try (BufferedWriter bufferedWriter = CompressedFileWriter.create(tempFilePath, false))
        {
            bufferedWriter.write("a,b,c\n1,2,3\n4,5,6\n");
            bufferedWriter.close();
        }

        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(tempFilePath)))
        {
            String csv = new String(fis.readAllBytes());
            String[] lines = csv.split("\\n");
            check2023Lines(lines);
        }

        new File(tempFilePath).delete();
    }

    /**
     * Test the CompressedFileWriter class for empty files.
     * @throws IOException on uncaught error
     */
    @Test
    public void testCompressedFileWriterEmpty() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + UUID.randomUUID().toString() + ".zip";
        
        // empty zip file
        CompressedFileWriter compressedFileWriter = new CompressedFileWriter(tempFilePath);
        compressedFileWriter.close();
        new File(tempFilePath).delete();
        
        // empty entry in zip file
        compressedFileWriter = new CompressedFileWriter(tempFilePath);
        compressedFileWriter.next("data_2023.csv");
        compressedFileWriter.close();
        new File(tempFilePath).delete();

    }

    /**
     * Test the CompressedFileWriter class for the correct exceptions.
     * @throws IOException on uncaught error
     */
    @Test
    public void testCompressedFileWriterErrors() throws IOException
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        final String tempFilePath = tempDir + File.separator + "//>>::.:/><:";
        Try.testFail(() -> new CompressedFileWriter(tempFilePath), IOException.class);
        Try.testFail(() -> CompressedFileWriter.create(tempFilePath, true), IOException.class);
        String tempFilePath2 = tempDir + File.separator + UUID.randomUUID().toString() + ".zip";
        CompressedFileWriter compressedFileWriter = new CompressedFileWriter(tempFilePath2);
        BufferedWriter bufferedWriter = compressedFileWriter.next("data_2023.csv");
        bufferedWriter.write("a,b,c\n1,2,3\n4,5,6\n");
        bufferedWriter.close();
        Try.testFail(() -> compressedFileWriter.next("data_2024.csv"), IOException.class);
        Try.testFail(() -> compressedFileWriter.close());
        new File(tempFilePath).delete();
    }

    /**
     * Check if the zip file contains the right data.
     * @param path String; the path of the zip file to check
     * @throws IOException on errors with the temporary zip file
     */
    private void checkZip(final String path) throws IOException
    {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null)
        {
            if (zipEntry.isDirectory())
            {
                fail("zip file contains a directory");
            }
            else
            {
                String csv = new String(zis.readAllBytes());
                String[] lines = csv.split("\\n");
                if (zipEntry.getName().contains("data_2023"))
                {
                    assertTrue(zipEntry.getName().endsWith(".csv"));
                    check2023Lines(lines);
                }
                else
                {
                    assertTrue(zipEntry.getName().endsWith(".csv"));
                    check2024Lines(lines);
                }
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    /**
     * Check the lines in the data_2023.csv file or entry.
     * @param lines String[] the line array with file content
     */
    private void check2023Lines(final String[] lines)
    {
        assertEquals(3, lines.length);
        String[] entries0 = lines[0].split(",");
        assertEquals("a", entries0[0]);
        assertEquals("b", entries0[1]);
        assertEquals("c", entries0[2]);
        String[] entries1 = lines[1].split(",");
        assertEquals("1", entries1[0]);
        assertEquals("2", entries1[1]);
        assertEquals("3", entries1[2]);
        String[] entries2 = lines[2].split(",");
        assertEquals("4", entries2[0]);
        assertEquals("5", entries2[1]);
        assertEquals("6", entries2[2]);
    }

    /**
     * Check the lines in the data_2024.csv file or entry.
     * @param lines String[] the line array with file content
     */
    private void check2024Lines(final String[] lines)
    {
        assertEquals(4, lines.length);
        String[] entries0 = lines[0].split(",");
        assertEquals("a", entries0[0]);
        assertEquals("b", entries0[1]);
        assertEquals("c", entries0[2]);
        String[] entries1 = lines[1].split(",");
        assertEquals("7", entries1[0]);
        assertEquals("8", entries1[1]);
        assertEquals("9", entries1[2]);
        String[] entries2 = lines[2].split(",");
        assertEquals("10", entries2[0]);
        assertEquals("11", entries2[1]);
        assertEquals("12", entries2[2]);
        String[] entries3 = lines[3].split(",");
        assertEquals("13", entries3[0]);
        assertEquals("14", entries3[1]);
        assertEquals("15", entries3[2]);
    }

}
