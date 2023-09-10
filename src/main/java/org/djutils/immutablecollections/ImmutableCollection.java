package org.djutils.immutablecollections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A Collection interface without the methods that can change it.
 * <p>
 * Copyright (c) 2016-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <E> the type of content of this Collection
 */
public interface ImmutableCollection<E> extends Iterable<E>, Serializable
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
     * Returns <code>true</code> if this immutable collection contains the specified element. More formally, returns
     * <code>true</code> if and only if this immutable collection contains at least one element <code>e</code> such that
     * <code>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</code>.
     * @param o Object; element whose presence in this immutable collection is to be tested
     * @return <code>true</code> if this immutable collection contains the specified element
     * @throws ClassCastException if the type of the specified element is incompatible with this immutable collection
     * @throws NullPointerException if the specified element is null and this immutable collection does not permit null elements
     */
    boolean contains(Object o);

    /**
     * Returns an immutable iterator over the elements in this immutable collection. There are no guarantees concerning the
     * order in which the elements are returned (unless this immutable collection is an instance of some class that provides a
     * guarantee). The ImmutableIterator is an Iterator where the remove() operation will throw an exception.
     * @return an <code>ImmutableIterator</code> over the elements in this immutable collection
     */
    @Override
    ImmutableIterator<E> iterator();

    /**
     * Returns an array containing all of the elements in this immutable collection. If this immutable collection makes any
     * guarantees as to what order its elements are returned by its iterator, this method must return the elements in the same
     * order.
     * <p>
     * See java.util.Collection.toArray() for more details.
     * @return an array containing all of the elements in this immutable collection
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this immutable collection; the runtime type of the returned array is
     * that of the specified array. If the immutable collection fits in the specified array, it is returned therein. Otherwise,
     * a new array is allocated with the runtime type of the specified array and the size of this immutable collection.
     * <p>
     * See java.util.Collection.toArray(T[]) for more details.
     * @param <T> the runtime type of the array to contain the immutable collection
     * @param a the array into which the elements of this immutable collection are to be stored, if it is big enough; otherwise,
     *            a new array of the same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this immutable collection
     * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of every
     *             element in this immutable collection
     * @throws NullPointerException if the specified array is null
     */
    <T> T[] toArray(T[] a);

    /**
     * Returns <code>true</code> if this immutable collection contains all of the elements in the specified collection.
     * @param c Collection&lt;?&gt;; collection to be checked for containment in this immutable collection
     * @return <code>true</code> if this immutable collection contains all of the elements in the specified collection
     * @throws ClassCastException if the types of one or more elements in the specified collection are incompatible with this
     *             immutable collection
     * @throws NullPointerException if the specified collection contains one or more null elements and this immutable collection
     *             does not permit null elements, or if the specified collection is null.
     * @see #contains(Object)
     */
    boolean containsAll(Collection<?> c);

    /**
     * Returns <code>true</code> if this immutable collection contains all of the elements in the specified immutable
     * collection.
     * @param c ImmutableCollection&lt;?&gt;; immutable collection to be checked for containment in this immutable collection
     * @return <code>true</code> if this immutable collection contains all of the elements in the specified immutable collection
     * @throws ClassCastException if the types of one or more elements in the specified immutable collection are incompatible
     *             with this immutable collection
     * @throws NullPointerException if the specified immutable collection contains one or more null elements and this immutable
     *             collection does not permit null elements, or if the specified immutable collection is null.
     * @see #contains(Object)
     */
    boolean containsAll(ImmutableCollection<?> c);

    /**
     * Creates a Spliterator over the elements in this collection. Implementations should document characteristic values
     * reported by the spliterator. See java.util.Collection for more information.
     * @return a {@code Spliterator} over the elements in this collection
     */
    @Override
    default Spliterator<E> spliterator()
    {
        return Spliterators.spliterator(toCollection(), 0);
    }

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     * <p>
     * This method should be overridden when the {@link #spliterator()} method cannot return a spliterator that is
     * {@code IMMUTABLE}, {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()} for details.)
     * @return a sequential {@code Stream} over the elements in this collection
     */
    default Stream<E> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a possibly parallel {@code Stream} with this collection as its source. It is allowable for this method to return
     * a sequential stream.
     * <p>
     * This method should be overridden when the {@link #spliterator()} method cannot return a spliterator that is
     * {@code IMMUTABLE}, {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()} for details.)
     * @return a possibly parallel {@code Stream} over the elements in this collection
     */
    default Stream<E> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * Returns a modifiable copy of this immutable collection.
     * @return a modifiable copy of this immutable collection.
     */
    Collection<E> toCollection();

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
     * Return whether the internal storage is a wrapped pointer to the original collection. If true, this means that anyone
     * holding a pointer to this data structure can still change it. The users of the ImmutableCollection itself can, however,
     * not make any changes.
     * @return boolean; whether the internal storage is a wrapped pointer to the original collection
     */
    boolean isWrap();

    /**
     * Return whether the internal storage is a (shallow) copy of the original collection. If true, this means that anyone
     * holding a pointer to the original of the data structure can not change it anymore. Nor can the users of the
     * ImmutableCollection itself make any changes.
     * @return boolean; whether the internal storage is a safe copy of the original collection
     */
    default boolean isCopy()
    {
        return !isWrap();
    }

}
