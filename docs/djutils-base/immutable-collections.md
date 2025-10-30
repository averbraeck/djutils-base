# Immutable Collections

## This package augments the Java collections framework with immutable variants.

When a collection is shared between different parties (classes or packages) there is often an implied trust that none of the parties subsequently modifies that collection. Violating such trust may break applications in ways that are very hard to debug. One commonly used way to deal with passing collections among untrusting parties is to pass around only copies of those collections instead of the collections themselves. Obviously, this may be expensive, both in memory requirements and in cpu time. This immutable collections package makes these (expensive) copies only when none of the parties may modify the collection. The immutable collections package also implements a one-way trust relation by providing an immutability shield around a collection whose content can still be modified by holders of the original (embedded, wrapped) collection, but not by holders of the immutable collection. In this case the holders of the immutable collection should be aware that the collection may be modified by another party, but the overhead of making and storing a copy just to be safe from programming errors is avoided.

| Trust relation | Immutable protection | Overhead |
| ----------------- | ---------------------------- | -------------- |
| Omni-directional (all holders of the immutable collection see <br/>a snapshot of the collection that can never be altered) | COPY | Large |
| One-directional (holders of the immutable collection see a collection <br/>that can be changed, though not by themselves) | WRAP | Small |

The classes in the immutable collections package embed the provided collection or a copy thereof (created at time of construction). The classes implement only those methods of the embedded collection that cannot modify it. The immutable collections can construct an Iterator, however the returned object is an ImmutableIterator that will throw an Exception if the remove method is called.

An immutable collection embedding a particular collection has the same generic parameters as the embedded collection. The constructors take a compatible collection object and then embed (wrap) that object, or a make and embed a copy of that collection object:

| Immutable collection | Constructed from | COPY / WRAP |
| --------------------------- | ----------------------- | --------------------- |
| ImmutableHashSet<E> | Collection<E> | Implicit COPY (into a HashSet) |
| | Set<E> | COPY (into a HashSet), or WRAP |
| ImmutableLinkedHashSet<E> | Collection<E> | Implicit COPY (into a LinkedHashSet) |
| | Set<E> | COPY (into a LinkedHashSet), or WRAP |
| ImmutableArrayList<E> | Collection<E> | Implicit COPY (into an ArrayList) |
| | List<E> | COPY (into an ArrayList), or WRAP |
| ImmutableHashMap<K,V> | Map<K,V> | COPY (into a HashMap), or WRAP |

The immutable collection package does not provide any protection against modifications of the objects contained in those collections. Whether the contents of immutable collections are prone to change is a property of those objects.
    
## Example code

This example code creates an ordinary `HashSet` (for Strings) and adds two Strings to it. Then two `ImmutableHashSets` are constructed. The first holds a COPY of the underlying HashSet, the other WRAPS the underlying HashSet.

When the underlying `HashSet` is modified, this is reflected in the `ImmutableHashSet` that WRAPS, but not in the one that holds a COPY.
    
The `ImmutableHashSet` can produce an ImmutableIterator for iterating over the set. An attempt to modify the underlying set with the remove method will result in a `UnsupportedOperationException`.
    
The `ImmutableHashSets` do not have a add, or remove methods.
    
```java
HashSet<String> hashSet = new HashSet<>(); // Create a normal HashSet containing Strings
hashSet.add("String 1"); // Add a String
hashSet.add("String 2"); // Add another String
System.out.println("normal hash set contains: " + hashSet);
ImmutableHashSet<String> immutableHashSetWithCopy = new ImmutableHashSet<>(hashSet); // Makes a copy
ImmutableHashSet<String> immutablehashSetThatWraps = new ImmutableHashSet<>(hashSet, Immutable.WRAP);
System.out.println("immutable hash set with copy:  " + immutableHashSetWithCopy); // Contains two Strings
System.out.println("immutable hash set that wraps: " + immutablehashSetThatWraps); // Contains two Strings
hashSet.add("String 3"); // Modify the original hash set by adding a third String
System.out.println("immutable hash set with copy:  " + immutableHashSetWithCopy); // Contains two Strings
System.out.println("immutable hash set that wraps: " + immutablehashSetThatWraps); // Contains three Strings
ImmutableIterator<String> immutableIterator = immutableHashSetWithCopy.iterator(); // Create an Iterator
while (immutableIterator.hasNext()) // Iterate over the immutable hash set 
{
    String string = immutableIterator.next();
    System.out.println("string is " + string);
    immutableIterator.remove(); // Throws UnsupportedOperationException
}
immutableHashSetWithCopy.add("String 4"); // does not compile; there is no add method
immutableHashSetThatWraps.add("String 4"); // does not compile; there is no add method
immutableHashSetWithCopy.remove("String 2"); // does not compile; there is no add method
immutableHashSetThatWraps.remove("String 2"); // does not compile; there is no add method
```
When the last four (non-compiling) lines are removed, or commented out, this code can be compiled and run and the output would be:
    
```text
normal hash set contains: [String 1, String 2]
immutable hash set with copy:  ImmutableHashSet [[String 1, String 2]]
immutable hash set that wraps: ImmutableHashSet [[String 1, String 2]]
immutable hash set with copy:  ImmutableHashSet [[String 1, String 2]]
immutable hash set that wraps: ImmutableHashSet [[String 1, String 2, String 3]]
string is String 1
Exception in thread "main" java.lang.UnsupportedOperationException: remove
  at java.base/java.util.Iterator.remove(Iterator.java:102)
  at org.djutils.immutablecollections.ImmutableCollectionsDemo.main(ImmutableCollectionsDemo.java:41)
```
    
The `ImmutableArrayList`, `ImmutableLinkedHashMap`, `ImmutableLinkedHashSet`, `ImmutableTreeMap`, `ImmutableTreeSet` and `ImmutableVector` similarly encapsulate the underlying collection, preventing modification by code that only has access to that Immutable container, or, in those cases where wrapping is not possible, making a copy that cannot even be modified by code that does have access to the underlying collection.