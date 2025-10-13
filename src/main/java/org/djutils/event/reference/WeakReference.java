package org.djutils.event.reference;

/**
 * A WeakReference. The WeakReference extends the <code>java.lang.ref.WeakReference</code> and besides implementing the
 * Reference interface no changes are defined.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the type of the reference
 */
public class WeakReference<T> extends Reference<T>
{
    /** the wrapped reference to the referent. */
    private final transient java.lang.ref.WeakReference<T> referent;

    /**
     * Creates a new weak reference that refers to the given object. The new reference is not registered with any queue.
     * @param referent object the new weak reference will refer to
     */
    public WeakReference(final T referent)
    {
        this.referent = new java.lang.ref.WeakReference<T>(referent);
    }

    @Override
    public final T get()
    {
        return this.referent.get();
    }
}
