package org.djutils.quadtree;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.djutils.exceptions.Throw;

/**
 * Quad tree for 2D objects. For now, this implementation needs an ultimate outer bounding box. No part of any 2D string object
 * may exceed that bounding box. A link to each stored 2D object will be stored in each sub-box that it intersects.
 * <p>
 * Copyright (c) 2019-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 * @param <T> Type of object stored in this quad tree
 */
public class QuadTree<T extends Envelope> implements Collection<T>, Serializable
{
    /** ... */
    private static final long serialVersionUID = 20200904L;

    /** Maximum number of payload objects in one cell. */
    private final int maximumLoad;

    /** Minimum width and height of a SubTree bounding box. */
    private final double minimumSize;

    /** The actual top level quad tree. */
    private final SubTree<T> tree;

    /** Count the number of sub trees created. */
    private int totalSubTrees = 0;

    /**
     * Create a new QuadTree object (or a sub-tree).
     * @param maximumLoad number of elements at any level that warrants investigating if the tree can be re-balanced
     * @param minimumSize minimum width or height of a sub tree Rectangle (smaller sub tree are never created)
     * @param left the lowest X-coordinate that is allowed (inclusive)
     * @param bottom the lowest Y-coordinate that is allowed (inclusive)
     * @param right the highest X-coordinate that is allowed (exclusive)
     * @param top the highest Y-coordinate that is allowed (exclusive)
     */
    public QuadTree(final int maximumLoad, final double minimumSize, final double left, final double bottom, final double right,
            final double top)
    {
        Throw.when(left >= right, IllegalArgumentException.class, "left (%f) must be less than right (%f)", left, right);
        Throw.when(bottom >= top, IllegalArgumentException.class, "bottom (%f) must be less than top (%f)", bottom, top);
        this.maximumLoad = maximumLoad;
        this.minimumSize = minimumSize;
        this.tree = new SubTree<T>(this, new Rectangle(left, bottom, right, top));
    }

    /**
     * Return the number of objects at which it is time to try to re-balance.
     * @return the number of objects at which it is time to try to re-balance
     */
    public int getMaxLoad()
    {
        return this.maximumLoad;
    }

    /**
     * Return the minimum sub-tree rectangle size.
     * @return the minimum sub-tree rectangle size
     */
    public double getMinimumSize()
    {
        return this.minimumSize;
    }

    @Override
    public int size()
    {
        return this.tree.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.tree.size() == 0;
    }

    @Override
    public boolean contains(final Object o)
    {
        if (!(o instanceof Envelope))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        T t = (T) o;
        return this.tree.recursiveContains(new RectangleAndPayload<T>(t.getBoundingRectangle(), t));
    }

    @Override
    public Iterator<T> iterator()
    {
        return iterator(this.tree.getBoundingBox());
    }

    /**
     * Find all elements intersecting a given bounding box. This iterator cannot be used to remove elements, but the remove
     * method can be safely called while the iterator is active.
     * @param searchArea the bounding box
     * @return iterator that returns all elements that intersect the given bounding box
     */
    public Iterator<T> iterator(final Rectangle searchArea)
    {
        return collect(searchArea).iterator();
    }

    @Override
    public Object[] toArray()
    {
        return collect(this.tree.getBoundingBox()).toArray();
    }

    @SuppressWarnings("hiding")
    @Override
    public <T> T[] toArray(final T[] a)
    {
        return collect(this.tree.getBoundingBox()).toArray(a);
    }

    /**
     * Construct a set containing all payload elements within a specified area.
     * @param searchArea the search area
     * @return the set containing all payload elements whose bounding areas intersect the specified area
     */
    private Set<T> collect(final Rectangle searchArea)
    {
        Iterator<RectangleAndPayload<T>> iterator = this.tree.recursiveCollect(searchArea).iterator();
        Set<T> result = new LinkedHashSet<>();
        while (iterator.hasNext())
        {
            result.add(iterator.next().getPayload());
        }
        return result;
    }

    @Override
    public boolean add(final T e)
    {
        return this.tree.add(new RectangleAndPayload<T>(e.getBoundingRectangle(), e));
    }

