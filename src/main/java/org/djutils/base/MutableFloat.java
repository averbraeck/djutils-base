package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable float with get() and set(float) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableFloat
{
    /** the current value of the mutable float. */
    private float value;

    /**
     * Initialize the mutable float with a value.
     * @param value float; the initial value of the mutable float
     */
    public MutableFloat(final float value)
    {
        set(value);
    }

    /**
     * set the mutable float to a new value.
     * @param newValue float; the new value of the mutable float
     */
    public void set(final float newValue)
    {
        this.value = newValue;
    }

    /**
     * @return float; the current value of the mutable float
     */
    public float get()
    {
        return this.value;
    }

    /**
     * Increment the modifiable float with a value.
     * @param increment float; the value to increment the modifiable float with
     */
    public void inc(final float increment)
    {
        this.value += increment;
    }

    /**
     * Increment the modifiable float with 1.
     */
    public void inc()
    {
        inc(1);
    }

    /**
     * Decrement the modifiable float with a value.
     * @param decrement float; the value to decrement the modifiable float with
     */
    public void dec(final float decrement)
    {
        this.value -= decrement;
    }

    /**
     * Decrement the modifiable float with 1.
     */
    public void dec()
    {
        dec(1);
    }

    /**
     * Increment the modifiable float with a value.
     * @param multiplier float; the value to multiply the modifiable float with
     */
    public void mul(final float multiplier)
    {
        this.value *= multiplier;
    }

    /**
     * Divide the modifiable float by a value.
     * @param divisor float; the value to divide the modifiable float by
     */
    public void div(final float divisor)
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
        MutableFloat other = (MutableFloat) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableFloat [value=" + this.value + "]";
    }

}
