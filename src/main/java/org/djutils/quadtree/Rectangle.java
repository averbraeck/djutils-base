package org.djutils.quadtree;

import java.io.Serializable;

import org.djutils.exceptions.Throw;

/**
 * Rectangle defines an area bounded by a lower and left edges (inclusive) and a upper and right edges (not inclusive). The
 * boundary values are stored in the object; unlike the Rectangle2D class where the width and height are stored and to the left
 * and bottom. Doing it this way absolutely ensures that we can make a grid of rectangles that have no ULP width gaps.
 * Additionally, this Rectangle object is immutable. Finally, there is no annoying name collision with the java.lang.Double
 * class.<br>
 * <br>
 * Copyright (c) 2020-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class Rectangle implements Serializable
{
    /** ... */
    private static final long serialVersionUID = 20200904L;

    /** Left boundary (inclusive). */
    private final double left;

    /** Bottom boundary (inclusive). */
    private final double bottom;

    /** Right boundary (not inclusive). */
    private final double right;

    /** Top boundary (not inclusive). */
    private final double top;

    /**
     * Construct a new Rectangle; all arguments are checked for having sensible values.
     * @param left double; the left boundary (inclusive)
     * @param bottom double; the bottom boundary (inclusive)
     * @param right double; the right boundary (not inclusive)
     * @param top double; the top boundary (not inclusive)
     * @param check boolean; if true; the values are checked for making sense
     */
    public Rectangle(final double left, final double bottom, final double right, final double top, final boolean check)
    {
        this(left, bottom, right, top);
        if (check)
        {
            Throw.when(Double.isNaN(left), IllegalArgumentException.class, "The value of left may not be NaN");
            Throw.when(Double.isNaN(bottom), IllegalArgumentException.class, "The value of bottom may not be NaN");
            Throw.when(Double.isNaN(right), IllegalArgumentException.class, "The value of right may not be NaN");
            Throw.when(Double.isNaN(top), IllegalArgumentException.class, "The value of top may not be NaN");
            Throw.when(left > right, IllegalArgumentException.class, "The value of left may not exceed the value of right");
            Throw.when(bottom > top, IllegalArgumentException.class, "The value of bottom may not exceed the value of top");
        }
    }

    /**
     * Construct a new Rectangle without checking the arguments for making sense.
     * @param left double; the left boundary (inclusive)
     * @param bottom double; the bottom boundary (inclusive)
     * @param right double; the right boundary (not inclusive)
     * @param top double; the top boundary (not inclusive)
     */
    public Rectangle(final double left, final double bottom, final double right, final double top)
    {
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
    }

    /**
     * Retrieve the left boundary value.
     * @return double; the left boundary value
     */
    public double getLeft()
    {
        return this.left;
    }

    /**
     * Retrieve the bottom boundary value.
     * @return double; the bottom boundary value
     */
    public double getBottom()
    {
        return this.bottom;
    }

    /**
     * Retrieve the right boundary value.
     * @return double; the right boundary value
     */
    public double getRight()
    {
        return this.right;
    }

    /**
     * Retrieve the top boundary value.
     * @return double; the top boundary value
     */
    public double getTop()
    {
        return this.top;
    }

    /**
     * Return the width of this Rectangle.
     * @return double; the width of this Rectangle
     */
    public double getWidth()
    {
        return this.right - this.left;
    }

    /**
     * Return the height of this Rectangle.
     * @return double; the height of this Rectangle
     */
    public double getHeight()
    {
        return this.top - this.bottom;
    }

    /**
     * Determine if this Rectangle intersects another Rectangle.
     * @param other Rectangle; the other rectangle
     * @return boolean; true if the rectangles intersect, false if the rectangles do not intersect
     */
    public boolean intersects(final Rectangle other)
    {
        return this.left < other.right && this.bottom < other.top && other.left < this.right && other.bottom < this.top;
    }

    /**
     * Determine if this Rectangle contains a point.
     * @param x double; x-coordinate of the point
     * @param y double; y-coordinate of the point
     * @return boolean; true if this Rectangle contains the point; false if this Rectangle does not contain the point
     */
    public boolean contains(final double x, final double y)
    {
        return (this.left <= x && x < this.right && this.bottom <= y && y < this.top);
    }

    /**
     * Determine if this Rectangle contains all points of another Rectangle.
     * @param other Rectangle; the other rectangle
     * @return boolean; true if this Rectangle contains all points of the other Rectangle; false if this Rectangle does not
     *         contain all points of the other Rectangle
     */
    public boolean contains(final Rectangle other)
    {
        return other.left >= this.left && other.right <= this.right && other.bottom >= this.bottom && other.top <= this.top;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Rectangle [LB=" + this.left + "," + this.bottom + ", RT=" + this.right + "," + this.top + "]";
    }

}
