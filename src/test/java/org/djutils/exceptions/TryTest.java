package org.djutils.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Test the methods in the Try class.
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class TryTest
{

    /**
     * Test the assign methods in the Try class.
     * @throws RuntimeException if that happens (uncaught); this test has failed.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void tryAssignTest() throws RuntimeException
    {
        String nullPointer = null;
        String initialValueOfResult = "initial value of result";
        String result = initialValueOfResult;
        String formatWithoutArg = "format";
        String arg1 = "arg1";
        String formatWithOneArg = "format %s";
        String arg2 = "arg2";
        String formatWith2Args = "format %s %s";
        String arg3 = "arg3";
        String formatWith3Args = "format %s %s %s";
        String arg4 = "arg4";
        String formatWith4Args = "format %s %s %s %s";

        // test successes

        result = Try.assign(() -> String.format(formatWithoutArg), "Should not fail");
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail");
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), "Should not fail %s", arg1);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail %s", arg1);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), "Should not fail %s %s", arg1, arg2);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail %s %s", arg1, arg2);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), "Should not fail %s %s %s", arg1, arg2, arg3);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail %s %s %s", arg1,
                arg2, arg3);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), "Should not fail %s %s %s %s", arg1, arg2, arg3, arg4);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail %s %s %s %s", arg1,
                arg2, arg3, arg4);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), "Should not fail %s %s %s %s %s", arg1, arg2, arg3, arg4,
                arg4);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail %s %s %s %s %s",
                arg1, arg2, arg3, arg4, arg4);
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        // test exceptions

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), formatWithoutArg);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), formatWithOneArg, arg1);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), formatWith2Args, arg1, arg2);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), formatWith3Args, arg1, arg2, arg3);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), formatWith4Args, arg1, arg2, arg3, arg4);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
            assertTrue(rte.getMessage().contains(arg4), "message contains arg4");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        result = Try.assign(() -> String.format(formatWithoutArg), RuntimeException.class, "Should not fail");
        assertEquals(formatWithoutArg, result, "assign should have succeeded");
        result = initialValueOfResult;

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWithoutArg);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWithOneArg,
                    arg1);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWith2Args,
                    arg1, arg2);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWith3Args,
                    arg1, arg2, arg3);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            result = Try.assign(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWith4Args,
                    arg1, arg2, arg3, arg4);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
            assertTrue(rte.getMessage().contains(arg4), "message contains arg4");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");
    }

    /** value to test for change by execute() method. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected String value;

    /**
     * setter for the value to be called from execute() method.
     * @param newResult the value to set
     */
    protected void setResult(final String newResult)
    {
        this.value = newResult;
    }

    /**
     * Test the execute methods in the Try class.
     * @throws RuntimeException if that happens (uncaught); this test has failed.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void tryExecuteTest() throws RuntimeException
    {
        String nullPointer = null;
        String initialValueOfResult = "initial value of result";
        String result = initialValueOfResult;
        String formatWithoutArg = "format";
        String arg1 = "arg1";
        String formatWithOneArg = "format %s";
        String arg2 = "arg2";
        String formatWith2Args = "format %s %s";
        String arg3 = "arg3";
        String formatWith3Args = "format %s %s %s";
        String arg4 = "arg4";
        String formatWith4Args = "format %s %s %s %s";

        // test successes

        Try.execute(() -> setResult(String.format(formatWithoutArg)), "Should not fail");
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), RuntimeException.class, "Should not fail");
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), "Should not fail %s", arg1);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), RuntimeException.class, "Should not fail %s", arg1);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), "Should not fail %s %s", arg1, arg2);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), RuntimeException.class, "Should not fail %s %s", arg1,
                arg2);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), "Should not fail %s %s %s", arg1, arg2, arg3);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), RuntimeException.class, "Should not fail %s %s %s", arg1,
                arg2, arg3);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), "Should not fail %s %s %s %s", arg1, arg2, arg3, arg4);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), RuntimeException.class, "Should not fail %s %s %s %s",
                arg1, arg2, arg3, arg4);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), "Should not fail %s %s %s %s %s", arg1, arg2, arg3, arg4,
                arg4);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        Try.execute(() -> setResult(String.format(formatWithoutArg)), RuntimeException.class, "Should not fail %s %s %s %s %s",
                arg1, arg2, arg3, arg4, arg4);
        assertEquals(formatWithoutArg, this.value, "assign should have succeeded");
        this.value = initialValueOfResult;

        // test exceptions

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWithoutArg);
            fail("String.format with nullPointer should have thrown a NullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWithOneArg, arg1);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWith2Args, arg1,
                    arg2);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWith3Args, arg1,
                    arg2, arg3);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), RuntimeException.class, formatWith4Args, arg1,
                    arg2, arg3, arg4);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
            assertTrue(rte.getMessage().contains(arg4), "message contains arg4");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), formatWithoutArg);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), formatWithOneArg, arg1);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), formatWith2Args, arg1, arg2);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), formatWith3Args, arg1, arg2, arg3);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");

        try
        {
            Try.execute(() -> String.format(nullPointer, "unused argument"), formatWith4Args, arg1, arg2, arg3, arg4);
            fail("String.format with nullPointer should have thrown a nullPointerException");
        }
        catch (RuntimeException rte)
        {
            assertTrue(rte.getCause().toString().contains("NullPointerException"),
                    "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            assertTrue(rte.getMessage().contains(formatWithoutArg), "message contains format");
            assertTrue(rte.getMessage().contains(arg1), "message contains arg1");
            assertTrue(rte.getMessage().contains(arg2), "message contains arg2");
            assertTrue(rte.getMessage().contains(arg3), "message contains arg3");
            assertTrue(rte.getMessage().contains(arg4), "message contains arg4");
        }
        assertEquals(initialValueOfResult, result, "Result has not changed");
    }

    /**
     * Can we get our intermittent test result to show up without the JUnit test harness?
     * @param args the command line arguments
     */
    public static void main(final String[] args)
    {
        String nullPointer = null;
        String arg1 = "arg1";
        String arg2 = "arg2";
        String arg3 = "arg3";
        String arg4 = "arg4";
        String formatWith4Args = "format %s %s %s %s";

        for (int iteration = 1; true; iteration++)
        {
            System.out.println("Starting iteration " + iteration);
            try
            {
                Try.execute(() -> String.format(nullPointer, "unused argument"), formatWith4Args, arg1, arg2, arg3, arg4);
                fail("String.format with nullPointer should have thrown a nullPointerException");
            }
            catch (RuntimeException rte)
            {
                if (!rte.getCause().toString().contains("NullPointerException"))
                {
                    System.out.println("Expected NullPointerException; got " + rte.getCause().toString());
                }
                assertTrue(rte.getCause().toString().contains("NullPointerException"),
                        "message cause is NullPointerException, instead got: " + rte.getCause().toString());
            }
        }
    }

}
