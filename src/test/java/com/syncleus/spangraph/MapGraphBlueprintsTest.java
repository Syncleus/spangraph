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

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.GraphTest;

import java.lang.reflect.Method;

/**
 * Created by me on 6/7/15.
 */
public class MapGraphBlueprintsTest extends GraphTest {

    public void testVertexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
    }

    public void testEdgeTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
    }

    public void testGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
    }

//    public void testKeyIndexableGraphTestSuite() throws Exception {
//        this.stopWatch();
//        doTestSuite(new KeyIndexableGraphTestSuite(this));
//        printTestPerformance("KeyIndexableGraphTestSuite", this.stopWatch());
//    }
//
//    public void testIndexableGraphTestSuite() throws Exception {
//        this.stopWatch();
//        doTestSuite(new IndexableGraphTestSuite(this));
//        printTestPerformance("IndexableGraphTestSuite", this.stopWatch());
//    }
//
//    public void testIndexTestSuite() throws Exception {
//        this.stopWatch();
//        doTestSuite(new IndexTestSuite(this));
//        printTestPerformance("IndexTestSuite", this.stopWatch());
//    }
//
//    public void testGraphMLReaderTestSuite() throws Exception {
//        this.stopWatch();
//        doTestSuite(new GraphMLReaderTestSuite(this));
//        printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
//    }
//
//    public void testGMLReaderTestSuite() throws Exception {
//        this.stopWatch();
//        doTestSuite(new GMLReaderTestSuite(this));
//        printTestPerformance("GMLReaderTestSuite", this.stopWatch());
//    }
//
//    public void testGraphSONReaderTestSuite() throws Exception {
//        this.stopWatch();
//        doTestSuite(new GraphSONReaderTestSuite(this));
//        printTestPerformance("GraphSONReaderTestSuite", this.stopWatch());
//    }

    public Graph generateGraph() {
        ConcurrentHashMapGraph g = new ConcurrentHashMapGraph();
        g.setRequireEdgeLabels(true); //for compliance
        return g;
    }

    @Override
    public Graph generateGraph(String s) {
        System.err.println("generateGraph(String ??):  " + s);
        return null;
    }

    public void doTestSuite(final TestSuite testSuite) throws Exception {
        String doTest = System.getProperty("testTinkerGraph");
        if (doTest == null || doTest.equals("true")) {
            for (Method method : testSuite.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("test")) {
                    System.out.println("Testing " + method.getName() + "...");
                    method.invoke(testSuite);
                }
            }
        }
    }

}
