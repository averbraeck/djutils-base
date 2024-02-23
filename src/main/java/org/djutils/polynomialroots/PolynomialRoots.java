package org.djutils.polynomialroots;

import org.djutils.complex.Complex;

/**
 * PolynomialRoots.java. Polynomial234RootSolvers - final all roots of linear, quadratic, cubic and quartic polynomials. Derived
 * from <a href="https://netlib.org/toms/954.zip">https://dl.acm.org/doi/10.1145/2699468</a>. Manual translation from Fortran90
 * to java by Peter Knoppers.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class PolynomialRoots
{
    /**
     * Do not instantiate.
     */
    private PolynomialRoots()
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
     * Unlike the quadratic, cubic and quartic code, this is NOT derived from that Fortran90 code; it was added for completenes.
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
     * Unlike the quadratic, cubic and quartic code, this is NOT derived from that Fortran90 code; it was added for completenes.
     * @param q0 double; independent coefficient
     * @return Complex[]; the roots of the equation
     */
    public static Complex[] linearRoots(final double q0)
    {
        return new Complex[] { new Complex(-q0, 0) };
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
            return new Complex[] { Complex.ZERO, Complex.ZERO };
        }
        else if (q0 == 0.0)
        {
            // Two real roots; one of these is 0,0
            // x^2 + q1 * x == x * (x + q1)
            Complex nonZeroRoot = new Complex(-q1);
            return new Complex[] { q1 > 0 ? Complex.ZERO : nonZeroRoot, q1 <= 0 ? nonZeroRoot : Complex.ZERO };
        }
        else if (q1 == 0.0)
        {
            x = Math.sqrt(Math.abs(q0));

            if (q0 < 0.0)
            {
                // Two real roots, symmetrically around 0
                return new Complex[] { new Complex(x, 0), new Complex(-x, 0) };
            }
            else
            {
                // Two complex roots, symmetrically around 0
                return new Complex[] { new Complex(0, x), new Complex(0, -x) };
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
                return new Complex[] { new Complex(Math.max(y, z), 0), new Complex(Math.min(y, z), 0) };
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
                return new Complex[] { new Complex(-x, y), new Complex(-x, -y) };
            }
        }
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * c3 * x^3 + c2 * x^2 + c1 * x + c0<br>
     * The first real root (which always exists) is obtained using an optimized Newton-Raphson scheme. The other remaining roots
     * are obtained through composite deflation into a quadratic.
     * <P>
     * The cubic root solver can handle any size of cubic coefficients and there is no danger of overflow due to proper
     * rescaling of the cubic polynomial. The order of the roots is as follows: 1) For real roots, the order is according to
     * their algebraic value on the number scale (largest positive first, largest negative last). 2) Since there can be only one
     * complex conjugate pair root, no order is necessary. 3) All real roots precede the complex ones.
     * @param c3 double; coefficient of the cubic term
     * @param c2 double; coefficient of the quadratic term
     * @param c1 double; coefficient of the linear term
     * @param c0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots
     */
    public static Complex[] cubicRoots(final double c3, final double c2, final double c1, final double c0)
    {
        if (c3 == 0)
        {
            return quadraticRoots(c2, c1, c0);
        }
        return cubicRoots(c2 / c3, c1 / c3, c0 / c3, false);
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * x^3 + c2 * x^2 + c1 * x + c0<br>
     * The first real root (which always exists) is obtained using an optimized Newton-Raphson scheme. The other remaining roots
     * are obtained through composite deflation into a quadratic.
     * <P>
     * The cubic root solver can handle any size of cubic coefficients and there is no danger of overflow due to proper
     * rescaling of the cubic polynomial. The order of the roots is as follows: 1) For real roots, the order is according to
     * their algebraic value on the number scale (largest positive first, largest negative last). 2) Since there can be only one
     * complex conjugate pair root, no order is necessary. 3) All real roots precede the complex ones.
     * @param c2 double; coefficient of the quadratic term
     * @param c1 double; coefficient of the linear term
     * @param c0 double; coefficient of the independent term
     * @return Complex[]; array of Complex with all the roots
     */
    public static Complex[] cubicRoots(final double c2, final double c1, final double c0)
    {
        return cubicRoots(c2, c1, c0, false);
    }

    /**
     * CUBIC POLYNOMIAL ROOT SOLVER.
     * <p>
     * Calculates all (real and complex) roots of the cubic polynomial:<br>
     * x^3 + c2 * x^2 + c1 * x + c0<br>
     * The first real root (which always exists) is obtained using an optimized Newton-Raphson scheme. The other remaining roots
     * are obtained through composite deflation into a quadratic. An option for printing detailed info about the intermediate
     * stages in solving the cubic is available.
     * <P>
     * The cubic root solver can handle any size of cubic coefficients and there is no danger of overflow due to proper
     * rescaling of the cubic polynomial. The order of the roots is as follows: 1) For real roots, the order is according to
     * their algebraic value on the number scale (largest positive first, largest negative last). 2) Since there can be only one
     * complex conjugate pair root, no order is necessary. 3) All real roots precede the complex ones.
     * @param c2 double; coefficient of the quadratic term
     * @param c1 double; coefficient of the linear term
     * @param c0 double; coefficient of the independent term
     * @param verbose boolean; if true; produce debugging output; if false; do not produce debugging output
     * @return Complex[]; array of Complex with all the roots
     */
    public static Complex[] cubicRoots(final double c2, final double c1, final double c0, final boolean verbose)
    {
        final int cubicType;
        int deflateCase;

        final double macheps = Math.ulp(1.0);
        final double one27th = 1.0 / 27.0;
        final double two27th = 2.0 / 27.0;
        final double third = 1.0 / 3.0;

        // Newton-Raphson coeffs for class 5 and 6
        final double p51 = 8.78558e-1;
        final double p52 = 1.92823e-1;
        final double p53 = 1.19748;
        final double p54 = 3.45219e-1;
        final double q51 = 5.71888e-1;
        final double q52 = 5.66324e-1;
        final double q53 = 2.83772e-1;
        final double q54 = 4.01231e-1;
        final double r51 = 7.11154e-1;
        final double r52 = 5.05734e-1;
        final double r53 = 8.37476e-1;
        final double r54 = 2.07216e-1;
        final double s51 = 3.22313e-1;
        final double s52 = 2.64881e-1;
        final double s53 = 3.56228e-1;
        final double s54 = 4.45532e-3;

        final int allzero = 0;
        final int linear = 1;
        final int quadratic = 2;
        final int general = 3;

        double a0 = 0, a1 = 0, a2 = 0;
        double a = 0, b = 0, c = 0, k = 0, s = 0, t, u = 0, x = 0, y, z;
        double xShift = 0;

        if (verbose)
        {
            System.out.println("initial cubic c2      = " + c2);
            System.out.println("initial cubic c1      = " + c1);
            System.out.println("initial cubic c0      = " + c0);
            System.out.println("------------------------------------------------");
        }
        // Handle special cases.
        //
        // 1) all terms zero
        // 2) only quadratic term is nonzero -> linear equation.
        // 3) only independent term is zero -> quadratic equation.
        if (c0 == 0.0 && c1 == 0.0 && c2 == 0.0)
        {
            cubicType = allzero;
        }
        else if (c0 == 0.0 && c1 == 0.0)
        {
            k = 1.0;
            a2 = c2;
            cubicType = linear;
        }
        else if (c0 == 0.0)
        {
            k = 1.0;
            a2 = c2;
            a1 = c1;
            cubicType = quadratic;
        }
        else
        {
            // The general case. Rescale cubic polynomial, such that largest absolute coefficient
            // is (exactly!) equal to 1. Honor the presence of a special cubic case that might have
            // been obtained during the rescaling process (due to underflow in the coefficients).
            x = Math.abs(c2);
            y = Math.sqrt(Math.abs(c1));
            z = Math.cbrt(Math.abs(c0));
            u = Math.max(Math.max(x, y), z);

            if (u == x)
            {
                k = 1.0 / x;
                a2 = sign(1.0, c2);
                a1 = (c1 * k) * k;
                a0 = ((c0 * k) * k) * k;
            }
            else if (u == y)
            {
                k = 1.0 / y;
                a2 = c2 * k;
                a1 = sign(1.0, c1);
                a0 = ((c0 * k) * k) * k;
            }
            else
            {
                k = 1.0 / z;
                a2 = c2 * k;
                a1 = (c1 * k) * k;
                a0 = sign(1.0, c0);
            }

            if (verbose)
            {
                System.out.println("rescaling factor      = " + k);
                System.out.println("------------------------------------------------");
                System.out.println("rescaled cubic c2     = " + a2);
                System.out.println("rescaled cubic c1     = " + a1);
                System.out.println("rescaled cubic c0     = " + a0);
                System.out.println("------------------------------------------------");
            }

            k = 1.0 / k;

            if (a0 == 0.0 && a1 == 0.0 && a2 == 0.0)
            {
                cubicType = allzero;
            }
            else if (a0 == 0.0 && a1 == 0.0)
            {
                cubicType = linear;
            }
            else if (a0 == 0.0)
            {
                cubicType = quadratic;
            }
            else
            {
                cubicType = general;
            }
        }

        switch (cubicType)
        {
            case allzero: // 1) Only zero roots
                return new Complex[] { Complex.ZERO, Complex.ZERO, Complex.ZERO };

            case linear: // 2) The linear equation case -> additional 2 zeros.
            {
                double root = -a2 * k;
                return new Complex[] { new Complex(Math.max(0.0, root), 0), Complex.ZERO, new Complex(Math.min(0.0, root), 0) };
            }

            case quadratic: // 3) The quadratic equation case -> additional 1 zero.
            {
                Complex[] otherRoots = quadraticRoots(a2, a1);

                if (otherRoots[0].isReal()) // There are guaranteed to be two roots of the quadratic sub case
                {
                    // Three real roots
                    double xx = otherRoots[0].re * k;
                    double yy = otherRoots[1].re * k;
                    return new Complex[] { new Complex(Math.max(xx, 0.0), 0), new Complex(Math.max(yy, Math.min(xx, 0.0)), 0),
                            new Complex(Math.min(yy, 0.0), 0) };
                }
                else
                {
                    // One real root and two complex roots
                    return new Complex[] { Complex.ZERO, otherRoots[0].times(k), otherRoots[1].times(k) };
                }
            }
            case general:
            {
                // 3) The general cubic case. Set the best Newton-Raphson root estimates for the cubic.
                // The easiest and most robust conditions are checked first. The most complicated
                // ones are last and only done when absolutely necessary.
                // Newton-Raphson coefficients for class 1 and 2
                final double p1 = 1.09574;
                final double q1 = 3.23900e-1;
                final double r1 = 3.23900e-1;
                final double s1 = 9.57439e-2;

                if (a0 == 1.0)
                {
                    x = -p1 + q1 * a1 - a2 * (r1 - s1 * a1);

                    a = a2;
                    b = a1;
                    c = a0;
                    xShift = 0.0;
                }
                else if (a0 == -1.0)
                {
                    x = p1 - q1 * a1 - a2 * (r1 - s1 * a1);

                    a = a2;
                    b = a1;
                    c = a0;
                    xShift = 0.0;
                }
                else if (a1 == 1.0)
                {
                    // Newton-Raphson coeffs for class 4
                    final double q4 = 7.71845e-1;
                    final double s4 = 2.28155e-1;

                    if (a0 > 0.0)
                    {
                        x = a0 * (-q4 - s4 * a2);
                    }
                    else
                    {
                        x = a0 * (-q4 + s4 * a2);
                    }

                    a = a2;
                    b = a1;
                    c = a0;
                    xShift = 0.0;
                }
                else if (a1 == -1.0)
                {
                    // Newton-Raphson coeffs for class 3
                    final double p3 = 1.14413;
                    final double q3 = 2.75509e-1;
                    final double r3 = 4.45578e-1;
                    final double s3 = 2.59342e-2;

                    y = -two27th;
                    y = y * a2;
                    y = y * a2 - third;
                    y = y * a2;

                    if (a0 < y)
                    {
                        x = +p3 - q3 * a0 - a2 * (r3 + s3 * a0); // + guess
                    }
                    else
                    {
                        x = -p3 - q3 * a0 - a2 * (r3 - s3 * a0); // - guess
                    }

                    a = a2;
                    b = a1;
                    c = a0;
                    xShift = 0.0;
                }
                else if (a2 == 1.0)
                {
                    b = a1 - third;
                    c = a0 - one27th;

                    if (Math.abs(b) < macheps && Math.abs(c) < macheps) // triple -1/3 root
                    {
                        x = -third * k;
                        Complex root = new Complex(x, 0);
                        return new Complex[] { root, root, root };
                    }
                    else
                    {
                        y = third * a1 - two27th;

                        if (a1 <= third)
                        {
                            if (a0 > y)
                            {
                                x = -p51 - q51 * a0 + a1 * (r51 - s51 * a0); // - guess
                            }
                            else
                            {
                                x = +p52 - q52 * a0 - a1 * (r52 + s52 * a0); // + guess
                            }
                        }
                        else if (a0 > y)
                        {
                            x = -p53 - q53 * a0 + a1 * (r53 - s53 * a0); // <-1/3 guess
                        }
                        else
                        {
                            x = +p54 - q54 * a0 - a1 * (r54 + s54 * a0); // >-1/3 guess
                        }
                    }
                    if (Math.abs(b) < 1.e-2 && Math.abs(c) < 1.e-2) // use shifted root
                    {
                        c = -third * b + c;
                        if (Math.abs(c) < macheps)
                        {
                            c = 0.0; // prevent random noise
                        }
                        a = 0.0;
                        xShift = third;
                        x = x + xShift;
                    }
                    else
                    {
                        a = a2;
                        b = a1;
                        c = a0;
                        xShift = 0.0;
                    }
                }
                else if (a2 == -1.0)
                {
                    b = a1 - third;
                    c = a0 + one27th;

                    if (Math.abs(b) < macheps && Math.abs(c) < macheps) // triple 1/3 root
                    {
                        x = third * k;
                        Complex root = new Complex(x, 0);
                        return new Complex[] { root, root, root };
                    }
                    else
                    {
                        y = two27th - third * a1;

                        if (a1 <= third)
                        {
                            if (a0 < y)
                            {
                                x = +p51 - q51 * a0 - a1 * (r51 + s51 * a0); // +1 guess
                            }
                            else
                            {
                                x = -p52 - q52 * a0 + a1 * (r52 - s52 * a0); // -1 guess
                            }
                        }
                        else if (a0 < y)
                        {
                            x = +p53 - q53 * a0 - a1 * (r53 + s53 * a0); // >1/3 guess
                        }
                        else
                        {
                            x = -p54 - q54 * a0 + a1 * (r54 - s54 * a0); // <1/3 guess
                        }
                    }

                    if (Math.abs(b) < 1.e-2 && Math.abs(c) < 1.e-2) // use shifted root
                    {
                        c = third * b + c;
                        if (Math.abs(c) < macheps)
                        {
                            c = 0.0; // prevent random noise
                        }
                        a = 0.0;
                        xShift = -third;
                        x = x + xShift;
                    }
                    else
                    {
                        a = a2;
                        b = a1;
                        c = a0;
                        xShift = 0.0;
                    }
                }

                // Perform Newton/Bisection iterations on x^3 + ax^2 + bx + c.
                z = x + a;
                y = x + z;
                z = z * x + b;
                y = y * x + z; // C'(x)
                z = z * x + c; // C(x)
                t = z; // save C(x) for sign comparison
                x = x - z / y; // 1st improved root

                int oscillate = 0;
                boolean bisection = false;
                boolean converged = false;

                while ((!converged) && (!bisection)) // Newton-Raphson iterates
                {
                    z = x + a;
                    y = x + z;
                    z = z * x + b;
                    y = y * x + z;
                    z = z * x + c;

                    if (z * t < 0.0) // does Newton start oscillating ?
                    {
                        if (z < 0.0)
                        {
                            oscillate = oscillate + 1; // increment oscillation counter
                            s = x; // save lower bisection bound
                        }
                        else
                        {
                            u = x; // save upper bisection bound
                        }
                        t = z; // save current C(x)
                    }

                    y = z / y; // Newton correction
                    x = x - y; // new Newton root

                    bisection = oscillate > 2; // activate bisection
                    converged = Math.abs(y) <= Math.abs(x) * macheps; // Newton convergence indicator

                    if (verbose)
                    {
                        System.out.println("Newton root           = " + x);
                    }
                }

                if (bisection)
                {
                    t = u - s; // initial bisection interval
                    while (Math.abs(t) > Math.abs(x) * macheps) // bisection iterates
                    {
                        z = x + a;
                        z = z * x + b; // C (x)
                        z = z * x + c;

                        if (z < 0.0)
                        {
                            s = x;
                        }
                        else
                        {
                            u = x; // keep bracket on root
                        }

                        t = 0.5 * (u - s); // new bisection interval
                        x = s + t; // new bisection root

                        if (verbose)
                        {
                            System.out.println("Bisection root        = " + x);
                        }
                    }
                }

                if (verbose)
                {
                    System.out.println("------------------------------------------------");
                }

                x = x - xShift; // unshift root
                // Forward / backward deflate rescaled cubic (if needed) to check for other real roots.
                // The deflation analysis is performed on the rescaled cubic. The actual deflation must
                // be performed on the original cubic, not the rescaled one. Otherwise deflation errors
                // will be enhanced when undoing the rescaling on the extra roots.
                z = Math.abs(x);
                s = Math.abs(a2);
                t = Math.abs(a1);
                u = Math.abs(a0);

                y = z * Math.max(s, z); // take maximum between |x^2|,|a2 * x|

                deflateCase = 1; // up to now, the maximum is |x^3| or |a2 * x^2|

                if (y < t) // check maximum between |x^2|,|a2 * x|,|a1|
                {
                    y = t * z; // the maximum is |a1 * x|
                    deflateCase = 2; // up to now, the maximum is |a1 * x|
                }
                else
                {
                    y = y * z; // the maximum is |x^3| or |a2 * x^2|
                }

                if (y < u) // check maximum between |x^3|,|a2 * x^2|,|a1 * x|,|a0|
                {
                    deflateCase = 3; // the maximum is |a0|
                }

                y = x * k; // real root of original cubic

                switch (deflateCase)
                {
                    case 1:
                        x = 1.0 / y;
                        t = -c0 * x; // t -> backward deflation on unscaled cubic
                        s = (t - c1) * x; // s -> backward deflation on unscaled cubic
                        break;
                    case 2:
                        s = c2 + y; // s -> forward deflation on unscaled cubic
                        t = -c0 / y; // t -> backward deflation on unscaled cubic
                        break;
                    case 3:
                        s = c2 + y; // s -> forward deflation on unscaled cubic
                        t = c1 + s * y; // t -> forward deflation on unscaled cubic
                        break;

                    default:
                        throw new RuntimeException("Bad switch; cannot happen");
                }

                if (verbose)
                {
                    System.out.println("Residual quadratic q1 = " + s);
                    System.out.println("Residual quadratic q0 = " + t);
                    System.out.println("------------------------------------------------");
                }

                Complex[] quadraticRoots = quadraticRoots(s, t);
                // call quadraticRoots (s, t, nReal, root (1:2,1:2))

                if (quadraticRoots[0].isReal())
                {
                    // Three real roots
                    return new Complex[] { new Complex(Math.max(quadraticRoots[0].re, y), 0),
                            new Complex(Math.max(quadraticRoots[1].re, Math.min(quadraticRoots[0].re, y)), 0),
                            new Complex(Math.min(quadraticRoots[1].re, y), 0) };
                }
                else
                {
                    // One real root and two complex roots
                    return new Complex[] { new Complex(y, 0), quadraticRoots[0], quadraticRoots[1] };
                }
            }

            default:
                throw new RuntimeException("Bad switch; cannot happen");
        }
    }

    /**
     * QUARTIC POLYNOMIAL ROOT SOLVER
     * <p>
     * Calculates all real + complex roots of the quartic polynomial:<br>
     * q4 * x^4 + q3 * x^3 + q2 * x^2 + q1 * x + q0
     * <p>
     * The quartic root solver can handle any size of quartic coefficients and there is no danger of overflow, due to proper
     * rescaling of the quartic polynomial.
     * <p>
     * The order of the roots is as follows:<br>
     * 1) For real roots, the order is according to their algebraic value on the number scale (largest positive first, largest
     * negative last).<br>
     * 2) For complex conjugate pair roots, the order is according to the algebraic value of their real parts (largest positive
     * first). If the real parts are equal, the order is according to the algebraic value of their imaginary parts (largest
     * first).<br>
     * 3) All real roots precede the complex ones.
     * @param q4 double; coefficient of the quartic term
     * @param q3 double; coefficient of the cubic term
     * @param q2 double; coefficient of the quadratic term
     * @param q1 double; coefficient of the linear term
     * @param q0 double; independent coefficient
     * @return Complex[]; array of Complex with all the roots
     */
    public static Complex[] quarticRoots(final double q4, final double q3, final double q2, final double q1, final double q0)
    {
        if (q4 == 0)
        {
            return cubicRoots(q3, q2, q1, q0);
        }
        return quarticRoots(q3 / q4, q2 / q4, q1 / q4, q0 / q4);
    }

    /**
     * QUARTIC POLYNOMIAL ROOT SOLVER
     * <p>
     * Calculates all real + complex roots of the quartic polynomial:<br>
     * x^4 + q3 * x^3 + q2 * x^2 + q1 * x + q0
     * <p>
     * The quartic root solver can handle any size of quartic coefficients and there is no danger of overflow, due to proper
     * rescaling of the quartic polynomial.
     * <p>
     * The order of the roots is as follows:<br>
     * 1) For real roots, the order is according to their algebraic value on the number scale (largest positive first, largest
     * negative last).<br>
     * 2) For complex conjugate pair roots, the order is according to the algebraic value of their real parts (largest positive
     * first). If the real parts are equal, the order is according to the algebraic value of their imaginary parts (largest
     * first).<br>
     * 3) All real roots precede the complex ones.
     * @param q3 double; coefficient of the cubic term
     * @param q2 double; coefficient of the quadratic term
     * @param q1 double; coefficient of the linear term
     * @param q0 double; independent coefficient
     * @return Complex[]; array of Complex with all the roots
     */
    public static Complex[] quarticRoots(final double q3, final double q2, final double q1, final double q0)
    {
        return quarticRoots(q3, q2, q1, q0, false);
    }

    /**
     * QUARTIC POLYNOMIAL ROOT SOLVER
     * <p>
     * Calculates all real + complex roots of the quartic polynomial:<br>
     * x^4 + q3 * x^3 + q2 * x^2 + q1 * x + q0
     * <p>
     * An option for printing detailed info about the intermediate stages in solving the quartic is available. This enables a
     * detailed check in case something went wrong and the roots obtained are not proper.<br>
     * The quartic root solver can handle any size of quartic coefficients and there is no danger of overflow, due to proper
     * rescaling of the quartic polynomial.
     * <p>
     * The order of the roots is as follows:<br>
     * 1) For real roots, the order is according to their algebraic value on the number scale (largest positive first, largest
     * negative last).<br>
     * 2) For complex conjugate pair roots, the order is according to the algebraic value of their real parts (largest positive
     * first). If the real parts are equal, the order is according to the algebraic value of their imaginary parts (largest
     * first).<br>
     * 3) All real roots precede the complex ones.
     * @param q3 double; coefficient of the cubic term
     * @param q2 double; coefficient of the quadratic term
     * @param q1 double; coefficient of the linear term
     * @param q0 double; independent coefficient
     * @param verbose boolean; if true; produce debugging output; if false; do not produce debugging output
     * @return Complex[]; array of Complex with all the roots
     */
    public static Complex[] quarticRoots(final double q3, final double q2, final double q1, final double q0,
            final boolean verbose)
    {
        int quarticType;

        final int biquadratic = 2;
        final int cubic = 3;
        final int general = 4;

        double a0 = 0, a1 = 0, a2, a3 = 0;
        double a, b, c, d, k, s, t, u = 0, x, y, z;

        final double macheps = Math.ulp(1.0);

        if (verbose)
        {
            System.out.println("initial quartic q3    = " + q3);
            System.out.println("initial quartic q2    = " + q2);
            System.out.println("initial quartic q1    = " + q1);
            System.out.println("initial quartic q0    = " + q0);
            System.out.println("------------------------------------------------");
        }
        /*
         * Handle special cases. Since the cubic solver handles all its special cases by itself, we need to check only for two
         * cases:<br> 1) independent term is zero -> solve cubic and include the zero root <br> 2) the biquadratic case.
         */
        if (q0 == 0.0)
        {
            k = 1.0;
            a3 = q3;
            a2 = q2;
            a1 = q1;
            quarticType = cubic;
        }
        else if (q3 == 0.0 && q1 == 0.0)
        {
            k = 1.0;
            a2 = q2;
            a0 = q0;
            quarticType = biquadratic;
        }
        else
        {
            /*
             * The general case. Rescale quartic polynomial, such that largest absolute coefficient is (exactly!) equal to 1.
             * Honor the presence of a special quartic case that might have been obtained during the rescaling process (due to
             * underflow in the coefficients).
             */
            s = Math.abs(q3);
            t = Math.sqrt(Math.abs(q2));
            u = Math.cbrt(Math.abs(q1));
            x = Math.sqrt(Math.sqrt(Math.abs(q0)));
            y = Math.max(Math.max(Math.max(s, t), u), x);

            if (y == s)
            {
                k = 1.0 / s;
                a3 = sign(1.0, q3);
                a2 = (q2 * k) * k;
                a1 = ((q1 * k) * k) * k;
                a0 = (((q0 * k) * k) * k) * k;
            }
            else if (y == t)
            {
                k = 1.0 / t;
                a3 = q3 * k;
                a2 = sign(1.0, q2);
                a1 = ((q1 * k) * k) * k;
                a0 = (((q0 * k) * k) * k) * k;
            }
            else if (y == u)
            {
                k = 1.0 / u;
                a3 = q3 * k;
                a2 = (q2 * k) * k;
                a1 = sign(1.0, q1);
                a0 = (((q0 * k) * k) * k) * k;
            }
            else
            {
                k = 1.0 / x;
                a3 = q3 * k;
                a2 = (q2 * k) * k;
                a1 = ((q1 * k) * k) * k;
                a0 = sign(1.0, q0);
            }

            k = 1.0 / k;

            if (verbose)
            {
                System.out.println("rescaling factor      = " + k);
                System.out.println("------------------------------------------------");
                System.out.println("rescaled quartic q3   = " + a3);
                System.out.println("rescaled quartic q2   = " + a2);
                System.out.println("rescaled quartic q1   = " + a1);
                System.out.println("rescaled quartic q0   = " + a0);
                System.out.println("------------------------------------------------");
            }

            if (a0 == 0.0)
            {
                quarticType = cubic;
            }
            else if (a3 == 0.0 && a1 == 0.0)
            {
                quarticType = biquadratic;
            }
            else
            {
                quarticType = general;
            }
        }

        switch (quarticType)
        {
            case cubic: // 1) The quartic with independent term = 0 -> solve cubic and add a zero root.
            {
                // x^4 + q3 * x^3 + q2 * x^2 + q1 * x + 0 == x * (x^3 + q3 * x^2 + q2 * x + q1)
                Complex[] cubicRoots = cubicRoots(a3, a2, a1, verbose);

                if (cubicRoots.length == 3 && cubicRoots[0].isReal() && cubicRoots[1].isReal())
                {
                    // Three real roots of the cubic; ordered x >= y >= z
                    x = cubicRoots[0].re * k;
                    y = cubicRoots[1].re * k;
                    z = cubicRoots[2].re * k;

                    return new Complex[] { new Complex(Math.max(x, 0), 0), new Complex(Math.max(y, Math.min(x, 0)), 0),
                            new Complex(Math.max(z, Math.min(y, 0)), 0), new Complex(Math.min(z, 0), 0) };
                }
                else
                {
                    // One real cubic root; should be in the first entry of the array
                    if (!cubicRoots[0].isReal())
                    {
                        throw new RuntimeException("Cannot happen");
                    }
                    x = cubicRoots[0].re * k;

                    return new Complex[] { new Complex(Math.max(0, x), 0), new Complex(Math.min(0, x), 0), cubicRoots[1],
                            cubicRoots[2] };
                }
            }

            case biquadratic: // The quartic with x^3 and x terms = 0 -> solve biquadratic.
            {
                Complex[] quadraticRoots = quadraticRoots(q2, q0);
                if (quadraticRoots.length == 2 && quadraticRoots[0].isReal() && quadraticRoots[1].isReal())
                {
                    x = quadraticRoots[0].re; // real roots of quadratic are ordered x >= y
                    y = quadraticRoots[1].re;

                    if (y >= 0.0)
                    {
                        x = Math.sqrt(x) * k;
                        y = Math.sqrt(y) * k;
                        return new Complex[] { new Complex(x, 0), new Complex(y, 0), new Complex(-y, 0), new Complex(-x, 0) };
                    }
                    else if (x >= 0.0 && y < 0.0)
                    {
                        x = Math.sqrt(x) * k;
                        y = Math.sqrt(Math.abs(y)) * k;
                        return new Complex[] { new Complex(x, 0), new Complex(-x, 0), new Complex(0, y), new Complex(0, -y) };
                    }
                    else if (x < 0.0)
                    {
                        x = Math.sqrt(Math.abs(x)) * k;
                        y = Math.sqrt(Math.abs(y)) * k;
                        return new Complex[] { new Complex(0, y), new Complex(0, x), new Complex(0, -x), new Complex(0, -y) };
                    }
                }
                else
                {
                    // complex conjugate pair biquadratic roots x +/- iy.

                    x = quadraticRoots[0].re * 0.5;
                    y = quadraticRoots[0].im * 0.5;
                    z = Math.sqrt(x * x + y * y);
                    y = Math.sqrt(z - x) * k;
                    x = Math.sqrt(z + x) * k;
                    return new Complex[] { new Complex(x, y), new Complex(x, -y), new Complex(-x, y), new Complex(-x, -y) };

                }
            }

            case general:
            {
                int nReal;
                /*
                 * 3) The general quartic case. Search for stationary points. Set the first derivative polynomial (cubic) equal
                 * to zero and find its roots. Check, if any minimum point of Q(x) is below zero, in which case we must have
                 * real roots for Q(x). Hunt down only the real root, which will potentially converge fastest during Newton
                 * iterates. The remaining roots will be determined by deflation Q(x) -> cubic.<p> The best roots for the Newton
                 * iterations are the two on the opposite ends, i.e. those closest to the +2 and -2. Which of these two roots to
                 * take, depends on the location of the Q(x) minima x = s and x = u, with s > u. There are three cases:<br> 1)
                 * both Q(s) and Q(u) < 0<br> The best root is the one that corresponds to the lowest of these minima. If Q(s)
                 * is lowest -> start Newton from +2 downwards (or zero, if s < 0 and a0 > 0). If Q(u) is lowest -> start Newton
                 * from -2 upwards (or zero, if u > 0 and a0 > 0).<br> 2) only Q(s) < 0>br? With both sides +2 and -2 possible
                 * as a Newton starting point, we have to avoid the area in the Q(x) graph, where inflection points are present.
                 * Solving Q''(x) = 0, leads to solutions x = -a3/4 +/- discriminant, i.e. they are centered around -a3/4. Since
                 * both inflection points must be either on the r.h.s or l.h.s. from x = s, a simple test where s is in relation
                 * to -a3/4 allows us to avoid the inflection point area.<br> 3) only Q(u) < 0<br> Same of what has been said
                 * under 2) but with x = u.
                 */

                x = 0.75 * a3;
                y = 0.50 * a2;
                z = 0.25 * a1;

                if (verbose)
                {
                    System.out.println("dQ(x)/dx cubic c2     = " + x);
                    System.out.println("dQ(x)/dx cubic c1     = " + y);
                    System.out.println("dQ(x)/dx cubic c0     = " + z);
                    System.out.println("------------------------------------------------");
                }

                Complex[] cubicRoots = cubicRoots(x, y, z);

                s = cubicRoots[0].re; // Q'(x) root s (real for sure)
                x = s + a3;
                x = x * s + a2;
                x = x * s + a1;
                x = x * s + a0; // Q(s)

                y = 1.0; // dual info: Q'(x) has more real roots, and if so, is Q(u) < 0 ?

                if (cubicRoots[1].isReal()) // then they're all real
                {
                    u = cubicRoots[2].re; // Q'(x) root u
                    y = u + a3;
                    y = y * u + a2;
                    y = y * u + a1;
                    y = y * u + a0; // Q(u)
                }

                if (verbose)
                {
                    System.out.println("dQ(x)/dx root s       = " + s);
                    System.out.println("Q(s)                  = " + x);
                    System.out.println("dQ(x)/dx root u       = " + u);
                    System.out.println("Q(u)                  = " + y);
                    System.out.println("------------------------------------------------");
                }

                if (x < 0.0 && y < 0.0)
                {
                    if (x < y)
                    {
                        if (s < 0.0)
                        {
                            x = 1.0 - sign(1.0, a0);
                        }
                        else
                        {
                            x = 2.0;
                        }
                    }
                    else if (u > 0.0)
                    {
                        x = -1.0 + sign(1.0, a0);
                    }
                    else
                    {
                        x = -2.0;
                    }

                    nReal = 1;
                }
                else if (x < 0.0)
                {
                    if (s < -a3 * 0.25)
                    {
                        if (s > 0.0)
                        {
                            x = -1.0 + sign(1.0, a0);
                        }
                        else
                        {
                            x = -2.0;
                        }
                    }
                    else if (s < 0.0)
                    {
                        x = 1.0 - sign(1.0, a0);
                    }
                    else
                    {
                        x = 2.0;
                    }
                    nReal = 1;
                }
                else if (y < 0.0)
                {
                    if (u < -a3 * 0.25)
                    {
                        if (u > 0.0)
                        {
                            x = -1.0 + sign(1.0, a0);
                        }
                        else
                        {
                            x = -2.0;
                        }
                    }
                    else if (u < 0.0)
                    {
                        x = 1.0 - sign(1.0, a0);
                    }
                    else
                    {
                        x = 2.0;
                    }
                    nReal = 1;
                }
                else
                {
                    nReal = 0;
                }
                /*
                 * Do all necessary Newton iterations. In case we have more than 2 oscillations, exit the Newton iterations and
                 * switch to bisection. Note, that from the definition of the Newton starting point, we always have Q(x) > 0 and
                 * Q'(x) starts (-ve/+ve) for the (-2/+2) starting points and (increase/decrease) smoothly and staying (< 0 / >
                 * 0). In practice, for extremely shallow Q(x) curves near the root, the Newton procedure can overshoot slightly
                 * due to rounding errors when approaching the root. The result are tiny oscillations around the root. If such a
                 * situation happens, the Newton iterations are abandoned after 3 oscillations and further location of the root
                 * is done using bisection starting with the oscillation brackets.
                 */
                if (nReal > 0)
                {
                    int oscillate = 0;
                    boolean bisection = false;
                    boolean converged = false;
                    int deflateCase;

                    while ((!converged) && (!bisection)) // Newton-Raphson iterates
                    {
                        y = x + a3;
                        z = x + y;
                        y = y * x + a2; // y = Q(x)
                        z = z * x + y;
                        y = y * x + a1; // z = Q'(x)
                        z = z * x + y;
                        y = y * x + a0;

                        if (y < 0.0) // does Newton start oscillating ?
                        {
                            oscillate = oscillate + 1; // increment oscillation counter
                            s = x; // save lower bisection bound
                        }
                        else
                        {
                            u = x; // save upper bisection bound
                        }

                        y = y / z; // Newton correction
                        x = x - y; // new Newton root

                        bisection = oscillate > 2; // activate bisection
                        converged = Math.abs(y) <= Math.abs(x) * macheps; // Newton convergence indicator

                        if (verbose)
                        {
                            System.out.println("Newton root           = " + x);
                        }
                    }

                    if (bisection)
                    {
                        t = u - s; // initial bisection interval
                        while (Math.abs(t) > Math.abs(x) * macheps) // bisection iterates
                        {
                            y = x + a3;
                            y = y * x + a2; // y = Q(x)
                            y = y * x + a1;
                            y = y * x + a0;

                            if (y < 0.0)
                            {
                                s = x;
                            }
                            else // keep bracket on root
                            {
                                u = x;
                            }

                            t = 0.5 * (u - s); // new bisection interval
                            x = s + t; // new bisection root

                            if (verbose)
                            {
                                System.out.println("Bisection root        = " + x);
                            }
                        }
                    }

                    if (verbose)
                    {
                        System.out.println("------------------------------------------------");
                    }
                    /*
                     * Find remaining roots -> reduce to cubic. The reduction to a cubic polynomial is done using composite
                     * deflation to minimize rounding errors. Also, while the composite deflation analysis is done on the
                     * reduced quartic, the actual deflation is being performed on the original quartic again to avoid enhanced
                     * propagation of root errors.
                     */
                    z = Math.abs(x);
                    a = Math.abs(a3);
                    b = Math.abs(a2); // prepare for composite deflation
                    c = Math.abs(a1);
                    d = Math.abs(a0);

                    y = z * Math.max(a, z); // take maximum between |x^2|,|a3 * x|

                    deflateCase = 1; // up to now, the maximum is |x^4| or |a3 * x^3|

                    if (y < b) // check maximum between |x^2|,|a3 * x|,|a2|
                    {
                        y = b * z; // the maximum is |a2| -> form |a2 * x|
                        deflateCase = 2; // up to now, the maximum is |a2 * x^2|
                    }
                    else
                    {
                        y = y * z; // the maximum is |x^3| or |a3 * x^2|
                    }

                    if (y < c) // check maximum between |x^3|,|a3 * x^2|,|a2 * x|,|a1|
                    {
                        y = c * z; // the maximum is |a1| -> form |a1 * x|
                        deflateCase = 3; // up to now, the maximum is |a1 * x|
                    }
                    else
                    {
                        y = y * z; // the maximum is |x^4|,|a3 * x^3| or |a2 * x^2|
                    }

                    if (y < d) // check maximum between |x^4|,|a3 * x^3|,|a2 * x^2|,|a1 * x|,|a0|
                    {
                        deflateCase = 4; // the maximum is |a0|
                    }

                    x = x * k; // 1st real root of original Q(x)

                    switch (deflateCase)
                    {
                        case 1:
                            z = 1.0 / x;
                            u = -q0 * z; // u -> backward deflation on original Q(x)
                            t = (u - q1) * z; // t -> backward deflation on original Q(x)
                            s = (t - q2) * z; // s -> backward deflation on original Q(x)
                            break;
                        case 2:
                            z = 1.0 / x;
                            u = -q0 * z; // u -> backward deflation on original Q(x)
                            t = (u - q1) * z; // t -> backward deflation on original Q(x)
                            s = q3 + x; // s -> forward deflation on original Q(x)
                            break;
                        case 3:
                            s = q3 + x; // s -> forward deflation on original Q(x)
                            t = q2 + s * x; // t -> forward deflation on original Q(x)
                            u = -q0 / x; // u -> backward deflation on original Q(x)
                            break;
                        case 4:
                            s = q3 + x; // s -> forward deflation on original Q(x)
                            t = q2 + s * x; // t -> forward deflation on original Q(x)
                            u = q1 + t * x; // u -> forward deflation on original Q(x)
                            break;
                        default:
                            throw new RuntimeException("Bad switch; cannot happen");
                    }

                    if (verbose)
                    {
                        System.out.println("Residual cubic c2     = " + s);
                        System.out.println("Residual cubic c1     = " + t);
                        System.out.println("Residual cubic c0     = " + u);
                        System.out.println("------------------------------------------------");
                    }

                    cubicRoots = cubicRoots(s, t, u, verbose);
                    nReal = 0;
                    for (Complex complex : cubicRoots)
                    {
                        if (complex.isReal())
                        {
                            nReal++;
                        }
                    }
                    if (nReal == 3)
                    {
                        s = cubicRoots[0].re;
                        t = cubicRoots[1].re; // real roots of cubic are ordered s >= t >= u
                        u = cubicRoots[2].re;
                        // Construct a new array and insert x at the appropriate place
                        return new Complex[] { new Complex(Math.max(s, x), 0), new Complex(Math.max(t, Math.min(s, x)), 0),
                                new Complex(Math.max(u, Math.min(t, x)), 0), new Complex(Math.min(u, x), 0) };
                    }
                    else // there is only one real cubic root here
                    {
                        s = cubicRoots[0].re;
                        return new Complex[] { new Complex(Math.max(s, x), 0), new Complex(Math.min(s, x), 0), cubicRoots[1],
                                cubicRoots[2] };
                    }
                }
                else
                {
                    /*
                     * As no real roots have been found by now, only complex roots are possible. Find real parts of roots first,
                     * followed by imaginary components.
                     */
                    s = a3 * 0.5;
                    t = s * s - a2;
                    u = s * t + a1; // value of Q'(-a3/4) at stationary point -a3/4

                    boolean notZero = (Math.abs(u) >= macheps); // H(-a3/4) is considered > 0 at stationary point

                    if (verbose)
                    {
                        System.out.println("dQ/dx (-a3/4) value   = " + u);
                        System.out.println("------------------------------------------------");
                    }

                    boolean minimum;
                    if (a3 != 0.0)
                    {
                        s = a1 / a3;
                        minimum = (a0 > s * s); // H''(-a3/4) > 0 -> minimum
                    }
                    else
                    {
                        minimum = (4 * a0 > a2 * a2); // H''(-a3/4) > 0 -> minimum
                    }

                    boolean iterate = notZero || (!notZero && minimum);

                    if (iterate)
                    {
                        x = sign(2.0, a3); // initial root -> target = smaller mag root

                        int oscillate = 0;
                        boolean bisection = false;
                        boolean converged = false;

                        while (!converged && !bisection) // ! Newton-Raphson iterates
                        {
                            a = x + a3;
                            b = x + a; // a = Q(x)
                            c = x + b;
                            d = x + c; // b = Q'(x)
                            a = a * x + a2;
                            b = b * x + a; // c = Q''(x) / 2
                            c = c * x + b;
                            a = a * x + a1; // d = Q'''(x) / 6
                            b = b * x + a;
                            a = a * x + a0;
                            y = a * d * d - b * c * d + b * b; // y = H(x), usually < 0
                            z = 2 * d * (4 * a - b * d - c * c); // z = H'(x)

                            if (y > 0.0) // does Newton start oscillating ?
                            {
                                oscillate = oscillate + 1; // increment oscillation counter
                                s = x; // save upper bisection bound
                            }
                            else
                            {
                                u = x; // save lower bisection bound
                            }

                            y = y / z; // Newton correction
                            x = x - y; // new Newton root

                            bisection = oscillate > 2; // activate bisection
                            converged = Math.abs(y) <= Math.abs(x) * macheps; // Newton convergence criterion

                            if (verbose)
                            {
                                System.out.println("Newton H(x) root      = " + x);
                            }

                        }

                        if (bisection)
                        {
                            t = u - s; // initial bisection interval
                            while (Math.abs(t) > Math.abs(x * macheps)) // bisection iterates
                            {
                                a = x + a3;
                                b = x + a; // a = Q(x)
                                c = x + b;
                                d = x + c; // b = Q'(x)
                                a = a * x + a2;
                                b = b * x + a; // c = Q''(x) / 2
                                c = c * x + b;
                                a = a * x + a1; // d = Q'''(x) / 6
                                b = b * x + a;
                                a = a * x + a0;
                                y = a * d * d - b * c * d + b * b; // y = H(x)

                                if (y > 0.0)
                                {
                                    s = x;
                                }
                                else // keep bracket on root
                                {
                                    u = x;
                                }

                                t = 0.5 * (u - s); // new bisection interval
                                x = s + t; // new bisection root

                                if (verbose)
                                {
                                    System.out.println("Bisection H(x) root   = " + x);
                                }

                            }
                        }

                        if (verbose)
                        {
                            System.out.println("------------------------------------------------");
                        }

                        a = x * k; // 1st real component -> a
                        b = -0.5 * q3 - a; // 2nd real component -> b

                        x = 4 * a + q3; // Q'''(a)
                        y = x + q3 + q3;
                        y = y * a + q2 + q2; // Q'(a)
                        y = y * a + q1;
                        y = Math.max(y / x, 0.0); // ensure Q'(a) / Q'''(a) >= 0
                        x = 4 * b + q3; // Q'''(b)
                        z = x + q3 + q3;
                        z = z * b + q2 + q2; // Q'(b)
                        z = z * b + q1;
                        z = Math.max(z / x, 0.0); // ensure Q'(b) / Q'''(b) >= 0
                        c = a * a; // store a^2 for later
                        d = b * b; // store b^2 for later
                        s = c + y; // magnitude^2 of (a + iy) root
                        t = d + z; // magnitude^2 of (b + iz) root

                        if (s > t) // minimize imaginary error
                        {
                            c = Math.sqrt(y); // 1st imaginary component -> c
                            d = Math.sqrt(q0 / s - d); // 2nd imaginary component -> d
                        }
                        else
                        {
                            c = Math.sqrt(q0 / t - c); // 1st imaginary component -> c
                            d = Math.sqrt(z); // 2nd imaginary component -> d
                        }
                    }
                    else // no bisection -> real components equal
                    {

                        a = -0.25 * q3; // 1st real component -> a
                        b = a; // 2nd real component -> b = a

                        x = a + q3;
                        x = x * a + q2; // Q(a)
                        x = x * a + q1;
                        x = x * a + q0;
                        y = -0.1875 * q3 * q3 + 0.5 * q2; // Q''(a) / 2
                        z = Math.max(y * y - x, 0.0); // force discriminant to be >= 0
                        z = Math.sqrt(z); // square root of discriminant
                        y = y + sign(z, y); // larger magnitude root
                        x = x / y; // smaller magnitude root
                        c = Math.max(y, 0.0); // ensure root of biquadratic > 0
                        d = Math.max(x, 0.0); // ensure root of biquadratic > 0
                        c = Math.sqrt(c); // large magnitude imaginary component
                        d = Math.sqrt(d); // small magnitude imaginary component
                    }

                    if (a > b)
                    {
                        return new Complex[] { new Complex(a, c), new Complex(a, -c), new Complex(b, d), new Complex(b, -d) };
                    }
                    else if (a < b)
                    {
                        return new Complex[] { new Complex(b, d), new Complex(b, -d), new Complex(a, c), new Complex(a, -c) };
                    }
                    else
                    {
                        return new Complex[] { new Complex(a, c), new Complex(a, -c), new Complex(a, d), new Complex(a, -d) };
                    }

                } // # of real roots 'if'
            }

            default:
                throw new RuntimeException("Bad switch; cannot happen");
        } // end select ! quartic type select
    }
}
