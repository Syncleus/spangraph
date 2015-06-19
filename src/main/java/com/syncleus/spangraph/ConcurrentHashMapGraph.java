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
import org.infinispan.util.concurrent.ConcurrentHashSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uses java.util.LinkedHashMap to implement adjacency
 */
public class ConcurrentHashMapGraph extends MapGraph {

    protected ConcurrentHashMapGraph() {
        super();

        init();
    }

    protected ConcurrentHashMapGraph(String globalGraphID) {
        super(globalGraphID);
    }

    @Override
    protected Map newEdgeMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    protected Map newVertexMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    protected Map<String, Set<Edge>> newVertexEdgeMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    protected Set<Edge> newEdgeSet(int size) {
        return new ConcurrentHashSet<>(size);
    }
}
