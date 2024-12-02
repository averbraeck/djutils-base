package org.djutils.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.djutils.io.URLResource;
import org.djutils.reflection.ClassUtil.ClassFileDescriptor;

/**
 * ClassFileDescriptorTest.java. <br>
 * <br>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class ClassFileDescriptorTest
{
    /**
     * Test the classFileDescriptor method in ClassUtil.
     * @throws ParseException on error
     * @throws MalformedURLException on error
     */
    // TODO: @Test test fails under Ubuntu
    public void classFileDescriptorTest() throws ParseException, MalformedURLException
    {
        URL cfdClassURL = URLResource.getResource("/org/djutils-test-resources/test/Test.class");
        // change the last accessed date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File fileClass = new File(cfdClassURL.getPath());
        fileClass.setLastModified(formatter.parse("2000-01-01 01:02:03").getTime());

        ClassFileDescriptor cfdClass = ClassUtil.classFileDescriptor(cfdClassURL);
        assertEquals("Test.class", cfdClass.getName());
        Date cfdClassDate = new Date(cfdClass.getLastChangedDate());
        assertEquals("2000-01-01 01:02:03", formatter.format(cfdClassDate));

        assertTrue(
                cfdClass.getPath().toString().replaceAll("\\\\", "/").endsWith("/org/djutils-test-resources/test/Test.class"));
        assertTrue(cfdClass.toString().startsWith("ClassFileDescriptor ["));

        URL cfdJarURL = URLResource.getResource("/org/djutils-test-resources/test/Test.jar");
        // change the last accessed date of the jar -- not of the file in the jar
        File fileJar = new File(cfdJarURL.getPath());
        fileJar.setLastModified(formatter.parse("2010-11-12 01:02:03").getTime());

        ClassFileDescriptor cfdJar = ClassUtil.classFileDescriptor(cfdJarURL);
        assertEquals("Test.jar", cfdJar.getName());
        Date cfdJarDate = new Date(cfdJar.getLastChangedDate());
        assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarDate));

        // find the file in the jar
        URL cfdJarFileURL = new URL("jar:file:" + cfdJarURL.getPath() + "!/Test.class");
        ClassFileDescriptor cfdJarFile = ClassUtil.classFileDescriptor(cfdJarFileURL);
        assertEquals("Test.class", cfdJarFile.getName());
        Date cfdJarFileDate = new Date(cfdJarFile.getLastChangedDate());
        assertEquals("2000-01-01 01:02:03", formatter.format(cfdJarFileDate));

        // if the file in the jar cannot be found, we should get the file descriptor of the jar itself
        URL cfdJarFileURL2 = new URL("jar:file:" + cfdJarURL.getPath() + "!/TestXYZ.class");
        ClassFileDescriptor cfdJarFile2 = ClassUtil.classFileDescriptor(cfdJarFileURL2);
        assertEquals("Test.jar", cfdJarFile2.getName());
        Date cfdJarFileDate2 = new Date(cfdJarFile2.getLastChangedDate());
        assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarFileDate2));
    }

    /**
     * Test the classFileDescriptor method in ClassUtil.
     * @throws ParseException on error
     * @throws MalformedURLException on error
     * @throws URISyntaxException on error
     */
    // TODO: @Test test fails under Ubuntu
    public void classFileDescriptorTestWithSpaces() throws ParseException, MalformedURLException, URISyntaxException
    {
        URL cfdClassURL = URLResource.getResource("/org/djutils-test-resources/test folder/Test.class");
        // change the last accessed date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File fileClass = new File(cfdClassURL.toURI());
        assertTrue(fileClass.exists());
        fileClass.setLastModified(formatter.parse("2000-01-01 01:02:03").getTime());

        ClassFileDescriptor cfdClass = ClassUtil.classFileDescriptor(cfdClassURL);
        assertEquals("Test.class", cfdClass.getName());
        Date cfdClassDate = new Date(cfdClass.getLastChangedDate());
        assertEquals("2000-01-01 01:02:03", formatter.format(cfdClassDate));

        URL cfdJarURL = URLResource.getResource("/org/djutils-test-resources/test folder/Test.jar");
        // change the last accessed date of the jar -- not of the file in the jar
        File fileJar = new File(cfdJarURL.toURI());
        fileJar.setLastModified(formatter.parse("2010-11-12 01:02:03").getTime());

        ClassFileDescriptor cfdJar = ClassUtil.classFileDescriptor(cfdJarURL);
        assertEquals("Test.jar", cfdJar.getName());
        Date cfdJarDate = new Date(cfdJar.getLastChangedDate());
        assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarDate));

        // find the file in the jar
        URL cfdJarFileURL = new URL("jar:file:" + cfdJarURL.getPath() + "!/Test.class");
        ClassFileDescriptor cfdJarFile = ClassUtil.classFileDescriptor(cfdJarFileURL);
        assertEquals("Test.class", cfdJarFile.getName());
        Date cfdJarFileDate = new Date(cfdJarFile.getLastChangedDate());
        assertEquals("2000-01-01 01:02:03", formatter.format(cfdJarFileDate));

        // if the file in the jar cannot be found, we should get the file descriptor of the jar itself
        URL cfdJarFileURL2 = new URL("jar:file:" + cfdJarURL.getPath() + "!/TestXYZ.class");
        ClassFileDescriptor cfdJarFile2 = ClassUtil.classFileDescriptor(cfdJarFileURL2);
        assertEquals("Test.jar", cfdJarFile2.getName());
        Date cfdJarFileDate2 = new Date(cfdJarFile2.getLastChangedDate());
        assertEquals("2010-11-12 01:02:03", formatter.format(cfdJarFileDate2));
    }

}
