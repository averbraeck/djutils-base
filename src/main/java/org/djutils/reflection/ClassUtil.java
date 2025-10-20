package org.djutils.reflection;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.djutils.io.ResourceResolver;
import org.djutils.primitives.Primitive;

/**
 * ClassUtil is a utility class providing assistance for Java Classes.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author Peter Jacobs, Niels Lang, Alexander Verbraeck
 */
public final class ClassUtil
{
    /** CACHE reflects the internal repository CACHE. */
    private static final Map<String, Object> CACHE = Collections.synchronizedMap(new LinkedHashMap<String, Object>());

    /**
     * Do not instantiate.
     */
    private ClassUtil()
    {
        // Do not instantiate
    }

    /** ************ CONSTRUCTOR UTILITIES *********** */

    /**
     * Returns all the constructors of a class. Public, package, protected and private constructors are returned. This method
     * returns an array of length 0 if the class object represents an interface, a primitive type, an array class, or void. Note
     * that the constructors of the superclass are not returned as these can never be invoked.
     * @param clazz the class to resolve the constructors for.
     * @return an array with all constructors
     * @param <T> the class of the constructors
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] getAllConstructors(final Class<T> clazz)
    {
        return (Constructor<T>[]) clazz.getDeclaredConstructors();
    }

    /**
     * Returns a constructor with the given signature, possibly from the cache. When the constructor is resolved for the first
     * time, it will be added to the cache.
     * @param clazz the class to resolve the constructor for
     * @param parameterTypes the parameter types of the signature
     * @return the constructor with the given signature, if found
     * @throws NoSuchMethodException if the constructor cannot be resolved
     * @param <T> the class of the constructor
     */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> resolveConstructorCache(final Class<T> clazz, final Class<?>[] parameterTypes)
            throws NoSuchMethodException
    {
        String key = "CONSTRUCTOR:" + clazz + "@" + FieldSignature.toDescriptor(parameterTypes);
        if (CACHE.containsKey(key))
        {
            return (Constructor<T>) CACHE.get(key);
        }
        Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
        CACHE.put(key, constructor);
        return constructor;
    }

