package org.djutils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.Test;

/**
 * URLResourceTest.java.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class URLResourceTest
{

    /**
     * Test whether URLResource retrieves files.
     * @throws IOException on I/O error
     */
    // TODO @Test -- this test does not run on ubuntu
    public final void fileTest() throws IOException
    {
        // create a temporary file.
        File tempFile = File.createTempFile("filetest-", ".temp");
        String tempFilePath = tempFile.getAbsolutePath();
        URL url1 = URLResource.getResource(tempFilePath);
        assertNotNull(url1);
        assertEquals(new File(url1.getPath()).getAbsolutePath().replaceAll("\\\\", "/"), tempFilePath.replaceAll("\\\\", "/"));

        URL url2 = URLResource.getResource("/" + tempFilePath);
        assertNotNull(url2);
        assertEquals(new File(url2.getPath()).getAbsolutePath().replaceAll("\\\\", "/"), tempFilePath.replaceAll("\\\\", "/"));
    }

    /**
     * Test whether URLResource retrieves files.
     * @throws IOException on I/O error
     * @throws URISyntaxException on URL error
     */
    @Test
    public final void resourceTest() throws IOException, URISyntaxException
    {
        URL url = URLResource.getResource("/org/djutils-test-resources/test.txt");
        String absolutePath = url.toURI().getPath();
        List<String> lines = Files.readAllLines(Paths.get(url.toURI()));
        assertEquals(3, lines.size());
        assertEquals("abc", lines.get(0));

        url = null;
        lines = null;

        url = URLResource.getResource("test.txt", "/org/djutils-test-resources/");
        lines = Files.readAllLines(Paths.get(url.toURI()));
        assertEquals(3, lines.size());
        assertEquals("def", lines.get(1));

        url = null;
        lines = null;

        url = URLResource.getResource("/org/djutils-test-resources/test.txt", "/");
        lines = Files.readAllLines(Paths.get(url.toURI()));
        assertEquals(3, lines.size());
        assertEquals("ghi", lines.get(2));

        url = null;
        lines = null;

        url = URLResource.getResource("file://" + absolutePath);
        lines = Files.readAllLines(Paths.get(url.toURI()));
        assertEquals(3, lines.size());
        assertEquals("abc", lines.get(0));
    }

    /**
     * Test whether URLResource retrieves files.
     * @throws IOException on I/O error
     * @throws URISyntaxException on URL error
     */
    @Test
    public final void resourceAsStreamTest() throws IOException, URISyntaxException
    {
        InputStream stream = URLResource.getResourceAsStream("/org/djutils-test-resources/test.txt");
        byte[] barr = readAllBytes(stream);
        stream.close();
        assertEquals('a', barr[0]);

        assertNull(URLResource.getResourceAsStream("xxx:///org::djutils-test-resources<>test.txt"));

        stream = URLResource.getResourceAsStream("/org/djutils-test-resources/test123.txt");
        assertNull(stream);
    }

    /**
     * Test whether URLResource retrieves files.
     * @throws IOException on I/O error
     * @throws URISyntaxException on error
     */
    @Test
    public final void jarTest() throws IOException, URISyntaxException
    {
        // create a temporary jar file.
        File jarFile = File.createTempFile("filetest-", ".jar");
        File file1 = File.createTempFile("filetest-", ".f1");
        File file2 = File.createTempFile("filetest-", ".f2");
        File file3 = File.createTempFile("filetest-", ".f3");

        FileOutputStream fos = new FileOutputStream(jarFile.getAbsolutePath());
        JarOutputStream jos = new JarOutputStream(fos);
        addToJarFile(file1.getAbsolutePath(), jos);
        addToJarFile(file2.getAbsolutePath(), jos);
        addToJarFile(file3.getAbsolutePath(), jos);
        jos.close();
        fos.close();

        String jarFilePath = jarFile.getAbsolutePath();
        URL jarURL = URLResource.getResource(jarFilePath);
        assertNotNull(jarURL);
        // System.out.println(jarFile.getAbsolutePath() + "!" + file1.getName());
        // URL jar1 = URLResource.getResource(jarFile.getAbsolutePath() + "!" + file1.getName());
        // System.out.println(jar1.toURI());
        // assertNotNull(jar1);
    }

    /**
     * Copy a plain file into a jar file.
     * @param fileName name of the file to copy
     * @param jos stream for writing into the jar file
     * @throws FileNotFoundException when the input file could not be found
     * @throws IOException when the input file could not be read, or writing to the jar stream fails
     */
    public void addToJarFile(final String fileName, final JarOutputStream jos) throws FileNotFoundException, IOException
    {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        JarEntry zipEntry = new JarEntry(file.getName());
        jos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0)
        {
            jos.write(bytes, 0, length);
        }
        jos.closeEntry();
        fis.close();
    }

    /**
     * Copied from Java 13 to handle readAllBytes in Java 8.
     * @param stream the input stream to read the bytes from
     * @return the byte array
     * @throws IOException on I/O error
     */
    private byte[] readAllBytes(final InputStream stream) throws IOException
    {
        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = Integer.MAX_VALUE;
        int n;
        do
        {
            byte[] buf = new byte[Math.min(remaining, 8192)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = stream.read(buf, nread, Math.min(buf.length - nread, remaining))) > 0)
            {
                nread += n;
                remaining -= n;
            }

            if (nread > 0)
            {
                if (Integer.MAX_VALUE - 8 - total < nread)
                {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null)
                {
                    result = buf;
                }
                else
                {
                    if (bufs == null)
                    {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        }
        while (n >= 0 && remaining > 0);

        if (bufs == null)
        {
            if (result == null)
            {
                return new byte[0];
            }
            return result.length == total ? result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs)
        {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;

    }
}
