package org.djutils.exceptions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;

import org.djutils.reflection.ClassUtil;

/**
 * The Throw class has a number of static methods that make it easy to throw an exception under conditions for any Exception
 * class, including the standard Java exceptions and exceptions from libraries that are used in the project. Instead of:
 * 
 * <pre>
 * if (car == null)
 * {
 *     throw new NullPointerException(&quot;Car may not be null.&quot;);
 * }
 * if (Double.isNaN(car.getPosition()))
 * {
 *     throw new IllegalArgumentException(&quot;Position of car &quot; + car + &quot; is NaN.&quot;);
 * }
 * </pre>
 * 
 * we can write:
 * 
 * <pre>
 * Throw.whenNull(car, &quot;Car may not be null.&quot;);
 * Throw.when(Double.isNaN(car.getPosition()), IllegalArgumentException.class, &quot;Position of car %s is NaN.&quot;, car);
 * </pre>
 * 
 * The exception message can be formatted with additional arguments, such that the overhead of building the exception message
 * only occurs if the exception condition is met. All methods have a version where the first parameter is returned. Thereby, the
 * Throw can be used as part of a <b>super</b>(...) call in a constructor.
 * <p>
 * Copyright (c) 2016-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
 */
@SuppressWarnings("checkstyle:linelength")
public final class Throw
{
    /** private constructor for utility class. */
    private Throw()
    {
        // utility class
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. Use as
     * follows: <br>
     * 
     * <pre>
     * Throw.when(Double.isNan(object.getValue()), IllegalArgumentException.class, &quot;Value may not be NaN.&quot;);
     * </pre>
     * 
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     */
    public static <T extends Throwable> void when(final boolean condition, final Class<T> throwableClass, final String message)
            throws T
    {
        if (condition)
        {
            throwMessage(throwableClass, message, null);
        }
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. Use as
     * follows: <br>
     * 
     * <pre>
     * Throw.when(Double.isNan(object.getValue()), IllegalArgumentException.class, &quot;Value may not be NaN for object %s.&quot;, object);
     * </pre>
     * 
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg Object; value to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     */
    public static <T extends Throwable> void when(final boolean condition, final Class<T> throwableClass, final String message,
            final Object arg) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg);
            throwMessage(throwableClass, message, argList);
        }
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. Use as
     * follows: <br>
     * 
     * <pre>
     * Throw.when(Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s with name %s.&quot;, object, name);
     * </pre>
     * 
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     */
    public static <T extends Throwable> void when(final boolean condition, final Class<T> throwableClass, final String message,
            final Object arg1, final Object arg2) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            throwMessage(throwableClass, message, argList);
        }
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. Use as
     * follows: <br>
     * 
     * <pre>
     * Throw.when(Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s with name %s and id %s.&quot;, object, name, id);
     * </pre>
     * 
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param arg3 Object; 3rd value to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     */
    public static <T extends Throwable> void when(final boolean condition, final Class<T> throwableClass, final String message,
            final Object arg1, final Object arg2, final Object arg3) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            throwMessage(throwableClass, message, argList);
        }
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. Use as
     * follows: <br>
     * 
     * <pre>
     * Throw.when(Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s with name %s, id %s and parent %s.&quot;, object, name, id, parent);
     * </pre>
     * 
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param arg3 Object; 3rd value to use for the formatting identifiers
     * @param args Object...; potential 4th and further values to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     */
    public static <T extends Throwable> void when(final boolean condition, final Class<T> throwableClass, final String message,
            final Object arg1, final Object arg2, final Object arg3, final Object... args) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            argList.addAll(Arrays.asList(args));
            throwMessage(throwableClass, message, argList);
        }
    }

    /**
     * Private method to handle the throwing an Exception, Throwable or Error.
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with potential formatting identifiers
     * @param argList List&lt;Object&gt;; List with potential values to use for the formatting identifiers, or null when there
     *            are no formatting identifiers
     * @throws T the throwable to throw
     * @param <T> the Throwable type
     */
    private static <T extends Throwable> void throwMessage(final Class<T> throwableClass, final String message,
            final List<Object> argList) throws T
    {
        // create a clear message
        List<StackTraceElement> steList = new ArrayList<>(Arrays.asList(new Throwable().getStackTrace()));
        steList.remove(0); // remove the throwMessage(...) call
        steList.remove(0); // remove the when(...) call
        StackTraceElement[] ste = steList.toArray(new StackTraceElement[steList.size()]);
        String where = ste[0].getClassName() + "." + ste[0].getMethodName() + " (" + ste[0].getLineNumber() + "): ";
        String formattedMessage;
        if (argList == null)
        {
            formattedMessage = message;
        }
        else
        {
            try
            {
                formattedMessage = where + String.format(message, argList.toArray());
            }
            catch (IllegalFormatException exception)
            {
                formattedMessage = where + message + " [FormatException; args=" + argList + "]";
            }
        }

        // throw all other exceptions through reflection
        T exception;
        try
        {
            Constructor<T> constructor = ClassUtil.resolveConstructor(throwableClass, new Class<?>[] {String.class});
            exception = constructor.newInstance(formattedMessage);
            exception.setStackTrace(ste);
        }
        catch (Throwable t)
        {
            RuntimeException rte = new RuntimeException(t.getMessage(), new Exception(formattedMessage));
            rte.setStackTrace(ste);
            throw rte;
        }
        throw exception;
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. This
     * version of the method returns its first parameter, so it can be used inside a constructor. Use e.g., as follows:
     * 
     * <pre>
     * super(Throw.when(object, Double.isNaN(object.getValue()), IllegalArgumentException.class, "Value may not be NaN."));
     * </pre>
     * 
     * @param object O; the object to return by this static method
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     */
    public static <T extends Throwable, O extends Object> O when(final O object, final boolean condition,
            final Class<T> throwableClass, final String message) throws T
    {
        if (condition)
        {
            throwMessage(throwableClass, message, null);
        }
        return object;
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. This
     * version of the method returns its first parameter, so it can be used inside a constructor. Use e.g., as follows:
     * 
     * <pre>
     * super(Throw.when(object, Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s.&quot;, object));
     * </pre>
     * 
     * @param object O; the object to return by this static method
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg Object; value to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     */
    public static <T extends Throwable, O extends Object> O when(final O object, final boolean condition,
            final Class<T> throwableClass, final String message, final Object arg) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg);
            throwMessage(throwableClass, message, argList);
        }
        return object;
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. This
     * version of the method returns its first parameter, so it can be used inside a constructor. Use e.g., as follows:
     * 
     * <pre>
     * super(Throw.when(object, Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s with name %s.&quot;, object, name));
     * </pre>
     * 
     * @param object O; the object to return by this static method
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     */
    public static <T extends Throwable, O extends Object> O when(final O object, final boolean condition,
            final Class<T> throwableClass, final String message, final Object arg1, final Object arg2) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            throwMessage(throwableClass, message, argList);
        }
        return object;
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. This
     * version of the method returns its first parameter, so it can be used inside a constructor. Use e.g., as follows:
     * 
     * <pre>
     * super(Throw.when(object, Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s with name %s and id %s.&quot;, object, name, id));
     * </pre>
     * 
     * @param object O; the object to return by this static method
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param arg3 Object; 3rd value to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     */
    public static <T extends Throwable, O extends Object> O when(final O object, final boolean condition,
            final Class<T> throwableClass, final String message, final Object arg1, final Object arg2, final Object arg3)
            throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            throwMessage(throwableClass, message, argList);
        }
        return object;
    }

    /**
     * Throw a Throwable (such as an Exception or Error) if a condition is met, e.g. for pre- and postcondition checking. This
     * version of the method returns its first parameter, so it can be used inside a constructor. Use e.g., as follows:
     * 
     * <pre>
     * super(Throw.when(object, Double.isNan(object.getValue()), IllegalArgumentException.class,
     *         &quot;Value may not be NaN for object %s with name %s, id %s and parent %s.&quot;, object, name, id, parent));
     * </pre>
     * 
     * @param object O; the object to return by this static method
     * @param condition boolean; the condition to check; an exception will be thrown if this is <b>true</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param arg3 Object; 3rd value to use for the formatting identifiers
     * @param args Object...; potential 4th and further values to use for the formatting identifiers
     * @throws T the throwable to throw on true condition
     * @param <T> the Throwable type
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public static <T extends Throwable, O extends Object> O when(final O object, final boolean condition,
            final Class<T> throwableClass, final String message, final Object arg1, final Object arg2, final Object arg3,
            final Object... args) throws T
    {
        if (condition)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            argList.addAll(Arrays.asList(args));
            throwMessage(throwableClass, message, argList);
        }
        return object;
    }

    /**
     * Throw a NullPointerException if object is null, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNull(value, &quot;value may not be null.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be null" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNull(value, &quot;value&quot;);
     * </pre>
     * 
     * @param object object to check; an exception will be thrown if the object is <b>null</b>
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            null"
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     * @throws NullPointerException if object is null
     */
    public static <O extends Object> O whenNull(final O object, final String message) throws NullPointerException
    {
        if (object == null)
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(NullPointerException.class, message + " may not be null", null);
            else
                throwMessage(NullPointerException.class, message, null);
        }
        return object;
    }

    /**
     * Throw a NullPointerException if object is null, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNull(object.getValue(), &quot;Value may not be null for object %s.&quot;, object);
     * </pre>
     * 
     * @param object object to check; an exception will be thrown if this is <b>null</b>
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg Object; value to use for the formatting identifiers
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     * @throws NullPointerException if object is null
     */
    public static <O extends Object> O whenNull(final O object, final String message, final Object arg)
            throws NullPointerException
    {
        if (object == null)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg);
            throwMessage(NullPointerException.class, message, argList);
        }
        return object;
    }

    /**
     * Throw a NullPointerException if object is null, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNull(object.getValue(), &quot;Value may not be null for object %s with name %s.&quot;, object, name);
     * </pre>
     * 
     * @param object object to check; an exception will be thrown if this is <b>null</b>
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     * @throws NullPointerException if object is null
     */
    public static <O extends Object> O whenNull(final O object, final String message, final Object arg1, final Object arg2)
            throws NullPointerException
    {
        if (object == null)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            throwMessage(NullPointerException.class, message, argList);
        }
        return object;
    }

    /**
     * Throw a NullPointerException if object is null, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNull(object.getValue(), &quot;Value may not be null for object %s with name %s and id %s.&quot;, object, name, id);
     * </pre>
     * 
     * @param object object to check; an exception will be thrown if this is <b>null</b>
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param arg3 Object; 3rd value to use for the formatting identifiers
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     * @throws NullPointerException if object is null
     */
    public static <O extends Object> O whenNull(final O object, final String message, final Object arg1, final Object arg2,
            final Object arg3) throws NullPointerException
    {
        if (object == null)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            throwMessage(NullPointerException.class, message, argList);
        }
        return object;
    }

    /**
     * Throw a NullPointerException if object is null, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNull(object.getValue(), &quot;Value may not be null for object %s with name %s, id %s and parent %s.&quot;, object, name, id,
     *         parent);
     * </pre>
     * 
     * @param object object to check; an exception will be thrown if this is <b>null</b>
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param arg2 Object; 2nd value to use for the formatting identifiers
     * @param arg3 Object; 3rd value to use for the formatting identifiers
     * @param args Object...; potential 4th and further values to use for the formatting identifiers
     * @param <O> the Object type to return
     * @return the object that was passed as the first parameter
     * @throws NullPointerException if object is null
     */
    public static <O extends Object> O whenNull(final O object, final String message, final Object arg1, final Object arg2,
            final Object arg3, final Object... args) throws NullPointerException
    {
        if (object == null)
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.add(arg2);
            argList.add(arg3);
            argList.addAll(Arrays.asList(args));
            throwMessage(NullPointerException.class, message, argList);
        }
        return object;
    }

    /**
     * Throw an ArithmeticException if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value&quot;);
     * </pre>
     * 
     * @param value double; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return double; the value that was passed as the first parameter
     * @throws ArithmeticException if value is NaN
     */
    public static double whenNaN(final double value, final String message) throws ArithmeticException
    {
        if (Double.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(ArithmeticException.class, message + " may not be NaN", null);
            else
                throwMessage(ArithmeticException.class, message, null);
        }
        return value;
    }

    /**
     * Throw an ArithmeticException if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value&quot;);
     * </pre>
     * 
     * @param value float; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return float; the value that was passed as the first parameter
     * @throws ArithmeticException if value is NaN
     */
    public static float whenNaN(final float value, final String message) throws ArithmeticException
    {
        if (Float.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(ArithmeticException.class, message + " may not be NaN", null);
            else
                throwMessage(ArithmeticException.class, message, null);
        }
        return value;
    }

    /**
     * Throw an ArithmeticException if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value&quot;);
     * </pre>
     * 
     * @param value Double; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return Double; the value that was passed as the first parameter
     * @throws ArithmeticException if value is NaN
     */
    public static Double whenNaN(final Double value, final String message) throws ArithmeticException
    {
        if (value != null && Double.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(ArithmeticException.class, message + " may not be NaN", null);
            else
                throwMessage(ArithmeticException.class, message, null);
        }
        return value;
    }

    /**
     * Throw an ArithmeticException if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;Value may not be NaN for object %s with name %s, id %s.&quot;, object, name, id);
     * </pre>
     * 
     * @param value Double; the value to check; an exception will be thrown if this is <b>NaN</b>
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param args Object...; potential 2nd and further values to use for the formatting identifiers
     * @return Double; the value that was passed as the first parameter
     * @throws ArithmeticException if value is NaN
     */
    public static Double whenNaN(final Double value, final String message, final Object arg1, final Object... args)
            throws ArithmeticException
    {
        if (value != null && Double.isNaN(value))
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.addAll(Arrays.asList(args));
            throwMessage(ArithmeticException.class, message, argList);
        }
        return value;
    }

    /**
     * Throw an ArithmeticException if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;value&quot;);
     * </pre>
     * 
     * @param value Float; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return Float; the value that was passed as the first parameter
     * @throws ArithmeticException if value is NaN
     */
    public static Float whenNaN(final Float value, final String message) throws ArithmeticException
    {
        if (value != null && Float.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(ArithmeticException.class, message + " may not be NaN", null);
            else
                throwMessage(ArithmeticException.class, message, null);
        }
        return value;
    }

    /**
     * Throw an ArithmeticException if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, &quot;Value may not be NaN for object %s with name %s, id %s.&quot;, object, name, id);
     * </pre>
     * 
     * @param value Float; the value to check; an exception will be thrown if this is <b>NaN</b>
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param args Object...; potential 2nd and further values to use for the formatting identifiers
     * @return Float; the value that was passed as the first parameter
     * @throws ArithmeticException if value is NaN
     */
    public static Float whenNaN(final Float value, final String message, final Object arg1, final Object... args)
            throws ArithmeticException
    {
        if (value != null && Float.isNaN(value))
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.addAll(Arrays.asList(args));
            throwMessage(ArithmeticException.class, message, argList);
        }
        return value;
    }

    /**
     * Throw a specified exception if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value&quot;);
     * </pre>
     * 
     * @param value double; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return double; the value that was passed as the first parameter
     * @throws T if value is NaN
     */
    public static <T extends Throwable> double whenNaN(final double value, final Class<T> throwableClass, final String message)
            throws T
    {
        if (Double.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(throwableClass, message + " may not be NaN", null);
            else
                throwMessage(throwableClass, message, null);
        }
        return value;
    }

    /**
     * Throw a specified exception if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value&quot;);
     * </pre>
     * 
     * @param value float; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return float; the value that was passed as the first parameter
     * @throws T if value is NaN
     */
    public static <T extends Throwable> float whenNaN(final float value, final Class<T> throwableClass, final String message)
            throws T
    {
        if (Float.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(throwableClass, message + " may not be NaN", null);
            else
                throwMessage(throwableClass, message, null);
        }
        return value;
    }

    /**
     * Throw a specified exception if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value&quot;);
     * </pre>
     * 
     * @param value Double; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return Double; the value that was passed as the first parameter
     * @throws T if value is NaN
     */
    public static <T extends Throwable> Double whenNaN(final Double value, final Class<T> throwableClass, final String message)
            throws T
    {
        if (value != null && Double.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(throwableClass, message + " may not be NaN", null);
            else
                throwMessage(throwableClass, message, null);
        }
        return value;
    }

    /**
     * Throw a specified exception if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;Value may not be NaN for object %s with name %s, 
     *          id %s.&quot;, object, name, id);
     * </pre>
     * 
     * @param value Double; the value to check; an exception will be thrown if this is <b>NaN</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param args Object...; potential 2nd and further values to use for the formatting identifiers
     * @return Double; the value that was passed as the first parameter
     * @throws T if value is NaN
     */
    public static <T extends Throwable> Double whenNaN(final Double value, final Class<T> throwableClass, final String message,
            final Object arg1, final Object... args) throws T
    {
        if (value != null && Double.isNaN(value))
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.addAll(Arrays.asList(args));
            throwMessage(throwableClass, message, argList);
        }
        return value;
    }

    /**
     * Throw a specified exception if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value may not be NaN.&quot;);
     * </pre>
     *
     * A shortened version where the text " may not be NaN" is automatically appended after the variable name is just listing
     * the variable name without any spaces:
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;value&quot;);
     * </pre>
     * 
     * @param value Float; value to check; an exception will be thrown if the object is <b>NaN</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, or the variable name that will be appended with " may not be
     *            NaN"
     * @return Float; the value that was passed as the first parameter
     * @throws T if value is NaN
     */
    public static <T extends Throwable> Float whenNaN(final Float value, final Class<T> throwableClass, final String message)
            throws T
    {
        if (value != null && Float.isNaN(value))
        {
            if (message.matches("\\S+")) // \S+ is any non-whitespace character
                throwMessage(throwableClass, message + " may not be NaN", null);
            else
                throwMessage(throwableClass, message, null);
        }
        return value;
    }

    /**
     * Throw a specified exception if value is NaN, e.g. for pre- and postcondition checking. Use as follows: <br>
     * 
     * <pre>
     * Throw.whenNaN(value, IllegalArgumentException.class, &quot;Value may not be NaN for object %s with name %s, 
     *          id %s.&quot;, object, name, id);
     * </pre>
     * 
     * @param value Float; the value to check; an exception will be thrown if this is <b>NaN</b>
     * @param throwableClass Class&lt;T&gt;; the Throwable type to throw
     * @param message String; the message to use in the exception, with formatting identifiers
     * @param arg1 Object; 1st value to use for the formatting identifiers
     * @param args Object...; potential 2nd and further values to use for the formatting identifiers
     * @return Float; the value that was passed as the first parameter
     * @throws T if value is NaN
     */
    public static <T extends Throwable> Float whenNaN(final Float value, final Class<T> throwableClass, final String message,
            final Object arg1, final Object... args) throws T
    {
        if (value != null && Float.isNaN(value))
        {
            List<Object> argList = new ArrayList<>();
            argList.add(arg1);
            argList.addAll(Arrays.asList(args));
            throwMessage(throwableClass, message, argList);
        }
        return value;
    }

    /**
     * Throw an unchecked exception for a method with a fixed signature (e.g., extending a method from a library that cannot be
     * changed), without having to declare the exception, which can be impossible when extending a method. The typical use is:
     * 
     * <pre>
     *   &#64;Override
     *   public void someMethod() {  
     *     try {
     *       // some code that throws e.g., an IOException
     *     } catch IOException e {
     *       Throw.throwUnchecked(e);
     *     }  
     *   }
     * </pre>
     * 
     * From: <a href="http://blog.ragozin.info/2011/10/java-how-to-throw-undeclared-checked.html" target="_blank">
     * http://blog.ragozin.info/2011/10/java-how-to-throw-undeclared-checked.html</a> as mentioned in <a href=
     * "https://stackoverflow.com/questions/11942946/how-to-throw-an-exception-when-your-method-signature-doesnt-allow-to-throw-exce"
     * target="_blank">
     * https://stackoverflow.com/questions/11942946/how-to-throw-an-exception-when-your-method-signature-doesnt-allow-to-throw-exce</a>.
     * @param e Throwavble; the exception of Throwable to throw in an unchecked manner
     */
    public static void throwUnchecked(final Throwable e)
    {
        Throw.<RuntimeException> throwAny(e);
    }

    /**
     * @param <E> The exception class
     * @param e Throwable; The throwable
     * @throws E The exception to throw
     */
    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAny(final Throwable e) throws E
    {
        throw (E) e;
    }
}
