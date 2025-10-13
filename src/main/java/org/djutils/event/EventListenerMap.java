package org.djutils.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djutils.event.reference.Reference;
import org.djutils.exceptions.Throw;

/**
 * The EventListenerMap maps EventTypes on lists of References to EventListeners. The References can be Weak or Strong.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class EventListenerMap
{

    /** The hasMap we map on. */
    private Map<EventType, List<Reference<EventListener>>> map = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Return the size of the EventListenerMap, i.e. the number of EventTypes that are registered.
     * @return the size of the EventListenerMap, i.e. the number of EventTypes that are registered
     */
    public int size()
    {
        return this.map.size();
    }

    /**
     * Clears the EventListenerMap.
     */
    public void clear()
    {
        this.map.clear();
    }

    /**
     * Return whether the EventListenerMap is empty.
     * @return whether the EventListenerMap is empty
     */
    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    /**
     * Return whether the EventListenerMap contains the EventType as a key.
     * @param eventType the EventType key to search for
     * @return whether the EventListenerMap contains the EventType as a key
     */
    public boolean containsKey(final EventType eventType)
    {
        Throw.whenNull(eventType, "Cannot search for a null EventType");
        return this.map.containsKey(eventType);
    }

    /**
     * Return whether the EventListenerMap contains the eventListener as one of the subscribers.
     * @param eventListener the EventListener value to search for
     * @return true if the EventListenerMap contains the eventListener as one of the subscribers; false otherwise
     */
    public boolean containsValue(final EventListener eventListener)
    {
        Throw.whenNull(eventListener, "Cannot search for a null EventListener");
        for (List<Reference<EventListener>> refList : this.map.values())
        {
            for (Reference<EventListener> ref : refList)
            {
                if (eventListener.equals(ref.get()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the EventListenerMap contains the reference to the eventListener as one of the subscribers.
     * @param reference the reference pointer an EventListener to search for
     * @return true if the EventListenerMap contains the reference to the eventListener as one of the subscribers; false
     *         otherwise
     */
    public boolean containsValue(final Reference<EventListener> reference)
    {
        Throw.whenNull(reference, "Cannot search for a null reference");
        for (List<Reference<EventListener>> refList : this.map.values())
        {
            for (Reference<EventListener> ref : refList)
            {
                if (reference.equals(ref))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a safe copy of the collection of lists of references to EventListeners, i.e. all the listeners registered in the
     * map
     * @return a safe copy of the collection of lists of references to EventListeners, i.e. all the listeners registered in the
     *         map
     */
    public Collection<List<Reference<EventListener>>> values()
    {
        Collection<List<Reference<EventListener>>> result = new LinkedHashSet<>();
        for (List<Reference<EventListener>> list : this.map.values())
        {
            result.add(new ArrayList<>(list));
        }
        return result;
    }

    /**
     * Add all entries of the map to the EventListenerMap. The lists of listeners are added as a safe copy, so the list will not
     * be changed when the entries from copied map will be changed.
     * @param m the map with references to event listeners to add to the current EventListenerMap
     */
    public void putAll(final EventListenerMap m)
    {
        Throw.whenNull(m, "Cannot use putAll for a null map");
        for (Map.Entry<EventType, List<Reference<EventListener>>> entry : m.entrySet())
        {
            put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * Returns the Set of Entry types holding pairs of a key (EventType) and a value (List of references to EventListeners for
     * that EventType). Note: this is a map with the real values, so not a safe copy. This entrySet can be used to change the
     * underlying map.
     * @return Set&lt;Map.Entry&lt;EventType, List&lt;Reference&lt;EventListenerInterface&gt;&gt;&gt;&gt;;the Set of Entry types
     *         holding pairs of a key (EventType) and a value (List of references to EventListeners for that EventType). Note:
     *         this is <b>not</b> a safe copy!
     */
    public Set<Map.Entry<EventType, List<Reference<EventListener>>>> entrySet()
    {
        return this.map.entrySet();
    }

    /**
     * Returns a safe copy of the Set of EventTypes for which listeners are registered.
     * @return a safe copy of the Set of EventType keys for which listeners are registered
     */
    public Set<EventType> keySet()
    {
        return new LinkedHashSet<EventType>(this.map.keySet());
    }

    /**
     * Returns the original List of references to EventListeners for the given EventType. Note: this is <b>not</b> a safe copy,
     * so the list is backed by the original data structure and will change when listeners are added or removed. The method will
     * return null when the EventType is not found.
     * @param key the eventType to look up the listeners for
     * @return the List of references to EventListeners for the given EventType, or null when the EventType is not found. Note:
     *         this is <b>not</b> a safe copy.
     */
    public List<Reference<EventListener>> get(final EventType key)
    {
        Throw.whenNull(key, "Cannot use get for a null EventType key");
        return this.map.get(key);
    }

    /**
     * Remove the List of references to EventListeners for the given EventType.
     * @param key the eventType to remove the listeners for
     * @return the removed List of references to EventListeners for the given EventType
     */
    public List<Reference<EventListener>> remove(final EventType key)
    {
        Throw.whenNull(key, "Cannot use remove for a null EventType key");
        return this.map.remove(key);
    }

    /**
     * Add the List of references to EventListeners for the given EventType to the underlying Map. A safe copy will be added, so
     * the original list will not be affected when listeners are removed or added, nor will the underlying map be affected when
     * the provided list is changed.
     * @param key the eventType to store the listeners for
     * @param value the references to EventListeners to store for the given EventType
     * @return the previous List of references to EventListeners for the given EventType, or null when there was no previous
     *         mapping
     */
    public List<Reference<EventListener>> put(final EventType key, final List<Reference<EventListener>> value)
    {
        Throw.whenNull(key, "Cannot use put with a null EventType key");
        Throw.whenNull(value, "Cannot use put with a null List as value");
        return this.map.put(key, new ArrayList<>(value));
    }

}
