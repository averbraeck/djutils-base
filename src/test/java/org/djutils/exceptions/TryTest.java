package org.djutils.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

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
     * @param newResult String; the value to set
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
     * Test the fail methods in the Try class using assignments with lambda functions.
     * @throws RuntimeException if that happens (uncaught); this test has failed.
     */
    @SuppressWarnings({"checkstyle:methodlength", "null"})
    @Test
    public void tryFailTestAssignLambda() throws RuntimeException
    {
        // test case from Issue #7.
        try
        {
            Double dn = null;
            Double d1 = Double.valueOf(1.0);
            Try.testFail(() -> d1.toString());
            Try.testFail(() -> dn.toString());
        }
        catch (AssertionError e)
        {
            // ok, one of the two should have thrown an error
        }
        
        Try.testFail(() -> fnfAssignment());
        Try.testFail(() -> npeAssignment());
        Try.testFail(() -> fnfAssignment(), "message");
        Try.testFail(() -> npeAssignment(), "message");
        Try.testFail(() -> fnfAssignment(), Exception.class);
        Try.testFail(() -> npeAssignment(), Exception.class);
        Try.testFail(() -> fnfAssignment(), FileNotFoundException.class);
        Try.testFail(() -> fnfAssignment(), IOException.class);
        Try.testFail(() -> npeAssignment(), NullPointerException.class);
        Try.testFail(() -> fnfAssignment(), "message", Exception.class);
        Try.testFail(() -> npeAssignment(), "message", Exception.class);
        Try.testFail(() -> fnfAssignment(), "message", FileNotFoundException.class);
        Try.testFail(() -> fnfAssignment(), "message", IOException.class);
        Try.testFail(() -> npeAssignment(), "message", NullPointerException.class);

        // test assignment methods that do not throw exceptions
        try
        {
            Try.testFail(() -> Math.abs(-1.0));
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
        }

        try
        {
            Try.testFail(() -> Math.abs(-1.0), "xyz");
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(() -> Math.abs(-1.0), NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
        }

        try
        {
            Try.testFail(() -> Math.abs(-1.0), "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        // test assignment methods that throw the wrong exception

        try
        {
            Try.testFail(() -> fnfAssignment(), NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(() -> fnfAssignment(), "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(() -> npeAssignment(), IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(() -> npeAssignment(), "xyz", IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }
    }

    /**
     * Test the fail methods in the Try class using Assignments.
     * @throws RuntimeException if that happens (uncaught); this test has failed.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void tryFailTestAssign() throws RuntimeException
    {
        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        });

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign()
            {
                return npeAssignment();
            }
        });

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, "message");

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign()
            {
                return npeAssignment();
            }
        }, "message");

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, Exception.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign()
            {
                return npeAssignment();
            }
        }, Exception.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, FileNotFoundException.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, IOException.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign()
            {
                return npeAssignment();
            }
        }, NullPointerException.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, "message", Exception.class);
        
        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign()
            {
                return npeAssignment();
            }
        }, "message", Exception.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, "message", FileNotFoundException.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign() throws FileNotFoundException
            {
                return fnfAssignment();
            }
        }, "message", IOException.class);

        Try.testFail(new Try.Assignment<Object>()
        {
            @Override
            public Object assign()
            {
                return npeAssignment();
            }
        }, "message", NullPointerException.class);

        // test assignment methods that do not throw exceptions
        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign()
                {
                    return Math.abs(-1.0);
                }
            });
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
        }

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign()
                {
                    return Math.abs(-1.0);
                }
            }, "xyz");
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign()
                {
                    return Math.abs(-1.0);
                }
            }, NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
        }

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign()
                {
                    return Math.abs(-1.0);
                }
            }, "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        // test assignment methods that throw the wrong exception

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign() throws FileNotFoundException
                {
                    return fnfAssignment();
                }
            }, NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign() throws FileNotFoundException
                {
                    return fnfAssignment();
                }
            }, "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign()
                {
                    return npeAssignment();
                }
            }, IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(new Try.Assignment<Object>()
            {
                @Override
                public Object assign()
                {
                    return npeAssignment();
                }
            }, "xyz", IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Assignment"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }
    }

    /**
     * Test the fail methods in the Try class using lambda executions.
     * @throws RuntimeException if that happens (uncaught); this test has failed.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void tryFailTestExecuteLambda() throws RuntimeException
    {
        Try.testFail(() -> fnfExecution());
        Try.testFail(() -> npeExecution());
        Try.testFail(() -> fnfExecution(), "message");
        Try.testFail(() -> npeExecution(), "message");
        Try.testFail(() -> fnfExecution(), Exception.class);
        Try.testFail(() -> npeExecution(), Exception.class);
        Try.testFail(() -> fnfExecution(), FileNotFoundException.class);
        Try.testFail(() -> fnfExecution(), IOException.class);
        Try.testFail(() -> npeExecution(), NullPointerException.class);
        Try.testFail(() -> fnfExecution(), "message", Exception.class);
        Try.testFail(() -> npeExecution(), "message", Exception.class);
        Try.testFail(() -> fnfExecution(), "message", FileNotFoundException.class);
        Try.testFail(() -> fnfExecution(), "message", IOException.class);
        Try.testFail(() -> npeExecution(), "message", NullPointerException.class);

        // test execution methods that do not throw exceptions
        try
        {
            Try.testFail(() -> new HashSet<String>().clear());
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
        }

        try
        {
            Try.testFail(() -> new HashSet<String>().clear(), "xyz");
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(() -> new HashSet<String>().clear(), NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
        }

        try
        {
            Try.testFail(() -> new HashSet<String>().clear(), "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        // test execution methods that throw the wrong exception

        try
        {
            Try.testFail(() -> fnfExecution(), NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(() -> fnfExecution(), "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(() -> npeExecution(), IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(() -> npeExecution(), "xyz", IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }
    }

    /**
     * Test the fail methods in the Try class using Executions.
     * @throws RuntimeException if that happens (uncaught); this test has failed.
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void tryFailTestExecute() throws RuntimeException
    {
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        });

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute()
            {
                npeExecution();
            }
        });

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, "message");

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute()
            {
                npeExecution();
            }
        }, "message");

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, Exception.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute()
            {
                npeExecution();
            }
        }, Exception.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, FileNotFoundException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, IOException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute()
            {
                npeExecution();
            }
        }, NullPointerException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, "message", Exception.class);
        
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute()
            {
                npeExecution();
            }
        }, "message", Exception.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, "message", FileNotFoundException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws FileNotFoundException
            {
                fnfExecution();
            }
        }, "message", IOException.class);

        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute()
            {
                npeExecution();
            }
        }, "message", NullPointerException.class);

        // test assignment methods that do not throw exceptions
        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute()
                {
                    new HashSet<Double>().clear();
                }
            });
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
        }

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute()
                {
                    new HashSet<Double>().clear();
                }
            }, "xyz");
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute()
                {
                    new HashSet<Double>().clear();
                }
            }, NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
        }

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute()
                {
                    new HashSet<Double>().clear();
                }
            }, "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution did not throw any exception"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        // test assignment methods that throw the wrong exception

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute() throws FileNotFoundException
                {
                    fnfExecution();
                }
            }, NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute() throws FileNotFoundException
                {
                    fnfExecution();
                }
            }, "xyz", NullPointerException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute()
                {
                    npeExecution();
                }
            }, IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
        }

        try
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute()
                {
                    npeExecution();
                }
            }, "xyz", IllegalStateException.class);
        }
        catch (Throwable t)
        {
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("Execution"));
            assertTrue(t.getMessage().contains("unexpected Throwable"));
            assertTrue(t.getMessage().contains("xyz"));
        }
    }

    /**
     * Test method that throws FNFE.
     * @return Object (never happens)
     * @throws FileNotFoundException to test FNF and IO exceptions
     */
    private Object fnfAssignment() throws FileNotFoundException
    {
        throw new FileNotFoundException();
    }

    /**
     * Test method that throws FNFE.
     * @throws FileNotFoundException to test FNF and IO exceptions
     */
    private void fnfExecution() throws FileNotFoundException
    {
        throw new FileNotFoundException();
    }

    /**
     * Test method that throws NPE.
     * @return Object (never happens)
     */
    private Object npeAssignment()
    {
        throw new NullPointerException();
    }

    /**
     * Test method that throws NPE.
     */
    private void npeExecution()
    {
        throw new NullPointerException();
    }

    /**
     * Can we get our intermittent test result to show up without the JUnit test harness?
     * @param args String[]; the command line arguments
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
