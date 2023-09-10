package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.Map.Entry;

import org.djutils.immutablecollections.ImmutableMap.ImmutableEntry;
import org.junit.jupiter.api.Test;

/**
 * Test the ImmutableEntry sub class.
 * <p>
 * Copyright (c) 2013-2023 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Jan 20, 2020 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class TestImmutableEntry
{

    /**
     * Test the ImmutableEntry sub class.
     */
    @Test
    public void testImmutableEntry()
    {
        MyEntry<String, String> entry = new MyEntry<>("Key", "Value");
        ImmutableEntry<String, String> ie = new ImmutableEntry<>(entry);
        assertEquals(entry.getKey(), ie.getKey(), "key must be retrievable");
        assertEquals(entry.getValue(), ie.getValue(), "value must be retrievable");
        assertEquals(ie, ie, "ie is equal to itself");
        Comparator<ImmutableEntry<String, String>> keyComparator = ImmutableEntry.comparingByKey();
        Comparator<ImmutableEntry<String, String>> valueComparator = ImmutableEntry.comparingByValue();
        Comparator<String> reverseComparator = new Comparator<String>()
        {
            @Override
            public int compare(final String o1, final String o2)
            {
                return o2.compareTo(o1); // swapped arguments
            }
        };
        Comparator<ImmutableEntry<String, String>> ownKeyComparator = ImmutableEntry.comparingByKey(reverseComparator);
        Comparator<ImmutableEntry<String, String>> ownValueComparator = ImmutableEntry.comparingByValue(reverseComparator);
        assertEquals(0, keyComparator.compare(ie, ie), "keyComparator returns 0 when comparing ie to itself");
        assertEquals(0, valueComparator.compare(ie, ie), "valueComparator returns 0 when comparing ie to itself");
        assertEquals(0, ownKeyComparator.compare(ie, ie), "ownKeyComparator returns 0 when comparing ie to itself");
        assertEquals(0, ownValueComparator.compare(ie, ie), "ownValueComparator returns 0 when comparing ie to itself");
        ImmutableEntry<String, String> ie2 = new ImmutableEntry<>(entry);
        assertEquals(ie.hashCode(), ie2.hashCode(), "ie has same hashCode as ie2 (which wraps the same MyEntry)");
        assertEquals(ie, ie2, "ie is equal to another ie embedding the same entry");
        assertNotEquals(ie, null, "ie is not equal to null");
        assertNotEquals(ie, "Hello", "ie is not equal to some unrelated object");
        ie2 = new ImmutableEntry<>(null);
        assertNotEquals(ie, ie2, "ie is not equal to ie embedding null");
        assertNotEquals(ie2, ie, "ie embedding null is not equal to ie embedding non-null");
        MyEntry<String, String> entry2 = new MyEntry<>("Key", "DifferentValue");
        ie2 = new ImmutableEntry<>(entry2);
        assertNotEquals(ie, ie2, "ie is not equal to other ie embedding same key but different value");
        assertEquals(0, keyComparator.compare(ie, ie2),
                "comparator returns 0 when comparing ie to other that has same key but different value");
        entry2 = new MyEntry<>("Key2", "Value2");
        ie2 = new ImmutableEntry<>(entry2);
        System.out.println(ie + " " + ie2 + " " + keyComparator.compare(ie, ie2));
        assertTrue(keyComparator.compare(ie, ie2) < 0,
                "keyComparator returns < 0 when comparing objects that are in natural order");
        assertTrue(keyComparator.compare(ie2, ie) > 0,
                "keyComparator returns > 0 when comparing objects that are in reverse natural order");
        assertTrue(ownKeyComparator.compare(ie, ie2) > 0,
                "ownKeyComparator returns > 0 when comparing objects that are in natural order");
        assertTrue(ownKeyComparator.compare(ie2, ie) < 0,
                "ownKeyComparator returns < 0 when comparing objects that are in reverse natural order");
        assertTrue(valueComparator.compare(ie, ie2) < 0,
                "valueComparator returns < 0 when comparing objects that are in natural order");
        assertTrue(valueComparator.compare(ie2, ie) > 0,
                "valueComparator returns > 0 when comparing objects that are in reverse natural order");
        assertTrue(ownValueComparator.compare(ie, ie2) > 0,
                "ownValueComparator returns > 0 when comparing objects that are in natural order");
        assertTrue(ownValueComparator.compare(ie2, ie) < 0,
                "ownValueComparator returns > 0 when comparing objects that are in reverse natural order");
        ie = new ImmutableEntry<>(null);
        ie2 = new ImmutableEntry<>(null);
        assertEquals(ie, ie2, "ie embedding null is equal to another that also embeds null");
    }

    /**
     * Simple implementation of Entry interface.
     * @param <K> type of the key
     * @param <V> type of the value
     */
    public static class MyEntry<K, V> implements Entry<K, V>
    {
        /** The key. */
        private final K key;

        /** The value. */
        private V value;

        /**
         * Construct a new MyEntry object.
         * @param key K; key of the entry
         * @param value V; value of the entry
         */
        MyEntry(final K key, final V value)
        {
            this.key = key;
            this.value = value;
        }

        /** {@inheritDoc} */
        @Override
        public final K getKey()
        {
            return this.key;
        }

        /** {@inheritDoc} */
        @Override
        public final V getValue()
        {
            return this.value;
        }

        /** {@inheritDoc} */
        @Override
        public final V setValue(final V newValue)
        {
            this.value = newValue;
            return this.value;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "MyEntry [key=" + this.key + ", value=" + this.value + "]";
        }

    }

}
