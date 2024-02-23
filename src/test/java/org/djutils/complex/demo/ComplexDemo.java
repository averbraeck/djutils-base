package org.djutils.complex.demo;

import org.djutils.complex.Complex;
import org.djutils.complex.ComplexMath;

/**
 * ComplexDemo.java. <br>
 * <br>
 * Copyright (c) 2021-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class ComplexDemo
{
    /**
     * Do not instantiate.
     */
    private ComplexDemo()
    {
        // Do not instantiate
    }

    /**
     * Demonstrate the Complex class.
     * @param args String[]; the command line arguments (not used).
     */
    public static void main(final String[] args)
    {
        constructors();
        System.out.println();
        fieldsGettersNormAndPhi();
        System.out.println();
        constants();
        System.out.println();
        binaryOps();
        System.out.println();
        mathFunctions();
    }

    /**
     * Constructors.
     */
    public static void constructors()
    {
        Complex z1 = new Complex(123.456, -345.678);
        System.out.println(z1);
        Complex z2 = new Complex(543.210); // Create a complex with imaginary component 0.0
        System.out.println(z2);
    }

    /**
     * Fields, getters, norm and phi.
     */
    public static void fieldsGettersNormAndPhi()
    {
        Complex z = new Complex(123.456, -345.678);
        System.out.println("re=" + z.re + ", im=" + z.im + ", getRe:" + z.getRe() + ", getIm:" + z.getIm() + ", norm:"
                + z.norm() + ", phi:" + z.phi());
    }
    
    /**
     * Constants.
     */
    public static void constants()
    {
        System.out.println("ZERO=" + Complex.ZERO);
        System.out.println("ONE=" + Complex.ONE);
        System.out.println("MINUS_ONE=" + Complex.MINUS_ONE);
        System.out.println("I=" + Complex.I);
        System.out.println("MINUS_I=" + Complex.MINUS_I);
    }
    
    /**
     * Binary operations.
     */
    public static void binaryOps()
    {
        Complex z1 = new Complex(3, 4);
        Complex z2 = new Complex(-2, 5);
        System.out.println("z1=" + z1);
        System.out.println("z2=" + z2);
        System.out.println("z1 + z2=" + z1.plus(z2));
        System.out.println("z1 - z2=" + z1.minus(z2));
        System.out.println("z1 * z2=" + z1.times(z2));
        System.out.println("z1 / z2=" + z1.divideBy(z2));
        System.out.println("z1 * I=" + z1.times(Complex.I));
        System.out.println("I * I=" + Complex.I.times(Complex.I));
    }

    /**
     * Math functions.
     */
    public static void mathFunctions()
    {
        Complex z = new Complex(3, -5);
        System.out.println(z);
        System.out.println(ComplexMath.sqrt(z));
        System.out.println(ComplexMath.sqrt(z).rotate(Math.PI));
        System.out.println(ComplexMath.sqrt(z).times(-1));
        System.out.println(ComplexMath.cbrt(z));
        System.out.println(ComplexMath.cbrt(z).rotate(2 * Math.PI / 3));
        System.out.println(ComplexMath.cbrt(z).rotate(4 * Math.PI / 3));
        System.out.println(ComplexMath.exp(z));
        System.out.println(ComplexMath.ln(z));
        System.out.println(ComplexMath.sin(z));
        System.out.println(ComplexMath.cos(z));
        System.out.println(ComplexMath.tan(z));
        System.out.println(ComplexMath.asin(z));
        System.out.println(ComplexMath.acos(z));
        System.out.println(ComplexMath.atan(z));
        System.out.println(ComplexMath.sinh(z));
        System.out.println(ComplexMath.cosh(z));
        System.out.println(ComplexMath.tanh(z));
        System.out.println(ComplexMath.asinh(z));
        System.out.println(ComplexMath.acosh(z));
        System.out.println(ComplexMath.atanh(z));
    }
    
}
