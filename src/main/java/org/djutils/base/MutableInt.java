package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable integer with get() and set(int) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableInt
{
    /** the current value of the mutable integer. */
    private int value;

    /**
     * Initialize the mutable integer with a value.
     * @param value int; the initial value of the mutable integer
     */
    public MutableInt(final int value)
    {
        set(value);
    }

    /**
     * set the mutable integer to a new value.
     * @param newValue int; the new value of the mutable integer
     */
    public void set(final int newValue)
    {
        this.value = newValue;
    }

    /**
     * @return int; the current value of the mutable integer
     */
    public int get()
    {
        return this.value;
    }

    /**
     * Increment the modifiable int with a value.
     * @param increment int; the value to increment the modifiable int with
     */
    public void inc(final int increment)
    {
        this.value += increment;
    }

    /**
     * Increment the modifiable int with 1.
     */
    public void inc()
    {
        inc(1);
    }

    /**
     * Decrement the modifiable int with a value.
     * @param decrement int; the value to decrement the modifiable int with
     */
    public void dec(final int decrement)
    {
        this.value -= decrement;
    }

    /**
     * Decrement the modifiable int with 1.
     */
    public void dec()
    {
        dec(1);
    }

    /**
     * Increment the modifiable int with a value.
     * @param multiplier int; the value to multiply the modifiable int with
     */
    public void mul(final int multiplier)
    {
        this.value *= multiplier;
    }

    /**
     * Divide the modifiable int by a value.
     * @param divisor int; the value to divide the modifiable int by
     */
    public void div(final int divisor)
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
        MutableInt other = (MutableInt) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableInt [value=" + this.value + "]";
    }

}
