package org.djutils.base;

/**
 * AngleUtil has some base methods to deal with angles, such as normalization between -PI and PI, and between 0 and 2*PI. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class AngleUtil
{
    /** Utility constructor. */
    private AngleUtil()
    {
        // Utility constructor
    }

    /** The value 2&pi;. */
    public static final double PI2 = 2.0 * Math.PI;

    /**
     * Normalize an angle (in radians) in a 2&pi; wide interval around &pi;, resulting in angles in the 0 to 2&pi; interval. An
     * angle value of NaN or Infinity returns NaN.
     * @param angle double; the angle in radians to normalize
     * @return double; the normalized angle in radians
     */
    public static double normalizeAroundPi(final double angle)
    {
        return angle - PI2 * Math.floor(angle / PI2);
    }

    /**
     * Normalize an angle (in radians) in a 2&pi; wide interval around 0, resulting in angles in the -&pi; to &pi; interval. An
     * angle value of NaN or Infinity returns NaN.
     * @param angle double; the angle in radians to normalize
     * @return double; the normalized angle in radians
     */
    public static double normalizeAroundZero(final double angle)
    {
        return angle + PI2 * Math.floor((-angle + Math.PI) / PI2);
    }

    /**
     * Return whether two angles (in radians) are less than epsilon apart. The method returns true, for instance when
     * epsilonEquals(-Math.PI, Math.PI, 1E-6) is called, since the -PI and +PI angles have the same angle.
     * @param angle1 double; the first angle in radians
     * @param angle2 double; the second angle in radians
     * @param epsilon double; the precision
     * @return whether the two angles are less than epsilon apart
     */
    public static boolean epsilonEquals(final double angle1, final double angle2, final double epsilon)
    {
        if (Math.abs(angle1 - angle2) < epsilon)
        {
            return true;
        }
        double diff = Math.abs(normalizeAroundZero(angle2) - normalizeAroundZero(angle1));
        return diff < epsilon || Math.abs(PI2 - diff) < epsilon;
    }

    /**
     * Interpolate between two angles that will first be normalized between 0 and 2&pi;, and take the interpolated angle when
     * going clockwise from the first angle to the second angle. The fraction indicates where the angle between angle1 and
     * angle2 will be. A fraction of 0.0 returns angle1, a fraction of 1.0 returns angle2, and a fraction of 0.5 returns the
     * shortest angle halfway. A fraction of less than 0 or larger than 1 can be used for extrapolation. The resulting angle is
     * normalized between -&pi; and &pi;. This means that the halfway interpolation between 0 and 2&pi; and between 2&pi; and 0
     * are both 0, since the two angles are the same. Likewise, the halfway interpolation between -&pi; and &pi; is +/- &pi;
     * where the sign depends on rounding.
     * @param angle1 double; the first angle in radians
     * @param angle2 double; the second angle in radians
     * @param fraction double; the fraction for interpolation; 0.5 is halfway
     * @return the normalized angle between angle1 and angle2
     */
    public static double interpolateClockwise(final double angle1, final double angle2, final double fraction)
    {
        double a1 = normalizeAroundPi(angle1);
        double a2 = normalizeAroundPi(angle2);
        if (a2 < a1)
        {
            a2 += PI2;
        }
        return normalizeAroundZero((1.0 - fraction) * a1 + fraction * a2);
    }

    /**
     * Interpolate between two angles taking the <i>shortest way</i>.
     * @param angle1 double; the first angle in radians
     * @param angle2 double; the second angle in radians
     * @param fraction double; the fraction for interpolation; 0.5 is halfway, 0.0 returns angle1, 1.0 returns angle2.
     * @return double; The interpolated angle, normalized around zero. If any input angle is not normalized around zero the
     *         result will still be normalized, and the exact equality at fraction is 0.0 or 1.0 may not hold. When the
     *         difference between angle1 and angle2 is very close to an odd multiple of PI, the <i>shortest way</i> between
     *         those angles is ill-defined. The result of this method reflects this (fundamental) problem.
     */
    public static double interpolateShortest(final double angle1, final double angle2, final double fraction)
    {
        double result;
        if (fraction == 1.0)
        {
            result = angle2; // Make sure that the promise of the exact result holds
        }
        else
        {
            double difference = angle2 - angle1;
            if (difference < -Math.PI)
            {
                difference = difference - PI2 * Math.ceil((difference - Math.PI) / PI2);
            }
            if (difference > Math.PI)
            {
                difference = difference - PI2 * Math.floor((difference + Math.PI) / PI2);
            }
            result = angle1 + fraction * difference;
        }
        return normalizeAroundZero(result);
    }

}
