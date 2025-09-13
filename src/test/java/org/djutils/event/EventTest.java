package org.djutils.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;

import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;
import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

/**
 * EventTest tests the EventType, Event, and TimedEvent.
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
public class EventTest
{
    /**
     * Test the NO_META_DATA object.
     */
    @Test
    public void testNoMetaData()
    {
        EventType noMetaDataEventType = new EventType("No Meta Data", MetaData.NO_META_DATA);
        new Event(noMetaDataEventType, new Object[] {"abc", 123, 0.6}); // should not fail

        EventType withMetaDataEventType =
                new EventType("With Meta Data", new MetaData("Almost identical to NO_META_DATA", "Any Object is accepted",
                        new ObjectDescriptor("Almost identical to NO_META_DATA", "Any Object is accepted", Object.class)));
        try
        {
            new Event(withMetaDataEventType, new Object[] {"abc", 123, 0.6});
            fail("imitation of NO_META_DATA does not work for Object[] payload");
        }
        catch (IndexOutOfBoundsException ioobe)
        {
            // Ignore expected exception
        }

    }

    /**
     * Test the constructor without a name.
     */
    @Test
    public void testNoName()
    {
        MetaData metaData =
                new MetaData("INT_EVENT", "event with integer payload", new ObjectDescriptor("int", "integer", Integer.class));
        EventType eventType = new EventType(metaData);
        assertEquals(eventType, eventType);
        Object content = new SerializableObject();
        assertNotEquals(eventType, content);
        assertNotEquals(eventType, null);
        assertEquals(eventType, new EventType(metaData));
        assertEquals(eventType.getName(), "INT_EVENT");
        assertEquals(eventType.getMetaData().getName(), "INT_EVENT");
        assertEquals(eventType.getMetaData().getDescription(), "event with integer payload");
        assertEquals(eventType.toString(), "INT_EVENT");
        assertEquals(1, eventType.getMetaData().getObjectDescriptors().length);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new EventType((MetaData) null);
            }
        }, "Constructing EventType with null metadata should have failed");

        new Event(eventType, 5);
        new Event(eventType, null);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Event(eventType, 1.2);
            }
        }, "Constructing Integer Event with double content should have failed");
        new Event(eventType, 1.2, false); // but without checking it should succeed

        EventType eventType2 = new EventType(MetaData.EMPTY);
        assertEquals(eventType2, eventType2);
        assertNotEquals(eventType, eventType2);
        Object content2 = new SerializableObject();
        assertNotEquals(eventType2, content2);
        assertNotEquals(eventType2, null);
        assertEquals(eventType2, new EventType(MetaData.EMPTY));
        assertEquals(eventType2.getName(), "No data");
        assertEquals(eventType2.getMetaData().getName(), "No data");
        assertEquals(eventType2.getMetaData().getDescription(), "No data");
        assertEquals(eventType2.toString(), "No data");
        assertEquals(1, eventType.getMetaData().getObjectDescriptors().length);

        new Event(eventType2, null);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Event(eventType2, 1.2);
            }
        }, "Constructing EMPTY Event with double content should have failed");
    }

    /**
     * Test the EventType.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testEventType()
    {
        EventType eventType = new EventType("TEST_TYPE", MetaData.NO_META_DATA);
        assertEquals(eventType, eventType);
        Object content = new SerializableObject();
        assertNotEquals(eventType, content);
        assertNotEquals(eventType, null);
        assertNotEquals(eventType, new EventType("TEST_TYPE2", MetaData.NO_META_DATA));
        assertEquals(eventType.getName(), "TEST_TYPE");
        assertEquals(eventType.toString(), "TEST_TYPE");

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new EventType("", null);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new EventType((String) null, (MetaData) null);
            }
        });

        // Check the deprecated constructor
        eventType = new EventType("event with unspecified meta data");
        assertEquals(MetaData.NO_META_DATA, eventType.getMetaData(),
                "Deprecated constructor uses NO_META_DATA for the meta data");
        assertEquals("event with unspecified meta data", eventType.getName(), "Name is correctly used");
    }

    /**
     * Test the Event.
     */
    @Test
    public void testEvent()
    {
        EventType eventType = new EventType("TEST_TYPE", MetaData.NO_META_DATA);
        EventType eventType2 = new EventType("TEST_TYPE2", MetaData.NO_META_DATA);
        Serializable content = new SerializableObject();
        Serializable content2 = new SerializableObject();
        Event event = new Event(eventType, content);
        assertEquals(event.getContent(), content);
        assertEquals(event.getType(), eventType);

        assertEquals(event, event);
        assertEquals(event, new Event(eventType, content));
        assertNotEquals(event, null);

        assertNotEquals(event, new Event(eventType2, content));
        assertNotEquals(event, new Event(eventType2, content));
        assertNotEquals(event, new Event(eventType, content2));

        assertNotEquals(event, new Event(eventType, null));
        assertNotEquals(new Event(eventType, null), event);
        assertEquals(new Event(eventType, null), new Event(eventType, null));

        assertEquals(event.hashCode(), event.hashCode());
        assertEquals(event.hashCode(), new Event(eventType, content).hashCode());

        assertNotEquals(event.hashCode(), new Event(eventType2, content).hashCode());
        assertNotEquals(event.hashCode(), new Event(eventType2, content).hashCode());
        assertNotEquals(event.hashCode(), new Event(eventType, content2).hashCode());
        assertNotEquals(event.hashCode(), new Event(eventType, null).hashCode());

        assertTrue(event.toString().contains("TEST_TYPE"));

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Event(null, content);
            }
        }, NullPointerException.class);
    }

    /**
     * Test the TimedEvent.
     */
    @Test
    public void testTimedEvent()
    {
        EventType eventType = new EventType("TEST_TYPE", MetaData.NO_META_DATA);
        EventType eventType2 = new EventType("TEST_TYPE2", MetaData.NO_META_DATA);
        Serializable content = new SerializableObject();
        Serializable content2 = new SerializableObject();
        long time = 123L;
        long time2 = 456L;
        TimedEvent<Long> timedEvent = new TimedEvent<>(eventType, content, time);
        TimedEvent<Long> timedEvent2 = new TimedEvent<>(eventType2, content2, time2);
        assertEquals(content, timedEvent.getContent());
        assertEquals(eventType, timedEvent.getType());
        assertEquals(time, timedEvent.getTimeStamp().longValue());

        assertEquals(timedEvent, timedEvent);
        assertEquals(new TimedEvent<Long>(eventType, content, time), timedEvent);
        assertNotEquals(timedEvent, null);

        assertNotEquals(timedEvent, new TimedEvent<Long>(eventType2, content, time));
        assertNotEquals(timedEvent, new TimedEvent<Long>(eventType2, content, time));
        assertNotEquals(timedEvent, new TimedEvent<Long>(eventType, content2, time));
        assertNotEquals(timedEvent, new TimedEvent<Long>(eventType, content, time2));

        assertNotEquals(timedEvent, new TimedEvent<Long>(eventType, null, time));
        assertNotEquals(new TimedEvent<Long>(eventType, null, time), timedEvent);

        assertEquals(new TimedEvent<Long>(eventType, null, time), new TimedEvent<Long>(eventType, null, time));

        assertEquals(timedEvent.hashCode(), timedEvent.hashCode());
        assertEquals(timedEvent.hashCode(), new TimedEvent<Long>(eventType, content, time).hashCode());

        assertNotEquals(timedEvent.hashCode(), new TimedEvent<Long>(eventType2, content, time).hashCode());
        assertNotEquals(timedEvent.hashCode(), new TimedEvent<Long>(eventType2, content, time).hashCode());
        assertNotEquals(timedEvent.hashCode(), new TimedEvent<Long>(eventType, content2, time).hashCode());
        assertNotEquals(timedEvent.hashCode(), new TimedEvent<Long>(eventType, content, time2).hashCode());
        assertNotEquals(timedEvent.hashCode(), new TimedEvent<Long>(eventType, null, time).hashCode());

        assertTrue(timedEvent.toString().contains("TEST_TYPE"));
        assertTrue(timedEvent.toString().contains("123"));

        assertTrue(timedEvent.compareTo(timedEvent) == 0);
        assertTrue(timedEvent.compareTo(timedEvent2) < 0);
        assertTrue(timedEvent2.compareTo(timedEvent) > 0);

        MetaData metaData =
                new MetaData("INT_EVENT", "event with integer payload", new ObjectDescriptor("int", "integer", Integer.class));
        EventType intEventType = new EventType(metaData);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new TimedEvent<Double>(intEventType, 1.2, 3.4);
            }
        }, "Constructing Integer TimedEvent with double content should have failed");
        new TimedEvent<Double>(intEventType, 1.2, 3.4, false); // but without checking it should succeed

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new TimedEvent<Long>(null, content, time);
            }
        }, NullPointerException.class);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new TimedEvent<Long>(eventType, content, null);
            }
        }, NullPointerException.class);

    }

    /** Serializable object class. */
    class SerializableObject extends Object implements Serializable
    {
        /** */
        private static final long serialVersionUID = 1L;
    }
}
