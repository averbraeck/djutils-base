package org.djutils.decoderdumper;

/**
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class FixedString implements Decoder
{
    /**
     * The String that will be returned by <code>getResult</code> on each invocation of getResult after at least one call to
     * append.
     */
    private final String fixedResult;

    /** Remember if the append method was ever called. */
    private boolean appendWasCalled = false;

    /**
     * Construct a Decoder that returns a fixed result in the <code>getResult</code> method.
     * @param fixedResult String; the String that the <code>getResult</code> method will return on each invocation
     */
    public FixedString(final String fixedResult)
    {
        this.fixedResult = fixedResult;
    }

    @Override
    public String getResult()
    {
        if (this.appendWasCalled)
        {
            this.appendWasCalled = false;
            return this.fixedResult;
        }
        return "";
    }

    @Override
    public int getMaximumWidth()
    {
        return this.fixedResult.length();
    }

    @Override
    public boolean append(final int address, final byte theByte)
    {
        this.appendWasCalled = true;
        return false;
    }

    @Override
    public boolean ignoreForIdenticalOutputCheck()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "FixedString [fixedResult=" + this.fixedResult + ", appendWasCalled=" + this.appendWasCalled + "]";
    }

}
