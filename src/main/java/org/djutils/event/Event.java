package org.djutils.event;

import java.util.Objects;

import org.djutils.exceptions.Throw;
import org.djutils.metadata.MetaData;

/**
 * The Event class forms the reference implementation for the Event.
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
 */
public class Event
{
    /** The type of the event. */
    private final EventType type;

    /** The content of the event. */
    private final Object content;

    /**
     * Construct a new Event, where compliance with the metadata is verified.
     * @param type the name of the Event.
     * @param content the content of the event
     */
    public Event(final EventType type, final Object content)
    {
        this(type, content, true);
    }

    /**
     * Construct a new Event, with a choice to verify compliance with metadata.
     * @param type the name of the Event.
     * @param content the content of the event
     * @param verifyMetaData whether to verify the compliance with metadata or not
     */
    public Event(final EventType type, final Object content, final boolean verifyMetaData)
    {
        Throw.whenNull(type, "type cannot be null");
        this.type = type;
        this.content = content;
        if (verifyMetaData)
        {
            MetaData metaData = type.getMetaData();
            if (null != metaData)
            {
                if ((null != content) && !(content instanceof Object[]))
                {
                    metaData.verifyComposition(content);
                }
                else
                {
                    metaData.verifyComposition((Object[]) content);
                }
            }
        }
    }

    /**
     * Return the content (payload) of this event.
     * @return the content (payload) of this event
     */
    public final Object getContent()
    {
        return this.content;
    }

    /**
     * Return the type of the event.
     * @return the type of the event
     */
    public EventType getType()
    {
        return this.type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.content, this.type);
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
        Event other = (Event) obj;
        return Objects.equals(this.content, other.content) && Objects.equals(this.type, other.type);
    }

    @Override
    public String toString()
    {
        return "[" + this.getClass().getName() + ";" + this.getType() + ";" + this.getContent() + "]";
    }
}
