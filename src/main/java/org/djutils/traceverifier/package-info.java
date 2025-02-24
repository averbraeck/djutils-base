/**
 * Create or verify identity of a trace of states of a system. <br>
 * E.g., A simulation that is run with the same initial conditions (including random seed) should behave in a 100% predictable
 * manner. If it does not, there is some source of randomness that probably should be eliminated. This package can help locate
 * where the various runs of the simulation start to deviate from one another. <br>
 * The simulation must be instrumented with calls to the <code>sample</code> method. The sample method either records these
 * samples in a file, or compares these samples with the values that were stored in the file in a previous run. When a
 * difference occurs, the sample method throws an exception. <br>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
package org.djutils.traceverifier;
