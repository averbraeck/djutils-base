/**
 * Contains a set of immutable collection interfaces and wrapper implementations. Two versions of immutable collections are
 * implemented:
 * <ol>
 * <li>A version, identified by Immutable.COPY, where the immutable collection can neither be changed by any object "using" the
 * ImmutableCollection nor anymore by objects that have a pointer to the collection, as an internal (shallow) copy is made of
 * the collection. This is the <b>default</b> implementation.</li>
 * <li>A version, identified by Immutable.WRAP, where the immutable collection can not be changed by any object "using" the
 * ImmutableCollection, but it can still be changed by any object that has a pointer to the original collection that is
 * "wrapped". Instead of a (shallow) copy of the collection, a pointer to the collection is stored.</li>
 * </ol>
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
package org.djutils.immutablecollections;
