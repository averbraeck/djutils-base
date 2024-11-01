package org.djutils.means;

/**
 * Compute arithmetic (weighted) mean of a set of values.
 * <p>
 * Copyright (c) 2013-2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <V> value type
 * @param <W> weight type
 */
public class ArithmeticMean<V extends Number, W extends Number> extends AbstractMean<ArithmeticMean<V, W>, V, W>
{
    @Override
    public final double getMean()
    {
        return getSum() / getSumOfWeights();
    }

    @Override
    public final ArithmeticMean<V, W> addImpl(final V value, final Number weight)
    {
        increment(weight.doubleValue() * value.doubleValue(), weight.doubleValue());
        return this;
    }

    @Override
    public final String toString()
    {
        return "ArithmeticMean [current sum=" + getSum() + ", current sum of weights=" + getSumOfWeights()
                + ", current arithmetic mean=" + getMean() + "]";
    }

}
