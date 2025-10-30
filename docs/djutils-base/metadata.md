# Metadata

Metadata is information that describes other data. The DJUTILS metadata package allows one to add a name and a description to a single `Object`, or an array of `Object`. The name and description are primarily for  _human consumption_. Additionally, a `MetaData` object contains one or more machine-readable desciptors of the data. The `verifyComposition` method of `MetaData` will check that an `Object`, or `Object[]` (array of `Object`) conforms to the descriptor(s). The metadata package is used in the DJUTILS `event` package and can be used anywhere else where an `Object` is used which is actually of more restricted type, but enforcement of that more restricted type is difficult, incomplete, or impossible with java generics. 

Verifying a payload takes some CPU time. When that becomes expensive, consider verifying the MetaData in your development and test environment, but not in production software.


## Example MetaData for a single object

```java
MetaData singleLong = new MetaData("Payload is one Long", "Payload is one Long; anything else is wrong",
        new ObjectDescriptor("Long", "64 bit Long", Long.class));
singleLong.verifyComposition(123L); // OK
singleLong.verifyComposition(123); // Throws ClassCastException
```

The code in the last line throws the exception because `123` is an `Integer`; not a `Long`.


## Example MetaData for an Object array

```java
MetaData objectArray = new MetaData("Payload is object array",
        "Payload is an object array of 3 objects; a String, a Double and an Integer",
        new ObjectDescriptor[] {
                new ObjectDescriptor("String", "String", String.class),
                new ObjectDescriptor("Double", "Double", Double.class),
                new ObjectDescriptor("Integer", "Integer", Integer.class) });
Object[] payload = new Object[] { "This is the string", 123.456, 987 };
objectArray.verifyComposition(payload); // OK
payload = new Object[] { "String", "123.456", 987L };
objectArray.verifyComposition(payload); // Throws ClassCastException
```
The code in the last line throws a ClassCastException because `987L` is a Long; not an Integer.


## Special cases

The `MetaData.EMPTY` object prescribes that the payload shall be `null`.

The `MetaData.NO_META_DATA` object prescribes that the composition of the payload is not to be verified because it varies in composition (or because the programmer was too lazy to write a constructor for a `MetaData` object that correctly describes the payload).
