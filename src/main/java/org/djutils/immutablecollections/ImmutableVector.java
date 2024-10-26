package org.djutils.immutablecollections;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * An immutable wrapper for a Vector.
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this ImmutableVector
 */
public class ImmutableVector<E> extends ImmutableAbstractList<E>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /**
     * @param collection Collection&lt;? extends E&gt;; the collection to use for the immutable vector.
     */
    public ImmutableVector(final Collection<? extends E> collection)
    {
        super(new Vector<E>(collection), Immutable.COPY);
    }

    /**
     * @param vector Vector&lt;E&gt;; the vector to use for the immutable vector.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableVector(final Vector<E> vector, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new Vector<E>(vector) : vector, copyOrWrap);
    }

    /**
     * @param collection ImmutableAbstractCollection&lt;? extends E&gt;; the immutable collection to use for the immutable
     *            vector.
     */
    public ImmutableVector(final ImmutableAbstractCollection<? extends E> collection)
    {
        super(new Vector<E>(collection.getUnderlyingCollection()), Immutable.COPY);
    }

    /**
     * @param vector ImmutableVector&lt;E&gt;; the vector to use for the immutable vector.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableVector(final ImmutableVector<E> vector, final Immutable copyOrWrap)
    {
        this(copyOrWrap == Immutable.COPY ? new Vector<E>(vector.getUnderlyingCollection()) : vector.getUnderlyingCollection(),
                copyOrWrap);
    }

    @Override
    public final List<E> toList()
    {
        return new Vector<E>(getUnderlyingCollection());
    }

    /**
     * Returns a modifiable copy of this immutable vector.
     * @return a modifiable copy of this immutable vector.
     */
    public final Vector<E> toVector()
    {
        return new Vector<E>(getUnderlyingCollection());
    }

    @Override
    protected Vector<E> getUnderlyingCollection()
    {
        return (Vector<E>) super.getUnderlyingCollection();
    }

    @Override
    public final ImmutableList<E> subList(final int fromIndex, final int toIndex)
    {
        return new ImmutableVector<E>(getUnderlyingCollection().subList(fromIndex, toIndex));
    }

    /**
     * Copies the components of this immutable vector into the specified array. The item at index {@code k} in this immutable
     * vector is copied into component {@code k} of {@code anArray}.
     * @param anArray Object[]; the array into which the components get copied
     * @throws NullPointerException if the given array is null
     * @throws IndexOutOfBoundsException if the specified array is not large enough to hold all the components of this immutable
     *             vector
     * @throws ArrayStoreException if a component of this immutable vector is not of a runtime type that can be stored in the
     *             specified array
     * @see #toArray(Object[])
     */
    public final void copyInto(final Object[] anArray)
    {
        getUnderlyingCollection().copyInto(anArray);
    }

    /**
     * Returns the current capacity of this immutable vector.
     * @return the current capacity of this immutable vector.
     */
    public final int capacity()
    {
        return getUnderlyingCollection().capacity();
    }

    /**
     * Returns an enumeration of the components of this vector. The returned {@code Enumeration} object will generate all items
     * in this vector. The first item generated is the item at index {@code 0}, then the item at index {@code 1}, and so on.
     * @return an enumeration of the components of this vector
     * @see Iterator
     */
    public final Enumeration<E> elements()
    {
        return getUnderlyingCollection().elements();
    }

    /**
     * Returns the index of the first occurrence of the specified element in this immutable vector, searching forwards from
     * {@code index}, or returns -1 if the element is not found. More formally, returns the lowest index {@code i} such that
     * <code>(i&nbsp;&gt;=&nbsp;index&nbsp;&amp;&amp;&nbsp;(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i))))</code>,
     * or -1 if there is no such index.
     * @param o Object; element to search for
     * @param index int; index to start searching from
     * @return the index of the first occurrence of the element in this immutable vector at position {@code index} or later in
     *         the vector; {@code -1} if the element is not found.
     * @throws IndexOutOfBoundsException if the specified index is negative
     * @see Object#equals(Object)
     */
    public final int indexOf(final Object o, final int index)
    {
        return getUnderlyingCollection().indexOf(o, index);
    }

    /**
     * Returns the index of the last occurrence of the specified element in this immutable vector, searching backwards from
     * {@code index}, or returns -1 if the element is not found. More formally, returns the highest index {@code i} such that
     * <code>(i&nbsp;&lt;=&nbsp;index&nbsp;&amp;&amp;&nbsp;(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i))))</code>,
     * or -1 if there is no such index.
     * @param o Object; element to search for
     * @param index int; index to start searching backwards from
     * @return the index of the last occurrence of the element at position less than or equal to {@code index} in this immutable
     *         vector; -1 if the element is not found.
     * @throws IndexOutOfBoundsException if the specified index is greater than or equal to the current size of this immutable
     *             vector
     */
    public final int lastIndexOf(final Object o, final int index)
    {
        return getUnderlyingCollection().lastIndexOf(o, index);
    }

    /**
     * Returns the component at the specified index.
     * <p>
     * This method is identical in functionality to the {@link #get(int)} method (which is part of the {@link List} interface).
     * @param index int; an index into this immutable vector
     * @return the component at the specified index
     * @throws ArrayIndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
     */
    public final E elementAt(final int index)
    {
        return getUnderlyingCollection().elementAt(index);
    }

    /**
     * Returns the first component (the item at index {@code 0}) of this immutable vector.
     * @return the first component of this immutable vector
     * @throws NoSuchElementException if this immutable vector has no components
     */
    public final E firstElement()
    {
        return getUnderlyingCollection().firstElement();
    }

    /**
     * Returns the last component of the immutable vector.
     * @return the last component of the immutable vector, i.e., the component at index <code>size()&nbsp;-&nbsp;1</code>.
     * @throws NoSuchElementException if this immutable vector is empty
     */
    public final E lastElement()
    {
        return getUnderlyingCollection().lastElement();
    }

    @Override
    public final String toString()
    {
        List<E> list = getUnderlyingCollection();
        if (null == list)
        {
            return "ImmutableVector []";
        }
        return "ImmutableVector [" + list.toString() + "]";
    }

}
