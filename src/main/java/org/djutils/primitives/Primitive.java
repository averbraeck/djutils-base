package org.djutils.primitives;

/**
 * The Primitive class is a utility class to deal with primitives. Besides widening and unwidening this class casts and parses
 * UTF8 strings into appropriate primitive classes.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 */
public final class Primitive
{
    /**
     * Utility class should not be instantiated.
     */
    private Primitive()
    {
        // Do not instantiate
    }

    /**
     * casts a set of values to classes.
     * @param classes the classes to cast to
     * @param values the values
     * @return the newly creates values
     */
    public static Object[] cast(final Class<?>[] classes, final Object[] values)
    {
        for (int i = 0; i < classes.length; i++)
        {
            values[i] = Primitive.cast(classes[i], values[i]);
        }
        return values;
    }

    /**
     * casts an object to a instance of clazz.
     * @param clazz the class to cast to
     * @param object the object to cast
     * @return the casted object
     */
    public static Object cast(final Class<?> clazz, final Object object)
    {
        if (clazz.isInstance(object) || !clazz.isPrimitive() || (clazz.equals(Primitive.getPrimitive(object.getClass()))))
        {
            return object;
        }

        // Boolean
        if (clazz.equals(boolean.class))
        {
            return Primitive.toBoolean(object);
        }

        // Character
        if (clazz.equals(char.class))
        {
            return Primitive.toCharacter(object);
        }

        // Byte
        if (clazz.equals(byte.class))
        {
            return Primitive.toByte(object);
        }

        // Double
        if (clazz.equals(double.class))
        {
            return Primitive.toDouble(object);
        }

        // Float
        if (clazz.equals(float.class))
        {
            return Primitive.toFloat(object);
        }

        // Long
        if (clazz.equals(long.class))
        {
            return Primitive.toLong(object);
        }

        // Integer
        if (clazz.equals(int.class))
        {
            return Primitive.toInteger(object);
        }

        // Short
        if (clazz.equals(short.class))
        {
            return Primitive.toShort(object);
        }
        return object;
    }

    /**
     * returns the primitiveClass of the name given as defined by the Java VM class constants. (i.e. both "int" and "I" return
     * int.class). Both void and "V" return void.class. null is returned whenever an unknown className is given.
     * @param className the className
     * @return Class the primitiveClass
     */
    public static Class<?> forName(final String className)
    {
        if (className.equals("int") || className.equals("I"))
        {
            return int.class;
        }
        if (className.equals("double") || className.equals("D"))
        {
            return double.class;
        }
        if (className.equals("byte") || className.equals("B"))
        {
            return byte.class;
        }
        if (className.equals("float") || className.equals("F"))
        {
            return float.class;
        }
        if (className.equals("long") || className.equals("J"))
        {
            return long.class;
        }
        if (className.equals("boolean") || className.equals("Z"))
        {
            return boolean.class;
        }
        if (className.equals("char") || className.equals("C"))
        {
            return char.class;
        }
        if (className.equals("short") || className.equals("S"))
        {
            return short.class;
        }
        if (className.equals("void") || className.equals("V"))
        {
            return void.class;
        }
        return null;
    }

    /**
     * gets the primitive of the given wrapperClass.
     * @param wrapperClass the wrapper class
     * @return the primitive Class. null is returned whenever wrapperClass is not a wrapperclass.
     */
    public static Class<?> getPrimitive(final Class<?> wrapperClass)
    {
        if (wrapperClass.equals(Integer.class))
        {
            return int.class;
        }
        if (wrapperClass.equals(Double.class))
        {
            return double.class;
        }
        if (wrapperClass.equals(Byte.class))
        {
            return byte.class;
        }
        if (wrapperClass.equals(Float.class))
        {
            return float.class;
        }
        if (wrapperClass.equals(Long.class))
        {
            return long.class;
        }
        if (wrapperClass.equals(Boolean.class))
        {
            return boolean.class;
        }
        if (wrapperClass.equals(Character.class))
        {
            return char.class;
        }
        if (wrapperClass.equals(Short.class))
        {
            return short.class;
        }
        return null;
    }

