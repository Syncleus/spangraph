/**
 * Copyright: (c) Syncleus, Inc.
 *
 * You may redistribute and modify this source code under the terms and
 * conditions of the Open Source Community License - Type C version 1.0
 * or any later version as published by Syncleus, Inc. at www.syncleus.com.
 * There should be a copy of the license included with this file. If a copy
 * of the license is not included you are granted no right to distribute or
 * otherwise use this file except through a legal and valid license. You
 * should also contact Syncleus, Inc. at the information below if you cannot
 * find a license:
 *
 * Syncleus, Inc.
 * 2604 South 12th Street
 * Philadelphia, PA 19148
 */
package com.syncleus.spangraph.spacetime;

import com.syncleus.spangraph.MapGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.syncleus.toxi.geom.XYZ;

/**
 * OctMapped indexes vertices in a Graph
 */
abstract public class OctMapGraph<X extends XYZ> extends MapGraph<X> {

    public interface DistanceFunction3 {

        public float get(XYZ a, XYZ b);


        /** allows an implementation to use early termination when
         * exceeding a minimum value than completely calculating the result
         */
        default public boolean lessThan(XYZ a, XYZ b, float minimum) {
            return get(a, b) < minimum;
        }

    }

    abstract protected OctMap<X, Edge> newEdgeMap();

    abstract protected OctMap<X, Vertex> newVertexMap();




}
