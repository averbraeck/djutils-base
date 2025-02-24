package org.djutils.event;

import java.io.Serializable;

import org.djutils.exceptions.Throw;

/**
 * The TimedEvent is the reference implementation for a timed event. Because events are often sent over the network, the
 * interface demands that the event, content and timestamp are serializable. It is the repsonsibility of the programmer, though,
 * that the <b>fields</b> of the content and timestamp are serializable as well.
 * <p>
 * Copyright (c) 2002-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. This class was
 * originally part of the DSOL project, see <a href="https://simulation.tudelft.nl/dsol/manual" target="_blank">
 * https://simulation.tudelft.nl/dsol/manual</a>.
 * </p>
 * @author <a href="https://www.linkedin.com/in/peterhmjacobs">Peter Jacobs </a>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the Comparable type that represents time
 */
public class TimedEvent<T extends Comparable<T> & Serializable> extends Event implements Comparable<TimedEvent<T>>
{
    /** The default serial version UID for serializable classes. */
    private static final long serialVersionUID = 20140826L;

    /** Time stamp of this TimedEvent. */
    private final T timeStamp;

    /**
     * Construct a new timed event, where compliance with the metadata is verified.
     * @param type the eventType of the event.
     * @param content the content of the event.
     * @param timeStamp the timeStamp.
     */
    public TimedEvent(final EventType type, final Serializable content, final T timeStamp)
    {
        this(type, content, timeStamp, true);
    }

    /**
     * Construct a new timed event, with a choice to verify compliance with metadata.
     * @param type the eventType of the event.
     * @param content the content of the event.
     * @param timeStamp the timeStamp.
     * @param verifyMetaData whether to verify the compliance with metadata or not
     */
    public TimedEvent(final EventType type, final Serializable content, final T timeStamp, final boolean verifyMetaData)
    {
        super(type, content, verifyMetaData);
        Throw.whenNull(timeStamp, "timeStamp cannot be null");
        this.timeStamp = timeStamp;
    }

    /**
     * Returns the timeStamp of this event.
     * @return the time stamp
     */
    public T getTimeStamp()
    {
        return this.timeStamp;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.timeStamp == null) ? 0 : this.timeStamp.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        TimedEvent<?> other = (TimedEvent<?>) obj;
        if (this.timeStamp == null)
        {
            if (other.timeStamp != null)
                return false;
        }
        else if (!this.timeStamp.equals(other.timeStamp))
            return false;
        return true;
    }

    @Override
    public int compareTo(final TimedEvent<T> o)
    {
        return this.timeStamp.compareTo(o.getTimeStamp());
    }

    @Override
    public String toString()
    {
        return "[" + this.getClass().getName() + ";" + this.getType() + ";" + this.getContent() + ";" + this.getTimeStamp()
                + "]";
    }

}
