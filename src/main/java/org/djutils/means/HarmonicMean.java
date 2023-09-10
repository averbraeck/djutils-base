package org.djutils.means;

/**
 * Compute the harmonic (weighted) mean of a set of values.
 * <p>
 * Copyright (c) 2013-2023 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Oct 26, 2018 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @param <V> value type
 * @param <W> weight type
 */
public class HarmonicMean<V extends Number, W extends Number> extends AbstractMean<HarmonicMean<V, W>, V, W>
{

    /** {@inheritDoc} */
    @Override
    public final double getMean()
    {
        return getSumOfWeights() / getSum();
    }

    /** {@inheritDoc} */
    @Override
    public final HarmonicMean<V, W> addImpl(final V value, final Number weight)
    {
        increment(weight.doubleValue() / value.doubleValue(), weight.doubleValue());
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "HarmonicMean [current sum of reciprocal values=" + getSum() + ", current sum of weights=" + getSumOfWeights()
                + ", current harmonic mean=" + getMean() + "]";
    }

}
