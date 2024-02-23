package org.djutils.multikeymap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

/**
 * Test the MultiKeyMap class.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestMultiKeyMap
{

    /**
     * Test the MultiKeyMap class.
     */
    @Test
    public void testMultiKeyMap()
    {
        try
        {
            new MultiKeyMap<>();
            fail("empty key list should have thrown an exception");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }

        MultiKeyMap<String> mkm = new MultiKeyMap<>(String.class);
        try
        {
            mkm.get(123.456);
            fail("key of wrong type should have thrown an exception");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        assertNull(mkm.get("abc"), "non existent key return null");
        mkm.put("value1", "key1");
        assertEquals("value1", mkm.get("key1"), "existing key return value for that  key");
        mkm.put("value2", "key2");
        assertEquals("value1", mkm.get("key1"), "existing key return value for that  key");
        assertEquals("value2", mkm.get("key2"), "existing key return value for that  key");
        mkm.clear("value1");
        assertNull(mkm.get("value1"), "no longer existent key return null");
        assertEquals("value2", mkm.get("key2"), "existing key return value for that  key");
        String result = mkm.get(new Supplier<String>()
        {
            @Override
            public String get()
            {
                return "newValue3";
            }
        }, "key3");
        assertEquals("newValue3", result, "result is new value");
        assertEquals("newValue3", mkm.get("key3"), "existing key return value for that  key");
        String oldValue = mkm.put("newValue3", "key3");
        assertEquals("newValue3", mkm.get("key3"), "existing key returns new value for that  key");
        assertEquals("newValue3", oldValue, "put has returned old value");
        result = mkm.get(new Supplier<String>()
        {
            @Override
            public String get()
            {
                fail("get method in Supplier should not have been called");
                return "newNewalue3";
            }
        }, "key3");
        assertEquals("newValue3", result, "result is unchanged");

        try
        {
            mkm.get("k", "l", "m");
            fail("Wrong number of keys should have thrown an exeption");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }

        try
        {
            mkm.get();
            fail("Wrong number of keys should have thrown an exeption");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }

        mkm = new MultiKeyMap<>(String.class, Double.class);
        result = mkm.get("k", 123.456);
        assertNull(result, "result should be null");
        mkm.put("dummy", "1", 123.456);
        assertEquals("dummy", mkm.get("1", 123.456), "two step key works");
        assertNull(mkm.get("1", 123.457), "two step key works");
        try
        {
            mkm.get("1", "2");
            fail("Wrong type of last key should have thrown an exception");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }

        Set<Object> keySet = mkm.getKeys();
        assertEquals(1, keySet.size(), "there is one key at level 0");
        assertEquals(String.class, keySet.iterator().next().getClass(), "type of key is String");
        assertEquals("1", keySet.iterator().next(), "object is string with value \"1\"");
        keySet = mkm.getKeys("1");
        assertEquals(1, keySet.size(), "there is one key at level 1");
        assertEquals(Double.class, keySet.iterator().next().getClass(), "type of key is Double");
        assertEquals(123.456, keySet.iterator().next(), "object is Double with value 123.456");
        try
        {
            mkm.getKeys("1", 123.456, "3");
            fail("too many keys should have thrown an exception");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        assertNull(mkm.clear("2", 123.4), "Clearing non-existant sub map returns null");
        Object o = mkm.clear("1", 123.456);
        assertEquals("dummy", o, "result of clear is removed object");
        assertNull(mkm.get("1", 123.456), "dummy is no longer in the map");
        mkm.put("dummy", "1", 123.456);
        assertEquals("dummy", mkm.get("1", 123.456), "dummy is back");
        mkm.put("dummy2", "2", 23.456);
        MultiKeyMap<String> subMap = mkm.getSubMap();
        assertEquals(subMap, mkm, "Top level sub map ");
        subMap = mkm.getSubMap("1");
        assertEquals("dummy", subMap.get(123.456), "level one sub map contains dummy");
        try
        {
            mkm.getSubMap("1", 123.456);
            fail("Too many arguments should have thrown an exception");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        mkm.clear("1");
        assertNull(mkm.get("1", 123.456), "dummy was removed");
        assertEquals("dummy2", mkm.get("2", 23.456), "dummy2 is still there");
        mkm.clear();
        assertEquals(0, mkm.getKeys().size(), "result of clear at top level clears the entire map");

        assertTrue(mkm.toString().startsWith("MultiKeyMap ["), "toString returns something descriptive");
    }
}
