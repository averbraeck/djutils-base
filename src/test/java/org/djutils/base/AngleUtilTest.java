package org.djutils.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * AngleUtilTest tests the angle normalization methods in AngleUtil. <br>
 * <br>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class AngleUtilTest
{
    /**
     * Test angle normalization.
     */
    @Test
    public void testAngleNormalization()
    {
        final double pi = Math.PI;
        final double pi05 = 0.5 * pi;
        final double pi15 = 1.5 * pi;
        final double pi20 = 2.0 * pi;
        final double pi100 = 10.0 * pi;

        // center
        assertEquals(pi, AngleUtil.normalizeAroundPi(pi), 1E-6);
        assertEquals(0.0, AngleUtil.normalizeAroundZero(0.0), 1E-6);

        assertEquals(pi, AngleUtil.normalizeAroundPi(pi + pi20), 1E-6);
        assertEquals(0.0, AngleUtil.normalizeAroundZero(0.0 + pi20), 1E-6);
        assertEquals(pi, AngleUtil.normalizeAroundPi(pi - pi20), 1E-6);
        assertEquals(0.0, AngleUtil.normalizeAroundZero(0.0 - pi20), 1E-6);

        assertEquals(pi, AngleUtil.normalizeAroundPi(pi + pi100), 1E-6);
        assertEquals(0.0, AngleUtil.normalizeAroundZero(0.0 + pi100), 1E-6);
        assertEquals(pi, AngleUtil.normalizeAroundPi(pi - pi100), 1E-6);
        assertEquals(0.0, AngleUtil.normalizeAroundZero(0.0 - pi100), 1E-6);

        // quart
        assertEquals(pi05, AngleUtil.normalizeAroundPi(pi05), 1E-6);
        assertEquals(pi15, AngleUtil.normalizeAroundPi(pi15), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundZero(pi05), 1E-6);
        assertEquals(-pi05, AngleUtil.normalizeAroundZero(-pi05), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundZero(-pi15), 1E-6);
        assertEquals(-pi05, AngleUtil.normalizeAroundZero(pi15), 1E-6);

        assertEquals(pi05, AngleUtil.normalizeAroundPi(pi05 + pi20), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundPi(pi05 - pi20), 1E-6);
        assertEquals(pi15, AngleUtil.normalizeAroundPi(pi15 + pi20), 1E-6);
        assertEquals(pi15, AngleUtil.normalizeAroundPi(pi15 - pi20), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundZero(pi05 + pi20), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundZero(pi05 - pi20), 1E-6);
        assertEquals(-pi05, AngleUtil.normalizeAroundZero(pi20 - pi05), 1E-6);
        assertEquals(-pi05, AngleUtil.normalizeAroundZero(-pi20 - pi05), 1E-6);

        assertEquals(pi05, AngleUtil.normalizeAroundPi(pi05 + pi100), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundPi(pi05 - pi100), 1E-6);
        assertEquals(pi15, AngleUtil.normalizeAroundPi(pi15 + pi100), 1E-6);
        assertEquals(pi15, AngleUtil.normalizeAroundPi(pi15 - pi100), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundZero(pi05 + pi100), 1E-6);
        assertEquals(pi05, AngleUtil.normalizeAroundZero(pi05 - pi100), 1E-6);
        assertEquals(-pi05, AngleUtil.normalizeAroundZero(pi100 - pi05), 1E-6);
        assertEquals(-pi05, AngleUtil.normalizeAroundZero(-pi100 - pi05), 1E-6);

        // edges plus or minus a small number
        final double eps = 1E-8;
        assertEquals(pi, AngleUtil.normalizeAroundZero(pi - eps), 1E-6);
        assertEquals(-pi, AngleUtil.normalizeAroundZero(pi + eps), 1E-6);
        assertEquals(-pi, AngleUtil.normalizeAroundZero(-pi + eps), 1E-6);
        assertEquals(pi, AngleUtil.normalizeAroundZero(-pi - eps), 1E-6);

        assertEquals(0.0, AngleUtil.normalizeAroundPi(eps), 1E-6);
        assertEquals(pi20, AngleUtil.normalizeAroundPi(-eps), 1E-6);
        assertEquals(pi20, AngleUtil.normalizeAroundPi(pi20 - eps), 1E-6);
        assertEquals(0.0, AngleUtil.normalizeAroundPi(pi20 + eps), 1E-6);

        // NaN and Infinity should result in NaN
        assertTrue(Double.isNaN(AngleUtil.normalizeAroundPi(Double.NaN)));
        assertTrue(Double.isNaN(AngleUtil.normalizeAroundZero(Double.NaN)));
        assertTrue(Double.isNaN(AngleUtil.normalizeAroundPi(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(AngleUtil.normalizeAroundZero(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(AngleUtil.normalizeAroundPi(Double.NEGATIVE_INFINITY)));
        assertTrue(Double.isNaN(AngleUtil.normalizeAroundZero(Double.NEGATIVE_INFINITY)));
    }

    /**
     * Test angle epsilonEquals.
     */
    @Test
    public void testAngleEpsilonEquals()
    {
        final double pi = Math.PI;
        final double pi05 = 0.5 * pi;
        final double pi15 = 1.5 * pi;
        final double pi20 = 2.0 * pi;

        assertTrue(AngleUtil.epsilonEquals(pi, 3, 0.2));
        assertTrue(AngleUtil.epsilonEquals(pi, 3.14, 0.01));
        assertFalse(AngleUtil.epsilonEquals(pi, 3.14, 0.001));
        assertTrue(AngleUtil.epsilonEquals(3, pi, 0.2));
        assertTrue(AngleUtil.epsilonEquals(3.14, pi, 0.01));
        assertFalse(AngleUtil.epsilonEquals(3.14, pi, 0.001));

        assertTrue(AngleUtil.epsilonEquals(pi, -pi, 1E-6));
        assertTrue(AngleUtil.epsilonEquals(0.0, pi20, 1E-6));
        assertTrue(AngleUtil.epsilonEquals(-pi20, 5.0 * pi20, 1E-6));
        assertFalse(AngleUtil.epsilonEquals(pi05, pi15, 1E-6));
        assertTrue(AngleUtil.epsilonEquals(pi - 1E-7, pi + 1E-7, 1E-6));
        assertTrue(AngleUtil.epsilonEquals(pi20 - 1E-7, pi20 + 1E-7, 1E-6));
        for (double s1 : new double[] {-1E-7, 0.0, 1E-7})
        {
            for (double s2 : new double[] {-1E-7, 0.0, 1E-7})
            {
                assertTrue(AngleUtil.epsilonEquals(s1, pi20 + s2, 1E-6));
                assertTrue(AngleUtil.epsilonEquals(s1, -pi20 + s2, 1E-6));
                assertTrue(AngleUtil.epsilonEquals(-pi + s1, pi + s2, 1E-6));
                for (double a : new double[] {0.0, pi05, pi15, pi20})
                {
                    assertTrue(AngleUtil.epsilonEquals(a + s1, a + s2, 1E-6));
                }
            }
        }
    }

    /**
     * Test angle interpolation.
     */
    @Test
    public void testAngleInterpolate()
    {
        assertEquals(0.1, AngleUtil.interpolateClockwise(0.1, 0.2, 0.0), 1E-6);
        assertEquals(0.2, AngleUtil.interpolateClockwise(0.1, 0.2, 1.0), 1E-6);
        assertEquals(0.15, AngleUtil.interpolateClockwise(0.1, 0.2, 0.5), 1E-6);
        assertEquals(0.3, AngleUtil.interpolateClockwise(0.1, 0.2, 2.0), 1E-6);
        assertEquals(0.05, AngleUtil.interpolateClockwise(0.1, 0.2, -0.5), 1E-6);

        assertEquals(0.1, AngleUtil.interpolateClockwise(0.1, 0.1, 0.0), 1E-6);
        assertEquals(0.1, AngleUtil.interpolateClockwise(0.1, 0.1, 1.0), 1E-6);
        assertEquals(0.1, AngleUtil.interpolateClockwise(0.1, 0.1, -1.0), 1E-6);
        assertEquals(0.1, AngleUtil.interpolateClockwise(0.1, 0.1, 10.0), 1E-6);

        final double pi = Math.PI;
        final double pi05 = 0.5 * pi;
        final double pi15 = 1.5 * pi;
        final double pi20 = 2.0 * pi;

        assertEquals(0.0, AngleUtil.interpolateClockwise(0, pi20, 0.0), 1E-6);
        assertEquals(0.0, AngleUtil.interpolateClockwise(0, pi20, 1.0), 1E-6);
        assertEquals(0.0, AngleUtil.interpolateClockwise(0, pi20, 0.5), 1E-6);
        assertEquals(0.0, AngleUtil.interpolateClockwise(pi20, 0, 0.5), 1E-6);
        assertEquals(pi, Math.abs(AngleUtil.interpolateClockwise(-pi, pi, 0.5)), 1E-6);
        assertEquals(pi, Math.abs(AngleUtil.interpolateClockwise(pi, -pi, 0.5)), 1E-6);

        assertEquals(pi, Math.abs(AngleUtil.interpolateClockwise(pi05, pi15, 0.5)), 1E-6);
        assertEquals(0.0, AngleUtil.interpolateClockwise(pi15, pi05, 0.5), 1E-6);

        assertEquals(pi / 4, AngleUtil.interpolateClockwise(0.0, pi05, 0.5), 1E-6);
        assertEquals(-0.75 * pi, AngleUtil.interpolateClockwise(pi05, 0.0, 0.5), 1E-6);
    }

    /**
     * Test the interpolateShortest method.
     */
    @Test
    public void testInterpolateShortest()
    {
        for (double angle1 = -15 * Math.PI / 6; angle1 < 50; angle1 += Math.PI / 300)
        {
            for (double angle2 = -15 * Math.PI / 6; angle2 < 50; angle2 += Math.PI / 300)
            {
                for (double fraction : new double[] {0, 1, 0.5, 0.1, 0.9})
                {
                    double angleFraction = AngleUtil.interpolateShortest(angle1, angle2, fraction);
                    double check1 = Math.abs(AngleUtil.normalizeAroundZero(angle1 - angleFraction));
                    double check2 = Math.abs(AngleUtil.normalizeAroundZero(angleFraction - angle2));
                    // System.out.println(String.format(
                    // "angle1 %s, normalized %s, angle2 %s, normalized %s, frac %s, check1 %s, check2 %s, fraction %.1f",
                    // printAngle(angle1), printAngle(AngleUtil.normalizeAroundZero(angle1)), printAngle(angle2),
                    // printAngle(AngleUtil.normalizeAroundZero(angle2)), printAngle(angleFraction), printAngle(check1),
                    // printAngle(check2), fraction));
                    if (check1 > Math.PI * fraction)
                    {
                        AngleUtil.interpolateShortest(angle1, angle2, fraction);
                    }
                    assertTrue(check1 <= Math.PI * fraction + 0.000001, "diff of normalized is less than Pi * fraction");
                    assertTrue(check2 <= Math.PI * (1 - fraction) + 0.000001,
                            "diff of normalized complement is less than 2*Pi/3");
                    if (angle1 != angle2)
                    {
                        if (Math.abs(check1) > 0.00001 && Math.abs(check2) > 0.00001)
                        {
                            assertEquals(fraction / (1 - fraction), check1 / check2,
                                    0.0001, "angle should be divided in parts with ratio fraction");
                        }
                    }
                    else
                    {
                        assertEquals(0, check1, 1e-10, "if angles are identical; both parts are zero ");
                        assertEquals(0, check2, 1e-10, "if angles are identical; both parts are zero ");
                    }
                }
            }
        }
        // Test extrapolation lightly
        assertEquals(0.4, AngleUtil.interpolateShortest(0.2, 0.3, 2), 0.00001, "extrapolate at 2 of 0.2, 0.3");
        assertEquals(0.1, AngleUtil.interpolateShortest(0.2, 0.3, -1), 0.00001, "extrapolate at 01 of 0.2, 0.3");
    }

    /**
     * Format an angle.
     * @param angle double; angle in radians
     * @return String; fixed width text representation of angle
     */
    public static String printAngle(final double angle)
    {
        return String.format("%9.6f (%4.0f\u00b0)", angle, Math.toDegrees(angle));
    }

}
