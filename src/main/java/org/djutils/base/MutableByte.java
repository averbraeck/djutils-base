package org.djutils.base;

import java.util.Objects;

/**
 * A simple version of a mutable byte with get() and set(byte) functions.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MutableByte
{
    /** the current value of the mutable byte. */
    private byte value;

    /**
     * Initialize the mutable byte with a value.
     * @param value byte; the initial value of the mutable byte
     */
    public MutableByte(final byte value)
    {
        set(value);
    }

    /**
     * set the mutable byte to a new value.
     * @param newValue byte; the new value of the mutable byte
     */
    public void set(final byte newValue)
    {
        this.value = newValue;
    }

    /**
     * @return byte; the current value of the mutable byte
     */
    public byte get()
    {
        return this.value;
    }

    /**
     * Increment the modifiable byte with a value.
     * @param increment byte; the value to increment the modifiable byte with
     */
    public void inc(final byte increment)
    {
        this.value += increment;
    }

    /**
     * Increment the modifiable byte with 1.
     */
    public void inc()
    {
        inc((byte) 1);
    }

    /**
     * Decrement the modifiable byte with a value.
     * @param decrement byte; the value to decrement the modifiable byte with
     */
    public void dec(final byte decrement)
    {
        this.value -= decrement;
    }

    /**
     * Decrement the modifiable byte with 1.
     */
    public void dec()
    {
        dec((byte) 1);
    }

    /**
     * Increment the modifiable byte with a value.
     * @param multiplier byte; the value to multiply the modifiable byte with
     */
    public void mul(final byte multiplier)
    {
        this.value *= multiplier;
    }

    /**
     * Divide the modifiable byte by a value.
     * @param divisor byte; the value to divide the modifiable byte by
     */
    public void div(final byte divisor)
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
        MutableByte other = (MutableByte) obj;
        return this.value == other.value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MutableByte [value=" + this.value + "]";
    }

}
