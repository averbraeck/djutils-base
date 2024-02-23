package org.djutils.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.djutils.base.AngleUtil;
import org.junit.jupiter.api.Test;

/**
 * TestComplex.java. <br>
 * <br>
 * Copyright (c) 2021-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestComplex
{

    /**
     * Test the various constructors of Complex.
     */
    @Test
    public void testConstructors()
    {
        double[] testValues = new double[] { 0, 1, 100, -1, -10000, Math.PI };

        for (double re : testValues)
        {
            for (double im : testValues)
            {
                Complex complex = new Complex(re, im);
                assertEquals(re, complex.re, 0.0001, "re");
                assertEquals(im, complex.im, 0.0001, "im");
                assertEquals(re, complex.getRe(), 0.0001, "getRe");
                assertEquals(im, complex.getIm(), 0.0001, "getIm");
                assertEquals(Math.hypot(re, im), complex.norm(), 0.0001, "norm");
                if (re != 0 || im != 0)
                {
                    assertEquals(Math.atan2(im, re), complex.phi(), 0.000001, "phi");
                }
                if (im == 0)
                {
                    assertTrue(complex.isReal(), "If imaginary part is 0; complex is pure real");
                    complex = new Complex(re);
                    assertEquals(re, complex.re, 0.0001, "re");
                    assertEquals(im, complex.im, 0.0001, "im");
                    assertEquals(Math.hypot(re, im), complex.norm(), 0.0001, "norm");
                    if (re != 0)
                    {
                        assertEquals(Math.atan2(im, re), complex.phi(), 0.000001, "phi");
                    }
                }
                else
                {
                    assertFalse(complex.isReal(), "If imaginary part is not null; complex is not pure real");
                }
                if (re == 0)
                {
                    assertTrue(complex.isImaginary(), "If real part is 0; complex is imaginary");
                }
                else
                {
                    assertFalse(complex.isImaginary(), "If real part is not 0; comples is not imaginary");
                }
                Complex conjugate = complex.conjugate(); // Loss less operation; we can test for exact equality
                assertEquals(complex.re, conjugate.re, 0, "Conjugate re");
                assertEquals(-complex.im, conjugate.im, 0, "Conjugate im");
            }
        }
    }

    /**
     * Test the constants defined by the Complex class.
     */
    @Test
    public void testConstants()
    {
        assertEquals(0, Complex.ZERO.re, 0, "real component of ZERO");
        assertEquals(0, Complex.ZERO.im, 0, "imaginary component of ZERO");
        assertEquals(1, Complex.ONE.re, 0, "real component of ONE");
        assertEquals(0, Complex.ONE.im, 0, "imaginary component of ONE");
        assertEquals(-1, Complex.MINUS_ONE.re, 0, "real component of MINUS_ONE");
        assertEquals(0, Complex.MINUS_ONE.im, 0, "imaginary component of MINUS_ONE");
        assertEquals(0, Complex.I.re, 0, "real component of I");
        assertEquals(1, Complex.I.im, 0, "imaginary component of I");
        assertEquals(0, Complex.MINUS_I.re, 0, "real component of MINUS_I");
        assertEquals(-1, Complex.MINUS_I.im, 0, "imaginary component of MINUS_I");
    }

    /**
     * Test the methods.
     */
    @Test
    public void testOperations()
    {
        Complex a = new Complex(12, -34);
        Complex b = new Complex(-23, 45);
        Complex c = a.plus(b);
        assertEquals(a.re + b.re, c.re, 0.00001, "sum re");
        assertEquals(a.im + b.im, c.im, 0.00001, "sum im");
        c = a.plus(123);
        assertEquals(a.re + 123, c.re, 0.00001, "sum re");
        assertEquals(a.im, c.im, 0.00001, "sum im");
        c = a.minus(b);
        assertEquals(a.re - b.re, c.re, 0.00001, "difference re");
        assertEquals(a.im - b.im, c.im, 0.00001, "difference im");
        c = a.minus(123);
        assertEquals(a.re - 123, c.re, 0.00001, "difference re");
        assertEquals(a.im, c.im, 0.00001, "difference im");
        c = a.times(b);
        assertEquals(a.norm() * b.norm(), c.norm(), 0.0001, "product norm");
        assertEquals(a.phi() + b.phi(), c.phi(), 0.000001, "product phi");
        c = a.times(123);
        assertEquals(a.norm() * 123, c.norm(), 0.0001, "product norm");
        assertEquals(a.phi(), c.phi(), 0.000001, "product phi");
        c = a.reciprocal();
        assertEquals(a.norm(), 1 / c.norm(), 0.00001, "norm of reciprocal");
        assertEquals(-a.phi(), c.phi(), 0.000001, "phi of reciprocal");
        c = a.times(c);
        assertEquals(1, c.re, 0.00001, "a * a.reciprocal re");
        assertEquals(0, c.im, 0.00001, "a * a.reciprocal im");
        for (double angle : new double[] { 0, 0.1, 1, Math.E, Math.PI, 5, 10, -1, -5 })
        {
            c = a.rotate(angle);
            assertEquals(a.norm(), c.norm(), 0.00001, "rotated a norm");
            assertEquals(AngleUtil.normalizeAroundZero(a.phi() + angle), c.phi(), 0.000001, "rotation difference");
        }
        c = a.divideBy(b);
        assertEquals(a.norm() / b.norm(), c.norm(), 0.0000001, "norm of division");
        assertEquals(AngleUtil.normalizeAroundZero(a.phi() - b.phi()), c.phi(), 0.000001, "phi of division");
        c = a.divideBy(123);
        assertEquals(a.re / 123, c.re, 0.0000001, "dividend re");
        assertEquals(a.im / 123, c.im, 0.0000001, "dividend im");
        c = Complex.ZERO.divideBy(Complex.ZERO);
        assertTrue(Double.isNaN(c.re), "ZERO / ZERO re is NaN");
        assertTrue(Double.isNaN(c.im), "ZERO / ZERO im is NaN");
        c = Complex.ONE.divideBy(Complex.ZERO);
        assertTrue(Double.isInfinite(c.re) && c.re > 0, "ONE / ZERO re is positive Infinity");
        assertTrue(Double.isNaN(c.im), "ONE / ZERO im is NaN");
        c = Complex.ZERO.minus(Complex.ONE).divideBy(Complex.ZERO);
        assertTrue(Double.isInfinite(c.re) && c.re < 0, "minus ONE / ZERO re is negative Infinity");
        assertTrue(Double.isNaN(c.im), "minus ONE / ZERO im is NaN");
        c = Complex.I.divideBy(Complex.ZERO);
        assertTrue(Double.isNaN(c.re), "I / ZERO re is NaN");
        assertTrue(Double.isInfinite(c.im) && c.im > 0, "I / ZERO im is positive Infinity");
        c = Complex.ZERO.minus(Complex.I).divideBy(Complex.ZERO);
        assertTrue(Double.isNaN(c.re), "minus I / ZERO re is NaN");
        assertTrue(Double.isInfinite(c.im) && c.im < 0, "minus I / ZERO im is positive Infinity");
        c = Complex.ZERO.reciprocal();
        assertTrue(Double.isInfinite(c.re) && c.re > 0, "reciprocal of ZERO re is positive Infinity ");
        assertTrue(Double.isInfinite(c.im) && c.im > 0, "reciprocal of ZERO im is positive Infinity ");
        c = Complex.ONE.divideBy(Complex.I);
        assertEquals(0, c.re, 0, "ONE / I re is 0");
        assertEquals(-1, c.im, 0, "ONE / I im is -1");
        c = Complex.ZERO.divideBy(0.0);
        assertTrue(Double.isNaN(c.re), "ZERO / 0.0 re is NaN");
        assertTrue(Double.isNaN(c.im), "ZERO / 0.0 im is NaN");
        c = Complex.ONE.divideBy(0.0);
        assertTrue(Double.isInfinite(c.re) && c.re > 0, "ONE / 0.0 re is positive Infinity");
        assertTrue(Double.isNaN(c.im), "ONE / 0.0 im is NaN");
        c = Complex.ZERO.minus(Complex.ONE).divideBy(0.0);
        assertTrue(Double.isInfinite(c.re) && c.re < 0, "minus ONE / 0.0 re is negative Infinity");
        assertTrue(Double.isNaN(c.im), "minus ONE / 0.0 im is NaN");
        c = Complex.I.divideBy(0.0);
        assertTrue(Double.isNaN(c.re), "I / 0.0 re is NaN");
        assertTrue(Double.isInfinite(c.im) && c.im > 0, "I / 0.0 im is positive Infinity");
        c = Complex.ZERO.minus(Complex.I).divideBy(0.0);
        assertTrue(Double.isNaN(c.re), "minus I / 0.0 re is NaN");
        assertTrue(Double.isInfinite(c.im) && c.im < 0, "minus I / 0.0 im is positive Infinity");
    }

    /**
     * Test other methods.
     */
    @SuppressWarnings({ "unlikely-arg-type" })
    @Test
    public void testOthers()
    {
        Complex a = new Complex(12, 34);
        assertTrue(a.toString().startsWith("Complex "), "toString returns something descriptive");
        assertNotEquals(a.hashCode(), a.plus(Complex.ONE).hashCode(), "hashCode takes re into account");
        assertNotEquals(a.hashCode(), a.plus(Complex.I).hashCode(), "hashCode takes im into account");
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals("This is a String; not a complex"));
        assertFalse(a.equals(new Complex(12, 35)));
        assertFalse(a.equals(new Complex(13, 34)));
        assertEquals(a, new Complex(12, 34));
    }

    /**
     * Test the hypot method.
     */
    @Test
    public void testHypot()
    {
        assertTrue(Double.isInfinite(Complex.hypot(Double.POSITIVE_INFINITY, 0)), "hypot(Inf, 0)");
        assertTrue(Double.isInfinite(Complex.hypot(0, Double.POSITIVE_INFINITY)), "hypot(0, Inf)");
        assertTrue(Double.isInfinite(Complex.hypot(Double.NEGATIVE_INFINITY, 0)), "hypot(-Inf, 0)");
        assertTrue(Double.isInfinite(Complex.hypot(0, Double.NEGATIVE_INFINITY)), "hypot(0, Inf)");
        assertEquals(0, Complex.hypot(0, 0), 0, "hypot(0, 0)");
        assertTrue(Double.isNaN(Complex.hypot(Double.NaN, 0)), "hypot(Nan, 0)");
        assertTrue(Double.isNaN(Complex.hypot(0, Double.NaN)), "hypot(0, Nan)");
        assertTrue(Double.isNaN(Complex.hypot(Double.NaN, Double.NaN)), "hypot(Nan, Nan)");
        assertEquals(Math.sqrt(2), Complex.hypot(1, 1), 0.000001, "hypot(1, 1");
        assertEquals(Double.MAX_VALUE, Complex.hypot(Double.MAX_VALUE, 0), Double.MAX_VALUE / 100000,
                "hypot(Double.MAX_VALUE, 0)");
        assertEquals(Double.MAX_VALUE, Complex.hypot(0, Double.MAX_VALUE), Double.MAX_VALUE / 100000,
                "hypot(0, Double.MAX_VALUE)");
        assertEquals(Double.MIN_VALUE, Complex.hypot(Double.MIN_VALUE, 0), 0, "hypot(Double.MIN_VALUE, 0)");
        assertEquals(Double.MIN_VALUE, Complex.hypot(0, Double.MIN_VALUE), 0, "hypot(0, Double.MIN_VALUE)");
        assertEquals(Double.MAX_VALUE / Math.sqrt(2), Complex.hypot(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2),
                Double.MAX_VALUE / 100000, "hypot(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2)");
        assertEquals(Double.MIN_VALUE * 10 *  Math.sqrt(2), Complex.hypot(Double.MIN_VALUE * 10, Double.MIN_VALUE * 10),
                Double.MIN_VALUE, "hypot(Double.MIN_VALUE * 10, Double.MIN_VALUE * 10)");
        assertEquals(Complex.hypot(10, 0), Complex.hypot(-10, 0), 0, "hypot is symmetrical in x");
        assertEquals(Complex.hypot(0, 10), Complex.hypot(0, -10), 0, "hypot is symmetrical in y");
    }

}
