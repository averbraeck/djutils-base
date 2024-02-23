package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * TestImmutableSortedMap tests the static of() methods of the ImmutableSortedMap. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestImmutableSortedMap
{
    /** test the of() methods. */
    @Test
    public void testOf()
    {
        ImmutableSortedMap<Integer, String> map = ImmutableSortedMap.of();
        assertTrue(map.isEmpty());

        map = ImmutableSortedMap.of(1, "a");
        assertEquals(1, map.size());
        ImmutableIterator<Integer> itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b");
        assertEquals(2, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b", 3, "c");
        assertEquals(3, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b", 3, "c", 4, "d");
        assertEquals(4, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e");
        assertEquals(5, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e", 6, "f");
        assertEquals(6, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e", 6, "f", 7, "g");
        assertEquals(7, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }

        map = ImmutableSortedMap.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e", 6, "f", 7, "g", 8, "h");
        assertEquals(8, map.size());
        itKey = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++)
        {
            assertTrue(map.containsKey(Integer.valueOf(i + 1)));
            assertTrue(map.containsValue("" + (char) ('a' + i)));
            assertEquals("" + (char) ('a' + i), map.get(i + 1));
            assertEquals(Integer.valueOf(i + 1), itKey.next());
        }
    }

}
