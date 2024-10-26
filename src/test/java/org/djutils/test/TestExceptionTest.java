package org.djutils.test;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * TestExceptionTest tests the ExceptionTest for an Exception.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TestExceptionTest
{
    /**
     * Test the generic exception class tester.
     */
    @Test
    public void testExceptionTest()
    {
        ExceptionTest.testExceptionClass(TestException.class);
        
        try
        {
            ExceptionTest.testExceptionClass(IncompleteException.class);
            fail("wrong!");
        }
        catch (AssertionError e)
        {
            if (e.getMessage().equals("wrong!"))
            {
                fail("An incomplete specification of an exception class should have thrown an error");
            }
        }
        
        try
        {
            ExceptionTest.testExceptionClass(PrivateException.class);
            fail("wrong!");
        }
        catch (AssertionError e)
        {
            if (e.getMessage().equals("wrong!"))
            {
                fail("An incomplete specification of an exception class should have thrown an error");
            }
        }
    }
    
    /** Test exception. */
    protected static class TestException extends Exception
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * The default empty constructor.
         */
        public TestException()
        {
            super();
        }

        /**
         * @param message the message to display
         * @param cause the cause of this exception
         */
        public TestException(final String message, final Throwable cause)
        {
            super(message, cause);
        }

        /**
         * @param message the message to display
         */
        public TestException(final String message)
        {
            super(message);
        }

        /**
         * @param cause the cause of this exception
         */
        public TestException(final Throwable cause)
        {
            super(cause);
        }
    }
    
    /** Incomplete exception. */
    protected static class IncompleteException extends Exception
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * The default empty constructor.
         */
        public IncompleteException()
        {
            super();
        }
    }
    
    /** Private exception with private constructors. */
    private static class PrivateException extends Exception
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * The default empty constructor.
         */
        private PrivateException()
        {
            super();
        }
        
        /**
         * @param message the message to display
         * @param cause the cause of this exception
         */
        private PrivateException(final String message, final Throwable cause)
        {
            super(message, cause);
        }

        /**
         * @param message the message to display
         */
        private PrivateException(final String message)
        {
            super(message);
        }

        /**
         * @param cause the cause of this exception
         */
        private PrivateException(final Throwable cause)
        {
            super(cause);
        }
    }
}
