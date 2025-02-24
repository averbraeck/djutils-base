package org.djutils.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;

/**
 * ExceptionTest has a generic test method for testing an exception with four constructors.
 * <p>
 * Copyright (c) 2024-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.github.com/WJSchakel">Wouter Schakel</a>
 */
public final class ExceptionTest
{
    /** */
    private ExceptionTest()
    {
        // utility class.
    }
    
    /**
     * Test exception class on constructors: empty, only message, only Throwable, message and Throwable.
     * @param clazz exception class to test.
     */
    public static void testExceptionClass(final Class<? extends Exception> clazz)
    {
        try
        {
            Constructor<? extends Exception> constructor = clazz.getConstructor();
            try
            {
                throw constructor.newInstance();
            }
            catch (Exception e)
            {
                checkRightException(e, clazz);
                assertNull(e.getMessage());
            }

            constructor = clazz.getConstructor(String.class);
            try
            {
                throw constructor.newInstance("abc");
            }
            catch (Exception e)
            {
                checkRightException(e, clazz);
                assertEquals("abc", e.getMessage());
            }

            constructor = clazz.getConstructor(Throwable.class);
            try
            {
                throw constructor.newInstance(new IllegalArgumentException());
            }
            catch (Exception e)
            {
                checkRightException(e, clazz);
                assertTrue(e.getMessage().contains("IllegalArgumentException"));
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }

            constructor = clazz.getConstructor(String.class, Throwable.class);
            try
            {
                throw constructor.newInstance("abc", new IllegalArgumentException("def"));
            }
            catch (Exception e)
            {
                checkRightException(e, clazz);
                assertEquals("abc", e.getMessage());
                assertTrue(e.getCause() instanceof IllegalArgumentException);
                assertEquals("def", e.getCause().getMessage());
            }
        }
        catch (SecurityException | NoSuchMethodException ex)
        {
            fail("Class " + clazz + " is missing standard and accessible exception constructor.");
        }
    }

    /**
     * Test exception class type.
     * @param e thrown exception
     * @param clazz class of exception that should have been thrown
     */
    private static void checkRightException(final Exception e, final Class<? extends Exception> clazz)
    {
        if (!e.getClass().equals(clazz))
        {
            fail("Right exception not thrown. It is " + e.getClass().getSimpleName() + " instead of " + clazz.getSimpleName());
        }
    }
}
