package com.syncleus.spangraph.spacetime;

import toxi.geom.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;


public class AbstractOctree<L> extends AABB implements Shape3D {

    /**
     * alternative tree recursion limit, number of world units when cells are
     * not subdivided any further
     */
    protected float minNodeSize = 4;

    /**
     *
     */
    public final AbstractOctree parent;

    protected AbstractOctree[] children;

    protected byte numChildren;

    protected Collection<XYZ> points;

    protected float size, halfSize;

    protected Vec3D center;

    private int depth = 0;

    private boolean isAutoReducing = true;

    /**
     * Constructs a new AbstractOctree node within the AABB cube volume: {o.x, o.y,
     * o.z} ... {o.x+size, o.y+size, o.z+size}
     *
     * @param p
     *            parent node
     * @param o
     *            tree origin
     * @param halfSize
     *            half length of the tree volume along a single axis
     */
    private AbstractOctree(AbstractOctree p, Vec3D o, float halfSize) {
        super(o.add(halfSize, halfSize, halfSize), new Vec3D(halfSize,
                halfSize, halfSize));
        this.parent = p;
        this.halfSize = halfSize;
        this.size = halfSize * 2;
        this.center = o;
        this.numChildren = 0;
        if (parent != null) {
            depth = parent.depth + 1;
            minNodeSize = parent.minNodeSize;
        }
    }

    /**
     * Constructs a new AbstractOctree node within the AABB cube volume: {o.x, o.y,
     * o.z} ... {o.x+size, o.y+size, o.z+size}
     *
     * @param o
     *            tree origin
     * @param size
     *            size of the tree volume along a single axis
     */
    public AbstractOctree(Vec3D o, float size) {
        this(null, o, size / 2);
    }

    /**
     * Adds all points of the collection to the octree. IMPORTANT: Points need
     * be of type Vec3D or have subclassed it.
     *
     * @param points
     *            point collection
     * @return true, if all points have been added successfully.
     */
    public boolean addAll(Collection<XYZ> points) {
        boolean addedAll = true;
        for (XYZ p : points) {
            addedAll &= addPoint(p);
        }
        return addedAll;
    }

    /**
     * Adds a new point/particle to the tree structure. All points are stored
     * within leaf nodes only. The tree implementation is using lazy
     * instantiation for all intermediate tree levels.
     *
     * @param p
     * @return true, if point has been added successfully
     */
    public boolean addPoint(final XYZ p) {
        final float halfSize = this.halfSize;

        AbstractOctree[] children = this.children;

        // check if point is inside cube
        if (containsPoint(p)) {
            // only add points to leaves for now
            if (halfSize <= minNodeSize) {
                if (points == null) {
                    points = newPointsCollection();
                }
                points.add(p);
                return true;
            } else {
                if (children == null) {
                    children = new AbstractOctree[8];
                }
                int octant = getOctantID(p, center);
                if (children[octant] == null) {
                    Vec3D off = center.add(new Vec3D(
                            (octant & 1) != 0 ? halfSize : 0,
                            (octant & 2) != 0 ? halfSize : 0,
                            (octant & 4) != 0 ? halfSize : 0));
                    children[octant] = new AbstractOctree(this, off,
                            halfSize * 0.5f);
                    numChildren++;
                }
                return children[octant].addPoint(p);
            }
        }
        return false;
    }

    protected Collection<XYZ> newPointsCollection() {
        return new ArrayList();
    }


    /**
     * Applies the given {@link OctreeVisitor} implementation to this node and
     * all of its children.
     */
    public void forEach(Consumer<AbstractOctree> visitor) {
        visitor.accept(this);
        if (numChildren > 0) {
            for (AbstractOctree c : children) {
                if (c != null) {
                    c.forEach(visitor);
                }
            }
        }
    }

    public boolean containsPoint(XYZ p) {
        return p.isInAABB(this);
    }

    public AbstractOctree clear() {
        numChildren = 0;
        children = null;
        points = null;
        return this;
    }

