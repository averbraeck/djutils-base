package org.djutils.reflection;

/**
 * Test class to find in ClassUtil.classFileDescriptor. <br>
 * <br>
 * Copyright (c) 2003-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class TestClass
{
    /** state to communicate back to the test. */
    private String state = "";

    /**
     * Test class to find in ClassUtil.classFileDescriptor.
     */
    public TestClass()
    {
        // Test class to find in ClassUtil.classFileDescriptor
        this.state = "<init>";
    }

    /**
     * @param string string
     */
    public TestClass(final String string)
    {
        this.state = string;
    }

    /**
     * @param bool boolean
     */
    @SuppressWarnings("unused")
    private TestClass(final boolean bool)
    {
        this.state = Boolean.toString(bool);
    }

    /**
     * @return state
     */
    public String getState()
    {
        return this.state;
    }

    /** */
    public static class InnerPublic
    {
        /** state to communicate back to the test. */
        private String innerState = "";

        /** */
        public InnerPublic()
        {
            this.innerState = "<initInnerPublic>";
        }

        /**
         * @param state state
         */
        public InnerPublic(final String state)
        {
            this.innerState = state;
        }

        /**
         * @return state
         */
        public String getInnerState()
        {
            return this.innerState;
        }
    }
}
