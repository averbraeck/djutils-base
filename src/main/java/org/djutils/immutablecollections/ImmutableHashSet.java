package org.djutils.immutablecollections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An immutable wrapper for a HashSet.
 * <p>
 * Copyright (c) 2016-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this Set
 */
public class ImmutableHashSet<E> extends ImmutableAbstractSet<E>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /**
     * Construct a new ImmutableHashSet containing a copy of the provided collection.
     * @param collection Collection&lt;? extends E&gt;; the collection to use for the immutable set.
     */
    public ImmutableHashSet(final Collection<? extends E> collection)
    {
        super(new HashSet<E>(collection), Immutable.COPY);
    }

    /**
     * Construct a new ImmutableHashSet containing the provided set.
     * @param set Set&lt;E&gt;; the set to use for the immutable set.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableHashSet(final Set<E> set, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new HashSet<E>(set) : set, copyOrWrap);
    }

    /**
     * Construct a new ImmutableHashSet containing a copy of the provided Collection.
     * @param collection ImmutableAbstractCollection&lt;? extends E&gt;; the collection to use for the immutable set.
     */
    public ImmutableHashSet(final ImmutableAbstractCollection<? extends E> collection)
    {
        super(new HashSet<E>(collection.getUnderlyingCollection()), Immutable.COPY);
    }

    /**
     * Construct a new ImmutableHashSet containing the provided set.
     * @param set ImmutableAbstractSet&lt;E&gt;; the set to use for the immutable set.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableHashSet(final ImmutableAbstractSet<E> set, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new HashSet<E>(set.getUnderlyingCollection()) : set.getUnderlyingCollection(),
                copyOrWrap);
    }

    /** {@inheritDoc} */
    @Override
    protected Set<E> getUnderlyingCollection()
    {
        return super.getUnderlyingCollection();
    }

    /** {@inheritDoc} */
    @Override
    public final Set<E> toSet()
    {
        return new HashSet<E>(getUnderlyingCollection());
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public String toString()
    {
        Set<E> set = getUnderlyingCollection();
        if (null == set)
        {
            return "ImmutableHashSet []";
        }
        return "ImmutableHashSet [" + set.toString() + "]";
    }

}
