package org.djutils.immutablecollections;

import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A Map interface without the methods that can change it. The constructor of the ImmutableMap needs to be given an initial Map.
 * <p>
 * Copyright (c) 2016-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <K> the key type of content of this Map
 * @param <V> the value type of content of this Map
 */
public interface ImmutableMap<K, V> extends Serializable
{
    /**
     * Returns the number of elements in this immutable collection. If this immutable collection contains more than
     * <code>Integer.MAX_VALUE</code> elements, returns <code>Integer.MAX_VALUE</code>.
     * @return the number of elements in this immutable collection
     */
    int size();

    /**
     * Returns <code>true</code> if this immutable collection contains no elements.
     * @return <code>true</code> if this immutable collection contains no elements
     */
    boolean isEmpty();

    /**
     * Returns <code>true</code> if this map contains a mapping for the specified key. More formally, returns <code>true</code>
     * if and only if this map contains a mapping for a key <code>k</code> such that
     * <code>(key==null ? k==null : key.equals(k))</code>. (There can be at most one such mapping.)
     * @param key Object; key whose presence in this map is to be tested
     * @return <code>true</code> if this map contains a mapping for the specified key
     * @throws ClassCastException if the key is of an inappropriate type for this map
     * @throws NullPointerException if the specified key is null and this map does not permit null keys
     */
    boolean containsKey(Object key);

