package org.djutils.event;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djutils.event.reference.Reference;
import org.djutils.event.reference.ReferenceType;
import org.djutils.event.reference.StrongReference;
import org.djutils.event.reference.WeakReference;
import org.djutils.exceptions.Throw;

/**
 * EventProducer is the interface that exposes a few of the methods of the implementation of an EventProducer to the outside
 * world: the ability to add and remove listeners.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface EventProducer extends Serializable, Remote
{
    /** The FIRST_POSITION in the queue. */
    int FIRST_POSITION = 0;

    /** The LAST_POSITION in the queue. */
    int LAST_POSITION = -1;

    /**
     * Add a listener to the specified position of a queue of listeners.
     * @param listener which is interested at certain events
     * @param eventType the events of interest
     * @param position the position of the listener in the queue
     * @param referenceType whether the listener is added as a strong or as a weak reference
     * @return the success of adding the listener. If a listener was already added or an illegal position is provided false is
     *         returned
     * @throws RemoteException on network error
     * @see org.djutils.event.reference.WeakReference
     */
    default boolean addListener(final EventListener listener, final EventType eventType, final int position,
            final ReferenceType referenceType) throws RemoteException
    {
        Throw.whenNull(listener, "listener cannot be null");
        Throw.whenNull(eventType, "eventType cannot be null");
        Throw.whenNull(referenceType, "referenceType cannot be null");
        if (position < LAST_POSITION)
        {
            return false;
        }
        Reference<EventListener> reference = null;
        if (referenceType.isStrong())
        {
            reference = new StrongReference<EventListener>(listener);
        }
        else
        {
            reference = new WeakReference<EventListener>(listener);
        }
        EventListenerMap eventListenerMap = getEventListenerMap();
        if (eventListenerMap.containsKey(eventType))
        {
            for (Reference<EventListener> entry : eventListenerMap.get(eventType))
            {
                if (listener.equals(entry.get()))
                {
                    return false;
                }
            }
            List<Reference<EventListener>> entries = eventListenerMap.get(eventType);
            if (position == LAST_POSITION)
            {
                entries.add(reference);
            }
            else
            {
                entries.add(position, reference);
            }
        }
        else
        {
            List<Reference<EventListener>> entries = new ArrayList<>();
            entries.add(reference);
            eventListenerMap.put(eventType, entries);
        }
        return true;
    }

    /**
     * Add a listener as strong reference to the BEGINNING of a queue of listeners.
     * @param listener the listener which is interested at events of eventType
     * @param eventType the events of interest
     * @return the success of adding the listener. If a listener was already added false is returned
     * @throws RemoteException on network error
     */
    default boolean addListener(final EventListener listener, final EventType eventType) throws RemoteException
    {
        return addListener(listener, eventType, FIRST_POSITION);
    }

    /**
     * Add a listener to the BEGINNING of a queue of listeners.
     * @param listener the listener which is interested at events of eventType
     * @param eventType the events of interest
     * @param referenceType whether the listener is added as a strong or as a weak reference
     * @return the success of adding the listener. If a listener was already added false is returned
     * @throws RemoteException on network error
     * @see org.djutils.event.reference.WeakReference
     */
    default boolean addListener(final EventListener listener, final EventType eventType, final ReferenceType referenceType)
            throws RemoteException
    {
        return addListener(listener, eventType, FIRST_POSITION, referenceType);
    }

    /**
     * Add a listener as strong reference to the specified position of a queue of listeners.
     * @param listener the listener which is interested at events of eventType
     * @param eventType the events of interest
     * @param position the position of the listener in the queue
     * @return the success of adding the listener. If a listener was already added, or an illegal position is provided false is
     *         returned
     * @throws RemoteException on network error
     */
    default boolean addListener(final EventListener listener, final EventType eventType, final int position)
            throws RemoteException
    {
        return addListener(listener, eventType, position, ReferenceType.STRONG);
    }

    /**
     * Return the map with the EventListener entries and the reference types.
     * @return the map with the EventListener entries and the reference types
     * @throws RemoteException on network error
     */
    EventListenerMap getEventListenerMap() throws RemoteException;

    /**
     * Remove all the listeners from this event producer.
     * @return the number of removed event types for which listeners existed
     * @throws RemoteException on network error
     */
    default int removeAllListeners() throws RemoteException
    {
        int result = getEventListenerMap().size();
        getEventListenerMap().clear();
        return result;
    }

    /**
     * Removes all the listeners of a class from this event producer.
     * @param ofClass the class or superclass
     * @return the number of removed listeners
     * @throws RemoteException on network error
     */
    default int removeAllListeners(final Class<?> ofClass) throws RemoteException
    {
        Throw.whenNull(ofClass, "ofClass may not be null");
        int result = 0;
        Map<EventType, Reference<EventListener>> removeMap = new LinkedHashMap<>();
        for (EventType type : getEventListenerMap().keySet())
        {
            for (Iterator<Reference<EventListener>> ii = getEventListenerMap().get(type).iterator(); ii.hasNext();)
            {
                Reference<EventListener> listener = ii.next();
                if (listener.get().getClass().isAssignableFrom(ofClass))
                {
                    removeMap.put(type, listener);
                    result++;
                }
            }
        }
        for (EventType type : removeMap.keySet())
        {
            removeListener(removeMap.get(type).get(), type);
        }
        return result;
    }

    /**
     * Remove the subscription of a listener for a specific event.
     * @param listener which is no longer interested
     * @param eventType the event which is of no interest any more
     * @return the success of removing the listener. If a listener was not subscribed false is returned
     * @throws RemoteException on network error
     */
    default boolean removeListener(final EventListener listener, final EventType eventType) throws RemoteException
    {
        Throw.whenNull(listener, "listener may not be null");
        Throw.whenNull(eventType, "eventType may not be null");
        EventListenerMap eventListenerMap = getEventListenerMap();
        if (!eventListenerMap.containsKey(eventType))
        {
            return false;
        }
        boolean result = false;
        for (Iterator<Reference<EventListener>> i = eventListenerMap.get(eventType).iterator(); i.hasNext();)
        {
            Reference<EventListener> reference = i.next();
            EventListener entry = reference.get();
            if (entry == null)
            {
                i.remove();
            }
            else
            {
                if (listener.equals(entry))
                {
                    i.remove();
                    result = true;
                }
            }
            if (eventListenerMap.get(eventType).size() == 0)
            {
                eventListenerMap.remove(eventType);
            }
        }
        return result;
    }

    /**
     * Return whether the EventProducer has listeners.
     * @return whether the EventProducer has listeners or not
     * @throws RemoteException on network error
     */
    default boolean hasListeners() throws RemoteException
    {
        return !getEventListenerMap().isEmpty();
    }

    /**
     * Return the number of listeners for the provided EventType.
     * @param eventType the event type to return the number of listeners for
     * @return whether the EventProducer has listeners or not
     * @throws RemoteException on network error
     */
    default int numberOfListeners(final EventType eventType) throws RemoteException
    {
        if (getEventListenerMap().containsKey(eventType))
        {
            return getEventListenerMap().get(eventType).size();
        }
        return 0;
    }

    /**
     * Return a safe copy of the list of (strong or weak) references to the registered listeners for the provided event type, or
     * an empty list when nothing is registered for this event type. The method never returns a null pointer, so it is safe to
     * use the result directly in an iterator. The references to the listeners are the original references, so not safe copies.
     * @param eventType the event type to look up the listeners for
     * @return the list of references to the listeners for this event type,
     *         or an empty list when the event type is not registered
     * @throws RemoteException on network error
     */
    default List<Reference<EventListener>> getListenerReferences(final EventType eventType) throws RemoteException
    {
        List<Reference<EventListener>> result = new ArrayList<>();
        if (getEventListenerMap().get(eventType) != null)
        {
            result.addAll(getEventListenerMap().get(eventType));
        }
        return result;
    }

    /**
     * Return the EventTypes for which the EventProducer has listeners.
     * @return the EventTypes for which the EventProducer has registered listeners
     * @throws RemoteException on netowrk error
     */
    default Set<EventType> getEventTypesWithListeners() throws RemoteException
    {
        return getEventListenerMap().keySet(); // is already a safe copy
    }

    /**
     * Remove one reference from the subscription list.
     * @param reference the (strong or weak) reference to remove
     * @param eventType the eventType for which reference must be removed
     * @return true if the reference was removed; otherwise false
     * @throws RemoteException on network error
     */
    private boolean removeListener(final Reference<EventListener> reference, final EventType eventType) throws RemoteException
    {
        Throw.whenNull(reference, "reference may not be null");
        Throw.whenNull(eventType, "eventType may not be null");
        EventListenerMap eventListenerMap = getEventListenerMap();
        boolean success = false;
        for (Iterator<Reference<EventListener>> i = eventListenerMap.get(eventType).iterator(); i.hasNext();)
        {
            if (i.next().equals(reference))
            {
                i.remove();
                success = true;
            }
        }
        if (eventListenerMap.get(eventType).size() == 0)
        {
            eventListenerMap.remove(eventType);
        }
        return success;
    }

    /**
     * Transmit an event to all subscribed listeners.
     * @param event the event
     * @throws RemoteException on network error
     */
    default void fireEvent(final Event event) throws RemoteException
    {
        Throw.whenNull(event, "event may not be null");
        EventListenerMap eventListenerMap = getEventListenerMap();
        if (eventListenerMap.containsKey(event.getType()))
        {
            // make a safe copy because of possible removeListener() in notify() method during fireEvent
            List<Reference<EventListener>> listenerList = new ArrayList<>(eventListenerMap.get(event.getType()));
            for (Reference<EventListener> reference : listenerList)
            {
                EventListener listener = reference.get();
                try
                {
                    if (listener != null)
                    {
                        // The garbage collection has not cleaned the referent
                        fireEvent(listener, event);
                    }
                    else
                    {
                        // The garbage collection cleaned the referent;
                        // there is no need to keep the subscription
                        removeListener(reference, event.getType());
                    }
                }
                catch (RemoteException remoteException)
                {
                    // A network failure prevented the delivery,
                    // subscription is removed.
                    removeListener(reference, event.getType());
                }
            }
        }
    }

    /**
     * Transmit an event to a listener. This method is a hook method. The default implementation simply invokes the notify on
     * the listener. In specific cases (filtering, storing, queueing, this method can be overwritten.
     * @param listener the listener for this event
     * @param event the event to fire
     * @throws RemoteException on network failure
     */
    private void fireEvent(final EventListener listener, final Event event) throws RemoteException
    {
        listener.notify(event);
    }

    /**
     * Transmit a time-stamped event to all interested listeners.
     * @param event the event
     * @param <C> the comparable type to indicate the time when the event is fired
     * @throws RemoteException on network failure
     */
    default <C extends Comparable<C> & Serializable> void fireTimedEvent(final TimedEvent<C> event) throws RemoteException
    {
        fireEvent(event);
    }

    /**
     * Transmit an event with no payload object to all interested listeners.
     * @param eventType the eventType of the event
     * @throws RemoteException on network failure
     */
    default void fireEvent(final EventType eventType) throws RemoteException
    {
        fireEvent(new Event(eventType, null, true));
    }

    /**
     * Transmit a time-stamped event with a no payload object to all interested listeners.
     * @param eventType the eventType of the event.
     * @param time a time stamp for the event
     * @param <C> the comparable type to indicate the time when the event is fired
     * @throws RemoteException on network failure
     */
    default <C extends Comparable<C> & Serializable> void fireTimedEvent(final EventType eventType, final C time)
            throws RemoteException

    {
        fireEvent(new TimedEvent<C>(eventType, null, time, true));
    }

    /**
     * Transmit an event with a serializable object as payload to all interested listeners.
     * @param eventType the eventType of the event
     * @param value the object sent with the event
     * @throws RemoteException on network failure
     */
    default void fireEvent(final EventType eventType, final Serializable value) throws RemoteException
    {
        fireEvent(new Event(eventType, value, true));
    }

    /**
     * Transmit a time-stamped event with a Serializable object (payload) to all interested listeners.
     * @param eventType the eventType of the event.
     * @param value the payload sent with the event
     * @param time a time stamp for the event
     * @param <C> the comparable type to indicate the time when the event is fired
     * @throws RemoteException on network failure
     */
    default <C extends Comparable<C> & Serializable> void fireTimedEvent(final EventType eventType, final Serializable value,
            final C time) throws RemoteException
    {
        fireEvent(new TimedEvent<C>(eventType, value, time, true));
    }

    /**
     * Transmit an event with no payload object to all interested listeners.
     * @param eventType the eventType of the event
     * @throws RemoteException on network failure
     */
    default void fireUnverifiedEvent(final EventType eventType) throws RemoteException
    {
        fireEvent(new Event(eventType, null, false));
    }

    /**
     * Transmit a time-stamped event with a no payload object to all interested listeners.
     * @param eventType the eventType of the event.
     * @param time a time stamp for the event
     * @param <C> the comparable type to indicate the time when the event is fired
     * @throws RemoteException on network failure
     */
    default <C extends Comparable<C> & Serializable> void fireUnverifiedTimedEvent(final EventType eventType, final C time)
            throws RemoteException
    {
        fireEvent(new TimedEvent<C>(eventType, null, time, false));
    }

    /**
     * Transmit an event with a serializable object as payload to all interested listeners.
     * @param eventType the eventType of the event
     * @param value the object sent with the event
     * @throws RemoteException on network failure
     */
    default void fireUnverifiedEvent(final EventType eventType, final Serializable value) throws RemoteException
    {
        fireEvent(new Event(eventType, value, false));
    }

    /**
     * Transmit a time-stamped event with a Serializable object (payload) to all interested listeners.
     * @param eventType the eventType of the event.
     * @param value the payload sent with the event
     * @param time a time stamp for the event
     * @param <C> the comparable type to indicate the time when the event is fired
     * @throws RemoteException on network failure
     */
    default <C extends Comparable<C> & Serializable> void fireUnverifiedTimedEvent(final EventType eventType,
            final Serializable value, final C time) throws RemoteException
    {
        fireEvent(new TimedEvent<C>(eventType, value, time, false));
    }

}
