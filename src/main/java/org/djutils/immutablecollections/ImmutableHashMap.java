package org.djutils.immutablecollections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An immutable wrapper for a HashMap.
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <K> the key type of content of this Map
 * @param <V> the value type of content of this Map
 */
public class ImmutableHashMap<K, V> extends ImmutableAbstractMap<K, V>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /** the cached keySet. */
    private ImmutableSet<K> cachedKeySet = null;

    /** the cached entrySet. */
    private ImmutableSet<ImmutableEntry<K, V>> cachedEntrySet = null;

    /**
     * @param map Map&lt;K,V&gt;; the map to use for the immutable map.
     */
    public ImmutableHashMap(final Map<K, V> map)
    {
        super(new HashMap<K, V>(map), Immutable.COPY);
    }

    /**
     * @param map Map&lt;K,V&gt;; the map to use for the immutable map.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableHashMap(final Map<K, V> map, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new HashMap<K, V>(map) : map, copyOrWrap);
    }

    /**
     * @param immutableMap ImmutableAbstractMap&lt;K,V&gt;; the map to use for the immutable map.
     */
    public ImmutableHashMap(final ImmutableAbstractMap<K, V> immutableMap)
    {
        super(new HashMap<K, V>(immutableMap.getUnderlyingMap()), Immutable.COPY);
    }

    /**
     * @param immutableMap ImmutableAbstractMap&lt;K,V&gt;; the map to use for the immutable map.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableHashMap(final ImmutableAbstractMap<K, V> immutableMap, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new HashMap<K, V>(immutableMap.getUnderlyingMap())
                : immutableMap.getUnderlyingMap(), copyOrWrap);
    }

    @Override
    protected final Map<K, V> getUnderlyingMap()
    {
        return super.getUnderlyingMap();
    }

    @Override
    public final Map<K, V> toMap()
    {
        return new HashMap<K, V>(getUnderlyingMap());
    }

    @Override
    public final ImmutableSet<K> keySet()
    {
        if (this.cachedKeySet == null)
        {
            Set<K> immutableKeySet = new HashSet<>(getUnderlyingMap().keySet());
            this.cachedKeySet = new ImmutableHashSet<>(immutableKeySet, Immutable.WRAP);
        }
        return this.cachedKeySet;
    }

    @Override
    public ImmutableSet<ImmutableEntry<K, V>> entrySet()
    {
        if (this.cachedEntrySet == null)
        {
            Set<ImmutableEntry<K, V>> immutableEntrySet = new HashSet<>();
            for (Entry<K, V> entry : getUnderlyingMap().entrySet())
            {
                immutableEntrySet.add(new ImmutableEntry<>(entry));
            }
            this.cachedEntrySet = new ImmutableHashSet<>(immutableEntrySet, Immutable.WRAP);
        }
        return this.cachedEntrySet;
    }

    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public String toString()
    {
        Map<K, V> map = getUnderlyingMap();
        if (null == map)
        {
            return "ImmutableHashMap []";
        }
        return "ImmutableHashMap [" + map.toString() + "]";
    }

}
