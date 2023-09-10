# Helper classes for exceptions

## Throw

Throwing exceptions often takes quite a number of lines of code. The Throw class makes this a lot easier:

```java
if (car == null)
{
  throw new NullPointerException("Car may not be null.");
}
if (Double.isNaN(car.getPosition()))
{
  throw new IllegalArgumentException("Position of car " + car + " is NaN.");
}
```

Using the Throw class we can write:

```java
Throw.whenNull(car, "Car may not be null.");
Throw.when(Double.isNaN(car.getPosition()), IllegalArgumentException.class, 
  "Position of car %s is NaN.", car);
```

In order to minimize the overhead of the Throw call, do not use String concatenations in the error message. Instead, use a formatting string as explained in [https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax), where each of the variables to be inserted is indicated by a format specifier that starts with a percent sign, and the parameters to be inserted are included as the last arguments in the Throw call. Again, ensure that these arguments are not calls to expensive methods, as these will be carried out each time the Throw call is evaluated. When expensive calculations are to be carried out for the reporting as part of an often used test, consider using the classical try ... catch construct instead. The most used format specifiers are: %s for String, %d for an integer, and %f for a floating point variable.


## Try

The Try class has a number of static methods that make it easy to try-catch an exception for any Throwable class, including the standard Java exceptions and exceptions from libraries that are used in a project. Instead of:

```java
FileInputStream fis;
try
{
  fis = new FileInputStream(fileString);
}
catch (FileNotFoundException exception)
{
  throw new IllegalArgumentException("File " + fileString + 
    " is not a valid file.", exception);
}
// Code that does things with fis goes here
try
{
  fis.close();
}
catch (IOException exception)
{
  throw new RuntimeException("Could not close the file.", exception);
}
```

We can write:

```java
FileInputStream fis = Try.assign(() -> new FileInputStream(fileString), 
  IllegalArgumentException.class, "File %s is not a valid file.", fileString);
// Code that does things with fis goes here
Try.execute(() -> fis.close(), "Could not close the file.");
```

For each method there is a version without Throwable class, in which case a RuntimeException will be thrown.

The `Try` class cannot replace try-with-resource constructs.

The `Try` class also has a few methods to aid JUNIT tests: `testFail(...)` and `testNotFail(...)`.

The exception message can be formatted with additional arguments, such that the overhead of building the exception message only occurs if the exception condition is met. The formatting string is explained in [https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax). Each of the variables to be inserted in the formatting string is indicated by a format specifier that starts with a percent sign, and the parameters to be inserted are included as the last arguments in the Try call. Again, ensure that these arguments are not calls to expensive methods, as these will be carried out each time the Try call is evaluated. When expensive calculations are to be carried out for the reporting as part of an often used test, consider using the classical try ... catch construct instead. The most used format specifiers are: %s for String, %d for an integer, and %f for a floating point variable. 
