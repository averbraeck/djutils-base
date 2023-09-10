package org.djutils.immutablecollections;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.djutils.exceptions.Throw;

/**
 * An abstract base class for an immutable wrapper for a List.
 * <p>
 * Copyright (c) 2016-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this List
 */
public abstract class ImmutableAbstractList<E> extends ImmutableAbstractCollection<E> implements ImmutableList<E>, RandomAccess
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /** the list that is wrapped, without giving access to methods that can change it. */
    private final List<E> list;

    /**
     * Construct an abstract immutable list. Make sure that the argument is a safe copy of the list or pointer to the list of
     * the right type! Copying does not take place in the Abstract class!
     * @param list List&lt;E&gt;; a safe copy of the list, or pointer to the list to use for the immutable list
     * @param copyOrWrap Immutable; indicate whether the immutable is a copy or a wrap
     */
    protected ImmutableAbstractList(final List<E> list, final Immutable copyOrWrap)
    {
        super(copyOrWrap);
        Throw.whenNull(list, "the list argument cannot be null");
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public final Collection<E> toCollection()
    {
        return toList();
    }

    /** {@inheritDoc} */
    @Override
    protected List<E> getUnderlyingCollection()
    {
        return this.list;
    }

    /** {@inheritDoc} */
    @Override
    public final int size()
    {
        return this.list.size();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean contains(final Object o)
    {
        return this.list.contains(o);
    }

    /** {@inheritDoc} */
    @Override
    public final int indexOf(final Object o)
    {
        return this.list.indexOf(o);
    }

    /** {@inheritDoc} */
    @Override
    public final int lastIndexOf(final Object o)
    {
        return this.list.lastIndexOf(o);
    }

    /** {@inheritDoc} */
    @Override
    public final Object[] toArray()
    {
        return this.list.toArray();
    }

    /** {@inheritDoc} */
    @Override
    public final <T> T[] toArray(final T[] a)
    {
        return this.list.toArray(a);
    }

    /** {@inheritDoc} */
    @Override
    public final E get(final int index)
    {
        return this.list.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public final ImmutableIterator<E> iterator()
    {
        return new ImmutableIterator<E>(this.list.iterator());
    }

    /** {@inheritDoc} */
    @Override
    public final void forEach(final Consumer<? super E> action)
    {
        this.list.forEach(action);
    }

    /** {@inheritDoc} */
    @Override
    public final Spliterator<E> spliterator()
    {
        return this.list.spliterator();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean containsAll(final Collection<?> c)
    {
        return this.list.containsAll(c);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean containsAll(final ImmutableCollection<?> c)
    {
        return this.list.containsAll(c.toCollection());
    }

    /** {@inheritDoc} */
    @Override
    public final Stream<E> stream()
    {
        return this.list.stream();
    }

    /** {@inheritDoc} */
    @Override
    public final Stream<E> parallelStream()
    {
        return this.list.parallelStream();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isWrap()
    {
        return this.copyOrWrap.isWrap();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.list == null) ? 0 : this.list.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({"checkstyle:designforextension", "checkstyle:needbraces"})
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImmutableAbstractList<?> other = (ImmutableAbstractList<?>) obj;
        if (this.list == null)
        {
            if (other.list != null)
                return false;
        }
        else if (!this.list.equals(other.list))
            return false;
        return true;
    }

}
