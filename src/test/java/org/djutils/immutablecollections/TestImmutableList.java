package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

/**
 * Test immutable list.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class TestImmutableList
{
    /** Accumulator for forEach test. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    int sum;

    /**
     * Test the immutable list class.
     */
    @SuppressWarnings({"unchecked", "unlikely-arg-type"})
    @Test
    public void testImmutableList()
    {
        Integer[] values = new Integer[] {2, 5, 1, 2, 4, 9};
        ImmutableList<Integer> il = new ImmutableArrayList<>(Arrays.asList(values));
        assertTrue(il.toString().startsWith("ImmutableArrayList ["), "toString returns something descriptive");
        assertTrue(il.isCopy(), "default is to copy");
        assertFalse(il.isWrap(), "default is not to wrap");
        ImmutableList<Integer> il2 = new ImmutableArrayList<>(Arrays.asList(values), Immutable.COPY);
        assertTrue(il2.isCopy(), "COPY means copy");
        il2 = new ImmutableArrayList<>(Arrays.asList(values), Immutable.WRAP);
        assertTrue(il2.isWrap(), "COPY means copy");
        il2 = new ImmutableArrayList<Integer>((ImmutableAbstractCollection<Integer>) il);
        assertTrue(il2.isCopy(), "default is to copy");
        assertEquals(il.size(), il2.size(), "has same size");
        il2 = new ImmutableArrayList<Integer>((ImmutableAbstractList<Integer>) il);
        assertTrue(il2.isCopy(), "default is to copy");
        assertEquals(il.size(), il2.size(), "has same size");
        il2 = new ImmutableArrayList<Integer>((ImmutableAbstractList<Integer>) il, Immutable.COPY);
        assertTrue(il2.isCopy(), "COPY means copy");
        assertEquals(il.size(), il2.size(), "has same size");
        il2 = new ImmutableArrayList<Integer>((ImmutableAbstractList<Integer>) il, Immutable.WRAP);
        assertTrue(il2.isWrap(), "WRAP means wrap");
        assertFalse(il2.isCopy(), "WRAP is not copy");
        assertTrue(il2.isWrap(), "Wrap is wrap");
        assertEquals(il.size(), il2.size(), "has same size");
        il2 = il.subList(2, 4);
        assertEquals(2, il2.size(), "sublist has length 2");
        for (int index = 0; index < il2.size(); index++)
        {
            assertEquals(values[index + 2], il2.get(index), "sub list element matches");
        }
        assertEquals(0, il.indexOf(Integer.valueOf(2)), "position of first 2");
        assertEquals(4, il.indexOf(Integer.valueOf(4)), "position of first (and only) 4");
        assertEquals(3, il.lastIndexOf(Integer.valueOf(2)), "position of last 2");
        assertTrue(il.contains(Integer.valueOf(1)), "contains 1");
        assertFalse(il.contains(Integer.valueOf(123)), "does not contain 123");
        Object[] outObject = il.toArray();
        assertEquals(values.length, outObject.length, "length of toArray matches size of what went in");
        for (int index = 0; index < outObject.length; index++)
        {
            assertEquals(values[index], outObject[index], "objects in out match what went in");
        }
        Integer[] outInteger = il.toArray(new Integer[0]);
        assertEquals(values.length, outInteger.length, "length of toArray matches size of what went in");
        for (int index = 0; index < outInteger.length; index++)
        {
            assertEquals(values[index], outInteger[index], "objects in out match what went in");
        }
        for (int index = 0; index < values.length; index++)
        {
            assertEquals(values[index], il.get(index), "values can be retrieved one by one");
        }
        ImmutableIterator<Integer> ii = il.iterator();
        assertTrue(ii.toString().startsWith("ImmutableIterator ["),
                "toString method of iterator returns something descriptive");
        for (int index = 0; index < values.length; index++)
        {
            assertTrue(ii.hasNext());
            Integer got = ii.next();
            assertEquals(values[index], got, "iterator returned next value");
        }
        assertFalse(ii.hasNext(), "iterator has run out");
        this.sum = 0;
        il.forEach(new Consumer<Integer>()
        {

            @Override
            public void accept(final Integer t)
            {
                TestImmutableList.this.sum += t;
            }
        });
        // compute the result the old fashioned way
        int expectedSum = 0;
        for (int index = 0; index < values.length; index++)
        {
            expectedSum += values[index];
        }
        assertEquals(expectedSum, this.sum, "sum matches");
        assertTrue(il.containsAll(Arrays.asList(values)), "contains all");
        assertFalse(il.containsAll(Arrays.asList(new Integer[] {1, 2, 3})), "not contains all");
        assertTrue(il.containsAll(new ImmutableArrayList<Integer>(Arrays.asList(values))), "contains all");
        assertFalse(il.containsAll(new ImmutableArrayList<Integer>(Arrays.asList(new Integer[] {1, 2, 3}))),
                "not contains all");
        outObject = il.stream().toArray();
        assertEquals(values.length, outObject.length, "length of toArray matches size of what went in");
        for (int index = 0; index < outObject.length; index++)
        {
            assertEquals(values[index], outObject[index], "objects in out match what went in");
        }
        assertTrue(il.toString().startsWith("ImmutableArrayList ["), "toString returns something descriptive");
        assertEquals(values.length, il.size(), "size returns correct value");
        assertFalse(il.isEmpty(), "list is not empty");
        assertTrue(new ImmutableArrayList<Integer>(Arrays.asList(new Integer[] {})).isEmpty(), "emty list reports it is empty");
        assertTrue(il.equals(il), "equal to itself");
        assertFalse(il.equals(null), "not equal to null");
        assertFalse(il.equals("abc"), "not equal to some string");
        assertFalse(il.equals(il2), "not equal to a (shorter) sub list of itself");
        il2 = new ImmutableArrayList<Integer>(Arrays.asList(values));
        assertTrue(il.equals(il2), "equal to another one that has the exact same contents");
        assertEquals(il.hashCode(), il2.hashCode(), "hashcodes should match");

        // Testing the spliterator and parallelstream will have to wait until I understand how to write a unit test for that
    }

    /**
     * Test the ImmutableHashMap class.
     */
    @SuppressWarnings({"unchecked", "unlikely-arg-type"})
    @Test
    public void testImmutableHashSet()
    {
        Integer[] values = new Integer[] {2, 5, 1, 12, 4, 9}; // all different
        ImmutableSet<Integer> is = new ImmutableHashSet<>(Arrays.asList(values));
        assertTrue(is.isCopy(), "default is to copy");
        assertFalse(is.isWrap(), "default is not to wrap");
        ImmutableSet<Integer> is2 = new ImmutableHashSet<>(new HashSet<Integer>(Arrays.asList(values)), Immutable.COPY);
        assertTrue(is2.isCopy(), "COPY means copy");
        is2 = new ImmutableHashSet<>(new HashSet<Integer>(Arrays.asList(values)), Immutable.WRAP);
        assertTrue(is2.isWrap(), "COPY means copy");
        is2 = new ImmutableHashSet<Integer>((ImmutableAbstractCollection<Integer>) is);
        assertTrue(is2.isCopy(), "default is to copy");
        assertEquals(is.size(), is2.size(), "has same size");
        is2 = new ImmutableHashSet<Integer>((ImmutableAbstractSet<Integer>) is);
        assertTrue(is2.isCopy(), "default is to copy");
        assertEquals(is.size(), is2.size(), "has same size");
        is2 = new ImmutableHashSet<Integer>((ImmutableAbstractSet<Integer>) is, Immutable.COPY);
        assertTrue(is2.isCopy(), "COPY means copy");
        assertEquals(is.size(), is2.size(), "has same size");
        is2 = new ImmutableHashSet<Integer>((ImmutableAbstractSet<Integer>) is, Immutable.WRAP);
        assertTrue(is2.isWrap(), "WRAP means wrap");
        assertEquals(is.size(), is2.size(), "has same size");
        assertTrue(is.contains(Integer.valueOf(1)), "contains 1");
        assertFalse(is.contains(Integer.valueOf(123)), "does not contain 123");
        Object[] outObject = is.toArray();
        assertEquals(values.length, outObject.length, "length of toArray matches size of what went in");
        Set<Integer> verify = new HashSet<>(Arrays.asList(values));
        for (int index = 0; index < outObject.length; index++)
        {
            assertTrue(verify.remove(outObject[index]), "Each object matches an object that went in");
        }
        assertTrue(verify.isEmpty(), "All objects were matched");
        Integer[] outInteger = is.toArray(new Integer[0]);
        assertEquals(values.length, outInteger.length, "length of toArray matches size of what went in");
        verify = new HashSet<>(Arrays.asList(values));
        for (int index = 0; index < outInteger.length; index++)
        {
            assertTrue(verify.remove(outInteger[index]), "Each object matches an object that went in");
        }
        assertTrue(verify.isEmpty(), "All objects were matched");
        verify = new HashSet<>(Arrays.asList(values));
        ImmutableIterator<Integer> ii = is.iterator();
        for (int index = 0; index < values.length; index++)
        {
            assertTrue(ii.hasNext());
            Integer got = ii.next();
            assertTrue(verify.remove(got), "Each object matches an object that went in");
        }
        assertFalse(ii.hasNext(), "iterator has run out");
        assertTrue(verify.isEmpty(), "All objects were matched");
        this.sum = 0;
        is.forEach(new Consumer<Integer>()
        {

            @Override
            public void accept(final Integer t)
            {
                TestImmutableList.this.sum += t;
            }
        });
        // compute the result the old fashioned way
        int expectedSum = 0;
        for (int index = 0; index < values.length; index++)
        {
            expectedSum += values[index];
        }
        assertEquals(expectedSum, this.sum, "sum matches");
        assertTrue(is.containsAll(Arrays.asList(values)), "contains all");
        assertFalse(is.containsAll(Arrays.asList(new Integer[] {1, 2, 3})), "not contains all");
        assertTrue(is.containsAll(new ImmutableArrayList<Integer>(Arrays.asList(values))), "contains all");
        assertFalse(is.containsAll(new ImmutableArrayList<Integer>(Arrays.asList(new Integer[] {1, 2, 3}))),
                "not contains all");
        outObject = is.stream().toArray();
        assertEquals(values.length, outObject.length, "length of toArray matches size of what went in");
        verify = new HashSet<>(Arrays.asList(values));
        for (int index = 0; index < outObject.length; index++)
        {
            assertTrue(verify.remove(outObject[index]), "Each object matches an object that went in");
        }
        assertTrue(verify.isEmpty(), "All objects were matched");
        assertTrue(is.toString().startsWith("ImmutableHashSet ["), "toString returns something descriptive");
        assertEquals(values.length, is.size(), "size returns correct value");
        assertFalse(is.isEmpty(), "list is not empty");
        assertTrue(new ImmutableArrayList<Integer>(Arrays.asList(new Integer[] {})).isEmpty(), "emty list reports it is empty");
        assertTrue(is.equals(is), "equal to itself");
        assertFalse(is.equals(null), "not equal to null");
        assertFalse(is.equals("abc"), "not equal to some string");
        is2 = new ImmutableHashSet<Integer>(Arrays.asList(Arrays.copyOfRange(values, 2, 4)));
        assertFalse(is.equals(is2), "not equal to a (smaller) sub set of itself");
        is2 = new ImmutableHashSet<Integer>(new HashSet<>(Arrays.asList(values)));
        assertTrue(is.equals(is2), "equal to another one that has the exact same contents");
        assertEquals(is.hashCode(), is2.hashCode(), "hashcodes should match");
        Collection<Integer> collection = is.toCollection();
        assertEquals(is.size(), collection.size(), "to collection result has correct number of values");
        verify = new HashSet<>(Arrays.asList(values));
        Iterator<Integer> i = collection.iterator();
        while (i.hasNext())
        {
            assertTrue(verify.remove(i.next()), "Each object matches an object that went in");
        }
        assertTrue(verify.isEmpty(), "All objects were matched");

        // Testing the spliterator and parallelstream will have to wait until I understand how to write a unit test for that
    }

    /** test the of() methods. */
    @Test
    public void testOf()
    {
        ImmutableList<Integer> list = ImmutableList.of();
        assertTrue(list.isEmpty());

        list = ImmutableList.of(1);
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());

        list = ImmutableList.of(1, 2);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());

        list = ImmutableList.of(1, 2, 3);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(3, list.get(2).intValue());

        list = ImmutableList.of(1, 2, 3, 4);
        assertEquals(4, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(3, list.get(2).intValue());
        assertEquals(4, list.get(3).intValue());

        list = ImmutableList.of(1, 2, 3, 4, 5);
        assertEquals(5, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(3, list.get(2).intValue());
        assertEquals(4, list.get(3).intValue());
        assertEquals(5, list.get(4).intValue());

        list = ImmutableList.of(1, 2, 3, 4, 5, 6);
        assertEquals(6, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(3, list.get(2).intValue());
        assertEquals(4, list.get(3).intValue());
        assertEquals(5, list.get(4).intValue());
        assertEquals(6, list.get(5).intValue());
    }
}
