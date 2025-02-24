package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import org.djutils.immutablecollections.ImmutableMap.ImmutableEntry;
import org.junit.jupiter.api.Test;

/**
 * TestImmutableHashMap.java.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public class TestImmutableHashMap
{

    /**
     * Test most of the equals and hashCode methods and the forEach method of the ImmutableAbstractMap class.
     */
    @SuppressWarnings({"unlikely-arg-type"})
    @Test
    public final void testMapEqualsAndHashCode()
    {
        Integer[] keys = new Integer[] {10, 20, 30, 40};
        Map<Integer, Double> mutableMap1 = new HashMap<>();
        Map<Integer, Double> mutableMap2 = new HashMap<>();
        for (Integer key : keys)
        {
            mutableMap1.put(key, Math.PI + key);
            mutableMap2.put(key, Math.PI + key);
        }
        assertEquals(mutableMap1, mutableMap2, "maps with same content should be equal");
        assertEquals(mutableMap1.hashCode(), mutableMap2.hashCode(), "maps with same content should have same hash code");
        // No see that the same logic holds for our immutable maps
        ImmutableMap<Integer, Double> im1 = new ImmutableHashMap<>(mutableMap1, Immutable.WRAP);
        assertFalse(im1.isCopy());
        ImmutableMap<Integer, Double> im2 = new ImmutableHashMap<>(mutableMap2, Immutable.WRAP);
        assertEquals(im1, im2, "immutable maps with same content should be equal");
        assertEquals(im1.hashCode(), im2.hashCode(), "immutable maps with same content should have same hash code");
        im2 = new ImmutableHashMap<>(mutableMap2, Immutable.COPY);
        assertEquals(im1, im2, "immutable maps with same content should be equal");
        assertEquals(im2, im1, "immutable maps with same content should be equal");
        im1 = new ImmutableHashMap<>(mutableMap1, Immutable.COPY);
        assertTrue(im1.isCopy());
        assertEquals(im1, im2, "immutable maps with same content should be equal");
        assertEquals(im2, im1, "immutable maps with same content should be equal");
        // test the short cut path in equals
        assertEquals(im1, im1, "immutable map is equal to itself");
        assertFalse(im1.equals(null), "immutable map is not equal to null");
        assertFalse(im1.equals("abc"), "immutable map is not equal to some totally different object");
        mutableMap2.put(keys[0], Math.E);
        assertFalse(mutableMap1.equals(mutableMap2), "altered mutable map differs");
        assertEquals(im1, im2, "immutable map holding copy is not altered");
        ImmutableMap<Integer, Double> im1Wrap = new ImmutableHashMap<>(mutableMap1, Immutable.WRAP);
        assertEquals(im1, im1Wrap, "another immutable map from the same collection is equal");
        assertEquals(im1.hashCode(), im1Wrap.hashCode(), "another immutable map from the same collection has same hash code");
        mutableMap1.put(keys[0], -Math.PI);
        assertFalse(im1.equals(im1Wrap), "wrapped immutable map re-checks content");
        assertFalse(im1Wrap.equals(im1), "wrapped immutable map re-checks content");
        assertFalse(im1.hashCode() == im1Wrap.hashCode(), "wrapped immutable map re-computes hash code");
        assertFalse(im1Wrap.hashCode() == im1.hashCode(), "wrapped immutable map re-computes hash code");
        // Test the get method
        assertNull(im1.get(-123), "result of get for non-existent key returns null");
        for (Integer key : keys)
        {
            assertEquals(mutableMap1.get(key), im1Wrap.get(key), "Immutable map returns same as underlying mutable map");
        }
        ImmutableMap<Integer, Double> map3 =
                new ImmutableHashMap<>((ImmutableAbstractMap<Integer, Double>) im1Wrap, Immutable.WRAP);
        assertEquals(im1Wrap, map3, "immutable map constructed by wrapping another immutable map is equals");
        map3 = new ImmutableHashMap<>((ImmutableAbstractMap<Integer, Double>) im1Wrap, Immutable.COPY);
        assertEquals(im1Wrap, map3, "immutable map constructed by copyinig another immutable map is equals");
        assertTrue(map3.toString().startsWith("ImmutableHashMap ["), "toString returns something descriptive");
        assertEquals(mutableMap1.get(keys[0]), map3.getOrDefault(keys[0], Math.asin(2.0)),
                "get with default returns value for key when it exists");
        assertEquals(Math.asin(2.0), map3.getOrDefault(-123, Math.asin(2.0)),
                0.00001, "get with default returns default for key when it does not exist");
        final ImmutableMap<Integer, Double> map4 =
                new ImmutableHashMap<Integer, Double>((ImmutableAbstractMap<Integer, Double>) im1Wrap, Immutable.WRAP);
        boolean[] tested = new boolean[keys.length];
        map3.forEach(new BiConsumer<Integer, Double>()
        {
            @Override
            public void accept(final Integer t, final Double u)
            {
                assertEquals(u, map4.get(t), 0.0001, "accept got a value that matches the key");
                int index = -1;
                for (int i = 0; i < keys.length; i++)
                {
                    if (keys[i] == t)
                    {
                        index = i;
                    }
                }
                assertTrue(index >= 0, "key is contained in keys");
                assertFalse(tested[index], "key has not appeared before");
                tested[index] = true;
            }
        });
        for (int index = 0; index < tested.length; index++)
        {
            assertTrue(tested[index], "each index got tested");
        }
    }

    /**
     * ...
     */
    @Test
    public final void testHashMap()
    {
        Map<Integer, Integer> isMap = new HashMap<>();
        for (int i = 1; i <= 10; i++)
        {
            isMap.put(i, 100 * i);
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(isMap);
        testIntMap(map, new ImmutableHashMap<Integer, Integer>(map, Immutable.WRAP), Immutable.WRAP);
        map = new HashMap<Integer, Integer>(isMap);
        testIntMap(map, new ImmutableHashMap<Integer, Integer>(map, Immutable.COPY), Immutable.COPY);
        map = new HashMap<Integer, Integer>(isMap);
        testIntMap(map, new ImmutableHashMap<Integer, Integer>(map), Immutable.COPY);
        map = new HashMap<Integer, Integer>(isMap);
        ImmutableHashMap<Integer, Integer> ihs = new ImmutableHashMap<Integer, Integer>(map);
        testIntMap(map, new ImmutableHashMap<Integer, Integer>(ihs), Immutable.COPY);
    }

    /**
     * ...
     * @param map map from int to int
     * @param imMap immutable map from int to int
     * @param copyOrWrap whether the map within the immutable map is copied or wrapped
     */
    private void testIntMap(final Map<Integer, Integer> map, final ImmutableMap<Integer, Integer> imMap,
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

        assertTrue(sameContent(map.keySet(), imMap.keySet().toSet()));
        assertTrue(sameContent(map.values(), imMap.values().toCollection()));
        assertTrue(sameContent(map.keySet(), imMap.keySet().toSet())); // cached
        assertTrue(sameContent(map.values(), imMap.values().toCollection()));

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
     * Check that two collection contain the same objects.
     * @param a the first collection
     * @param b the second collection
     * @return true if collection contain the same objects
     */
    private boolean sameContent(final Collection<?> a, final Collection<?> b)
    {
        return a.containsAll(b) && b.containsAll(a); // Oops: second half was b.containsAll(b)
    }

    /**
     * Check that two Sets of Entries contain the same entries.
     * @param es Set&lt;Entry&ltInteger, Integer&gt;&gt;; a set of Entries
     * @param ies Set&lt;Entry&ltInteger, Integer&gt;&gt;; an immutable set of Entries
     * @return true if the two sets contain the same entries
     */
    private boolean checkEntrySets(final Set<Entry<Integer, Integer>> es, final Set<ImmutableEntry<Integer, Integer>> ies)
    {
        if (es.size() != ies.size())
        {
            return false;
        }
        for (Entry<Integer, Integer> entry : es)
        {
            boolean found = false;
            for (ImmutableEntry<Integer, Integer> immEntry : ies)
            {
                if (entry.getKey().equals(immEntry.getKey()) && entry.getValue().equals(immEntry.getValue()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                return false;
            }
        }
        return true;
    }

}
