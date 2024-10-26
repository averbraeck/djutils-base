package org.djutils.event.collection;

import java.rmi.RemoteException;
import java.util.Collection;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.reference.ReferenceType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * The Event producing collection provides a set to which one can subscribe interest in entry changes. This class does not keep
 * track of changes which take place indirectly. One is for example not notified on <code>map.iterator.remove()</code>. A
 * listener must subscribe to the iterator individually.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> The type of the event producing Collection.
 */
public class EventProducingCollection<T> extends LocalEventProducer implements EventListener, Collection<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20191230L;

    /** OBJECT_ADDED_EVENT is fired on new entries. */
    public static final EventType OBJECT_ADDED_EVENT =
            new EventType("OBJECT_ADDED_EVENT", new MetaData("Size of the collection after add", "Size of the collection",
                    new ObjectDescriptor("Size of the collection after add", "Size of the collection", Integer.class)));

    /** OBJECT_REMOVED_EVENT is fired on removal of entries. */
    public static final EventType OBJECT_REMOVED_EVENT =
            new EventType("OBJECT_REMOVED_EVENT", new MetaData("Size of the collection after remove", "Size of the collection",
                    new ObjectDescriptor("Size of the collection after remove", "Size of the collection", Integer.class)));

    /** OBJECT_CHANGED_EVENT is fired on change of one or more entries. */
    public static final EventType OBJECT_CHANGED_EVENT =
            new EventType("OBJECT_CHANGED_EVENT", new MetaData("Size of the collection after change", "Size of the collection",
                    new ObjectDescriptor("Size of the collection after change", "Size of the collection", Integer.class)));

    /** the wrapped collection. */
    private final Collection<T> wrappedCollection;

    /**
     * constructs a new EventProducingCollection with a local EventProducer.
     * @param wrappedCollection Collection&lt;T&gt;; the wrapped collection.
     */
    public EventProducingCollection(final Collection<T> wrappedCollection)
    {
        Throw.whenNull(wrappedCollection, "wrappedCollection cannot be null");
        this.wrappedCollection = wrappedCollection;
    }

    @Override
    public int size()
    {
        return this.wrappedCollection.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.wrappedCollection.isEmpty();
    }

    @Override
    public void clear()
    {
        int nr = this.wrappedCollection.size();
        this.wrappedCollection.clear();
        if (nr != this.wrappedCollection.size())
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedCollection.size());
        }
    }

    @Override
    public boolean add(final T o)
    {
        boolean changed = this.wrappedCollection.add(o);
        if (changed)
        {
            fireEvent(OBJECT_ADDED_EVENT, this.wrappedCollection.size());
        }
        else
        {
            fireEvent(OBJECT_CHANGED_EVENT, this.wrappedCollection.size());
        }
        return changed;
    }

    @Override
    public boolean addAll(final Collection<? extends T> c)
    {
        boolean changed = this.wrappedCollection.addAll(c);
        if (changed)
        {
            fireEvent(OBJECT_ADDED_EVENT, this.wrappedCollection.size());
        }
        else
        {
            if (!c.isEmpty())
            {
                fireEvent(OBJECT_CHANGED_EVENT, this.wrappedCollection.size());
            }
        }
        return changed;
    }

    @Override
    public boolean contains(final Object o)
    {
        return this.wrappedCollection.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return this.wrappedCollection.containsAll(c);
    }

    @Override
    public EventProducingIterator<T> iterator()
    {
        EventProducingIterator<T> iterator = new EventProducingIterator<T>(this.wrappedCollection.iterator());
        // WEAK reference as an iterator is usually local and should be eligible for garbage collection
        iterator.addListener(this, EventProducingIterator.OBJECT_REMOVED_EVENT, ReferenceType.WEAK);
        return iterator;
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        // pass through the OBJECT_REMOVED_EVENT from the iterator
        if (event.getType().equals(EventProducingIterator.OBJECT_REMOVED_EVENT))
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedCollection.size());
        }
    }

    @Override
    public boolean remove(final Object o)
    {
        boolean changed = this.wrappedCollection.remove(o);
        if (changed)
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedCollection.size());
        }
        return changed;
    }

    @Override
    public boolean removeAll(final Collection<?> c)
    {
        boolean changed = this.wrappedCollection.removeAll(c);
        if (changed)
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedCollection.size());
        }
        return changed;
    }

    @Override
    public boolean retainAll(final Collection<?> c)
    {
        boolean changed = this.wrappedCollection.retainAll(c);
        if (changed)
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedCollection.size());
        }
        return changed;
    }

    @Override
    public Object[] toArray()
    {
        return this.wrappedCollection.toArray();
    }

    @Override
    public <E> E[] toArray(final E[] a)
    {
        return this.wrappedCollection.toArray(a);
    }

}
