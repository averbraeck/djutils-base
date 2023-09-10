package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableSet tests the static of() methods of the ImmutableSet. <br>
 * <br>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestImmutableSet
{
    /** test the of() methods. */
    @Test
    public void testOf()
    {
        ImmutableSet<Integer> set = ImmutableSet.of();
        assertTrue(set.isEmpty());

        set = ImmutableSet.of(1);
        assertEquals(1, set.size());
        assertTrue(set.contains(Integer.valueOf(1)));

        set = ImmutableSet.of(1, 2);
        assertEquals(2, set.size());
        assertTrue(set.contains(Integer.valueOf(1)));
        assertTrue(set.contains(Integer.valueOf(2)));

        set = ImmutableSet.of(1, 2, 3);
        assertEquals(3, set.size());
        assertTrue(set.contains(Integer.valueOf(1)));
        assertTrue(set.contains(Integer.valueOf(2)));
        assertTrue(set.contains(Integer.valueOf(3)));

        set = ImmutableSet.of(1, 2, 3, 4);
        assertEquals(4, set.size());
        assertTrue(set.contains(Integer.valueOf(1)));
        assertTrue(set.contains(Integer.valueOf(2)));
        assertTrue(set.contains(Integer.valueOf(3)));
        assertTrue(set.contains(Integer.valueOf(4)));

        set = ImmutableSet.of(1, 2, 3, 4, 5);
        assertEquals(5, set.size());
        assertTrue(set.contains(Integer.valueOf(1)));
        assertTrue(set.contains(Integer.valueOf(2)));
        assertTrue(set.contains(Integer.valueOf(3)));
        assertTrue(set.contains(Integer.valueOf(4)));
        assertTrue(set.contains(Integer.valueOf(5)));

        set = ImmutableSet.of(1, 2, 3, 4, 5, 6);
        assertEquals(6, set.size());
        assertTrue(set.contains(Integer.valueOf(1)));
        assertTrue(set.contains(Integer.valueOf(2)));
        assertTrue(set.contains(Integer.valueOf(3)));
        assertTrue(set.contains(Integer.valueOf(4)));
        assertTrue(set.contains(Integer.valueOf(5)));
        assertTrue(set.contains(Integer.valueOf(6)));
    }

}
