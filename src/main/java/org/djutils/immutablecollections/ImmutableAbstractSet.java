package org.djutils.immutablecollections;

import java.util.Collection;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.djutils.exceptions.Throw;

/**
 * An abstract base class for an immutable wrapper for a Set.
 * <p>
 * Copyright (c) 2016-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this Set
 */
public abstract class ImmutableAbstractSet<E> extends ImmutableAbstractCollection<E> implements ImmutableSet<E>
{
    /** the set that is wrapped, without giving access to methods that can change it. */
    private final Set<E> set;

    /**
     * Construct an abstract immutable set. Make sure that the argument is a safe copy of the set of the right type! Copying
     * does not take place in the Abstract class!
     * @param set a safe copy of the set to use for the immutable set
     * @param copyOrWrap indicate whether the immutable is a copy or a wrap
     */
    protected ImmutableAbstractSet(final Set<E> set, final Immutable copyOrWrap)
    {
        super(copyOrWrap);
        Throw.whenNull(set, "the collection argument cannot be null");
        this.set = set;
    }

    @Override
    public final Collection<E> toCollection()
    {
        return toSet();
    }

    @Override
    protected Set<E> getUnderlyingCollection()
    {
        return this.set;
    }

    @Override
    public final int size()
    {
        return this.set.size();
    }

    @Override
    public final boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    @Override
    public final boolean contains(final Object o)
    {
        return this.set.contains(o);
    }

    @Override
    public final Object[] toArray()
    {
        return this.set.toArray();
    }

    @Override
    public final <T> T[] toArray(final T[] a)
    {
        return this.set.toArray(a);
    }

    @Override
    public final ImmutableIterator<E> iterator()
    {
        return new ImmutableIterator<E>(this.set.iterator());
    }

    @Override
    public final void forEach(final Consumer<? super E> action)
    {
        this.set.forEach(action);
    }

    @Override
    public final Spliterator<E> spliterator()
    {
        return this.set.spliterator();
    }

    @Override
    public final boolean containsAll(final Collection<?> c)
    {
        return this.set.containsAll(c);
    }

    @Override
    public final boolean containsAll(final ImmutableCollection<?> c)
    {
        return this.set.containsAll(c.toCollection());
    }

    @Override
    public final Stream<E> stream()
    {
        return this.set.stream();
    }

    @Override
    public final Stream<E> parallelStream()
    {
        return this.set.parallelStream();
    }

    @Override
    public final boolean isWrap()
    {
        return this.copyOrWrap.isWrap();
    }

    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.set == null) ? 0 : this.set.hashCode());
        return result;
    }

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
        ImmutableAbstractSet<?> other = (ImmutableAbstractSet<?>) obj;
        if (this.set == null)
        {
            if (other.set != null)
                return false;
        }
        else if (!this.set.equals(other.set))
            return false;
        return true;
    }

}
