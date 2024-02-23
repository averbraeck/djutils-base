package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Feb 26, 2019 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class TestImmutableCollectionStatics
{

    /**
     * Test the empty constructors.
     */
    @Test
    public void testEmptyConstructors()
    {
        assertEquals(0, ImmutableCollections.emptyImmutableSet().size(), "empty immutable set is empty");
        assertEquals(0, ImmutableCollections.emptyImmutableList().size(), "empty immutable list is empty");
        assertEquals(0, ImmutableCollections.emptyImmutableMap().size(), "empty immutable map is empty");
    }

    /**
     * Test the various methods that return an index.
     */
    @Test
    public void testSearchers()
    {
        final Integer[] values = new Integer[] {10, 2, -5, 2, 2, 6};
        ImmutableList<Integer> il = new ImmutableArrayList<>(new ArrayList<Integer>(Arrays.asList(values)), Immutable.WRAP);
        assertEquals(Integer.valueOf(10), ImmutableCollections.max(il), "max");
        assertEquals(Integer.valueOf(-5), ImmutableCollections.min(il), "min");
        Comparator<Integer> indexComparator = new Comparator<Integer>()
        {
            // This crazy comparator compares position of the given values in the array values
            @Override
            public int compare(final Integer o1, final Integer o2)
            {
                return Arrays.binarySearch(values, o1) - Arrays.binarySearch(values, o2);
            }
        };
        assertEquals(Integer.valueOf(6), ImmutableCollections.max(il, indexComparator), "custom comparator max");
        assertEquals(Integer.valueOf(10), ImmutableCollections.min(il, indexComparator), "custom comparator min");
        assertEquals(1, ImmutableCollections.frequency(il, Integer.valueOf(10)), "number of 10s");
        assertEquals(0, ImmutableCollections.frequency(il, Integer.valueOf(100)), "number of 100s");
        assertEquals(3, ImmutableCollections.frequency(il, Integer.valueOf(2)), "number of 2s");
        Integer[] subList = new Integer[] {2, 2};
        ImmutableList<Integer> isl = new ImmutableArrayList<Integer>(new ArrayList<>(Arrays.asList(subList)));
        assertEquals(3, ImmutableCollections.indexOfSubList(il, isl), "position of sub list");
        assertEquals(-1, ImmutableCollections.indexOfSubList(isl, il), "position of non-sub list");
        assertEquals(3, ImmutableCollections.lastIndexOfSubList(il, isl), "last position of sub list");
        assertEquals(-1, ImmutableCollections.lastIndexOfSubList(isl, il), "last position of non-sub list");
        List<Integer> msl = new ArrayList<>(Arrays.asList(subList));
        assertEquals(3, ImmutableCollections.indexOfSubList(il, msl), "position of sub list");
        assertEquals(-1, ImmutableCollections.indexOfSubList(msl, il), "position of non-sub list");
        assertEquals(3, ImmutableCollections.lastIndexOfSubList(il, msl), "last position of sub list");
        assertEquals(-1, ImmutableCollections.lastIndexOfSubList(msl, il), "last position of non-sub list");

        Arrays.sort(values); // this modifies the contents of our array
        il = new ImmutableArrayList<>(new ArrayList<Integer>(Arrays.asList(values)));
        assertEquals(4, ImmutableCollections.binarySearch(il, Integer.valueOf(6)), "position of 6");
        assertEquals(-5, ImmutableCollections.binarySearch(il, Integer.valueOf(5)),
                "position where 5 would be if it were present");
        final Integer[] uniqueValues = new Integer[] {10, 2, -5, 6};
        ImmutableList<Integer> il2 = new ImmutableArrayList<>(new ArrayList<Integer>(Arrays.asList(uniqueValues)));
        indexComparator = new Comparator<Integer>()
        {
            // This crazy comparator compares position of the given values in the array values
            @Override
            public int compare(final Integer o1, final Integer o2)
            {
                return Arrays.binarySearch(uniqueValues, o1) - Arrays.binarySearch(uniqueValues, o2);
            }
        };
        assertEquals(1, ImmutableCollections.binarySearch(il2, Integer.valueOf(2), indexComparator),
                "position of 2 binary search with crazy comparator");
        assertEquals(0, ImmutableCollections.binarySearch(il2, Integer.valueOf(10), indexComparator),
                "position of 10 binary search with crazy comparator");
        assertEquals(3, ImmutableCollections.binarySearch(il2, Integer.valueOf(6), indexComparator),
                "position of 6 binary search with crazy comparator");
        assertFalse(ImmutableCollections.disjoint(il, il2), "The collections are not disjoint");
        ImmutableList<Integer> il3 = new ImmutableArrayList<>(new ArrayList<Integer>(Arrays.asList(new Integer[] {99, 999})));
        assertTrue(ImmutableCollections.disjoint(il, il3), "The collections are disjoint");
        List<Integer> mutableList = new ArrayList<>(Arrays.asList(uniqueValues));
        assertFalse(ImmutableCollections.disjoint(il, mutableList), "The collections are not disjoint");
        assertFalse(ImmutableCollections.disjoint(mutableList, il), "The collections are not disjoint");
        mutableList = new ArrayList<Integer>(Arrays.asList(new Integer[] {99, 999}));
        assertTrue(ImmutableCollections.disjoint(il, mutableList), "The collections are disjoint");
        assertTrue(ImmutableCollections.disjoint(mutableList, il), "The collections are disjoint");

    }
}
