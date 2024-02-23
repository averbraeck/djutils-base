package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableSortedSet tests the static of() methods of the ImmutableSortedSet. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestImmutableSortedSet
{
    /** test the of() methods. */
    @Test
    public void testOf()
    {
        ImmutableSortedSet<Integer> set = ImmutableSortedSet.of();
        assertTrue(set.isEmpty());

        set = ImmutableSortedSet.of(1);
        assertEquals(1, set.size());
        Iterator<Integer> it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());

        set = ImmutableSortedSet.of(1, 2);
        assertEquals(2, set.size());
        it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());

        set = ImmutableSortedSet.of(1, 2, 3);
        assertEquals(3, set.size());
        it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(3), it.next());

        set = ImmutableSortedSet.of(1, 2, 3, 4);
        assertEquals(4, set.size());
        it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(4), it.next());

        set = ImmutableSortedSet.of(1, 2, 3, 4, 5);
        assertEquals(5, set.size());
        it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(5), it.next());

        set = ImmutableSortedSet.of(1, 2, 3, 4, 5, 6);
        assertEquals(6, set.size());
        it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(5), it.next());
        assertEquals(Integer.valueOf(6), it.next());
    }

}
