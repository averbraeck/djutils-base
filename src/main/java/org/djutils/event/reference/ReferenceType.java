package org.djutils.event.reference;

/**
 * ReferenceType indicates whether a reference is strong or weak.
 * <p>
 * Copyright (c) 2019-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public enum ReferenceType
{
    /** Strong reference. */
    STRONG,

    /** Weak reference. */
    WEAK;

    /**
     * Return whether the reference is strong.
     * @return true when reference is strong
     */
    public boolean isStrong()
    {
        return this.equals(STRONG);
    }

    /**
     * Return whether the reference is weak.
     * @return true when reference is weak
     */
    public boolean isWeak()
    {
        return this.equals(WEAK);
    }
}
