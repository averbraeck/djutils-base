package org.djutils.immutablecollections;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A SortedSet interface without the methods that can change it. The return values of subSet, tailSet and headSet are all
 * ImmutableSortedSets.
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
public interface ImmutableSortedSet<E> extends ImmutableSet<E>
{
    /**
     * Returns a modifiable copy of this immutable set.
     * @return a modifiable copy of this immutable set.
     */
    @Override
    SortedSet<E> toSet();

    /**
     * Returns the comparator used to order the elements in this immutable set, or <code>null</code> if this immutable set uses
     * the {@linkplain Comparable natural ordering} of its elements.
     * @return the comparator used to order the elements in this immutable set, or <code>null</code> if this immutable set uses
     *         the natural ordering of its elements
     */
    Comparator<? super E> comparator();

    /**
     * Returns a view of the portion of this immutable set whose elements range from <code>fromElement</code>, inclusive, to
     * <code>toElement</code>, exclusive. (If <code>fromElement</code> and <code>toElement</code> are equal, the returned
     * immutable set is empty.)
     * <p>
     * The result of this method is a new, immutable sorted set.
     * @param fromElement E; low endpoint (inclusive) of the returned immutable set
     * @param toElement E; high endpoint (exclusive) of the returned immutable set
     * @return a new, immutable sorted set of the portion of this immutable set whose elements range from
     *         <code>fromElement</code>, inclusive, to <code>toElement</code>, exclusive
     * @throws ClassCastException if <code>fromElement</code> and <code>toElement</code> cannot be compared to one another using
     *             this immutable set's comparator (or, if the immutable set has no comparator, using natural ordering).
     *             Implementations may, but are not required to, throw this exception if <code>fromElement</code> or
     *             <code>toElement</code> cannot be compared to elements currently in the immutable set.
     * @throws NullPointerException if <code>fromElement</code> or <code>toElement</code> is null and this immutable set does
     *             not permit null elements
     * @throws IllegalArgumentException if <code>fromElement</code> is greater than <code>toElement</code>; or if this immutable
     *             set itself has a restricted range, and <code>fromElement</code> or <code>toElement</code> lies outside the
     *             bounds of the range
     */
    ImmutableSortedSet<E> subSet(E fromElement, E toElement);

    /**
     * Returns a view of the portion of this immutable set whose elements are strictly less than <code>toElement</code>. The
     * returned immutable set is backed by this immutable set, so changes in the returned immutable set are reflected in this
     * immutable set, and vice-versa. The returned immutable set supports all optional immutable set operations that this
     * immutable set supports.
     * <p>
     * The result of this method is a new, immutable sorted set.
     * @param toElement E; high endpoint (exclusive) of the returned immutable set
     * @return a view of the portion of this immutable set whose elements are strictly less than <code>toElement</code>
     * @throws ClassCastException if <code>toElement</code> is not compatible with this immutable set's comparator (or, if the
     *             immutable set has no comparator, if <code>toElement</code> does not implement {@link Comparable}).
     *             Implementations may, but are not required to, throw this exception if <code>toElement</code> cannot be
     *             compared to elements currently in the immutable set.
     * @throws NullPointerException if <code>toElement</code> is null and this immutable set does not permit null elements
     * @throws IllegalArgumentException if this immutable set itself has a restricted range, and <code>toElement</code> lies
     *             outside the bounds of the range
     */
    ImmutableSortedSet<E> headSet(E toElement);

