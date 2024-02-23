package org.djutils.complex;

import org.djutils.base.AngleUtil;

/**
 * ComplexMath.java. Math with complex operands and results. <br>
 * Copyright (c) 2021-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class ComplexMath
{

    /**
     * Do not instantiate.
     */
    private ComplexMath()
    {
        // Do not instantiate.
    }

    /**
     * Principal square root of a Complex operand. The principal square root of a complex number has a non-negative real
     * component.
     * @param z Complex; the operand
     * @return Complex; the principal square root of the operand
     */
    public static Complex sqrt(final Complex z)
    {
        double norm = z.norm();
        return new Complex(Math.sqrt((z.re + norm) / 2), (z.im >= 0 ? 1 : -1) * Math.sqrt((-z.re + norm) / 2));
    }

    /**
     * Principal cube root of a Complex operand. The principal cube root of a complex number has <i>the most positive</i>. real
     * component.
     * @param z Complex; the operand
     * @return Complex; the principal cube root of the operand
     */
    public static Complex cbrt(final Complex z)
    {
        double cbrtOfNorm = Math.cbrt(z.norm());
        double phi = z.phi() / 3;
        return new Complex(cbrtOfNorm * Math.cos(phi), cbrtOfNorm * Math.sin(phi));
    }

    /**
     * Exponential function of a Complex operand.
     * @param z Complex; the operand
     * @return Complex; the result of the exponential function applied to the operand
     */
    public static Complex exp(final Complex z)
    {
        double factor = Math.exp(z.re);
        return new Complex(factor * Math.cos(z.im), factor * Math.sin(z.im));
    }

    /**
     * Principal value of the natural logarithm of a Complex operand. See
     * <a href="https://en.wikipedia.org/wiki/Complex_logarithm">Wikipedia Complex logarithm</a>.
     * @param z Complex; the operand
     * @return Complex; the principal value of the natural logarithm of the Complex operand
     */
    public static Complex ln(final Complex z)
    {
        return new Complex(Math.log(z.norm()), z.phi());
    }

    /**
     * Sine function of a Complex operand. See <a href="https://proofwiki.org/wiki/Sine_of_Complex_Number">ProofWiki Sine of
     * Complex Number</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the sine function applied to the operand
     */
    public static Complex sin(final Complex z)
    {
        return new Complex(Math.sin(z.re) * Math.cosh(z.im), Math.cos(z.re) * Math.sinh(z.im));
    }

    /**
     * Cosine function of Complex operand. See <a href="https://proofwiki.org/wiki/Cosine_of_Complex_Number">ProofWiki Cosine of
     * Complex Number</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the cosine function applied to the operand
     */
    public static Complex cos(final Complex z)
    {
        return new Complex(Math.cos(z.re) * Math.cosh(z.im), -Math.sin(z.re) * Math.sinh(z.im));
    }

    /**
     * Tangent function of a Complex operand. See <a href="https://proofwiki.org/wiki/Tangent_of_Complex_Number">ProofWiki
     * Tangent of Complex Number</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the tangent function applied to the operand
     */
    public static Complex tan(final Complex z)
    {
        // Using Formulation 4 of the reference as it appears to need the fewest trigonometric operations
        double divisor = Math.cos(2 * z.re) + Math.cosh(2 * z.im);
        return new Complex(Math.sin(2 * z.re) / divisor, Math.sinh(2 * z.im) / divisor);
    }

    /**
     * Hyperbolic sine function of a Complex operand.
     * @param z Complex; the operand
     * @return Complex; the result of the sinh function applied to the operand
     */
    public static Complex sinh(final Complex z)
    {
        return new Complex(Math.sinh(z.re) * Math.cos(z.im), Math.cosh(z.re) * Math.sin(z.im));
    }

    /**
     * Hyperbolic cosine function of Complex operand.
     * @param z Complex; the operand
     * @return Complex; the result of the cosh function applied to the operand
     */
    public static Complex cosh(final Complex z)
    {
        return new Complex(Math.cosh(z.re) * Math.cos(z.im), Math.sinh(z.re) * Math.sin(z.im));
    }

    /**
     * Hyperbolic tangent function of a Complex operand. Based on
     * <a href="https://proofwiki.org/wiki/Hyperbolic_Tangent_of_Complex_Number">ProofWiki: Hyperbolic Tangent of Complex
     * Number, Formulation 4</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the tanh function applied to the operand
     */
    public static Complex tanh(final Complex z)
    {
        double re2 = z.re * 2;
        double im2 = z.im * 2;
        double divisor = Math.cosh(re2) + Math.cos(im2);
        return new Complex(Math.sinh(re2) / divisor, Math.sin(im2) / divisor);
    }

    /**
     * Inverse sine function of a Complex operand. Derived from <a href=
     * "http://cvsweb.netbsd.org/bsdweb.cgi/~checkout~/src/lib/libm/complex/casin.c?rev=1.1&amp;content-type=text/plain">NetBSD
     * Complex casin.c</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the asin function applied to the operand
     */
    public static Complex asin(final Complex z)
    {
        Complex ct = z.times(Complex.I);
        Complex zz = new Complex((z.re - z.im) * (z.re + z.im), (2.0 * z.re * z.im));

        zz = new Complex(1.0 - zz.re, -zz.im);
        zz = ct.plus(sqrt(zz));
        zz = ln(zz);
        /* multiply by 1/i = -i */
        return zz.times(Complex.MINUS_I);
    }

    /**
     * Inverse cosine function of a Complex operand. Derived from <a href=
     * "http://cvsweb.netbsd.org/bsdweb.cgi/~checkout~/src/lib/libm/complex/cacos.c?rev=1.1&amp;content-type=text/plain">NetBSD
     * Complex cacos.c</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the acos function applied to the operand
     */
    public static Complex acos(final Complex z)
    {
        Complex asin = asin(z);
        return new Complex(Math.PI / 2 - asin.re, -asin.im);
    }

    /** Maximum value of a Complex. May be returned by the atan function. */
    private static final Complex MAXIMUM = new Complex(Double.MAX_VALUE, Double.MAX_VALUE);

    /**
     * Inverse tangent function of a Complex operand. Derived from <a href=
     * "http://cvsweb.netbsd.org/bsdweb.cgi/~checkout~/src/lib/libm/complex/catan.c?rev=1.2&amp;content-type=text/plain">NetBSD
     * Complex catan.c</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the atan function applied to the operand
     */
    public static Complex atan(final Complex z)
    {
        if ((z.re == 0.0) && (z.im > 1.0))
        {
            return MAXIMUM;
        }

        double x2 = z.re * z.re;
        double a = 1.0 - x2 - (z.im * z.im);
        if (a == 0.0)
        {
            return MAXIMUM;
        }

        double re = AngleUtil.normalizeAroundZero(Math.atan2(2.0 * z.re, a)) / 2;
        double t = z.im - 1.0;
        a = x2 + (t * t);
        if (a == 0.0)
        {
            return MAXIMUM;
        }

        t = z.im + 1.0;
        a = (x2 + (t * t)) / a;
        return new Complex(re, 0.25 * Math.log(a));
    }

    /**
     * Inverse hyperbolic sine of a Complex operand.
     * @param z Complex; the operand
     * @return Complex; the result of the asinh function applied to the operand
     */
    public static Complex asinh(final Complex z)
    {
        return Complex.MINUS_I.times(asin(z.times(Complex.I)));
    }

    /**
     * Inverse hyperbolic cosine of a Complex operand.
     * @param z Complex; the operand
     * @return Complex; the result of the acosh function applied to the operand
     */
    public static Complex acosh(final Complex z)
    {
        return ln(z.plus(sqrt(z.plus(Complex.ONE)).times(sqrt(z.minus(Complex.ONE)))));
    }

    /**
     * Inverse hyperbolic tangent of a Complex operand. Derived from
     * <a href="http://cvsweb.netbsd.org/bsdweb.cgi/~checkout~/src/lib/libm/complex/catanh.c?rev=1.1&amp;content-type=text/plain">
     * NetBSD Complex catanh.c</a>.
     * @param z Complex; the operand
     * @return Complex; the result of the atanh function applied to the operand
     */
    public static Complex atanh(final Complex z)
    {
        return Complex.MINUS_I.times(atan(z.times(Complex.I)));
    }

}
