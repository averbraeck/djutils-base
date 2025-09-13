package org.djutils.event.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.TimedEvent;
import org.djutils.event.reference.Reference;
import org.djutils.event.reference.ReferenceType;
import org.djutils.event.rmi.RmiEventListener;
import org.djutils.event.rmi.RmiEventProducer;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.rmi.RmiRegistry;
import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

/**
 * RemoteEventTest makes some very basic tests for the RemoteEventListener and RemoteEventProducer.
 * <p>
 * Copyright (c) 2019-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RemoteEventPubSubTest
{
    /**
     * Test the construction of the RemoteEventListsner and RemoteEventProducer.
     * @throws RemoteException on remote error
     * @throws AlreadyBoundException when producer or listener is already bound in the RMI registry
     * @throws MalformedURLException on URL error
     */
    @Test
    public void testRemoteEventListenerProducer() throws RemoteException, AlreadyBoundException, MalformedURLException
    {
        TestRemoteEventProducer producer = new TestRemoteEventProducer(2100);
        try
        {
            TestRemoteEventListener listener = new TestRemoteEventListener("listener", 2100);
            assertFalse(producer.hasListeners());
            assertEquals(0, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));
            assertEquals(0, producer.getEventTypesWithListeners().size());
            assertEquals(0, producer.getListenerReferences(TestRemoteEventProducer.REMOTE_EVENT_1).size());
            boolean addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1);
            assertTrue(addListenerOK);
            assertTrue(producer.hasListeners());
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));
            assertEquals(1, producer.getEventTypesWithListeners().size());
            assertEquals(1, producer.getListenerReferences(TestRemoteEventProducer.REMOTE_EVENT_1).size());
            assertEquals(listener, producer.getListenerReferences(TestRemoteEventProducer.REMOTE_EVENT_1).get(0).get());

            String string = "abc123";
            listener.setExpectedObject(string);
            producer.fireEvent(new Event(TestRemoteEventProducer.REMOTE_EVENT_1, string));
            assertEquals(string, listener.getReceivedEvent().getContent());
            assertEquals(TestRemoteEventProducer.REMOTE_EVENT_1, listener.getReceivedEvent().getType());

            listener.setExpectedObject(Boolean.valueOf(true));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, true);
            listener.setExpectedObject(Boolean.valueOf(false));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, false);

            listener.setExpectedObject(Byte.valueOf((byte) 87));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, (byte) 87);

            listener.setExpectedObject(Character.valueOf('a'));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 'a');

            listener.setExpectedObject(Short.valueOf((short) -234));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, (short) -234);

            listener.setExpectedObject(Float.valueOf(458.9f));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 458.9f);

            listener.setExpectedObject(Double.valueOf(123.456d));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 123.456d);

            listener.setExpectedObject(Integer.valueOf(12345));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 12345);

            listener.setExpectedObject(Long.valueOf(123456L));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 123456L);

            listener.setExpectedObject("abcde");
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, "abcde");

            listener.setExpectedObject(null);
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1);

            // remove listener tests
            producer.removeListener(listener, TestRemoteEventProducer.REMOTE_EVENT_2);
            listener.setExpectedObject(Byte.valueOf((byte) 87));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, (byte) 87);

            producer.removeListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1);
            listener.setExpectingNotification(false);
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 12345);

            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_2);
            assertTrue(addListenerOK);
            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1);
            assertTrue(addListenerOK);
            listener.setExpectingNotification(true);
            listener.setExpectedObject(Double.valueOf(123.456d));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 123.456d);
            assertEquals(TestRemoteEventProducer.REMOTE_EVENT_1, listener.getReceivedEvent().getType());
            listener.setExpectedObject(Double.valueOf(234.567d));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_2, 234.567d);
            assertEquals(TestRemoteEventProducer.REMOTE_EVENT_2, listener.getReceivedEvent().getType());

            int nrRemovedListeners = producer.removeAllListeners();
            assertEquals(2, nrRemovedListeners);
            listener.setExpectingNotification(false);
            listener.setExpectedObject(Byte.valueOf((byte) 87));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, (byte) 87);
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_2, 12345);

            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_2);
            assertTrue(addListenerOK);
            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1);
            assertTrue(addListenerOK);
            listener.setExpectingNotification(true);
            listener.setExpectedObject(Double.valueOf(123.456d));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 123.456d);
            assertEquals(TestRemoteEventProducer.REMOTE_EVENT_1, listener.getReceivedEvent().getType());
            listener.setExpectedObject(Double.valueOf(234.567d));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_2, 234.567d);
            assertEquals(TestRemoteEventProducer.REMOTE_EVENT_2, listener.getReceivedEvent().getType());

            TestRemoteTimedEventListener<Double> timedListener = new TestRemoteTimedEventListener<>("timedListener", 2100);
            addListenerOK = producer.addListener(timedListener, TestRemoteEventProducer.TIMED_REMOTE_EVENT_1);
            assertTrue(addListenerOK);
            timedListener.setExpectingNotification(true);
            timedListener.setExpectedObject(Double.valueOf(12.34d));
            listener.setExpectedObject(Double.valueOf(12.34d));
            producer.fireTimedEvent(
                    new TimedEvent<Double>(TestRemoteEventProducer.TIMED_REMOTE_EVENT_1, Double.valueOf(12.34d), 12.01d));
            assertEquals(12.01, timedListener.getReceivedEvent().getTimeStamp(), 0.001);

            nrRemovedListeners = producer.removeAllListeners(TestRemoteEventListener.class);
            assertEquals(2, nrRemovedListeners);
            listener.setExpectingNotification(false);
            timedListener.setExpectingNotification(true);
            timedListener.setExpectedObject(Byte.valueOf((byte) 87));
            producer.fireTimedEvent(TestRemoteEventProducer.TIMED_REMOTE_EVENT_1, (byte) 87, Double.valueOf(13.02d));
            assertEquals(13.02, timedListener.getReceivedEvent().getTimeStamp(), 0.001);

            nrRemovedListeners = producer.removeAllListeners();
            assertEquals(1, nrRemovedListeners);
        }
        catch (RemoteException | AlreadyBoundException exception)
        {
            throw exception;
        }
        finally
        {
            // clean up the registry
            RmiRegistry.closeRegistry(producer.getRegistry());
        }
    }

    /**
     * Test the RemoteEventProducer and RemoteEventListener for verified / unverified events.
     * @throws RemoteException on remote error
     * @throws AlreadyBoundException when producer or listener is already bound in the RMI registry
     * @throws MalformedURLException on URL error
     */
    @Test
    @SuppressWarnings("checkstyle:methodlength")
    public void testRemoteEventVerificationPubSub() throws RemoteException, AlreadyBoundException, MalformedURLException
    {
        TestRemoteEventProducer producer = new TestRemoteEventProducer(2101);
        try
        {
            TestRemoteEventListener listener = new TestRemoteEventListener("listener", 2101);
            EventType eventType = new EventType("STRING_TYPE",
                    new MetaData("STRING", "string", new ObjectDescriptor("String", "string", String.class)));

            boolean addListenerOK = producer.addListener(listener, eventType);
            assertTrue(addListenerOK);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Boolean.valueOf(true));
                    producer.fireEvent(eventType, true);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Byte.valueOf((byte) 87));
                    producer.fireEvent(eventType, (byte) 87);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Character.valueOf('a'));
                    producer.fireEvent(eventType, 'a');
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Short.valueOf((short) -234));
                    producer.fireEvent(eventType, (short) -234);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Float.valueOf(458.9f));
                    producer.fireEvent(eventType, 458.9f);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Double.valueOf(123.456d));
                    producer.fireEvent(eventType, 123.456d);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Integer.valueOf(12345));
                    producer.fireEvent(eventType, 12345);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(Long.valueOf(123456L));
                    producer.fireEvent(eventType, 123456L);
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    listener.setExpectedObject(new String[] {"a", "b"});
                    producer.fireEvent(eventType, new String[] {"a", "b"});
                }
            }, "expected IndexOutOfBoundsException", IndexOutOfBoundsException.class);

            UnitTest.testFail(new UnitTest.Execution()
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
        catch (RemoteException | AlreadyBoundException exception)
        {
            throw exception;
        }
        finally
        {
            // clean up the registry
            RmiRegistry.closeRegistry(producer.getRegistry());
        }
    }

    /**
     * Test the construction of the RemoteEventListsner and RemoteEventProducer.
     * @throws RemoteException on remote error
     * @throws AlreadyBoundException when producer or listener is already bound in the RMI registry
     * @throws MalformedURLException on URL error
     */
    @Test
    public void testTimedRemoteEventListenerProducer() throws RemoteException, AlreadyBoundException, MalformedURLException
    {
        TestRemoteEventProducer producer = new TestRemoteEventProducer(2102);
        try
        {
            TestRemoteTimedEventListener<Double> timedListener = new TestRemoteTimedEventListener<>("timedListener", 2102);
            EventType timedEventType = new EventType("TIMED_TEST_TYPE", MetaData.NO_META_DATA);

            boolean addListenerOK = producer.addListener(timedListener, timedEventType);
            assertTrue(addListenerOK);

            String string = "abc123";
            timedListener.setExpectedObject(string);
            producer.fireTimedEvent(new TimedEvent<Double>(timedEventType, string, 12.01d));
            assertEquals(string, timedListener.getReceivedEvent().getContent());
            assertEquals(timedEventType, timedListener.getReceivedEvent().getType());
            assertEquals(12.01d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Boolean.valueOf(true));
            producer.fireTimedEvent(timedEventType, true, Double.valueOf(12.02d));
            assertEquals(12.02d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);
            timedListener.setExpectedObject(Boolean.valueOf(false));
            producer.fireTimedEvent(timedEventType, false, Double.valueOf(12.03d));
            assertEquals(12.03d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Byte.valueOf((byte) 87));
            producer.fireTimedEvent(timedEventType, (byte) 87, Double.valueOf(12.04d));
            assertEquals(12.04d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Character.valueOf('X'));
            producer.fireTimedEvent(timedEventType, 'X', Double.valueOf(12.14d));
            assertEquals(12.14d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Short.valueOf((short) -234));
            producer.fireTimedEvent(timedEventType, (short) -234, Double.valueOf(12.05d));
            assertEquals(12.05d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Float.valueOf(246.8f));
            producer.fireTimedEvent(timedEventType, 246.8f, Double.valueOf(12.15d));
            assertEquals(12.15d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Double.valueOf(123.456d));
            producer.fireTimedEvent(timedEventType, 123.456d, Double.valueOf(12.06d));
            assertEquals(12.06d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Integer.valueOf(12345));
            producer.fireTimedEvent(timedEventType, 12345, Double.valueOf(12.07d));
            assertEquals(12.07d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Long.valueOf(123456L));
            producer.fireTimedEvent(timedEventType, 123456L, Double.valueOf(12.08d));
            assertEquals(12.08d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(null);
            producer.fireTimedEvent(timedEventType, null, Double.valueOf(12.09d));
            assertEquals(12.09d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject("abc");
            producer.fireTimedEvent(new TimedEvent<Double>(timedEventType, "abc", Double.valueOf(12.10d)));
            assertEquals(12.10d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(null);
            producer.fireUnverifiedTimedEvent(timedEventType, Double.valueOf(12.11d));
            assertEquals(12.11d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            producer.removeAllListeners();
        }
        catch (RemoteException | AlreadyBoundException | MalformedURLException exception)
        {
            throw exception;
        }
        finally
        {
            // clean up the registry
            RmiRegistry.closeRegistry(producer.getRegistry());
        }
    }

    /**
     * Test the RemoteEventProducer and RemoteTimedEventListener for verified / unverified events.
     * @throws RemoteException on remote error
     * @throws AlreadyBoundException when producer or listener is already bound in the RMI registry
     * @throws MalformedURLException on URL error
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testRemoteTimedEventVerificationPubSub() throws RemoteException, AlreadyBoundException, MalformedURLException
    {
        TestRemoteEventProducer producer = new TestRemoteEventProducer(2103);
        try
        {
            TestRemoteTimedEventListener<Double> timedListener = new TestRemoteTimedEventListener<>("listener", 2103);
            EventType timedEventType = new EventType("TIMED_STRING_TYPE",
                    new MetaData("STRING", "string", new ObjectDescriptor("String", "string", String.class)));

            boolean addListenerOK = producer.addListener(timedListener, timedEventType);
            assertTrue(addListenerOK);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Boolean.valueOf(true));
                    producer.fireTimedEvent(timedEventType, true, Double.valueOf(12.01d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Byte.valueOf((byte) 87));
                    producer.fireTimedEvent(timedEventType, (byte) 87, Double.valueOf(12.02d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Character.valueOf('a'));
                    producer.fireTimedEvent(timedEventType, 'a', Double.valueOf(12.03d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Short.valueOf((short) -234));
                    producer.fireTimedEvent(timedEventType, (short) -234, Double.valueOf(12.04d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Float.valueOf(458.9f));
                    producer.fireTimedEvent(timedEventType, 458.9f, Double.valueOf(12.05d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Double.valueOf(123.456d));
                    producer.fireTimedEvent(timedEventType, 123.456d, Double.valueOf(12.06d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Integer.valueOf(12345));
                    producer.fireTimedEvent(timedEventType, 12345, Double.valueOf(12.07d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Long.valueOf(123456L));
                    producer.fireTimedEvent(timedEventType, 123456L, Double.valueOf(12.08d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(new String[] {"a", "b"});
                    producer.fireTimedEvent(timedEventType, new String[] {"a", "b"}, Double.valueOf(12.09d));
                }
            }, "expected IndexOutOfBoundsException", IndexOutOfBoundsException.class);

            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    timedListener.setExpectedObject(Double.valueOf(1.2));
                    producer.fireTimedEvent(timedEventType, Double.valueOf(1.2), Double.valueOf(12.10d));
                }
            }, "expected ClassCastException", ClassCastException.class);

            timedListener.setExpectedObject(Boolean.valueOf(true));
            producer.fireUnverifiedTimedEvent(timedEventType, true, Double.valueOf(12.01d));
            assertEquals(12.01d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Byte.valueOf((byte) 87));
            producer.fireUnverifiedTimedEvent(timedEventType, (byte) 87, Double.valueOf(12.02d));
            assertEquals(12.02d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Character.valueOf('a'));
            producer.fireUnverifiedTimedEvent(timedEventType, 'a', Double.valueOf(12.03d));
            assertEquals(12.03d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Short.valueOf((short) -234));
            producer.fireUnverifiedTimedEvent(timedEventType, (short) -234, Double.valueOf(12.04d));
            assertEquals(12.04d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Float.valueOf(458.9f));
            producer.fireUnverifiedTimedEvent(timedEventType, 458.9f, Double.valueOf(12.05d));
            assertEquals(12.05d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Double.valueOf(123.456d));
            producer.fireUnverifiedTimedEvent(timedEventType, 123.456d, Double.valueOf(12.06d));
            assertEquals(12.06d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Integer.valueOf(12345));
            producer.fireUnverifiedTimedEvent(timedEventType, 12345, Double.valueOf(12.07d));
            assertEquals(12.07d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Long.valueOf(123456L));
            producer.fireUnverifiedTimedEvent(timedEventType, 123456L, Double.valueOf(12.08d));
            assertEquals(12.08d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(new String[] {"a", "b"});
            producer.fireUnverifiedTimedEvent(timedEventType, new String[] {"a", "b"}, Double.valueOf(12.09d));
            assertEquals(12.09d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            timedListener.setExpectedObject(Double.valueOf(1.2));
            producer.fireUnverifiedTimedEvent(timedEventType, Double.valueOf(1.2), Double.valueOf(12.10d));
            assertEquals(12.10d, timedListener.getReceivedEvent().getTimeStamp().doubleValue(), 0.001);

            producer.removeAllListeners();
        }
        catch (RemoteException | AlreadyBoundException | MalformedURLException exception)
        {
            throw exception;
        }
        finally
        {
            // clean up the registry
            RmiRegistry.closeRegistry(producer.getRegistry());
        }
    }

    /**
     * Test the EventProducer for strong and weak references, and for position information.
     * @throws SecurityException on error retrieving listener map
     * @throws NoSuchFieldException on error retrieving listener map
     * @throws IllegalAccessException on error retrieving listener map
     * @throws IllegalArgumentException on error retrieving listener map
     * @throws RemoteException on network exception
     * @throws AlreadyBoundException when RMI registry not cleaned
     */
    @Test
    public void testEventStrongWeakPos() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
            SecurityException, RemoteException, AlreadyBoundException
    {
        TestRemoteEventProducer producer = new TestRemoteEventProducer(2104);
        try
        {
            TestRemoteEventListener listener = new TestRemoteEventListener("listener", 2104);

            // test illegal parameters and null pointer exceptions in adding a listener
            try
            {
                producer.addListener(null, TestRemoteEventProducer.REMOTE_EVENT_1);
                fail("null listener should have thrown an exception");
            }
            catch (NullPointerException npe)
            {
                // Ignore expected exception
            }
            boolean addListenerOK =
                    producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1, -10, ReferenceType.STRONG);
            assertFalse(addListenerOK);
            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    producer.addListener(listener, null);
                }
            }, "expected NullPointerException", NullPointerException.class);
            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1, null);
                }
            }, "expected NullPointerException", NullPointerException.class);
            UnitTest.testFail(new UnitTest.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1, 0, null);
                }
            }, "expected NullPointerException", NullPointerException.class);

            // test whether weak and strong calls to addListener work, and whether positions can be provided
            assertFalse(producer.hasListeners());
            assertEquals(0, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));
            assertEquals(new LinkedHashSet<EventType>(), producer.getEventTypesWithListeners());

            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1);
            assertTrue(addListenerOK);
            assertTrue(producer.hasListeners());
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));
            assertEquals(new LinkedHashSet<EventType>(Arrays.asList(TestRemoteEventProducer.REMOTE_EVENT_1)),
                    producer.getEventTypesWithListeners());

            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_2, ReferenceType.WEAK);
            assertTrue(addListenerOK);
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_2));
            assertEquals(
                    new LinkedHashSet<EventType>(
                            Arrays.asList(TestRemoteEventProducer.REMOTE_EVENT_1, TestRemoteEventProducer.REMOTE_EVENT_2)),
                    producer.getEventTypesWithListeners());

            // check false for adding same listener second time
            addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_2, ReferenceType.WEAK);
            assertFalse(addListenerOK);
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_2));
            assertEquals(
                    new LinkedHashSet<EventType>(
                            Arrays.asList(TestRemoteEventProducer.REMOTE_EVENT_1, TestRemoteEventProducer.REMOTE_EVENT_2)),
                    producer.getEventTypesWithListeners());

            // check LAST_POSITION and FIRST_POSITION
            TestRemoteEventListener listener2 = new TestRemoteEventListener("listener2", 2104);
            TestRemoteEventListener listener3 = new TestRemoteEventListener("listener3", 2104);
            addListenerOK =
                    producer.addListener(listener2, TestRemoteEventProducer.REMOTE_EVENT_2, LocalEventProducer.LAST_POSITION);
            addListenerOK =
                    producer.addListener(listener3, TestRemoteEventProducer.REMOTE_EVENT_2, LocalEventProducer.FIRST_POSITION);
            assertEquals(3, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_2));

            // check whether positions have been inserted okay: listener3 - listener - listener2
            List<Reference<EventListener>> listenerList =
                    producer.getListenerReferences(TestRemoteEventProducer.REMOTE_EVENT_2);
            assertEquals(3, listenerList.size());
            assertEquals(listener3, listenerList.get(0).get());
            assertEquals(listener, listenerList.get(1).get());
            assertEquals(listener2, listenerList.get(2).get());

            producer.removeAllListeners();
        }
        catch (RemoteException | AlreadyBoundException exception)
        {
            throw exception;
        }
        finally
        {
            // clean up the registry
            RmiRegistry.closeRegistry(producer.getRegistry());
        }
    }

    /**
     * Test the EventProducer for a weak reference that is removed by the garbage collector.
     * @throws SecurityException on error retrieving listener map
     * @throws NoSuchFieldException on error retrieving listener map
     * @throws IllegalAccessException on error retrieving listener map
     * @throws IllegalArgumentException on error retrieving listener map
     * @throws RemoteException on network exception
     * @throws AlreadyBoundException when RMI registry not cleaned
     */
    @Test
    public void testEventProducerWeakRemoval() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
            SecurityException, RemoteException, AlreadyBoundException
    {
        TestRemoteEventProducer producer = new TestRemoteEventProducer(2105);
        try
        {
            TestRemoteEventListener listener = new TestRemoteEventListener("listener", 2105);
            boolean addListenerOK = producer.addListener(listener, TestRemoteEventProducer.REMOTE_EVENT_1, ReferenceType.WEAK);
            assertTrue(addListenerOK);
            assertTrue(producer.hasListeners());
            assertEquals(1, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));

            // fire an event -- should arrive
            listener.setExpectingNotification(true);
            listener.setExpectedObject(Integer.valueOf(12));
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 12);

            List<Reference<EventListener>> listenerList =
                    producer.getListenerReferences(TestRemoteEventProducer.REMOTE_EVENT_1);
            assertEquals(1, listenerList.size());
            Reference<EventListener> ref = listenerList.get(0);
            // simulate clearing by GC (the list is a safe copy, but the reference is original)
            Field referent = ref.getClass().getDeclaredField("referent");
            referent.setAccessible(true);
            referent.set(ref, new java.lang.ref.WeakReference<EventListener>(null));
            referent.setAccessible(false);

            // fire an event -- should not arrive
            listener.setExpectingNotification(false);
            producer.fireEvent(TestRemoteEventProducer.REMOTE_EVENT_1, 34);
            assertFalse(producer.hasListeners());
            assertEquals(0, producer.numberOfListeners(TestRemoteEventProducer.REMOTE_EVENT_1));

            producer.removeAllListeners();
        }
        catch (RemoteException | AlreadyBoundException exception)
        {
            throw exception;
        }
        finally
        {
            // clean up the registry
            RmiRegistry.closeRegistry(producer.getRegistry());
        }
    }

    /** */
    protected static class TestRemoteEventProducer extends RmiEventProducer
    {
        /** */
        private static final long serialVersionUID = 20191230L;

        /** */
        public static final EventType REMOTE_EVENT_1 = new EventType("REMOTE_EVENT_1", MetaData.NO_META_DATA);

        /** */
        public static final EventType REMOTE_EVENT_2 = new EventType("REMOTE_EVENT_2", MetaData.NO_META_DATA);

        /** */
        public static final EventType TIMED_REMOTE_EVENT_1 = new EventType("TIMED_REMOTE_EVENT_1", MetaData.NO_META_DATA);

        /**
         * Construct a RemoteEventProducer.
         * @param port the port to use for this test
         * @throws RemoteException on error
         * @throws AlreadyBoundException on error
         */
        public TestRemoteEventProducer(final int port) throws RemoteException, AlreadyBoundException
        {
            super("127.0.0.1", port, "producer");
        }

    }

    /** */
    protected static class TestRemoteEventListener extends RmiEventListener
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
         * @param key the key under which the listener will be registered in RMI
         * @param port the port to use in the test
         * @throws RemoteException on error
         * @throws AlreadyBoundException on error
         */
        public TestRemoteEventListener(final String key, final int port) throws RemoteException, AlreadyBoundException
        {
            super("localhost", port, key);
        }

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
     * RemoteTimedEventListener.
     * @param <C> the comparable time type
     */
    protected static class TestRemoteTimedEventListener<C extends Comparable<C> & Serializable> extends RmiEventListener
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
         * @param key key used to bind to the RMI registry
         * @param port the TCP/IP port
         * @throws RemoteException on error
         * @throws AlreadyBoundException on error
         * @throws MalformedURLException on URL error
         */
        public TestRemoteTimedEventListener(final String key, final int port)
                throws RemoteException, AlreadyBoundException, MalformedURLException
        {
            super(new URL("http://127.0.0.1:" + port), key);
        }

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
