package org.djutils.traceverifier;

/**
 * TraceVerifierException is the exception thrown when the expected string differs from the actual string. <br>
 * <br>
 * Copyright (c) 2020-2025 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class TraceVerifierException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 20200822L;

    /**
     * Create a specific RuntimeException that shows the difference between the sample and the expected sample.
     * @param message the message of the exception, showing the difference between the sample and the expected sample
     */
    public TraceVerifierException(final String message)
    {
        super(message);
    }

}
