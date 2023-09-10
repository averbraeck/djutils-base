# Mutable Primitives

## These classes augment the Java primitive wrappers with mutable variants

Sometimes, we want to return one or more primitive values after a method call in addition to the return value itself. 
As an example, consider a parser that returns the parsed value, but also an index for the character where the parsing stopped.
Often, 'ugly' constructs are used, such as providing a `new int[1]` to the method, or an `AtomicInteger`. Both solutions have an internal value thay can be written. Instead of these solutions, djutils-base contains the classes `MutableInt`, `MutableLong`, `MutableFloat`, `MutableDouble`, `MutableShort`, `MutableByte`, and `MutableBoolean`. The classes can, for instance be used as follows:

```java
  String text = "123.4 m/s";
  MutableInt position = new MutableInt(0);
  double d = parser.parseDouble(text, position);
```

After parsing, `position` can for instance contain the first position in the String after the number. This can be used as follows:

```java
  String unit = test.substring(position.get()).trim();
```

The String `unit` will contain the value `m/s` after this call.


## Methods for the mutable primitives

The `MutableInt`, `MutableLong`, `MutableFloat`, `MutableDouble`, `MutableShort`, `MutableByte` contain the following methods:

| method | meaning |
| ----------------- | -------------- |
| set(value) | set the internal value to value |
| get() | return the internal value |
| inc() | increment internal value with 1 |
| inc(value) | increment internal value with value |
| dec() | decrement internal value by 1 |
| dec(value) | decrement internal value by value |
| mul(value) | multiply internal value by value |
| div(value) | divide internal value by value |

The `MutableBoolean` class contains the following methods:

| method | meaning |
| ----------------- | -------------- |
| set(value) | set the internal value to value |
| get() | return the internal value |
| flip() | flip the internal boolean value from true to false or vice versa |

