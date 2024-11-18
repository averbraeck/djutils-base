package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableLinkedHashSet.java.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class TestImmutableLinkedHashSet
{

    /**
     * ...
     */
    @Test
    public final void testLinkedHashSet()
    {
        Set<Integer> intSet = new LinkedHashSet<>(Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        Set<Integer> set = new LinkedHashSet<Integer>(intSet);
        testIntSet(set, new ImmutableLinkedHashSet<Integer>(set, Immutable.WRAP), Immutable.WRAP);
        set = new LinkedHashSet<Integer>(intSet);
        testIntSet(set, new ImmutableLinkedHashSet<Integer>(set, Immutable.COPY), Immutable.COPY);
        set = new LinkedHashSet<Integer>(intSet);
        testIntSet(set, new ImmutableLinkedHashSet<Integer>(set), Immutable.COPY);
        set = new LinkedHashSet<Integer>(intSet);
        ImmutableLinkedHashSet<Integer> ihs = new ImmutableLinkedHashSet<Integer>(set);
        testIntSet(set, new ImmutableLinkedHashSet<Integer>(ihs), Immutable.COPY);

        set = new LinkedHashSet<Integer>(intSet);
        List<Integer> il = Arrays.asList(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        testIntSet(set, new ImmutableLinkedHashSet<Integer>(il), Immutable.COPY);
        ImmutableLinkedHashSet<Integer> ilhs = new ImmutableLinkedHashSet<Integer>(set, Immutable.COPY);
        assertTrue(ilhs.toString().startsWith("ImmutableLinkedHashSet ["), "toString returns something descriptive");

        ImmutableLinkedHashSet<Integer> wilhs = new ImmutableLinkedHashSet<Integer>(ilhs, Immutable.WRAP);
        assertEquals(wilhs, ilhs, "wrapped immutable linked hash set is equal to that immutable hash set");
        ImmutableLinkedHashSet<Integer> cilhs = new ImmutableLinkedHashSet<Integer>(ilhs, Immutable.COPY);
        assertEquals(cilhs, ilhs, "copied immutable linked hash set is equal to that immutable hash set");
    }

    /**
     * ...
     * @param set a set
     * @param imSet an immutable set
     * @param copyOrWrap 
     */
    private void testIntSet(final Set<Integer> set, final ImmutableSet<Integer> imSet, final Immutable copyOrWrap)
    {
        assertTrue(set.size() == 10);
        assertTrue(imSet.size() == 10);
        for (int i = 0; i < 10; i++)
        {
            assertTrue(imSet.contains(i + 1));
        }
        assertFalse(imSet.isEmpty());
        assertFalse(imSet.contains(15));
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

}
