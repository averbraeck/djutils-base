# Reflection helper classes

## Introduction

The `ClassUtil` static class can help with a lot of tasks relating to reflection that are not part of the Java reflection package. A number of most used examples are explained below. As resolving constructors and methods is quite expensive, ClassUtil caches all resolved constructors and methods based on their signature.


## ClassUtil.getAllFields(class)

The `getAllFields(class)` method returns a `Set` of fields of the class, including all fields of the superclasses. The resulting set includes public, protected, package, and private fields. The method uses recursion until it reaches `java.lang.Object`, which does not have a superclass. Note that private fields of a class can have the same name as private fields of one of their superclasses. All of these are included as they are different `Field` objects.


## ClassUtil.getAllMethods(class)

The `getAllMethods(class)` method returns a `Set` of methods of the class, including all methods of the superclasses. The resulting set includes public, protected, package, and private methods. The method uses recursion until it reaches `java.lang.Object`, which does not have a superclass. Note that private methods of a class can have the same name as private methods of one of their superclasses. All of these will be included as they are different `Method` objects.


## ClassUtil.getAllConstructors(class)

The `getAllConstructors(class)` method returns a `Set` of constructors of the class, including all constructors of the superclasses. The resulting set includes public, protected, package, and private constructors. The method uses recursion until it reaches `java.lang.Object`, which does not have a superclass. Note that you cannot invoke a constructor of a superclass directly, so this method has limited usage, except for understanding the constructor stack of a certain class.


## ClassUtil.isVisible(field, callerClass)

Check if the given `Field` is visible for the caller class. The caller class can be a class in the class hierarchy of the field, or an external class that wants access to the field.


## ClassUtil.isVisible(method, callerClass)

Check if the given `Method` is visible for the caller class. The caller class can be a class in the class hierarchy of the method, or an external class that wants access to the method, e.g., to execute it.


## ClassUtil.isVisible(constructor, callerClass)

Check if the given Constructor is visible for the caller class. Before invoking a constructor on a class, it is good to test whether the invoking class has the appropriate access rights to call the constructor.


## ClassUtil.resolveField(object, string) or ClassUtil.resolveField(class, string)

Find the given `Field` within the object or class. Throw a `NoSuchFieldException` if the field cannot be found.


## ClassUtil.resolveMethod(...)

Find the given `Method` within an object or class. Throw a `NoSuchMethodException` if the method cannot be found. Methods are searched based on their signature, which is given to resolveMethod as an array of Objects or an array of Classes. Therefore several variants of the `resolveMethod` method exist:

* `ClassUtil.resolveMethod(Class class, String name, Class[] signature)`
* `ClassUtil.resolveMethod(Class class, String name, Object[] signature)`
* `ClassUtil.resolveMethod(Object object, String name, Class[] signature)`
* `ClassUtil.resolveMethod(Object object, String name, Object[] signature)`


## ClassUtil.resolveConstructor(...)

Find a `Constructor` that matches the given arguments within a class. Throw a `NoSuchMethodException` if the constructor cannot be found. Constructors are searched based on their signature, which is given to resolveConstructor as an array of Objects or an array of Classes. Therefore several variants of the `resolveConstructor` method exist:

* `ClassUtil.resolveConstructor(Class class, Class[] signature)`
* `ClassUtil.resolveConstructor(Class class, Object[] signature)`


## ClassUtil.classFileDescriptor(...)

This method can give information about a class file, such as the name, whether it is inside a JAR file, and the last changed date. Information is provided in an object called `ClassUtil.ClassFileDescriptor`. An example is:

```java
    ClassFileDescriptor cfdClass = ClassUtil.classFileDescriptor(Test.class);
    Date cfdClassDate = new Date(cfdClass.getLastChangedDate());
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println(formatter.format(cfdClassDate));
```

This method will print the last changed date of the Test.class file, independent whether the class file is in the file system, or in a JAR file.


## Other methods

Several other methods exist, such as `isMoreSpecific` and `matchSignature`. See the documentation of the `ClassUtil` class for these methods.
