package org.djutils.multikeymap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.djutils.exceptions.Throw;

/**
 * Store for data indexed by a combination of objects. An important difference with a normal (Linked-)HashMap is that the
 * <code>getValue</code> method has a <code>Supplier</code> argument that will be invoked when no value was stored under the
 * provided combination of keys. The value yielded by the <code>Supplier</code> is then stored under that key combination. If
 * this behavior is not wanted, the user can provide the null value for the <code>Supplier</code> argument. The map can store
 * null values, but on retrieval these are indistinguishable from values that were never set. No key values may be null.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version 20 apr. 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 * @param <T> value type
 */
public class MultiKeyMap<T>
{

    /** Array of key types. */
    private final Class<?>[] keyTypes;

    /** Map with keys and values at this level. */
    private final Map<Object, Object> map = new LinkedHashMap<>();

    /**
     * Construct a new MultiKeyMap.
     * @param keyTypes Class&lt;?&gt;...; the types of the keys
     */
    public MultiKeyMap(final Class<?>... keyTypes)
    {
        Throw.when(keyTypes.length < 1, IllegalArgumentException.class, "keyTypes may not be empty");
        this.keyTypes = keyTypes;
    }

    /**
     * Retrieve a value from this MultiKeyMap. Can create a new entry if it does not exist yet.
     * @param supplier Supplier&lt;T&gt;; supplier of {@code T} in case the leaf does not exist yet. Set this to
     *            <code>null</code> to suppress creation of new branches and a new leaf
     * @param keys Object...; list of key objects
     * @return T; the existing value, or the value that was obtained through the <code>supplier</code>. Returns
     *         <code>null</code> if the leaf does not exist and <code>supplier</code> is <code>null</code>
     */
    public T get(final Supplier<T> supplier, final Object... keys)
    {
        return getValue(supplier, Arrays.asList(keys));
    }

    /**
     * Retrieve a value from this MultiKeyMap.
     * @param keys Object...; the key objects
     * @return T; value, or null if no value is stored under the provided key objects
     */
    public T get(final Object... keys)
    {
        return getValue(null, Arrays.asList(keys));
    }

    /**
     * Retrieve a sub map.
     * @param keys Object...; the key objects (must be at least one item shorter than the full depth)
     * @return MultiKeyMap&lt;T&gt;; the sub map
     */
    public MultiKeyMap<T> getSubMap(final Object... keys)
    {
        Throw.when(keys.length >= this.keyTypes.length, IllegalArgumentException.class, "Too many keys");
        return getSubMap(false, Arrays.asList(keys));
    }

    /**
     * Put (add or replace) a value in this MultiKeyMap.
     * @param newValue T; the new value
     * @param keys Object...; the key objects
     * @return T; the previous value stored under the key objects, or null if no value was currently stored under the key
     *         objects
     */
    public T put(final T newValue, final Object... keys)
    {
        List<Object> keyList = Arrays.asList(keys);
        Map<Object, T> branch = getFinalMap(true, keyList);
        Object key = getFinalKey(keyList);
        return branch.put(key, newValue);
    }

    /**
     * Select and verify the type of the last key.
     * @param keys List&lt;Object&gt;; the keys
     * @return Object; the last element of keys
     */
    private Object getFinalKey(final List<Object> keys)
    {
        Object key = keys.get(keys.size() - 1);
        Throw.whenNull(key, "key may not be null");
        Throw.when(key != null && !this.keyTypes[keys.size() - 1].isAssignableFrom(key.getClass()),
                IllegalArgumentException.class, "Key %s is not of %s.", key, this.keyTypes[keys.size() - 1]);
        return key;
    }

    /**
     * Walk the tree up to (but not including) the last level.
     * @param createBranches boolean; if true; missing branches are constructed; if false; missing branches cause this method to
     *            return null;
     * @param keys List&lt;Object&gt;; the keys.
     * @return Map&lt;T&gt;; the lowest level map
     */
    @SuppressWarnings("unchecked")
    private Map<Object, T> getFinalMap(final boolean createBranches, final List<Object> keys)
    {
        Throw.when(keys.size() != this.keyTypes.length, IllegalArgumentException.class, "Incorrect number of keys.");
        MultiKeyMap<T> finalMap = getSubMap(createBranches, keys.subList(0, keys.size() - 1));
        if (null == finalMap)
        {
            return null;
        }
        return (Map<Object, T>) finalMap.map;
    }