    /**
     * Returns <code>true</code> if this map maps one or more keys to the specified value. More formally, returns
     * <code>true</code> if and only if this map contains at least one mapping to a value <code>v</code> such that
     * <code>(value==null ? v==null : value.equals(v))</code>. This operation will probably require time linear in the map size
     * for most implementations of the <code>Map</code> interface.
     * @param value Object; value whose presence in this map is to be tested
     * @return <code>true</code> if this map maps one or more keys to the specified value
     * @throws ClassCastException if the value is of an inappropriate type for this map
     * @throws NullPointerException if the specified value is null and this map does not permit null values
     */
    boolean containsValue(Object value);

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
     * <p>
     * More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such that
     * {@code (key==null ? k==null : key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     * <p>
     * If this map permits null values, then a return value of {@code null} does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map explicitly maps the key to {@code null}. The
     * {@link #containsKey containsKey} operation may be used to distinguish these two cases.
     * @param key Object; the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
     * @throws ClassCastException if the key is of an inappropriate type for this map
     * @throws NullPointerException if the specified key is null and this map does not permit null keys
     */
    V get(Object key);

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * @return an immutable set of the keys contained in this map
     */
    ImmutableSet<K> keySet();

    /**
     * Returns a {@link Set} view of the entries contained in this map.
     * @return an immutable set of the entries contained in this map
     */
    ImmutableSet<ImmutableEntry<K, V>> entrySet();

    /**
     * Returns a {@link ImmutableCollection} view of the values contained in this map.
     * @return an immutable collection view of the values contained in this map
     */
    ImmutableCollection<V> values();

    /**
     * Returns the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the
     * key. The default implementation makes no guarantees about synchronization or atomicity properties of this method. Any
     * implementation providing atomicity guarantees must override this method and document its concurrency properties.
     * @param key Object; the key whose associated value is to be returned
     * @param defaultValue V; the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the
     *         key
     * @throws ClassCastException if the key is of an inappropriate type for this map
     * @throws NullPointerException if the specified key is null and this map does not permit null keys
     */
    default V getOrDefault(final Object key, final V defaultValue)
    {
        V v = get(key);
        return ((v != null) || containsKey(key)) ? v : defaultValue;
    }

    /**
     * Performs the given action for each entry in this map until all entries have been processed or the action throws an
     * exception. Unless otherwise specified by the implementing class, actions are performed in the order of entry set
     * iteration (if an iteration order is specified.) Exceptions thrown by the action are relayed to the caller. The default
     * implementation makes no guarantees about synchronization or atomicity properties of this method. Any implementation
     * providing atomicity guarantees must override this method and document its concurrency properties.
     * @param action BiConsumer&lt;? super K,? super V&gt;; The action to be performed for each entry
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException if an entry is found to be removed during iteration
     */
    default void forEach(final BiConsumer<? super K, ? super V> action)
    {
        Objects.requireNonNull(action);
        for (ImmutableEntry<K, V> entry : entrySet())
        {
            K k;
            V v;
            try
            {
                k = entry.getKey();
                v = entry.getValue();
            }
            catch (IllegalStateException ise)
            {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    /**
     * Returns a modifiable copy of this immutable map.
     * @return a modifiable copy of this immutable map.
     */
    Map<K, V> toMap();

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
     * Return whether the internal storage is a wrapped pointer to the original map. If true, this means that anyone holding a
     * pointer to this data structure can still change it. The users of the ImmutableMap itself can, however, not make any
     * changes.
     * @return boolean; whether the internal storage is a wrapped pointer to the original map
     */
    boolean isWrap();

    /**
     * Return whether the internal storage is a (shallow) copy of the original map. If true, this means that anyone holding a
     * pointer to the original of the data structure can not change it anymore. Nor can the users of the ImmutableMap itself
     * make any changes.
     * @return boolean; whether the internal storage is a safe copy of the original map
     */
    default boolean isCopy()
    {
        return !isWrap();
    }

    /**
     * Return an empty ImmutableMap, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @return ImmutableMap&lt;K, V&gt;; an empty ImmutableMap
     */
    static <K, V> ImmutableMap<K, V> of()
    {
        return new ImmutableLinkedHashMap<>(new LinkedHashMap<K, V>(), Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 1 entry, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 1 entry, backed by a LinkedHashMap
     */
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 2 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 2 entries, backed by a LinkedHashMap
     */
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 3 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @param k3 K; key 3
     * @param v3 V; value 3
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 3 entries, backed by a LinkedHashMap
     */
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 4 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @param k3 K; key 3
     * @param v3 V; value 3
     * @param k4 K; key 4
     * @param v4 V; value 4
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 4 entries, backed by a LinkedHashMap
     */
    @SuppressWarnings("checkstyle:parameternumber")
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4,
            final V v4)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 5 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @param k3 K; key 3
     * @param v3 V; value 3
     * @param k4 K; key 4
     * @param v4 V; value 4
     * @param k5 K; key 5
     * @param v5 V; value 5
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 5 entries, backed by a LinkedHashMap
     */
    @SuppressWarnings("checkstyle:parameternumber")
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4,
            final V v4, final K k5, final V v5)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 6 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @param k3 K; key 3
     * @param v3 V; value 3
     * @param k4 K; key 4
     * @param v4 V; value 4
     * @param k5 K; key 5
     * @param v5 V; value 5
     * @param k6 K; key 6
     * @param v6 V; value 6
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 6 entries, backed by a LinkedHashMap
     */
    @SuppressWarnings("checkstyle:parameternumber")
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4,
            final V v4, final K k5, final V v5, final K k6, final V v6)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 7 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @param k3 K; key 3
     * @param v3 V; value 3
     * @param k4 K; key 4
     * @param v4 V; value 4
     * @param k5 K; key 5
     * @param v5 V; value 5
     * @param k6 K; key 6
     * @param v6 V; value 6
     * @param k7 K; key 7
     * @param v7 V; value 7
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 7 entries, backed by a LinkedHashMap
     */
    @SuppressWarnings("checkstyle:parameternumber")
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4,
            final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * Return an ImmutableMap with 8 entries, backed by a LinkedHashMap.
     * @param <K> the key type
     * @param <V> the value type
     * @param k1 K; key 1
     * @param v1 V; value 1
     * @param k2 K; key 2
     * @param v2 V; value 2
     * @param k3 K; key 3
     * @param v3 V; value 3
     * @param k4 K; key 4
     * @param v4 V; value 4
     * @param k5 K; key 5
     * @param v5 V; value 5
     * @param k6 K; key 6
     * @param v6 V; value 6
     * @param k7 K; key 7
     * @param v7 V; value 7
     * @param k8 K; key 8
     * @param v8 V; value 8
     * @return ImmutableMap&lt;K, V&gt;; an ImmutableMap with 8 entries, backed by a LinkedHashMap
     */
    @SuppressWarnings("checkstyle:parameternumber")
    static <K, V> ImmutableMap<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4,
            final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8)
    {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return new ImmutableLinkedHashMap<>(map, Immutable.WRAP);
    }

    /**
     * A map entry (key-value pair). The <code>Map.entrySet</code> method returns a collection-view of the map, whose elements
     * are of this class. The <i>only</i> way to obtain a reference to a map entry is from the iterator of this collection-view.
     * These <code>ImmutableMap.ImmutableEntry</code> objects are valid <i>only</i> for the duration of the iteration; more
     * formally, the behavior of a map entry is undefined if the backing map has been modified after the entry was returned by
     * the iterator, except through the <code>setValue</code> operation on the map entry.
     * @param <K> key
     * @param <V> value
     */
    class ImmutableEntry<K, V>
    {
        /** the wrapped entry. */
        private final Entry<K, V> wrappedEntry;

        /**
         * @param wrappedEntry Entry&lt;K,V&gt;; the wrapped entry
         */
        public ImmutableEntry(final Entry<K, V> wrappedEntry)
        {
            this.wrappedEntry = wrappedEntry;
        }

        /**
         * Returns the key corresponding to this entry.
         * @return the key corresponding to this entry
         * @throws IllegalStateException implementations may, but are not required to, throw this exception if the entry has
         *             been removed from the backing map.
         */
        public K getKey()
        {
            return this.wrappedEntry.getKey();
        }

        /**
         * Returns the value corresponding to this entry. If the mapping has been removed from the backing map (by the
         * iterator's <code>remove</code> operation), the results of this call are undefined.
         * @return the value corresponding to this entry
         * @throws IllegalStateException implementations may, but are not required to, throw this exception if the entry has
         *             been removed from the backing map.
         */
        public V getValue()
        {
            return this.wrappedEntry.getValue();
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.wrappedEntry == null) ? 0 : this.wrappedEntry.hashCode());
            return result;
        }

        /** {@inheritDoc} */
        @Override
        @SuppressWarnings("checkstyle:needbraces")
        public boolean equals(final Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ImmutableEntry<?, ?> other = (ImmutableEntry<?, ?>) obj;
            if (this.wrappedEntry == null)
            {
                if (other.wrappedEntry != null)
                    return false;
            }
            else if (!this.wrappedEntry.equals(other.wrappedEntry))
                return false;
            return true;
        }

        /**
         * Returns a comparator that compares {@link ImmutableMap.ImmutableEntry} in natural order on key.
         * <p>
         * The returned comparator is serializable and throws {@link NullPointerException} when comparing an entry with a null
         * key.
         * @param <K> the {@link Comparable} type of then map keys
         * @param <V> the type of the map values
         * @return a comparator that compares {@link ImmutableMap.ImmutableEntry} in natural order on key.
         * @see Comparable
         * @since 1.8
         */
        @SuppressWarnings("unchecked")
        public static <K extends Comparable<? super K>, V> Comparator<ImmutableMap.ImmutableEntry<K, V>> comparingByKey()
        {
            return (Comparator<ImmutableMap.ImmutableEntry<K, V>> & Serializable) (c1, c2) -> c1.getKey()
                    .compareTo(c2.getKey());
        }

        /**
         * Returns a comparator that compares {@link ImmutableMap.ImmutableEntry} in natural order on value.
         * <p>
         * The returned comparator is serializable and throws {@link NullPointerException} when comparing an entry with null
         * values.
         * @param <K> the type of the map keys
         * @param <V> the {@link Comparable} type of the map values
         * @return a comparator that compares {@link ImmutableMap.ImmutableEntry} in natural order on value.
         * @see Comparable
         * @since 1.8
         */
        @SuppressWarnings("unchecked")
        public static <K, V extends Comparable<? super V>> Comparator<ImmutableMap.ImmutableEntry<K, V>> comparingByValue()
        {
            return (Comparator<ImmutableMap.ImmutableEntry<K, V>> & Serializable) (c1, c2) -> c1.getValue()
                    .compareTo(c2.getValue());
        }

        /**
         * Returns a comparator that compares {@link ImmutableMap.ImmutableEntry} by key using the given {@link Comparator}.
         * <p>
         * The returned comparator is serializable if the specified comparator is also serializable.
         * @param <K> the type of the map keys
         * @param <V> the type of the map values
         * @param cmp Comparator&lt;? super K&gt;; the key {@link Comparator}
         * @return a comparator that compares {@link ImmutableMap.ImmutableEntry} by the key.
         * @since 1.8
         */
        @SuppressWarnings("unchecked")
        public static <K, V> Comparator<ImmutableMap.ImmutableEntry<K, V>> comparingByKey(final Comparator<? super K> cmp)
        {
            Objects.requireNonNull(cmp);
            return (Comparator<ImmutableMap.ImmutableEntry<K, V>> & Serializable) (c1, c2) -> cmp.compare(c1.getKey(),
                    c2.getKey());
        }

        /**
         * Returns a comparator that compares {@link ImmutableMap.ImmutableEntry} by value using the given {@link Comparator}.
         * <p>
         * The returned comparator is serializable if the specified comparator is also serializable.
         * @param <K> the type of the map keys
         * @param <V> the type of the map values
         * @param cmp Comparator&lt;? super V&gt;; the value {@link Comparator}
         * @return a comparator that compares {@link ImmutableMap.ImmutableEntry} by the value.
         * @since 1.8
         */
        @SuppressWarnings("unchecked")
        public static <K, V> Comparator<ImmutableMap.ImmutableEntry<K, V>> comparingByValue(final Comparator<? super V> cmp)
        {
            Objects.requireNonNull(cmp);
            return (Comparator<ImmutableMap.ImmutableEntry<K, V>> & Serializable) (c1, c2) -> cmp.compare(c1.getValue(),
                    c2.getValue());
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "ImmutableEntry [wrappedEntry=" + this.wrappedEntry + "]";
        }

    }

    /**
     * Force to redefine toString.
     * @return String; a description of this immutable map
     */
    @Override
    String toString();

}
