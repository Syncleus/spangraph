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

import com.tinkerpop.blueprints.Graph;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * https://github.com/tinkerpop/blueprints/wiki/Property-Graph-Model-Test-Suite
 */
public class SpanGraphBlueprintsTest extends MapGraphBlueprintsTest {

    static {
        Logger.getGlobal().setLevel(Level.SEVERE);
    }

    static final InfiniPeer local = InfiniPeer.local();

    public static int n = 0;
    public Graph generateGraph() {
        MapGraph g = new SpanGraph("test" + (n++), local);
        g.setRequireEdgeLabels(true); //for compliance
        return g;
    }

}
