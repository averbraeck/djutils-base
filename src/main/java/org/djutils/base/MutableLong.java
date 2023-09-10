package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable long with get() and set(long) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableLong
{
    /** the current value of the mutable long. */
    private long value;

    /**
     * Initialize the mutable long with a value.
     * @param value long; the initial value of the mutable long
     */
    public MutableLong(final long value)
    {
        set(value);
    }

    /**
     * set the mutable long to a new value.
     * @param newValue long; the new value of the mutable long
     */
    public void set(final long newValue)
    {
        this.value = newValue;
    }

    /**
     * @return long; the current value of the mutable long
     */
    public long get()
    {
        return this.value;
    }

    /**
     * Increment the modifiable long with a value.
     * @param increment long; the value to increment the modifiable long with
     */
    public void inc(final long increment)
    {
        this.value += increment;
    }

    /**
     * Increment the modifiable long with 1.
     */
    public void inc()
    {
        inc(1);
    }

    /**
     * Decrement the modifiable long with a value.
     * @param decrement long; the value to decrement the modifiable long with
     */
    public void dec(final long decrement)
    {
        this.value -= decrement;
    }

    /**
     * Decrement the modifiable long with 1.
     */
    public void dec()
    {
        dec(1);
    }

    /**
     * Increment the modifiable long with a value.
     * @param multiplier long; the value to multiply the modifiable long with
     */
    public void mul(final long multiplier)
    {
        this.value *= multiplier;
    }

    /**
     * Divide the modifiable long by a value.
     * @param divisor long; the value to divide the modifiable long by
     */
    public void div(final long divisor)
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
        MutableLong other = (MutableLong) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableLong [value=" + this.value + "]";
    }

}
