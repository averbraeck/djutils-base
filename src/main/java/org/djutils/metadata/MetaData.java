package org.djutils.metadata;

import java.io.Serializable;
import java.util.Arrays;

import org.djutils.exceptions.Throw;

/**
 * MetaDataInterface; documenting Object arrays. <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class MetaData implements Serializable
{
    /** ... */
    private static final long serialVersionUID = 20200417L;

    /** Name of this MetaData object. */
    private final String name;

    /** Description of this MetaData object. */
    private final String description;

    /** The array of object descriptors. */
    private final ObjectDescriptor[] objectDescriptors;

    /** MetaData object that indicates no data is expected. */
    public static final MetaData EMPTY = new MetaData("No data", "No data", new ObjectDescriptor[0]);

    /** Meta data object to use when none is available. Please do not use this, except when the payload is varying. */
    public static final MetaData NO_META_DATA = new MetaData("No descriptive meta data provided", "Any payload is accepted",
            new ObjectDescriptor("No descriptive meta data provided", "Any payload is accepted", Object.class));

    /**
     * Construct a new MetaData object that can check an array of Object.
     * @param name String; name of the new MetaData object, which cannot be null or the empty string
     * @param description String; description of the new MetaData object
     * @param objectDescriptors ObjectDescriptor...; array of ObjectDescriptor. This constructor does &lt;b&gt;not&lt;/b&gt;
     *            make a deep copy of this array; subsequent modification of the contents of the provided
     *            <code>objectDescriptors</code> array will affect the behavior of the MetaData object.
     */
    public MetaData(final String name, final String description, final ObjectDescriptor... objectDescriptors)
    {
        Throw.whenNull(name, "name may not be null");
        Throw.when(name.length() == 0, IllegalArgumentException.class, "name cannot be the empty string");
        Throw.whenNull(description, "description may not be null");
        for (int i = 0; i < objectDescriptors.length; i++)
        {
            ObjectDescriptor objectDescriptor = objectDescriptors[i];
            Throw.whenNull(objectDescriptor, "objectDescriptor %d may not be null", i);
        }
        this.name = name;
        this.description = description;
        this.objectDescriptors = objectDescriptors;
    }

    /**
     * Retrieve the name of this MetaData object.
     * @return String; the name of this MetaData object
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Retrieve the description of this MetaData object.
     * @return String; the description of this MetaData object
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Retrieve the length of described Object array.
     * @return int; the length of the described Object array; returns 0 if this MetaDataObject is not set up to validate an
     *         array of Object.
     */
    public int size()
    {
        return this.objectDescriptors.length;
    }

    /**
     * Returns a safe copy of the object descriptors. As the object descriptors are immutable objects, they are not cloned.
     * @return ObjectDescriptor[]; a safe copy of the object descriptors
     */
    public ObjectDescriptor[] getObjectDescriptors()
    {
        return this.objectDescriptors.clone();
    }

    /**
     * Retrieve the name of one element in the Object array.
     * @param index int; index of the element in the Object array (must be 0 if this MetaData object is not set up to validate
     *            an array of Object)
     * @return String; name of the argument
     */
    public String getFieldName(final int index)
    {
        return getObjectDescriptor(index).getName();
    }

    /**
     * Retrieve the description of one element in the Object array.
     * @param index int; index of the element in the Object array (must be 0 if this MetaData object is not set up to validate
     *            an array of Object)
     * @return String; description of the argument
     */
    public String getObjectDescription(final int index)
    {
        return getObjectDescriptor(index).getDescription();
    }

    /**
     * Retrieve the java class of one element in the Object array.
     * @param index int; index of the element in the Object array (must be 0 if this MetaData object is not set up to validate
     *            an array of Object)
     * @return Class&lt;?&gt;; java class of the element
     */
    public Class<?> getObjectClass(final int index)
    {
        return getObjectDescriptor(index).getObjectClass();
    }

    /**
     * Select one of the ObjectDescriptors.
     * @param index int; index of the ObjectDescriptor (must be 0 in case this MetaData object is not set up to validate an
     *            array of Object)
     * @return ObjectDescriptor; the selected ObjectDescriptor
     */
    public ObjectDescriptor getObjectDescriptor(final int index)
    {
        Throw.when(index < 0 || index >= this.objectDescriptors.length, IndexOutOfBoundsException.class,
                "Index < 0 or index >= number of object descriptors");
        return this.objectDescriptors[index];
    }

    /**
     * Verify that an Object array has the prescribed composition.
     * @param objectArray Object[]; the Object array to verify. If the array is supposed to have 0 length, a null pointer is
     *            deemed OK.
     * @throws NullPointerException when the object array is null and the size of the object descriptors array is not 0 or 1
     * @throws IndexOutOfBoundsException when size of the object descriptors array is not equal to the size of the object array
     * @throws ClassCastException when one of the objects is of the wrong class
     */
    public final void verifyComposition(final Object[] objectArray)
    {
        if ((size() == 0 || size() == 1) && objectArray == null)
        {
            return;
        }
        if (this.equals(NO_META_DATA)) // anything goes
        {
            return;
        }
        Throw.whenNull(objectArray, "objectArray may not be null");
        Throw.when(objectArray.length != size(), IndexOutOfBoundsException.class,
                "objectArray for \"%s\" has wrong length (expected %d, got %d)", this.name, size(), objectArray.length);
        for (int index = 0; index < objectArray.length; index++)
        {
            Object object = objectArray[index];
            if ((null != object) && (!(getObjectClass(index).isAssignableFrom(object.getClass()))))
            {
                throw new ClassCastException(String.format("objectArray[%d] (%s) cannot be used for %s", index,
                        objectArray[index], getObjectClass(index).getName()));
            }
        }
    }

    /**
     * Verify that an Object has the prescribed composition. In order for one object to fit the metadata, the array of expected
     * objects needs to have a length of 1.
     * @param object Object; the Object to verify.
     * @throws IndexOutOfBoundsException when size of the object descriptors array is not 1
     * @throws ClassCastException when the object is of the wrong class
     */
    public final void verifyComposition(final Object object)
    {
        Throw.when(this.objectDescriptors.length != 1, IndexOutOfBoundsException.class,
                "Testing single object, but length of the object descriptors array is %d", this.objectDescriptors.length);
        if (this.equals(NO_META_DATA)) // anything goes
        {
            return;
        }
        Class<?> objectClass = getObjectClass(0);
        if (!(objectClass.isAssignableFrom(object.getClass())))
        {
            throw new ClassCastException(String.format("object (%s) cannot be used for %s", object, objectClass.getName()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.description.hashCode();
        result = prime * result + this.name.hashCode();
        result = prime * result + Arrays.hashCode(this.objectDescriptors);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MetaData other = (MetaData) obj;
        if (!this.name.equals(other.name))
            return false;
        if (!this.description.equals(other.description))
            return false;
        if (!Arrays.equals(this.objectDescriptors, other.objectDescriptors))
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "MetaData [name=" + this.name + ", description=" + this.description + ", objectDescriptors="
                + Arrays.toString(this.objectDescriptors) + "]";
    }

}
