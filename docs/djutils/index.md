# DJUTILS Project

## What is in the DJUTILS main project?

These are light-weight projects that do not depend on huge external libraries and can be used in all kinds of applications.

*  DJUTILS provides a framework for [decoding and dumping data](decoder-dumper) with decoders for hexadecimal and base64 data.
*  DJUTILS contains [immutable collections](immutable-collections), such as the ImmutableList, ImmutableSet and ImmutableMap.
*  DJUTILS [events](event) is a framework for publish and subscribe communication, including remote event handling.
*  DJUTILS offers an implementation of [complex numbers](complex-numbers) and [complex math](complex-math).
*  DJUTILS offers several helper classes for [logging](logging) using the tinylog package.
*  DJUTILS provides several classes for handling [exceptions](exceptions) in an easy way, such as Throw and Try.
*  DJUTILS has a class to easily [resolve URLs](urlresource) from a resource location, also when the resource is in a JAR file.
*  DJUTILS extends the [reflection classes](reflection) of Java with several new classes to easily work with Method, Field, and Class signatures.
*  DJUTILS has a package that implements computation of [three kinds of mean value](means).


## Maven use

Maven is one of the easiest ways to include DJUTILS in a Java project. The Maven files for DJUTILS reside at Maven Central as of version 2.0.0. When a POM-file is created for the project, the following snippet needs to be included to include DJUTILS:

```xml
<dependencies>
  <dependency>
    <groupId>org.djutils</groupId>
    <artifactId>djutils</artifactId>
    <version>2.3.0</version>
  </dependency>
</dependencies>
```

Of course, the version number (2.3.0 in the above example) needs to be replaced with the version that one wants to include in the project.

As of version 2.3.0, djutils is compliant with Java-17.

DJUTILS jars before version 2 are kept on a server at TU Delft at [https://djutils.org/maven](https://djutils.org/maven).


## Dependencies

DJUTILS is directly dependent on one package, which can have further dependencies:

* [tinylog](https://tinylog.org/v1/) for providing an easy-to-use and fast logger.

If the DJUTILS library is used as a part of a Maven project, all dependencies will be automatically resolved, and the programmer / user does not have to worry about finding the libraries.


## Documentation and test reports

DJUTILS documentation and test reports for the current version can be found at [https://djutils.org/docs/latest/djutils](https://djutils.org/docs/latest/djutils) and the API can be found at [https://djutils.org/docs/latest/djutils/apidocs/index.html](https://djutils.org/docs/latest/djutils/apidocs/index.html).

