package com.syncleus.spangraph;

import com.tinkerpop.blueprints.Vertex;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SpanGraphTest {


//    @Test
//    public void testVertexEdgePropagationMemory() throws InterruptedException {
//        testVertexPropagation(x ->
//                        new SpanGraph("memory", InfiniPeer.cluster(x))
//        );
//    }

    @Test
    public void testVertexEdgePropagationNetwork() throws InterruptedException {
        testVertexPropagation(x ->
                        new SpanGraph("network", InfiniPeer.cluster(x))
        );
    }

    public void testVertexPropagation(Function<String, SpanGraph> graph) throws InterruptedException {

        final AtomicReference<SpanGraph> bRef = new AtomicReference(null);


        SpanGraph a = graph.apply("PeerA");

        Vertex vx = a.addVertex("x");
        Vertex vy = a.addVertex("y");
        assertEquals("correct vertex id", vx.getId(), "x");
        assertEquals("correct vertex id", vy.getId(), "y");
        assertEquals("non-string vertex id", ((MapGraph.MVertex) a.addVertex(17)).getId(), 17);
        assertEquals(3, a.vertexCount());

        Thread x = new Thread(() -> {

            try {
                int preDelayMS = 10;
                int afterConnectedDelayMS = 100;

                sleep(preDelayMS);

                SpanGraph b = graph.apply("PeerB");
                bRef.set(b);

                b.addEdge("xy", "x", "y");
                assertEquals(1, b.edgeCount());

                sleep(afterConnectedDelayMS);
            } catch (Throwable e) {
                e.printStackTrace();
                assertTrue(e.toString(), false);
            }

        });

        x.start();
        x.join();


        SpanGraph b = bRef.get();


        assertEquals(3, a.vertexCount());
        assertEquals(1, a.edgeCount());
        assertEquals(0, a.differentEdges(b).size());
        assertEquals(0, b.differentEdges(a).size());
        assertEquals("Graphs:\n" + a.toString() + "\n" + b.toString() + "\n", a, b);


    }


    static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
