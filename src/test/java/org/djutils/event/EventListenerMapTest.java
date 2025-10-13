package org.djutils.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djutils.event.reference.Reference;
import org.djutils.event.reference.StrongReference;
import org.djutils.event.reference.WeakReference;
import org.djutils.event.rmi.RmiEventListener;
import org.djutils.metadata.MetaData;
import org.junit.jupiter.api.Test;

/**
 * Test the EventListenerMap.
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
public class EventListenerMapTest
{
    /**
     * Test the EventListenerMap.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testEventListenerMap()
    {
        EventType eventType1 = new EventType("EVENT_TYPE1", MetaData.NO_META_DATA);
        EventType eventType2 = new EventType("EVENT_TYPE2", MetaData.NO_META_DATA);
        EventListener el1 = new TestEventListener();
        Reference<EventListener> sref1 = new StrongReference<>(el1);
        EventListener el2 = new TestEventListener();
        Reference<EventListener> sref2 = new StrongReference<>(el2);
        EventListener el3 = new TestEventListener();
        Reference<EventListener> wref3 = new WeakReference<>(el3);
        EventListener el4 = new TestEventListener();
        Reference<EventListener> wref4 = new WeakReference<>(el4);
        Reference<EventListener> sref4 = new StrongReference<>(el4);

        // test size(), isEmpty(), put()
        EventListenerMap elm = new EventListenerMap();
        assertEquals(0, elm.size());
        assertTrue(elm.isEmpty());
        assertFalse(elm.containsKey(eventType1));
        List<Reference<EventListener>> list1 = new ArrayList<>();
        list1.add(sref1);
        List<Reference<EventListener>> putResult = elm.put(eventType1, list1);
        assertNull(putResult);
        assertEquals(1, elm.size());
        assertFalse(elm.isEmpty());
        putResult = elm.put(eventType1, list1);
        assertEquals(1, elm.size());
        assertEquals(putResult, list1);
        List<Reference<EventListener>> list2 = new ArrayList<>();
        list2.add(sref2);
        list2.add(wref3);
        elm.put(eventType2, list2);
        assertEquals(2, elm.size());

        // test keySet()
        Set<EventType> keySet = elm.keySet();
        assertEquals(2, keySet.size());
        assertTrue(keySet.contains(eventType1));
        assertTrue(keySet.contains(eventType2));
        assertFalse(keySet.contains(new EventType("EVENT_TYPE3", MetaData.NO_META_DATA)));

        // test containsKey()
        assertTrue(elm.containsKey(eventType1));
        assertTrue(elm.containsKey(eventType2));
        assertFalse(elm.containsKey(new EventType("EVENT_TYPE3", MetaData.NO_META_DATA)));

        // test containsValue() for Reference and Listener
        assertTrue(elm.containsValue(el1));
        assertTrue(elm.containsValue(el2));
        assertTrue(elm.containsValue(el3));
        assertFalse(elm.containsValue(el4));
        assertTrue(elm.containsValue(sref1));
        assertTrue(elm.containsValue(sref2));
        assertTrue(elm.containsValue(wref3));
        assertFalse(elm.containsValue(sref4));
        assertFalse(elm.containsValue(wref4));

        // test values()
        Collection<List<Reference<EventListener>>> values = elm.values();
        assertEquals(2, values.size());
        Iterator<List<Reference<EventListener>>> vit = values.iterator();
        List<Reference<EventListener>> v1 = vit.next();
        List<Reference<EventListener>> v2 = vit.next();
        assertTrue((v1.size() == 1 && v2.size() == 2) || (v1.size() == 2 && v2.size() == 1));

        // test entrySet()
        Set<Map.Entry<EventType, List<Reference<EventListener>>>> entrySet = elm.entrySet();
        assertEquals(2, entrySet.size());

        // test putAll()
        EventListenerMap elm2 = new EventListenerMap();
        elm2.putAll(elm);
        assertEquals(elm.size(), elm2.size());
        assertEquals(elm.keySet(), elm2.keySet());

        // change something in the underlying list and see if the map remains unaffected
        list1.remove(0);
        assertTrue((v1.size() == 1 && v2.size() == 2) || (v1.size() == 2 && v2.size() == 1));
        list1.add(sref1);

        // test get()
        List<Reference<EventListener>> getList = elm.get(eventType2);
        assertEquals(2, elm.size());
        assertEquals(2, getList.size());
        getList = elm.get(eventType1);
        assertEquals(2, elm.size());
        assertEquals(1, getList.size());
        getList = elm.get(new EventType("EVENT_TYPE3", MetaData.NO_META_DATA));
        assertNull(getList);

        // test remove() and see if the underlying and copied data structures remain unaffected
        List<Reference<EventListener>> removedList = elm.remove(eventType2);
        assertEquals(1, elm.size());
        assertEquals(2, removedList.size());
        assertEquals(list2, removedList);
        assertEquals(2, elm2.size());
        assertEquals(2, values.size());
        assertEquals(2, keySet.size());
        assertTrue(keySet.contains(eventType1));
        assertTrue(keySet.contains(eventType2));
        assertTrue(elm.keySet().contains(eventType1));
        assertFalse(elm.keySet().contains(eventType2));
        // the entrySet should be affected as it is the only infrastructure that is not a safe copy
        assertEquals(1, entrySet.size());

        // test clear() and see if the underlying and copied data structures remain unaffected
        elm.clear();
        assertEquals(0, elm.size());
        assertEquals(2, elm2.size());
        assertEquals(2, values.size());
        assertEquals(2, keySet.size());
        assertTrue(keySet.contains(eventType1));
        assertTrue(keySet.contains(eventType2));
        assertFalse(elm.keySet().contains(eventType1));
        assertFalse(elm.keySet().contains(eventType2));
    }

    /** */
    protected static class TestEventListener implements EventListener
    {
        /** expect notification or not. */
        private boolean expectingNotification = true;

        /** expected object in notify. */
        private Object expectedObject;

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
         * @param expectedObject set expectedObject
         */
        public void setExpectedObject(final Object expectedObject)
        {
            this.expectedObject = expectedObject;
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
            assertEquals(this.expectedObject, event.getContent());
        }

    }

    /** */
    protected static class TestRemoteEventListener extends RmiEventListener
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /**
         * @throws RemoteException on error
         * @throws AlreadyBoundException on error
         */
        public TestRemoteEventListener() throws RemoteException, AlreadyBoundException
        {
            super("localhost", 2050, "testListener");
        }

        @Override
        public void notify(final Event event)
        {
            // tagging method
        }
    }

}
