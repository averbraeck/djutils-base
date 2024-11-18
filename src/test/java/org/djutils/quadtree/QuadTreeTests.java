package org.djutils.quadtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

/**
 * QuadTreeTests.java. <br>
 * <br>
 * Copyright (c) 2020-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>. <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public class QuadTreeTests
{

    /**
     * Test the basic set operations.
     */
    @Test
    public void testBasics()
    {
        QuadTree<Bounded> qt = new QuadTree<>(10, 10.0, 0, 0, 100, 100);
        assertTrue(qt.toString().startsWith("QuadTree"), "The toString method returns something descriptive");
        // System.out.println(qt);
        assertEquals(0, qt.size(), "quad tree is empty");
        assertTrue(qt.isEmpty(), "quad tree is empty");
        Bounded[] b = new Bounded[324];
        for (int i = 0; i < b.length; i++)
        {
            double cX = (i * 5) % 90 + 7;
            double cY = i / 18 * 5 + 6;
            Point2D.Double[] points = new Point2D.Double[5];
            points[0] = new Point2D.Double(cX - 6, cY);
            points[1] = new Point2D.Double(cX - 4, cY + 4);
            points[2] = new Point2D.Double(cX, cY + 6);
            points[3] = new Point2D.Double(cX + 4, cY + 4);
            points[4] = new Point2D.Double(cX, cY + 6);
            b[i] = new Bounded(points, "Test object " + i);
            // System.out.println("Created shape " + b[i]);
            assertFalse(qt.contains(b[i]), "shape is not yet in quad tree");
            assertTrue(qt.add(b[i]), "adding the shape modifies the quad tree");
            assertEquals(i + 1, qt.size(), "quad tree now contains one more item");
            if (!qt.contains(b[i]))
            {
                qt.contains(b[i]);
            }
            assertTrue(qt.contains(b[i]), "shape was added");
            assertFalse(qt.isEmpty(), "quad tree is not empty");
        }
        for (int i = 0; i < b.length; i++)
        {
            assertTrue(qt.contains(b[i]), "shape is in the quad tree");
            assertFalse(qt.add(b[i]), "Adding shape " + i + " again does not modify the quad tree");
        }
        assertEquals(b.length, qt.size(), "quad tree contains the expected number of shapes");
        // System.out.println(qt.toString(5));
        // System.out.println(qt.dump(" "));
        boolean[] seen = new boolean[b.length];
        int totalSeen = 0;
        for (Bounded bounded : qt)
        {
            for (int i = 0; i < b.length; i++)
            {
                if (bounded.equals(b[i]))
                {
                    assertFalse(seen[i], "each one is returned only once");
                    seen[i] = true;
                    totalSeen++;
                }
            }
        }
        assertEquals(b.length, totalSeen, "all elements have been returned");
        for (int i = 0; i < b.length; i++)
        {
            assertFalse(qt.add(b[i]), "Element can not be added again");
        }
        assertEquals(b.length, qt.size(), "quad tree contains the expected number of shapes");

        for (int i = -10; i < 100; i++)
        {
            for (int j = -10; j < 100; j++)
            {
                seen = new boolean[b.length];
                Rectangle area = new Rectangle(i, j, i + 20, j + 20);
                Iterator<Bounded> iterator = qt.iterator(area);
                // int found = 0;
                while (iterator.hasNext())
                {
                    Bounded bounded = iterator.next();
                    // found++;
                    for (int k = 0; k < b.length; k++)
                    {
                        if (bounded.equals(b[k]))
                        {
                            assertFalse(seen[k], "each one is returned only once");
                            seen[k] = true;
                        }
                    }
                }
                // System.out.println("Found " + found + " shapes in " + area);
                for (int k = 0; k < b.length; k++)
                {
                    if (b[k].intersects(area))
                    {
                        assertTrue(seen[k], "Intersecting object was found");
                    }
                }
            }
        }

        for (int i = 0; i < b.length; i++)
        {
            assertTrue(qt.remove(b[i]), "quad tree changes on removal of object");
            assertEquals(b.length - i - 1, qt.size(), "size of quad tree is now one less");
            assertFalse(qt.contains(b[i]), "shape is no longer in quad tree");
        }
        assertEquals(0, qt.size(), "quad tree is empty when all shapes have been removed");
        assertTrue(qt.isEmpty(), "quad tree is empty");
    }

}

/**
 * Simple object that implements BoundingBoxed.
 */
class Bounded implements Envelope
{
    /** The points that make up the 2D shape. */
    private final Point2D.Double[] points;

    /** The bounding rectangle. */
    private final Rectangle boundingRectangle;

    /** Id of this object. */
    private final String id;

    /**
     * Construct a new Bounded object for testing.
     * @param points Point2D.Double[]; array of Point2D.Double
     * @param id id of the test object
     */
    Bounded(final Point2D.Double[] points, final String id)
    {
        this.points = points;
        double left = java.lang.Double.MAX_VALUE;
        double bottom = java.lang.Double.MAX_VALUE;
        double right = java.lang.Double.MIN_VALUE;
        double top = java.lang.Double.MIN_VALUE;
        for (Point2D.Double point : points)
        {
            left = Math.min(left, point.x);
            bottom = Math.min(bottom, point.y);
            right = Math.max(right, point.x);
            top = Math.max(top, point.y);
        }
        this.boundingRectangle = new Rectangle(left, bottom, right + Math.ulp(right), top + Math.ulp(top));
        this.id = id;
    }

    @Override
    public Rectangle getBoundingRectangle()
    {
        return this.boundingRectangle;
    }

    /**
     * Determine if this object intersects a given Rectangle.
     * @param rectangle the rectangle
     * @return true if this object intersects the given Rectangle; false if this object does not intersect the given
     *         Rectangle
     */
    public boolean intersects(final Rectangle rectangle)
    {
        for (Point2D.Double point : this.points)
        {
            if (rectangle.contains(point.x, point.y))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Bounded [id=" + this.id + ", boundingRectangle=" + this.boundingRectangle + ", points="
                + Arrays.toString(this.points) + "]";
    }

}
