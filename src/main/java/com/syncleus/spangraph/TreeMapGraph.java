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
