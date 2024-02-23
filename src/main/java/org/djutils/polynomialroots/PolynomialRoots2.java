package org.djutils.polynomialroots;

import org.djutils.complex.Complex;
import org.djutils.complex.ComplexMath;

/**
 * PolynomialRoots2 implements functions to find all roots of linear, quadratic, cubic and quartic polynomials, as well as
 * higher order polynomials without restrictions on the order. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class PolynomialRoots2
{
    /**
     * Do not instantiate.
     */
    private PolynomialRoots2()
    {
        // Do not instantiate
    }

    /**
     * Emulate the F77 sign function.
     * @param a double; the value to optionally sign invert
     * @param b double; the sign of which determines what to do
     * @return double; if b &gt;= 0 then a; else -a
     */
    private static double sign(final double a, final double b)
    {
        return b >= 0 ? a : -a;
    }

    /**
     * LINEAR POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates the root of the linear polynomial:<br>
     * q1 * x + q0<br>
     * @param q1 double; coefficient of the x term
     * @param q0 double; independent coefficient
     * @return Complex[]; the roots of the equation
     */
    public static Complex[] linearRoots(final double q1, final double q0)
    {
        if (q1 == 0)
        {
            return new Complex[] {}; // No roots; return empty array
        }
        return linearRoots(q0 / q1);
    }

    /**
     * LINEAR POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates the root of the linear polynomial:<br>
     * x + q0<br>
     * @param q0 double; independent coefficient
     * @return Complex[]; the roots of the equation
     */
    public static Complex[] linearRoots(final double q0)
    {
        return new Complex[] {new Complex(-q0, 0)};
    }

    /**
     * QUADRATIC POLYNOMIAL ROOT SOLVER
     * <p>
     * Calculates all real + complex roots of the quadratic polynomial:<br>
     * q2 * x^2 + q1 * x + q0<br>
     * The code checks internally if rescaling of the coefficients is needed to avoid overflow.
     * <p>
     * The order of the roots is as follows:<br>
     * 1) For real roots, the order is according to their algebraic value on the number scale (largest positive first, largest
     * negative last).<br>
     * 2) Since there can be only one complex conjugate pair root, no order is necessary.<br>
     * q1 : coefficient of x term q0 : independent coefficient
     * @param q2 double; coefficient of the quadratic term
     * @param q1 double; coefficient of the x term
     * @param q0 double; independent coefficient
     * @return Complex[]; the roots of the equation
     */
    public static Complex[] quadraticRoots(final double q2, final double q1, final double q0)
    {
        if (q2 == 0)
        {
            return linearRoots(q1, q0);
        }
        return quadraticRoots(q1 / q2, q0 / q2);
    }

    /**
     * QUADRATIC POLYNOMIAL ROOT SOLVER
     * <p>
     * Calculates all real + complex roots of the quadratic polynomial:<br>
     * x^2 + q1 * x + q0<br>
     * The code checks internally if rescaling of the coefficients is needed to avoid overflow.
     * <p>
     * The order of the roots is as follows:<br>
     * 1) For real roots, the order is according to their algebraic value on the number scale (largest positive first, largest
     * negative last).<br>
     * 2) Since there can be only one complex conjugate pair root, no order is necessary.<br>
     * q1 : coefficient of x term q0 : independent coefficient
     * @param q1 double; coefficient of the x term
     * @param q0 double; independent coefficient
     * @return Complex[]; the roots of the equation
     */
    public static Complex[] quadraticRoots(final double q1, final double q0)
    {
        boolean rescale;

        double a0, a1;
        double k = 0, x, y, z;

        // Handle special cases.
        if (q0 == 0.0 && q1 == 0.0)
        {
            // Two real roots at 0,0
            return new Complex[] {Complex.ZERO, Complex.ZERO};
        }
        else if (q0 == 0.0)
        {
            // Two real roots; one of these is 0,0
            // x^2 + q1 * x == x * (x + q1)
            Complex nonZeroRoot = new Complex(-q1);
            return new Complex[] {q1 > 0 ? Complex.ZERO : nonZeroRoot, q1 <= 0 ? nonZeroRoot : Complex.ZERO};
        }
        else if (q1 == 0.0)
        {
            x = Math.sqrt(Math.abs(q0));

            if (q0 < 0.0)
            {
                // Two real roots, symmetrically around 0
                return new Complex[] {new Complex(x, 0), new Complex(-x, 0)};
            }
            else
            {
                // Two complex roots, symmetrically around 0
                return new Complex[] {new Complex(0, x), new Complex(0, -x)};
            }
        }
        else
        {
            // The general case. Do rescaling, if either squaring of q1/2 or evaluation of
            // (q1/2)^2 - q0 will lead to overflow. This is better than to have the solver
            // crashed. Note, that rescaling might lead to loss of accuracy, so we only
            // invoke it when absolutely necessary.
            final double sqrtLPN = Math.sqrt(Double.MAX_VALUE); // Square root of the Largest Positive Number
            rescale = (q1 > sqrtLPN + sqrtLPN); // this detects overflow of (q1/2)^2

            if (!rescale)
            {
                x = q1 * 0.5; // we are sure here that x*x will not overflow
                rescale = (q0 < x * x - Double.MAX_VALUE); // this detects overflow of (q1/2)^2 - q0
            }

            if (rescale)
            {
                x = Math.abs(q1);
                y = Math.sqrt(Math.abs(q0));

                if (x > y)
                {
                    k = x;
                    z = 1.0 / x;
                    a1 = sign(1.0, q1);
                    a0 = (q0 * z) * z;
                }
                else
                {
                    k = y;
                    a1 = q1 / y;
                    a0 = sign(1.0, q0);
                }
            }
            else
            {
                a1 = q1;
                a0 = q0;
            }
            // Determine the roots of the quadratic. Note, that either a1 or a0 might
            // have become equal to zero due to underflow. But both cannot be zero.
            x = a1 * 0.5;
            y = x * x - a0;

            if (y >= 0.0)
            {
                // Two real roots
                y = Math.sqrt(y);

                if (x > 0.0)
                {
                    y = -x - y;
                }
                else
                {
                    y = -x + y;
                }

                if (rescale)
                {
                    y = y * k; // very important to convert to original
                    z = q0 / y; // root first, otherwise complete loss of
                }
                else // root due to possible a0 = 0 underflow
                {
                    z = a0 / y;
                }
                return new Complex[] {new Complex(Math.max(y, z), 0), new Complex(Math.min(y, z), 0)};
            }
            else
            {
                // Two complex roots (zero real roots)
                y = Math.sqrt(-y);

                if (rescale)
                {
                    x *= k;
                    y *= k;
                }
                return new Complex[] {new Complex(-x, y), new Complex(-x, -y)};
            }
        }
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * a * x^3 + b * x^2 + c * x + d<br>
     * The roots are found using the Newton-Raphson algorithm for the first (real) root, and then deflate the equation to a
     * quadratic equation to find the other two roots.
     * <p>
     * The order of the roots is as follows: 1) For real roots, the order is according to their algebraic value on the number
     * scale (largest positive first, largest negative last). 2) Since there can be only one complex conjugate pair root, no
     * order is necessary. 3) All real roots precede the complex ones.
     * @param a3 double; coefficient of the cubic term
     * @param a2 double; coefficient of the quadratic term
     * @param a1 double; coefficient of the linear term
     * @param a0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots; there can be one, two or three roots.
     */
    public static Complex[] cubicRootsNewtonFactor(final double a3, final double a2, final double a1, final double a0)
    {
        // corner case: a == 0 --> quadratic equation
        if (a3 == 0.0)
        {
            return quadraticRoots(a2, a1, a0);
        }

        // corner case: d == 0 --> solve x * (ax^2 + bx + c) = 0
        if (a0 == 0.0)
        {
            Complex[] result = quadraticRoots(a3, a2, a1);
            if (result.length == 0)
            {
                return new Complex[] {Complex.ZERO, Complex.ZERO, Complex.ZERO};
            }
            else if (result.length == 1)
            {
                return new Complex[] {Complex.ZERO, Complex.ZERO, result[0]};
            }
            else
            {
                return new Complex[] {Complex.ZERO, result[0], result[1]};
            }
        }

        double argmax = maxAbs(a3, a2, a1, a0);
        double d = a0 / argmax;
        double c = a1 / argmax;
        double b = a2 / argmax;
        double a = a3 / argmax;

        // find the first real root
        double[] args = new double[] {d, c, b, a};
        double root1 = rootNewtonRaphson(args, 0.0);

        // check and apply bisection if this did not work
        if (Double.isNaN(root1) || f(args, root1) > 1E-9)
        {
            double min = -64.0;
            double max = 64.0;
            int s1, s2;
            do
            {
                min *= 2.0;
                max *= 2.0;
                if (max > 1E64)
                {
                    throw new RuntimeException(
                            String.format("cannot find first root for %fx^3 + %fx^2 + %fx + %f = 0", a, b, c, d));
                }
                s1 = (int) Math.signum(f(args, min));
                s2 = (int) Math.signum(f(args, max));
            }
            while (s1 != 0 && s2 != 0 && s1 == s2);
            root1 = rootBisection(args, min, max, 0.0);
//            if (Double.isNaN(root1))
//            {
//                throw new RuntimeException(
//                        String.format("cannot find first root for %fx^3 + %fx^2 + %fx + %f = 0", a, b, c, d));
//            }
//            if (Math.abs(f(args, root1)) > 1E-6)
//            {
//                throw new RuntimeException(String.format("f(first root) != 0 for [%fx^3 + %fx^2 + %fx + %f = 0, but %f]", a, b,
//                        c, d, f(args, root1)));
//            }
        }

        /*-
         * Use Factor theory to factor out root 1 (r):
         * if the equation is ax^3 + bx^2 + cx + d, and there is a root r, the equation equals:
         * (x-r) (ax^2 + (b+ra)x + (c+r(b+ra)) + (ar^3+br^2+cr+d)/(x-r) where the last term becomes zero
         * We can then solve find the other two roots by solving ax^2 + (b+ra)x + (c+r(b+ra)) = 0
         */

        Complex[] rootsQuadaratic = quadraticRoots(a, b + root1 * a, c + root1 * (b + root1 * a));
//        if (Math.abs(f(args, rootsQuadaratic[0]).norm()) > 1E-6)
//        {
//            throw new RuntimeException(String.format("f(second root) != 0 for [%fx^3 + %fx^2 + %fx + %f = 0], but %f", a, b, c,
//                    d, f(args, root1)));
//        }
//        if (Math.abs(f(args, rootsQuadaratic[1]).norm()) > 1E-6)
//        {
//            throw new RuntimeException(String.format("f(second root) != 0 for [%fx^3 + %fx^2 + %fx + %f = 0], but %f", a, b, c,
//                    d, f(args, root1)));
//        }

        return new Complex[] {new Complex(root1), rootsQuadaratic[0], rootsQuadaratic[1]};
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * a * x^3 + b * x^2 + c * x + d<br>
     * The roots are found using Cardano's algorithm.
     * <p>
     * The order of the roots is as follows: 1) For real roots, the order is according to their algebraic value on the number
     * scale (largest positive first, largest negative last). 2) Since there can be only one complex conjugate pair root, no
     * order is necessary. 3) All real roots precede the complex ones.
     * @param a double; coefficient of the cubic term
     * @param b double; coefficient of the quadratic term
     * @param c double; coefficient of the linear term
     * @param d double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots; there can be one, two or three roots.
     */
    @SuppressWarnings("checkstyle:localvariablename")
    public static Complex[] cubicRootsCardano(final double a, final double b, final double c, final double d)
    {
        // corner case: a == 0 --> quadratic equation
        if (a == 0.0)
        {
            return quadraticRoots(b, c, d);
        }

        // corner case: d == 0 --> solve x * (ax^2 + bx + c) = 0
        if (d == 0.0)
        {
            Complex[] result = quadraticRoots(a, b, c);
            if (result.length == 0)
            {
                return new Complex[] {Complex.ZERO, Complex.ZERO, Complex.ZERO};
            }
            else if (result.length == 1)
            {
                return new Complex[] {Complex.ZERO, Complex.ZERO, result[0]};
            }
            else
            {
                return new Complex[] {Complex.ZERO, result[0], result[1]};
            }
        }

        // other cases: use Cardano's formula
        double D0 = b * b - 3.0 * a * c;
        double D1 = 2.0 * b * b * b - 9.0 * a * b * c + 27.0 * a * a * d;
        if (D0 == 0 && D1 == 0)
        {
            // one real solution
            Complex root = new Complex(rootNewtonRaphson(new double[] {d, c, b, a}, 0.0));
            return new Complex[] {root, root, root};
        }
        Complex r = ComplexMath.sqrt(new Complex(D1 * D1 - 4.0 * D0 * D0 * D0));
        Complex s = r.plus(D1).times(0.5);
        if (s.re == 0.0 && s.im == 0.0)
        {
            s = r.times(-1.0).plus(D1).times(0.5);
        }
        Complex C = ComplexMath.cbrt(s);
        Complex x1 = C.plus(b).plus((C.reciprocal().times(D0))).times(-1.0 / (3.0 * a));
        Complex x2 = C.rotate(Math.toRadians(120.0)).plus(b).plus((C.rotate(Math.toRadians(120.0)).reciprocal().times(D0)))
                .times(-1.0 / (3.0 * a));
        Complex x3 = C.rotate(Math.toRadians(-120.0)).plus(b).plus((C.rotate(Math.toRadians(-120.0)).reciprocal().times(D0)))
                .times(-1.0 / (3.0 * a));
        return new Complex[] {x1, x2, x3};
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * a3 * x^3 + a2 * x^2 + a1 * x + a0<br>
     * The roots are found using Durand-Kerner's algorithm.
     * @param a3 double; coefficient of the cubic term
     * @param a2 double; coefficient of the quadratic term
     * @param a1 double; coefficient of the linear term
     * @param a0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots; there can be one, two or three roots.
     */
    public static Complex[] cubicRootsDurandKerner(final double a3, final double a2, final double a1, final double a0)
    {
        // corner case: a3 == 0 --> quadratic equation
        if (a3 == 0.0)
        {
            return quadraticRoots(a2, a1, a0);
        }

        // other cases: use Durand-Kerner's algorithm
        return rootsDurandKerner(new Complex[] {new Complex(a0 / a3), new Complex(a1 / a3), new Complex(a2 / a3), Complex.ONE});
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * a3 * x^3 + a2 * x^2 + a1 * x + a0<br>
     * The roots are found using Aberth-Ehrlich's algorithm.
     * @param a3 double; coefficient of the cubic term
     * @param a2 double; coefficient of the quadratic term
     * @param a1 double; coefficient of the linear term
     * @param a0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots; there can be one, two or three roots.
     */
    public static Complex[] cubicRootsAberthEhrlich(final double a3, final double a2, final double a1, final double a0)
    {
        // corner case: a3 == 0 --> quadratic equation
        if (a3 == 0.0)
        {
            return quadraticRoots(a2, a1, a0);
        }

        // other cases: use Durand-Kerner's algorithm
        return rootsAberthEhrlich(
                new Complex[] {new Complex(a0 / a3), new Complex(a1 / a3), new Complex(a2 / a3), Complex.ONE});
    }

    /**
     * QUADRATIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * a4 * x^4 + a3 * x^3 + a2 * x^2 + a1 * x + a0<br>
     * The roots are found using Durand-Kerner's algorithm.
     * @param a4 double; coefficient of the quartic term
     * @param a3 double; coefficient of the cubic term
     * @param a2 double; coefficient of the quadratic term
     * @param a1 double; coefficient of the linear term
     * @param a0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots; always 4 roots are returned; there can be double roots
     */
    public static Complex[] quarticRootsDurandKerner(final double a4, final double a3, final double a2, final double a1,
            final double a0)
    {
        // corner case: a4 == 0 --> cubic equation
        if (a4 == 0.0)
        {
            return cubicRootsDurandKerner(a3, a2, a1, a0);
        }

        return rootsDurandKerner(new Complex[] {new Complex(a0 / a4), new Complex(a1 / a4), new Complex(a2 / a4),
                new Complex(a3 / a4), Complex.ONE});
    }

    /**
     * QUADRATIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * a4 * x^4 + a3 * x^3 + a2 * x^2 + a1 * x + a0<br>
     * The roots are found using Aberth-Ehrlich's algorithm.
     * @param a4 double; coefficient of the quartic term
     * @param a3 double; coefficient of the cubic term
     * @param a2 double; coefficient of the quadratic term
     * @param a1 double; coefficient of the linear term
     * @param a0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots; always 4 roots are returned; there can be double roots
     */
    public static Complex[] quarticRootsAberthEhrlich(final double a4, final double a3, final double a2, final double a1,
            final double a0)
    {
        // corner case: a4 == 0 --> cubic equation
        if (a4 == 0.0)
        {
            return cubicRootsAberthEhrlich(a3, a2, a1, a0);
        }

        return rootsAberthEhrlich(new Complex[] {new Complex(a0 / a4), new Complex(a1 / a4), new Complex(a2 / a4),
                new Complex(a3 / a4), Complex.ONE});
    }

    /** the number of steps in solving the equation with the Durand-Kerner method. */
    private static final int MAX_STEPS_DURAND_KERNER = 100;

    /**
     * Polynomial root finder using the Durand-Kerner method, with complex coefficients for the polynomial equation. Own
     * implementation. See <a href="https://en.wikipedia.org/wiki/Durand%E2%80%93Kerner_method">
     * https://en.wikipedia.org/wiki/Durand%E2%80%93Kerner_method</a> for brief information.
     * @param a Complex[]; the complex factors of the polynomial, where the index i indicate the factor for x^i, so the
     *            polynomial is<br>
     *            a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0]
     * @return Complex[] all roots of the equation, where real roots are coded with Im = 0
     */
    public static Complex[] rootsDurandKerner(final Complex[] a)
    {
        int n = a.length - 1;
        double radius = 1 + maxAbs(a);

        // initialize the initial values, not as a real number and not as a root of unity
        Complex[] p = new Complex[n];
        p[0] = new Complex(Math.sqrt(radius), Math.cbrt(radius));
        double rot = 350.123 / n;
        for (int i = 1; i < n; i++)
        {
            p[i] = p[0].rotate(rot * i);
        }

        double maxError = 1.0;
        int count = 0;
        while (maxError > 0 && count < MAX_STEPS_DURAND_KERNER)
        {
            maxError = 0.0;
            count++;
            for (int i = 0; i < n; i++)
            {
                Complex factors = Complex.ONE;
                for (int j = 0; j < n; j++)
                {
                    if (i != j)
                    {
                        factors = factors.times(p[i].minus(p[j]));
                    }
                }
                Complex newValue = p[i].minus(f(a, p[i]).divideBy(factors));
                if (!(newValue.equals(p[i])))
                {
                    double error = newValue.minus(p[i]).norm();
                    if (error > maxError)
                    {
                        maxError = error;
                    }
                    p[i] = newValue;
                }
            }
        }

        // correct for 1 ulp above or below zero; make that value zero instead
        for (int i = 0; i < n; i++)
        {
            if (Math.abs(p[i].im) == Double.MIN_VALUE)
            {
                p[i] = new Complex(p[i].re, 0.0);
            }
            if (Math.abs(p[i].re) == Double.MIN_VALUE)
            {
                p[i] = new Complex(0.0, p[i].im);
            }
        }

        return p;
    }

    /** the number of steps in solving the equation with the Durand-Kerner method. */
    private static final int MAX_STEPS_ABERTH_EHRLICH = 100;

    /**
     * Polynomial root finder using the Aberth-Ehrlich method or Aberth method, with complex coefficients for the polynomial
     * equation. Own implementation. See <a href="https://en.wikipedia.org/wiki/Aberth_method">
     * https://en.wikipedia.org/wiki/Aberth_method</a> for brief information.
     * @param a Complex[]; the complex factors of the polynomial, where the index i indicate the factor for x^i, so the
     *            polynomial is<br>
     *            a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0]
     * @return Complex[] all roots of the equation, where real roots are coded with Im = 0
     */
    public static Complex[] rootsAberthEhrlich(final Complex[] a)
    {
        int n = a.length - 1;
        double radius = 1 + maxAbs(a);

        // initialize the initial values, not as a real number and not as a root of unity
        Complex[] p = new Complex[n];
        p[0] = new Complex(Math.sqrt(radius), Math.cbrt(radius));
        double rot = 350.123 / n;
        for (int i = 1; i < n; i++)
        {
            p[i] = p[0].rotate(rot * i);
        }

        double maxError = 1.0;
        int count = 0;
        while (maxError > 0 && count < MAX_STEPS_ABERTH_EHRLICH)
        {
            maxError = 0.0;
            count++;
            for (int i = 0; i < n; i++)
            {
                Complex sum = Complex.ZERO;
                for (int j = 0; j < n; j++)
                {
                    if (i != j)
                    {
                        sum = sum.plus(Complex.ONE.divideBy(p[i].minus(p[j])));
                    }
                }
                Complex pDivPDer = f(a, p[i]).divideBy(fDerivative(a, p[i]));
                Complex w = pDivPDer.divideBy(Complex.ONE.minus(pDivPDer.times(sum)));
                double error = w.norm();
                if (error > maxError)
                {
                    maxError = error;
                }
                p[i] = p[i].minus(w);
            }
        }

        // correct for 1 ulp above or below zero; make that value zero instead
        for (int i = 0; i < n; i++)
        {
            if (Math.abs(p[i].im) == Double.MIN_VALUE)
            {
                p[i] = new Complex(p[i].re, 0.0);
            }
            if (Math.abs(p[i].re) == Double.MIN_VALUE)
            {
                p[i] = new Complex(0.0, p[i].im);
            }
        }

        return p;
    }

    /** the number of steps in approximating a real root with the Newton-Raphson method. */
    private static final int MAX_STEPS_NEWTON = 100;

    /**
     * Polynomial root finder using the Newton-Raphson method. See <a href="https://en.wikipedia.org/wiki/Newton%27s_method">
     * https://en.wikipedia.org/wiki/Newton%27s_method</a> for brief information about the Newton-Raphson or Newton method for
     * root finding.
     * @param a double[]; the factors of the polynomial, where the index i indicate the factor for x^i, so the polynomial is<br>
     *            a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0]
     * @param allowedError double; the allowed absolute error in the result
     * @return double the root of the equation that has been found on the basis of the start value, or NaN if not found within
     *         the allowed error bounds and the allowed number of steps.
     */
    public static double rootNewtonRaphson(final double[] a, final double allowedError)
    {
        double x = 0.1232232323234; // take a a start point that has an almost zero chance of having f'(x) = 0.
        double newValue = Double.NaN; // for testing convergence
        double fx = -1;
        for (int j = 0; j < MAX_STEPS_NEWTON; j++)
        {
            fx = f(a, x);
            newValue = x - fx / fDerivative(a, x);
            if (x == newValue || Math.abs(fx) <= allowedError)
            {
                return x;
            }
            x = newValue;
        }
        return Math.abs(fx) <= allowedError ? x : Double.NaN;
    }

    /** the number of steps in approximating a real root with the bisection method. */
    private static final int MAX_STEPS_BISECTION = 100;

    /**
     * Polynomial root finder using the bisection method combined with bisection to avoid cycles and the algorithm going out of
     * bounds. Implementation based on Numerical Recipes in C, section 9.4, pp 365-366. See
     * <a href="https://en.wikipedia.org/wiki/Newton%27s_method"> https://en.wikipedia.org/wiki/Newton%27s_method</a> for brief
     * information about the Newton-Raphson or Newton method for root finding.
     * @param a double[]; the factors of the polynomial, where the index i indicate the factor for x^i, so the polynomial is<br>
     *            a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0]
     * @param startMin double; the lowest initial search value
     * @param startMax double; the highest initial search value
     * @param allowedError double; the allowed absolute error in the result
     * @return double the root of the equation that has been found on the basis of the start values, or NaN if not found within
     *         the allowed error bounds and the allowed number of steps.
     */
    public static double rootBisection(final double[] a, final double startMin, final double startMax,
            final double allowedError)
    {
        int n = 0;
        double xPrev = startMin;
        double min = startMin;
        double max = startMax;
        double fmin = f(a, min);
        while (n <= MAX_STEPS_BISECTION)
        {
            double x = (min + max) / 2.0;
            double fx = f(a, x);
            if (x == xPrev || Math.abs(fx) < allowedError)
            {
                return x;
            }
            if (Math.signum(fx) == Math.signum(fmin))
            {
                min = x;
                fmin = fx;
            }
            else
            {
                max = x;
            }
            xPrev = x;
            n++;
        }
        return Double.NaN;
    }

    /**
     * Return the max of the norm of the complex coefficients in an array.
     * @param array Complex[] the array with complex numbers
     * @return double; the highest value of the norm of the complex numbers in the array
     */
    private static double maxAbs(final Complex[] array)
    {
        double m = Double.NEGATIVE_INFINITY;
        for (Complex c : array)
        {
            if (c.norm() > m)
            {
                m = c.norm();
            }
        }
        return m;
    }

    /**
     * Return the max of the absolute values of the coefficients in an array.
     * @param values double[] the array with numbers
     * @return double; the highest absolute value of the norm of the complex numbers in the array
     */
    private static double maxAbs(final double... values)
    {
        double m = Double.NEGATIVE_INFINITY;
        for (double d : values)
        {
            if (Math.abs(d) > m)
            {
                m = Math.abs(d);
            }
        }
        return m;
    }

    /**
     * Return the complex value of f(c) where f(x) = a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0].
     * @param a Complex[] the complex factors of the equation
     * @param c Complex; the value for which to calculate f(c)
     * @return Complex; f(c)
     */
    private static Complex f(final Complex[] a, final Complex c)
    {
        Complex cPow = Complex.ONE;
        Complex result = Complex.ZERO;
        for (int i = 0; i < a.length; i++)
        {
            result = result.plus(cPow.times(a[i]));
            cPow = cPow.times(c);
        }
        return result;
    }

    /**
     * Return the real value of f(x) where f(x) = a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0].
     * @param a double[] the factors of the equation
     * @param x double; the value for which to calculate f(x)
     * @return double; f(x)
     */
    private static double f(final double[] a, final double x)
    {
        double xPow = 1.0;
        double result = 0.0;
        for (int i = 0; i < a.length; i++)
        {
            result += xPow * a[i];
            xPow = xPow * x;
        }
        return result;
    }

    /**
     * Return the complex value of f(c) where f(x) = a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0].
     * @param a double[] the complex factors of the equation
     * @param c Complex; the value for which to calculate f(c)
     * @return Complex; f(c)
     */
    private static Complex f(final double[] a, final Complex c)
    {
        Complex cPow = Complex.ONE;
        Complex result = Complex.ZERO;
        for (int i = 0; i < a.length; i++)
        {
            result = result.plus(cPow.times(a[i]));
            cPow = cPow.times(c);
        }
        return result;
    }

    /**
     * Return the real value of f'(x) where f(x) = a[n]x^n + a[n-1]x^(n-1) + ... + a[2]a^2 + a[1]x + a[0].<br>
     * The derivative function f'(x) = n.a[n]x^(n-1) + (n-1).a[n-1]x^(n-2) + ... + 2.a[2]x^1 + 1.a[1]
     * @param a double[] the factors of the equation
     * @param x double; the value for which to calculate f'(x)
     * @return double; f'(x), the value of the derivative function at point x
     */
    private static double fDerivative(final double[] a, final double x)
    {
        double xPow = 1.0;
        double result = 0.0;
        for (int i = 1; i < a.length; i++)
        {
            result += xPow * i * a[i];
            xPow = xPow * x;
        }
        return result;
    }

    /**
     * Return the complex value of f'(c) where f(c) = a[n]c^n + a[n-1]c^(n-1) + ... + a[2]a^2 + a[1]c + a[0].<br>
     * The derivative function f'(c) = n.a[n]c^(n-1) + (n-1).a[n-1]c^(n-2) + ... + 2.a[2]c^1 + 1.a[1]
     * @param a double[] the factors of the equation
     * @param c double; the value for which to calculate f'(c)
     * @return Complex; f'(c), the value of the derivative function at point c
     */
    private static Complex fDerivative(final Complex[] a, final Complex c)
    {
        Complex cPow = Complex.ONE;
        Complex result = Complex.ZERO;
        for (int i = 1; i < a.length; i++)
        {
            result = result.plus(cPow.times(a[i]).times(i));
            cPow = cPow.times(c);
        }
        return result;
    }

    /**
     * @param args String[] not used
     */
    public static void main(final String[] args)
    {
        double a = 0.001;
        double b = 1000;
        double c = 0;
        double d = 1;
        Complex[] roots = cubicRootsNewtonFactor(a, b, c, d);
        for (Complex root : roots)
        {
            System.out.println("NewtonFactor    " + root + "   f(x) = " + f(new double[] {d, c, b, a}, root));
        }
        System.out.println();
        roots = cubicRootsCardano(a, b, c, d);
        for (Complex root : roots)
        {
            System.out.println("Cardano         " + root + "   f(x) = " + f(new double[] {d, c, b, a}, root));
        }
        System.out.println();
        roots = cubicRootsAberthEhrlich(a, b, c, d);
        for (Complex root : roots)
        {
            System.out.println("Aberth-Ehrlich  " + root + "   f(x) = " + f(new double[] {d, c, b, a}, root));
        }
        System.out.println();
        roots = cubicRootsDurandKerner(a, b, c, d);
        for (Complex root : roots)
        {
            System.out.println("Durand-Kerner   " + root + "   f(x) = " + f(new double[] {d, c, b, a}, root));
        }
        System.out.println();
        roots = PolynomialRoots.cubicRoots(a, b, c, d);
        for (Complex root : roots)
        {
            System.out.println("F77             " + root + "   f(x) = " + f(new double[] {d, c, b, a}, root));
        }
    }
}