    /**
     * @return a copy of the child nodes array
     */
    public AbstractOctree[] getChildrenCopy() {
        if (children != null) {
            AbstractOctree[] clones = new AbstractOctree[8];
            System.arraycopy(children, 0, clones, 0, 8);
            return clones;
        }
        return null;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Finds the leaf node which spatially relates to the given point
     *
     * @return leaf node or null if point is outside the tree dimensions
     */
    public AbstractOctree getLeafForPoint(XYZ p) {
        // if not a leaf node...
        if (p.isInAABB(this)) {
            if (numChildren > 0) {
                int octant = getOctantID(p, center);
                if (children[octant] != null) {
                    return children[octant].getLeafForPoint(p);
                }
            } else if (points != null) {
                return this;
            }
        }
        return null;
    }



    /**
     * Returns the minimum size of nodes (in world units). This value acts as
     * tree recursion limit since nodes smaller than this size are not
     * subdivided further. Leaf node are always smaller or equal to this size.
     *
     * @return the minimum size of tree nodes
     */
    public float getMinNodeSize() {
        return minNodeSize;
    }

    public float getNodeSize() {
        return size;
    }

    /**
     * @return the number of child nodes (max. 8)
     */
    public int getNumChildren() {
        return numChildren;
    }

    /**
     * Computes the local child octant/cube index for the given point
     *
     * @param plocal
     *            point in the node-local coordinate system
     * @return octant index
     */
    protected final int getOctantID(final Vec3D plocal) {
        float halfSize = this.halfSize;

        return (plocal.x >= halfSize ? 1 : 0) + (plocal.y >= halfSize ? 2 : 0)
                + (plocal.z >= halfSize ? 4 : 0);
    }

    /** computes getOctantID for the point subtracted by another point,
     *  without needing to allocate a temporary object

     */
    private int getOctantID(final XYZ p, final Vec3D s) {
        return ((p.x() - s.x) >= halfSize ? 1 : 0) + ((p.y() - s.y) >= halfSize ? 2 : 0)
                + ((p.z() - s.z) >= halfSize ? 4 : 0);
    }

    /**
     * @return the offset
     */
    public roVec3D getCenter() {
        return center;
    }

    /**
     * @return the parent
     */
    public AbstractOctree getParent() {
        return parent;
    }

    public List<XYZ> getPoints() {
        return getPoints(new ArrayList());
    }

    /**
     * @return the points
     */
    public List<XYZ> getPoints(List<XYZ> results) {
        if (points != null) {
        } else if (numChildren > 0) {
            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    List<XYZ> childPoints = children[i].getPoints();
                    if (childPoints != null) {
                        results.addAll(childPoints);
                    }
                }
            }
        }
        return results;
    }

    /**
     * Selects all stored points within the given axis-aligned bounding box.
     *
     * @param b
     *            AABB
     * @return all points with the box volume
     */
    public List<XYZ> getPointsWithinBox(AABB b) {
        ArrayList<XYZ> results = null;
        if (this.intersectsBox(b)) {
            if (points != null) {
                for (XYZ q : points) {
                    if (q.isInAABB(b)) {
                        if (results == null) {
                            results = new ArrayList<XYZ>();
                        }
                        results.add(q);
                    }
                }
            } else if (numChildren > 0) {
                for (int i = 0; i < 8; i++) {
                    if (children[i] != null) {
                        List<XYZ> points = children[i].getPointsWithinBox(b);
                        if (points != null) {
                            if (results == null) {
                                results = new ArrayList();
                            }
                            results.addAll(points);
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * Selects all stored points within the given sphere volume
     *
     * @param s
     *            sphere
     * @return selected points
     */
    @Deprecated public List<XYZ> getPointsWithinSphere(Sphere s) {
        ArrayList<XYZ> results = null;
        if (this.intersectsSphere(s)) {
            if (points != null) {
                for (XYZ q : points) {
                    if (s.containsPoint(q)) {
                        if (results == null) {
                            results = new ArrayList();
                        }
                        results.add(q);
                    }
                }
            } else if (numChildren > 0) {
                for (int i = 0; i < 8; i++) {
                    if (children[i] != null) {
                        List<XYZ> points = children[i].getPointsWithinSphere(s);
                        if (points != null) {
                            if (results == null) {
                                results = new ArrayList();
                            }
                            results.addAll(points);
                        }
                    }
                }
            }
        }
        return results;
    }

    public void forEachInSphere(Sphere s, Consumer<XYZ> c) {

        if (this.intersectsSphere(s)) {
            if (points != null) {
                for (XYZ q : points) {
                    if (s.containsPoint(q)) {
                        c.accept(q);
                    }
                }
            } else if (numChildren > 0) {
                for (int i = 0; i < 8; i++) {
                    AbstractOctree cc = children[i];
                    if (cc != null) {
                        cc.forEachInSphere(s, c);
                    }
                }
            }
        }
    }

    /**
     * Selects all stored points within the given sphere volume
     *
     * @param sphereOrigin
     * @param clipRadius
     * @return selected points
     */
    public void forEachInSphere(Vec3D sphereOrigin, float clipRadius, Consumer<XYZ> c) {
        forEachInSphere(new Sphere(sphereOrigin, clipRadius), c);
    }

    /**
     * @return the size
     */
    public float getSize() {
        return size;
    }

    private void reduceBranch() {
        if (points != null && points.size() == 0) {
            points = null;
        }
        if (numChildren > 0) {
            for (int i = 0; i < 8; i++) {
                if (children[i] != null && children[i].points == null) {
                    children[i] = null;
                }
            }
        }
        if (parent != null) {
            parent.reduceBranch();
        }
    }

    /**
     * Removes a point from the tree and (optionally) tries to release memory by
     * reducing now empty sub-branches.
     *
     * @param p
     *            point to delete
     * @return true, if the point was found & removed
     */
    public boolean remove(XYZ p) {
        boolean found = false;
        AbstractOctree leaf = getLeafForPoint(p);
        if (leaf != null) {
            if (leaf.points.remove(p)) {
                found = true;
                if (isAutoReducing && leaf.points.size() == 0) {
                    leaf.reduceBranch();
                }
            }
        }
        return found;
    }

    public void removeAll(Collection<XYZ> points) {
        for (XYZ p : points) {
            remove(p);
        }
    }

    /**
     * @param minNodeSize
     */
    public void setMinNodeSize(float minNodeSize) {
        this.minNodeSize = minNodeSize * 0.5f;
    }

    /**
     * Enables/disables auto reduction of branches after points have been
     * deleted from the tree. Turned off by default.
     *
     * @param state
     *            true, to enable feature
     */
    public void setTreeAutoReduction(boolean state) {
        isAutoReducing = state;
    }

    /*
     * (non-Javadoc)
     *
     * @see toxi.geom.AABB#toString()
     */
    public String toString() {
        return "<octree> offset: " + super.toString() + " size: " + size;
    }
}
