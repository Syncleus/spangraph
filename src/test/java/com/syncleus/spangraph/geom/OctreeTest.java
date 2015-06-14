package com.syncleus.spangraph.geom;

import org.junit.Test;

/**
 * Created by me on 6/13/15.
 */
public class OctreeTest {
    @Test
    public void test1() {
        Octree o = new Octree(2, new Point(0,0,0));
        o.add(1, 1, 0);
        o.add(0, 1, 0);
        o.add(0, 0, 1);
        o.add(0, 0, 1.25);
        o.add(0, 0, 1.5);
        o.add(0, 0, -1);
        o.add(0, 0, -1.25);
        o.add(0, 0, -1.50);
        o.add(0, 0, -1.55);
        o.add(0, 0, -1.575);
        o.print_tree();

    }
}
