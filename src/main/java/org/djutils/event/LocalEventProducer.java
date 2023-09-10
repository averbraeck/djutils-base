package org.djutils.event;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import org.djutils.event.reference.Reference;
import org.djutils.event.reference.ReferenceType;

/**
 * The LocalEventProducer defines the registration and fireEvent operations of an event producer. This behavior includes adding
 * and removing listeners for a specific event type, and firing events for all kinds of different payloads. The EventListener
 * and EventProducer together form a combination of the Publish-Subscribe design pattern and the Observer design pattern using
 * the notify(event) method. See <a href="https://en.wikipedia.org/wiki/Publish-subscribe_pattern" target=
 * "_blank">https://en.wikipedia.org/wiki/Publish-subscribe_pattern</a>,
 * <a href="https://en.wikipedia.org/wiki/Observer_pattern" target="_blank">https://en.wikipedia.org/wiki/Observer_pattern</a>,
 * and <a href="https://howtodoinjava.com/design-patterns/behavioral/observer-design-pattern/" target=
 * "_blank">https://howtodoinjava.com/design-patterns/behavioral/observer-design-pattern/</a>.
 * <p>
 * The EventProducer forms the reference implementation of the publish side of the pub/sub design pattern. The storage of the
 * listeners is done in a Map with the EventType as the key, and a List of References (weak or strong) to the Listeners. Note
 * that the term 'Local' used in the class name is opposed to remote event producers such as the RmiEventProducer.
 * </p>
 * <p>
 * Copyright (c) 2002-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LocalEventProducer implements EventProducer, Serializable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20200207;

    /** The collection of interested listeners. */
    private EventListenerMap eventListenerMap = new EventListenerMap();

    /** {@inheritDoc} */
    @Override
    public EventListenerMap getEventListenerMap()
    {
        return this.eventListenerMap;
    }

    /* **************************************************************************************************** */
    /* *********************** (RE) IMPLEMENTATION OF METHODS WITHOUT REMOTEEXCEPTION ********************* */
    /* **************************************************************************************************** */

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final int position,
            final ReferenceType referenceType)
    {
        try
        {
            return EventProducer.super.addListener(listener, eventType, position, referenceType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType)
    {
        try
        {
            return EventProducer.super.addListener(listener, eventType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final ReferenceType referenceType)
    {
        try
        {
            return EventProducer.super.addListener(listener, eventType, referenceType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean addListener(final EventListener listener, final EventType eventType, final int position)
    {
        try
        {
            return EventProducer.super.addListener(listener, eventType, position);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public int removeAllListeners()
    {
        try
        {
            return EventProducer.super.removeAllListeners();
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public int removeAllListeners(final Class<?> ofClass)
    {
        try
        {
            return EventProducer.super.removeAllListeners(ofClass);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeListener(final EventListener listener, final EventType eventType)
    {
        try
        {
            return EventProducer.super.removeListener(listener, eventType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasListeners()
    {
        try
        {
            return EventProducer.super.hasListeners();
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfListeners(final EventType eventType)
    {
        try
        {
            return EventProducer.super.numberOfListeners(eventType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Reference<EventListener>> getListenerReferences(final EventType eventType)
    {
        try
        {
            return EventProducer.super.getListenerReferences(eventType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<EventType> getEventTypesWithListeners()
    {
        try
        {
            return EventProducer.super.getEventTypesWithListeners();
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public void fireEvent(final Event event)
    {
        try
        {
            EventProducer.super.fireEvent(event);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireTimedEvent(final TimedEvent<C> event)
    {
        try
        {
            EventProducer.super.fireTimedEvent(event);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public void fireEvent(final EventType eventType)
    {
        try
        {
            EventProducer.super.fireEvent(eventType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireTimedEvent(final EventType eventType, final C time)
    {
        try
        {
            EventProducer.super.fireTimedEvent(eventType, time);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public void fireEvent(final EventType eventType, final Serializable value)
    {
        try
        {
            EventProducer.super.fireEvent(eventType, value);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireTimedEvent(final EventType eventType, final Serializable value,
            final C time)

    {
        try
        {
            EventProducer.super.fireTimedEvent(eventType, value, time);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public void fireUnverifiedEvent(final EventType eventType)
    {
        try
        {
            EventProducer.super.fireUnverifiedEvent(eventType);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireUnverifiedTimedEvent(final EventType eventType, final C time)

    {
        try
        {
            EventProducer.super.fireUnverifiedTimedEvent(eventType, time);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public void fireUnverifiedEvent(final EventType eventType, final Serializable value)
    {
        try
        {
            EventProducer.super.fireUnverifiedEvent(eventType, value);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

    /** {@inheritDoc} */
    @Override
    public <C extends Comparable<C> & Serializable> void fireUnverifiedTimedEvent(final EventType eventType,
            final Serializable value, final C time)
    {
        try
        {
            EventProducer.super.fireUnverifiedTimedEvent(eventType, value, time);
        }
        catch (RemoteException exception)
        {
            throw new RuntimeException(exception);

        }
    }

}
