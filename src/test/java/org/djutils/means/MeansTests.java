package org.djutils.means;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test the classes in the means package
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class MeansTests
{
    /** Some double values. */
    private static Double[] testValues = {10.0, Math.PI, Math.E, 1234.};

    /** Some weights. */
    private static Double[] testWeights = {1.0, 2.0, Math.PI, 1.0};

    /**
     * Test the Mean classes using unity weights.
     */
    @Test
    public final void testMeansWithUnityWeights()
    {
        ArithmeticMean<Double, Double> am = new ArithmeticMean<Double, Double>();
        double sum = 0;
        for (int i = 0; i < testValues.length; i++)
        {
            double testValue = testValues[i];
            assertEquals(am, am.add(testValue), "add returns object for method chaining");
            sum += testValue;
            assertEquals(sum, am.getSum(), sum / 99999999, "sum");
            assertEquals(sum / (i + 1), am.getMean(), sum / (i + 1) / 99999999, "arithmetic mean");
        }
        am = new ArithmeticMean<Double, Double>();
        assertEquals(am, am.add(testValues), "add returns object for method chaining");
        assertEquals(sum / testValues.length, am.getMean(), sum / testValues.length / 99999999, "arithmetic mean");

        am = new ArithmeticMean<Double, Double>();
        assertEquals(am, am.add(new ArrayList<Double>(Arrays.asList(testValues))), "add returns object for method chaining");
        assertEquals(sum / testValues.length, am.getMean(), sum / testValues.length / 99999999, "arithmetic mean");
    }

    /**
     * Test the Mean classes using varying weights.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public final void testMeansWithWeights()
    {
        ArithmeticMean<Double, Double> am = new ArithmeticMean<Double, Double>();
        assertEquals(0, am.getSum(), 0.00000, "Initial sum is 0");
        assertEquals(0, am.getSumOfWeights(), 0.00000, "Initial sum of weights is 0");
        assertTrue(Double.isNaN(am.getMean()), "Initial mean is NaN");
        HarmonicMean<Double, Double> hm = new HarmonicMean<Double, Double>();
        assertEquals(0, hm.getSum(), 0.00000, "Initial sum is 0");
        assertEquals(0, hm.getSumOfWeights(), 0.00000, "Initial sum of weights is 0");
        assertTrue(Double.isNaN(hm.getMean()), "Initial mean is NaN");
        GeometricMean<Double, Double> gm = new GeometricMean<Double, Double>();
        assertEquals(0, gm.getSum(), 0.00000, "Initial sum is 0");
        assertEquals(0, gm.getSumOfWeights(), 0.00000, "Initial sum of weights is 0");
        assertTrue(Double.isNaN(gm.getMean()), "Initial mean is NaN");
        double sum = 0;
        double sumWeights = 0;
        double recipSum = 0;
        double product = 1;
        double geometricMean = 0;
        Map<Double, Double> map = new HashMap<>();
        for (int i = 0; i < testValues.length; i++)
        {
            double testValue = testValues[i];
            double testWeight = testWeights[i];
            map.put(testValue, testWeight); // There are no duplicates in testValues
            assertEquals(am, am.add(testValue, testWeight), "add returns object for method chaining");
            hm.add(testValue, testWeight);
            gm.add(testValue, testWeight);
            sum += testValue * testWeight;
            recipSum += testWeight / testValue;
            product *= Math.pow(testValue, testWeight);
            sumWeights += testWeight;
            if (0 == i)
            {
                assertEquals(testValue, am.getMean(), testValue / 99999999, "mean of one value equals value");
                assertEquals(testValue, hm.getMean(), testValue / 99999999, "mean of one value equals value");
                assertEquals(testValue, gm.getMean(), testValue / 99999999, "mean of one value equals value");
            }
            assertEquals(sum, am.getSum(), sum / 99999999, "sum");
            assertEquals(sumWeights, am.getSumOfWeights(), sumWeights / 99999999, "sum of weights");
            assertEquals(sum / sumWeights, am.getMean(), sum / sumWeights / 99999999, "arithmetic mean");
            assertEquals(recipSum, hm.getSum(), recipSum / 99999999, "sum");
            assertEquals(sumWeights, hm.getSumOfWeights(), sumWeights / 99999999, "sum of weights");
            assertEquals(sumWeights / recipSum, hm.getMean(), sumWeights / recipSum / 999999999, "harmonic mean");
            geometricMean = Math.pow(product, 1 / sumWeights);
            assertEquals(geometricMean, gm.getMean(), geometricMean / 99999999,
                    "check with alternative way to compute geometric mean");
        }
        // System.out.println(
        // "Mean of test data: arithmetic=" + am.getMean() + ", harmonic=" + hm.getMean() + ", geometric=" + gm.getMean());
        am = new ArithmeticMean<Double, Double>();
        hm = new HarmonicMean<Double, Double>();
        gm = new GeometricMean<Double, Double>();
        am.add(testValues[0], 123.456);
        hm.add(testValues[0], 123.456);
        gm.add(testValues[0], 123.456);
        assertEquals(testValues[0], am.getMean(), testValues[0] / 99999999, "One value, any weight has mean equal to value");
        assertEquals(testValues[0], hm.getMean(), testValues[0] / 99999999, "One value, any weight has mean equal to value");
        assertEquals(testValues[0], gm.getMean(), testValues[0] / 99999999, "One value, any weight has mean equal to value");
        am = new ArithmeticMean<Double, Double>();
        hm = new HarmonicMean<Double, Double>();
        gm = new GeometricMean<Double, Double>();
        assertEquals(am, am.add(testValues, testWeights), "add returns object for method chaining");
        assertEquals(sum / sumWeights, am.getMean(), sum / sumWeights / 99999999, "arithmetic mean");
        hm.add(testValues, testWeights);
        assertEquals(sumWeights / recipSum, hm.getMean(), sumWeights / recipSum / 999999999, "harmonic mean");
        gm.add(testValues, testWeights);
        assertEquals(geometricMean, gm.getMean(), geometricMean / 99999999, "geometric mean");
        Double[] shortArray = Arrays.copyOfRange(testValues, 0, 2);
        try
        {
            am.add(shortArray, testWeights);
            fail("Short array of values should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        shortArray = Arrays.copyOfRange(testWeights, 0, 2);
        try
        {
            am.add(testValues, shortArray);
            fail("Short array of weights should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        am = new ArithmeticMean<Double, Double>();
        hm = new HarmonicMean<Double, Double>();
        gm = new GeometricMean<Double, Double>();
        assertEquals(am, am.add(new ArrayList<Double>(Arrays.asList(testValues)), new ArrayList<Double>(Arrays.asList(testWeights))),
                "add returns object for method chaining");
        assertEquals(sum / sumWeights, am.getMean(), sum / sumWeights / 99999999, "arithmetic mean");
        hm.add(new ArrayList<Double>(Arrays.asList(testValues)), new ArrayList<Double>(Arrays.asList(testWeights)));
        assertEquals(sumWeights / recipSum, hm.getMean(), sumWeights / recipSum / 999999999, "harmonic mean");
        gm.add(new ArrayList<Double>(Arrays.asList(testValues)), new ArrayList<Double>(Arrays.asList(testWeights)));
        assertEquals(geometricMean, gm.getMean(), geometricMean / 99999999, "geometric mean");

        List<Double> shortList = new ArrayList<Double>(Arrays.asList(testValues));
        shortList.remove(2);
        try
        {
            am.add(shortList, new ArrayList<Double>(Arrays.asList(testWeights)));
            fail("Short list of values should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }
        shortList = new ArrayList<Double>(Arrays.asList(testWeights));
        shortList.remove(2);
        try
        {
            am.add(new ArrayList<Double>(Arrays.asList(testValues)), shortList);
            fail("Short list of weights should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            // Ignore expected exception
        }

        am = new ArithmeticMean<Double, Double>();
        hm = new HarmonicMean<Double, Double>();
        gm = new GeometricMean<Double, Double>();
        assertEquals(am, am.add(map), "add returns object for method chaining");
        assertEquals(sum / sumWeights, am.getMean(), sum / sumWeights / 99999999, "arithmetic mean");
        hm.add(map);
        assertEquals(sumWeights / recipSum, hm.getMean(), sumWeights / recipSum / 999999999, "harmonic mean");
        gm.add(map);
        assertEquals(geometricMean, gm.getMean(), geometricMean / 99999999, "geometric mean");

        am = new ArithmeticMean<Double, Double>();
        hm = new HarmonicMean<Double, Double>();
        gm = new GeometricMean<Double, Double>();
        assertEquals(am, am.add(new ArrayList<Double>(Arrays.asList(testValues)), (final Double v) -> map.get(v)),
                "add returns object for method chaining");
        assertEquals(sum / sumWeights, am.getMean(), sum / sumWeights / 99999999, "arithmetic mean");
        hm.add(new ArrayList<Double>(Arrays.asList(testValues)), (final Double v) -> map.get(v));
        assertEquals(sumWeights / recipSum, hm.getMean(), sumWeights / recipSum / 999999999, "harmonic mean");
        gm.add(new ArrayList<Double>(Arrays.asList(testValues)), (final Double v) -> map.get(v));
        assertEquals(geometricMean, gm.getMean(), geometricMean / 99999999, "geometric mean");

        am = new ArithmeticMean<Double, Double>();
        hm = new HarmonicMean<Double, Double>();
        gm = new GeometricMean<Double, Double>();
        Integer[] indices = new Integer[] {0, 1, 2, 3};
        List<Integer> indexList = new ArrayList<>(Arrays.asList(indices));
        assertEquals(am, am.add(indexList, (final Integer i) -> testValues[i], (final Integer i) -> testWeights[i]),
                "add returns object for method chaining");
        assertEquals(sum / sumWeights, am.getMean(), sum / sumWeights / 99999999, "arithmetic mean");
        hm.add(indexList, (final Integer i) -> testValues[i], (final Integer i) -> testWeights[i]);
        assertEquals(sumWeights / recipSum, hm.getMean(), sumWeights / recipSum / 999999999, "harmonic mean");
        gm.add(indexList, (final Integer i) -> testValues[i], (final Integer i) -> testWeights[i]);
        assertEquals(geometricMean, gm.getMean(), geometricMean / 99999999, "geometric mean");

        assertTrue(am.toString().contains("ArithmeticMean"), "toString method returns something descriptive");
        assertTrue(hm.toString().contains("HarmonicMean"), "toString method returns something descriptive");
        assertTrue(gm.toString().contains("GeometricMean"), "toString method returns something descriptive");
    }

}