    /**
     * Returns the constructor of a class with a particular signature if and only if the caller class can invoke that
     * constructor. So a private constructor will not be invoked, unless the caller class is the class that defines the
     * constructor. For a protected class the superclass and classes in the same package can invoke the constructor, but other
     * classes should not be able to invoke the constructor.
     * @param clazz the class for which the constructor needs to be
     * @param callerClass the calling class for which the test is carried out whether the constructor is visible
     * @param parameterTypes the parameter types for the constructor's signature
     * @return the retrieved constructor
     * @throws NoSuchMethodException if the constructor with the given signature does not exist
     * @throws IllegalAccessException if the constructor exists but is not callable from the callerClass
     * @param <T> the class of the constructor
     */
    public static <T> Constructor<T> resolveConstructor(final Class<T> clazz, final Class<?> callerClass,
            final Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException
    {
        Constructor<T> constructor = ClassUtil.resolveConstructor(clazz, parameterTypes);
        if (ClassUtil.isVisible(constructor, callerClass.getClass()))
        {
            return constructor;
        }
        throw new IllegalAccessException("constructor resolved but not visible");
    }

    /**
     * returns the interface method.
     * @param clazz the class to start with
     * @param parameterTypes the parameterTypes
     * @return Constructor
     * @throws NoSuchMethodException if the method cannot be resolved
     * @param <T> the class of the constructor
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> resolveConstructor(final Class<T> clazz, final Class<?>[] parameterTypes)
            throws NoSuchMethodException
    {
        try
        {
            return resolveConstructorCache(clazz, (Class<?>[]) ClassUtil.checkInput(parameterTypes, Class.class));
        }
        catch (Exception exception)
        {
            String className = clazz.getName();
            if (className.indexOf("$") >= 0)
            {
                Class<?> parentClass = null;
                try
                {
                    parentClass = Class.forName(className.substring(0, className.lastIndexOf("$")));
                }
                catch (Exception e2)
                {
                    throw new NoSuchMethodException("class " + parentClass + " not found to resolve constructor");
                }
                return (Constructor<T>) ClassUtil.resolveConstructor(parentClass,
                        (Class<?>[]) ClassUtil.checkInput(parameterTypes, Class.class));
            }
            throw new NoSuchMethodException("class " + clazz + " does not contain constructor");
        }
    }

    /**
     * returns the constructor.
     * @param clazz the clazz to start with
     * @param arguments the arguments
     * @return Constructor
     * @throws NoSuchMethodException on lookup failure
     * @param <T> the class of the constructor
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> resolveConstructor(final Class<T> clazz, final Object[] arguments)
            throws NoSuchMethodException
    {
        Class<?>[] parameterTypes = ClassUtil.getClass(arguments);
        String key = "CONSTRUCTOR:" + clazz + "@" + FieldSignature.toDescriptor(parameterTypes);
        if (CACHE.containsKey(key))
        {
            return (Constructor<T>) CACHE.get(key);
        }
        try
        {
            return ClassUtil.resolveConstructor(clazz, parameterTypes);
        }
        catch (NoSuchMethodException noSuchMethodException)
        {
            // We get all constructors
            Constructor<T>[] constructors = ClassUtil.getAllConstructors(clazz);
            // now we match the signatures
            constructors = ClassUtil.matchSignature(constructors, parameterTypes);
            // Now we find the most specific
            Constructor<T> result = (Constructor<T>) ClassUtil.getSpecificConstructor(constructors);
            CACHE.put(key, result);
            return result;
        }
    }

    /**
     * Filters an array methods for signatures that are compatible with a given signature.
     * @param constructor which are constructors to be filtered.
     * @param argTypes are the constructor's argument types
     * @return boolean if methodParameters assignable from argTypes
     */
    public static boolean matchSignature(final Constructor<?> constructor, final Class<?>[] argTypes)
    {
        if (constructor.getParameterTypes().length != argTypes.length)
        {
            return false;
        }
        Class<?>[] types = constructor.getParameterTypes();
        for (int i = 0; i < constructor.getParameterTypes().length; i++)
        {
            if (!(types[i].isAssignableFrom(argTypes[i]) || types[i].equals(Primitive.getPrimitive(argTypes[i]))))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Filters an array methods for signatures that are compatible with a given signature.
     * @param constructors which are constructors to be filtered.
     * @param argTypes are the constructor's argument types
     * @return Constructor&lt;?&gt;[] An unordered Constructor-array consisting of the elements of 'constructors' that match
     *         with the given signature. An array with 0 elements is returned when no matching Method objects are found.
     * @param <T> the class of the constructor
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] matchSignature(final Constructor<T>[] constructors, final Class<?>[] argTypes)
    {
        List<Constructor<T>> results = new ArrayList<Constructor<T>>();
        for (int i = 0; i < constructors.length; i++)
        {
            if (ClassUtil.matchSignature(constructors[i], argTypes))
            {
                results.add(constructors[i]);
            }
        }
        return results.toArray(new Constructor[results.size()]);
    }

    /* ************ FIELD UTILITIES *********** */

    /**
     * gets all the fields of a class (public, protected, package, and private) and adds the result to the return value.
     * @param clazz the class
     * @param result the resulting set
     * @return the set of fields including all fields of the field clazz
     */
    public static Set<Field> getAllFields(final Class<?> clazz, final Set<Field> result)
    {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            result.add(fields[i]);
        }
        if (clazz.getSuperclass() != null)
        {
            return ClassUtil.getAllFields(clazz.getSuperclass(), result);
        }
        return result;
    }

    /**
     * gets all the fields of a class (public, protected, package, and private).
     * @param clazz the class
     * @return all fields of the class
     */
    public static Set<Field> getAllFields(final Class<?> clazz)
    {
        Set<Field> fieldSet = new LinkedHashSet<Field>();
        return ClassUtil.getAllFields(clazz, fieldSet);
    }

    /**
     * resolves the field for a class, taking into account inner classes.
     * @param clazz the class to resolve the field for, including inner classes
     * @param fieldName name of the field
     * @return Field the field
     * @throws NoSuchFieldException on no such field
     */

    public static Field resolveField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException
    {
        try
        {
            return resolveFieldSuper(clazz, fieldName);
        }
        catch (NoSuchFieldException noSuchFieldException)
        {
            String className = clazz.getName();
            if (className.indexOf("$") >= 0)
            {
                Class<?> clazz2 = null;
                try
                {
                    clazz2 = Class.forName(className.substring(0, className.lastIndexOf("$")));
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    throw new NoSuchFieldException("class " + clazz + " not found to resolve field " + fieldName);
                }
                return ClassUtil.resolveField(clazz2, fieldName);
            }
            throw new NoSuchFieldException("class " + clazz + " does not contain field " + fieldName);
        }
    }

    /**
     * returns the field.
     * @param clazz the class to start with
     * @param callerClass the calling class
     * @param name the fieldName
     * @return Constructor
     * @throws NoSuchFieldException if the method cannot be resolved
     */
    public static Field resolveField(final Class<?> clazz, final Class<?> callerClass, final String name)
            throws NoSuchFieldException
    {
        Field field = ClassUtil.resolveField(clazz, name);
        if (ClassUtil.isVisible(field, callerClass.getClass()))
        {
            return field;
        }
        throw new NoSuchFieldException("field resolved but not visible");
    }

    /**
     * resolves the field for a given object instance.
     * @param object the object to resolve the field for
     * @param fieldName name of the field to resolve
     * @return the field (if found)
     * @throws NoSuchFieldException if the field cannot be resolved
     */
    public static Field resolveField(final Object object, final String fieldName) throws NoSuchFieldException
    {
        if (object == null)
        {
            throw new NoSuchFieldException("resolveField: object is null for field " + fieldName);
        }
        return resolveField(object.getClass(), fieldName);
    }

    /** ************ METHOD UTILITIES *********** */

    /**
     * gets all the methods of a class (public, protected, package, and private) and adds the result to the return value.
     * @param clazz the class
     * @param result the resulting set
     * @return the set of methods including all methods of the field clazz
     */
    public static List<Method> getAllMethods(final Class<?> clazz, final List<Method> result)
    {
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            result.add(methods[i]);
        }
        if (clazz.getSuperclass() != null)
        {
            return ClassUtil.getAllMethods(clazz.getSuperclass(), result);
        }
        return result;
    }

