package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable boolean with get() and set(boolean) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableBoolean
{
    /** the current value of the mutable boolean. */
    private boolean value;

    /**
     * Initialize the mutable boolean with a value.
     * @param value boolean; the initial value of the mutable boolean
     */
    public MutableBoolean(final boolean value)
    {
        set(value);
    }

    /**
     * set the mutable boolean to a new value.
     * @param newValue boolean; the new value of the mutable boolean
     */
    public void set(final boolean newValue)
    {
        this.value = newValue;
    }

    /**
     * @return boolean; the current value of the mutable boolean
     */
    public boolean get()
    {
        return this.value;
    }

    /**
     * Flip the value of modifiable boolean.
     */
    public void flip()
    {
        this.value = !this.value;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.value);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MutableBoolean other = (MutableBoolean) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableBoolean [value=" + this.value + "]";
    }

}
