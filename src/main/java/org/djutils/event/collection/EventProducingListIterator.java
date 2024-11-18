package org.djutils.event.collection;

import java.io.Serializable;
import java.util.ListIterator;

import org.djutils.event.EventType;
import org.djutils.metadata.MetaData;

/**
 * EventProducingListIterator provides an iterator embedding the ListIterator, which fires an event when an object has been
 * removed. Note that one does not have to subscribe specifically to the events of the EventProducingListIterator, as the
 * EventProducing collection subscribes to the EventProducingListIterator's remove events and fires these again to its
 * subscribers.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the type of elements to iterate on
 */
public class EventProducingListIterator<T> extends EventProducingIterator<T> implements ListIterator<T>, Serializable
{
    /** */
    private static final long serialVersionUID = 20191230L;

    /** OBJECT_ADDED_EVENT is fired on adding of entries. */
    public static final EventType OBJECT_ADDED_EVENT = new EventType("OBJECT_ADDED_EVENT", MetaData.EMPTY);

    /** OBJECT_CHANGED_EVENT is fired on changing of entries. */
    public static final EventType OBJECT_CHANGED_EVENT = new EventType("OBJECT_CHANGED_EVENT", MetaData.EMPTY);

    /**
     * constructs a new EventProducingListIterator, embedding the parent ListIterator.
     * @param wrappedIterator embedded iterator.
     */
    public EventProducingListIterator(final ListIterator<T> wrappedIterator)
    {
        super(wrappedIterator);
    }

    @Override
    protected ListIterator<T> getWrappedIterator()
    {
        return (ListIterator<T>) super.getWrappedIterator();
    }

    @Override
    public boolean hasPrevious()
    {
        return getWrappedIterator().hasPrevious();
    }

    @Override
    public T previous()
    {
        return getWrappedIterator().previous();
    }

    @Override
    public int nextIndex()
    {
        return getWrappedIterator().nextIndex();
    }

    @Override
    public int previousIndex()
    {
        return getWrappedIterator().previousIndex();
    }

    @Override
    public void set(final T e)
    {
        getWrappedIterator().set(e);
        fireEvent(OBJECT_CHANGED_EVENT);
    }

    @Override
    public void add(final T e)
    {
        getWrappedIterator().add(e);
        fireEvent(OBJECT_ADDED_EVENT);
    }

}
