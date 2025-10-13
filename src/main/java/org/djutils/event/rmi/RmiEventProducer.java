package org.djutils.event.rmi;

import java.net.URL;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventListenerMap;
import org.djutils.event.EventProducer;
import org.djutils.event.EventType;
import org.djutils.event.reference.Reference;
import org.djutils.exceptions.Throw;
import org.djutils.rmi.RmiObject;

/**
 * The RmiEventProducer provides a remote implementation of the eventProducer using the RMI protocol.
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
public class RmiEventProducer implements EventProducer, Remote
{
    /** The embedded RmiObject class for the remote firing of events. */
    private final RmiObject rmiObject;

    /** the subscriber list. */
    private final EventListenerMap eventListenerMap;

    /**
     * Create a remote event listener and register the listener in the RMI registry. When the RMI registry does not exist yet,
     * it will be created, but <b>only</b> on the local host. Remote creation of a registry on another computer is not possible.
     * Any attempt to do so will cause an AccessException to be fired.
     * @param host the host where the RMI registry resides or will be created. Creation is only possible on localhost.
     * @param port the port where the RMI registry can be found or will be created
     * @param bindingKey the key under which this object will be bound in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when host, path, or bindingKey is null
     * @throws IllegalArgumentException when port &lt; 0 or port &gt; 65535
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public RmiEventProducer(final String host, final int port, final String bindingKey)
            throws RemoteException, AlreadyBoundException
    {
        this.rmiObject = new RmiObject(host, port, bindingKey);
        this.eventListenerMap = new EventListenerMap();
    }

    /**
     * Create a remote event listener and register the listener in the RMI registry. When the host has not been specified in the
     * URL, 127.0.0.1 will be used. When the port has not been specified in the URL, the default RMI port 1099 will be used.
     * When the RMI registry does not exist yet, it will be created, but <b>only</b> on the local host. Remote creation of a
     * registry on another computer is not possible. Any attempt to do so will cause an AccessException to be fired.
     * @param registryURL the URL of the registry, e.g., "http://localhost:1099" or "http://130.161.185.14:28452"
     * @param bindingKey the key under which this object will be bound in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when registryURL or bindingKey is null
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public RmiEventProducer(final URL registryURL, final String bindingKey) throws RemoteException, AlreadyBoundException
    {
        this.rmiObject = new RmiObject(registryURL, bindingKey);
        this.eventListenerMap = new EventListenerMap();
    }

    @Override
    public void fireEvent(final Event event)
    {
        Throw.whenNull(event, "event may not be null");
        EventListenerMap elm = getEventListenerMap();
        if (elm.containsKey(event.getType()))
        {
            // make a safe copy because of possible removeListener() in notify() method during fireEvent
            List<Reference<EventListener>> listenerList = new ArrayList<>(elm.get(event.getType()));
            for (Reference<EventListener> reference : listenerList)
            {
                EventListener listener = reference.get();
                try
                {
                    if (listener != null)
                    {
                        // The garbage collection has not cleaned the referent
                        fireEvent(listener, event);
                    }
                    else
                    {
                        // The garbage collection cleaned the referent;
                        // there is no need to keep the subscription
                        removeListener(reference, event.getType());
                    }
                }
                catch (RemoteException remoteException)
                {
                    // A network failure prevented the delivery,
                    // subscription is removed.
                    try
                    {
                        removeListener(reference, event.getType());
                    }
                    catch (RemoteException re)
                    {
                        throw new RuntimeException("removeListener failed", re);
                    }
                }
            }
        }
    }

    /**
     * Transmit an event to a listener. This method is a hook method. The default implementation simply invokes the notify on
     * the listener. In specific cases (filtering, storing, queueing, this method can be overwritten.
     * @param listener the listener for this event
     * @param event the event to fire
     * @throws RemoteException on network error
     */
    private void fireEvent(final EventListener listener, final Event event) throws RemoteException
    {
        listener.notify(event);
    }

    /**
     * Remove one reference from the subscription list.
     * @param reference the (strong or weak) reference to remove
     * @param eventType the eventType for which reference must be removed
     * @return true if the reference was removed; otherwise false
     * @throws RemoteException on network error
     */
    private boolean removeListener(final Reference<EventListener> reference, final EventType eventType) throws RemoteException
    {
        Throw.whenNull(reference, "reference may not be null");
        Throw.whenNull(eventType, "eventType may not be null");
        EventListenerMap elm = getEventListenerMap();
        boolean success = false;
        for (Iterator<Reference<EventListener>> i = elm.get(eventType).iterator(); i.hasNext();)
        {
            if (i.next().equals(reference))
            {
                i.remove();
                success = true;
            }
        }
        if (elm.get(eventType).size() == 0)
        {
            elm.remove(eventType);
        }
        return success;
    }

    /**
     * Returns the registry in which this object has been bound, e.g., to look up other objects in the registry.
     * @return the registry in which this object has been bound
     * @throws RemoteException on network error
     */
    public Registry getRegistry() throws RemoteException
    {
        return this.rmiObject.getRegistry();
    }

    @Override
    public EventListenerMap getEventListenerMap()
    {
        return this.eventListenerMap;
    }
}
