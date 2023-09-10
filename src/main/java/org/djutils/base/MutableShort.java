package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable short with get() and set(short) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableShort
{
    /** the current value of the mutable short. */
    private short value;

    /**
     * Initialize the mutable short with a value.
     * @param value short; the initial value of the mutable short
     */
    public MutableShort(final short value)
    {
        set(value);
    }

    /**
     * set the mutable short to a new value.
     * @param newValue short; the new value of the mutable short
     */
    public void set(final short newValue)
    {
        this.value = newValue;
    }

    /**
     * @return short; the current value of the mutable short
     */
    public short get()
    {
        return this.value;
    }

    /**
     * Increment the modifiable short with a value.
     * @param increment short; the value to increment the modifiable short with
     */
    public void inc(final short increment)
    {
        this.value += increment;
    }

    /**
     * Increment the modifiable short with 1.
     */
    public void inc()
    {
        inc((short) 1);
    }

    /**
     * Decrement the modifiable short with a value.
     * @param decrement short; the value to decrement the modifiable short with
     */
    public void dec(final short decrement)
    {
        this.value -= decrement;
    }

    /**
     * Decrement the modifiable short with 1.
     */
    public void dec()
    {
        dec((short) 1);
    }

    /**
     * Increment the modifiable short with a value.
     * @param multiplier short; the value to multiply the modifiable short with
     */
    public void mul(final short multiplier)
    {
        this.value *= multiplier;
    }

    /**
     * Divide the modifiable short by a value.
     * @param divisor short; the value to divide the modifiable short by
     */
    public void div(final short divisor)
    {
        this.value /= divisor;
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
        MutableShort other = (MutableShort) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableShort [value=" + this.value + "]";
    }

}
