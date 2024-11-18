package org.djutils.metadata;

import java.io.Serializable;

import org.djutils.exceptions.Throw;

/**
 * ObjectDescriptor: wrapper for name, description and class of one object. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class ObjectDescriptor implements Serializable
{
    /** ... */
    private static final long serialVersionUID = 20200417L;

    /** Name. */
    private final String name;

    /** Description. */
    private final String description;

    /** Class. */
    private final Class<?> objectClass;

    /**
     * Construct a new FieldDescription object.
     * @param name name of the object
     * @param description description of the object
     * @param objectClass class of the object
     */
    public ObjectDescriptor(final String name, final String description, final Class<?> objectClass)
    {
        Throw.whenNull(name, "name may not be null");
        Throw.when(name.length() == 0, IllegalArgumentException.class, "empty name is not allowed");
        Throw.whenNull(description, "description may not be null");
        Throw.whenNull(objectClass, "objectClass may not be null");
        this.name = name;
        this.description = description;
        this.objectClass = objectClass;
    }

    /**
     * Retrieve the name of the object.
     * @return description of the object
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Retrieve the description of the object.
     * @return description of the object
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Retrieve the Class of the object.
     * @return class name of the object
     */
    public Class<?> getObjectClass()
    {
        return this.objectClass;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.description.hashCode();
        result = prime * result + this.name.hashCode();
        result = prime * result + this.objectClass.getName().hashCode();
        return result;
    }

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
        ObjectDescriptor other = (ObjectDescriptor) obj;
        if (!this.description.equals(other.description))
            return false;
        if (!this.name.equals(other.name))
            return false;
        if (!this.objectClass.equals(other.objectClass))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ObjectDescriptor [name=" + this.name + ", description=" + this.description + ", objectClass=" + this.objectClass
                + "]";
    }

}
