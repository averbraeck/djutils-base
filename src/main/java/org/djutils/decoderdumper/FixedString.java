package org.djutils.decoderdumper;

/**
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Jan 3, 2019 <br>
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public int getMaximumWidth()
    {
        return this.fixedResult.length();
    }

    /** {@inheritDoc} */
    @Override
    public boolean append(final int address, final byte theByte)
    {
        this.appendWasCalled = true;
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean ignoreForIdenticalOutputCheck()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "FixedString [fixedResult=" + this.fixedResult + ", appendWasCalled=" + this.appendWasCalled + "]";
    }

}
