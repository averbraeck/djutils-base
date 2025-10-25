package org.djutils.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.djutils.io.ResourceResolver;
import org.djutils.reflection.ClassUtil.ClassFileDescriptor;
import org.junit.jupiter.api.Test;

/**
 * ClassFileDescriptorTest tests class file descriptors. <br>
 * <br>
 * Copyright (c) 2003-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ClassFileDescriptorTest
{
    /**
     * Test the classFileDescriptor method in ClassUtil.
     * @throws ParseException on error
     * @throws IOException on error
     */
    @Test
    public void classFileDescriptorTest() throws ParseException, IOException
    {
        // Use a fixed zone for the whole test
        TimeZone originalTz = TimeZone.getDefault();
        try
        {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            var original = ResourceResolver.resolve("/org/djutils-test-resources/test/Test.class");
            InputStream originalClassStream = original.openStream();
            File cfdFile = copyToTempFile(originalClassStream, "Test", ".class");
            var cfd = ResourceResolver.resolve(cfdFile.getAbsolutePath());

            // change the last accessed date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            cfdFile.setLastModified(formatter.parse("2000-01-01 02:03:04").getTime());

            ClassFileDescriptor cfdClass = ClassUtil.classFileDescriptorForPath(cfd.asPath());
            assertTrue(cfdClass.getName().startsWith("Test"));
            assertTrue(cfdClass.getName().endsWith(".class"));
            Date cfdClassDate = new Date(cfdClass.getLastChangedDate());
            assertEquals("2000-01-01 02:03:04", formatter.format(cfdClassDate));
            assertTrue(cfdClass.toString().startsWith("ClassFileDescriptor ["));

            var originalJar = ResourceResolver.resolve("/org/djutils-test-resources/test/Test.jar");
            InputStream jarStream = originalJar.openStream();
            File jarFile = copyToTempFile(jarStream, "Test", ".jar");
            var jar = ResourceResolver.resolve(jarFile.getAbsolutePath());

            // change the last accessed date of the jar -- not of the file in the jar
            jarFile.setLastModified(formatter.parse("2010-11-12 01:02:03").getTime());

            ClassFileDescriptor cfdJar = ClassUtil.classFileDescriptorForPath(jar.asPath());
            Date cfdJarDate = new Date(cfdJar.getLastChangedDate());
            assertTrue(cfdJar.getName().startsWith("Test"));
            assertTrue(cfdJar.getName().endsWith(".jar"));
            assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarDate));

            // find the file in the jar
            Path cfdJarFilePath = ResourceResolver.resolve(jar.asPath().toString() + "!/Test.class").asPath();
            ClassFileDescriptor cfdJarFile = ClassUtil.classFileDescriptorForPath(cfdJarFilePath);
            assertEquals("Test.class", cfdJarFile.getName());
            Date cfdJarFileDate = new Date(cfdJarFile.getLastChangedDate());
            assertTrue(formatter.format(cfdJarFileDate).startsWith("2000-01-01"));
            // Note: time cannot be checked -- file was made in CET, test runs in UTC

            // if the file in the jar cannot be found, we should get the file descriptor of the jar itself
            Path cfdJarFilePath2 = ResourceResolver.resolve(jar.asPath().toString() + "!/TestXYZ.class").asPath();
            ClassFileDescriptor cfdJarFile2 = ClassUtil.classFileDescriptorForPath(cfdJarFilePath2);
            assertTrue(cfdJarFile2.getName().startsWith("Test"));
            assertTrue(cfdJarFile2.getName().endsWith(".jar"));
            Date cfdJarFileDate2 = new Date(cfdJarFile2.getLastChangedDate());
            assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarFileDate2));
        }
        finally
        {
            TimeZone.setDefault(originalTz);
        }
    }

    /**
     * Test the classFileDescriptor method in ClassUtil with a path that contains a space.
     * @throws ParseException on error
     * @throws IOException on error
     */
    @Test
    public void classFileDescriptorTestWithSpaces() throws ParseException, IOException
    {
        // Use a fixed zone for the whole test
        TimeZone originalTz = TimeZone.getDefault();
        try
        {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            var original = ResourceResolver.resolve("/org/djutils-test-resources/test folder/Test.class");
            InputStream originalClassStream = original.openStream();
            File cfdFile = copyToTempFile(originalClassStream, "Test", ".class");
            var cfd = ResourceResolver.resolve(cfdFile.getAbsolutePath());

            // change the last accessed date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            cfdFile.setLastModified(formatter.parse("2000-01-01 02:03:04").getTime());

            ClassFileDescriptor cfdClass = ClassUtil.classFileDescriptorForPath(cfd.asPath());
            assertTrue(cfdClass.getName().startsWith("Test"));
            assertTrue(cfdClass.getName().endsWith(".class"));
            Date cfdClassDate = new Date(cfdClass.getLastChangedDate());
            assertEquals("2000-01-01 02:03:04", formatter.format(cfdClassDate));
            assertTrue(cfdClass.toString().startsWith("ClassFileDescriptor ["));

            var originalJar = ResourceResolver.resolve("/org/djutils-test-resources/test folder/Test.jar");
            InputStream jarStream = originalJar.openStream();
            File jarFile = copyToTempFile(jarStream, "Test", ".jar");
            var jar = ResourceResolver.resolve(jarFile.getAbsolutePath());

            // change the last accessed date of the jar -- not of the file in the jar
            jarFile.setLastModified(formatter.parse("2010-11-12 01:02:03").getTime());

            ClassFileDescriptor cfdJar = ClassUtil.classFileDescriptorForPath(jar.asPath());
            Date cfdJarDate = new Date(cfdJar.getLastChangedDate());
            assertTrue(cfdJar.getName().startsWith("Test"));
            assertTrue(cfdJar.getName().endsWith(".jar"));
            assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarDate));

            // find the file in the jar
            Path cfdJarFilePath = ResourceResolver.resolve(jar.asPath().toString() + "!/Test.class").asPath();
            ClassFileDescriptor cfdJarFile = ClassUtil.classFileDescriptorForPath(cfdJarFilePath);
            assertEquals("Test.class", cfdJarFile.getName());
            Date cfdJarFileDate = new Date(cfdJarFile.getLastChangedDate());
            assertTrue(formatter.format(cfdJarFileDate).startsWith("2000-01-01"));
            // Note: time cannot be checked -- file was made in CET, test runs in UTC

            // if the file in the jar cannot be found, we should get the file descriptor of the jar itself
            Path cfdJarFilePath2 = ResourceResolver.resolve(jar.asPath().toString() + "!/TestXYZ.class").asPath();
            ClassFileDescriptor cfdJarFile2 = ClassUtil.classFileDescriptorForPath(cfdJarFilePath2);
            assertTrue(cfdJarFile2.getName().startsWith("Test"));
            assertTrue(cfdJarFile2.getName().endsWith(".jar"));
            Date cfdJarFileDate2 = new Date(cfdJarFile2.getLastChangedDate());
            assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarFileDate2));
        }
        finally
        {
            TimeZone.setDefault(originalTz);
        }
    }

    /**
     * Copy a (class) file to a temporary file.
     * @param in the input stream of the file to copy
     * @param prefix the prefix of the file (will be appended to make it unique)
     * @param suffix the suffix of the file, including the 'dot'
     * @return the file handle of the copied file
     * @throws IOException on error
     */
    private static File copyToTempFile(final InputStream in, final String prefix, final String suffix) throws IOException
    {
        Path tempFile = Files.createTempFile(prefix, suffix);
        tempFile.toFile().deleteOnExit();
        try (OutputStream out = Files.newOutputStream(tempFile))
        {
            in.transferTo(out);
        }
        return tempFile.toFile();
    }
}
