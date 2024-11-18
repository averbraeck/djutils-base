package org.djutils.event;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The EventListener interface creates a callback method for publishers to inform their clients. The EventListener and
 * EventProducer together form a combination of the Publish-Subscribe design pattern and the Observer design pattern using the
 * notify(event) method. See <a href="https://en.wikipedia.org/wiki/Publish-subscribe_pattern" target=
 * "_blank">https://en.wikipedia.org/wiki/Publish-subscribe_pattern</a>,
 * <a href="https://en.wikipedia.org/wiki/Observer_pattern" target="_blank">https://en.wikipedia.org/wiki/Observer_pattern</a>,
 * and <a href="https://howtodoinjava.com/design-patterns/behavioral/observer-design-pattern/" target=
 * "_blank">https://howtodoinjava.com/design-patterns/behavioral/observer-design-pattern/</a>.
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
 */
public interface EventListener extends java.util.EventListener, Serializable, Remote
{
    /**
     * Notifies the event listener of an event. This operation forms the callback method of the asynchronous communication
     * protocol expressed in the event package.
     * @param event the event which is sent to the listener
     * @throws RemoteException If a network connection failure occurs.
     */
    void notify(Event event) throws RemoteException;

}
