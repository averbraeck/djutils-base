package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.djutils.immutablecollections.ImmutableMap.ImmutableEntry;
import org.junit.jupiter.api.Test;

/**
 * TestImmutableTreeMap.java.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class TestImmutableTreeMap
{

    /**
     * Test the ImmutableTreeMap.
     */
    @SuppressWarnings({"unlikely-arg-type"})
    @Test
    public final void testTreeMap()
    {
        NavigableMap<Integer, Integer> isMap = new TreeMap<>();
        for (int i = 1; i <= 10; i++)
        {
            isMap.put(i, 100 * i);
        }
        NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>(isMap);
        testIntMap(map, new ImmutableTreeMap<Integer, Integer>(map, Immutable.WRAP), Immutable.WRAP);
        map = new TreeMap<Integer, Integer>(isMap);
        testIntMap(map, new ImmutableTreeMap<Integer, Integer>(map, Immutable.COPY), Immutable.COPY);
        map = new TreeMap<Integer, Integer>(isMap);
        testIntMap(map, new ImmutableTreeMap<Integer, Integer>(map), Immutable.COPY);
        map = new TreeMap<Integer, Integer>(isMap);
        ImmutableTreeMap<Integer, Integer> ihs = new ImmutableTreeMap<Integer, Integer>(map);
        testIntMap(map, new ImmutableTreeMap<Integer, Integer>(ihs), Immutable.COPY);

        ImmutableTreeMap<Integer, Integer> itm = new ImmutableTreeMap<>(isMap, Immutable.WRAP);
        ImmutableTreeMap<Integer, Integer> itmw = new ImmutableTreeMap<>(itm, Immutable.WRAP);
        assertEquals(itm, itmw, "wrapper is equal to wrapped");
        itmw = new ImmutableTreeMap<>(itm, Immutable.COPY);
        assertEquals(itm, itmw, "copied is equal to wrapped");
        assertEquals(itmw, itm, "wrapped is equal to copied");
        assertTrue(itm.toString().startsWith("ImmutableTreeMap ["), "toString returns something descriptive");
        ImmutableTreeMap<Integer, Integer> subMap = (ImmutableTreeMap<Integer, Integer>) itm.subMap(3, 5);
        assertEquals(2, subMap.size(), "size is 2");
        assertEquals(3, subMap.firstKey(), 0, "first key is 3");
        assertEquals(4, subMap.lastKey(), 0, "last key is 4");
        assertNull(subMap.get(2), "sub map has no value for key 2");
        assertNull(subMap.get(5), "sub map has no value for key 5");
        assertEquals(300, subMap.get(3), 0, "value for key 3 is 300");
        assertEquals(400, subMap.get(4), 0, "value for key 4 is 400");
        subMap = (ImmutableTreeMap<Integer, Integer>) itm.subMap(2, true, 6, true);
        assertEquals(2, subMap.firstKey(), 0, "first key is 2");
        assertEquals(6, subMap.lastKey(), 0, "last key is 6");
        ImmutableTreeMap<Integer, Integer> headMap = (ImmutableTreeMap<Integer, Integer>) itm.headMap(5);
        assertEquals(4, headMap.size(), "headMap has 4 entries");
        assertEquals(1, headMap.firstKey(), 0, "first key is 1");
        assertEquals(4, headMap.lastKey(), 0, "last key is 4");
        headMap = (ImmutableTreeMap<Integer, Integer>) itm.headMap(5, true);
        assertEquals(5, headMap.size(), "headMap has 5 entries");
        assertEquals(1, headMap.firstKey(), 0, "first key is 1");
        assertEquals(5, headMap.lastKey(), 0, "last key is 5");
        ImmutableTreeMap<Integer, Integer> tailMap = (ImmutableTreeMap<Integer, Integer>) itm.tailMap(5);
        assertEquals(6, tailMap.size(), "tailMap has 6 entries");
        assertEquals(5, tailMap.firstKey(), 0, "first key is 5");
        tailMap = (ImmutableTreeMap<Integer, Integer>) itm.tailMap(5, false);
        assertEquals(5, tailMap.size(), "tailMap has 5 entries");
        assertEquals(6, tailMap.firstKey(), 0, "first key is 6");
        assertNull(itm.lowerKey(1), "there is no lower key than 1");
        assertEquals(4, itm.lowerKey(5), 0, "highest key lower than 5 is 4");
        assertEquals(10, itm.lowerKey(999), 0, "highest key lower than 999 is 10");
        assertEquals(5, itm.floorKey(5), 0, "highest key lower than, or equal to 5 is s");
        assertNull(itm.floorKey(0), "highest key lower than, or equal to 0 does not exist");
        assertEquals(3, itm.ceilingKey(3), 0, "lowest key equal or bigger than 3 is 3");
        assertNull(itm.ceilingKey(11), "lowest key equal or bigger than 11 does not exist");
        assertEquals(1, itm.higherKey(-10), 0, "lowest key bigger than -10 is 1");
        assertEquals(6, itm.higherKey(5), 0, "lowest key bigger than 5 is 6");
        assertNull(itm.higherKey(10), "lowest key bigger than 10 does not exist");
        ImmutableTreeMap<Integer, Integer> descending = (ImmutableTreeMap<Integer, Integer>) itm.descendingMap();
        assertEquals(itm.size(), descending.size(), "descending map has same size");
        ImmutableSet<ImmutableEntry<Integer, Integer>> entrySet = itm.entrySet();
        Iterator<ImmutableEntry<Integer, Integer>> iterator = entrySet.iterator();
        ImmutableEntry<Integer, Integer> sampleEntry = iterator.next();
        assertEquals(sampleEntry, sampleEntry, "ImmutableEntry is equal to itself");
        assertFalse(sampleEntry.equals(null), "entry is not equal to null");
        assertFalse(sampleEntry.equals("ABC"), "entry is not equal to some other object");
        ImmutableEntry<Integer, Integer> differentEntry = iterator.next();
        assertFalse(sampleEntry.equals(differentEntry), "entry is not equal to the next entry");
        ImmutableEntry<Integer, Integer> copy = new ImmutableEntry<Integer, Integer>(isMap.firstEntry());
        assertEquals(sampleEntry, copy, "wrapped entry is equal to self made entry containing same entry");
        ImmutableEntry<Integer, Integer> containsNull = new ImmutableEntry<Integer, Integer>(null);
        assertFalse(sampleEntry.equals(containsNull), "wrapped entry is not equal to self made entry contaning null");
        assertFalse(containsNull.equals(sampleEntry), "Self made entry containing null is not equal to sampleEntry");
        ImmutableEntry<Integer, Integer> otherContainsNull = new ImmutableEntry<Integer, Integer>(null);
        assertEquals(containsNull, otherContainsNull, "entry containing null is equal to another entry containing null");

    }

    /**
     * test a map from int to int.
     * @param map map
     * @param imMap immutable map
     * @param copyOrWrap copy data or wrap data
     */
    private void testIntMap(final NavigableMap<Integer, Integer> map, final ImmutableTreeMap<Integer, Integer> imMap,
            final Immutable copyOrWrap)
    {
        assertTrue(map.size() == 10);
        assertTrue(imMap.size() == 10);
        for (int i = 0; i < 10; i++)
        {
            assertTrue(imMap.containsKey(i + 1));
        }
        for (int i = 0; i < 10; i++)
        {
            assertTrue(imMap.containsValue(100 * (i + 1)));
        }
        assertFalse(imMap.isEmpty());
        assertFalse(imMap.containsKey(15));
        assertFalse(imMap.containsValue(1500));

        assertTrue(imMap.keySet().size() == 10);
        assertTrue(imMap.values().size() == 10);
        assertTrue(imMap.keySet().first() == 1);
        assertTrue(imMap.keySet().last() == 10);
        assertTrue(imMap.values().contains(200));

        assertArrayEquals(map.keySet().toArray(), imMap.keySet().toSet().toArray());
        assertArrayEquals(map.values().toArray(), imMap.values().toSet().toArray());
        assertArrayEquals(map.keySet().toArray(), imMap.keySet().toSet().toArray()); // cached
        assertArrayEquals(map.values().toArray(), imMap.values().toSet().toArray());

        assertTrue(checkEntrySets(map.entrySet(), imMap.entrySet().toSet()));
        assertTrue(checkEntrySets(map.entrySet(), imMap.entrySet().toSet())); // cached

        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imMap.isCopy());
            assertTrue(imMap.toMap().equals(map));
            assertFalse(imMap.toMap() == map);
        }
        else
        {
            assertTrue(imMap.isWrap());
            assertTrue(imMap.toMap().equals(map));
            assertFalse(imMap.toMap() == map); // this WRAP method returns a NEW list
        }

        Map<Integer, Integer> to = imMap.toMap();
        assertTrue(map.equals(to));

        // modify the underlying data structure
        map.put(11, 1100);
        if (copyOrWrap == Immutable.COPY)
        {
            assertTrue(imMap.size() == 10);
        }
        else
        {
            assertTrue(imMap.size() == 11);
        }
    }

    /**
     * Determine if two entry sets contain the same keys and corresponding values.
     * @param es entry set
     * @param ies immutable entry set
     * @return true if the sets contain the same keys and corresponding values
     */
    private boolean checkEntrySets(final Set<Entry<Integer, Integer>> es, final Set<ImmutableEntry<Integer, Integer>> ies)
    {
        if (es.size() != ies.size())
        {
            return false;
        }
        Iterator<Entry<Integer, Integer>> entryIt = es.iterator();
        Iterator<ImmutableEntry<Integer, Integer>> immEntryIt = ies.iterator();
        while (entryIt.hasNext())
        {
            Entry<Integer, Integer> e1 = entryIt.next();
            ImmutableEntry<Integer, Integer> e2 = immEntryIt.next();
            if (!e1.getKey().equals(e2.getKey()) || !e1.getValue().equals(e2.getValue()))
            {
                return false;
            }
        }
        return true;
    }
}
