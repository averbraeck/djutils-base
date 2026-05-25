package org.djutils.base;

/**
 * Interface for all classes with a {@link Describable#getDescription()} method in addition to an {@link Identifiable#getId()}
 * method.
 * <p>
 * Copyright (c) 2013-2026 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author Alexander Verbraeck
 * @author Peter Knoppers
 * @author Wouter Schakel
 */
public interface Describable extends Identifiable
{

    /**
     * Returns the description.
     * @return description.
     */
    String getDescription();

}
