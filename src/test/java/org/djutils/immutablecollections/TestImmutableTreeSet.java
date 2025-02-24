package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableTreeSet.java.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class TestImmutableTreeSet
{

    /**
     * Test the tree set.
     */
    @Test
    public final void testTreeSet()
    {
        Set<Integer> intSet = new TreeSet<>(Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        NavigableSet<Integer> sortedSet = new TreeSet<Integer>(intSet);
        testIntSet(sortedSet, new ImmutableTreeSet<Integer>(sortedSet, Immutable.WRAP), Immutable.WRAP);
        sortedSet = new TreeSet<Integer>(intSet);
        testIntSet(sortedSet, new ImmutableTreeSet<Integer>(sortedSet, Immutable.COPY), Immutable.COPY);
        sortedSet = new TreeSet<Integer>(intSet);
        testIntSet(sortedSet, new ImmutableTreeSet<Integer>(sortedSet), Immutable.COPY);
        sortedSet = new TreeSet<Integer>(intSet);
        ImmutableTreeSet<Integer> ihs = new ImmutableTreeSet<Integer>(sortedSet);
        testIntSet(sortedSet, new ImmutableTreeSet<Integer>(ihs), Immutable.COPY);

        sortedSet = new TreeSet<Integer>(intSet);
        List<Integer> il = Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        testIntSet(sortedSet, new ImmutableTreeSet<Integer>(il), Immutable.COPY);
        ImmutableTreeSet<Integer> its = new ImmutableTreeSet<Integer>(sortedSet);
        assertTrue(its.toString().startsWith("ImmutableTreeSet ["), "toString returns something descriptive");

        ImmutableTreeSet<Integer> wrapped = new ImmutableTreeSet<Integer>(its, Immutable.WRAP);
        assertEquals(its, wrapped, "wrapped is equal wrapped-wrapped");
        ImmutableTreeSet<Integer> copied = new ImmutableTreeSet<Integer>(its, Immutable.COPY);
        assertEquals(its, copied, "wrapped is equal to copy-wrapped");
        assertEquals(copied, its, "copy-wrapped is equal to wrapped");
    }

    /**
     * Test a set of Integer.
     * @param set set
     * @param imSet immutable set
     * @param copyOrWrap copy data or wrap data
     */
    private void testIntSet(final NavigableSet<Integer> set, final ImmutableTreeSet<Integer> imSet, final Immutable copyOrWrap)
    {
        assertTrue(set.size() == 10);
        assertTrue(imSet.size() == 10);
        for (int i = 0; i < 10; i++)
        {
            assertTrue(imSet.contains(i + 1));
        }
        assertFalse(imSet.isEmpty());
        assertFalse(imSet.contains(15));

        assertTrue(imSet.first() == 1);
        assertTrue(imSet.last() == 10);

        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imSet.isCopy());
            assertTrue(imSet.toSet().equals(set));
            assertFalse(imSet.toSet() == set);
        }
        else
        {
            assertTrue(imSet.isWrap());
            assertTrue(imSet.toSet().equals(set));
            assertFalse(imSet.toSet() == set); // this WRAP method returns a NEW list
        }

        Set<Integer> to = imSet.toSet();
        assertTrue(set.equals(to));

        Integer[] arr = imSet.toArray(new Integer[] {});
        Integer[] sar = set.toArray(new Integer[] {});
        assertArrayEquals(arr, sar);

        // modify the underlying data structure
        set.add(11);
        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imSet.size() == 10);
        }
        else
        {
            assertTrue(imSet.size() == 11);
        }
    }

    /**
     * Test the comparator of the ImmutableTreeSet.
     */
    @Test
    public void testComparator()
    {
        Integer[] values = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Set<Integer> intSet = new TreeSet<>(Arrays.asList(values));
        NavigableSet<Integer> sortedSet = new TreeSet<Integer>(intSet);
        assertNull(sortedSet.comparator(), "Sorted set uses default compare; not an explicit comparator");
        Comparator<Integer> reverseIntegerComparator = new Comparator<Integer>()
        {
            @Override
            public int compare(final Integer o1, final Integer o2)
            {
                return -Integer.compare(o1, o2);
            }

            @Override
            public String toString()
            {
                return "Reversing comparator";
            }
        };
        sortedSet = new TreeSet<Integer>(reverseIntegerComparator);
        sortedSet.addAll(intSet);
        ImmutableTreeSet<Integer> its = new ImmutableTreeSet<>(sortedSet, Immutable.WRAP);
        assertEquals(reverseIntegerComparator, its.comparator(), "custom comparator is returned");
        // Let's check that the custom comparator actually worked
        assertEquals(values.length, its.size(), "size must match");
        Integer prev = null;
        for (Integer value : its)
        {
            // System.out.println(value);
            if (prev != null)
            {
                assertTrue(value <= prev, "Values must be in non-increasing order");
            }
            prev = value;
        }
        ImmutableSortedSet<Integer> subSet = its.subSet(7, 3);
        prev = null;
        boolean seen3 = false;
        boolean seen7 = false;
        for (Integer value : subSet)
        {
            // System.out.println(value);
            assertTrue(value <= 7 && value >= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertFalse(seen3, "3 must not have been returned");
        assertTrue(seen7, "7 must have been returned");

        subSet = its.subSet(7, false, 3, false);
        prev = null;
        seen3 = false;
        seen7 = false;
        for (Integer value : subSet)
        {
            // System.out.println(value);
            assertTrue(value <= 7 && value >= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertFalse(seen3, "3 must not have been returned");
        assertFalse(seen7, "7 must not have been returned");

        subSet = its.subSet(7, true, 3, false);
        prev = null;
        seen3 = false;
        seen7 = false;
        for (Integer value : subSet)
        {
            // System.out.println(value);
            assertTrue(value <= 7 && value >= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertFalse(seen3, "3 must not have been returned");
        assertTrue(seen7, "7 must have been returned");

        subSet = its.subSet(7, false, 3, true);
        prev = null;
        seen3 = false;
        seen7 = false;
        for (Integer value : subSet)
        {
            // System.out.println(value);
            assertTrue(value <= 7 && value >= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertTrue(seen3, "3 must have been returned");
        assertFalse(seen7, "7 must not have been returned");

        subSet = its.subSet(7, true, 3, true);
        prev = null;
        seen3 = false;
        seen7 = false;
        for (Integer value : subSet)
        {
            // System.out.println(value);
            assertTrue(value <= 7 && value >= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertTrue(seen3, "3 must have been returned");
        assertTrue(seen7, "7 must have been returned");

        ImmutableSortedSet<Integer> headSet = its.headSet(7);
        prev = null;
        seen7 = false;
        for (Integer value : headSet)
        {
            assertTrue(value >= 7, "value must be in range");
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertFalse(seen7, "7 must not have been returned");

        headSet = its.headSet(7, true);
        prev = null;
        seen7 = false;
        for (Integer value : headSet)
        {
            assertTrue(value >= 7, "value must be in range");
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertTrue(seen7, "7 must have been returned");

        headSet = its.headSet(7, false);
        prev = null;
        seen7 = false;
        for (Integer value : headSet)
        {
            assertTrue(value >= 7, "value must be in range");
            if (7 == value)
            {
                seen7 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertFalse(seen7, "7 must not have been returned");

        ImmutableSortedSet<Integer> tailSet = its.tailSet(3);
        prev = null;
        seen3 = false;
        for (Integer value : tailSet)
        {
            assertTrue(value <= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertTrue(seen3, "3 must have been returned");

        tailSet = its.tailSet(3, true);
        prev = null;
        seen3 = false;
        for (Integer value : tailSet)
        {
            assertTrue(value <= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertTrue(seen3, "3 must have been returned");

        tailSet = its.tailSet(3, false);
        prev = null;
        seen3 = false;
        for (Integer value : tailSet)
        {
            assertTrue(value <= 3, "value must be in range");
            if (3 == value)
            {
                seen3 = true;
            }
            if (prev != null)
            {
                assertTrue(value <= prev, "Values are in decreasing order");
            }
            prev = value;
        }
        assertFalse(seen3, "3 must not have been returned");

        for (int index = 0; index < values.length; index++)
        {
            Integer lower = its.lower(values[index]);
            if (index == values.length - 1)
            {
                assertNull(lower, "lower of last element should have returned null");
            }
            else
            {
                assertEquals(values[index + 1], lower, "lower should have returned next higher value");
            }
            Integer higher = its.higher(values[index]);
            if (index == 0)
            {
                assertNull(higher, "higher should have returned null");
            }
            else
            {
                assertEquals(values[index - 1], higher, "higher should have returned next lower value");
            }
            Integer floor = its.floor(values[index]);
            assertEquals(values[index], floor, "floor of element in set returns that element");
            Integer ceil = its.floor(values[index]);
            assertEquals(values[index], ceil, "ceil of element in set returns that element");
        }
        assertNull(its.floor(11), "floor of value higher than any in set returns null");
        assertNull(its.ceiling(0), "ceil of value lower than any in set returns null");
        assertEquals(1, its.floor(0), 0, "floor of value lower than any in set is lowest in set");
        assertEquals(10, its.ceiling(11), 0, "ceiling of value higher than any in set is highest in set");
        ImmutableSet<Integer> descendingSet = its.descendingSet();
        assertEquals(values.length, descendingSet.size(), "descendingSet has correct size");
        prev = null;
        for (Integer value : descendingSet)
        {
            if (null != prev)
            {
                assertTrue(value >= prev, "descendingSet has value in ascending order");
            }
            prev = value;
        }
        ImmutableIterator<Integer> ii = its.descendingIterator();
        prev = null;
        while (ii.hasNext())
        {
            Integer next = ii.next();
            if (null != prev)
            {
                assertTrue(next >= prev, "descendingSet has value in ascending order");
            }
            prev = next;
        }
        try
        {
            ii.next();
            fail("next should have thrown an Exception");
        }
        catch (NoSuchElementException nsee)
        {
            // Ignore expected exception
        }
    }

}
