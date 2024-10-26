package org.djutils.event.reference;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.pmw.tinylog.Logger;

/**
 * A StrongReference class represents a normal pointer relation to a reference. This class is created to complete the
 * java.lang.ref package. This class ensures that references can be used without casting to either an object or a reference.
 * Strong references are not created to be cleaned by the garbage collector. Since they represent normal pointer relations, they
 * are the only ones which might be serialized. This class therefore implements <code>java.io.Serializable</code>
 * <p>
 * Copyright (c) 2002-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the type of the reference
 */
public class StrongReference<T extends Serializable> extends Reference<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20191230L;

    /** the referent. */
    private final transient T referent;

    /**
     * Creates a new strong reference that refers to the given object. The new reference is not registered with any queue.
     * @param referent T; object the new strong reference will refer to
     */
    public StrongReference(final T referent)
    {
        this.referent = referent;
    }

    @Override
    public final T get()
    {
        return this.referent;
    }

    /**
     * writes a serializable referent to stream.
     * @param out ObjectOutputStream; the output stream
     * @throws IOException on IOException
     */
    private synchronized void writeObject(final ObjectOutputStream out) throws IOException
    {
        out.writeObject(this.get());
    }

    /**
     * reads a serializable method from stream.
     * @param in java.io.ObjectInputStream; the input stream
     * @throws IOException on IOException
     * @throws ClassNotFoundException on ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        try
        {
            Field field = getClass().getDeclaredField("referent");
            field.setAccessible(true);
            field.set(this, in.readObject());
            field.setAccessible(false);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception)
        {
            Logger.error(exception, "Error using ReadObject on StrongReference");
        }
    }
}
