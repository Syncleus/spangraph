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
        return new SpanGraph("test" + (n++), local);
    }

}
