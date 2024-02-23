/**
 * Provides classes and interfaces for asynchronous communication. The event package is designed around four main classes:
 * <ul>
 * <li>The EventListener defining callback mechanism for a listener.</li>
 * <li>The EventProducer defining registration capabilities.</li>
 * <li>The Event defining the events which are sent by a producer and received by a listener.</li>
 * <li>The EventType allowing for strongly typed Events</li>
 * </ul>
 * In this package, two aspects are of crucial importance. Operations defined in the classes are prepared for distribution.
 * The event.remote package extends all listeners and producers to throw the <code>RemoteException</code> on network problems.
 * The second aspect is concurrency. The classes providing a reference implementation of these interfaces are designed for
 * multi-threaded deployment.
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
package org.djutils.event;
