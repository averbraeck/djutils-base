package org.djutils.demo.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import org.djutils.rmi.RmiObject;
import org.djutils.rmi.RmiRegistry;

/**
 * Producer.java.
 * <p>
 * Copyright (c) 2019-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class Producer extends RmiObject implements ProducerInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** */
    private Set<ListenerInterface> listeners = new LinkedHashSet<>();

    /**
     * Register the producer on the localhost RMI registry at the default port.
     * @throws RemoteException on network error
     * @throws AlreadyBoundException on error
     */
    public Producer() throws RemoteException, AlreadyBoundException
    {
        super("localhost", 1099, "producer");
    }

    @Override
    public void addListener(final ListenerInterface listener) throws RemoteException
    {
        this.listeners.add(listener);
    }

    /**
     * Fire a message.
     * @param payload String the payload to send to the listener
     * @throws RemoteException on network error
     */
    protected void fire(final String payload) throws RemoteException
    {
        for (ListenerInterface listener : this.listeners)
        {
            try
            {
                listener.notify(payload);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * create a small interactive application.
     */
    protected void commands()
    {
        char c = 'n';
        Scanner s = new Scanner(System.in);
        while (c != 'x')
        {
            System.out.println("\nCommands: 'x' = exit; 'l' = list listeners; 's string' = send string to listeners");
            String str = s.nextLine();
            c = str.charAt(0);

            if (c == 's')
            {
                String[] cmd = str.split(" ", 2);
                if (cmd.length != 2)
                {
                    System.err.println("not a legal command");
                    continue;
                }
                try
                {
                    fire(cmd[1]);
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                }
                continue;
            }

            if (c == 'x')
            {
                try
                {
                    fire("x");
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                }
                continue;
            }

            if (c == 'l')
            {
                try
                {
                    for (ListenerInterface listener : this.listeners)
                    {
                        System.out.println(listener.getName());
                        continue;
                    }
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                    continue;
                }
            }
        }
        s.close();
        try
        {
            RmiRegistry.closeRegistry(getRegistry());
        }
        catch (Exception re)
        {
            System.err.println(re.getMessage());
        }
    }

    /**
     * System.exit(...) calls help to shut down RMI / socket thread that might wait for a timeout.
     * @param args nothing needed
     */
    public static void main(final String[] args)
    {
        try
        {
            Producer producer = new Producer();
            producer.commands();

        }
        catch (RemoteException | AlreadyBoundException exception)
        {
            exception.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }
}
