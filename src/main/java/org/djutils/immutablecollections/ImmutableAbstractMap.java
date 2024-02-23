package org.djutils.immutablecollections;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.djutils.exceptions.Throw;

/**
 * An abstract base class for an immutable wrapper for a Map.
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
public abstract class ImmutableAbstractMap<K, V> implements ImmutableMap<K, V>
{
    /** */
    private static final long serialVersionUID = 20160507L;

    /** the map that is wrapped, without giving access to methods that can change it. */
    private final Map<K, V> map;

    /** COPY stores a safe, internal copy of the collection; WRAP stores a pointer to the original collection. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final Immutable copyOrWrap;

    /** the cached values. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected ImmutableCollection<V> cachedValues = null;

    /**
     * Construct an abstract immutable map. Make sure that the argument is a safe copy of the map of the right type! Copying
     * does not take place in the Abstract class!
     * @param map Map&lt;K,V&gt;; a safe copy of the map to use for the immutable map
     * @param copyOrWrap Immutable; indicate whether the immutable is a copy or a wrap
     */
    protected ImmutableAbstractMap(final Map<K, V> map, final Immutable copyOrWrap)
    {
        Throw.whenNull(copyOrWrap, "the copyOrWrap argument should be Immutable.COPY or Immutable.WRAP");
        this.copyOrWrap = copyOrWrap;
        Throw.whenNull(map, "the map argument cannot be null");
        this.map = map;
    }

    /**
     * Return the raw underlying map.
     * @return Map&lt;K, V&gt;; the raw underlying map
     */
    @SuppressWarnings("checkstyle:designforextension")
    protected Map<K, V> getUnderlyingMap()
    {
        return this.map;
    }

    /** {@inheritDoc} */
    @Override
    public final int size()
    {
        return this.map.size();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public final boolean containsKey(final Object key)
    {
        return this.map.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean containsValue(final Object value)
    {
        return this.map.containsValue(value);
    }

    /** {@inheritDoc} */
    @Override
    public final V get(final Object key)
    {
        return this.map.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableCollection<V> values()
    {
        if (this.cachedValues == null)
        {
            Set<V> immutableValues = new LinkedHashSet<>(getUnderlyingMap().values());
            this.cachedValues = new ImmutableHashSet<>(immutableValues, Immutable.WRAP);
        }
        return this.cachedValues;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isWrap()
    {
        return this.copyOrWrap.isWrap();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.map == null) ? 0 : this.map.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({"checkstyle:designforextension", "checkstyle:needbraces"})
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImmutableAbstractMap<?, ?> other = (ImmutableAbstractMap<?, ?>) obj;
        if (this.map == null)
        {
            if (other.map != null)
                return false;
        }
        else if (!this.map.equals(other.map))
            return false;
        return true;
    }

}
