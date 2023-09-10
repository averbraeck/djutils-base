package org.djutils.immutablecollections;

import java.util.HashSet;

/**
 * Demonstration code.
 * @author pknoppers
 */
public final class ImmutableCollectionsDemo
{
    /**
     * Do not instantiate.
     */
    private ImmutableCollectionsDemo()
    {
        // Do not instantiate
    }

    /**
     * Demo code.
     * @param args String[]; not used
     */
    public static void main(final String[] args)
    {
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
        // immutableHashSetWithCopy.add("String 4"); // does not compile; there is no add method
        // immutableHashSetThatWraps.add("String 4"); // does not compile; there is no add method
        // immutableHashSetWithCopy.remove("String 2"); // does not compile; there is no add method
        // immutableHashSetThatWraps.remove("String 2"); // does not compile; there is no add method
    }

}
