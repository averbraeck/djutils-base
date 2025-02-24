package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableArrayList.java.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class TestImmutableArrayList
{

    /** Test the ImmutableArrayList. */
    @Test
    public final void testArrayList()
    {
        List<Integer> intList = Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        List<Integer> list = new ArrayList<Integer>(intList);
        testIntList(list, new ImmutableArrayList<Integer>(list, Immutable.WRAP), Immutable.WRAP);
        list = new ArrayList<Integer>(intList);
        testIntList(list, new ImmutableArrayList<Integer>(list, Immutable.COPY), Immutable.COPY);
        list = new ArrayList<Integer>(intList);
        testIntList(list, new ImmutableArrayList<Integer>(list), Immutable.COPY);
        list = new ArrayList<Integer>(intList);
        ImmutableArrayList<Integer> ial = new ImmutableArrayList<Integer>(list);
        testIntList(list, new ImmutableArrayList<Integer>(ial), Immutable.COPY);

        list = new ArrayList<Integer>(intList);
        Set<Integer> intSet = new HashSet<>(Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        testIntList(list, new ImmutableArrayList<Integer>(intSet), Immutable.COPY);
    }

    /**
     * Test for the int list.
     * @param list the expected list
     * @param imList the immutable ist
     * @param copyOrWrap whether to expect COPY or WRAP
     */
    private void testIntList(final List<Integer> list, final ImmutableList<Integer> imList, final Immutable copyOrWrap)
    {
        assertTrue(list.size() == 10);
        assertTrue(imList.size() == 10);
        for (int i = 0; i < 10; i++)
        {
            assertTrue(imList.get(i) == list.get(i));
        }
        assertFalse(imList.isEmpty());
        assertTrue(imList.contains(5));
        assertFalse(imList.contains(15));
        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imList.isCopy());
            assertTrue(imList.toList().equals(list));
            assertFalse(imList.toList() == list);
        }
        else
        {
            assertTrue(imList.isWrap());
            assertTrue(imList.toList().equals(list));
            assertFalse(imList.toList() == list); // this WRAP method returns a NEW list
        }

        List<Integer> to = imList.toList();
        assertTrue(list.equals(to));

        Integer[] arr = imList.toArray(new Integer[] {});
        Integer[] sar = list.toArray(new Integer[] {});
        assertArrayEquals(arr, sar);

        // modify the underlying data structure
        list.add(11);
        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imList.size() == 10);
        }
        else
        {
            assertTrue(imList.size() == 11);
        }
    }

}
