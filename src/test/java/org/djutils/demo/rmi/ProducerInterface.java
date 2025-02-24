package org.djutils.demo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * ProducerInterface.java.
 * <p>
 * Copyright (c) 2019-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public interface ProducerInterface extends Remote
{
    /**
     * Add a listener to the producer.
     * @param listener the listener to add
     * @throws RemoteException on network error
     */
    void addListener(ListenerInterface listener) throws RemoteException;

}
