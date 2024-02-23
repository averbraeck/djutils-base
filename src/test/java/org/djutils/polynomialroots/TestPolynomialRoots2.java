package org.djutils.polynomialroots;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.djutils.complex.Complex;
import org.junit.jupiter.api.Test;

/**
 * TestPolynomialRoots2.java.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TestPolynomialRoots2
{

    /**
     * Test the solver for quadratic (and simpler) cases and ensure that the cubic solver falls back to the quadratic solver
     * when appropriate.
     */
    @Test
    public void quadraticTest()
    {
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.0001, 10000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    // System.out.print(a + " x^2 + " + b + " x " + c + " = 0");
                    Complex[] roots = PolynomialRoots2.quadraticRoots(a, b, c);
                    int expectedNumber = a != 0 ? 2 : b != 0 ? 1 : 0;
                    assertEquals(expectedNumber, roots.length, "number of roots");
                    // System.out.print(": " + expectedNumber + " solutions");
                    // Check that each root is indeed a root
                    for (Complex root : roots)
                    {
                        // System.out.print(" " + root);
                        Complex result = root.times(root).times(a).plus(root.times(b)).plus(c);
                        double margin = Math.max(root.norm() / 1e6, 1E-300);
                        assertEquals(0, result.re, margin, "root is a root of the equation; re");
                        assertEquals(0, result.im, margin, "root is a root of the equation; im");
                    }
                    // System.out.println();
                    Complex[] alternate = PolynomialRoots2.cubicRootsDurandKerner(0, a, b, c);
                    assertArrayEquals(roots, alternate, "cubic solver returns same results as quadratic solver if a is 0");
                }
            }
        }
        double q1 = 3 * Math.sqrt(Double.MAX_VALUE);
        Complex[] roots = PolynomialRoots2.quadraticRoots(q1, 1);
        assertEquals(2, roots.length, "two roots");
        assertEquals(0, roots[0].re, 0.0001, "first root is 0: re");
        assertEquals(0, roots[0].im, 0.0001, "first root is 0: im");
        // System.out.println(roots[1]);
        assertEquals(-q1, roots[1].re, q1 / 1e7, "second root is 0: re");
        assertEquals(0, roots[1].im, 0.0001, "second root is 0: im");
    }

    /**
     * Test the cubic solver.
     */
    @Test
    public void cubicTestDurandKerner()
    {
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        // System.out.print(a + " x^3 " + b + " x^2 + " + c + " x + " + d + " = 0");
                        Complex[] roots = PolynomialRoots2.cubicRootsDurandKerner(a, b, c, d);
                        int expectedNumber = a != 0 ? 3 : b != 0 ? 2 : c != 0 ? 1 : 0;
                        assertEquals(expectedNumber, roots.length, "number of roots");
                        // System.out.print(": " + expectedNumber + " solutions");
                        // Check that each root is indeed a root
                        for (Complex root : roots)
                        {
                            // System.out.print(" " + root);
                            Complex result = root.times(root).times(root).times(a).plus(root.times(root).times(b))
                                    .plus(root.times(c)).plus(d);
                            double margin = Math.max(root.norm() / 1e6, 1E-300);
                            assertEquals(0, result.re, margin, "root is a root of the equation; re");
                            assertEquals(0, result.im, margin, "root is a root of the equation; im");
                        }
                        // System.out.println();
                    }
                }
            }
        }
    }

    /**
     * Test the quartic solver.
     */
    @Test
    public void quarticTestDurandKerner()
    {
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.01, 100};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        for (double e : paramValues)
                        {
                            // System.out.print(a + " x^4 + " + b + " x^3 + " + c + " x^2 + " + d + " x + " + e + " = 0");
                            Complex[] roots = PolynomialRoots2.quarticRootsDurandKerner(a, b, c, d, e);
                            int expectedNumber = a != 0 ? 4 : b != 0 ? 3 : c != 0 ? 2 : d != 0 ? 1 : 0;
                            assertEquals(expectedNumber, roots.length, "number of roots");
                            // System.out.print(": " + expectedNumber + " solutions");
                            // Check that each root is indeed a root
                            for (Complex root : roots)
                            {
                                // System.out.print(" " + root);
                                // TODO compute result with less chance of loss of precision
                                Complex result = root.times(root).times(root).times(root).times(a)
                                        .plus(root.times(root).times(root).times(b)).plus(root.times(root).times(c))
                                        .plus(root.times(d)).plus(e);
                                double margin = Math.max(root.norm() / 2e5, 1E-300);
                                assertEquals(0, result.re, margin, a + " x^4 + " + b + " x^3 + " + c + " x^2 + " + d + " x + " + e
                                                + " = 0 : root is a root of the equation; re");
                                assertEquals(0, result.im, margin, a + " x^4 + " + b + " x^3 + " + c + " x^2 + " + d + " x + " + e
                                                + " = 0 : root is a root of the equation; im");
                            }
                            // System.out.println();
                        }
                    }
                }
            }
        }
    }

    /**
     * Test the cubic solver.
     */
    @Test
    public void cubicTestAberthEhrlich()
    {
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        // System.out.print(a + " x^3 " + b + " x^2 + " + c + " x + " + d + " = 0");
                        Complex[] roots = PolynomialRoots2.cubicRootsAberthEhrlich(a, b, c, d);
                        int expectedNumber = a != 0 ? 3 : b != 0 ? 2 : c != 0 ? 1 : 0;
                        assertEquals(expectedNumber, roots.length, "number of roots");
                        // System.out.print(": " + expectedNumber + " solutions");
                        // Check that each root is indeed a root
                        for (Complex root : roots)
                        {
                            // System.out.print(" " + root);
                            Complex result = root.times(root).times(root).times(a).plus(root.times(root).times(b))
                                    .plus(root.times(c)).plus(d);
                            double margin = Math.max(root.norm() / 1e6, 1E-300);
                            assertEquals(0, result.re, margin, "root is a root of the equation; re");
                            assertEquals(0, result.im, margin, "root is a root of the equation; im");
                        }
                        // System.out.println();
                    }
                }
            }
        }
    }

    /**
     * Test the quartic solver.
     */
    @Test
    public void quarticTestAberthEhrlich()
    {
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.01, 100};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        for (double e : paramValues)
                        {
                            // System.out.print(a + " x^4 + " + b + " x^3 + " + c + " x^2 + " + d + " x + " + e + " = 0");
                            Complex[] roots = PolynomialRoots2.quarticRootsAberthEhrlich(a, b, c, d, e);
                            int expectedNumber = a != 0 ? 4 : b != 0 ? 3 : c != 0 ? 2 : d != 0 ? 1 : 0;
                            assertEquals(expectedNumber, roots.length, "number of roots");
                            // System.out.print(": " + expectedNumber + " solutions");
                            // Check that each root is indeed a root
                            for (Complex root : roots)
                            {
                                // System.out.print(" " + root);
                                // TODO compute result with less chance of loss of precision
                                Complex result = root.times(root).times(root).times(root).times(a)
                                        .plus(root.times(root).times(root).times(b)).plus(root.times(root).times(c))
                                        .plus(root.times(d)).plus(e);
                                double margin = Math.max(root.norm() / 2e5, 1E-300);
                                assertEquals(0, result.re, margin, a + " x^4 + " + b + " x^3 + " + c + " x^2 + " + d + " x + " + e
                                                + " = 0 : root is a root of the equation; re");
                                assertEquals(0, result.im, margin, a + " x^4 + " + b + " x^3 + " + c + " x^2 + " + d + " x + " + e
                                                + " = 0 : root is a root of the equation; im");
                            }
                            // System.out.println();
                        }
                    }
                }
            }
        }
    }

    /**
     * Test the cubic solver.<br>
     * XXX: Not active yet. Code is in development
     */
    public void cubicTestNewtonFactor()
    {
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        // System.out.print(a + " x^3 + " + b + " x^2 + " + c + " x + " + d + " = 0");
                        Complex[] roots = PolynomialRoots2.cubicRootsNewtonFactor(a, b, c, d);
                        int expectedNumber = a != 0 ? 3 : b != 0 ? 2 : c != 0 ? 1 : 0;
                        assertEquals(expectedNumber, roots.length, "number of roots");
                        // System.out.print(": " + expectedNumber + " solutions");
                        // Check that each root is indeed a root
                        for (Complex root : roots)
                        {
                            // System.out.print(" " + root);
                            Complex result = root.times(root).times(root).times(a).plus(root.times(root).times(b))
                                    .plus(root.times(c)).plus(d);
                            double margin = Math.max(root.norm() / 1e4, 1E-3);
                            String eq = a + " x^3 + " + b + " x^2 + " + c + " x + " + d + " = 0";
                            assertEquals(0, result.re, margin, eq + ", root=" + root + ", result=" + result + "; re");
                            assertEquals(0, result.im, margin, eq + ", root=" + root + ", result=" + result + "; im");
                        }
                        // System.out.println();
                    }
                }
            }
        }
    }

}
