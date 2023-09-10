package org.djutils.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * MutablePrimitiveTest tests the mutable primitive classes such as the MutableInt and the MutableDouble.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutablePrimitiveTest
{
    /**
     * Test the MutableInt.
     */
    @Test
    public void testMutableInt()
    {
        MutableInt m = new MutableInt(0);
        assertEquals(0, m.get());
        m.inc();
        assertEquals(1, m.get());
        m.inc(10);
        assertEquals(11, m.get());
        m.dec();
        assertEquals(10, m.get());
        m.dec(15);
        assertEquals(-5, m.get());
        m.set(25);
        assertEquals(25, m.get());
        m.mul(2);
        assertEquals(50, m.get());
        m.div(2);
        assertEquals(25, m.get());
        m.div(2);
        assertEquals(12, m.get());

        assertEquals(m, m);
        MutableInt m2 = new MutableInt(12);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set(10);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableInt"));
        assertTrue(m2.toString().contains("10"));
    }

    /**
     * Test the MutableLong.
     */
    @Test
    public void testMutableLong()
    {
        MutableLong m = new MutableLong(0);
        assertEquals(0, m.get());
        m.inc();
        assertEquals(1, m.get());
        m.inc(10);
        assertEquals(11, m.get());
        m.dec();
        assertEquals(10, m.get());
        m.dec(15);
        assertEquals(-5, m.get());
        m.set(25);
        assertEquals(25, m.get());
        m.mul(2);
        assertEquals(50, m.get());
        m.div(2);
        assertEquals(25, m.get());
        m.div(2);
        assertEquals(12, m.get());

        assertEquals(m, m);
        MutableLong m2 = new MutableLong(12);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set(10);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableLong"));
        assertTrue(m2.toString().contains("10"));
    }

    /**
     * Test the MutableDouble.
     */
    @Test
    public void testMutableDouble()
    {
        MutableDouble m = new MutableDouble(0.0);
        assertEquals(0.0, m.get(), 1E-6);
        m.inc();
        assertEquals(1.0, m.get(), 1E-6);
        m.inc(10.0);
        assertEquals(11.0, m.get(), 1E-6);
        m.dec();
        assertEquals(10.0, m.get(), 1E-6);
        m.dec(15.0);
        assertEquals(-5.0, m.get(), 1E-6);
        m.set(25.0);
        assertEquals(25.0, m.get(), 1E-6);
        m.mul(2.0);
        assertEquals(50.0, m.get(), 1E-6);
        m.div(2.0);
        assertEquals(25.0, m.get(), 1E-6);
        m.div(2.0);
        assertEquals(12.5, m.get(), 1E-6);

        assertEquals(m, m);
        MutableDouble m2 = new MutableDouble(12.5);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set(10.0);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableDouble"));
        assertTrue(m2.toString().contains("10.0"));

        assertTrue(Double.isFinite(m.get()));
        m.set(Double.NaN);
        assertTrue(Double.isNaN(m.get()));
        m.set(Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(m.get()));
        m.set(Double.NEGATIVE_INFINITY);
        assertTrue(Double.isInfinite(m.get()));
    }

    /**
     * Test the MutableFloat.
     */
    @Test
    public void testMutableFloat()
    {
        MutableFloat m = new MutableFloat(0.0f);
        assertEquals(0.0f, m.get(), 1E-6);
        m.inc();
        assertEquals(1.0f, m.get(), 1E-6);
        m.inc(10.0f);
        assertEquals(11.0f, m.get(), 1E-6);
        m.dec();
        assertEquals(10.0f, m.get(), 1E-6);
        m.dec(15.0f);
        assertEquals(-5.0f, m.get(), 1E-6);
        m.set(25.0f);
        assertEquals(25.0f, m.get(), 1E-6);
        m.mul(2.0f);
        assertEquals(50.0f, m.get(), 1E-6);
        m.div(2.0f);
        assertEquals(25.0f, m.get(), 1E-6);
        m.div(2.0f);
        assertEquals(12.5f, m.get(), 1E-6);

        assertEquals(m, m);
        MutableFloat m2 = new MutableFloat(12.5f);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set(10.0f);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableFloat"));
        assertTrue(m2.toString().contains("10.0"));

        assertTrue(Float.isFinite(m.get()));
        m.set(Float.NaN);
        assertTrue(Float.isNaN(m.get()));
        m.set(Float.POSITIVE_INFINITY);
        assertTrue(Float.isInfinite(m.get()));
        m.set(Float.NEGATIVE_INFINITY);
        assertTrue(Float.isInfinite(m.get()));
    }

    /**
     * Test the MutableShort.
     */
    @Test
    public void testMutableShort()
    {
        MutableShort m = new MutableShort((short) 0);
        assertEquals((short) 0, m.get());
        m.inc();
        assertEquals((short) 1, m.get());
        m.inc((short) 10);
        assertEquals((short) 11, m.get());
        m.dec();
        assertEquals((short) 10, m.get());
        m.dec((short) 15);
        assertEquals((short) -5, m.get());
        m.set((short) 25);
        assertEquals((short) 25, m.get());
        m.mul((short) 2);
        assertEquals((short) 50, m.get());
        m.div((short) 2);
        assertEquals((short) 25, m.get());
        m.div((short) 2);
        assertEquals((short) 12, m.get());

        assertEquals(m, m);
        MutableShort m2 = new MutableShort((short) 12);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set((short) 10);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableShort"));
        assertTrue(m2.toString().contains("10"));
    }

    /**
     * Test the MutableByte.
     */
    @Test
    public void testMutableByte()
    {
        MutableByte m = new MutableByte((byte) 0);
        assertEquals((byte) 0, m.get());
        m.inc();
        assertEquals((byte) 1, m.get());
        m.inc((byte) 10);
        assertEquals((byte) 11, m.get());
        m.dec();
        assertEquals((byte) 10, m.get());
        m.dec((byte) 15);
        assertEquals((byte) -5, m.get());
        m.set((byte) 25);
        assertEquals((byte) 25, m.get());
        m.mul((byte) 2);
        assertEquals((byte) 50, m.get());
        m.div((byte) 2);
        assertEquals((byte) 25, m.get());
        m.div((byte) 2);
        assertEquals((byte) 12, m.get());

        assertEquals(m, m);
        MutableByte m2 = new MutableByte((byte) 12);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set((byte) 10);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableByte"));
        assertTrue(m2.toString().contains("10"));
    }

    /**
     * Test the MutableBoolean.
     */
    @Test
    public void testMutableBoolean()
    {
        MutableBoolean m = new MutableBoolean(true);
        assertEquals(true, m.get());
        m.flip();
        assertEquals(false, m.get());
        m.flip();
        assertEquals(true, m.get());
        m.set(false);
        assertEquals(false, m.get());

        assertEquals(m, m);
        MutableBoolean m2 = new MutableBoolean(false);
        assertEquals(m, m2);
        assertEquals(m.hashCode(), m2.hashCode());
        m2.set(true);
        assertNotEquals(m, m2);
        assertNotEquals(m.hashCode(), m2.hashCode());
        assertNotEquals(m2, null);
        assertNotEquals(m2, "abc");
        assertTrue(m2.toString().contains("MutableBoolean"));
        assertTrue(m2.toString().contains("true"));
    }

}
