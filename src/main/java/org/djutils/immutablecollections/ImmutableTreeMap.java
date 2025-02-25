package org.djutils.immutablecollections;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An immutable wrapper for a TreeMap.
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
public class ImmutableTreeMap<K, V> extends ImmutableAbstractMap<K, V> implements ImmutableNavigableMap<K, V>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /** the cached keySet. */
    private ImmutableSortedSet<K> cachedKeySet = null;

    /** the cached entrySet. */
    private ImmutableSortedSet<ImmutableEntry<K, V>> cachedEntrySet = null;

    /**
     * @param sortedMap the map to use for the immutable map.
     */
    public ImmutableTreeMap(final Map<K, V> sortedMap)
    {
        super(new TreeMap<K, V>(sortedMap), Immutable.COPY);
    }

    /**
     * @param map the map to use for the immutable map.
     * @param copyOrWrap WRAP stores a pointer to the original collection
     */
    public ImmutableTreeMap(final NavigableMap<K, V> map, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new TreeMap<K, V>(map) : map, copyOrWrap);
    }

    /**
     * @param immutableMap the map to use for the immutable map.
     */
    public ImmutableTreeMap(final ImmutableAbstractMap<K, V> immutableMap)
    {
        super(new TreeMap<K, V>(immutableMap.getUnderlyingMap()), Immutable.COPY);
    }

    /**
     * @param immutableTreeMap the map to use for the immutable map.
     * @param copyOrWrap WRAP stores a pointer to the original collection
     */
    public ImmutableTreeMap(final ImmutableTreeMap<K, V> immutableTreeMap, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new TreeMap<K, V>(immutableTreeMap.getUnderlyingMap())
                : immutableTreeMap.getUnderlyingMap(), copyOrWrap);
    }

    @Override
    protected final NavigableMap<K, V> getUnderlyingMap()
    {
        return (NavigableMap<K, V>) super.getUnderlyingMap();
    }

    @Override
    public final NavigableMap<K, V> toMap()
    {
        return new TreeMap<K, V>(super.getUnderlyingMap());
    }

    @Override
    public final ImmutableSortedSet<K> keySet()
    {
        if (this.cachedKeySet == null)
        {
            NavigableSet<K> immutableKeySet = new TreeSet<>(getUnderlyingMap().comparator());
            immutableKeySet.addAll(getUnderlyingMap().keySet());
            this.cachedKeySet = new ImmutableTreeSet<>(immutableKeySet, Immutable.WRAP);
        }
        return this.cachedKeySet;
    }

    @Override
    public ImmutableSortedSet<ImmutableEntry<K, V>> entrySet()
    {
        if (this.cachedEntrySet == null)
        {
            NavigableSet<ImmutableEntry<K, V>> immutableEntrySet = new TreeSet<>(new Comparator<ImmutableEntry<K, V>>()
            {
                @SuppressWarnings("unchecked")
                @Override
                public int compare(final ImmutableEntry<K, V> o1, final ImmutableEntry<K, V> o2)
                {
                    return ((Comparable<K>) o1.getKey()).compareTo(o2.getKey());
                }

            });
            for (Entry<K, V> entry : getUnderlyingMap().entrySet())
            {
                immutableEntrySet.add(new ImmutableEntry<>(entry));
            }
            this.cachedEntrySet = new ImmutableTreeSet<>(immutableEntrySet, Immutable.WRAP);
        }
        return this.cachedEntrySet;
    }

    @Override
    public ImmutableSortedSet<V> values()
    {
        if (this.cachedValues == null)
        {
            NavigableSet<V> immutableValues = new TreeSet<>(getUnderlyingMap().values());
            this.cachedValues = new ImmutableTreeSet<>(immutableValues, Immutable.WRAP);
        }
        return (ImmutableNavigableSet<V>) this.cachedValues;
    }

    @Override
    public final Comparator<? super K> comparator()
    {
        return getUnderlyingMap().comparator();
    }

    @Override
    public final ImmutableSortedMap<K, V> subMap(final K fromKey, final K toKey)
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().subMap(fromKey, toKey));
    }

    @Override
    public final ImmutableSortedMap<K, V> headMap(final K toKey)
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().headMap(toKey));
    }

    @Override
    public final ImmutableSortedMap<K, V> tailMap(final K fromKey)
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().tailMap(fromKey));
    }

    @Override
    public final K firstKey()
    {
        return getUnderlyingMap().firstKey();
    }

    @Override
    public final K lastKey()
    {
        return getUnderlyingMap().lastKey();
    }

    @Override
    public final K lowerKey(final K key)
    {
        return getUnderlyingMap().lowerKey(key);
    }

    @Override
    public final K floorKey(final K key)
    {
        return getUnderlyingMap().floorKey(key);
    }

    @Override
    public final K ceilingKey(final K key)
    {
        return getUnderlyingMap().ceilingKey(key);
    }

    @Override
    public final K higherKey(final K key)
    {
        return getUnderlyingMap().higherKey(key);
    }

    @Override
    public final ImmutableNavigableMap<K, V> descendingMap()
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().descendingMap());
    }

    @Override
    public final ImmutableNavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
            final boolean toInclusive)
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().subMap(fromKey, fromInclusive, toKey, toInclusive));
    }

    @Override
    public final ImmutableNavigableMap<K, V> headMap(final K toKey, final boolean inclusive)
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().headMap(toKey, inclusive));
    }

    @Override
    public final ImmutableNavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive)
    {
        return new ImmutableTreeMap<K, V>(getUnderlyingMap().tailMap(fromKey, inclusive));
    }

    @Override
    public final String toString()
    {
        NavigableMap<K, V> map = getUnderlyingMap();
        if (null == map)
        {
            return "ImmutableTreeMap []";
        }
        return "ImmutableTreeMap [" + map.toString() + "]";
    }

}
