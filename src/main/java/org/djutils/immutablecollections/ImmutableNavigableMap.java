package org.djutils.immutablecollections;

import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableMap;

/**
 * A {@link ImmutableSortedMap} extended with navigation methods reporting closest matches for given search targets. Methods
 * {@code lowerKey}, {@code floorKey}, {@code ceilingKey}, and {@code higherKey} return keys respectively less than, less than
 * or equal, greater than or equal, and greater than a given key, returning {@code null} if there is no such key. All methods
 * from java.util.NavigableMap that can change the map have been left out.
 * <p>
 * Copyright (c) 2016-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <K> the key type of content of this Map
 * @param <V> the value type of content of this Map
 */
public interface ImmutableNavigableMap<K, V> extends ImmutableSortedMap<K, V>
{
    /**
     * Returns a modifiable copy of this immutable map.
     * @return a modifiable copy of this immutable map.
     */
    @Override
    NavigableMap<K, V> toMap();

    /**
     * Returns a {@link ImmutableSortedSet} view of the keys contained in this map.
     * @return an immutable sorted set of the keys contained in this map
     */
    @Override
    ImmutableSortedSet<K> keySet();

    /**
     * Returns the greatest key in this immutable map strictly less than the given key, or {@code null} if there is no such key.
     * @param e the value to match
     * @return the greatest key less than {@code e}, or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared with the keys currently in the immutable map
     * @throws NullPointerException if the specified key is null and this immutable map does not permit null keys
     */
    K lowerKey(K e);

    /**
     * Returns the greatest key in this immutable map less than or equal to the given key, or {@code null} if there is no such
     * key.
     * @param e the value to match
     * @return the greatest key less than or equal to {@code e}, or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared with the keys currently in the immutable map
     * @throws NullPointerException if the specified key is null and this immutable map does not permit null keys
     */
    K floorKey(K e);

    /**
     * Returns the least key in this immutable map greater than or equal to the given key, or {@code null} if there is no such
     * key.
     * @param e the value to match
     * @return the least key greater than or equal to {@code e}, or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared with the keys currently in the immutable map
     * @throws NullPointerException if the specified key is null and this immutable map does not permit null keys
     */
    K ceilingKey(K e);

    /**
     * Returns the least key in this immutable map strictly greater than the given key, or {@code null} if there is no such key.
     * @param e the value to match
     * @return the least key greater than {@code e}, or {@code null} if there is no such key
     * @throws ClassCastException if the specified key cannot be compared with the keys currently in the immutable map
     * @throws NullPointerException if the specified key is null and this immutable map does not permit null keys
     */
    K higherKey(K e);

    /**
     * Returns a reverse order view of the keys contained in this immutable map.
     * <p>
     * The returned immutable map has an ordering equivalent to
     * <code>{@link Collections#reverseOrder(Comparator) Collections.reverseOrder}(comparator())</code>. The expression
     * {@code s.descendingMap().descendingMap()} returns a view of {@code s} essentially equivalent to {@code s}.
     * @return a reverse order view of this immutable map
     */
    ImmutableNavigableMap<K, V> descendingMap();

    /**
     * Returns a view of the portion of this immutable map whose keys range from {@code fromKey} to {@code toKey}. If
     * {@code fromKey} and {@code toKey} are equal, the returned immutable map is empty unless {@code fromInclusive} and
     * {@code toInclusive} are both true.
     * @param fromKey low endpoint of the returned immutable map
     * @param fromInclusive {@code true} if the low endpoint is to be included in the returned view
     * @param toKey high endpoint of the returned immutable map
     * @param toInclusive {@code true} if the high endpoint is to be included in the returned view
     * @return a view of the portion of this immutable map whose keys range from {@code fromKey}, inclusive, to {@code toKey},
     *         exclusive
     * @throws ClassCastException if {@code fromKey} and {@code toKey} cannot be compared to one another using this immutable
     *             map's comparator (or, if the immutable map has no comparator, using natural ordering). Implementations may,
     *             but are not required to, throw this exception if {@code fromKey} or {@code toKey} cannot be compared to keys
     *             currently in the immutable map.
     * @throws NullPointerException if {@code fromKey} or {@code toKey} is null and this immutable map does not permit null keys
     * @throws IllegalArgumentException if {@code fromKey} is greater than {@code toKey}; or if this immutable map itself has a
     *             restricted range, and {@code fromKey} or {@code toKey} lies outside the bounds of the range.
     */
    ImmutableNavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive);

    /**
     * Returns a view of the portion of this immutable map whose keys are less than (or equal to, if {@code inclusive} is true)
     * {@code toKey}.
     * @param toKey high endpoint of the returned immutable map
     * @param inclusive {@code true} if the high endpoint is to be included in the returned view
     * @return a view of the portion of this immutable map whose keys are less than (or equal to, if {@code inclusive} is true)
     *         {@code toKey}
     * @throws ClassCastException if {@code toKey} is not compatible with this immutable map's comparator (or, if the immutable
     *             map has no comparator, if {@code toKey} does not implement {@link Comparable}). Implementations may, but are
     *             not required to, throw this exception if {@code toKey} cannot be compared to keys currently in the immutable
     *             map.
     * @throws NullPointerException if {@code toKey} is null and this immutable map does not permit null keys
     * @throws IllegalArgumentException if this immutable map itself has a restricted range, and {@code toKey} lies outside the
     *             bounds of the range
     */
    ImmutableNavigableMap<K, V> headMap(K toKey, boolean inclusive);

    /**
     * Returns a view of the portion of this immutable map whose keys are greater than (or equal to, if {@code inclusive} is
     * true) {@code fromKey}.
     * @param fromKey low endpoint of the returned immutable map
     * @param inclusive {@code true} if the low endpoint is to be included in the returned view
     * @return a view of the portion of this immutable map whose keys are greater than or equal to {@code fromKey}
     * @throws ClassCastException if {@code fromKey} is not compatible with this immutable map's comparator (or, if the
     *             immutable map has no comparator, if {@code fromKey} does not implement {@link Comparable}). Implementations
     *             may, but are not required to, throw this exception if {@code fromKey} cannot be compared to keys currently in
     *             the immutable map.
     * @throws NullPointerException if {@code fromKey} is null and this immutable map does not permit null keys
     * @throws IllegalArgumentException if this immutable map itself has a restricted range, and {@code fromKey} lies outside
     *             the bounds of the range
     */
    ImmutableNavigableMap<K, V> tailMap(K fromKey, boolean inclusive);

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

}
