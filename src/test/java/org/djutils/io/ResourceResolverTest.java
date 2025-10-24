package org.djutils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

/**
 * ResourceResolverTest tests the ResourceResolver class.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ResourceResolverTest
{
    /**
     * Test whether ResourceResolver retrieves files.
     * @throws IOException on I/O error
     */
    @Test
    public final void fileTest() throws IOException
    {
        // List<String> s = Files.readAllLines(ResourceResolver.resolve("test.txt").asPath());
        // create a temporary file.
        Path tempFile = Files.createTempFile("filetest-", ".temp");
        tempFile.toFile().deleteOnExit();
        var res1 = ResourceResolver.resolve(tempFile.toAbsolutePath().toString());
        assertNotNull(res1);
        assertEquals(tempFile, res1.asPath());
        assertFalse(res1.isClassPath());

        Path folder = tempFile.getParent();
        Path filename = tempFile.getFileName();
        var res2 = ResourceResolver.resolve(filename.toString(), folder);
        assertEquals(res1.asUri(), res2.asUri());
        var res3 = ResourceResolver.resolve(filename.toString(), folder.toAbsolutePath().toString());
        assertEquals(res1.asUri(), res3.asUri());
        var res4 = ResourceResolver.resolve(filename.toString(), ResourceResolver.class.getClassLoader(), folder);
        assertEquals(res1.asUri(), res4.asUri());
        var res5 = ResourceResolver.resolve(filename.toString(), ResourceResolver.class.getClassLoader(),
                folder.toAbsolutePath().toString());
        assertEquals(res1.asUri(), res5.asUri());
        
        String uri = res1.asUri().toString();
        var res7 = ResourceResolver.resolve(uri);
        assertEquals(res1.asUri(), res7.asUri());

        // non-existent file
        UnitTest.testFail(() -> ResourceResolver.resolve(tempFile.toAbsolutePath().toString() + ".xyz"));
    }

    /**
     * Test whether ResourceResolver retrieves resources.
     * @throws IOException on I/O error
     * @throws URISyntaxException on URL error
     */
    @Test
    public final void resourceTest() throws IOException, URISyntaxException
    {
        String path1 = "/org/djutils-test-resources/test.txt";
        var res1 = ResourceResolver.resolve(path1);
        List<String> lines = Files.readAllLines(res1.asPath());
        assertEquals(3, lines.size());
        assertEquals("abc", lines.get(0));
        assertTrue(res1.isClassPath());

        String path2 = "org/djutils-test-resources/test.txt";
        lines = Files.readAllLines(ResourceResolver.resolve(path2).asPath());
        assertEquals(3, lines.size());
        assertEquals("def", lines.get(1));

        String path3 = "/org/djutils-test-resources/test.txt";
        lines = Files.readAllLines(ResourceResolver.resolveAsResource(path3).asPath());
        assertEquals(3, lines.size());
        assertEquals("def", lines.get(1));

        String path4 = "org/djutils-test-resources/test.txt";
        lines = Files.readAllLines(ResourceResolver.resolveAsResource(path4).asPath());
        assertEquals(3, lines.size());
        assertEquals("def", lines.get(1));

        UnitTest.testFail(() -> ResourceResolver.resolveAsResource("/org/abc.xyz").asPath());
        UnitTest.testFail(() -> ResourceResolver.resolveAsResource("org/abc.xyz").asPath());
    }

    /**
     * Test whether ResourceResolver can open a stream.
     * @throws IOException on I/O error
     * @throws URISyntaxException on URL error
     */
    @Test
    public final void resourceAsStreamTest() throws IOException, URISyntaxException
    {
        InputStream stream = ResourceResolver.resolve("/org/djutils-test-resources/test.txt").openStream();
        byte[] barr = stream.readAllBytes();
        stream.close();
        assertEquals('a', barr[0]);

        UnitTest.testFail(() -> (ResourceResolver.resolve("xxx:///org::djutils-test-resources<>test.txt").openStream()));
        UnitTest.testFail(() -> ResourceResolver.resolve("/org/djutils-test-resources/test123.txt").openStream());
    }

    /**
     * Test whether ResourceResolver retrieves entries from jar-files.
     * @throws IOException on I/O error
     * @throws URISyntaxException on error
     */
    @Test
    public final void jarTest() throws IOException, URISyntaxException
    {
        // create a temporary jar file.
        File jarFile = File.createTempFile("filetest-", ".jar");
        jarFile.deleteOnExit();
        File file1 = File.createTempFile("filetest-", ".f1");
        file1.deleteOnExit();
        File file2 = File.createTempFile("filetest-", ".f2");
        file2.deleteOnExit();
        File file3 = File.createTempFile("filetest-", ".f3");
        file3.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(jarFile.getAbsolutePath());
        JarOutputStream jos = new JarOutputStream(fos);
        addToJarFile(file1.getAbsolutePath(), jos);
        addToJarFile(file2.getAbsolutePath(), jos);
        addToJarFile(file3.getAbsolutePath(), jos);
        jos.close();
        fos.close();

        String jarFilePath = jarFile.getAbsolutePath();
        URL jarURL = ResourceResolver.resolve(jarFilePath).asUrl();
        assertNotNull(jarURL);
        URL jar1 = ResourceResolver.resolve(jarFile.getAbsolutePath() + "!/" + file1.getName()).asUrl();
        assertNotNull(jar1);
    }

    /**
     * Test whether ResourceResolver retrieves entries from http-files.
     * @throws IOException on I/O error
     */
    @Test
    public final void httpTest() throws IOException
    {
        String url = "https://djutils.org/manual/index.html";
        var res = ResourceResolver.resolve(url);
        assertNotNull(res);
        try (var stream = res.openStream())
        {
            byte[] data = stream.readNBytes(100);
            String lines = new String(data).strip();
            assertTrue(lines.toLowerCase().startsWith("<!doctype html>"));
        }
    }

    /**
     * Test whether ResourceResolver can store an ftp handle.
     * @throws IOException on I/O error
     */
    @Test
    public final void ftpTest() throws IOException
    {
        String url = "ftp://djutils.org/manual/index.html";
        var res = ResourceResolver.resolve(url);
        assertNotNull(res);
        
        final String xurl = "xxftp://djutils.org/manual/index.html";
        UnitTest.testFail(() -> ResourceResolver.resolve(xurl));
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

}
