package org.djutils.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.djutils.base.AngleUtil;
import org.junit.jupiter.api.Test;

/**
 * TestComplexMath.java. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestComplexMath
{

    /**
     * Test the square root function.
     */
    @Test
    public void testSqrt()
    {
        // Start by testing the bloody obvious
        assertEquals(Complex.I, ComplexMath.sqrt(new Complex(-1)), "square root of -1 is I");
        Complex in = new Complex(0, -4);
        Complex c = ComplexMath.sqrt(in);
        assertEquals(2, c.norm(), 0.000001, "square root of " + in + " norm");
        assertEquals(-Math.PI / 4, c.phi(), 0.000001, "square root of " + in + " phi");
        double[] values = new double[] {0, 1, 0.01, 100, Math.PI, -Math.E};
        for (double re : values)
        {
            for (double im : values)
            {
                in = new Complex(re, im);
                c = ComplexMath.sqrt(in);
                assertEquals(Math.sqrt(in.norm()), c.norm(), 0.0001, "square root of " + in + " norm");
                assertEquals(in.phi() / 2, c.phi(), 0.0000001, "square root of " + in + " phi");
                Complex c2 = c.times(c);
                assertEquals(in.re, c2.re, 0.0001, "square of square root re");
                assertEquals(in.im, c2.im, 0.0001, "square of square root im");
            }
        }
    }

    /**
     * Test the cube root function.
     */
    @Test
    public void testCbrt()
    {
        double[] values = new double[] {0, 1, 0.01, 100, Math.PI, -Math.E};
        for (double re : values)
        {
            for (double im : values)
            {
                Complex in = new Complex(re, im);
                Complex c = ComplexMath.cbrt(in);
                assertEquals(Math.cbrt(in.norm()), c.norm(), 0.0001, "cube root of " + in + " norm");
                assertEquals(in.phi() / 3, c.phi(), 0.0000001, "cube root of " + in + " phi");
                Complex c3 = c.times(c).times(c);
                assertEquals(in.re, c3.re, 0.0001, "cube of cube root re");
                assertEquals(in.im, c3.im, 0.0001, "cube of cube root im");
            }
        }
    }

    /**
     * Test the exponential function.
     */
    @Test
    public void testExp()
    {
        assertEquals(Math.E, ComplexMath.exp(Complex.ONE).re, 0.000001, "exp of 1 is e; re");
        assertEquals(0, ComplexMath.exp(Complex.ONE).im, 0.000001, "exp of 1 is e; im");
        for (double re : new double[] {0, 1, Math.PI, -Math.E, 10, -10})
        {
            for (double im : new double[] {0, 0.1, Math.PI / 2, Math.PI, 5, -1, -Math.E, -50})
            {
                Complex in = new Complex(re, im);
                Complex out = ComplexMath.exp(in);
                assertEquals(Math.exp(re) * Math.cos(im), out.re, 0.01, "exp(" + in + ") re");
                assertEquals(Math.exp(re) * Math.sin(im), out.im, 0.01, "exp(" + in + ") im");
            }
        }
    }

    /**
     * Test the natural logarithm function.
     */
    @Test
    public void testLog()
    {
        assertEquals(Complex.ZERO, ComplexMath.ln(Complex.ONE), "ln(ONE) is ZERO");
        Complex in = new Complex(Math.E);
        Complex out = ComplexMath.ln(in);
        assertEquals(1, out.re, 0.00000001, "ln(e) is ONE re");
        assertEquals(0, out.im, 0.00000001, "ln(e) is ONE im");
        for (double re : new double[] {0, 1, Math.PI, 10, -Math.E, -10})
        {
            for (double im : new double[] {0, 0.1, Math.PI / 2, Math.PI, 5, -1, -Math.E, -50})
            {
                in = new Complex(re, im);
                out = ComplexMath.ln(in);
                assertEquals(Math.log(in.norm()), out.re, 0.01, "ln(" + in + ") re");
                assertEquals(Math.atan2(im, re), out.im, 0.00001, "ln(" + in + ") im");
            }
        }
    }

    /**
     * Test the sine, cosine and tangent functions.
     */
    @Test
    public void testSinCosTan()
    {
        assertEquals(Complex.ZERO, ComplexMath.sin(Complex.ZERO), "sin(ZERO) is ZERO");
        Complex c = ComplexMath.cos(Complex.ZERO);
        assertEquals(1, c.re, 0.00001, "cos(ZERO) is ONE: re");
        assertEquals(0, c.im, 0.00001, "cos(ZERO) is ONE: im");
        assertEquals(Complex.ZERO, ComplexMath.tan(Complex.ZERO), "tan(ZERO) is ZERO");
        double[] values = new double[] {0, 1, Math.PI, 10, -Math.E, -10};
        for (double re : values)
        {
            for (double im : values)
            {
                Complex in = new Complex(re, im);
                Complex sin = ComplexMath.sin(in);
                assertEquals(Math.sin(re) * Math.cosh(im), sin.re, 0.0001, "sin(" + in + ") re");
                assertEquals(Math.cos(re) * Math.sinh(im), sin.im, 0.0001, "sin(" + in + ") im");
                Complex cos = ComplexMath.cos(in);
                assertEquals(Math.cos(re) * Math.cosh(im), cos.re, 0.0001, "cos(" + in + ") re");
                assertEquals(-Math.sin(re) * Math.sinh(im), cos.im, 0.0001, "cos(" + in + ") im");
                Complex tan = ComplexMath.tan(in);
                Complex div = sin.divideBy(cos);
                assertEquals(sin.norm() / cos.norm(), div.norm(), 0.00001, "div norm");
                assertEquals(AngleUtil.normalizeAroundZero(sin.phi() - cos.phi()), div.phi(), 0.0001, "div phi");
                assertEquals(div.re, tan.re, 0.0001, "tan(" + in + ") re");
                assertEquals(div.im, tan.im, 0.0001, "tan(" + in + ") im");
                Complex sin2plusCos2 = sin.times(sin).plus(cos.times(cos));
                assertEquals(1, sin2plusCos2.re, 0.00001, "sin^2 + cos^2 re");
                assertEquals(0, sin2plusCos2.im, 0.00001, "sin^2 + cos^2 im");
            }
        }
    }

    /**
     * Test the sinh, cosh, tanh functions.
     */
    @Test
    public void testSinhCoshTanH()
    {
        double[] values = new double[] {0, 1, Math.PI, 10, -Math.E, -10};
        for (double re : values)
        {
            for (double im : values)
            {
                Complex in = new Complex(re, im);
                Complex sinh = ComplexMath.sinh(in);
                Complex cosh = ComplexMath.cosh(in);
                Complex tanh = ComplexMath.tanh(in);

                // System.out.println(" in=" + printComplex(in) + "\ntanh=" + printComplex(tanh));
                assertEquals(Math.sinh(re) * Math.cos(im), sinh.re, 0.0001, "sinh re");
                assertEquals(Math.cosh(re) * Math.sin(im), sinh.im, 0.0001, "sinh im");
                assertEquals(Math.cosh(re) * Math.cos(im), cosh.re, 0.0001, "cosh re");
                assertEquals(Math.sinh(re) * Math.sin(im), cosh.im, 0.0001, "cosh im");
                assertEquals(Math.sinh(2 * re) / (Math.cosh(2 * re) + Math.cos(2 * im)), tanh.re, 0.0001, "tanh re");
                assertEquals(Math.sin(2 * im) / (Math.cosh(2 * re) + Math.cos(2 * im)), tanh.im, 0.0001, "tanh im");
                // Alternate way to compute tanh
                Complex alternateTanh = sinh.divideBy(cosh);
                assertEquals(tanh.re, alternateTanh.re, 0.0001, "alternate tanh re");
                assertEquals(tanh.im, alternateTanh.im, 0.0001, "alternate tanh im");
                if (im == 0)
                {
                    // Extra checks
                    assertEquals(Math.sinh(re), sinh.re, 0.0001, "sinh of real re");
                    assertEquals(0, sinh.im, 0.0001, "sinh of real im");
                    assertEquals(Math.cosh(re), cosh.re, 0.0001, "cosh of real re");
                    assertEquals(0, cosh.im, 0.0001, "cosh of real im");
                    assertEquals(Math.tanh(re), tanh.re, 0.0001, "tanh of real re");
                    assertEquals(0, tanh.im, 0.0001, "tahh of real im");
                }
            }
        }
    }

    /**
     * Test the asin, acos and atan functions.
     */
    @Test
    public void testAsinAcosAtan()
    {
        double[] values = new double[] {0, 0.2, 0, 8, 1, -1, -0.2, -0.8, Math.PI, 10, -Math.E, -10};
        for (double re : values)
        {
            for (double im : values)
            {
                Complex in = new Complex(re, im);
                Complex asin = ComplexMath.asin(in);
                Complex acos = ComplexMath.acos(in);
                Complex atan = ComplexMath.atan(in);
                // This is a lousy test; we only verify that asin(sin(asin(z)) roughly equals asin(z)
                Complex asinOfSinOfAsin = ComplexMath.asin(ComplexMath.sin(asin));
                assertEquals(asinOfSinOfAsin.re, asin.re, 0.0001, "asin re");
                assertEquals(asinOfSinOfAsin.im, asin.im, 0.0001, "asin im");
                Complex acosOfCosOfAcos = ComplexMath.acos(ComplexMath.cos(acos));
                assertEquals(acosOfCosOfAcos.re, acos.re, 0.0001, "acos re");
                assertEquals(acosOfCosOfAcos.im, acos.im, 0.0001, "acos im");
                Complex atanOfTanOfAtan = ComplexMath.atan(ComplexMath.tan(atan));
                if (Math.abs(atan.re) < 100)
                {
                    assertEquals(atanOfTanOfAtan.re, atan.re, 0.0001, "atan re");
                    assertEquals(atanOfTanOfAtan.im, atan.im, 0.0001, "atan im");
                }
                if (im == 0 && re >= -1 && re <= 1)
                {
                    // Extra checks
                    assertEquals(Math.asin(re), asin.re, 0.00001, "asin of real in range -1, 1 re");
                    assertEquals(0, asin.im, 0.00001, "asin of real in range -1, 1 im");
                    assertEquals(Math.acos(re), acos.re, 0.00001, "acos of real in range -1, 1 re");
                    assertEquals(0, acos.im, 0.00001, "acos of real in range -1, 1 im");
                }
                else if (im == 0)
                {
                    assertEquals(Math.atan(re), atan.re, 0.00001, "atan of real re");
                    assertEquals(0, atan.im, 0.00001, "atan of real, 1 im");

                }
            }
        }
    }

    /**
     * Test the asinh function.
     */
    @Test
    public void testAsinh()
    {
        double[] values = new double[] {0, 0.2, 0, 8, 1, -1, -0.2, -0.8, Math.PI, 10, -Math.E, -10};
        for (double re : values)
        {
            for (double im : values)
            {
                Complex in = new Complex(re, im);
                Complex asinh = ComplexMath.asinh(in);
                Complex acosh = ComplexMath.acosh(in);
                Complex atanh = ComplexMath.atanh(in);
                // This is a lousy test; we only verify that asinh(sinh(asinh(z)) roughly equals asinh(z)
                Complex asinhOfSinhOfAsinh = ComplexMath.asinh(ComplexMath.sinh(asinh));
                assertEquals(asinhOfSinhOfAsinh.re, asinh.re, 0.0001, "asinh re");
                assertEquals(asinhOfSinhOfAsinh.im, asinh.im, 0.0001, "asinh im");
                Complex acoshOfCoshOfAcosh = ComplexMath.acosh(ComplexMath.cosh(acosh));
                if (im != 0 || re > 1.0)
                {
                    // acosh is unstable around im == 0 && re <= 1.0; see <a
                    // href="https://mathworld.wolfram.com/InverseHyperbolicCosine.html">Wolfram mathWorld: Inverse Hyperbolic
                    // Cosine<//a> so we can't use this test there.
                    assertEquals(acoshOfCoshOfAcosh.re, acosh.re, 0.0001, "acosh re");
                    assertEquals(acoshOfCoshOfAcosh.im, acosh.im, 0.0001, "acosh im");
                }
                Complex atanhOfTanhOfAtanh = ComplexMath.atanh(ComplexMath.tanh(atanh));
                if (im != 0 || re > -1.0 && re < 1.0)
                {
                    // atanh is unstable around im == 0 && re <= -1 && re >= 1; see <a
                    // "https://mathworld.wolfram.com/InverseHyperbolicTangent.html">Wolfram mathWorld: Inverse Hyperbolic
                    // Tangent</a>, so we can't use this test there.
                    // System.out.println(" in=" + printComplex(in) + "\natanh=" + printComplex(atanh));
                    if (im != 1 && im != -1 || re != 0)
                    {
                        // Also unstable around i and minus i as the atan function is unstable around -1
                        assertEquals(atanhOfTanhOfAtanh.re, atanh.re, 0.0001, "atanh re");
                        assertEquals(atanhOfTanhOfAtanh.im, atanh.im, 0.0001, "atanh im");
                    }
                }

                if (im == 0)
                {
                    // Extra checks
                    assertEquals(doubleAsinh(re), asinh.re, 0.00001, "asinh of real re");
                    assertEquals(0, asinh.im, 0.00001, "asinh of real im");
                    if (re >= 1.0)
                    {
                        assertEquals(doubleAcosh(re), acosh.re, 0.00001, "acosh of real re");
                        assertEquals(0, acosh.im, 0.00001, "acosh of real im");
                    }
                    if (re > -1.0 && re < 1.0)
                    {
                        assertEquals((Math.log(1 + re) - Math.log(1 - re)) / 2, atanh.re, 0.00001, "atanh of real re");
                        assertEquals(0, atanh.im, 0.00001, "acosh of real im");
                    }
                }
            }
        }
    }

    /**
     * Copied from <a href="https://forgetcode.com/java/1746-asinh-return-the-hyperbolic-sine-of-value-as-a-argument">Forget
     * Code asinh</a>.
     * @param x double; the argument
     * @return double; the inverse hyperbolic cosine of x
     */
    public static double doubleAsinh(final double x)
    {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    /**
     * Copied from <a href="https://forgetcode.com/Java/1747-acosh-Return-the-hyperbolic-Cosine-of-value-as-a-Argument">Forget
     * Code acosh</a>.
     * @param x double; the argument
     * @return double; the inverse hyperbolic cosine of x
     */
    public static double doubleAcosh(final double x)
    {
        return Math.log(x + Math.sqrt(x * x - 1.0));
    }

    /**
     * @param c Complex;
     * @return String
     */
    public static String printComplex(final Complex c)
    {
        return String.format("re=%10.6f, im=%10.6f, norm=%10.6f, phi=%10.6f(=%10.6f\u00b0)", c.re, c.im, c.norm(), c.phi(),
                Math.toDegrees(c.phi()));
    }

}