    @Override
    public boolean remove(final Object o)
    {
        if (!(o instanceof Envelope))
        {
            return false;
        }
        @SuppressWarnings("unchecked")
        T t = (T) o;
        return this.tree.remove(new RectangleAndPayload<T>(t.getBoundingRectangle(), t));
    }

    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return collect(this.tree.getBoundingBox()).containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c)
    {
        boolean result = false;
        for (T t : c)
        {
            result |= add(t);
        }
        return result;
    }

    @Override
    public boolean removeAll(final Collection<?> c)
    {
        boolean result = false;
        for (Object o : c)
        {
            result |= remove(o);
        }
        return result;
    }

    @Override
    public boolean retainAll(final Collection<?> c)
    {
        throw new RuntimeException("Not (yet) implemented");
    }

    @Override
    public void clear()
    {
        this.tree.clear();
    }

    /**
     * Increment the number of sub trees created.
     */
    void incrementSubTreeCount()
    {
        this.totalSubTrees++;
    }

    /**
     * Return the total number of sub trees.
     * @return the total number of sub trees
     */
    public int getSubTreeCount()
    {
        return this.totalSubTrees;
    }

    @Override
    public String toString()
    {
        return "QuadTree [maximumLoad=" + this.maximumLoad + ", minimumSize=" + this.minimumSize + ", tree=" + this.tree + "]";
    }

    /**
     * Make a textual description of this quad tree drilling down to the prescribed depth.
     * @param expandDepth maximum depth to descend
     * @return textual description of this quad tree
     */
    public String toString(final int expandDepth)
    {
        return "QuadTree [maximumLoad=" + this.maximumLoad + ", minimumSize=" + this.minimumSize + ", tree="
                + this.tree.toString(expandDepth) + "]";
    }

    /**
     * Dump a quad tree.
     * @param indent prefix for each output line
     * @return textual description of this quad tree.
     */
    public String dump(final String indent)
    {
        return this.tree.dump(indent);
    }

    /**
     * Sub tree of a quad tree.
     * @param <T> Type of object stored in this quad tree
     */
    @SuppressWarnings("hiding")
    class SubTree<T extends Envelope> implements Serializable
    {
        /** ... */
        private static final long serialVersionUID = 20200904L;

        /** Root of the quad tree. */
        private final QuadTree<T> root;

        /** Bounding box of this quad tree. */
        private final Rectangle boundingBox;

        /** Current number of objects in this quad tree. Includes all children, counting each object exactly once. */
        private int size = 0;

        /** If the four children have been allocated, this array will be non-null and contain the four children. */
        private SubTree<T>[] children = null;

        /** Elements stored at this node. */
        private Set<RectangleAndPayload<T>> elements = null;

        /**
         * Construct a new sub tree.
         * @param root the root
         * @param boundingBox the bounding box of the new sub tree
         */
        SubTree(final QuadTree<T> root, final Rectangle boundingBox)
        {
            this.root = root;
            this.boundingBox = boundingBox;
            root.incrementSubTreeCount();
        }

        /**
         * Retrieve the bounding box of this sub tree.
         * @return the bounding box of this sub tree
         */
        public final Rectangle getBoundingBox()
        {
            return this.boundingBox;
        }

        /**
         * Return the number of objects stored in and under this SubTree.
         * @return the number of objects stored in and under this SubTree
         */
        public int size()
        {
            return this.size;
        }

        /**
         * Add a RectangleAndPayload to this SubTree.
         * @param e the object to add
         * @return true if this SubTree was changed (object was added); false if this SubTree did not change
         */
        public boolean add(final RectangleAndPayload<T> e)
        {
            if (contains(e))
            {
                return false;
            }
            if (this.elements == null)
            {
                this.elements = new LinkedHashSet<>();
            }
            this.elements.add(e);
            this.size++;
            reBalance();
            return true;
        }

        /**
         * Remove a RectangleAndPayload from this SubTree.
         * @param o the object to remove
         * @return true if this SubTree was changed (object was removed); false if this SubTree did not change
         */
        public boolean remove(final RectangleAndPayload<T> o)
        {
            if (this.elements.remove(o))
            {
                this.size--;
                return true; // The object cannot be also present in any of the sub trees
            }
            // Try all of the sub trees
            boolean result = false;
            if (this.children != null)
            {
                Rectangle rectangle = o.getRectangle();
                for (SubTree<T> child : this.children)
                {
                    if (!child.boundingBox.intersects(rectangle))
                    {
                        continue; // This is the time saver
                    }
                    if (child.remove(o))
                    {
                        result = true;
                    }
                }
            }
            if (result)
            {
                this.size--;
            }
            return result;
        }

        /**
         * Delete all objects stored in this SubTree.
         */
        public void clear()
        {
            this.elements.clear();
            this.children = null;
            this.size = 0;
        }

        /**
         * Determine if this SubTree contains a specific object.
         * @param o the object to search
         * @return true if this SubTree contains the object
         */
        public boolean contains(final RectangleAndPayload<T> o)
        {
            if (this.elements == null)
            {
                return false;
            }
            return recursiveContains(o);
        }

        /**
         * Recursively search for a particular object.
         * @param o the object to search for
         * @return true if this quad tree contains the object; false if this quad tree does not contain the object
         */
        boolean recursiveContains(final RectangleAndPayload<T> o)
        {
            if ((!this.boundingBox.intersects(o.getRectangle())) || (this.elements == null))
            {
                return false; // This is the time saver
            }
            for (RectangleAndPayload<T> element : this.elements)
            {
                if (element.equals(o))
                {
                    return true;
                }
            }
            if (this.children == null)
            {
                return false;
            }
            for (SubTree<T> child : this.children)
            {
                if (child.recursiveContains(o))
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * Recursively collect all elements that intersect the given rectangle.
         * @param rectangle the rectangle
         * @return all stored elements that intersect the given rectangle
         */
        public Set<RectangleAndPayload<T>> recursiveCollect(final Rectangle rectangle)
        {
            Set<RectangleAndPayload<T>> result = new LinkedHashSet<>();
            if (!this.boundingBox.intersects(rectangle))
            {
                return result; // This is the time saver
            }
            if (this.elements != null)
            {
                for (RectangleAndPayload<T> element : this.elements)
                {
                    if (element.getRectangle().intersects(rectangle))
                    {
                        result.add(element);
                    }
                }
            }
            if (this.children != null)
            {
                for (SubTree<T> child : this.children)
                {
                    result.addAll(child.recursiveCollect(rectangle));
                }
            }
            return result;
        }

        /**
         * Optimize the distribution of elements at this node and at sub-nodes.
         */
        @SuppressWarnings("unchecked")
        private void reBalance()
        {
            if (this.elements.size() < this.root.getMaxLoad() || this.boundingBox.getWidth() < this.root.getMinimumSize()
                    || this.boundingBox.getHeight() < this.root.getMinimumSize())
            {
                return;
            }
            // Count the number of elements that could be moved down to sub-trees
            double cX = (this.boundingBox.getLeft() + this.boundingBox.getRight()) / 2;
            double cY = (this.boundingBox.getBottom() + this.boundingBox.getTop()) / 2;
            int canMove = 0;
            /*-
            for (T e : this.elements)
            {
                // This criterion is not good
                if (!e.getBoundingRectangle().contains(this.boundingBox))
                {
                    canMove++;
                }
            }
            */
            canMove = this.elements.size();
            if (canMove == 0 || canMove < this.root.getMaxLoad() /* / 2 */ && this.children == null)
            {
                // System.out.println("reBalance: not moving " + canMove + " of " + this.elements.size());
                return;
            }
            // System.out.println("At start of reBalance of " + this.toString(1));
            if (this.children == null)
            {
                this.children = new SubTree[] {
                        new SubTree<T>(this.root,
                                new Rectangle(this.boundingBox.getLeft(), this.boundingBox.getBottom(), cX, cY)),
                        new SubTree<T>(this.root,
                                new Rectangle(cX, this.boundingBox.getBottom(), this.boundingBox.getRight(), cY)),
                        new SubTree<T>(this.root, new Rectangle(this.boundingBox.getLeft(), cY, cX, this.boundingBox.getTop())),
                        new SubTree<T>(this.root,
                                new Rectangle(cX, cY, this.boundingBox.getRight(), this.boundingBox.getTop()))};
            }
            Iterator<RectangleAndPayload<T>> iterator = this.elements.iterator();
            while (iterator.hasNext())
            {
                RectangleAndPayload<T> e = iterator.next();
                if (e.getRectangle().contains(this.boundingBox))
                {
                    continue;
                }
                boolean added = false;
                for (SubTree<T> child : this.children)
                {
                    if (e.getRectangle().intersects(child.boundingBox))
                    {
                        added |= child.add(e);
                    }
                }
                if (added)
                {
                    iterator.remove();
                }
                else
                {
                    System.out.println("ERROR: Could not add " + e + " to any of the children");
                }
            }
            // System.out.println("At end of reBalanceof " + this.toString(1));
        }

        @Override
        public String toString()
        {
            return "SubTree [boundingBox=" + this.boundingBox + ", size=" + this.size + ", children=" + this.children
                    + (this.elements == null ? "elements=null" : ", elements.size=" + this.elements.size()) + "]";
        }

        /**
         * Return a textual representation of this quad tree up to the specified depth.
         * @param expandDepth the maximum depth to expand
         * @return textual representation of this quad tree
         */
        public String toString(final int expandDepth)
        {
            if (expandDepth > 0)
            {
                return "SubTree [boundingBox=" + this.boundingBox + ", size=" + this.size + ", children="
                        + (this.children != null ? "[SW:" + this.children[0].toString(expandDepth - 1) + ", SE:"
                                + this.children[1].toString(expandDepth - 1) + ", NW:"
                                + this.children[2].toString(expandDepth - 1) + ", NE:"
                                + this.children[3].toString(expandDepth - 1) + "]" : "null")
                        + ", elements.size=" + this.elements.size() + "]";
            }
            else
            {
                return toString();
            }
        }

        /**
         * Dump a quad tree.
         * @param indent prefix for each output line
         * @return textual description of this quad tree.
         */
        public String dump(final String indent)
        {
            StringBuilder result = new StringBuilder();
            result.append(indent);
            result.append("SubTree [size=");
            result.append(this.size);
            result.append("] ");
            result.append(this.boundingBox);
            result.append("\n");
            String subIndent = indent + "    ";
            Iterator<RectangleAndPayload<T>> iterator = this.elements.iterator();
            for (int i = 0; i < this.elements.size(); i++)
            {
                result.append(subIndent);
                result.append(i);
                result.append(" ");
                result.append(iterator.next());
                result.append("\n");
            }
            if (this.children != null)
            {
                result.append(subIndent);
                result.append("SW");
                result.append("\n");
                result.append(this.children[0].dump(subIndent));
                result.append(subIndent);
                result.append("SE");
                result.append("\n");
                result.append(this.children[1].dump(subIndent));
                result.append(subIndent);
                result.append("NW");
                result.append("\n");
                result.append(this.children[2].dump(subIndent));
                result.append(subIndent);
                result.append("NE");
                result.append("\n");
                result.append(this.children[3].dump(subIndent));
            }
            return result.toString();
        }

    }

}

/**
 * Container for a Rectangle and a payload.
 * @param <T> Object; the payload
 */
class RectangleAndPayload<T extends Object> implements Serializable
{
    /** ... */
    private static final long serialVersionUID = 20200904L;

    /** The bounding rectangle. */
    private final Rectangle rectangle;

    /** The payload. */
    private final T payload;

    /**
     * Construct a new RectangleAndPayload object.
     * @param rectangle the bounding rectangle of the payload
     * @param payload the payload
     */
    RectangleAndPayload(final Rectangle rectangle, final T payload)
    {
        this.rectangle = rectangle;
        this.payload = payload;
    }

    /**
     * Retrieve the bounding rectangle.
     * @return the bounding rectangle
     */
    public Rectangle getRectangle()
    {
        return this.rectangle;
    }

    /**
     * Retrieve the payload.
     * @return the payload
     */
    public T getPayload()
    {
        return this.payload;
    }

    @Override
    public String toString()
    {
        return "RectangleAndPayload [rectangle=" + this.rectangle + ", payload=" + this.payload + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.payload == null) ? 0 : this.payload.hashCode());
        result = prime * result + ((this.rectangle == null) ? 0 : this.rectangle.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        @SuppressWarnings("rawtypes")
        RectangleAndPayload other = (RectangleAndPayload) obj;
        if (this.payload == null)
        {
            if (other.payload != null)
            {
                return false;
            }
        }
        else if (!this.payload.equals(other.payload))
        {
            return false;
        }
        if (this.rectangle == null)
        {
            if (other.rectangle != null)
            {
                return false;
            }
        }
        else if (!this.rectangle.equals(other.rectangle))
        {
            return false;
        }
        return true;
    }

}