    /**
     * gets the wrapper of this primitive class.
     * @param primitiveClass the primitive class
     * @return the Class. null is returned whenever wrapperClass is not a wrapperclass.
     */
    public static Class<?> getWrapper(final Class<?> primitiveClass)
    {
        if (primitiveClass.equals(int.class))
        {
            return Integer.class;
        }
        if (primitiveClass.equals(double.class))
        {
            return Double.class;
        }
        if (primitiveClass.equals(byte.class))
        {
            return Byte.class;
        }
        if (primitiveClass.equals(float.class))
        {
            return Float.class;
        }
        if (primitiveClass.equals(long.class))
        {
            return Long.class;
        }
        if (primitiveClass.equals(boolean.class))
        {
            return Boolean.class;
        }
        if (primitiveClass.equals(char.class))
        {
            return Character.class;
        }
        if (primitiveClass.equals(short.class))
        {
            return Short.class;
        }
        throw new IllegalArgumentException(primitiveClass + " != primitive");
    }

    /**
     * casts an object to Boolean.
     * @param object the object
     * @return Boolean
     */
    public static Boolean toBoolean(final Object object)
    {
        if (object instanceof Number)
        {
            int value = ((Number) object).intValue();
            if (value == 1)
            {
                return Boolean.TRUE;
            }
            if (value == 0)
            {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("object.intValue !=0 && !=1");
        }
        return (Boolean) object;
    }

    /**
     * casts an object to Byte.
     * @param object the object
     * @return Byte
     */
    public static Byte toByte(final Object object)
    {
        if (object instanceof Number)
        {
            return Byte.valueOf(((Number) object).byteValue());
        }
        return (Byte) object;
    }

    /**
     * casts an object to Character.
     * @param object the object to parse
     * @return Integer the result
     */
    public static Character toCharacter(final Object object)
    {
        if (object instanceof Number)
        {
            return Character.valueOf((char) ((Number) object).byteValue());
        }
        return (Character) object;
    }

    /**
     * casts an object to Double.
     * @param object the object to parse
     * @return Integer the result
     */
    public static Double toDouble(final Object object)
    {
        return Double.valueOf(((Number) object).doubleValue());
    }

    /**
     * casts an object to Float.
     * @param object the object to parse
     * @return Float the result
     */
    public static Float toFloat(final Object object)
    {
        return Float.valueOf(((Number) object).floatValue());
    }

    /**
     * casts an object to Long.
     * @param object the object to parse
     * @return Long the result
     */
    public static Long toLong(final Object object)
    {
        return Long.valueOf(((Number) object).longValue());
    }

    /**
     * casts an object to Short.
     * @param object the object to parse
     * @return Long the result
     */
    public static Short toShort(final Object object)
    {
        return Short.valueOf(((Number) object).shortValue());
    }

    /**
     * casts an object to Integer.
     * @param object the object to parse
     * @return Integer the result
     */
    public static Integer toInteger(final Object object)
    {
        if (object instanceof Character)
        {
            return Integer.valueOf(((Character) object).charValue());
        }
        if (object instanceof Boolean)
        {
            if (((Boolean) object).booleanValue())
            {
                return Integer.valueOf(1);
            }
            return Integer.valueOf(0);
        }
        return Integer.valueOf(((Number) object).intValue());
    }

    /**
     * Returns true when the first class is assignable from the second class, e.g. when firstClass is Number and secondClass is
     * Integer, the result is true (a number is assignable from an integer, i.e. Number n = Integer.valueOf(4) will work, but
     * Integer i = new Number(4) will not work). This method also takes into account primitive types, so when firstClass is
     * Number, and secondClass is int, the result is true, or when the firstClass is int, and the second class is Integer /
     * firstClass is Integer and secondClass is int, the result will also be true.
     * @param firstClass the class to which secondClass should be assignable
     * @param secondClass the class from which firstClass should be assignable
     * @return firstClass.isAssignableFrom(secondClass) taking into acount primitive types
     */
    public static boolean isPrimitiveAssignableFrom(final Class<?> firstClass, final Class<?> secondClass)
    {
        boolean result = firstClass.isAssignableFrom(secondClass);
        if (!result)
        {
            if (firstClass.isPrimitive())
            {
                result = getWrapper(firstClass).isAssignableFrom(secondClass);
            }
            if (!result)
            {
                if (secondClass.isPrimitive())
                {
                    result = firstClass.isAssignableFrom(getWrapper(secondClass));
                }
            }
        }
        return result;
    }
}