    /**
     * Retrieve a value. This uses a {@code List} rather than an array because that is easier to slice.
     * @param supplier Supplier&lt;T&gt;; supplier of {@code T} for if it wasn't cached yet. Set <code>supplier</code> to null
     *            to suppress construction of missing branches and a leaf
     * @param keys List&lt;Object&gt;; list of key objects
     * @return T; value, or null if no value was stored under the specified list of keys and <code>supplier</code> was null
     */
    private T getValue(final Supplier<T> supplier, final List<Object> keys)
    {
        Throw.when(keys.size() != this.keyTypes.length, IllegalArgumentException.class, "Wrong number of keys");
        Map<Object, T> branch = getFinalMap(null != supplier, keys);
        if (null == branch)
        {
            return null;
        }
        Object key = getFinalKey(keys);
        T leaf = branch.get(key);
        if (leaf == null) // Leaf does not exist, yet
        {
            if (null == supplier)
            {
                return null; // Caller does not want to create a new leaf
            }
            // Create a new leaf
            leaf = supplier.get();
            branch.put(key, leaf);
        }
        return leaf;
    }

    /**
     * Return set of key objects on this level.
     * @param keys Object...; list of key objects (may be empty to select the key set at the top level
     * @return Set; set of key objects on this level. This is not a safe copy; this set reflects subsequent changes in this
     *         MultiKeyMap and modifying this set would modify this MultiKeyMap (potentially making it inconsistent).
     */
    public Set<Object> getKeys(final Object... keys)
    {
        return getSubMap(false, Arrays.asList(keys)).map.keySet();
    }

    /**
     * Walk the tree to the requested branch.
     * @param createMissingBranches boolean; if true; missing branches will be created.
     * @param keys List&lt;Object&gt;; the keys
     * @return MultiKeyMap&lt;T&gt;; the branch at the end of the list of keys, or null if that branch does not exist and
     *         <code>createMissingBranches</code> is false
     */
    @SuppressWarnings("unchecked")
    private MultiKeyMap<T> getSubMap(final boolean createMissingBranches, final List<Object> keys)
    {
        if (keys.size() == 0)
        {
            return this;
        }
        Throw.when(keys.size() > this.keyTypes.length, IllegalArgumentException.class, "Too many keys");
        Throw.when(keys.get(0) != null && !this.keyTypes[0].isAssignableFrom(keys.get(0).getClass()),
                IllegalArgumentException.class, "Key %s is not of %s.", keys.get(0), this.keyTypes[0]);
        MultiKeyMap<T> subMap = (MultiKeyMap<T>) this.map.get(keys.get(0));
        if (null == subMap && !createMissingBranches)
        {
            return null; // Caller does not want to create new branches
        }
        if (null == subMap)
        {
            // Create branch with 1 less key
            Class<Object>[] subTypes = new Class[this.keyTypes.length - 1];
            System.arraycopy(this.keyTypes, 1, subTypes, 0, this.keyTypes.length - 1);
            subMap = new MultiKeyMap<T>(subTypes);
            this.map.put(keys.get(0), subMap);
        }
        return (subMap.getSubMap(createMissingBranches, keys.subList(1, keys.size())));
    }

    /**
     * Clears the mapping for a key combination.
     * @param keys Object...; key combination to clear the map for
     * @return Object; object that was previously mapped to the key combination, or {@code null} if it was not cached.
     */
    public Object clear(final Object... keys)
    {
        return clear(Arrays.asList(keys));

    }

    /**
     * Clear the mapping for a key combination.
     * @param keys List&lt;Object&gt;; key combination to clear the map for
     * @return Object; object that was previously mapped to the key combination, or {@code null} if it was not cached.
     */
    @SuppressWarnings("unchecked")
    private Object clear(final List<Object> keys)
    {
        if (keys.size() == 0)
        {
            this.map.clear();
            return this;
        }
        MultiKeyMap<T> subMap = getSubMap(false, keys.subList(0, keys.size() - 1));
        if (null == subMap)
        {
            return null;
        }
        if (keys.size() == this.keyTypes.length)
        {
            Map<Object, T> endMap = (Map<Object, T>) subMap.map;
            return endMap.remove(keys.get(keys.size() - 1));
        }
        return subMap.map.remove(keys.get(keys.size() - 1));
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MultiKeyMap [types=" + Arrays.toString(this.keyTypes) + ", map=" + this.map + "]";
    }

}
