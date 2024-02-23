package org.djutils.immutablecollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An immutable wrapper for an ArrayList.
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * $, initial version May 7, 2016 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this List
 */
public class ImmutableArrayList<E> extends ImmutableAbstractList<E>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /**
     * @param collection Collection&lt;? extends E&gt;; the collection to use for the immutable list.
     */
    public ImmutableArrayList(final Collection<? extends E> collection)
    {
        super(new ArrayList<E>(collection), Immutable.COPY);
    }

    /**
     * @param list List&lt;E&gt;; the list to use for the immutable list.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableArrayList(final List<E> list, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new ArrayList<E>(list) : list, copyOrWrap);
    }

    /**
     * @param collection ImmutableAbstractCollection&lt;? extends E&gt;; the collection to use for the immutable list.
     */
    public ImmutableArrayList(final ImmutableAbstractCollection<? extends E> collection)
    {
        super(new ArrayList<E>(collection.getUnderlyingCollection()), Immutable.COPY);
    }

    /**
     * @param list ImmutableAbstractList&lt;E&gt;; the list to use for the immutable list.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableArrayList(final ImmutableAbstractList<E> list, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new ArrayList<E>(list.getUnderlyingCollection()) : list.getUnderlyingCollection(),
                copyOrWrap);
    }

    /** {@inheritDoc} */
    @Override
    public final ArrayList<E> toList()
    {
        return new ArrayList<E>(getUnderlyingCollection());
    }

    /** {@inheritDoc} */
    @Override
    protected List<E> getUnderlyingCollection()
    {
        return super.getUnderlyingCollection();
    }

    /** {@inheritDoc} */
    @Override
    public final ImmutableList<E> subList(final int fromIndex, final int toIndex)
    {
        return new ImmutableArrayList<>(getUnderlyingCollection().subList(fromIndex, toIndex));
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        List<E> list = getUnderlyingCollection();
        if (null == list)
        {
            return "ImmutableArrayList []";
        }
        return "ImmutableArrayList [" + list.toString() + "]";
    }

}
