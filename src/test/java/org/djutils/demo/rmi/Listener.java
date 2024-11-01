package org.djutils.demo.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.djutils.rmi.RmiObject;
import org.djutils.rmi.RmiRegistry;

/**
 * Listener.java.
 * <p>
 * Copyright (c) 2019-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Listener extends RmiObject implements ListenerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    private final String listenerName;

    /**
     * Explicit definition of constructor has to be included to be able to throws RemoteException.
     * @param listenerName the name of the listener in the registry
     * @throws RemoteException on network error
     * @throws AlreadyBoundException on error
     * @throws NotBoundException when producer cannot be found
     */
    public Listener(final String listenerName) throws RemoteException, AlreadyBoundException, NotBoundException
    {
        super("localhost", 1099, listenerName);
        this.listenerName = listenerName;
        ProducerInterface producer = (ProducerInterface) RmiRegistry.lookup(getRegistry(), "producer");
        producer.addListener(this);
    }

    @Override
    public String getName() throws RemoteException
    {
        return this.listenerName;
    }

    @Override
    public void notify(final String payload) throws RemoteException
    {
        System.out.println("got payload: " + payload);
        if (payload.equals("x"))
        {
            // leave the program after having returned from the method.
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(100); // wail 100 msec
                        getRegistry().unbind(Listener.this.getListenerName());
                    }
                    catch (NotBoundException | RemoteException | InterruptedException exception)
                    {
                        // ignore
                    }
                    System.exit(0);
                }
            }).start();
        }
    }

    /**
     * @return listenerName
     */
    public String getListenerName()
    {
        return this.listenerName;
    }

    /**
     * @param args args name of the listener
     */
    public static void main(final String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Use as java -jar Listener.jar listenerName");
            System.exit(-1);
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    new Listener(args[0]);
                }
                catch (RemoteException | AlreadyBoundException | NotBoundException exception)
                {
                    exception.printStackTrace();
                    System.exit(-1);
                }
            }
        }).start();
        while (true)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException exception)
            {
                // ignore
            }
        }
    }

}
