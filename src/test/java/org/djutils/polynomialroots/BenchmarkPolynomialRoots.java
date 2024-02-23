package org.djutils.polynomialroots;

/**
 * BenchmarkPolynomialRoots tests the speed of different algorithms.
 * <p>
 * Copyright (c) 2020-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class BenchmarkPolynomialRoots
{
    /** utility class. */
    private BenchmarkPolynomialRoots()
    {
        // utility class
    }

    /**
     * Test the solver for quadratic (and simpler) cases and ensure that the cubic solver falls back to the quadratic solver
     * when appropriate.
     */
    public static void quadraticTest()
    {
        long time = System.currentTimeMillis();
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.0001, 10000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    PolynomialRoots2.quadraticRoots(a, b, c);
                }
            }
        }
        System.out.println("quadratic F77: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the cubic solver.
     */
    public static void cubicTest()
    {
        long time = System.currentTimeMillis();
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        PolynomialRoots.cubicRoots(a, b, c, d);
                    }
                }
            }
        }
        System.out.println("cubic F77: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the quartic solver.
     */
    public static void quarticTest()
    {
        long time = System.currentTimeMillis();
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
                            PolynomialRoots.quarticRoots(a, b, c, d, e);
                        }
                    }
                }
            }
        }
        System.out.println("quartic F77: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the cubic solver.
     */
    public static void cubicTestDurandKerner()
    {
        long time = System.currentTimeMillis();
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        PolynomialRoots2.cubicRootsDurandKerner(a, b, c, d);
                    }
                }
            }
        }
        System.out.println("cubic Durand-Kerner: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the quartic solver.
     */
    public static void quarticTestDurandKerner()
    {
        long time = System.currentTimeMillis();
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
                            PolynomialRoots2.quarticRootsDurandKerner(a, b, c, d, e);
                        }
                    }
                }
            }
        }
        System.out.println("quartic Durand-Kerner: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the cubic solver.
     */
    public static void cubicTestAberthEhrlich()
    {
        long time = System.currentTimeMillis();
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        PolynomialRoots2.cubicRootsAberthEhrlich(a, b, c, d);
                    }
                }
            }
        }
        System.out.println("cubic Aberth-Ehrlich: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the quartic solver.
     */
    public static void quarticTestAberthEhrlich()
    {
        long time = System.currentTimeMillis();
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
                            PolynomialRoots2.quarticRootsAberthEhrlich(a, b, c, d, e);
                        }
                    }
                }
            }
        }
        System.out.println("quartic Aberth-Ehrlich: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Test the cubic solver.
     */
    public static void cubicTestNewtonFactor()
    {
        long time = System.currentTimeMillis();
        double[] paramValues = new double[] {0, 1, 2, 3, 4, Math.PI, -Math.E, 0.001, 1000};
        for (double a : paramValues)
        {
            for (double b : paramValues)
            {
                for (double c : paramValues)
                {
                    for (double d : paramValues)
                    {
                        PolynomialRoots2.cubicRootsNewtonFactor(a, b, c, d);
                    }
                }
            }
        }
        System.out.println("cubic Cardano: " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * @param args String[] not used
     */
    public static void main(final String[] args)
    {
        quadraticTest();
        cubicTest();
        cubicTestDurandKerner();
        cubicTestAberthEhrlich();
        cubicTestNewtonFactor();
        quarticTest();
        quarticTestDurandKerner();
        quarticTestAberthEhrlich();
    }

}
