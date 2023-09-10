package org.djutils.rmi;

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.djutils.exceptions.Throw;
import org.djutils.logger.CategoryLogger;

/**
 * RMIUtils contains a number of utilities to help with the RMI registry.
 * <p>
 * Copyright (c) 2019-2023 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public final class RmiRegistry
{
    /**
     * Static class; constructor should not be called.
     */
    private RmiRegistry()
    {
        // static class; should not be called.
    }

    /**
     * Lookup or create the RMI registry. When the RMI registry does not exist yet, it will be created, but <b>only</b> on the
     * local host. Remote creation of a registry on another computer is not possible. Any attempt to do so will cause an
     * AccessException to be fired.
     * @param host String; the host where the RMI registry resides or will be created. Creation is only possible on localhost.
     * @param port int; the port where the RMI registry can be found or will be created
     * @return Registry; the located or created RMI registry
     * @throws RemoteException when there is a problem with locating or creating the RMI registry
     * @throws NullPointerException when host is null
     * @throws IllegalArgumentException when port &le; 0 or port &gt; 65535
     * @throws AccessException when there is an attempt to create a registry on a remote host
     */
    public static Registry getRegistry(final String host, final int port) throws RemoteException
    {
        Throw.whenNull(host, "host cannot be null");
        Throw.when(port <= 0 || port > 65535, IllegalArgumentException.class, "port <= 0 or port > 65535");
        try
        {
            Registry registry = LocateRegistry.getRegistry(host, port);
            boolean validRegistry = registry != null;
            if (validRegistry)
            {
                try
                {
                    // If there is no registry, registry!=null, so we have to test the registry for validity
                    // see https://stackoverflow.com/questions/13779624/java-rmi-check-if-registry-exists
                    // or https://stackoverflow.com/questions/8337215/remote-method-invocation-port-in-use
                    registry.list();
                }
                catch (ConnectException | NoSuchObjectException connectException)
                {
                    validRegistry = false;
                }
            }

            // create the registry when it does not exist yet, but ONLY on localhost
            if (!validRegistry)
            {
                if (!(host.equals("localhost") || host.equals("127.0.0.1")
                        || host.equals(InetAddress.getLocalHost().getHostName())
                        || host.equals(InetAddress.getLocalHost().getHostAddress())))
                {
                    throw new AccessException("Cannot create registry on remote host: " + host);
                }
                registry = LocateRegistry.createRegistry(port);
            }
            return registry;
        }
        catch (Exception exception)
        {
            CategoryLogger.always().error(exception, "RMI exception when locating or creating RMI registry");
            throw new RemoteException("RMI exception when locating or creating RMI registry", exception);
        }
    }

    /**
     * Bind an object in the RMI registry.
     * @param registry Registry; the RMI registry where the object will be bound using the key
     * @param bindingKey String; the key under which the object will be bound in the RMI registry
     * @param object Remote; the object that will be bound
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws AlreadyBoundException when there is already another object bound to the bindingKey
     * @throws NullPointerException when registry, bindingKey or object is null
     * @throws IllegalArgumentException when bindingKey is the empty String
     */
    public static void bind(final Registry registry, final String bindingKey, final Remote object)
            throws RemoteException, AlreadyBoundException
    {
        Throw.whenNull(registry, "registry cannot be null");
        Throw.whenNull(bindingKey, "bindingKey cannot be null");
        Throw.when(bindingKey.length() == 0, IllegalArgumentException.class, "bindingKey cannot be the empty String");
        Throw.whenNull(object, "null object cannot be bound");
        try
        {
            registry.bind(bindingKey, object);
        }
        catch (RemoteException | AlreadyBoundException exception)
        {
            CategoryLogger.always().error(exception, "RMI exception when binding object to the registry");
            throw exception;
        }
    }

    /**
     * Unbind an object from the RMI registry.
     * @param registry Registry; the RMI registry where the object will be bound using the key
     * @param bindingKey String; the key under which the object will be bound in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws NotBoundException when there is no object bound to the bindingKey
     * @throws NullPointerException when registry or bindingKey is null
     * @throws IllegalArgumentException when bindingKey is the empty String
     */
    public static void unbind(final Registry registry, final String bindingKey) throws RemoteException, NotBoundException
    {
        Throw.whenNull(registry, "registry cannot be null");
        Throw.whenNull(bindingKey, "bindingKey cannot be null");
        Throw.when(bindingKey.length() == 0, IllegalArgumentException.class, "bindingKey cannot be the empty String");
        try
        {
            registry.unbind(bindingKey);
        }
        catch (RemoteException | NotBoundException exception)
        {
            CategoryLogger.always().error(exception, "RMI exception when unbinding object from the registry");
            throw exception;
        }
    }

    /**
     * Rebind an object to an existing string in the RMI registry.
     * @param registry Registry; the RMI registry where the object will be bound using the key
     * @param bindingKey String; the (existing) key under which the new object will be bound in the RMI registry
     * @param newObject Remote; the new object that will be bound
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws NullPointerException when registry, bindingKey or newObject is null
     * @throws IllegalArgumentException when bindingKey is the empty String
     */
    public static void rebind(final Registry registry, final String bindingKey, final Remote newObject) throws RemoteException
    {
        Throw.whenNull(registry, "registry cannot be null");
        Throw.whenNull(bindingKey, "bindingKey cannot be null");
        Throw.when(bindingKey.length() == 0, IllegalArgumentException.class, "bindingKey cannot be the empty String");
        Throw.whenNull(newObject, "null object cannot be bound");
        try
        {
            registry.rebind(bindingKey, newObject);
        }
        catch (RemoteException exception)
        {
            CategoryLogger.always().error(exception, "RMI exception when rebinding object to the registry");
            throw exception;
        }
    }

    /**
     * Lookup an object in the RMI registry.
     * @param registry Registry; the RMI registry in which the object will be looked up using the key
     * @param bindingKey String; the key under which the object should be registered in the RMI registry
     * @return Remote; the remote object that bound to the key in the RMI registry
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws NotBoundException when there is no object bound to the bindingKey
     * @throws NullPointerException when registry or bindingKey is null
     * @throws IllegalArgumentException when bindingKey is the empty String
     */
    public static Remote lookup(final Registry registry, final String bindingKey) throws RemoteException, NotBoundException
    {
        Throw.whenNull(registry, "registry cannot be null");
        Throw.whenNull(bindingKey, "bindingKey cannot be null");
        Throw.when(bindingKey.length() == 0, IllegalArgumentException.class, "bindingKey cannot be the empty String");
        try
        {
            return registry.lookup(bindingKey);
        }
        catch (RemoteException | NotBoundException exception)
        {
            CategoryLogger.always().error(exception, "RMI exception when looking up key {} in the RMI registry", bindingKey);
            throw exception;
        }
    }

    /**
     * Unbinds all the objects from the RMI registry and closes the registry.
     * @param registry Registry; the RMI registry that will unbind all the objects and close.
     * @throws RemoteException when there is a problem with the RMI registry
     * @throws NullPointerException when registry is null
     */
    public static void closeRegistry(final Registry registry) throws RemoteException
    {
        Throw.whenNull(registry, "registry cannot be null");
        for (String key : registry.list())
        {
            try
            {
                unbind(registry, key);
            }
            catch (RemoteException | NotBoundException nbe)
            {
                CategoryLogger.always().error(nbe, "RMI exception when unbinding key {} from the RMI registry", key);
            }
        }
        UnicastRemoteObject.unexportObject(registry, true);
    }
}
