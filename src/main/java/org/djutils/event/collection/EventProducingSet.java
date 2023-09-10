package org.djutils.event.collection;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.event.reference.ReferenceType;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * The Event producing set provides a set to which one can subscribe interest in entry changes. This class does not keep track
 * of changes which take place indirectly. One is for example not notified on <code>map.iterator.remove()</code>. A listener
 * must subscribe to the iterator individually.
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
 * @param <E> the type of elements in the set
 */
public class EventProducingSet<E> extends LocalEventProducer implements EventListener, Set<E>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20191230L;

    /** OBJECT_ADDED_EVENT is fired on new entries. */
    public static final EventType OBJECT_ADDED_EVENT =
            new EventType("OBJECT_ADDED_EVENT", new MetaData("Size of the set after add", "Size of the set",
                    new ObjectDescriptor("Size of the set after add", "Size of the set", Integer.class)));

    /** OBJECT_REMOVED_EVENT is fired on removal of entries. */
    public static final EventType OBJECT_REMOVED_EVENT =
            new EventType("OBJECT_REMOVED_EVENT", new MetaData("Size of the set after remove", "Size of the set",
                    new ObjectDescriptor("Size of the set after remove", "Size of the set", Integer.class)));

    /** OBJECT_CHANGED_EVENT is fired on change of one or more entries. */
    public static final EventType OBJECT_CHANGED_EVENT =
            new EventType("OBJECT_CHANGED_EVENT", new MetaData("Size of the set after change", "Size of the set",
                    new ObjectDescriptor("Size of the set after change", "Size of the set", Integer.class)));

    /** the wrapped set. */
    private Set<E> wrappedSet = null;

    /**
     * Constructs a new EventProducingSet.
     * @param wrappedSet Set&lt;E&gt;; the embedded set.
     */
    public EventProducingSet(final Set<E> wrappedSet)
    {
        Throw.whenNull(wrappedSet, "wrappedSet cannot be null");
        this.wrappedSet = wrappedSet;
    }

    /** {@inheritDoc} */
    @Override
    public int size()
    {
        return this.wrappedSet.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty()
    {
        return this.wrappedSet.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void clear()
    {
        int nr = this.wrappedSet.size();
        this.wrappedSet.clear();
        if (nr != this.wrappedSet.size())
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedSet.size());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean add(final E o)
    {
        boolean changed = this.wrappedSet.add(o);
        if (changed)
        {
            fireEvent(OBJECT_ADDED_EVENT, this.wrappedSet.size());
        }
        else
        {
            fireEvent(OBJECT_CHANGED_EVENT, this.wrappedSet.size());
        }
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addAll(final Collection<? extends E> c)
    {
        boolean changed = this.wrappedSet.addAll(c);
        if (changed)
        {
            fireEvent(OBJECT_ADDED_EVENT, this.wrappedSet.size());
        }
        else
        {
            if (!c.isEmpty())
            {
                fireEvent(OBJECT_CHANGED_EVENT, this.wrappedSet.size());
            }
        }
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(final Object o)
    {
        return this.wrappedSet.contains(o);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return this.wrappedSet.containsAll(c);
    }

    /** {@inheritDoc} */
    @Override
    public EventProducingIterator<E> iterator()
    {
        EventProducingIterator<E> iterator = new EventProducingIterator<E>(this.wrappedSet.iterator());
        // WEAK reference as an iterator is usually local and should be eligible for garbage collection
        iterator.addListener(this, EventProducingIterator.OBJECT_REMOVED_EVENT, ReferenceType.WEAK);
        return iterator;
    }

    /** {@inheritDoc} */
    @Override
    public void notify(final Event event) throws RemoteException
    {
        // pass through the OBJECT_REMOVED_EVENT from the iterator
        if (event.getType().equals(EventProducingIterator.OBJECT_REMOVED_EVENT))
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedSet.size());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean remove(final Object o)
    {
        boolean changed = this.wrappedSet.remove(o);
        if (changed)
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedSet.size());
        }
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeAll(final Collection<?> c)
    {
        boolean changed = this.wrappedSet.removeAll(c);
        if (changed)
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedSet.size());
        }
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    public boolean retainAll(final Collection<?> c)
    {
        boolean changed = this.wrappedSet.retainAll(c);
        if (changed)
        {
            fireEvent(OBJECT_REMOVED_EVENT, this.wrappedSet.size());
        }
        return changed;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] toArray()
    {
        return this.wrappedSet.toArray();
    }

    /** {@inheritDoc} */
    @Override
    public <T> T[] toArray(final T[] a)
    {
        return this.wrappedSet.toArray(a);
    }

}
