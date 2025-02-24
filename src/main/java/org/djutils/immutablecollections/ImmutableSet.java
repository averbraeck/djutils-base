package org.djutils.immutablecollections;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A Set interface without the methods that can change it. The constructor of the ImmutableSet needs to be given an initial Set.
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
public interface ImmutableSet<E> extends ImmutableCollection<E>
{
    /**
     * Returns a modifiable copy of this immutable set.
     * @return a modifiable copy of this immutable set.
     */
    Set<E> toSet();

    /**
     * Force to redefine equals for the implementations of immutable collection classes.
     * @param obj the object to compare this collection with
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
     * @return a description of this immutable set
     */
    @Override
    String toString();

    /**
     * Return an empty ImmutableSet, backed by a LinkedHashSet.
     * @param <E> the value type
     * @return an empty ImmutableSet
     */
    static <E> ImmutableSet<E> of()
    {
        return new ImmutableLinkedHashSet<>(new LinkedHashSet<E>(), Immutable.WRAP);
    }

    /**
     * Return an ImmutableSet with 1 entry, backed by a LinkedHashSet.
     * @param <E> the value type
     * @param v1 value 1
     * @return an ImmutableSet with 1 entry, backed by a LinkedHashSet
     */
    static <E> ImmutableSet<E> of(final E v1)
    {
        LinkedHashSet<E> set = new LinkedHashSet<>();
        set.add(v1);
        return new ImmutableLinkedHashSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSet with 2 entries, backed by a LinkedHashSet.
     * @param <E> the value type
     * @param v1 value 1
     * @param v2 value 2
     * @return an ImmutableSet with 2 entries, backed by a LinkedHashSet
     */
    static <E> ImmutableSet<E> of(final E v1, final E v2)
    {
        LinkedHashSet<E> set = new LinkedHashSet<>();
        set.add(v1);
        set.add(v2);
        return new ImmutableLinkedHashSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSet with 3 entries, backed by a LinkedHashSet.
     * @param <E> the value type
     * @param v1 value 1
     * @param v2 value 2
     * @param v3 value 3
     * @return an ImmutableSet with 3 entries, backed by a LinkedHashSet
     */
    static <E> ImmutableSet<E> of(final E v1, final E v2, final E v3)
    {
        LinkedHashSet<E> set = new LinkedHashSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        return new ImmutableLinkedHashSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSet with 4 entries, backed by a LinkedHashSet.
     * @param <E> the value type
     * @param v1 value 1
     * @param v2 value 2
     * @param v3 value 3
     * @param v4 value 4
     * @return an ImmutableSet with 4 entries, backed by a LinkedHashSet
     */
    static <E> ImmutableSet<E> of(final E v1, final E v2, final E v3, final E v4)
    {
        LinkedHashSet<E> set = new LinkedHashSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        set.add(v4);
        return new ImmutableLinkedHashSet<>(set, Immutable.WRAP);
    }

    /**
     * Return an ImmutableSet with 5 or more entries, backed by a LinkedHashSet.
     * @param <E> the value type
     * @param v1 value 1
     * @param v2 value 2
     * @param v3 value 3
     * @param v4 value 4
     * @param v5 value 5
     * @param vn values 6 and beyond
     * @return an ImmutableSet with 5 or more entries, backed by a LinkedHashSet
     */
    @SuppressWarnings("unchecked")
    static <E> ImmutableSet<E> of(final E v1, final E v2, final E v3, final E v4, final E v5, final E... vn)
    {
        LinkedHashSet<E> set = new LinkedHashSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        set.add(v4);
        set.add(v5);
        for (E v : vn)
        {
            set.add(v);
        }
        return new ImmutableLinkedHashSet<>(set, Immutable.WRAP);
    }
}
