package org.djutils.rmi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.LinkedHashSet;
import java.util.Set;

import org.djutils.exceptions.Try;
import org.djutils.logger.CategoryLogger;
import org.junit.jupiter.api.Test;
import org.pmw.tinylog.Level;

/**
 * RMITest tests the RMIUtils class and the RMIObject class. Note that port 1099 should be opened for 'localhost' for this test.
 * <p>
 * Copyright (c) 2019-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class RMITest
{
    /**
     * Test the RMI utilities for creating and destroying an RMI registry.
     * @throws RemoteException on RMI error
     * @throws AlreadyBoundException when object was already bound (and should not have been there)
     * @throws NotBoundException when object ould not be found in registry (and should have been there)
     */
    @SuppressWarnings("checkstyle:methodlength")
    @Test
    public void testRMIRegistry() throws RemoteException, AlreadyBoundException, NotBoundException
    {
        CategoryLogger.setAllLogLevel(Level.OFF);
        // test making the registry
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.getRegistry(null, 2000);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        for (int port : new int[] {-1, 0, 100000, -10, 65536})
        {
            Try.testFail(new Try.Execution()
            {
                @Override
                public void execute() throws Throwable
                {
                    RmiRegistry.getRegistry("localhost", port);
                }
            }, "should have thrown IllegalArgumentException", IllegalArgumentException.class);
        }
        // the following Try might wait some time for a timeout
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.getRegistry("130.161.3.179", 41099);
            }
        }, "should have thrown RemoteException or AccessException", RemoteException.class);

        // valid registry
        Registry registry = RmiRegistry.getRegistry("localhost", 2000);
        assertNotNull(registry);
        assertEquals(0, registry.list().length);

        // test the bind method
        String key = "testKey";
        RemoteObject remoteObject = new RemoteObject(key);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.bind(null, key, remoteObject);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.bind(registry, null, remoteObject);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.bind(registry, key, null);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.bind(registry, "", remoteObject);
            }
        }, "should have thrown IllegalArgumentPointerException", IllegalArgumentException.class);

        // valid bind
        RmiRegistry.bind(registry, key, remoteObject);
        assertEquals(1, registry.list().length);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.bind(registry, key, remoteObject);
            }
        }, "should have thrown AlreadyBoundException", AlreadyBoundException.class);
        assertEquals(1, registry.list().length);

        // see if the same registry is retrieved the second time
        Registry reg2 = RmiRegistry.getRegistry("localhost", 2000);
        assertNotNull(reg2);
        // reg2 and registry can have a different internal structure (IP address versus localhost)
        // so assertEquals(registry, reg2) cannot be used, but they should still point to the same registry
        assertEquals(1, reg2.list().length); // see if the two registries are indeed 100% the same

        // test the lookup method
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.lookup(null, key);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.lookup(registry, null);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.lookup(registry, "");
            }
        }, "should have thrown IllegalArgumentPointerException", IllegalArgumentException.class);

        // valid lookup
        RemoteObject ro = (RemoteObject) RmiRegistry.lookup(registry, key);
        assertNotNull(ro);
        assertEquals(remoteObject, ro);
        assertEquals(key, ro.getName());

        // test the rebind method
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.rebind(null, key, remoteObject);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.rebind(registry, null, remoteObject);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.rebind(registry, key, null);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.rebind(registry, "", remoteObject);
            }
        }, "should have thrown IllegalArgumentPointerException", IllegalArgumentException.class);

        // valid rebind
        String otherKey = "otherKey";
        RemoteObject otherObject = new RemoteObject(otherKey);
        assertNotEquals(remoteObject, otherObject);
        RmiRegistry.rebind(registry, key, otherObject);
        assertEquals(1, registry.list().length);
        RemoteObject ro2 = (RemoteObject) registry.lookup(key);
        assertNotNull(ro2);
        assertEquals(otherObject, ro2);
        assertNotEquals(key, ro2.getName());

        // test unbind method
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.unbind(null, key);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.unbind(registry, null);
            }
        }, "should have thrown NullPointerException", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.unbind(registry, "");
            }
        }, "should have thrown IllegalArgumentPointerException", IllegalArgumentException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.unbind(registry, otherKey);
            }
        }, "should have thrown NotBoundException", NotBoundException.class);

        // valid rebind / unbind
        RmiRegistry.rebind(registry, otherKey, remoteObject); // rebind should always work
        assertEquals(2, registry.list().length);
        RmiRegistry.unbind(registry, otherKey);
        assertEquals(1, registry.list().length);

        // test closeRegistry
        RmiRegistry.closeRegistry(registry);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.lookup(registry, key);
            }
        }, "should have thrown an Exception");
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.lookup(reg2, key);
            }
        });

        // test that registry is empty after closeRegistry
        // bind and rebind still work -- closeRegistry takes care it does not accept outside calls anymore
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.lookup(registry, key);
            }
        }, "did not get expected exception for lookup()");
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.unbind(registry, key);
            }
        }, "did not get expected exception for unbind()");
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                RmiRegistry.closeRegistry(registry);
            }
        }, "did not get expected exception for closeRegistry()");

        CategoryLogger.setAllLogLevel(Level.INFO);
    }

    /**
     * Test the creation of the RMIRegistry on several versions of localhost (as IP address, using the name, using 127.0.0.1,
     * and using the localhst string).
     * @throws UnknownHostException when the IP address or name of the localhost cannot be retrieved
     */
    @Test
    public void testRMIRegistryLocalHost() throws UnknownHostException
    {
        CategoryLogger.setAllLogLevel(Level.OFF);

        for (String localHostName : new String[] {"localhost", "127.0.0.1", InetAddress.getLocalHost().getHostName(),
                InetAddress.getLocalHost().getHostAddress()})
        {
            Registry registry = null;
            try
            {
                registry = RmiRegistry.getRegistry(localHostName, 2001);
            }
            catch (RemoteException exception)
            {
                fail("Creation of local registry failed for host " + localHostName + ", message was: "
                        + exception.getMessage());
            }

            try
            {
                assertNotNull(registry);
                assertEquals(0, registry.list().length);
                RmiRegistry.closeRegistry(registry);
                final Registry testRegistry = registry;
                Try.testFail(new Try.Execution()
                {
                    @Override
                    public void execute() throws Throwable
                    {
                        RmiRegistry.lookup(testRegistry, "key");
                    }
                }, "should have thrown an Exception");
            }
            catch (RemoteException exception)
            {
                fail("Access or closing of local registry failed for host " + localHostName + ", RemoteException message was: "
                        + exception.getMessage());
            }
        }

        CategoryLogger.setAllLogLevel(Level.INFO);
    }

    /**
     * Test the RMIObject class, and test the RMI communication between objects.
     * @throws AlreadyBoundException when producer is already there
     * @throws RemoteException on RMI error
     * @throws NotBoundException when Listener cnnot find producer
     * @throws MalformedURLException when the test creating RMI regsirt from URL goes wrong
     */
    @Test
    public void testRMIObject() throws RemoteException, AlreadyBoundException, NotBoundException, MalformedURLException
    {
        CategoryLogger.setAllLogLevel(Level.OFF);

        // make the producer
        Producer producer = new Producer();
        assertNotNull(producer);

        // make the listener in another Thread
        final Listener[] listeners = new Listener[1];
        listeners[0] = null;
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Listener listener = null;
                try
                {
                    listener = new Listener("listener");
                }
                catch (RemoteException | AlreadyBoundException | NotBoundException exception)
                {
                    exception.printStackTrace();
                    fail(exception.getMessage());
                }
                assertNotNull(listener);
                listeners[0] = listener;
            }
        });
        thread.start();
        int counter = 0;
        while (listeners[0] == null && counter < 10)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // ignore
            }
            counter++;
        }
        if (counter >= 10)
        {
            fail("Could not start listener");
        }
        Listener listener = listeners[0];
        listener.setLastMessage("");
        producer.fire("test");
        assertEquals("test", listener.getLastMessage());

        // close down
        Registry registry = producer.getRegistry();
        RmiRegistry.closeRegistry(registry);

        // check errors in creating RMIObject
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Producer(null, "producer");
            }
        }, "did not get expected exception", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Producer(new URL("http://localhost:2002"), null);
            }
        }, "did not get expected exception", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Producer(null, 2002, "producer");
            }
        }, "did not get expected exception", NullPointerException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Producer("localhost", -2, "producer");
            }
        }, "did not get expected exception", IllegalArgumentException.class);
        Try.testFail(new Try.Execution()
        {
            @Override
            public void execute() throws Throwable
            {
                new Producer("localhost", 2002, null);
            }
        }, "did not get expected exception", NullPointerException.class);

        // test a few constructions that should work
        producer = new Producer(new URL("http", null, ""), "producer");
        assertNotNull(producer);
        assertNotNull(producer.getRegistry());
        RmiRegistry.closeRegistry(producer.getRegistry());
        sleep(200);
        producer = new Producer(new URL("http://127.0.0.1:2002"), "producer");
        assertNotNull(producer);
        assertNotNull(producer.getRegistry());
        RmiRegistry.closeRegistry(producer.getRegistry());
        sleep(200);
        producer = new Producer(new URL("http://localhost:2002"), "producer");
        assertNotNull(producer);
        assertNotNull(producer.getRegistry());
        RmiRegistry.closeRegistry(producer.getRegistry());
        sleep(200);

        CategoryLogger.setAllLogLevel(Level.INFO);
    }

    /**
     * @param msec the number of msec to sleep.
     */
    private static void sleep(final int msec)
    {
        try
        {
            Thread.sleep(msec);
        }
        catch (Exception exception)
        {
            // Ignore
        }
    }

    /** A test remote object. */
    protected static class RemoteObject implements Remote
    {
        /** */
        private final String name;

        /**
         * Name a remote object.
         * @param name the name of the object
         * @throws RemoteException on a remote exception
         */
        public RemoteObject(final String name) throws RemoteException
        {
            this.name = name;
        }

        /**
         * @return the name of the object
         */
        public String getName()
        {
            return this.name;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
            return result;
        }

        @SuppressWarnings("checkstyle:needbraces")
        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RemoteObject other = (RemoteObject) obj;
            if (this.name == null)
            {
                if (other.name != null)
                    return false;
            }
            else if (!this.name.equals(other.name))
                return false;
            return true;
        }
    }

    /** ListenerInterface for remote listener object. */
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

    /** producerInterface for remote producer object. */
    public interface ProducerInterface extends Remote
    {
        /**
         * Add a listener to the producer.
         * @param listener the listener to add
         * @throws RemoteException on network error
         */
        void addListener(ListenerInterface listener) throws RemoteException;
    }

    /** Producer object. */
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
            super("localhost", 2002, "producer");
        }

        /**
         * Register this object in the RMI registry.
         * @param host the host where the RMI registry resides or will be created.
         * @param port the port where the RMI registry can be found or will be created
         * @param bindingKey the key under which this object will be bound in the RMI registry
         * @throws RemoteException when there is a problem with the RMI registry
         * @throws AlreadyBoundException when there is already another object bound to the bindingKey
         * @throws NullPointerException when host, path, or bindingKey is null
         * @throws IllegalArgumentException when port &lt; 0 or port &gt; 65535
         * @throws AccessException when there is an attempt to create a registry on a remote host
         */
        public Producer(final String host, final int port, final String bindingKey)
                throws RemoteException, AlreadyBoundException
        {
            super(host, port, bindingKey);
        }

        /**
         * Register this object in the RMI registry.
         * @param registryURL the URL of the registry, e.g., "http://localhost:1099"
         * @param bindingKey the key under which this object will be bound in the RMI registry
         * @throws RemoteException when there is a problem with the RMI registry
         * @throws AlreadyBoundException when there is already another object bound to the bindingKey
         * @throws NullPointerException when registryURL or bindingKey is null
         * @throws AccessException when there is an attempt to create a registry on a remote host
         */
        public Producer(final URL registryURL, final String bindingKey) throws RemoteException, AlreadyBoundException
        {
            super(registryURL, bindingKey);
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
                    fail("Notifying listener failed");
                }
            }
        }
    }

    /** Listener object. */
    public class Listener extends RmiObject implements ListenerInterface
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** listener name. */
        private final String listenerName;

        /** last received message. */
        private String lastMessage;

        /**
         * Explicit definition of constructor has to be included to be able to throws RemoteException.
         * @param listenerName the name of the listener in the registry
         * @throws RemoteException on network error
         * @throws AlreadyBoundException on error
         * @throws NotBoundException when producer cannot be found
         */
        public Listener(final String listenerName) throws RemoteException, AlreadyBoundException, NotBoundException
        {
            super("localhost", 2002, listenerName);
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
            this.lastMessage = payload;
        }

        /**
         * @return listenerName
         */
        public String getListenerName()
        {
            return this.listenerName;
        }

        /**
         * @return lastMessage
         */
        public String getLastMessage()
        {
            return this.lastMessage;
        }

        /**
         * @param lastMessage set lastMessage
         */
        public void setLastMessage(final String lastMessage)
        {
            this.lastMessage = lastMessage;
        }
    }

}
