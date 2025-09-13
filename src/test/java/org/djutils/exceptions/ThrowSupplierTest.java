package org.djutils.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

/**
 * ThrowSupplierTest tests the Supplier options in Throw.
 * <p>
 * Copyright (c) 2025-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ThrowSupplierTest
{
    /** status string. */
    private final String status = "status of the object is not ok!";

    /** has reportStatus been called? */
    private boolean statusReported = false;

    /**
     * Method that reports status.
     * @return status
     */
    private String reportStatus()
    {
        this.statusReported = true;
        return this.status;
    }

    /**
     * Check if status has been reported and reset the reporting boolean.
     * @return whether status has been reported
     */
    private boolean isStatusReported()
    {
        boolean b = this.statusReported;
        this.statusReported = false;
        return b;
    }

    /**
     * Test the Throw.when() methods.
     */
    @Test
    public void testThrowWhen()
    {
        UnitTest.testFail(() -> Throw.when(true, IllegalArgumentException.class, () -> reportStatus()), this.status,
                IllegalArgumentException.class);
        assertTrue(isStatusReported());

        UnitTest.testFail(() -> Throw.when(new Object(), true, IllegalArgumentException.class, () -> reportStatus()), this.status,
                IllegalArgumentException.class);
        assertTrue(isStatusReported());

        try
        {
            Throw.when(false, IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
        }
        catch (Exception e)
        {
            fail("Throw.when() should not have thrown an exception");
        }

        try
        {
            String s = Throw.when("abc", false, IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals("abc", s);
        }
        catch (Exception e)
        {
            fail("Throw.when() should not have thrown an exception");
        }
    }

    /**
     * Test the Throw.whenNull() method.
     */
    @Test
    public void testThrowWhenNull()
    {
        UnitTest.testFail(() -> Throw.whenNull(null, () -> reportStatus()), this.status, NullPointerException.class);
        assertTrue(isStatusReported());

        try
        {
            String s = Throw.whenNull("abc", () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals("abc", s);
        }
        catch (Exception e)
        {
            fail("Throw.whenNull() should not have thrown an exception");
        }
    }

    /**
     * Test the Throw.whenNaN() methods.
     */
    @Test
    public void testThrowWhenNaN()
    {
        double d = Double.NaN;
        Double dd = Double.valueOf(d);
        float f = Float.NaN;
        Float ff = Float.valueOf(f);

        UnitTest.testFail(() -> Throw.whenNaN(d, () -> reportStatus()), this.status, ArithmeticException.class);
        assertTrue(isStatusReported());
        UnitTest.testFail(() -> Throw.whenNaN(dd, () -> reportStatus()), this.status, ArithmeticException.class);
        assertTrue(isStatusReported());
        UnitTest.testFail(() -> Throw.whenNaN(f, () -> reportStatus()), this.status, ArithmeticException.class);
        assertTrue(isStatusReported());
        UnitTest.testFail(() -> Throw.whenNaN(ff, () -> reportStatus()), this.status, ArithmeticException.class);
        assertTrue(isStatusReported());

        try
        {
            double d1 = Throw.whenNaN(1.0d, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(1.0d, d1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            Double d1 = Throw.whenNaN(Double.valueOf(1.0d), () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(Double.valueOf(1.0d), d1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            float f1 = Throw.whenNaN(1.0f, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(1.0f, f1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            Float f1 = Throw.whenNaN(Float.valueOf(1.0f), () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(Float.valueOf(1.0f), f1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }
    }

    /**
     * Test the Throw.whenNaN() methods with another exception.
     */
    @Test
    public void testThrowWhenNaNException()
    {
        double d = Double.NaN;
        Double dd = Double.valueOf(d);
        float f = Float.NaN;
        Float ff = Float.valueOf(f);

        UnitTest.testFail(() -> Throw.whenNaN(d, IllegalArgumentException.class, () -> reportStatus()), this.status,
                IllegalArgumentException.class);
        assertTrue(isStatusReported());
        UnitTest.testFail(() -> Throw.whenNaN(dd, IllegalArgumentException.class, () -> reportStatus()), this.status,
                IllegalArgumentException.class);
        assertTrue(isStatusReported());
        UnitTest.testFail(() -> Throw.whenNaN(f, IllegalArgumentException.class, () -> reportStatus()), this.status,
                IllegalArgumentException.class);
        assertTrue(isStatusReported());
        UnitTest.testFail(() -> Throw.whenNaN(ff, IllegalArgumentException.class, () -> reportStatus()), this.status,
                IllegalArgumentException.class);
        assertTrue(isStatusReported());

        try
        {
            double d1 = Throw.whenNaN(1.0d, IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(1.0d, d1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            Double d1 = Throw.whenNaN(Double.valueOf(1.0d), IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(Double.valueOf(1.0d), d1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            float f1 = Throw.whenNaN(1.0f, IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(1.0f, f1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            Float f1 = Throw.whenNaN(Float.valueOf(1.0f), IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertEquals(Float.valueOf(1.0f), f1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }
    }

    /**
     * Test the Throw.whenNaN() methods with null argument.
     */
    @Test
    public void testThrowWhenNaNNull()
    {
        Double d = null;
        Float f = null;
        
        try
        {
            Double d1 = Throw.whenNaN(d, () -> reportStatus());
            assertFalse(isStatusReported());
            assertNull(d1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }
        
        try
        {
            Double d1 = Throw.whenNaN(d, IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertNull(d1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

        try
        {
            Float f1 = Throw.whenNaN(f, () -> reportStatus());
            assertFalse(isStatusReported());
            assertNull(f1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }
        
        try
        {
            Float f1 = Throw.whenNaN(f, IllegalArgumentException.class, () -> reportStatus());
            assertFalse(isStatusReported());
            assertNull(f1);
        }
        catch (Exception e)
        {
            fail("Throw.whenNaN() should not have thrown an exception");
        }

    }
}
