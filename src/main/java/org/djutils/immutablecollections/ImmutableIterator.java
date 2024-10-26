package org.djutils.immutablecollections;

import java.util.Iterator;

/**
 * An immutable iterator over elements, wrapping a "mutable" iterator. The default remove method from the interface will throw
 * an exception.
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the element type
 */
public class ImmutableIterator<E> implements Iterator<E>
{
    /** the wrapped iterator. */
    private final Iterator<E> iterator;

    /**
     * @param iterator Iterator&lt;E&gt;; the iterator to wrap as an immutable iterator.
     */
    public ImmutableIterator(final Iterator<E> iterator)
    {
        this.iterator = iterator;
    }

    @Override
    public final boolean hasNext()
    {
        return this.iterator.hasNext();
    }

    @Override
    public final E next()
    {
        return this.iterator.next();
    }

    @Override
    public final String toString()
    {
        return "ImmutableIterator [iterator=" + this.iterator + "]";
    }

}
