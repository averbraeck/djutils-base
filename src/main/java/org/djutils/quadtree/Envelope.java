package org.djutils.quadtree;

/**
 * Interface that must be implemented by objects that need to be stored in a quad tree.
 * <p>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public interface Envelope
{
    /**
     * Determine the bounding box. The result must be constant; it may not vary from one call to the next. Objects implementing
     * this interface should probably cache the result of this method.
     * @return Rectangle; the bounding box
     */
    Rectangle getBoundingRectangle();

}
