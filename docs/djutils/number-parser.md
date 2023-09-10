# Number Parser

## The NumberParser class provides Locale-dependent, flexible parsing of numbers

Locale-dependent parsing can be quite difficult. The classes provided in Java are very strict, which means that an extra space or plus at the start of the parsed String already leads to a partially parsed String or to an exception. 

The `NumberParser` class in djutils-base tries to address this issue. and provides many options to parse Strings to numbers (`int`, `long`, `float`, and `double`). It allows for the use of different Locales, and it can parse in a strict manner (only allowing the exact format that the Locale prescribes) or in a lenient manner (allowing for leading plus sign for the number and leading plus sign for the exponent, and allowing for the wrong case of the exponent character). Both strict and lenient parsing ignores leading and trailing whitespace. The parser also allows for trailing characters (such as units) and keeps a pointer where the first trailing character begins.

The class has been defined to use two ways of defining a parser: The first is a classical manner with a constructor that defines the settings::

```java
  NumberParser np = new NumberParser(true, true, Locale.US);
  String text = "+1.127E3 m/s";
  double d = np.parseDouble(text);
  String unit = text.substring(np.getTrailingPosition()).trim();
```

After executing, the local variables have the following values:

```
d = 1127.0
unit = "m/s"
```

It is also possible to use the `NumberParser` for a simple lenient setting without trailing information:

```java
  double d = new NumberParser().parseDouble(text);
```
 
The `NumberParser` class can also work with chaining, which makes the meaning of the booleans more clear:

```java
  double d = new NumberParser().lenient().locale(Locale.US).noTrailing().parseDouble(text);
```

The chaining methods that can be used are:

| method | meaning |
| ----------------- | -------------- |
| strict() | strict parsing, no deviations from the definitions in the Locale allowed |
| lenient() | opposite of strict(), deviations from the Locale are allowed |
| trailing() | trailing information is allowed (but not necessary) |
| noTrailing() | trailing information is not allowed (exception of present) |
| locale(newLocale) | define the parser to use newLocale |

The parsing methods that can be used are:

| method | meaning |
| ----------------- | -------------- |
| double parseDouble(text) | parse text as a double value |
| float parseFloat(text) | parse text as a float value |
| int parseInt(text) | parse text as an int value |
| long parseLong(text) | parse text as a long value |

!!! Warning
    The parsing of numbers is dependent on the version of the JDK / JRE. Jdk11 uses CLDR version 33 
    (see [cldr-33](https://cldr.unicode.org/index/downloads/cldr-33)) 
    and Jdk17 uses CLDR version 35.1 (see [cldr-35](https://cldr.unicode.org/index/downloads/cldr-35)). 
    This means that, for instance, both the French and the Arabic Locales for parsing and displaying numbers
    are different between Jdk11 and Jdk17. A number formatted and stores with Jdk11 cannot be read back by Jdk17 
    and vice versa. 

