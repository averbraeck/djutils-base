package org.djutils.quadtree;

/**
 * Interface that must be implemented by objects that need to be stored in a quad tree.
 * <p>
 * Copyright (c) 2019-2025 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public interface Envelope
{
    /**
     * Determine the bounding box. The result must be constant; it may not vary from one call to the next. Objects implementing
     * this interface should probably cache the result of this method.
     * @return the bounding box
     */
    Rectangle getBoundingRectangle();

}
