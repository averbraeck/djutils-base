package org.djutils.immutablecollections;

import java.util.ArrayList;
import java.util.List;

/**
 * A List interface without the methods that can change it. The constructor of the ImmutableList needs to be given an initial
 * List.
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this List
 */
public interface ImmutableList<E> extends ImmutableCollection<E>
{
    /**
     * Returns the element at the specified position in this immutable list.
     * @param index int; index of the element to return
     * @return the element at the specified position in this immutable list
     * @throws IndexOutOfBoundsException if the index is out of range (<code>index &lt; 0 || index &gt;= size()</code>)
     */
    E get(int index);

    /**
     * Returns the index of the first occurrence of the specified element in this immutable list, or -1 if this immutable list
     * does not contain the element. More formally, returns the lowest index <code>i</code> such that
     * <code>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</code>, or -1 if there is no such index.
     * @param o Object; element to search for
     * @return the index of the first occurrence of the specified element in this immutable list, or -1 if this immutable list
     *         does not contain the element
     * @throws ClassCastException if the type of the specified element is incompatible with this immutable list
     * @throws NullPointerException if the specified element is null and this immutable list does not permit null elements
     */
    int indexOf(Object o);

    /**
     * Returns the index of the last occurrence of the specified element in this immutable list, or -1 if this immutable list
     * does not contain the element. More formally, returns the highest index <code>i</code> such that
     * <code>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</code>, or -1 if there is no such index.
     * @param o Object; element to search for
     * @return the index of the last occurrence of the specified element in this immutable list, or -1 if this immutable list
     *         does not contain the element
     * @throws ClassCastException if the type of the specified element is incompatible with this immutable list
     * @throws NullPointerException if the specified element is null and this immutable list does not permit null elements
     */
    int lastIndexOf(Object o);

    /**
     * Returns a safe, immutable copy of the portion of this immutable list between the specified <code>fromIndex</code>,
     * inclusive, and <code>toIndex</code>, exclusive. (If <code>fromIndex</code> and <code>toIndex</code> are equal, the
     * returned immutable list is empty).
     * @param fromIndex int; low endpoint (inclusive) of the subList
     * @param toIndex int; high endpoint (exclusive) of the subList
     * @return a view of the specified range within this immutable list
     * @throws IndexOutOfBoundsException for an illegal endpoint index value (<code>fromIndex &lt; 0 || toIndex &gt; size ||
     *         fromIndex &gt; toIndex</code>)
     */
    ImmutableList<E> subList(int fromIndex, int toIndex);

    /**
     * Returns a modifiable copy of this immutable list.
     * @return a modifiable copy of this immutable list.
     */
    List<E> toList();

    /**
     * Force to redefine equals for the implementations of immutable collection classes.
     * @param obj Object; the object to compare this collection with
     * @return whether the objects are equal
     */
    @Override
    boolean equals(Object obj);

    /**
     * Force to redefine hashCode for the implementations of immutable collection classes.
     * @return the calculated hashCode
     */
    @Override
    int hashCode();

    /**
     * Force to redefine toString.
     * @return String; a description of this immutable list
     */
    @Override
    String toString();

    /**
     * Return an empty ImmutableList, backed by a ArrayList.
     * @param <E> the value type
     * @return ImmutableList&lt;K, V&gt;; an empty ImmutableList
     */
    static <E> ImmutableList<E> of()
    {
        return new ImmutableArrayList<>(new ArrayList<E>(), Immutable.WRAP);
    }

    /**
     * Return an ImmutableList with 1 entry, backed by a ArrayList.
     * @param <E> the value type
     * @param v1 E; value 1
     * @return ImmutableList&lt;K, V&gt;; an ImmutableList with 1 entry, backed by a ArrayList
     */
    static <E> ImmutableList<E> of(final E v1)
    {
        ArrayList<E> list = new ArrayList<>();
        list.add(v1);
        return new ImmutableArrayList<>(list, Immutable.WRAP);
    }

    /**
     * Return an ImmutableList with 2 entries, backed by a ArrayList.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @return ImmutableList&lt;K, V&gt;; an ImmutableList with 2 entries, backed by a ArrayList
     */
    static <E> ImmutableList<E> of(final E v1, final E v2)
    {
        ArrayList<E> list = new ArrayList<>();
        list.add(v1);
        list.add(v2);
        return new ImmutableArrayList<>(list, Immutable.WRAP);
    }

    /**
     * Return an ImmutableList with 3 entries, backed by a ArrayList.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @param v3 E; value 3
     * @return ImmutableList&lt;K, V&gt;; an ImmutableList with 3 entries, backed by a ArrayList
     */
    static <E> ImmutableList<E> of(final E v1, final E v2, final E v3)
    {
        ArrayList<E> list = new ArrayList<>();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        return new ImmutableArrayList<>(list, Immutable.WRAP);
    }

    /**
     * Return an ImmutableList with 4 entries, backed by a ArrayList.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @param v3 E; value 3
     * @param v4 E; value 4
     * @return ImmutableList&lt;K, V&gt;; an ImmutableList with 4 entries, backed by a ArrayList
     */
    static <E> ImmutableList<E> of(final E v1, final E v2, final E v3, final E v4)
    {
        ArrayList<E> list = new ArrayList<>();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        list.add(v4);
        return new ImmutableArrayList<>(list, Immutable.WRAP);
    }

    /**
     * Return an ImmutableList with 5 or more entries, backed by a ArrayList.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @param v3 E; value 3
     * @param v4 E; value 4
     * @param v5 E; value 5
     * @param vn E...; values 6 and beyond
     * @return ImmutableList&lt;K, V&gt;; an ImmutableList with 5 or more entries, backed by a ArrayList
     */
    @SuppressWarnings("unchecked")
    static <E> ImmutableList<E> of(final E v1, final E v2, final E v3, final E v4, final E v5, final E... vn)
    {
        ArrayList<E> list = new ArrayList<>();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        list.add(v4);
        list.add(v5);
        for (E v : vn)
        {
            list.add(v);
        }
        return new ImmutableArrayList<>(list, Immutable.WRAP);
    }

}