    /**
     * gets all the methods of a class (public, protected, package, and private).
     * @param clazz the class
     * @return all methods of the class
     */
    public static List<Method> getAllMethods(final Class<?> clazz)
    {
        List<Method> methodSet = new ArrayList<Method>();
        return ClassUtil.getAllMethods(clazz, methodSet);
    }

    /**
     * gets all the methods of a class (public, protected, package, and private) with a certain name and adds the result to the
     * return value.
     * @param clazz the class
     * @param name the name of the method to look up
     * @param result the resulting set
     * @return the set of methods including all methods with the given name of the field clazz
     */
    public static List<Method> getAllMethods(final Class<?> clazz, final String name, final List<Method> result)
    {
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
            {
                result.add(methods[i]);
            }
        }
        if (clazz.getSuperclass() != null)
        {
            return ClassUtil.getAllMethods(clazz.getSuperclass(), name, result);
        }
        return result;
    }

    /**
     * gets all the methods of a class (public, protected, package, and private) with a certain name.
     * @param clazz the class
     * @param name the name of the method to look up
     * @return all methods of the class with a certain name
     */
    public static List<Method> getAllMethods(final Class<?> clazz, final String name)
    {
        List<Method> methodSet = new ArrayList<Method>();
        return ClassUtil.getAllMethods(clazz, name, methodSet);
    }

    /**
     * returns the interface method.
     * @param clazz the class to start with
     * @param callerClass the caller class
     * @param name the name of the method
     * @param parameterTypes the parameterTypes
     * @return Method
     * @throws NoSuchMethodException on lookup failure
     */
    public static Method resolveMethod(final Class<?> clazz, final Class<?> callerClass, final String name,
            final Class<?>[] parameterTypes) throws NoSuchMethodException
    {
        Method method = ClassUtil.resolveMethod(clazz, name, parameterTypes);
        if (ClassUtil.isVisible(method, callerClass))
        {
            return method;
        }
        throw new NoSuchMethodException("method found but not visible");
    }

    /**
     * returns the interface method.
     * @param clazz the class to start with
     * @param name the name of the method
     * @param parameterTypes the parameterTypes
     * @return Method
     * @throws NoSuchMethodException on lookup failure
     */
    public static Method resolveMethod(final Class<?> clazz, final String name, final Class<?>[] parameterTypes)
            throws NoSuchMethodException
    {
        try
        {
            return resolveMethodSuper(clazz, name, (Class<?>[]) ClassUtil.checkInput(parameterTypes, Class.class));
        }
        catch (Exception exception)
        {
            String className = clazz.getName();
            if (className.indexOf("$") >= 0)
            {
                Class<?> parentClass = null;
                try
                {
                    parentClass = Class.forName(className.substring(0, className.lastIndexOf("$")));
                }
                catch (Exception e2)
                {
                    throw new NoSuchMethodException("class " + parentClass + " not found to resolve method " + name);
                }
                return ClassUtil.resolveMethod(parentClass, name,
                        (Class<?>[]) ClassUtil.checkInput(parameterTypes, Class.class));
            }
            throw new NoSuchMethodException("class " + clazz + " does not contain method " + name);
        }
    }

    /**
     * resolves a method the method.
     * @param object the object to start with
     * @param name the name of the method
     * @param parameterTypes the parameterTypes
     * @return Method
     * @throws NoSuchMethodException on lookup failure
     */
    public static Method resolveMethod(final Object object, final String name, final Class<?>[] parameterTypes)
            throws NoSuchMethodException
    {
        if (object == null)
        {
            throw new NoSuchMethodException("resolveField: object is null for method " + name);
        }
        return resolveMethod(object.getClass(), name, parameterTypes);
    }

    /**
     * returns the method.
     * @param object the object to start with
     * @param name the name of the method
     * @param arguments the arguments
     * @return Method
     * @throws NoSuchMethodException on lookup failure
     */
    public static Method resolveMethod(final Object object, final String name, final Object[] arguments)
            throws NoSuchMethodException
    {
        Class<?>[] parameterTypes = ClassUtil.getClass(arguments);
        String key = "METHOD:" + object.getClass() + "@" + name + "@" + FieldSignature.toDescriptor(parameterTypes);
        if (CACHE.containsKey(key))
        {
            return (Method) CACHE.get(key);
        }
        try
        {
            return ClassUtil.resolveMethod(object, name, parameterTypes);
        }
        catch (NoSuchMethodException noSuchMethodException)
        {
            // We get all methods
            List<Method> methods = ClassUtil.getAllMethods(object.getClass(), name);
            if (methods.size() == 0)
            {
                throw new NoSuchMethodException("No such method: " + name + " for object " + object);
            }
            // now we match the signatures
            methods = ClassUtil.matchSignature(methods, name, parameterTypes);
            if (methods.size() == 0)
            {
                throw new NoSuchMethodException("No method with right signature: " + name + " for object " + object);
            }
            // Now we find the most specific
            Method result = ClassUtil.getSpecificMethod(methods);
            CACHE.put(key, result);
            return result;
        }
    }

    /* ************ ANNOTATION UTILITIES *********** */

    /**
     * gets all the annotations of a class (public, protected, package, and private) and adds the result to the return value.
     * @param clazz the class
     * @param result the resulting set
     * @return the set of annotations including all annotations of the annotation clazz
     */
    public static Set<Annotation> getAllAnnotations(final Class<?> clazz, final Set<Annotation> result)
    {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (int i = 0; i < annotations.length; i++)
        {
            result.add(annotations[i]);
        }
        if (clazz.getSuperclass() != null)
        {
            return ClassUtil.getAllAnnotations(clazz.getSuperclass(), result);
        }
        return result;
    }

    /**
     * gets all the annotations of a class (public, protected, package, and private).
     * @param clazz the class
     * @return all annotations of the class
     */
    public static Set<Annotation> getAllAnnotations(final Class<?> clazz)
    {
        Set<Annotation> annotationSet = new LinkedHashSet<Annotation>();
        return ClassUtil.getAllAnnotations(clazz, annotationSet);
    }

    /**
     * resolves the annotation for a class, taking into account inner classes.
     * @param clazz the class to resolve the annotation for, including inner classes
     * @param annotationClass class of the annotation
     * @return Annotation the annotation
     * @throws NoSuchElementException on no such annotation
     */

    public static Annotation resolveAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotationClass)
            throws NoSuchElementException
    {
        try
        {
            return resolveAnnotationSuper(clazz, annotationClass);
        }
        catch (NoSuchElementException noSuchAnnotationException)
        {
            String className = clazz.getName();
            if (className.indexOf("$") >= 0)
            {
                Class<?> clazz2 = null;
                try
                {
                    clazz2 = Class.forName(className.substring(0, className.lastIndexOf("$")));
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    throw new NoSuchElementException("class " + clazz + " not found to resolve annotation " + annotationClass);
                }
                return ClassUtil.resolveAnnotation(clazz2, annotationClass);
            }
            throw new NoSuchElementException("class " + clazz + " does not contain annotation " + annotationClass);
        }
    }

    /* ************ OTHER UTILITIES *********** */

    /**
     * Returns whether a declaringClass is accessible according to the modifiers.
     * @param modifiers the modifiers
     * @param declaringClass the declaringClass
     * @param caller the caller
     * @return boolean isVisible
     */
    public static boolean isVisible(final int modifiers, final Class<?> declaringClass, final Class<?> caller)
    {
        if (Modifier.isPublic(modifiers))
        {
            return true;
        }
        if (Modifier.isProtected(modifiers))
        {
            if (declaringClass.isAssignableFrom(caller))
            {
                return true;
            }
            if (declaringClass.getPackage().equals(caller.getPackage()))
            {
                return true;
            }
            return false;
        }
        if (declaringClass.equals(caller))
        {
            return true;
        }
        return false;
    }

    /**
     * Determines &amp; returns whether constructor 'a' is more specific than constructor 'b', as defined in the Java Language
     * Specification par 15.12.
     * @return true if 'a' is more specific than b, false otherwise. 'false' is also returned when constructors are
     *         incompatible, e.g. have different names or a different number of parameters.
     * @param a reflects the first constructor
     * @param b reflects the second constructor
     */
    public static boolean isMoreSpecific(final Class<?>[] a, final Class<?>[] b)
    {
        if (a.length != b.length)
        {
            return false;
        }
        int i = 0;
        while (i < a.length)
        {
            if (!b[i].isAssignableFrom(a[i]))
            {
                return false;
            }
            i++;
        }
        return true;
    }

    /**
     * Determines &amp; returns whether constructor 'a' is more specific than constructor 'b', as defined in the Java Language
     * Specification par 15.12.
     * @return true if 'a' is more specific than b, false otherwise. 'false' is also returned when constructors are
     *         incompatible, e.g. have different names or a different number of parameters.
     * @param a the first constructor
     * @param b the second constructor
     */
    public static boolean isMoreSpecific(final Constructor<?> a, final Constructor<?> b)
    {
        if (Arrays.equals(a.getParameterTypes(), b.getParameterTypes()))
        {
            if (b.getDeclaringClass().isAssignableFrom(a.getDeclaringClass()))
            {
                return true;
            }
        }
        return ClassUtil.isMoreSpecific(a.getParameterTypes(), b.getParameterTypes());
    }

    /**
     * Determines &amp; returns whether constructor 'a' is more specific than constructor 'b', as defined in the Java Language
     * Specification par 15.12.
     * @return true if 'a' is more specific than b, false otherwise. 'false' is also returned when constructors are
     *         incompatible, e.g. have different names or a different number of parameters.
     * @param a reflects the first method
     * @param b reflects the second method
     */
    public static boolean isMoreSpecific(final Method a, final Method b)
    {
        if (!a.getName().equals(b.getName()))
        {
            return false;
        }
        return ClassUtil.isMoreSpecific(a.getParameterTypes(), b.getParameterTypes());
    }

    /**
     * Returns whether a field is visible for a caller.
     * @param field The field
     * @param caller The class of the caller for whom invocation visibility is checked.
     * @return boolean yes or no
     */
    public static boolean isVisible(final Field field, final Class<?> caller)
    {
        return ClassUtil.isVisible(field.getModifiers(), field.getDeclaringClass(), caller);
    }

    /**
     * Returns whether a constructor is visible for a caller.
     * @param constructor The constructor
     * @param caller The class of the caller for whom invocation visibility is checked.
     * @return boolean yes or no
     */
    public static boolean isVisible(final Constructor<?> constructor, final Class<?> caller)
    {
        return ClassUtil.isVisible(constructor.getModifiers(), constructor.getDeclaringClass(), caller);
    }

    /**
     * Returns whether a method is visible for a caller.
     * @param method The method
     * @param caller The class of the caller for whom invocation visibility is checked.
     * @return boolean yes or no
     */
    public static boolean isVisible(final Method method, final Class<?> caller)
    {
        return ClassUtil.isVisible(method.getModifiers(), method.getDeclaringClass(), caller);
    }

    /**
     * Filters an array methods for signatures that are compatible with a given signature.
     * @param methods which are methods to be filtered.
     * @param name reflects the method's name, part of the signature
     * @param argTypes are the method's argument types
     * @return Method[] An unordered Method-array consisting of the elements of 'methods' that match with the given signature.
     *         An array with 0 elements is returned when no matching Method objects are found.
     */
    public static List<Method> matchSignature(final List<Method> methods, final String name, final Class<?>[] argTypes)
    {
        List<Method> results = new ArrayList<Method>();
        for (int i = 0; i < methods.size(); i++)
        {
            if (ClassUtil.matchSignature(methods.get(i), name, argTypes))
            {
                results.add(methods.get(i));
            }
        }
        return results;
    }

    /**
     * Filters an array methods for signatures that are compatible with a given signature.
     * @param method The method to be filtered.
     * @param name reflects the method's name, part of the signature
     * @param argTypes are the method's argument types
     * @return boolean if methodParameters assignable from argTypes
     */
    public static boolean matchSignature(final Method method, final String name, final Class<?>[] argTypes)
    {
        if (!method.getName().equals(name))
        {
            return false;
        }
        if (method.getParameterTypes().length != argTypes.length)
        {
            return false;
        }
        Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < method.getParameterTypes().length; i++)
        {
            if (!(types[i].isAssignableFrom(argTypes[i]) || types[i].equals(Primitive.getPrimitive(argTypes[i]))))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts an array of objects to their corresponding classes. Note that primitive types are always autoboxed to the
     * corresponding object types. So an int in the array will have an Integer.class at the position in the resulting Class
     * array.
     * @param array the array to invoke
     * @return the array of classes
     */
    public static Class<?>[] getClass(final Object[] array)
    {
        if (array == null)
        {
            return new Class[0];
        }
        Class<?>[] result = new Class[array.length];
        for (int i = 0; i < result.length; i++)
        {
            if (array[i] == null)
            {
                result[i] = null;
            }
            else
            {
                result[i] = array[i].getClass();
            }
        }
        return result;
    }

    /** ************** PRIVATE METHODS ********* */

    /**
     * checks the input of an array.
     * @param array the array
     * @param myClass the class of the result
     * @return Returns array if array!=null else returns myClass[0]
     */
    private static Object checkInput(final Object[] array, final Class<?> myClass)
    {
        if (array != null)
        {
            return array;
        }
        return Array.newInstance(myClass, 0);
    }

    /**
     * Determines & returns the most specific constructor as defined in the Java Language Specification par 15.12. The current
     * algorithm is simple and reliable, but probably slow.
     * @param methods are the constructors to be searched. They are assumed to have the same name and number of parameters, as
     *            determined by the constructor matchSignature.
     * @return Constructor which is the most specific constructor.
     * @throws NoSuchMethodException when no constructor is found that's more specific than the others.
     */
    private static Constructor<?> getSpecificConstructor(final Constructor<?>[] methods) throws NoSuchMethodException
    {
        if (methods.length == 0)
        {
            throw new NoSuchMethodException();
        }
        if (methods.length == 1)
        {
            return methods[0];
        }
        // Apply generic algorithm
        int resultID = 0; // Assume first method to be most specific
        while (resultID < methods.length)
        {
            // Verify assumption
            boolean success = true;
            for (int i = 0; i < methods.length; i++)
            {
                if (resultID == i)
                {
                    continue;
                }
                if (!isMoreSpecific(methods[resultID], methods[i]))
                {
                    success = false;
                }
            }
            // Assumption verified
            if (success)
            {
                return methods[resultID];
            }
            resultID++;
        }
        // No method is most specific, thus:
        throw new NoSuchMethodException();
    }

    /**
     * Determines & returns the most specific method as defined in the Java Language Specification par 15.12. The current
     * algorithm is simple and reliable, but probably slow.
     * @param methods which are the methods to be searched. They are assumed to have the same name and number of parameters, as
     *            determined by the method matchSignature.
     * @return The most specific method.
     * @throws NoSuchMethodException when no method is found that's more specific than the others.
     */
    private static Method getSpecificMethod(final List<Method> methods) throws NoSuchMethodException
    {
        // Check for evident cases
        if (methods.size() == 0)
        {
            throw new NoSuchMethodException();
        }
        if (methods.size() == 1)
        {
            return methods.get(0);
        }
        // Apply generic algorithm
        int resultID = 0; // Assume first method to be most specific
        while (resultID < methods.size())
        {
            // Verify assumption
            boolean success = true;
            for (int i = 0; i < methods.size(); i++)
            {
                if (resultID == i)
                {
                    continue;
                }
                if (!isMoreSpecific(methods.get(resultID), methods.get(i)))
                {
                    success = false;
                }
            }
            // Assumption verified
            if (success)
            {
                return methods.get(resultID);
            }
            resultID++;
        }
        // No method is most specific, thus:
        throw new NoSuchMethodException();
    }

    /**
     * returns the interface method.
     * @param clazz the class to start with
     * @param name the name of the method
     * @param parameterTypes the parameterTypes
     * @return Method
     * @throws NoSuchMethodException on lookup failure
     */
    private static Method resolveMethodSuper(final Class<?> clazz, final String name, final Class<?>[] parameterTypes)
            throws NoSuchMethodException
    {
        String key = "METHOD:" + clazz + "@" + name + "@" + FieldSignature.toDescriptor(parameterTypes);
        try
        {
            if (CACHE.containsKey(key))
            {
                return (Method) CACHE.get(key);
            }
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            CACHE.put(key, method);
            return method;
        }
        catch (Exception exception)
        {
            if (clazz.getSuperclass() != null)
            {
                Method method = ClassUtil.resolveMethodSuper(clazz.getSuperclass(), name, parameterTypes);
                CACHE.put(key, method);
                return method;
            }
            throw new NoSuchMethodException(exception.getMessage());
        }
    }

    /**
     * resolves the field for a class, taking into account superclasses.
     * @param clazz the class for which superclasses will be probed
     * @param fieldName the name of the field to resolve
     * @return the field (if found)
     * @throws NoSuchFieldException if the field cannot be resolved
     */
    private static Field resolveFieldSuper(final Class<?> clazz, final String fieldName) throws NoSuchFieldException
    {
        String key = "FIELD:" + clazz + "@" + fieldName;
        try
        {
            if (CACHE.containsKey(key))
            {
                return (Field) CACHE.get(key);
            }
            Field result = clazz.getDeclaredField(fieldName);
            CACHE.put(key, result);
            return result;
        }
        catch (Exception exception)
        {
            if (clazz.getSuperclass() != null)
            {
                Field result = ClassUtil.resolveFieldSuper(clazz.getSuperclass(), fieldName);
                CACHE.put(key, result);
                return result;
            }
            throw new NoSuchFieldException(exception.getMessage());
        }
    }

    /**
     * resolves the annotation for a class, taking into account superclasses.
     * @param clazz the class for which superclasses will be probed
     * @param annotationClass the class of the annotation to resolve
     * @return the annotation (if found)
     * @throws NoSuchElementException if the annotation cannot be resolved
     */
    private static Annotation resolveAnnotationSuper(final Class<?> clazz, final Class<? extends Annotation> annotationClass)
            throws NoSuchElementException
    {
        String key = "ANNOTATION:" + clazz + "@" + annotationClass;
        try
        {
            if (CACHE.containsKey(key))
            {
                return (Annotation) CACHE.get(key);
            }
            Annotation[] annotations = clazz.getDeclaredAnnotations();
            Annotation result = null;
            for (Annotation annotation : annotations)
            {
                if (annotation.annotationType().equals(annotationClass))
                {
                    result = annotation;
                    break;
                }
            }
            if (result == null)
            {
                throw new NoSuchElementException("Annotation " + annotationClass + " not found in class " + clazz.getName());
            }
            CACHE.put(key, result);
            return result;
        }
        catch (Exception exception)
        {
            if (clazz.getSuperclass() != null)
            {
                Annotation result = ClassUtil.resolveAnnotationSuper(clazz.getSuperclass(), annotationClass);
                CACHE.put(key, result);
                return result;
            }
            throw new NoSuchElementException(exception.getMessage());
        }
    }

    /**
     * Retrieve a file pointer of a class, e.g. to request the last compilation date.
     * @param object the object for which the class information should be retrieved
     * @return a ClassFileDescriptor with some information of the .class file
     */
    public static ClassFileDescriptor classFileDescriptorForObject(final Object object)
    {
        return classFileDescriptorForClass(object.getClass());
    }

    /**
     * Retrieve a file pointer of a class, e.g. to request the last compilation date.
     * @param clazz the class for which a file descriptor should be retrieved
     * @return a ClassFileDescriptor with some information of the .class file
     */
    public static ClassFileDescriptor classFileDescriptorForClass(final Class<?> clazz)
    {
        Path classPath = ResourceResolver.resolve("/" + clazz.getName().replaceAll("\\.", "/") + ".class").asPath();
        return classFileDescriptorForPath(classPath);
    }

    /**
     * Retrieve a file pointer of a class, e.g. to request the last compilation date.
     * @param classPath the Path to a class for which a file descriptor should be retrieved
     * @return a ClassFileDescriptor with some information of the .class file
     */
    public static ClassFileDescriptor classFileDescriptorForPath(final Path classPath)
    {
        try
        {
            URL clazzUrl = classPath.toUri().toURL();
            if (clazzUrl.toString().startsWith("jar:file:") && clazzUrl.toString().contains("!"))
            {
                String[] parts = clazzUrl.toString().split("\\!");
                String jarFileName = parts[0].replace("jar:file:", "");
                try
                {
                    URL jarURL = new URL("file:" + jarFileName);
                    File jarUrlFile = new File(jarURL.toURI());
                    try (JarFile jarFile = new JarFile(jarUrlFile))
                    {
                        if (parts[1].startsWith("/"))
                        {
                            parts[1] = parts[1].substring(1);
                        }
                        JarEntry jarEntry = jarFile.getJarEntry(parts[1]);
                        return new ClassFileDescriptor(jarEntry, jarFileName + "!" + parts[1]);
                    }
                    catch (Exception exception)
                    {
                        URL jarURL2 = new URL("file:" + jarFileName);
                        return new ClassFileDescriptor(new File(jarURL2.toURI()));
                    }
                }
                catch (URISyntaxException | MalformedURLException exception)
                {
                    return new ClassFileDescriptor(new File(jarFileName));
                }
            }
            try
            {
                return new ClassFileDescriptor(new File(clazzUrl.toURI()));
            }
            catch (URISyntaxException exception)
            {
                return new ClassFileDescriptor(new File(clazzUrl.getPath()));
            }
        }
        catch (MalformedURLException exception)
        {
            return new ClassFileDescriptor(new File(classPath.toString()));
        }

    }

    /**
     * ClassFileDescriptor contains some information about a class file, either stand-alone on the classpath, or within a Jar
     * file.<br>
     * <br>
     * Copyright (c) 2019-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
     * See for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>.
     * The source code and binary code of this software is proprietary information of Delft University of Technology.
     * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
     */
    public static class ClassFileDescriptor
    {
        /** the final name + extension (without path) of the file. */
        private final String name;

        /** the full path (with a ! inside if it is a Jar file descriptor). */
        private final String path;

        /** whether it is a file from a Jar container or a zip file. */
        private final boolean jar;

        /** last changed date of the file in millis, if known. Otherwise 1-1-1970, 00:00. */
        private long lastChangedDate;

        /**
         * Construct a ClassFileDescriptor from a File.
         * @param classFile the file to use.
         */
        public ClassFileDescriptor(final File classFile)
        {
            this.name = classFile.getName();
            this.path = classFile.getPath();
            this.jar = false;
            long lastModified = classFile.lastModified();
            if (lastModified == 0L)
            {
                BasicFileAttributes attributes;
                try
                {
                    attributes = Files.readAttributes(Paths.get(this.path), BasicFileAttributes.class);
                    lastModified = attributes.lastModifiedTime().toMillis();
                }
                catch (IOException exception)
                {
                    // ignore - date will be 1-1-1970
                }
            }
            this.lastChangedDate = lastModified;
        }

        /**
         * Construct a ClassFileDescriptor from a JarEntry.
         * @param jarEntry the JarEntry to use.
         * @param path the path of the JarEntry
         */
        public ClassFileDescriptor(final JarEntry jarEntry, final String path)
        {
            this.name = jarEntry.getName();
            this.path = path;
            this.jar = true;
            this.lastChangedDate = jarEntry.getLastModifiedTime().toMillis();
        }

        /**
         * Construct a ClassFileDescriptor from a ZipEntry.
         * @param zipEntry the ZipEntry to use.
         * @param path the path of the ZipEntry
         */
        public ClassFileDescriptor(final ZipEntry zipEntry, final String path)
        {
            this.name = zipEntry.getName();
            this.path = path;
            this.jar = true;
            this.lastChangedDate = zipEntry.getLastModifiedTime().toMillis();
        }

        /**
         * @return name
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * @return path
         */
        public String getPath()
        {
            return this.path;
        }

        /**
         * @return jar
         */
        public boolean isJar()
        {
            return this.jar;
        }

        /**
         * @return lastChangedDate
         */
        public long getLastChangedDate()
        {
            return this.lastChangedDate;
        }

        @Override
        public String toString()
        {
            return "ClassFileDescriptor [name=" + this.name + ", path=" + this.path + ", jar=" + this.jar + ", lastChangedDate="
                    + this.lastChangedDate + "]";
        }
    }
}
