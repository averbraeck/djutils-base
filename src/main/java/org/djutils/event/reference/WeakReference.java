package org.djutils.event.reference;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.pmw.tinylog.Logger;

/**
 * A WeakReference. The WeakReference extends the <code>java.lang.ref.WeakReference</code> and besides implementing the
 * Reference interface no changes are defined.
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
public class WeakReference<T extends Serializable> extends Reference<T>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20140830L;

    /** the wrapped reference to the referent. */
    private final transient java.lang.ref.WeakReference<T> referent;

    /**
     * Creates a new weak reference that refers to the given object. The new reference is not registered with any queue.
     * @param referent T; object the new weak reference will refer to
     */
    public WeakReference(final T referent)
    {
        this.referent = new java.lang.ref.WeakReference<T>(referent);
    }

    @Override
    public final T get()
    {
        return this.referent.get();
    }

    /**
     * Write a serializable method to a stream.
     * @param out ObjectOutputStream; the output stream
     * @throws IOException on IOException
     */
    private synchronized void writeObject(final ObjectOutputStream out) throws IOException
    {
        out.writeObject(this.get());
    }

    /**
     * Read a serializable method from a stream.
     * @param in java.io.ObjectInputStream; the input stream
     * @throws IOException on IOException
     * @throws ClassNotFoundException on ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        try
        {
            Field field = getClass().getDeclaredField("referent");
            field.setAccessible(true);
            field.set(this, new java.lang.ref.WeakReference<T>((T) in.readObject()));
            field.setAccessible(false);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception)
        {
            Logger.error(exception, "Error using ReadObject on StrongReference");
        }
    }
}
