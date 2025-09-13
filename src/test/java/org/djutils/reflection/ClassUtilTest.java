package org.djutils.reflection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.djutils.reflection.TestClass.InnerPublic;
import org.djutils.test.UnitTest;
import org.junit.jupiter.api.Test;

/**
 * The JUNIT Test for <code>ClassUtil</code>.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class ClassUtilTest
{
    /** */
    @Test
    public void testClassUtilClass()
    {
        assertEquals(0, ClassUtil.getClass(null).length);
        assertEquals(String.class, ClassUtil.getClass(new Object[] {"Peter"})[0]);
        // Note that primitive types are always autoboxed to the corresponding object types.
        assertArrayEquals(new Class<?>[] {String.class, Double.class, Integer.class, Integer.class},
                ClassUtil.getClass(new Object[] {"X", 1.0d, 5, Integer.valueOf(5)}));
        assertArrayEquals(new Class<?>[] {String.class, Double.class, null, Integer.class},
                ClassUtil.getClass(new Object[] {"X", 1.0d, null, 5}));
    }

    /**
     * Test the toDescriptor method of the FieldSignature class.
     */
    @Test
    public void testToDescriptor()
    {
        assertEquals("I", FieldSignature.toDescriptor(int.class));
        assertEquals("D", FieldSignature.toDescriptor(double.class));
        assertEquals("Z", FieldSignature.toDescriptor(boolean.class));
        assertEquals("C", FieldSignature.toDescriptor(char.class));
        assertEquals("B", FieldSignature.toDescriptor(byte.class));
        assertEquals("F", FieldSignature.toDescriptor(float.class));
        assertEquals("J", FieldSignature.toDescriptor(long.class));
        assertEquals("S", FieldSignature.toDescriptor(short.class));
        assertEquals("[I", FieldSignature.toDescriptor(int[].class));
        // System.out.println(FieldSignature.toDescriptor(int[].class));
        assertEquals("Ljava/lang/Integer;", FieldSignature.toDescriptor(Integer.class));
    }

    /**
     * Test the toClass method of the FieldSignature class.
     * @throws ClassNotFoundException if that happens uncaught; this test has failed
     */
    @Test
    public void testToClass() throws ClassNotFoundException
    {
        assertEquals(int.class, FieldSignature.toClass("int"));
        assertEquals(int.class, FieldSignature.toClass("I"));
        assertEquals(double.class, FieldSignature.toClass("double"));
        assertEquals(double.class, FieldSignature.toClass("D"));
        assertEquals(boolean.class, FieldSignature.toClass("boolean"));
        assertEquals(boolean.class, FieldSignature.toClass("Z"));
        assertEquals(char.class, FieldSignature.toClass("C"));
        assertEquals(char.class, FieldSignature.toClass("char"));
        assertEquals(byte.class, FieldSignature.toClass("B"));
        assertEquals(byte.class, FieldSignature.toClass("byte"));
        assertEquals(float.class, FieldSignature.toClass("F"));
        assertEquals(float.class, FieldSignature.toClass("float"));
        assertEquals(long.class, FieldSignature.toClass("J"));
        assertEquals(long.class, FieldSignature.toClass("long"));
        assertEquals(short.class, FieldSignature.toClass("S"));
        assertEquals(short.class, FieldSignature.toClass("short"));
        assertEquals(int[].class, FieldSignature.toClass("[I"));
        assertEquals(int[].class, FieldSignature.toClass("[I")); // repeated call uses the cache
        assertEquals(Integer.class, FieldSignature.toClass("Ljava/lang/Integer;"));
        assertEquals(void.class, FieldSignature.toClass("void"));
        assertEquals(void.class, FieldSignature.toClass("V"));
        try
        {
            assertNull(FieldSignature.toClass("XXX"));
            fail("name of non existant class should have thrown a ClassNotFoundException");
        }
        catch (ClassNotFoundException cnfe)
        {
            // Ignore expected exception
        }
    }

    /**
     * Test the constructors and fields of the FieldSignature class.
     * @throws ClassNotFoundException if that happens uncaught; this test has failed
     */
    @Test
    public void testFieldSignature() throws ClassNotFoundException
    {
        FieldSignature fs = new FieldSignature("[J");
        assertEquals("[J", fs.toString());
        assertEquals("[J", fs.getStringValue());
        assertEquals(long[].class, fs.getClassValue());
        FieldSignature fs2 = new FieldSignature(double[].class);
        assertEquals("[D", fs2.toString());
        assertEquals("[D", fs2.getStringValue());
        assertEquals(double[].class, fs2.getClassValue());
    }

    /**
     * Test the constructors and fields of the MethodSignature class.
     * @throws SecurityException if that happens uncaught; this test has failed
     * @throws NoSuchMethodException if that happens uncaught; this test has failed
     * @throws ClassNotFoundException if that happens uncaught; this test has failed
     */
    @Test
    public void testMethodSignature() throws NoSuchMethodException, SecurityException, ClassNotFoundException
    {
        MethodSignature ms = new MethodSignature(String.class.getConstructor(String.class));
        assertEquals(String.class, ms.getReturnType());
        Class<?>[] parameterTypes = ms.getParameterTypes();
        assertEquals(1, parameterTypes.length);
        assertEquals(String.class, parameterTypes[0]);
        assertEquals("Ljava/lang/String;", ms.getParameterDescriptor());
        ms = new MethodSignature(String.class.getConstructor()); // Constructor for empty string
        assertEquals(String.class, ms.getReturnType());
        parameterTypes = ms.getParameterTypes();
        assertEquals(0, parameterTypes.length);
        ms = new MethodSignature("(I)[D");
        assertEquals(double[].class, ms.getReturnType());
        parameterTypes = ms.getParameterTypes();
        assertEquals(1, parameterTypes.length);
        assertEquals(int.class, parameterTypes[0]);
        ms = new MethodSignature(String.class.getMethod("length"));
        parameterTypes = ms.getParameterTypes();
        assertEquals(0, parameterTypes.length);
        Class<?> returnType = ms.getReturnType();
        assertEquals(int.class, returnType);
        Method[] methods = String.class.getMethods();
        // find the substring method that takes two arguments
        Method substring = null;
        for (Method m : methods)
        {
            if (m.getName().equals("substring") && m.getParameterCount() == 2)
            {
                substring = m;
            }
        }
        if (null == substring)
        {
            System.err.println("Could not find substring(int from, int to) method");
        }
        else
        {
            ms = new MethodSignature(substring);
            parameterTypes = ms.getParameterTypes();
            assertEquals(2, parameterTypes.length);
            assertEquals(int.class, parameterTypes[0]);
            assertEquals(int.class, parameterTypes[1]);
            returnType = ms.getReturnType();
            assertEquals(String.class, returnType);
            assertEquals("II", ms.getParameterDescriptor());
            assertEquals("Ljava/lang/String;", ms.getReturnDescriptor());
            assertEquals("(II)Ljava/lang/String;", ms.toString());
        }
        ms = new MethodSignature(String.class.getMethod("getBytes"));
        returnType = ms.getReturnType();
        assertEquals(byte[].class, returnType);
        ms = new MethodSignature(String.class.getConstructor(byte[].class));
        parameterTypes = ms.getParameterTypes();
        assertEquals(1, parameterTypes.length);
        assertEquals(byte[].class, parameterTypes[0]);
        assertEquals("[B", ms.getParameterDescriptor());
    }

    /**
     * Tests the ClassUtil Constructors.
     * @throws NoSuchMethodException on error
     * @throws InvocationTargetException on error
     * @throws IllegalArgumentException on error
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     */
    @Test
    public void testClassUtilConstructors() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        Constructor<TestClass> c1 = ClassUtil.resolveConstructor(TestClass.class, new Class<?>[] {});
        TestClass o1 = c1.newInstance();
        assertEquals("<init>", o1.getState());

        Constructor<TestClass> c2 = ClassUtil.resolveConstructor(TestClass.class, new Class<?>[] {String.class});
        TestClass o2 = c2.newInstance("c2");
        assertEquals("c2", o2.getState());

        Constructor<InnerPublic> c3 = ClassUtil.resolveConstructor(InnerPublic.class, new Class<?>[] {});
        InnerPublic o3 = c3.newInstance();
        assertEquals("<initInnerPublic>", o3.getInnerState());

        Constructor<InnerPublic> c4 = ClassUtil.resolveConstructor(InnerPublic.class, new Class<?>[] {String.class});
        InnerPublic o4 = c4.newInstance("inner");
        assertEquals("inner", o4.getInnerState());

        // test caching
        Constructor<InnerPublic> c4a = ClassUtil.resolveConstructor(InnerPublic.class, new Class<?>[] {String.class});
        InnerPublic o4a = c4a.newInstance("inner2");
        assertEquals("inner2", o4a.getInnerState());

        // test constructor that cannot be found
        try
        {
            ClassUtil.resolveConstructor(TestClass.class, new Class<?>[] {Integer.class});
            fail("Constructor TestClass(int) does not exist and resolving should throw an exception");
        }
        catch (NoSuchMethodException e)
        {
            // ok
        }

        // test access to public and private constructors
        ClassUtil.resolveConstructor(TestClass.class, new Class<?>[] {boolean.class});
        ClassUtil.resolveConstructor(TestClass.class, ClassUtilTest.class, new Class<?>[] {String.class});
        try
        {
            ClassUtil.resolveConstructor(TestClass.class, ClassUtilTest.class, new Class<?>[] {boolean.class});
            fail("Constructor TestClass(boolean) is private and resolving should throw an exception");
        }
        catch (IllegalAccessException e)
        {
            // ok
        }

        assertEquals(3, ClassUtil.getAllConstructors(TestClass.class).length);
    }

    /**
     * Tests the ClassUtil Constructor lookup methods.
     * @throws NoSuchMethodException on error
     * @throws InvocationTargetException on error
     * @throws IllegalArgumentException on error
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     */
    @Test
    public void testClassUtilConstructor() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException
    {
        Constructor<Sup>[] cArr = ClassUtil.getAllConstructors(Sup.class);
        assertEquals(2, cArr.length);
        // retrieve again from cache
        cArr = ClassUtil.getAllConstructors(Sup.class);
        assertEquals(2, cArr.length);
        Constructor<Sup> c1 = ClassUtil.resolveConstructor(Sup.class, new Class<?>[] {String.class, int.class});
        assertNotNull(c1);
        // retrieve again from cache
        Constructor<Sup> c1a = ClassUtil.resolveConstructor(Sup.class, new Class<?>[] {String.class, int.class});
        assertEquals(c1, c1a);
        Constructor<Sup> c2 = ClassUtil.resolveConstructor(Sup.class, new Object[] {"abc", 1, 2.0d});
        assertNotNull(c2);
        // retrieve again from cache
        Constructor<Sup> c2a = ClassUtil.resolveConstructor(Sup.class, new Object[] {"abc", 1, 2.0d});
        assertEquals(c2, c2a);
        Constructor<Sub> c2b = ClassUtil.resolveConstructor(Sub.class, new Object[] {"abc", 1, 2.0d});
        assertNotEquals(c2a, c2b);
        assertTrue(ClassUtil.isMoreSpecific(c2b, c2a));
        // XXX: look carefully at isMoreSpecific: assertFalse(ClassUtil.isMoreSpecific(c2a, c2b));
        assertFalse(ClassUtil.isMoreSpecific(c2a, c1));
        assertFalse(ClassUtil.isMoreSpecific(c1, c2a));
        assertFalse(ClassUtil.isMoreSpecific(c1, c2b));

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Constructor<Sup> cx = ClassUtil.resolveConstructor(Sup.class, new Class<?>[] {String.class, float.class});
                fail("illegal retrieval of constructor " + cx.toString());
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Constructor<Sup> cx = ClassUtil.resolveConstructor(Sup.class, new Object[] {1.2f});
                fail("illegal retrieval of constructor " + cx.toString());
            }
        });

        Sub sup = new Sub("a", 1);
        Constructor<Sub.Inner> csi2 = ClassUtil.resolveConstructor(Sub.Inner.class, new Object[] {sup, 123L});
        assertNotNull(csi2);
    }

    /**
     * Tests the ClassUtil Field lookup methods.
     * @throws NoSuchFieldException on error
     */
    @Test
    public void testClassUtilField() throws NoSuchFieldException
    {
        Set<Field> fSet = ClassUtil.getAllFields(Sup.class);
        removeJacoco(fSet);
        assertEquals(7, fSet.size());

        testField(Sup.class, "staticFinalString", Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC);
        testField(Sup.class, "finalString", Modifier.FINAL, Modifier.PUBLIC);
        testField(Sup.class, "publicInt", Modifier.PUBLIC);
        testField(Sup.class, "protectedLong", Modifier.PROTECTED);
        testField(Sup.class, "packageDouble");
        testField(Sup.class, "privateFloat", Modifier.PRIVATE);
        testField(Sup.class, "publicSubInt", Modifier.PUBLIC);

        testField(Sub.class, "staticFinalString", Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC);
        testField(Sub.class, "finalString", Modifier.FINAL, Modifier.PUBLIC);
        testField(Sub.class, "publicInt", Modifier.PUBLIC);
        testField(Sub.class, "protectedLong", Modifier.PROTECTED);
        testField(Sub.class, "packageDouble");
        testField(Sub.class, "privateFloat", Modifier.PRIVATE);
        testField(Sub.class, "publicSuperInt", Modifier.PUBLIC);
        testField(Sub.class, "publicSubInt", Modifier.PUBLIC);

        Sub sup = new Sub("a", 1);
        testField(sup, "staticFinalString", Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC);
        testField(sup, "finalString", Modifier.FINAL, Modifier.PUBLIC);
        testField(sup, "publicInt", Modifier.PUBLIC);
        testField(sup, "protectedLong", Modifier.PROTECTED);
        testField(sup, "packageDouble");
        testField(sup, "privateFloat", Modifier.PRIVATE);
        testField(sup, "publicSuperInt", Modifier.PUBLIC);
        testField(sup, "publicSubInt", Modifier.PUBLIC);

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testField(sup, "xyz", Modifier.PUBLIC);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testField(Sub.class, "xyz", Modifier.PUBLIC);
            }
        });
        Field f1 = ClassUtil.resolveField(Sub.class, this.getClass(), "publicInt");
        assertNotNull(f1);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Field f2 = ClassUtil.resolveField(Sub.class, this.getClass(), "privateFloat");
                fail("should not be able to resolve private field " + f2);
            }
        });

        Sub.Inner supInn = new Sub("a", 1).new Inner(12L);
        testField(supInn, "publicInnerLong", Modifier.PUBLIC);
        testField(supInn, "staticFinalString", Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC);
        testField(supInn, "finalString", Modifier.FINAL, Modifier.PUBLIC);
        testField(supInn, "publicInt", Modifier.PUBLIC);
        testField(supInn, "protectedLong", Modifier.PROTECTED);
        testField(supInn, "packageDouble");
        testField(supInn, "privateFloat", Modifier.PRIVATE);
        testField(supInn, "publicSuperInt", Modifier.PUBLIC);
        testField(supInn, "publicSubInt", Modifier.PUBLIC);

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Field f3 = ClassUtil.resolveField((Class<?>) null, "publicSubInt");
                fail("should not be able to resolve field from null object" + f3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Field f3 = ClassUtil.resolveField(Sub.class, null);
                fail("should not be able to resolve field from null object" + f3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Field f3 = ClassUtil.resolveField((Object) null, "publicSubInt");
                fail("should not be able to resolve field from null object" + f3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Field f3 = ClassUtil.resolveField(supInn, null);
                fail("should not be able to resolve field from null object" + f3);
            }
        });
    }

    /**
     * Tests the ClassUtil Method lookup methods.
     * @throws NoSuchMethodException on error
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testClassUtilMethod() throws NoSuchMethodException
    {
        List<Method> mList0 = ClassUtil.getAllMethods(Object.class);
        removeJacoco(mList0);
        List<Method> mList1 = ClassUtil.getAllMethods(Sup.class);
        removeJacoco(mList1);
        // 12 methods in Sub
        assertEquals(mList0.size() + 12, mList1.size());
        List<Method> mList2 = ClassUtil.getAllMethods(Sup.class, "privateVoid");
        assertEquals(1, mList2.size());

        testMethod(Sup.class, "publicVoid", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(Sup.class, "publicInt", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(Sup.class, "publicArgs", new Class<?>[] {String.class, double.class}, Modifier.PUBLIC);
        testMethod(Sup.class, "protectedVoid", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(Sup.class, "protectedInt", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(Sup.class, "protectedArgs", new Class<?>[] {String.class, double.class}, Modifier.PROTECTED);
        testMethod(Sup.class, "packageVoid", new Class<?>[] {});
        testMethod(Sup.class, "packageInt", new Class<?>[] {});
        testMethod(Sup.class, "packageArgs", new Class<?>[] {String.class, double.class});
        testMethod(Sup.class, "privateVoid", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(Sup.class, "privateInt", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(Sup.class, "privateArgs", new Class<?>[] {String.class, double.class}, Modifier.PRIVATE);

        testMethod(Sub.class, "publicVoid", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(Sub.class, "publicInt", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(Sub.class, "publicArgs", new Class<?>[] {String.class, double.class}, Modifier.PUBLIC);
        testMethod(Sub.class, "publicArgs", new Class<?>[] {String.class}, Modifier.PUBLIC);
        testMethod(Sub.class, "protectedVoid", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(Sub.class, "protectedInt", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(Sub.class, "protectedArgs", new Class<?>[] {String.class, double.class}, Modifier.PROTECTED);
        testMethod(Sub.class, "packageVoid", new Class<?>[] {});
        testMethod(Sub.class, "packageInt", new Class<?>[] {});
        testMethod(Sub.class, "packageArgs", new Class<?>[] {String.class, double.class});
        testMethod(Sub.class, "privateVoid", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(Sub.class, "privateInt", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(Sub.class, "privateArgs", new Class<?>[] {String.class, double.class}, Modifier.PRIVATE);

        Sub sup = new Sub("a", 1);
        testMethod(sup, "publicVoid", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(sup, "publicInt", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(sup, "publicArgs", new Class<?>[] {String.class, double.class}, Modifier.PUBLIC);
        testMethod(sup, "publicArgs", new Class<?>[] {String.class}, Modifier.PUBLIC);
        testMethod(sup, "protectedVoid", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(sup, "protectedInt", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(sup, "protectedArgs", new Class<?>[] {String.class, double.class}, Modifier.PROTECTED);
        testMethod(sup, "packageVoid", new Class<?>[] {});
        testMethod(sup, "packageInt", new Class<?>[] {});
        testMethod(sup, "packageArgs", new Class<?>[] {String.class, double.class});
        testMethod(sup, "privateVoid", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(sup, "privateInt", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(sup, "privateArgs", new Class<?>[] {String.class, double.class}, Modifier.PRIVATE);

        testMethod(sup, "publicVoid", new Object[] {}, Modifier.PUBLIC);
        testMethod(sup, "publicInt", new Object[] {}, Modifier.PUBLIC);
        testMethod(sup, "publicArgs", new Object[] {"abc", 12.0d}, Modifier.PUBLIC);
        testMethod(sup, "publicArgs", new Object[] {"def"}, Modifier.PUBLIC);
        testMethod(sup, "protectedVoid", new Object[] {}, Modifier.PROTECTED);
        testMethod(sup, "protectedInt", new Object[] {}, Modifier.PROTECTED);
        testMethod(sup, "protectedArgs", new Object[] {"ghi", 13.0d}, Modifier.PROTECTED);
        testMethod(sup, "packageVoid", new Object[] {});
        testMethod(sup, "packageInt", new Object[] {});
        testMethod(sup, "packageArgs", new Object[] {"jkl", 14.0d});
        testMethod(sup, "privateVoid", new Object[] {}, Modifier.PRIVATE);
        testMethod(sup, "privateInt", new Object[] {}, Modifier.PRIVATE);
        testMethod(sup, "privateArgs", new Object[] {"mno", 15.0d}, Modifier.PRIVATE);

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testMethod(sup, "xyz", new Class<?>[] {}, Modifier.PUBLIC);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testMethod(sup, "publicArgs", new Class<?>[] {float.class}, Modifier.PUBLIC);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testMethod(sup, "xyz", new Object[] {}, Modifier.PUBLIC);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testMethod(sup, "publicArgs", new Object[] {12L}, Modifier.PUBLIC);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testMethod(Sub.class, "xyz", new Class<?>[] {}, Modifier.PUBLIC);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                testMethod(Sub.class, "publicArgs", new Class<?>[] {float.class}, Modifier.PUBLIC);
            }
        });

        Method m1 = ClassUtil.resolveMethod(Sub.class, this.getClass(), "publicArgs", new Class<?>[] {String.class});
        assertNotNull(m1);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m2 = ClassUtil.resolveMethod(Sub.class, this.getClass(), "privateArgs", new Class<?>[] {});
                fail("should not be able to resolve private field " + m2);
            }
        });

        Sub.Inner supInn = new Sub("a", 1).new Inner(12L);
        testMethod(supInn, "publicInnerMethod", new Class<?>[] {long.class}, Modifier.PUBLIC);
        testMethod(supInn, "publicVoid", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(supInn, "publicInt", new Class<?>[] {}, Modifier.PUBLIC);
        testMethod(supInn, "publicArgs", new Class<?>[] {String.class, double.class}, Modifier.PUBLIC);
        testMethod(supInn, "publicArgs", new Class<?>[] {String.class}, Modifier.PUBLIC);
        testMethod(supInn, "protectedVoid", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(supInn, "protectedInt", new Class<?>[] {}, Modifier.PROTECTED);
        testMethod(supInn, "protectedArgs", new Class<?>[] {String.class, double.class}, Modifier.PROTECTED);
        testMethod(supInn, "packageVoid", new Class<?>[] {});
        testMethod(supInn, "packageInt", new Class<?>[] {});
        testMethod(supInn, "packageArgs", new Class<?>[] {String.class, double.class});
        testMethod(supInn, "privateVoid", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(supInn, "privateInt", new Class<?>[] {}, Modifier.PRIVATE);
        testMethod(supInn, "privateArgs", new Class<?>[] {String.class, double.class}, Modifier.PRIVATE);

        testMethod(supInn, "publicInnerMethod", new Object[] {12L}, Modifier.PUBLIC);
        testMethod(supInn, "publicVoid", new Object[] {}, Modifier.PUBLIC);
        testMethod(supInn, "publicInt", new Object[] {}, Modifier.PUBLIC);
        testMethod(supInn, "publicArgs", new Object[] {"abc", 12.0d}, Modifier.PUBLIC);
        testMethod(supInn, "publicArgs", new Object[] {"def"}, Modifier.PUBLIC);
        testMethod(supInn, "protectedVoid", new Object[] {}, Modifier.PROTECTED);
        testMethod(supInn, "protectedInt", new Object[] {}, Modifier.PROTECTED);
        testMethod(supInn, "protectedArgs", new Object[] {"ghi", 13.0d}, Modifier.PROTECTED);
        testMethod(supInn, "packageVoid", new Object[] {});
        testMethod(supInn, "packageInt", new Object[] {});
        testMethod(supInn, "packageArgs", new Object[] {"jkl", 14.0d});
        testMethod(supInn, "privateVoid", new Object[] {}, Modifier.PRIVATE);
        testMethod(supInn, "privateInt", new Object[] {}, Modifier.PRIVATE);
        testMethod(supInn, "privateArgs", new Object[] {"mno", 15.0d}, Modifier.PRIVATE);

        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m3 = ClassUtil.resolveMethod((Class<?>) null, "publicInt", new Class<?>[] {});
                fail("should not be able to resolve field from null object" + m3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m3 = ClassUtil.resolveMethod(Sub.class, null, new Class<?>[] {});
                fail("should not be able to resolve field from null object" + m3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m3 = ClassUtil.resolveMethod((Object) null, "publicInt", new Class<?>[] {});
                fail("should not be able to resolve field from null object" + m3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m3 = ClassUtil.resolveMethod(supInn, null, new Class<?>[] {});
                fail("should not be able to resolve field from null object" + m3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m3 = ClassUtil.resolveMethod((Object) null, "publicInt", new Object[] {});
                fail("should not be able to resolve field from null object" + m3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Method m3 = ClassUtil.resolveMethod(supInn, null, new Object[] {});
                fail("should not be able to resolve field from null object" + m3);
            }
        });
    }

    /**
     * Tests the ClassUtil Annotation lookup methods.
     */
    @Test
    public void testClassUtilAnnotation()
    {
        Set<Annotation> aSet0 = ClassUtil.getAllAnnotations(Sub.class);
        assertEquals(2, aSet0.size());

        Annotation a1 = ClassUtil.resolveAnnotation(Sub.class, AnnTag.class);
        assertNotNull(a1);
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Annotation a3 = ClassUtil.resolveAnnotation(Sup.class, AnnTag.class);
                fail("should not be able to resolve non-existing annotation " + a3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Annotation a3 = ClassUtil.resolveAnnotation(null, AnnTag.class);
                fail("should not be able to resolve non-existing annotation " + a3);
            }
        });
        UnitTest.testFail(new UnitTest.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                Annotation a3 = ClassUtil.resolveAnnotation(Sub.class, null);
                fail("should not be able to resolve non-existing annotation " + a3);
            }
        });

        Annotation a4 = ClassUtil.resolveAnnotation(Sub.Inner.class, AnnTag.class);
        assertNotNull(a4);

        AnnString a5 = (AnnString) ClassUtil.resolveAnnotation(Sub.class, AnnString.class);
        assertNotNull(a5);
        assertEquals("avalue", a5.value());
    }

    /**
     * Clean up the fields to remove jacoco / debugger artifacts.
     * @param fields the set to clean
     */
    private void removeJacoco(final Set<Field> fields)
    {
        for (Iterator<Field> it = fields.iterator(); it.hasNext();)
        {
            Field field = it.next();
            if (field.getName().contains("jacoco"))
            {
                it.remove();
            }
        }
    }

    /**
     * @param clazz the class to test
     * @param fieldName the field to retrieve
     * @param modifiers the expected modifiers
     * @throws NoSuchFieldException on error
     */
    protected void testField(final Class<?> clazz, final String fieldName, final int... modifiers) throws NoSuchFieldException
    {
        Field field = ClassUtil.resolveField(clazz, fieldName);
        assertNotNull(field);
        assertEquals(fieldName, field.getName());
        Set<Integer> mSet = new HashSet<>();
        for (int m : modifiers)
        {
            assertTrue((field.getModifiers() & m) != 0,
                    "failed modifier for field " + clazz.getSimpleName() + "." + fieldName + " for modifier " + m);
            mSet.add(m);
        }
        for (int p = 0; p < 12; p++)
        {
            int m = 1 << p;
            if (!mSet.contains(m))
            {
                assertTrue((field.getModifiers() & m) == 0,
                        "failed modifier for field " + clazz.getSimpleName() + "." + fieldName + " for bit " + p);
            }
        }
    }

    /**
     * @param object the object to test
     * @param fieldName the field to retrieve
     * @param modifiers the expected modifiers
     * @throws NoSuchFieldException on error
     */
    protected void testField(final Object object, final String fieldName, final int... modifiers) throws NoSuchFieldException
    {
        Field field = ClassUtil.resolveField(object, fieldName);
        assertNotNull(field);
        assertEquals(fieldName, field.getName());
        Set<Integer> mSet = new HashSet<>();
        for (int m : modifiers)
        {
            assertTrue((field.getModifiers() & m) != 0,
                    "failed modifier for field " + object.getClass().getSimpleName() + "." + fieldName + " for modifier " + m);
            mSet.add(m);
        }
        for (int p = 0; p < 12; p++)
        {
            int m = 1 << p;
            if (!mSet.contains(m))
            {
                assertTrue((field.getModifiers() & m) == 0,
                        "failed modifier for field " + object.getClass().getSimpleName() + "." + fieldName + " for bit " + p);
            }
        }
    }

    /**
     * Clean up the fields to remove jacoco / debugger artifacts.
     * @param methods the set to clean
     */
    private void removeJacoco(final List<Method> methods)
    {
        for (Iterator<Method> it = methods.iterator(); it.hasNext();)
        {
            Method method = it.next();
            if (method.getName().contains("jacoco"))
            {
                it.remove();
            }
        }
    }

    /**
     * @param clazz the class to test
     * @param methodName the method to retrieve
     * @param params the parameters of the method as a class array
     * @param modifiers the expected modifiers
     * @throws NoSuchMethodException on error
     */
    protected void testMethod(final Class<?> clazz, final String methodName, final Class<?>[] params, final int... modifiers)
            throws NoSuchMethodException
    {
        Method method = ClassUtil.resolveMethod(clazz, methodName, params);
        assertNotNull(method);
        assertEquals(methodName, method.getName());
        Set<Integer> mSet = new HashSet<>();
        for (int m : modifiers)
        {
            assertTrue((method.getModifiers() & m) != 0,
                    "failed modifier for method " + clazz.getSimpleName() + "." + methodName + " for modifier " + m);
            mSet.add(m);
        }
        for (int p = 0; p < 12; p++)
        {
            int m = 1 << p;
            if (!mSet.contains(m))
            {
                assertTrue((method.getModifiers() & m) == 0,
                        "failed modifier for method " + clazz.getSimpleName() + "." + methodName + " for bit " + p);
            }
        }
    }

    /**
     * @param object the object to test
     * @param methodName the method to retrieve
     * @param params the parameters of the method as a class array
     * @param modifiers the expected modifiers
     * @throws NoSuchMethodException on error
     */
    protected void testMethod(final Object object, final String methodName, final Class<?>[] params, final int... modifiers)
            throws NoSuchMethodException
    {
        Method method = ClassUtil.resolveMethod(object, methodName, params);
        assertNotNull(method);
        assertEquals(methodName, method.getName());
        Set<Integer> mSet = new HashSet<>();
        for (int m : modifiers)
        {
            assertTrue((method.getModifiers() & m) != 0, "failed modifier for method " + object.getClass().getSimpleName() + "."
                    + methodName + " for modifier " + m);
            mSet.add(m);
        }
        for (int p = 0; p < 12; p++)
        {
            int m = 1 << p;
            if (!mSet.contains(m))
            {
                assertTrue((method.getModifiers() & m) == 0,
                        "failed modifier for method " + object.getClass().getSimpleName() + "." + methodName + " for bit " + p);
            }
        }
    }

    /**
     * @param object the object to test
     * @param methodName the method to retrieve
     * @param args the parameters of the method as an object array
     * @param modifiers the expected modifiers
     * @throws NoSuchMethodException on error
     */
    protected void testMethod(final Object object, final String methodName, final Object[] args, final int... modifiers)
            throws NoSuchMethodException
    {
        Method method = ClassUtil.resolveMethod(object, methodName, args);
        assertNotNull(method);
        assertEquals(methodName, method.getName());
        Set<Integer> mSet = new HashSet<>();
        for (int m : modifiers)
        {
            assertTrue((method.getModifiers() & m) != 0, "failed modifier for method " + object.getClass().getSimpleName() + "."
                    + methodName + " for modifier " + m);
            mSet.add(m);
        }
        for (int p = 0; p < 12; p++)
        {
            int m = 1 << p;
            if (!mSet.contains(m))
            {
                assertTrue((method.getModifiers() & m) == 0,
                        "failed modifier for method " + object.getClass().getSimpleName() + "." + methodName + " for bit " + p);
            }
        }
    }

    /** subclass with fields and methods. */
    @SuppressWarnings("unused")
    protected static class Sup
    {
        /** */
        @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:constantname"})
        public static final String staticFinalString = "ABC";

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        public final String finalString = "DEF";

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        private float privateFloat = 8.0f;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        double packageDouble = 4.0d;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        protected long protectedLong = 2L;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        public int publicInt = 1;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        public int publicSubInt = 21;

        /**
         * @param s String
         * @param i int
         * @param d double
         */
        public Sup(final String s, final int i, final double d)
        {
            //
        }

        /**
         * @param s String
         * @param i int
         */
        public Sup(final String s, final int i)
        {
            //
        }

        /** */
        private void privateVoid()
        {
            //
        }

        /** */
        void packageVoid()
        {
            //
        }

        /** */
        protected void protectedVoid()
        {
            //
        }

        /** */
        public void publicVoid()
        {
            //
        }

        /**
         * @return int
         */
        private int privateInt()
        {
            return 1;
        }

        /**
         * @return int
         */
        int packageInt()
        {
            return 2;
        }

        /**
         * @return int
         */
        protected int protectedInt()
        {
            return 3;
        }

        /**
         * @return int
         */
        public int publicInt()
        {
            return 4;
        }

        /**
         * @param s String
         * @param d double
         */
        private void privateArgs(final String s, final double d)
        {
            //
        }

        /**
         * @param s String
         * @param d double
         */
        void packageArgs(final String s, final double d)
        {
            //
        }

        /**
         * @param s String
         * @param d double
         */
        protected void protectedArgs(final String s, final double d)
        {
            //
        }

        /**
         * @param s String
         * @param d double
         */
        public void publicArgs(final String s, final double d)
        {
            //
        }
    }

    /** superclass with fields and methods. */
    @SuppressWarnings({"hiding", "unused"})
    @AnnTag
    @AnnString("avalue")
    protected static class Sub extends Sup
    {
        /** */
        @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:constantname"})
        public static final String staticFinalString = "ABC";

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        public final String finalString = "DEF";

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        private float privateFloat = 8.0f;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        double packageDouble = 4.0d;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        protected long protectedLong = 2L;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        public int publicInt = 1;

        /** */
        @SuppressWarnings("checkstyle:visibilitymodifier")
        public int publicSuperInt = 11;

        /**
         * @param s String
         * @param i int
         * @param d double
         */
        public Sub(final String s, final int i, final double d)
        {
            super(s, i, d);
        }

        /**
         * @param s String
         * @param i int
         */
        public Sub(final String s, final int i)
        {
            super(s, i);
        }

        /** */
        private void privateVoid()
        {
            //
        }

        @Override
        void packageVoid()
        {
            //
        }

        @Override
        protected void protectedVoid()
        {
            //
        }

        @Override
        public void publicVoid()
        {
            //
        }

        /**
         * @return int
         */
        private int privateInt()
        {
            return 1;
        }

        @Override
        int packageInt()
        {
            return 2;
        }

        @Override
        protected int protectedInt()
        {
            return 3;
        }

        @Override
        public int publicInt()
        {
            return 4;
        }

        /**
         * @param s string
         * @param d double
         */
        private void privateArgs(final String s, final double d)
        {
            //
        }

        @Override
        void packageArgs(final String s, final double d)
        {
            //
        }

        @Override
        protected void protectedArgs(final String s, final double d)
        {
            //
        }

        @Override
        public void publicArgs(final String s, final double d)
        {
            //
        }

        /**
         * @param s String
         */
        public void publicArgs(final String s)
        {
            //
        }

        /** non-static inner class. */
        @AnnTag
        public class Inner
        {
            /**
             * @param l long
             */
            Inner(final long l)
            {
                //
            }

            /** */
            @SuppressWarnings("checkstyle:visibilitymodifier")
            public long publicInnerLong = 123L;

            /**
             * @param l long
             */
            public void publicInnerMethod(final long l)
            {
                //
            }
        }
    }

    /** annotation class. */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnTag
    {
        // tagging annotation
    }

    /** annotation class with String parameter. */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnString
    {
        /** @return the value */
        String value();
    }

}
