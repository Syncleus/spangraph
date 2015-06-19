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
package com.syncleus.spangraph;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Uses java.util.TreeMap to implement adjacency,
 * with abstract comparator to control sorting
 */
abstract public class TreeMapGraph extends MapGraph {

    protected TreeMapGraph() {
        super();

        init();
    }

    protected TreeMapGraph(String globalGraphID) {
        super(globalGraphID);
    }

    @Override
    protected Map newEdgeMap() {
        return new TreeMap<>(newEdgeComparator());
    }

    abstract protected Comparator<Edge> newEdgeComparator();

    @Override
    protected Map newVertexMap() {
        return new TreeMap<>(newVertexComparator());
    }

    abstract protected Comparator<Vertex> newVertexComparator();
}
