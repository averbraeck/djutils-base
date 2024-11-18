package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableVector.java.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class TestImmutableVector
{

    /**
     * Test vectors.
     */
    @Test
    public final void testVector()
    {
        Integer[] testData = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Vector<Integer> intVector = new Vector<>(Arrays.asList(testData));
        Vector<Integer> vector = new Vector<Integer>(intVector);
        testIntVector(vector, new ImmutableVector<Integer>(vector, Immutable.WRAP), Immutable.WRAP);
        vector = new Vector<Integer>(intVector);
        testIntVector(vector, new ImmutableVector<Integer>(vector, Immutable.COPY), Immutable.COPY);
        vector = new Vector<Integer>(intVector);
        testIntVector(vector, new ImmutableVector<Integer>(vector), Immutable.COPY);
        vector = new Vector<Integer>(intVector);
        ImmutableVector<Integer> ial = new ImmutableVector<Integer>(vector);
        testIntVector(vector, new ImmutableVector<Integer>(ial), Immutable.COPY);

        // Verify that the ImmutableIterator throws an exception when the remove method is called.
        ImmutableIterator<Integer> ii = ial.iterator();
        ii.next();
        try
        {
            ii.remove();
            fail("remove method of ImmutableIterator should have thrown an exception");
        }
        catch (UnsupportedOperationException e)
        {
            // Ignore expected exception
        }

        vector = new Vector<Integer>(intVector);
        Set<Integer> intSet = new HashSet<>(Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        testIntVector(vector, new ImmutableVector<Integer>(intSet), Immutable.COPY);
        ImmutableVector<Integer> iv = new ImmutableVector<Integer>(vector);
        assertTrue(iv.toString().startsWith("ImmutableVector ["), "toString returns something descriptive");
        ImmutableVector<Integer> iv2 = new ImmutableVector<>(iv, Immutable.COPY);
        assertEquals(iv, iv2, "ImmutableVector with copy of other ImmutableVector tests equal to it");
        assertEquals(iv.hashCode(), iv2.hashCode(), "ImmutableVector with copy of other ImmutableVector has same hash code");
        iv2 = new ImmutableVector<>(iv, Immutable.WRAP);
        assertEquals(iv, iv2, "ImmutableVector wrapping other ImmutableVector tests equal to it");
        assertEquals(iv.hashCode(), iv2.hashCode(), "ImmutableVector wrapping other ImmutableVector has same hash code");
        // start anew as the testIntVector method modifies the underlying data.
        intVector = new Vector<>(Arrays.asList(testData));
        iv = new ImmutableVector<Integer>(intVector);
        ImmutableList<Integer> subList = iv.subList(2, 5);
        assertEquals(3, subList.size(), "size of sub list is 3");
        for (int index = 0; index < subList.size(); index++)
        {
            assertEquals(iv.get(index + 2), subList.get(index), "value at index matches");
        }
        try
        {
            iv.subList(-1, 3);
            fail("Negative from index should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException ioobe)
        {
            // Ignore expected exception
        }
        try
        {
            iv.subList(1, iv.size() + 1);
            fail("To index bigger than size should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException ioobe)
        {
            // Ignore expected exception
        }
        try
        {
            iv.subList(5, 4);
            fail("negative range should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        subList = iv.subList(4, 4);
        assertEquals(0, subList.size(), "sub list should be empty");
        Integer[] justRight = new Integer[iv.size()];
        iv.copyInto(justRight);
        for (int index = 0; index < iv.size(); index++)
        {
            assertEquals(iv.get(index), justRight[index], "contents of array matches");
        }
        Integer[] bigger = new Integer[iv.size() + 3];
        bigger[bigger.length - 2] = -1;
        iv.copyInto(bigger);
        for (int index = 0; index < iv.size(); index++)
        {
            assertEquals(iv.get(index), justRight[index], "contents of array matches");
        }
        assertEquals(null, bigger[iv.size()], "element after required length is still null");
        assertEquals(-1, bigger[bigger.length - 2], 0, "element at length - 2 is still -1");
        assertEquals(null, bigger[bigger.length - 1], "element at length - 1 is null");

        Integer[] tooShort = new Integer[iv.size() - 1];
        try
        {
            iv.copyInto(tooShort);
            fail("Too short target array should have thrown an IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException ioobe)
        {
            // Ignore expected exception
        }
        assertTrue(iv.capacity() >= testData.length, "capacity returns capacity of the underlying collection");

        Enumeration<Integer> e = iv.elements();
        for (int index = 0; index < testData.length; index++)
        {
            assertTrue(e.hasMoreElements(), "There is another element to be had");
            Integer got = e.nextElement();
            assertEquals(testData[index], got, "element at index matches");
        }
        assertFalse(e.hasMoreElements(), "there are no more elements to be had");
        for (int index = 0; index < testData.length; index++)
        {
            int indexOf = iv.indexOf(testData[index]);
            assertEquals(index, indexOf, "index matches");
            assertEquals(testData[index], iv.get(indexOf), "value at index matches");
            indexOf = iv.lastIndexOf(testData[index]);
            assertEquals(index, indexOf, "index matches");
            int noIndex = iv.indexOf(testData[index], indexOf + 1);
            assertEquals(-1, noIndex, "there is no later next index for this value");
            noIndex = iv.lastIndexOf(testData[index], indexOf - 1);
            assertEquals(-1, noIndex, "there is no earlier next index for this value");
            assertEquals(iv.get(index), iv.elementAt(index), "get returns same as elementAt");
        }
        assertEquals(testData[0], iv.firstElement(), "firstElement returns first element");
        assertEquals(testData[testData.length - 1], iv.lastElement(), "lastElement returns last element");
    }

    /**
     * ...
     * @param vector a vector of Integer
     * @param imVector an immutable vector of Integer
     * @param copyOrWrap Immutable
     */
    private void testIntVector(final Vector<Integer> vector, final ImmutableVector<Integer> imVector,
            final Immutable copyOrWrap)
    {
        assertTrue(vector.size() == 10);
        assertTrue(imVector.size() == 10);
        for (int i = 0; i < 10; i++)
        {
            assertTrue(imVector.get(i) == vector.get(i));
        }
        assertFalse(imVector.isEmpty());
        assertTrue(imVector.contains(5));
        assertFalse(imVector.contains(15));
        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imVector.isCopy());
            assertTrue(imVector.toList().equals(vector));
            assertFalse(imVector.toList() == vector);
        }
        else
        {
            assertTrue(imVector.isWrap());
            assertTrue(imVector.toList().equals(vector));
            assertFalse(imVector.toList() == vector); // this WRAP method returns a NEW list
        }

        Vector<Integer> to = imVector.toVector();
        assertTrue(vector.equals(to));

        Integer[] arr = imVector.toArray(new Integer[] {});
        Integer[] sar = vector.toArray(new Integer[] {});
        assertArrayEquals(arr, sar);

        // modify the underlying data structure
        vector.add(11);
        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imVector.size() == 10);
        }
        else
        {
            assertTrue(imVector.size() == 11);
        }
    }
}
