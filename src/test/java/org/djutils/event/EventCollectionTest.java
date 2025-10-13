package org.djutils.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.djutils.event.collection.EventProducingCollection;
import org.djutils.event.collection.EventProducingIterator;
import org.djutils.event.collection.EventProducingList;
import org.djutils.event.collection.EventProducingListIterator;
import org.djutils.event.collection.EventProducingMap;
import org.djutils.event.collection.EventProducingSet;
import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

/**
 * Test the EventProducingCollection, EventProducingList, EventProducingMap, EventProducingSet and EventIterator.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EventCollectionTest
{
    /**
     * Test the EventProducingCollection.
     */
    @Test
    public void testEventProducingCollection()
    {
        EventProducingCollection<String> epc = new EventProducingCollection<>(new LinkedHashSet<>());
        TestEventListener listener = new TestEventListener();
        epc.addListener(listener, EventProducingCollection.OBJECT_ADDED_EVENT);
        epc.addListener(listener, EventProducingCollection.OBJECT_REMOVED_EVENT);
        epc.addListener(listener, EventProducingCollection.OBJECT_CHANGED_EVENT);

        // test add
        listener.setExpectingNotification(true);
        assertTrue(epc.isEmpty());
        boolean ok = epc.add("abc");
        assertTrue(ok);
        assertEquals(EventProducingCollection.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());
        assertFalse(epc.isEmpty());
        ok = epc.add("abc");
        assertFalse(ok);
        assertEquals(EventProducingCollection.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());

        // test remove
        ok = epc.remove("abc");
        assertTrue(ok);
        assertEquals(EventProducingCollection.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        assertTrue(epc.isEmpty());
        listener.setExpectingNotification(false);
        ok = epc.remove("def");
        assertFalse(ok);
        listener.setExpectingNotification(true);

        // test addAll, size
        ok = epc.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(ok);
        assertEquals(EventProducingCollection.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, epc.size());
        ok = epc.addAll(Arrays.asList("b", "e"));
        assertFalse(ok);
        assertEquals(EventProducingCollection.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        ok = epc.addAll(Arrays.asList());
        assertFalse(ok);
        listener.setExpectingNotification(true);

        // test removeAll
        epc.removeAll(Arrays.asList("b", "c"));
        assertEquals(EventProducingCollection.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(3), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epc.removeAll(Arrays.asList());
        listener.setExpectingNotification(true);

        // test retainAll
        epc.retainAll(Arrays.asList("c", "d", "e"));
        assertEquals(EventProducingCollection.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(2), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epc.retainAll(Arrays.asList("d", "e"));
        listener.setExpectingNotification(true);

        // test contains, containsAll
        assertTrue(epc.contains("d"));
        assertFalse(epc.contains("a"));
        assertTrue(epc.containsAll(Arrays.asList("d", "e")));

        // test toArray
        Object[] arr = epc.toArray();
        String[] stringArr = epc.toArray(new String[] {});
        assertEquals(2, arr.length);
        assertTrue(arr[0].equals("d") || arr[0].equals("e"));
        assertTrue(arr[1].equals("d") || arr[1].equals("e"));
        assertNotEquals(arr[0], arr[1]);
        assertEquals(2, stringArr.length);
        assertTrue(stringArr[0].equals("d") || stringArr[0].equals("e"));
        assertTrue(stringArr[1].equals("d") || stringArr[1].equals("e"));
        assertNotEquals(stringArr[0], stringArr[1]);

        // test clear
        epc.clear();
        assertEquals(EventProducingCollection.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epc.clear();
        listener.setExpectingNotification(true);

        // test iterator
        ok = epc.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(ok);
        assertEquals(EventProducingCollection.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, epc.size());
        EventProducingIterator<String> eit = epc.iterator();
        assertNotNull(eit);
        assertTrue(eit.hasNext());
        String firstString = eit.next();
        assertTrue(eit.hasNext());
        String secondString = eit.next();
        eit.remove();
        assertEquals(EventProducingCollection.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(4), listener.getReceivedEvent().getContent());
        assertEquals(4, epc.size());
        assertTrue(epc.contains(firstString));
        assertFalse(epc.contains(secondString));

        // clear the collection and remove the listeners
        epc.removeAllListeners();
        epc.clear();
    }

    /**
     * Test the EventProducingSet.
     */
    @Test
    public void testEventProducingSet()
    {
        EventProducingSet<String> eps = new EventProducingSet<>(new LinkedHashSet<>());
        TestEventListener listener = new TestEventListener();
        eps.addListener(listener, EventProducingSet.OBJECT_ADDED_EVENT);
        eps.addListener(listener, EventProducingSet.OBJECT_REMOVED_EVENT);
        eps.addListener(listener, EventProducingSet.OBJECT_CHANGED_EVENT);

        // test add
        listener.setExpectingNotification(true);
        assertTrue(eps.isEmpty());
        boolean ok = eps.add("abc");
        assertTrue(ok);
        assertEquals(EventProducingSet.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());
        assertFalse(eps.isEmpty());
        ok = eps.add("abc");
        assertFalse(ok);
        assertEquals(EventProducingSet.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());

        // test remove
        ok = eps.remove("abc");
        assertTrue(ok);
        assertEquals(EventProducingSet.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        assertTrue(eps.isEmpty());
        listener.setExpectingNotification(false);
        ok = eps.remove("def");
        assertFalse(ok);
        listener.setExpectingNotification(true);

        // test addAll, size
        ok = eps.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(ok);
        assertEquals(EventProducingSet.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, eps.size());
        ok = eps.addAll(Arrays.asList("b", "e"));
        assertFalse(ok);
        assertEquals(EventProducingSet.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        ok = eps.addAll(Arrays.asList());
        assertFalse(ok);
        listener.setExpectingNotification(true);

        // test removeAll
        eps.removeAll(Arrays.asList("b", "c"));
        assertEquals(EventProducingSet.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(3), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        eps.removeAll(Arrays.asList());
        listener.setExpectingNotification(true);

        // test retainAll
        eps.retainAll(Arrays.asList("c", "d", "e"));
        assertEquals(EventProducingSet.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(2), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        eps.retainAll(Arrays.asList("d", "e"));
        listener.setExpectingNotification(true);

        // test contains, containsAll
        assertTrue(eps.contains("d"));
        assertFalse(eps.contains("a"));
        assertTrue(eps.containsAll(Arrays.asList("d", "e")));

        // test toArray
        Object[] arr = eps.toArray();
        String[] stringArr = eps.toArray(new String[] {});
        assertEquals(2, arr.length);
        assertTrue(arr[0].equals("d") || arr[0].equals("e"));
        assertTrue(arr[1].equals("d") || arr[1].equals("e"));
        assertNotEquals(arr[0], arr[1]);
        assertEquals(2, stringArr.length);
        assertTrue(stringArr[0].equals("d") || stringArr[0].equals("e"));
        assertTrue(stringArr[1].equals("d") || stringArr[1].equals("e"));
        assertNotEquals(stringArr[0], stringArr[1]);

        // test clear
        eps.clear();
        assertEquals(EventProducingSet.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        eps.clear();
        listener.setExpectingNotification(true);

        // test iterator
        ok = eps.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(ok);
        assertEquals(EventProducingSet.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, eps.size());
        EventProducingIterator<String> eit = eps.iterator();
        assertNotNull(eit);
        assertTrue(eit.hasNext());
        String firstString = eit.next();
        assertTrue(eit.hasNext());
        String secondString = eit.next();
        eit.remove();
        assertEquals(EventProducingSet.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(4), listener.getReceivedEvent().getContent());
        assertEquals(4, eps.size());
        assertTrue(eps.contains(firstString));
        assertFalse(eps.contains(secondString));

        // clear the collection and remove the listeners
        eps.removeAllListeners();
        eps.clear();
    }

    /**
     * Test the EventProducingList.
     */
    @Test
    public void testEventProducingList()
    {
        EventProducingList<String> epl = new EventProducingList<>(new ArrayList<>());
        TestEventListener listener = new TestEventListener();
        epl.addListener(listener, EventProducingList.OBJECT_ADDED_EVENT);
        epl.addListener(listener, EventProducingList.OBJECT_REMOVED_EVENT);
        epl.addListener(listener, EventProducingList.OBJECT_CHANGED_EVENT);

        // test add, remove(int)
        listener.setExpectingNotification(true);
        assertTrue(epl.isEmpty());
        assertEquals(0, epl.size());
        boolean ok = epl.add("abc");
        assertTrue(ok);
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());
        assertFalse(epl.isEmpty());
        ok = epl.add("abc");
        assertTrue(ok); // duplicates allowed in list
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(2), listener.getReceivedEvent().getContent());
        epl.remove(1);
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());
        assertEquals(1, epl.size());

        // test remove(object)
        ok = epl.remove("abc");
        assertTrue(ok);
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        assertTrue(epl.isEmpty());
        listener.setExpectingNotification(false);
        ok = epl.remove("def");
        assertFalse(ok);
        listener.setExpectingNotification(true);

        // test addAll, set, size, indexOf, lastIndexOf
        ok = epl.addAll(Arrays.asList("a", "e"));
        assertTrue(ok);
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(2), listener.getReceivedEvent().getContent());
        assertEquals(2, epl.size());
        ok = epl.addAll(1, Arrays.asList("b", "c", "d"));
        assertTrue(ok);
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, epl.size());
        assertEquals(2, epl.indexOf("c"));
        String old = epl.set(0, "aa");
        assertEquals("a", old);
        assertEquals(EventProducingList.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        listener.setExpectingNotification(false);
        ok = epl.addAll(Arrays.asList());
        assertFalse(ok);
        listener.setExpectingNotification(true);
        epl.add(1, "z");
        assertEquals(6, epl.size());
        assertEquals(3, epl.indexOf("c"));
        assertEquals(1, epl.indexOf("z"));
        epl.add("z");
        assertEquals(7, epl.size());
        assertEquals(3, epl.indexOf("c"));
        assertEquals(6, epl.lastIndexOf("z"));
        listener.setExpectingNotification(false);
        epl.addAll(2, Arrays.asList());
        listener.setExpectingNotification(true);

        // test subList
        List<String> subList = epl.subList(2, 5); // from = inclusive, to = exclusive
        assertEquals(Arrays.asList("b", "c", "d"), subList);

        // test removeAll
        epl.removeAll(Arrays.asList("b", "c", "z")); // { "aa", "d", "e" } left
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(3), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epl.removeAll(Arrays.asList());
        listener.setExpectingNotification(true);

        // test retainAll
        epl.retainAll(Arrays.asList("c", "d", "e")); // { "d", "e" } left
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(2), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epl.retainAll(Arrays.asList("d", "e"));
        listener.setExpectingNotification(true);

        // test contains, containsAll, get
        assertTrue(epl.contains("d"));
        assertFalse(epl.contains("a"));
        assertTrue(epl.containsAll(Arrays.asList("d", "e")));
        assertEquals("d", epl.get(0));
        assertEquals("e", epl.get(1));

        // test toArray
        Object[] arr = epl.toArray();
        String[] stringArr = epl.toArray(new String[] {});
        assertEquals(2, arr.length);
        assertTrue(arr[0].equals("d") && arr[1].equals("e"));
        assertEquals(2, stringArr.length);
        assertTrue(stringArr[0].equals("d") && stringArr[1].equals("e"));

        // test clear
        epl.clear();
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epl.clear();
        listener.setExpectingNotification(true);

        // test iterator
        ok = epl.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(ok);
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, epl.size());
        EventProducingIterator<String> eit = epl.iterator();
        assertNotNull(eit);
        assertTrue(eit.hasNext());
        String firstString = eit.next();
        assertTrue(eit.hasNext());
        String secondString = eit.next();
        eit.remove();
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(4), listener.getReceivedEvent().getContent());
        assertEquals(4, epl.size());
        assertTrue(epl.contains(firstString));
        assertFalse(epl.contains(secondString));
        epl.clear();

        // test listIterator
        ok = epl.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(ok);
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, epl.size());
        EventProducingListIterator<String> leit = epl.listIterator();
        assertNotNull(leit);
        assertTrue(leit.hasNext());
        assertFalse(leit.hasPrevious());
        assertEquals(-1, leit.previousIndex());
        assertEquals(0, leit.nextIndex());
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                System.out.println(leit.previous());
            }
        }, "call to previous() should have caused exception", NoSuchElementException.class);
        firstString = leit.next();
        assertEquals("a", firstString);
        assertTrue(leit.hasNext());
        secondString = leit.next();
        assertEquals("b", secondString);
        assertEquals(1, leit.previousIndex());
        assertEquals(2, leit.nextIndex());
        leit.remove();
        assertEquals(EventProducingList.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(4), listener.getReceivedEvent().getContent());
        assertEquals(4, epl.size());
        assertTrue(epl.contains(firstString));
        assertFalse(epl.contains(secondString));
        assertEquals(0, leit.previousIndex());
        assertEquals(1, leit.nextIndex());
        String thirdString = leit.next();
        assertEquals("c", thirdString);
        leit.set("cc");
        assertEquals(EventProducingList.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(4, epl.size());
        assertEquals(Arrays.asList("a", "cc", "d", "e"), Arrays.asList(epl.toArray()));
        leit.add("dd");
        assertEquals(EventProducingList.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(5, epl.size());
        assertEquals(Arrays.asList("a", "cc", "dd", "d", "e"), Arrays.asList(epl.toArray()));
        assertEquals("dd", leit.previous());
        assertEquals("dd", leit.next());
        assertEquals("d", leit.next());
        assertEquals("e", leit.next());
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                System.out.println(leit.next());
            }
        }, "call to next() should have caused exception", NoSuchElementException.class);
        epl.clear();

        // clear the collection and remove the listeners
        epl.removeAllListeners();
        epl.clear();
    }

    /**
     * Test the EventProducingMap.
     */
    @Test
    public void testEventProducingMap()
    {
        EventProducingMap<Integer, String> epm = new EventProducingMap<>(new TreeMap<>());
        TestEventListener listener = new TestEventListener();
        epm.addListener(listener, EventProducingMap.OBJECT_ADDED_EVENT);
        epm.addListener(listener, EventProducingMap.OBJECT_REMOVED_EVENT);
        epm.addListener(listener, EventProducingMap.OBJECT_CHANGED_EVENT);

        // test put, get, size, containsKey, containsValue
        listener.setExpectingNotification(true);
        assertTrue(epm.isEmpty());
        assertEquals(0, epm.size());
        String replaced = epm.put(1, "abc");
        assertNull(replaced);
        assertEquals(EventProducingMap.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(1), listener.getReceivedEvent().getContent());
        assertFalse(epm.isEmpty());
        assertEquals(1, epm.size());
        replaced = epm.put(1, "def");
        assertEquals("abc", replaced);
        assertEquals(EventProducingMap.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        // assertNull(listener.getReceivedEvent().getContent()); // Changed 2020/04/17 PK
        assertEquals(1, listener.getReceivedEvent().getContent(), "payload is now the unchanged size of the map");
        assertNull(epm.get(2));
        assertEquals("def", epm.get(1));
        assertEquals(1, epm.size());
        assertTrue(epm.containsKey(1));
        assertFalse(epm.containsKey(2));
        assertTrue(epm.containsValue("def"));
        assertFalse(epm.containsValue("abc"));

        // test remove
        String removed = epm.remove(123);
        assertNull(removed);
        removed = epm.remove(1);
        assertEquals("def", removed);
        assertEquals(EventProducingMap.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        assertTrue(epm.isEmpty());
        listener.setExpectingNotification(false);
        removed = epm.remove(234);
        assertNull(removed);
        listener.setExpectingNotification(true);
        epm.clear();

        // test addAll, size
        Map<Integer, String> addMap = new LinkedHashMap<>();
        addMap.put(1, "a");
        addMap.put(2, "b");
        addMap.put(3, "c");
        addMap.put(4, "d");
        addMap.put(5, "e");
        epm.putAll(addMap);
        assertEquals(EventProducingMap.OBJECT_ADDED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(5), listener.getReceivedEvent().getContent());
        assertEquals(5, epm.size());
        epm.putAll(addMap);
        assertEquals(EventProducingMap.OBJECT_CHANGED_EVENT, listener.getReceivedEvent().getType());
        // assertNull(listener.getReceivedEvent().getContent()); // Changed 2020/04/17 PK
        assertEquals(5, listener.getReceivedEvent().getContent(), "payload is now the unchanged size of the map");
        listener.setExpectingNotification(false);
        epm.putAll(new LinkedHashMap<>());
        assertEquals(5, epm.size());
        listener.setExpectingNotification(true);

        // test keySet, values, entrySet
        assertEquals(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), new HashSet<Integer>(epm.keySet()));
        assertEquals(new TreeSet<String>(Arrays.asList("a", "b", "c", "d", "e")), new TreeSet<String>(epm.values()));
        Set<Map.Entry<Integer, String>> entrySet = epm.entrySet(); // TreeMap so set is sorted
        assertEquals(5, entrySet.size());
        Iterator<Map.Entry<Integer, String>> it = entrySet.iterator();
        Map.Entry<Integer, String> entry = it.next();
        assertEquals(1, entry.getKey().intValue());
        assertEquals("a", entry.getValue());
        entry = it.next();
        assertEquals(2, entry.getKey().intValue());
        assertEquals("b", entry.getValue());

        // test clear
        epm.clear();
        assertEquals(EventProducingMap.OBJECT_REMOVED_EVENT, listener.getReceivedEvent().getType());
        assertEquals(Integer.valueOf(0), listener.getReceivedEvent().getContent());
        listener.setExpectingNotification(false);
        epm.clear();
        listener.setExpectingNotification(true);

        // clear the collection and remove the listeners
        epm.removeAllListeners();
        epm.clear();
    }

    /** */
    protected static class TestEventListener implements EventListener
    {
        /** expect notification or not. */
        private boolean expectingNotification = true;

        /** received event. */
        private Event receivedEvent;

        /**
         * @param expectingNotification set expectingNotification
         */
        public void setExpectingNotification(final boolean expectingNotification)
        {
            this.expectingNotification = expectingNotification;
        }

        /**
         * @return receivedEvent
         */
        public Event getReceivedEvent()
        {
            return this.receivedEvent;
        }

        @Override
        public void notify(final Event event)
        {
            if (!this.expectingNotification)
            {
                fail("Received event " + event + " unexpectedly");
            }
            this.receivedEvent = event;
        }
    }

}
