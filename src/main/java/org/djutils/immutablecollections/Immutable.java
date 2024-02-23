package org.djutils.immutablecollections;

/**
 * Indicate whether the immutable collection contains a COPY of the collection (neither changeable by the user of the immutable
 * collection, nor by anyone holding a pointer to the original collection), or a WRAP for the original collection (not
 * changeable by the user of the immutable collection, but can be changed by anyone holding a pointer to the original collection
 * that is wrapped).
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public enum Immutable
{
    /**
     * A copy is neither changeable by the user of the immutable collection, nor by anyone holding a pointer to the original
     * collection that is put into the immutable collection.
     */
    COPY,

    /**
     * A wrapped immutable collection is not changeable by the user of the immutable collection, but can be changed by anyone
     * holding a pointer to the original collection that is wrapped.
     */
    WRAP;

    /**
     * @return whether the immutable is a COPY
     */
    public boolean isCopy()
    {
        return this.equals(COPY);
    }

    /**
     * @return whether the immutable is a WRAP
     */
    public boolean isWrap()
    {
        return this.equals(WRAP);
    }

}