    /**
     * Returns a view of the portion of this immutable set whose elements are greater than or equal to <code>fromElement</code>.
     * The returned immutable set is backed by this immutable set, so changes in the returned immutable set are reflected in
     * this immutable set, and vice-versa. The returned immutable set supports all optional immutable set operations that this
     * immutable set supports.
     * <p>
     * The result of this method is a new, immutable sorted set.
     * @param fromElement E; low endpoint (inclusive) of the returned immutable set
     * @return a view of the portion of this immutable set whose elements are greater than or equal to <code>fromElement</code>
     * @throws ClassCastException if <code>fromElement</code> is not compatible with this immutable set's comparator (or, if the
     *             immutable set has no comparator, if <code>fromElement</code> does not implement {@link Comparable}).
     *             Implementations may, but are not required to, throw this exception if <code>fromElement</code> cannot be
     *             compared to elements currently in the immutable set.
     * @throws NullPointerException if <code>fromElement</code> is null and this immutable set does not permit null elements
     * @throws IllegalArgumentException if this immutable set itself has a restricted range, and <code>fromElement</code> lies
     *             outside the bounds of the range
     */
    ImmutableSortedSet<E> tailSet(E fromElement);

    /**
     * Returns the first (lowest) element currently in this immutable set.
     * @return the first (lowest) element currently in this immutable set
     * @throws NoSuchElementException if this immutable set is empty
     */
    E first();

    /**
     * Returns the last (highest) element currently in this immutable set.
     * @return the last (highest) element currently in this immutable set
     * @throws NoSuchElementException if this immutable set is empty
     */
    E last();

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
     * Return an empty ImmutableSortedSet, backed by a TreeSet.
     * @param <E> the value type
     * @return ImmutableSortedSet&lt;K, V&gt;; an empty ImmutableSortedSet
     */
    static <E> ImmutableSortedSet<E> of()
    {
        return new ImmutableTreeSet<>(new TreeSet<E>(), Immutable.WRAP);
    }

    /**
     * Return an ImmutableSortedSet with 1 entry, backed by a TreeSet.
     * @param <E> the value type
     * @param v1 E; value 1
     * @return ImmutableSortedSet&lt;K, V&gt;; an ImmutableSortedSet with 1 entry, backed by a TreeSet
     */
    static <E> ImmutableSortedSet<E> of(final E v1)
    {
        TreeSet<E> set = new TreeSet<>();
        set.add(v1);
        return new ImmutableTreeSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSortedSet with 2 entries, backed by a TreeSet.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @return ImmutableSortedSet&lt;K, V&gt;; an ImmutableSortedSet with 2 entries, backed by a TreeSet
     */
    static <E> ImmutableSortedSet<E> of(final E v1, final E v2)
    {
        TreeSet<E> set = new TreeSet<>();
        set.add(v1);
        set.add(v2);
        return new ImmutableTreeSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSortedSet with 3 entries, backed by a TreeSet.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @param v3 E; value 3
     * @return ImmutableSortedSet&lt;K, V&gt;; an ImmutableSortedSet with 3 entries, backed by a TreeSet
     */
    static <E> ImmutableSortedSet<E> of(final E v1, final E v2, final E v3)
    {
        TreeSet<E> set = new TreeSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        return new ImmutableTreeSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSortedSet with 4 entries, backed by a TreeSet.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @param v3 E; value 3
     * @param v4 E; value 4
     * @return ImmutableSortedSet&lt;K, V&gt;; an ImmutableSortedSet with 4 entries, backed by a TreeSet
     */
    static <E> ImmutableSortedSet<E> of(final E v1, final E v2, final E v3, final E v4)
    {
        TreeSet<E> set = new TreeSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        set.add(v4);
        return new ImmutableTreeSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSortedSet with 5 or more entries, backed by a TreeSet.
     * @param <E> the value type
     * @param v1 E; value 1
     * @param v2 E; value 2
     * @param v3 E; value 3
     * @param v4 E; value 4
     * @param v5 E; value 5
     * @param vn E...; values 6 and beyond
     * @return ImmutableSortedSet&lt;K, V&gt;; an ImmutableSortedSet with 5 or more entries, backed by a TreeSet
     */
    @SuppressWarnings("unchecked")
    static <E> ImmutableSortedSet<E> of(final E v1, final E v2, final E v3, final E v4, final E v5, final E... vn)
    {
        TreeSet<E> set = new TreeSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        set.add(v4);
        set.add(v5);
        for (E v : vn)
        {
            set.add(v);
        }
        return new ImmutableTreeSet<>(set, Immutable.WRAP);
    }

}
