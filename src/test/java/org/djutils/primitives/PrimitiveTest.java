package org.djutils.primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * The JUNIT Test for the <code>Primitive</code>.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public class PrimitiveTest
{

    /**
     * Test most of the Primitive class.
     */
    @Test
    public void test()
    {
        assertEquals(Primitive.getPrimitive(Boolean.class), boolean.class);
        assertEquals(Primitive.getPrimitive(Integer.class), int.class);
        assertEquals(Primitive.getPrimitive(Double.class), double.class);
        assertEquals(Primitive.getPrimitive(Float.class), float.class);
        assertEquals(Primitive.getPrimitive(Character.class), char.class);
        assertEquals(Primitive.getPrimitive(Byte.class), byte.class);
        assertEquals(Primitive.getPrimitive(Short.class), short.class);
        assertEquals(Primitive.getPrimitive(Long.class), long.class);
        assertNull(Primitive.getPrimitive(String.class));

        assertEquals(Primitive.getWrapper(boolean.class), Boolean.class);
        assertEquals(Primitive.getWrapper(int.class), Integer.class);
        assertEquals(Primitive.getWrapper(double.class), Double.class);
        assertEquals(Primitive.getWrapper(float.class), Float.class);
        assertEquals(Primitive.getWrapper(char.class), Character.class);
        assertEquals(Primitive.getWrapper(byte.class), Byte.class);
        assertEquals(Primitive.getWrapper(long.class), Long.class);
        assertEquals(Primitive.getWrapper(short.class), Short.class);
        try
        {
            Primitive.getWrapper(String[].class);
            fail("getWrapper for non primitive class should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }

        assertEquals(Primitive.toBoolean(Integer.valueOf(1)), Boolean.TRUE);
        assertEquals(Primitive.toBoolean(1.0), Boolean.TRUE);
        assertEquals(Primitive.toBoolean(1.0f), Boolean.TRUE);
        assertEquals(Primitive.toBoolean((short) 1.0), Boolean.TRUE);
        assertEquals(Primitive.toBoolean(1L), Boolean.TRUE);

        assertEquals(Primitive.toBoolean(Integer.valueOf(0)), Boolean.FALSE);
        assertEquals(Primitive.toBoolean(0.0), Boolean.FALSE);
        assertEquals(Primitive.toBoolean(0.0f), Boolean.FALSE);
        assertEquals(Primitive.toBoolean((short) 0.0), Boolean.FALSE);
        assertEquals(Primitive.toBoolean(0L), Boolean.FALSE);
        try
        {
            Primitive.toBoolean(Integer.valueOf(-1));
            fail("casting number not in {0, 1} to boolean should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        try
        {
            Primitive.toBoolean(Integer.valueOf(2));
            fail("casting number not in {0, 1} to boolean should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        try
        {
            Primitive.toBoolean(new String[] {"ab", "cd"});
            fail("casting unrelated object to boolean should have thrown a ClassCastException");
        }
        catch (ClassCastException cce)
        {
            // Ignore expected exception
        }
        assertEquals(Primitive.toBoolean(false), Boolean.FALSE);
        assertEquals(Primitive.toBoolean(true), Boolean.TRUE);

        assertEquals((Integer) 0x61, Primitive.toInteger('a'));
        assertEquals((Integer) 1, Primitive.toInteger(true));
        assertEquals((Integer) 0, Primitive.toInteger(false));
    }

    /**
     * Test the cast methods.
     */
    @Test
    public void testCast()
    {
        String junk = "Junk";
        assertEquals(junk, Primitive.cast(String.class, junk));
        assertEquals(true, Primitive.cast(boolean.class, 1));
        assertEquals('a', Primitive.cast(char.class, 0x61));
        Byte b = 0x77;
        assertEquals(b, Primitive.cast(byte.class, 0x77));
        assertEquals(123.0, Primitive.cast(double.class, 123L));
        assertEquals(123.0f, Primitive.cast(float.class, 123L));
        assertEquals(123456L, Primitive.cast(long.class, 123456.0));
        assertEquals(-123456, Primitive.cast(int.class, -123456.0));
        assertEquals((short) -123, Primitive.cast(short.class, -123.0));
        Object[] in = new Object[] {123, 2.0};
        Class<?>[] to = new Class[] {float.class, byte.class};
        Object[] out = Primitive.cast(to, in);
        assertEquals(123.0f, out[0]);
        assertEquals((byte) 2, out[1]);
    }

    /**
     * test the isPrimitiveAssignable method.
     */
    @Test
    public void testPrimitiveAssignable()
    {
        assertFalse(Primitive.isPrimitiveAssignableFrom(Integer.class, Number.class));
        assertTrue(Primitive.isPrimitiveAssignableFrom(Number.class, Integer.class));
        assertFalse(Primitive.isPrimitiveAssignableFrom(double.class, Number.class));
        assertTrue(Primitive.isPrimitiveAssignableFrom(Number.class, double.class));
        assertTrue(Primitive.isPrimitiveAssignableFrom(Integer.class, int.class));
        assertFalse(Primitive.isPrimitiveAssignableFrom(Double.class, Integer.class));
        assertTrue(Primitive.isPrimitiveAssignableFrom(long.class, Long.class));
        assertFalse(Primitive.isPrimitiveAssignableFrom(double.class, int.class));
        assertFalse(Primitive.isPrimitiveAssignableFrom(Double.class, int.class));
        assertFalse(Primitive.isPrimitiveAssignableFrom(double.class, Integer.class));
        assertFalse(Primitive.isPrimitiveAssignableFrom(String.class, CharSequence.class));
        assertTrue(Primitive.isPrimitiveAssignableFrom(CharSequence.class, String.class));
    }
}
