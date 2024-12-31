package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import org.junit.Test;

import static com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.BellmanFord.bellmanFordAlgorithm;
import static org.junit.Assert.assertEquals;

public class BellmanFordTest {
    @Test
    public void test0() {
        Vertex a = new Vertex("A", 0);
        Graph graph = new Graph(a);
        graph.addVertex(a);
        Vertex b = new Vertex("B", 0);
        graph.addVertex(b);
        Vertex c = new Vertex("C", 0);
        graph.addVertex(c);
        Vertex d = new Vertex("D", 0);
        graph.addVertex(d);
        Vertex e = new Vertex("E", 0);
        graph.addVertex(e);
        graph.addEdge(a, b, -1);
        graph.addEdge(a, c, 4);
        graph.addEdge(b, e, 2);
        graph.addEdge(b, d, 2);
        graph.addEdge(b, c, 3);
        graph.addEdge(d, b, 1);
        graph.addEdge(d, c, 5);
        graph.addEdge(e, d, -3);
        bellmanFordAlgorithm(graph, a, e);
        assertEquals(1.0, bellmanFordAlgorithm(graph, a, e), 0);
        assertEquals(-1.0, bellmanFordAlgorithm(graph, a, b), 0);
        assertEquals(2.0, bellmanFordAlgorithm(graph, a, c), 0);
        assertEquals(-2.0, bellmanFordAlgorithm(graph, a, d), 0);
        assertEquals(0, bellmanFordAlgorithm(graph, a, a), 0);
    }

    @Test
    public void test1() {
        Vertex a = new Vertex("A", 0);
        Graph graph = new Graph(a);
        graph.addVertex(a);
        Vertex b = new Vertex("B", 0);
        graph.addVertex(b);
        Vertex c = new Vertex("C", 0);
        graph.addVertex(c);
        Vertex d = new Vertex("D", 0);
        graph.addVertex(d);
        Vertex e = new Vertex("E", 0);
        graph.addVertex(e);
        graph.addEdge(a, b, 2);
        graph.addEdge(a, c, 2);
        graph.addEdge(b, d, 3);
        graph.addEdge(c, d, 6);
        graph.addEdge(c, e, 4);
        graph.addEdge(e, d, -5);
        bellmanFordAlgorithm(graph, a, e);
        assertEquals(2.0, bellmanFordAlgorithm(graph, a, b), 0);
        assertEquals(2.0, bellmanFordAlgorithm(graph, a, c), 0);
        assertEquals(1.0, bellmanFordAlgorithm(graph, a, d), 0);
        assertEquals(6.0, bellmanFordAlgorithm(graph, a, e), 0);
        assertEquals(0, bellmanFordAlgorithm(graph, a, a), 0);
    }

    @Test
    public void test2() {
        Vertex a = new Vertex("A", 0);
        Graph graph = new Graph(a);
        graph.addVertex(a);
        Vertex b = new Vertex("B", 0);
        graph.addVertex(b);
        Vertex c = new Vertex("C", 0);
        graph.addVertex(c);
        Vertex d = new Vertex("D", 0);
        graph.addVertex(d);
        Vertex e = new Vertex("E", 0);
        graph.addVertex(e);
        Vertex f = new Vertex("F", 0);
        graph.addVertex(f);
        graph.addEdge(a, d, 5);
        graph.addEdge(b, e, -1);
        graph.addEdge(c, b, -2);
        graph.addEdge(c, e, 3);
        graph.addEdge(d, c, -2);
        graph.addEdge(d, f, -1);
        graph.addEdge(e, f, 3);
        bellmanFordAlgorithm(graph, a, f);
        assertEquals(1.0, bellmanFordAlgorithm(graph, a, b), 0);
        assertEquals(3.0, bellmanFordAlgorithm(graph, a, c), 0);
        assertEquals(5.0, bellmanFordAlgorithm(graph, a, d), 0);
        assertEquals(0.0, bellmanFordAlgorithm(graph, a, e), 0);
        assertEquals(3.0, bellmanFordAlgorithm(graph, a, f), 0);
        assertEquals(0, bellmanFordAlgorithm(graph, a, a), 0);
    }

}