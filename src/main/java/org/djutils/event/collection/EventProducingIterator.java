package org.djutils.event.collection;

import java.io.Serializable;
import java.util.Iterator;

import org.djutils.event.EventType;
import org.djutils.event.LocalEventProducer;
import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;

/**
 * The EventProducingIterator provides an iterator embedding the Iterator, which fires an event when an object has been removed.
 * Note that one does not have to subscribe specifically to the events of the EventProducingIterator, as the EventProducing
 * collection subscribes to the EventProducingIterator's remove events and fires these again to its subscribers.
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
 * @param <T> the type of elements to iterate on
 */
public class EventProducingIterator<T> extends LocalEventProducer implements Iterator<T>, Serializable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20191230L;

    /** OBJECT_REMOVED_EVENT is fired on removal of entries. */
    public static final EventType OBJECT_REMOVED_EVENT = new EventType("OBJECT_REMOVED_EVENT", MetaData.NO_META_DATA);

    /** our parent iterator. */
    private Iterator<T> wrappedIterator = null;

    /**
     * constructs a new EventProducingIterator, embedding the parent Iterator.
     * @param wrappedIterator Iterator&lt;T&gt;; parent.
     */
    public EventProducingIterator(final Iterator<T> wrappedIterator)
    {
        Throw.whenNull(wrappedIterator, "parent cannot be null");
        this.wrappedIterator = wrappedIterator;
    }

    @Override
    public boolean hasNext()
    {
        return getWrappedIterator().hasNext();
    }

    @Override
    public T next()
    {
        return getWrappedIterator().next();
    }

    @Override
    public void remove()
    {
        getWrappedIterator().remove();
        fireEvent(OBJECT_REMOVED_EVENT);
    }

    /**
     * Return the embedded iterator.
     * @return parent Iterator&lt;T&gt;; the embedded iterator
     */
    protected Iterator<T> getWrappedIterator()
    {
        return this.wrappedIterator;
    }

}
