package org.djutils.immutablecollections;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An immutable wrapper for a LinkedHashMap.
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
public class ImmutableLinkedHashMap<K, V> extends ImmutableAbstractMap<K, V>
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
    public ImmutableLinkedHashMap(final Map<K, V> map)
    {
        super(new LinkedHashMap<K, V>(map), Immutable.COPY);
    }

    /**
     * @param map Map&lt;K,V&gt;; the map to use for the immutable map.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableLinkedHashMap(final Map<K, V> map, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new LinkedHashMap<K, V>(map) : map, copyOrWrap);
    }

    /**
     * @param immutableMap ImmutableAbstractMap&lt;K,V&gt;; the map to use for the immutable map.
     */
    public ImmutableLinkedHashMap(final ImmutableAbstractMap<K, V> immutableMap)
    {
        super(new LinkedHashMap<K, V>(immutableMap.getUnderlyingMap()), Immutable.COPY);
    }

    /**
     * @param immutableMap ImmutableAbstractMap&lt;K,V&gt;; the map to use for the immutable map.
     * @param copyOrWrap COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection
     */
    public ImmutableLinkedHashMap(final ImmutableAbstractMap<K, V> immutableMap, final Immutable copyOrWrap)
    {
        super(copyOrWrap == Immutable.COPY ? new LinkedHashMap<K, V>(immutableMap.getUnderlyingMap())
                : immutableMap.getUnderlyingMap(), copyOrWrap);
    }

    /** {@inheritDoc} */
    @Override
    protected final Map<K, V> getUnderlyingMap()
    {
        return super.getUnderlyingMap();
    }

    /** {@inheritDoc} */
    @Override
    public final Map<K, V> toMap()
    {
        return new LinkedHashMap<K, V>(getUnderlyingMap());
    }

    /** {@inheritDoc} */
    @Override
    public final ImmutableSet<K> keySet()
    {
        if (this.cachedKeySet == null)
        {
            Set<K> immutableKeySet = new LinkedHashSet<>(getUnderlyingMap().keySet());
            this.cachedKeySet = new ImmutableHashSet<>(immutableKeySet, Immutable.WRAP);
        }
        return this.cachedKeySet;
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableSet<ImmutableEntry<K, V>> entrySet()
    {
        if (this.cachedEntrySet == null)
        {
            Set<ImmutableEntry<K, V>> immutableEntrySet = new LinkedHashSet<>();
            for (Entry<K, V> entry : getUnderlyingMap().entrySet())
            {
                immutableEntrySet.add(new ImmutableEntry<>(entry));
            }
            this.cachedEntrySet = new ImmutableHashSet<>(immutableEntrySet, Immutable.WRAP);
        }
        return this.cachedEntrySet;
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableCollection<V> values()
    {
        if (this.cachedValues == null)
        {
            Set<V> immutableValues = new LinkedHashSet<>(getUnderlyingMap().values());
            this.cachedValues = new ImmutableLinkedHashSet<>(immutableValues, Immutable.WRAP);
        }
        return this.cachedValues;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        Map<K, V> map = getUnderlyingMap();
        if (null == map)
        {
            return "ImmutableLinkedHashMap []";
        }
        return "ImmutableLinkedHashMap [" + map.toString() + "]";
    }

}
