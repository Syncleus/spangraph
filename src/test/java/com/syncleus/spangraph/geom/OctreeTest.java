package com.syncleus.spangraph.geom;

import com.syncleus.spangraph.spacetime.AbstractOctree;
import org.junit.Test;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by me on 6/13/15.
 */
public class OctreeTest {
    @Test
    public void test1() {
        AbstractOctree o = new AbstractOctree(
                new Vec3D(-2f, -2f, -2f), 4f, 0.05f);
        
        o.put(new Vec3D(1, 1, 0));
        o.put(new Vec3D(0, 1, 0));
        o.put(new Vec3D(0, 0, 1));
        o.put(new Vec3D(0, 0, 1.25f));
        o.put(new Vec3D(0, 0, 1.5f));
        o.put(new Vec3D(0, 0, -1));
        o.put(new Vec3D(0, 0, -1.25f));
        o.put(new Vec3D(0, 0, -1.50f));
        o.put(new Vec3D(0, 0, -1.55f));
        o.put(new Vec3D(0, 0, -1.575f));

        o.forEachRecursive(x -> {

            List p = (((AbstractOctree) x).getPointsRecursively());
            //if (!p.isEmpty())
            //System.out.println(x + " " + p);
        });

        //System.out.println("size: " + o.getNumChildren());

        assertEquals(o.countPointsRecursively(), 10);

        int[] sphereCount = new int[1];
        o.forEachInSphere(new Vec3D(0, 0, -0.75f), 0.5f, x -> {
            sphereCount[0]++;
        });
        assertEquals(2, sphereCount[0]);

        int[] boxCount = new int[1];

        AABB aabb = new AABB(new Vec3D(0f, -0.5f, -2.0f), new Vec3D(0.5f, 0.5f, 0.5f));
        o.forEachInBox(aabb, x -> {
            boxCount[0]++;
        });
        assertEquals(3, boxCount[0]);

        //o.print_tree();

    }
}
