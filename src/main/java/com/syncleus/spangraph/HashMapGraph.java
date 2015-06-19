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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Uses java.util.LinkedHashMap to implement adjacency
 */
public class HashMapGraph extends MapGraph {

    protected HashMapGraph() {
        super();

        init();
    }

    protected HashMapGraph(String globalGraphID) {
        super(globalGraphID);
    }

    @Override
    protected Map newEdgeMap() {
        return new LinkedHashMap<>();
    }

    @Override
    protected Map newVertexMap() {
        return new LinkedHashMap<>();
    }

    @Override
    protected Map<String, Set<Edge>> newVertexEdgeMap() {
        return new LinkedHashMap();
    }

    @Override
    protected Set<Edge> newEdgeSet(int size) {
        return new LinkedHashSet(size);
    }
}
