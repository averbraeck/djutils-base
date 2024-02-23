package org.djutils.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.djutils.event.reference.Reference;
import org.djutils.event.reference.ReferenceType;
import org.djutils.exceptions.Try;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.junit.jupiter.api.Test;

/**
 * Test the EventProducer and EventListener.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class EventPubSubTest
{
    /**
     * Test the EventProducer and EventListener.
     */
    @Test
    public void testEventPubSub()
    {
        TestEventProducer producer = new TestEventProducer();
        TestEventListener listener = new TestEventListener();
        EventType eventType = new EventType("TEST_TYPE", MetaData.NO_META_DATA);
        EventType timedEventType = new EventType("TIMED_TEST_TYPE", MetaData.NO_META_DATA);

        assertFalse(producer.hasListeners());
        assertEquals(0, producer.numberOfListeners(eventType));
        assertEquals(0, producer.getEventTypesWithListeners().size());
        assertEquals(0, producer.getListenerReferences(eventType).size());
        boolean addListenerOK = producer.addListener(listener, eventType);
        assertTrue(addListenerOK);
        assertTrue(producer.hasListeners());
        assertEquals(1, producer.numberOfListeners(eventType));
        assertEquals(1, producer.getEventTypesWithListeners().size());
        assertEquals(1, producer.getListenerReferences(eventType).size());
        assertEquals(listener, producer.getListenerReferences(eventType).get(0).get());

        String string = "abc123";
        listener.setExpectedObject(string);
        producer.fireEvent(new Event(eventType, string));
        assertEquals(string, listener.getReceivedEvent().getContent());
        assertEquals(eventType, listener.getReceivedEvent().getType());

        listener.setExpectedObject(Boolean.valueOf(true));
        producer.fireEvent(eventType, true);
        listener.setExpectedObject(Boolean.valueOf(false));
        producer.fireEvent(eventType, false);

        listener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireEvent(eventType, (byte) 87);

        listener.setExpectedObject(Character.valueOf('a'));
        producer.fireEvent(eventType, 'a');

        listener.setExpectedObject(Short.valueOf((short) -234));
        producer.fireEvent(eventType, (short) -234);

        listener.setExpectedObject(Float.valueOf(458.9f));
        producer.fireEvent(eventType, 458.9f);

        listener.setExpectedObject(Double.valueOf(123.456d));
        producer.fireEvent(eventType, 123.456d);

        listener.setExpectedObject(Integer.valueOf(12345));
        producer.fireEvent(eventType, 12345);

        listener.setExpectedObject(Long.valueOf(123456L));
        producer.fireEvent(eventType, 123456L);

        listener.setExpectedObject("abcde");
        producer.fireEvent(eventType, "abcde");

        listener.setExpectedObject(null);
        producer.fireEvent(eventType);

        // remove listener tests
        producer.removeListener(listener, TestEventProducer.PRODUCER_EVENT_1);
        listener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireEvent(eventType, (byte) 87);

        producer.removeListener(listener, eventType);
        listener.setExpectingNotification(false);
        producer.fireEvent(eventType, 12345);

        addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1);
        assertTrue(addListenerOK);
        addListenerOK = producer.addListener(listener, eventType);
        assertTrue(addListenerOK);
        listener.setExpectingNotification(true);
        listener.setExpectedObject(Double.valueOf(123.456d));
        producer.fireEvent(eventType, 123.456d);
        assertEquals(eventType, listener.getReceivedEvent().getType());
        listener.setExpectedObject(Double.valueOf(234.567d));
        producer.fireEvent(TestEventProducer.PRODUCER_EVENT_1, 234.567d);
        assertEquals(TestEventProducer.PRODUCER_EVENT_1, listener.getReceivedEvent().getType());

        int nrRemovedListeners = producer.removeAllListeners();
        assertEquals(2, nrRemovedListeners);
        listener.setExpectingNotification(false);
        listener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireEvent(eventType, (byte) 87);
        producer.fireEvent(TestEventProducer.PRODUCER_EVENT_1, 12345);

        addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1);
        assertTrue(addListenerOK);
        addListenerOK = producer.addListener(listener, eventType);
        assertTrue(addListenerOK);
        listener.setExpectingNotification(true);
        listener.setExpectedObject(Double.valueOf(123.456d));
        producer.fireEvent(eventType, 123.456d);
        assertEquals(eventType, listener.getReceivedEvent().getType());
        listener.setExpectedObject(Double.valueOf(234.567d));
        producer.fireEvent(TestEventProducer.PRODUCER_EVENT_1, 234.567d);
        assertEquals(TestEventProducer.PRODUCER_EVENT_1, listener.getReceivedEvent().getType());
        TestTimedEventListener<Double> timedListener = new TestTimedEventListener<>();
        addListenerOK = producer.addListener(timedListener, timedEventType);
        assertTrue(addListenerOK);
        timedListener.setExpectingNotification(true);
        timedListener.setExpectedObject(Double.valueOf(12.34d));
        listener.setExpectedObject(Double.valueOf(12.34d));
        producer.fireTimedEvent(new TimedEvent<Double>(timedEventType, Double.valueOf(12.34d), 12.01d));
        assertEquals(12.01, timedListener.getReceivedEvent().getTimeStamp(), 0.001);

        nrRemovedListeners = producer.removeAllListeners(TestEventListener.class);
        assertEquals(2, nrRemovedListeners);
        listener.setExpectingNotification(false);
        timedListener.setExpectingNotification(true);
        timedListener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireTimedEvent(timedEventType, (byte) 87, Double.valueOf(13.02d));
        assertEquals(13.02, timedListener.getReceivedEvent().getTimeStamp(), 0.001);

        nrRemovedListeners = producer.removeAllListeners();
        assertEquals(1, nrRemovedListeners);
    }

    /**
     * Test the EventProducer and EventListener for verified / unverified events.
     */
    @Test
    public void testEventVerificationPubSub()
    {
        TestEventProducer producer = new TestEventProducer();
        TestEventListener listener = new TestEventListener();
        EventType eventType = new EventType("STRING_TYPE",
                new MetaData("STRING", "string", new ObjectDescriptor("String", "string", String.class)));

        boolean addListenerOK = producer.addListener(listener, eventType);
        assertTrue(addListenerOK);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Boolean.valueOf(true));
                producer.fireEvent(eventType, true);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Byte.valueOf((byte) 87));
                producer.fireEvent(eventType, (byte) 87);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Character.valueOf('a'));
                producer.fireEvent(eventType, 'a');
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Short.valueOf((short) -234));
                producer.fireEvent(eventType, (short) -234);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Float.valueOf(458.9f));
                producer.fireEvent(eventType, 458.9f);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Double.valueOf(123.456d));
                producer.fireEvent(eventType, 123.456d);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Integer.valueOf(12345));
                producer.fireEvent(eventType, 12345);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Long.valueOf(123456L));
                producer.fireEvent(eventType, 123456L);
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(new String[] {"a", "b"});
                producer.fireEvent(eventType, new String[] {"a", "b"});
            }
        }, "expected IndexOutOfBoundsException", IndexOutOfBoundsException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Double.valueOf(1.2));
                producer.fireEvent(eventType, Double.valueOf(1.2));
            }
        }, "expected ClassCastException", ClassCastException.class);

        listener.setExpectedObject("abc");
        producer.fireEvent(new Event(eventType, "abc"));

        listener.setExpectedObject(Boolean.valueOf(true));
        producer.fireUnverifiedEvent(eventType, true);

        listener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireUnverifiedEvent(eventType, (byte) 87);

        listener.setExpectedObject(Character.valueOf('a'));
        producer.fireUnverifiedEvent(eventType, 'a');

        listener.setExpectedObject(Short.valueOf((short) -234));
        producer.fireUnverifiedEvent(eventType, (short) -234);

        listener.setExpectedObject(Float.valueOf(458.9f));
        producer.fireUnverifiedEvent(eventType, 458.9f);

        listener.setExpectedObject(Double.valueOf(123.456d));
        producer.fireUnverifiedEvent(eventType, 123.456d);

        listener.setExpectedObject(Integer.valueOf(12345));
        producer.fireUnverifiedEvent(eventType, 12345);

        listener.setExpectedObject(Long.valueOf(123456L));
        producer.fireUnverifiedEvent(eventType, 123456L);

        listener.setExpectedObject(new String[] {"a", "b"});
        producer.fireUnverifiedEvent(eventType, new String[] {"a", "b"});

        listener.setExpectedObject(Double.valueOf(1.2));
        producer.fireUnverifiedEvent(eventType, Double.valueOf(1.2));

        listener.setExpectedObject(null);
        producer.fireUnverifiedEvent(eventType);

        producer.removeAllListeners();
    }

    /**
     * Test the EventProducer and EventListener for TimedEvents.
     */
    @Test
    public void testTimedEventPubSub()
    {
        TestEventProducer producer = new TestEventProducer();
        TestTimedEventListener<Double> listener = new TestTimedEventListener<>();
        EventType eventType = new EventType("TIMED_TEST_TYPE", MetaData.NO_META_DATA);

        boolean addListenerOK = producer.addListener(listener, eventType);
        assertTrue(addListenerOK);

        String string = "abc123";
        listener.setExpectedObject(string);
        producer.fireTimedEvent(new TimedEvent<Double>(eventType, string, 12.01d));
        assertEquals(string, listener.getReceivedEvent().getContent());
        assertEquals(eventType, listener.getReceivedEvent().getType());
        assertEquals(12.01d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Boolean.valueOf(true));
        producer.fireTimedEvent(eventType, true, Double.valueOf(12.02d));
        assertEquals(12.02d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);
        listener.setExpectedObject(Boolean.valueOf(false));
        producer.fireTimedEvent(eventType, false, Double.valueOf(12.03d));
        assertEquals(12.03d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireTimedEvent(eventType, (byte) 87, Double.valueOf(12.04d));
        assertEquals(12.04d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Character.valueOf('X'));
        producer.fireTimedEvent(eventType, 'X', Double.valueOf(12.14d));
        assertEquals(12.14d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Short.valueOf((short) -234));
        producer.fireTimedEvent(eventType, (short) -234, Double.valueOf(12.05d));
        assertEquals(12.05d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Float.valueOf(246.8f));
        producer.fireTimedEvent(eventType, 246.8f, Double.valueOf(12.15d));
        assertEquals(12.15d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Double.valueOf(123.456d));
        producer.fireTimedEvent(eventType, 123.456d, Double.valueOf(12.06d));
        assertEquals(12.06d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Integer.valueOf(12345));
        producer.fireTimedEvent(eventType, 12345, Double.valueOf(12.07d));
        assertEquals(12.07d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Long.valueOf(123456L));
        producer.fireTimedEvent(eventType, 123456L, Double.valueOf(12.08d));
        assertEquals(12.08d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(null);
        producer.fireTimedEvent(eventType, null, Double.valueOf(12.09d));
        assertEquals(12.09d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject("abc");
        producer.fireTimedEvent(new TimedEvent<Double>(eventType, "abc", Double.valueOf(12.10d)));
        assertEquals(12.10d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(null);
        producer.fireUnverifiedTimedEvent(eventType, Double.valueOf(12.11d));
        assertEquals(12.11d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        producer.removeAllListeners();
    }

    /**
     * Test the EventProducer and EventListener for verified / unverified timed events.
     */
    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testTimedEventVerificationPubSub()
    {
        TestEventProducer producer = new TestEventProducer();
        TestTimedEventListener<Double> listener = new TestTimedEventListener<>();
        EventType eventType = new EventType("TIMED_STRING_TYPE",
                new MetaData("STRING", "string", new ObjectDescriptor("String", "string", String.class)));

        boolean addListenerOK = producer.addListener(listener, eventType);
        assertTrue(addListenerOK);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Boolean.valueOf(true));
                producer.fireTimedEvent(eventType, true, Double.valueOf(12.01d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Byte.valueOf((byte) 87));
                producer.fireTimedEvent(eventType, (byte) 87, Double.valueOf(12.02d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Character.valueOf('a'));
                producer.fireTimedEvent(eventType, 'a', Double.valueOf(12.03d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Short.valueOf((short) -234));
                producer.fireTimedEvent(eventType, (short) -234, Double.valueOf(12.04d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Float.valueOf(458.9f));
                producer.fireTimedEvent(eventType, 458.9f, Double.valueOf(12.05d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Double.valueOf(123.456d));
                producer.fireTimedEvent(eventType, 123.456d, Double.valueOf(12.06d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Integer.valueOf(12345));
                producer.fireTimedEvent(eventType, 12345, Double.valueOf(12.07d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Long.valueOf(123456L));
                producer.fireTimedEvent(eventType, 123456L, Double.valueOf(12.08d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(new String[] {"a", "b"});
                producer.fireTimedEvent(eventType, new String[] {"a", "b"}, Double.valueOf(12.09d));
            }
        }, "expected IndexOutOfBoundsException", IndexOutOfBoundsException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                listener.setExpectedObject(Double.valueOf(1.2));
                producer.fireTimedEvent(eventType, Double.valueOf(1.2), Double.valueOf(12.10d));
            }
        }, "expected ClassCastException", ClassCastException.class);

        listener.setExpectedObject(Boolean.valueOf(true));
        producer.fireUnverifiedTimedEvent(eventType, true, Double.valueOf(12.01d));
        assertEquals(12.01d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Byte.valueOf((byte) 87));
        producer.fireUnverifiedTimedEvent(eventType, (byte) 87, Double.valueOf(12.02d));
        assertEquals(12.02d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Character.valueOf('a'));
        producer.fireUnverifiedTimedEvent(eventType, 'a', Double.valueOf(12.03d));
        assertEquals(12.03d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Short.valueOf((short) -234));
        producer.fireUnverifiedTimedEvent(eventType, (short) -234, Double.valueOf(12.04d));
        assertEquals(12.04d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Float.valueOf(458.9f));
        producer.fireUnverifiedTimedEvent(eventType, 458.9f, Double.valueOf(12.05d));
        assertEquals(12.05d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Double.valueOf(123.456d));
        producer.fireUnverifiedTimedEvent(eventType, 123.456d, Double.valueOf(12.06d));
        assertEquals(12.06d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Integer.valueOf(12345));
        producer.fireUnverifiedTimedEvent(eventType, 12345, Double.valueOf(12.07d));
        assertEquals(12.07d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Long.valueOf(123456L));
        producer.fireUnverifiedTimedEvent(eventType, 123456L, Double.valueOf(12.08d));
        assertEquals(12.08d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(new String[] {"a", "b"});
        producer.fireUnverifiedTimedEvent(eventType, new String[] {"a", "b"}, Double.valueOf(12.09d));
        assertEquals(12.09d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        listener.setExpectedObject(Double.valueOf(1.2));
        producer.fireUnverifiedTimedEvent(eventType, Double.valueOf(1.2), Double.valueOf(12.10d));
        assertEquals(12.10d, listener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

        producer.removeAllListeners();
    }

    /**
     * Test the EventProducer and EventListener for two events with the same name being equal...
     */
    @Test
    public void testIllegalEventProducer()
    {
        assertEquals(TestIllegalEventProducer.PRODUCER_EVENT_1, TestIllegalEventProducer.PRODUCER_EVENT_2);
        assertNotEquals(TestIllegalEventProducer.PRODUCER_EVENT_1, TestEventProducer.PRODUCER_EVENT_1);
        assertNotEquals(TestEventProducer.PRODUCER_EVENT_2, TestEventProducer.PRODUCER_EVENT_1);
    }

    /**
     * Test the EventProducer for strong and weak references, and for position information.
     * @throws SecurityException on error retrieving listener map
     * @throws NoSuchFieldException on error retrieving listener map
     * @throws IllegalAccessException on error retrieving listener map
     * @throws IllegalArgumentException on error retrieving listener map
     */
    @Test
    public void testEventStrongWeakPos()
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
    {
        TestEventProducer producer = new TestEventProducer();
        TestEventListener listener = new TestEventListener();

        // test illegal parameters and null pointer exceptions in adding a listener
        try
        {
            producer.addListener(null, TestEventProducer.PRODUCER_EVENT_1);
            fail("null listener should have thrown an exception");
        }
        catch (NullPointerException npe)
        {
            // Ignore expected exception
        }
        boolean addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1, -10, ReferenceType.STRONG);
        assertFalse(addListenerOK);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                producer.addListener(listener, null);
            }
        }, "expected NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1, null);
            }
        }, "expected NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1, 0, null);
            }
        }, "expected NullPointerException", NullPointerException.class);

        // test whether weak and strong calls to addListener work, and whether positions can be provided
        assertFalse(producer.hasListeners());
        assertEquals(0, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_1));
        assertEquals(new LinkedHashSet<EventType>(), producer.getEventTypesWithListeners());

        addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1);
        assertTrue(addListenerOK);
        assertTrue(producer.hasListeners());
        assertEquals(1, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_1));
        assertEquals(new LinkedHashSet<EventType>(Arrays.asList(TestEventProducer.PRODUCER_EVENT_1)),
                producer.getEventTypesWithListeners());

        addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_2, ReferenceType.WEAK);
        assertTrue(addListenerOK);
        assertEquals(1, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_1));
        assertEquals(1, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_2));
        assertEquals(
                new LinkedHashSet<EventType>(
                        Arrays.asList(TestEventProducer.PRODUCER_EVENT_1, TestEventProducer.PRODUCER_EVENT_2)),
                producer.getEventTypesWithListeners());

        // check false for adding same listener second time
        addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_2, ReferenceType.WEAK);
        assertFalse(addListenerOK);
        assertEquals(1, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_1));
        assertEquals(1, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_2));
        assertEquals(
                new LinkedHashSet<EventType>(
                        Arrays.asList(TestEventProducer.PRODUCER_EVENT_1, TestEventProducer.PRODUCER_EVENT_2)),
                producer.getEventTypesWithListeners());

        // check LAST_POSITION and FIRST_POSITION
        TestEventListener listener2 = new TestEventListener();
        TestEventListener listener3 = new TestEventListener();
        addListenerOK = producer.addListener(listener2, TestEventProducer.PRODUCER_EVENT_2, LocalEventProducer.LAST_POSITION);
        addListenerOK = producer.addListener(listener3, TestEventProducer.PRODUCER_EVENT_2, LocalEventProducer.FIRST_POSITION);
        assertEquals(3, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_2));

        // check whether positions have been inserted okay: listener3 - listener - listener2
        List<Reference<EventListener>> listenerList = producer.getListenerReferences(TestEventProducer.PRODUCER_EVENT_2);
        assertEquals(3, listenerList.size());
        assertEquals(listener3, listenerList.get(0).get());
        assertEquals(listener, listenerList.get(1).get());
        assertEquals(listener2, listenerList.get(2).get());
    }

    /**
     * Test the EventProducer for a weak reference that is removed by the garbage collector.
     * @throws SecurityException on error retrieving listener map
     * @throws NoSuchFieldException on error retrieving listener map
     * @throws IllegalAccessException on error retrieving listener map
     * @throws IllegalArgumentException on error retrieving listener map
     */
    @Test
    public void testEventProducerWeakRemoval()
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
    {
        TestEventProducer producer = new TestEventProducer();
        TestEventListener listener = new TestEventListener();
        boolean addListenerOK = producer.addListener(listener, TestEventProducer.PRODUCER_EVENT_1, ReferenceType.WEAK);
        assertTrue(addListenerOK);
        assertTrue(producer.hasListeners());
        assertEquals(1, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_1));

        // fire an event -- should arrive
        listener.setExpectingNotification(true);
        listener.setExpectedObject(Integer.valueOf(12));
        producer.fireEvent(TestEventProducer.PRODUCER_EVENT_1, 12);

        // check the registered listeners in the map
        List<Reference<EventListener>> listenerList = producer.getListenerReferences(TestEventProducer.PRODUCER_EVENT_1);
        assertEquals(1, listenerList.size());
        Reference<EventListener> ref = listenerList.get(0);
        // simulate clearing by GC (the list is a safe copy, but the reference is original)
        Field referent = ref.getClass().getDeclaredField("referent");
        referent.setAccessible(true);
        referent.set(ref, new java.lang.ref.WeakReference<EventListener>(null));
        referent.setAccessible(false);

        // fire an event -- should not arrive
        listener.setExpectingNotification(false);
        producer.fireEvent(TestEventProducer.PRODUCER_EVENT_1, 34);
        assertFalse(producer.hasListeners());
        assertEquals(0, producer.numberOfListeners(TestEventProducer.PRODUCER_EVENT_1));

        producer.removeAllListeners();
    }

    /** */
    protected static class TestEventProducer extends LocalEventProducer
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /** */
        public static final EventType PRODUCER_EVENT_1 = new EventType("PRODUCER_EVENT_1", MetaData.NO_META_DATA);

        /** */
        public static final EventType PRODUCER_EVENT_2 = new EventType("PRODUCER_EVENT_2", MetaData.NO_META_DATA);

        /** this should be okay. */
        @SuppressWarnings("unused")
        private static final EventType PRODUCER_EVENT_3 = new EventType("PRODUCER_EVENT_1", MetaData.NO_META_DATA);

        /** this should be okay. */
        @SuppressWarnings("unused")
        private static final EventType PRODUCER_EVENT_4 = new EventType("PRODUCER_EVENT_1", MetaData.NO_META_DATA);
    }

    /** */
    protected static class TestIllegalEventProducer extends LocalEventProducer
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /** */
        public static final EventType PRODUCER_EVENT_1 = new EventType("PRODUCER_EVENT_1", MetaData.NO_META_DATA);

        /** duplicate static non-private EventType should give error on class construction. */
        public static final EventType PRODUCER_EVENT_2 = new EventType("PRODUCER_EVENT_1", MetaData.NO_META_DATA);
    }

    /** */
    protected static class TestEventListener implements EventListener
    {
        /** */
        private static final long serialVersionUID = 20191230L;

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

        /** {@inheritDoc} */
        @Override
        public void notify(final Event event) throws RemoteException
        {
            if (!this.expectingNotification)
            {
                fail("Received event " + event + " unexpectedly");
            }
            this.receivedEvent = event;
            if (this.expectedObject != null && event.getContent() != null && this.expectedObject.getClass().isArray()
                    && event.getContent().getClass().isArray())
            {
                Object[] e = (Object[]) this.expectedObject;
                Object[] r = (Object[]) event.getContent();
                assertEquals(e.length, r.length);
                for (int i = 0; i < e.length; i++)
                {
                    assertEquals(e[i], r[i]);
                }
            }
            else
            {
                assertEquals(this.expectedObject, event.getContent());
            }
        }

    }

    /**
     * TimedEventListener.
     * @param <C> the comparable time type
     */
    protected static class TestTimedEventListener<C extends Comparable<C> & Serializable> implements EventListener
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /** expect notification or not. */
        private boolean expectingNotification = true;

        /** expected object in notify. */
        private Object expectedObject;

        /** received event. */
        private TimedEvent<C> receivedEvent;

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
        public TimedEvent<C> getReceivedEvent()
        {
            return this.receivedEvent;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        @Override
        public void notify(final Event event) throws RemoteException
        {
            if (!this.expectingNotification)
            {
                fail("Received event " + event + " unexpectedly");
            }
            if (!(event instanceof TimedEvent))
            {
                fail("Received event " + event + " is not a TimedEvent");
            }
            this.receivedEvent = (TimedEvent<C>) event;
            if (this.expectedObject != null && event.getContent() != null && this.expectedObject.getClass().isArray()
                    && event.getContent().getClass().isArray())
            {
                Object[] e = (Object[]) this.expectedObject;
                Object[] r = (Object[]) event.getContent();
                assertEquals(e.length, r.length);
                for (int i = 0; i < e.length; i++)
                {
                    assertEquals(e[i], r[i]);
                }
            }
            else
            {
                assertEquals(this.expectedObject, event.getContent());
            }
        }

    }

}
