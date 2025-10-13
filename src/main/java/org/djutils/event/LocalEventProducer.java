package org.djutils.event;

/**
 * The LocalEventProducer defines the registration and fireEvent operations of an event producer. This behavior includes adding
 * and removing listeners for a specific event type, and firing events for all kinds of different payloads. The EventListener
 * and EventProducer together form a combination of the Publish-Subscribe design pattern and the Observer design pattern using
 * the notify(event) method. See <a href="https://en.wikipedia.org/wiki/Publish-subscribe_pattern" target=
 * "_blank">https://en.wikipedia.org/wiki/Publish-subscribe_pattern</a>,
 * <a href="https://en.wikipedia.org/wiki/Observer_pattern" target="_blank">https://en.wikipedia.org/wiki/Observer_pattern</a>,
 * and <a href="https://howtodoinjava.com/design-patterns/behavioral/observer-design-pattern/" target=
 * "_blank">https://howtodoinjava.com/design-patterns/behavioral/observer-design-pattern/</a>.
 * <p>
 * The EventProducer forms the reference implementation of the publish side of the pub/sub design pattern. The storage of the
 * listeners is done in a Map with the EventType as the key, and a List of References (weak or strong) to the Listeners. Note
 * that the term 'Local' used in the class name is opposed to remote event producers such as the RmiEventProducer.
 * </p>
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
public class LocalEventProducer implements EventProducer
{
    /** The collection of interested listeners. */
    private EventListenerMap eventListenerMap = new EventListenerMap();

    @Override
    public EventListenerMap getEventListenerMap()
    {
        return this.eventListenerMap;
    }

}
