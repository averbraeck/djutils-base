package org.djutils.event.reference;

import java.io.Serializable;

/**
 * The Reference abstract class defines an indirect pointer to an object that can be serialized, in contrast with the Java
 * Reference class, which is not serializable. References can be weak or strong.
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
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
public abstract class Reference<T extends Serializable> implements Serializable
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20140830L;

    /**
     * Returns this reference object's referent. If this reference object has been cleared, either by the program or by the
     * garbage collector, then this method returns <code>null</code>.
     * @return The object to which this reference refers, or <code>null</code> if this reference object has been cleared.
     */
    public abstract T get();

}
