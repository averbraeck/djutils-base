package org.djutils.demo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * ListenreInterface.java.
 * <p>
 * Copyright (c) 2019-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ListenerInterface extends Remote
{
    /**
     * Return the name under which the listenerInterface is registered.
     * @return the name
     * @throws RemoteException on network error
     */
    String getName() throws RemoteException;

    /**
     * Notify the listener of a message.
     * @param payload the message
     * @throws RemoteException on network error
     */
    void notify(String payload) throws RemoteException;
}
