package org.djutils.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

/**
 * The JUNIT Test for <code>Throw</code> class.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class ThrowTest
{
    /**
     * Test the Throw class.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrow()
    {
        Object object = new Object();
        Object objectNull = null;
        int i = 10;
        Double d = Double.valueOf(20.0);
        String s = "argument";
        int hex = 26; // 1A

        //
        // Throw.whenNull(...)
        //

        String message = "Throw error has occurred. Correct";
        try
        {
            Throw.whenNull(objectNull, message);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNull(objectNull, "objectNull");
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith("objectNull may not be null"),
                    e.getMessage() + " does not end with 'may not be null'");
        }

        String message1arg = "Throw error has occurred for %s. Correct";
        String message1 = "Throw error has occurred for argument. Correct";
        try
        {
            Throw.whenNull(objectNull, message1arg, s);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        String message2arg = "Throw error %d has occurred for %s. Correct";
        String message2 = "Throw error 10 has occurred for argument. Correct";
        try
        {
            Throw.whenNull(objectNull, message2arg, i, s);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        String message3arg = "Throw error %4.1f has occurred for %s, value %d. Correct";
        String message3 = "Throw error 20.0 has occurred for argument, value 10. Correct";
        try
        {
            Throw.whenNull(objectNull, message3arg, d, s, i);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message3), e.getMessage() + " / " + message3);
        }

        String message4arg = "Throw error %4.1f has occurred for %s, hex=%h, value %d. Correct";
        String message4 = "Throw error 20.0 has occurred for argument, hex=1a, value 10. Correct";
        try
        {
            Throw.whenNull(objectNull, message4arg, d, s, hex, i);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message4), e.getMessage() + " / " + message4);
        }

        // this should be okay
        Throw.whenNull(object, "object is not null -- this should not be triggered");
        Throw.whenNull(object, message1arg, s);
        Throw.whenNull(object, message2arg, i, s);
        Throw.whenNull(object, message3arg, d, s, i);
        Throw.whenNull(object, message4arg, d, s, hex, i);

        //
        // Throw.when(...)
        //

        try
        {
            Throw.when(i == 10, Exception.class, message);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.when(i == 10, Exception.class, message1arg, s);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.when(i == 10, Exception.class, message2arg, i, s);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.when(i == 10, Exception.class, message3arg, d, s, i);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message3), e.getMessage() + " / " + message3);
        }

        try
        {
            Throw.when(i == 10, Exception.class, message4arg, d, s, hex, i);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().endsWith(message4), e.getMessage() + " / " + message4);
        }

        // this should be okay
        Throw.when(false, RuntimeException.class, "object is not null -- this should not be triggered");
        Throw.when(false, RuntimeException.class, message1arg, s);
        Throw.when(false, RuntimeException.class, message2arg, i, s);
        Throw.when(false, RuntimeException.class, message3arg, d, s, i);
        Throw.when(false, RuntimeException.class, message4arg, d, s, hex, i);

        assertEquals(d, Throw.when(d, false, RuntimeException.class, "object is not null -- this should not be triggered"));
        assertEquals(d, Throw.when(d, false, RuntimeException.class, message1arg, s));
        assertEquals(d, Throw.when(d, false, RuntimeException.class, message2arg, i, s));
        assertEquals(d, Throw.when(d, false, RuntimeException.class, message3arg, d, s, i));
        assertEquals(d, Throw.when(d, false, RuntimeException.class, message4arg, d, s, hex, i));

        try
        {
            Throw.when(true, RuntimeException.class, message);
            fail("Throw.when(true, ...) should have thrown an exception");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains(message), "message is present");
        }

        String badFormatString = "FormatString with bad placeholder %f";
        String notAFloatOrDouble = "this is not a float or double";
        try
        {
            Throw.when(true, RuntimeException.class, badFormatString, notAFloatOrDouble);
            fail("Throw.when(true, ...) should have thrown an exception");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains(badFormatString),
                    "description is descriptive, despite error in format string");
            assertTrue(rte.getMessage().contains(notAFloatOrDouble),
                    "description is descriptive, despite error in format string");
        }

        try
        {
            Throw.when("result (which will not be returned because there will be no return)", true, RuntimeException.class,
                    badFormatString, notAFloatOrDouble);
            fail("Throw.when(true, ...) should have thrown an exception");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains(badFormatString),
                    "description is descriptive, despite error in format string");
            assertTrue(rte.getMessage().contains(notAFloatOrDouble),
                    "description is descriptive, despite error in format string");
        }

        try
        {
            Throw.when("result (which will not be returned because there will be no return)", true, RuntimeException.class,
                    message);
            fail("Throw.when(true, ...) should have thrown an exception");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains(message), "description is descriptive");
        }

        Object result = Double.valueOf(123 / 456);
        String formatStringWith2PlaceHolders = "message %s %s";
        String arg1 = "arg1";
        String arg2 = "arg2";
        try
        {
            Throw.when(result, true, RuntimeException.class, formatStringWith2PlaceHolders, arg1, arg2);
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains("message"), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg1), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg2), "description is descriptive");
        }

        String formatStringWith3PlaceHolders = "message %s %s %s";
        String arg3 = "arg3";
        try
        {
            Throw.when(result, true, RuntimeException.class, formatStringWith3PlaceHolders, arg1, arg2, arg3);
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains("message"), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg1), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg2), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg3), "description is descriptive");
        }

        String formatStringWith4PlaceHolders = "message %s %s %s %s";
        String arg4 = "arg4";
        try
        {
            Throw.when(result, true, RuntimeException.class, formatStringWith4PlaceHolders, arg1, arg2, arg3, arg4);
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte instanceof RuntimeException, "exception is a RuntimeException");
            assertTrue(rte.getMessage().contains("message"), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg1), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg2), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg3), "description is descriptive");
            assertTrue(rte.getMessage().contains(arg4), "description is descriptive");
        }
    }
    
    /**
     * Test the Throw.whenAnyNull method.
     */
    @Test
    public void testWhenAnyNull()
    {
        try
        {
            Throw.whenAnyNull(null, "argxx");
            fail("Throw.whenAnyNull failed to trigger on null object");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof NullPointerException);
            assertTrue(e.getMessage().startsWith("argxx"));
        }
        
        try
        {
            Throw.whenAnyNull(new Object(), "argxx");
        }
        catch (Exception e)
        {
            fail("Throw.whenAnyNull triggered on non-null object");
        }

        try
        {
            Throw.whenAnyNull(null, "argxx", new Object(), "argyy");
            fail("Throw.whenAnyNull failed to trigger on null object");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof NullPointerException);
            assertTrue(e.getMessage().startsWith("argxx"));
        }
        
        try
        {
            Throw.whenAnyNull(new Object(), "argxx", "abc", "argyy");
        }
        catch (Exception e)
        {
            fail("Throw.whenAnyNull triggered on non-null object");
        }

        try
        {
            Throw.whenAnyNull(new Object(), "argxx", null, "argyy");
            fail("Throw.whenAnyNull failed to trigger on null object");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof NullPointerException);
            assertTrue(e.getMessage().startsWith("argyy"));
        }

        try
        {
            Throw.whenAnyNull(new Object(), "argxx", null, "argyy", "abc", "argzz");
            fail("Throw.whenAnyNull failed to trigger on null object");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof NullPointerException);
            assertTrue(e.getMessage().startsWith("argyy"));
        }

        try
        {
            Throw.whenAnyNull(new Object(), "argxx", "def", "argyy", null, "argzz");
            fail("Throw.whenAnyNull failed to trigger on null object");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof NullPointerException);
            assertTrue(e.getMessage().startsWith("argzz"));
        }

        try
        {
            Throw.whenAnyNull(new Object(), "argxx", "abc", "argyy", Integer.valueOf(14), "argzz");
        }
        catch (Exception e)
        {
            fail("Throw.whenAnyNull triggered on non-null object");
        }

        // Check internal errors
        
        Try.testFail(() -> Throw.whenAnyNull(null, null), IllegalArgumentException.class);
        Try.testFail(() -> Throw.whenAnyNull("abc", null), IllegalArgumentException.class);
        Try.testFail(() -> Throw.whenAnyNull(new Object(), "object", Double.valueOf("123.0")), IllegalArgumentException.class);
        Try.testFail(() -> Throw.whenAnyNull("1", "object", "2", "nr2", "3"), IllegalArgumentException.class);
        Try.testFail(() -> Throw.whenAnyNull("1", "object", "2", "nr2", "3", null), IllegalArgumentException.class);
    }

    /**
     * Test the Throw.whenNaN methods.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrowWhenNaN()
    {
        Double od = Double.valueOf(20.0);
        Float of = Float.valueOf(20.0f);
        double d = 20.0;
        float f = 20.0f;

        Double odnull = null;
        Float ofnull = null;

        Double odnan = Double.NaN;
        Float ofnan = Float.NaN;
        double dnan = Double.NaN;
        float fnan = Float.NaN;

        //
        // Throw.whenNaN(...) -> ArithmeticException with message
        //

        String message = "Throw error has occurred. Correct";
        try
        {
            Throw.whenNaN(odnan, message);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNaN(ofnan, message);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNaN(dnan, message);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNaN(fnan, message);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        //
        // Throw.whenNaN(...) -> ArithmeticException with standard message
        //

        try
        {
            Throw.whenNaN(odnan, "value");
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        try
        {
            Throw.whenNaN(ofnan, "value");
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        try
        {
            Throw.whenNaN(dnan, "value");
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        try
        {
            Throw.whenNaN(fnan, "value");
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        //
        // Throw.whenNaN(value) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(od, message);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(of, message);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(d, message);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(f, message);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        //
        // Throw.whenNaN(value) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(od, "value");
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(of, "value");
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(d, "value");
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(f, "value");
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        //
        // Throw.whenNaN(null) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(odnull, message);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(ofnull, message);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }

        //
        // Throw.whenNaN(null) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(odnull, "value");
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(ofnull, "value");
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }
    }
    
    /**
     * Test the Throw.whenNaN methods with one argument.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrowWhenNaNArg1()
    {
        Double od = Double.valueOf(20.0);
        Float of = Float.valueOf(20.0f);
        double d = 20.0;
        float f = 20.0f;

        Double odnull = null;
        Float ofnull = null;

        Double odnan = Double.NaN;
        Float ofnan = Float.NaN;
        double dnan = Double.NaN;
        float fnan = Float.NaN;
        
        String s1 = "arg1";

        //
        // Throw.whenNaN(...) -> ArithmeticException with message
        //

        String message1arg = "Throw error has occurred for %s. Correct";
        String message1 = "Throw error has occurred for arg1. Correct";
        
        try
        {
            Throw.whenNaN(odnan, message1arg, s1); 
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.whenNaN(ofnan, message1arg, s1);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.whenNaN(dnan, message1arg, s1);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.whenNaN(fnan, message1arg, s1);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        //
        // Throw.whenNaN(value) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(od, message1arg, s1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(of, message1arg, s1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(d, message1arg, s1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(f, message1arg, s1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        //
        // Throw.whenNaN(null) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(odnull, message1arg, s1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(ofnull, message1arg, s1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }
    }

    /**
     * Test the Throw.whenNaN methods with two arguments.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrowWhenNaNArg2()
    {
        Double od = Double.valueOf(20.0);
        Float of = Float.valueOf(20.0f);
        double d = 20.0;
        float f = 20.0f;

        Double odnull = null;
        Float ofnull = null;

        Double odnan = Double.NaN;
        Float ofnan = Float.NaN;
        double dnan = Double.NaN;
        float fnan = Float.NaN;
        
        String s1 = "arg1";
        Integer i1 = 12;

        //
        // Throw.whenNaN(...) -> ArithmeticException with message
        //

        String message2arg = "Throw error has occurred for %s, id=%d. Correct";
        String message2 = "Throw error has occurred for arg1, id=12. Correct";
        
        try
        {
            Throw.whenNaN(odnan, message2arg, s1, i1); 
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.whenNaN(ofnan, message2arg, s1, i1);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.whenNaN(dnan, message2arg, s1, i1);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.whenNaN(fnan, message2arg, s1, i1);
            fail("Throw.whenNaN should have resulted in an ArithmeticException");
        }
        catch (ArithmeticException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        //
        // Throw.whenNaN(value) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(od, message2arg, s1, i1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(of, message2arg, s1, i1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(d, message2arg, s1, i1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(f, message2arg, s1, i1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an ArithmeticException");
        }

        //
        // Throw.whenNaN(null) -> NO ArithmeticException
        //

        try
        {
            Throw.whenNaN(odnull, message2arg, s1, i1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }

        try
        {
            Throw.whenNaN(ofnull, message2arg, s1, i1);
        }
        catch (ArithmeticException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an ArithmeticException");
        }
    }

    /**
     * Test the Throw.whenNaN methods with another RTE.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrowRTEWhenNaN()
    {
        Double od = Double.valueOf(20.0);
        Float of = Float.valueOf(20.0f);
        double d = 20.0;
        float f = 20.0f;

        Double odnull = null;
        Float ofnull = null;

        Double odnan = Double.NaN;
        Float ofnan = Float.NaN;
        double dnan = Double.NaN;
        float fnan = Float.NaN;

        //
        // Throw.whenNaN(...) -> IllegalArgumentException with message
        //

        String message = "Throw error has occurred. Correct";
        try
        {
            Throw.whenNaN(odnan, IllegalArgumentException.class, message);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNaN(ofnan, IllegalArgumentException.class, message);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNaN(dnan, IllegalArgumentException.class, message);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        try
        {
            Throw.whenNaN(fnan, IllegalArgumentException.class, message);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message), e.getMessage() + " / " + message);
        }

        //
        // Throw.whenNaN(...) -> IllegalArgumentException with standard message
        //

        try
        {
            Throw.whenNaN(odnan, IllegalArgumentException.class, "value");
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        try
        {
            Throw.whenNaN(ofnan, IllegalArgumentException.class, "value");
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        try
        {
            Throw.whenNaN(dnan, IllegalArgumentException.class, "value");
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        try
        {
            Throw.whenNaN(fnan, IllegalArgumentException.class, "value");
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith("value may not be NaN"), e.getMessage() + " does not end with 'may not be NaN'");
        }

        //
        // Throw.whenNaN(value) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(od, IllegalArgumentException.class, message);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(of, IllegalArgumentException.class, message);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(d, IllegalArgumentException.class, message);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(f, IllegalArgumentException.class, message);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        //
        // Throw.whenNaN(value) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(od, IllegalArgumentException.class, "value");
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(of, IllegalArgumentException.class, "value");
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(d, IllegalArgumentException.class, "value");
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(f, IllegalArgumentException.class, "value");
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        //
        // Throw.whenNaN(null) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(odnull, IllegalArgumentException.class, message);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(ofnull, IllegalArgumentException.class, message);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }

        //
        // Throw.whenNaN(null) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(odnull, IllegalArgumentException.class, "value");
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(ofnull, IllegalArgumentException.class, "value");
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }
    }
    
    /**
     * Test the Throw.whenNaN methods with one argument.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrowRTEWhenNaNArg1()
    {
        Double od = Double.valueOf(20.0);
        Float of = Float.valueOf(20.0f);
        double d = 20.0;
        float f = 20.0f;

        Double odnull = null;
        Float ofnull = null;

        Double odnan = Double.NaN;
        Float ofnan = Float.NaN;
        double dnan = Double.NaN;
        float fnan = Float.NaN;
        
        String s1 = "arg1";

        //
        // Throw.whenNaN(...) -> IllegalArgumentException with message
        //

        String message1arg = "Throw error has occurred for %s. Correct";
        String message1 = "Throw error has occurred for arg1. Correct";
        
        try
        {
            Throw.whenNaN(odnan, IllegalArgumentException.class, message1arg, s1); 
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.whenNaN(ofnan, IllegalArgumentException.class, message1arg, s1);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.whenNaN(dnan, IllegalArgumentException.class, message1arg, s1);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        try
        {
            Throw.whenNaN(fnan, IllegalArgumentException.class, message1arg, s1);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message1), e.getMessage() + " / " + message1);
        }

        //
        // Throw.whenNaN(value) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(od, IllegalArgumentException.class, message1arg, s1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(of, IllegalArgumentException.class, message1arg, s1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(d, IllegalArgumentException.class, message1arg, s1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(f, IllegalArgumentException.class, message1arg, s1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        //
        // Throw.whenNaN(null) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(odnull, IllegalArgumentException.class, message1arg, s1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(ofnull, IllegalArgumentException.class, message1arg, s1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }
    }

    /**
     * Test the Throw.whenNaN methods with two arguments.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testThrowRTEWhenNaNArg2()
    {
        Double od = Double.valueOf(20.0);
        Float of = Float.valueOf(20.0f);
        double d = 20.0;
        float f = 20.0f;

        Double odnull = null;
        Float ofnull = null;

        Double odnan = Double.NaN;
        Float ofnan = Float.NaN;
        double dnan = Double.NaN;
        float fnan = Float.NaN;
        
        String s1 = "arg1";
        Integer i1 = 12;

        //
        // Throw.whenNaN(...) -> IllegalArgumentException with message
        //

        String message2arg = "Throw error has occurred for %s, id=%d. Correct";
        String message2 = "Throw error has occurred for arg1, id=12. Correct";
        
        try
        {
            Throw.whenNaN(odnan, IllegalArgumentException.class, message2arg, s1, i1); 
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.whenNaN(ofnan, IllegalArgumentException.class, message2arg, s1, i1);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.whenNaN(dnan, IllegalArgumentException.class, message2arg, s1, i1);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        try
        {
            Throw.whenNaN(fnan, IllegalArgumentException.class, message2arg, s1, i1);
            fail("Throw.whenNaN should have resulted in an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().endsWith(message2), e.getMessage() + " / " + message2);
        }

        //
        // Throw.whenNaN(value) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(od, IllegalArgumentException.class, message2arg, s1, i1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(of, IllegalArgumentException.class, message2arg, s1, i1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(d, IllegalArgumentException.class, message2arg, s1, i1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(f, IllegalArgumentException.class, message2arg, s1, i1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a normal value should NOT have resulted in an IllegalArgumentException");
        }

        //
        // Throw.whenNaN(null) -> NO IllegalArgumentException
        //

        try
        {
            Throw.whenNaN(odnull, IllegalArgumentException.class, message2arg, s1, i1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }

        try
        {
            Throw.whenNaN(ofnull, IllegalArgumentException.class, message2arg, s1, i1);
        }
        catch (IllegalArgumentException e)
        {
            fail("Throw.whenNaN of a null value should NOT have resulted in an IllegalArgumentException");
        }
    }

    /**
     * Test the Throw.throwUnchecked() method.
     */
    @Test
    public void testThrowUnchecked()
    {
        NoThrow nt = new NoThrow();
        try
        {
            nt.doSomethingWithoutException();
            fail("An exception should have been thrown by NoThrow.doSomething()");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof IOException, "The exception thrown by NoThrow.doSomething() is not an IOException");
        }
    }

    /** Class that has a method with an exception. */
    class NoThrow implements NoThrowInterface
    {
        @Override
        public void doSomethingWithoutException()
        {
            try
            {
                InputStream is = new FileInputStream("z:xyz/x/y/z.xyz");
                is.read();
                is.close();
            }
            catch (IOException exception)
            {
                Throw.throwUnchecked(exception);
            }
        }

    }

    /** Interface that has a method without an exception. */
    interface NoThrowInterface
    {
        /** A method that does not throw an exception. */
        void doSomethingWithoutException();
    }
}
