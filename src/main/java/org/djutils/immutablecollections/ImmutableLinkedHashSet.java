package org.djutils.immutablecollections;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An immutable wrapper for a LinkedHashSet.
 * <p>
 * Copyright (c) 2016-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * $, initial version May 7, 2016 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this Set
 */
public class ImmutableLinkedHashSet<E> extends ImmutableAbstractSet<E>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /**
     * @param collection the collection to use for the immutable set.
     */
    public ImmutableLinkedHashSet(final Collection<? extends E> collection)
    {
        super(new LinkedHashSet<E>(collection), Immutable.COPY);
    }

    /**
     * @param collection the collection to use for the immutable set.
     * @param copyOrWrap WRAP stores a pointer to the original collection
     */
    public ImmutableLinkedHashSet(final Set<E> collection, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new LinkedHashSet<E>(collection) : collection, copyOrWrap);
    }

    /**
     * @param collection the collection to use for the immutable set.
     */
    public ImmutableLinkedHashSet(final ImmutableAbstractCollection<? extends E> collection)
    {
        super(new LinkedHashSet<E>(collection.getUnderlyingCollection()), Immutable.COPY);
    }

    /**
     * @param set the collection to use for the immutable set.
     * @param copyOrWrap WRAP stores a pointer to the original collection
     */
    public ImmutableLinkedHashSet(final ImmutableAbstractSet<E> set, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new LinkedHashSet<E>(set.getUnderlyingCollection())
                : set.getUnderlyingCollection(), copyOrWrap);
    }

    @Override
    protected Set<E> getUnderlyingCollection()
    {
        return super.getUnderlyingCollection();
    }

    @Override
    public final Set<E> toSet()
    {
        return new LinkedHashSet<E>(getUnderlyingCollection());
    }

    @Override
    public final String toString()
    {
        Set<E> set = getUnderlyingCollection();
        if (null == set)
        {
            return "ImmutableLinkedHashSet []";
        }
        return "ImmutableLinkedHashSet [" + set.toString() + "]";
    }

}
