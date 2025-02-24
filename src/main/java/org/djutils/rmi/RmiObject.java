package org.djutils.rmi;

import java.net.URL;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.djutils.exceptions.Throw;

/**
 * The RMIObject is an object that registers iteself in the RMI registry using a key by which it can be found. The class creates
 * the RMI registry when it does not exist yet.
 * <p>
 * Copyright (c) 2020-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/license.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RmiObject extends UnicastRemoteObject
{
    /** */
    private static final long serialVersionUID = 20200111L;

    /** pointer to the registry in which the object has been registered to look up other objects. */
    private Registry registry;

    /**
     * Register this object in the RMI registry. When the host has not been specified in the URL, 127.0.0.1 will be used. When
     * the port has not been specified in the URL, the default RMI port 1099 will be used. When the RMI registry does not exist
     * yet, it will be created, but <b>only</b> on the local host. Remote creation of a registry on another computer is not
     * possible. Any attempt to do so will cause an AccessException to be fired.
     * @param registryURL the URL of the registry, e.g., "http://localhost:1099" or "http://130.161.185.14:28452"
     * @param bindingKey the key under which this object will be bound in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when registryURL or bindingKey is null
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public RmiObject(final URL registryURL, final String bindingKey) throws RemoteException, AlreadyBoundException
    {
        Throw.whenNull(registryURL, "registryURL cannot be null");
        Throw.whenNull(bindingKey, "bindingKey cannot be null");
        String host = registryURL.getHost() == null ? "127.0.0.1" : registryURL.getHost();
        int port = registryURL.getPort() == -1 ? 1099 : registryURL.getPort();
        register(host, port, bindingKey);
    }

    /**
     * Register this object in the RMI registry. When the RMI registry does not exist yet, it will be created, but <b>only</b>
     * on the local host. Remote creation of a registry on another computer is not possible. Any attempt to do so will cause an
     * AccessException to be fired.
     * @param host the host where the RMI registry resides or will be created. Creation is only possible on localhost.
     * @param port the port where the RMI registry can be found or will be created
     * @param bindingKey the key under which this object will be bound in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when host, path, or bindingKey is null
     * @throws IllegalArgumentException when port &lt; 0 or port &gt; 65535
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public RmiObject(final String host, final int port, final String bindingKey) throws RemoteException, AlreadyBoundException
    {
        register(host, port, bindingKey);
    }

    /**
     * Register this object in the RMI registry. When the RMI registry does not exist yet, it will be created, but <b>only</b>
     * on the local host. Remote creation of a registry on another computer is not possible. Any attempt to do so will cause an
     * AccessException to be fired.
     * @param host the host where the RMI registry resides or will be created. Creation is only possible on localhost.
     * @param port the port where the RMI registry can be found or will be created
     * @param bindingKey the key under which this object will be bound in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    protected void register(final String host, final int port, final String bindingKey)
            throws RemoteException, AlreadyBoundException
    {
        Throw.whenNull(bindingKey, "bindingKey cannot be null");
        this.registry = RmiRegistry.getRegistry(host, port);
        RmiRegistry.bind(this.registry, bindingKey, this);
    }

    /**
     * Returns the registry in which this object has been bound, e.g., to look up other objects in the registry.
     * @return the registry in which this object has been bound
     * @throws RemoteException on network error
     */
    public Registry getRegistry() throws RemoteException
    {
        return this.registry;
    }

}
