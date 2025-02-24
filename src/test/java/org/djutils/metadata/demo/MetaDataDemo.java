package org.djutils.metadata.demo;

import org.djutils.metadata.MetaData;
import org.djutils.metadata.ObjectDescriptor;

/**
 * MetaDataDemo.java. <br>
 * <br>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public final class MetaDataDemo
{
    /**
     * Do not instantiate.
     */
    private MetaDataDemo()
    {
        // Do not instantiate
    }

    /**
     * Demonstration of MetaData use.
     * @param args the command line arguments; not used
     */
    public static void main(final String[] args)
    {
        try
        {
            testSingleLong();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }

        try
        {
            testObjectArray();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Single object meta data that only permits a Long.
     * @throws RuntimeException ...
     */
    static void testSingleLong() throws RuntimeException
    {
        MetaData singleLong = new MetaData("Payload is one Long", "Payload is one Long; anything else is wrong",
                new ObjectDescriptor("Long", "64 bit Long", Long.class));
        singleLong.verifyComposition(123L); // OK
        singleLong.verifyComposition(123); // Throws ClassCastException
    }

    /**
     * MetaData demo using array of Object.
     * @throws RuntimeException ...
     */
    static void testObjectArray() throws RuntimeException
    {
        MetaData objectArray = new MetaData("Payload is object array",
                "Payload is an object array of 3 objects; a String, a Double and an Integer",
                new ObjectDescriptor[] {new ObjectDescriptor("String", "String", String.class),
                        new ObjectDescriptor("Double", "Double", Double.class),
                        new ObjectDescriptor("Integer", "Integer", Integer.class)});
        Object[] payload = new Object[] {"This is the string", 123.456, 987};
        objectArray.verifyComposition(payload); // OK
        payload = new Object[] {"String", "123.456", 987L};
        objectArray.verifyComposition(payload); // Throws ClassCastException
    }

}
