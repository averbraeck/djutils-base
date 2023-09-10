package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable double with get() and set(double) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableDouble
{
    /** the current value of the mutable double. */
    private double value;

    /**
     * Initialize the mutable double with a value.
     * @param value double; the initial value of the mutable double
     */
    public MutableDouble(final double value)
    {
        set(value);
    }

    /**
     * set the mutable double to a new value.
     * @param newValue double; the new value of the mutable double
     */
    public void set(final double newValue)
    {
        this.value = newValue;
    }

    /**
     * @return double; the current value of the mutable double
     */
    public double get()
    {
        return this.value;
    }

    /**
     * Increment the modifiable double with a value.
     * @param increment double; the value to increment the modifiable double with
     */
    public void inc(final double increment)
    {
        this.value += increment;
    }

    /**
     * Increment the modifiable double with 1.
     */
    public void inc()
    {
        inc(1);
    }

    /**
     * Decrement the modifiable double with a value.
     * @param decrement double; the value to decrement the modifiable double with
     */
    public void dec(final double decrement)
    {
        this.value -= decrement;
    }

    /**
     * Decrement the modifiable double with 1.
     */
    public void dec()
    {
        dec(1);
    }

    /**
     * Increment the modifiable double with a value.
     * @param multiplier double; the value to multiply the modifiable double with
     */
    public void mul(final double multiplier)
    {
        this.value *= multiplier;
    }

    /**
     * Divide the modifiable double by a value.
     * @param divisor double; the value to divide the modifiable double by
     */
    public void div(final double divisor)
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
        MutableDouble other = (MutableDouble) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableDouble [value=" + this.value + "]";
    }

}
