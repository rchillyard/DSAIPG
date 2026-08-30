package com.phasmidsoftware.dsaipg.graphs.dijkstra;

import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class ShortestPathsTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Test case: Verify the correct behavior of the solve method for a graph with a single vertex.
     */
    @Test
    public void testSolveSingleVertex() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(1);
        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra.ShortestPaths shortestPaths = dijkstra.shortestPaths(0);

        assertEquals(0.0, shortestPaths.distTo(0), 0.0001);
        assertTrue(shortestPaths.hasPathTo(0));
        assertFalse(shortestPaths.pathTo(0).iterator().hasNext());
    }

    /**
     * Test case: Verify the solve method correctly calculates shortest paths for a small graph with multiple edges.
     */
    @Test
    public void testSolveSmallGraph() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(5);
        double zero = 0.0;
        double dist01 = 2.0;
        double dist02 = 4.0;
        double dist12 = 1.0;
        double dist13 = 7.0;
        double dist24 = 3.0;
        graph.addEdge(new DirectedEdge(0, 1, dist01));
        graph.addEdge(new DirectedEdge(0, 2, dist02));
        graph.addEdge(new DirectedEdge(1, 2, dist12));
        graph.addEdge(new DirectedEdge(1, 3, dist13));
        graph.addEdge(new DirectedEdge(2, 4, dist24));

        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra.ShortestPaths shortestPaths = dijkstra.shortestPaths(0);

        assertEquals(zero, shortestPaths.distTo(0), 0.0001);
        assertEquals(dist01, shortestPaths.distTo(1), 0.0001);
        assertEquals(dist01 + dist12, shortestPaths.distTo(2), 0.0001);
        assertEquals(dist01 + dist13, shortestPaths.distTo(3), 0.0001);
        assertEquals(dist01 + dist12 + dist24, shortestPaths.distTo(4), 0.0001);

        assertTrue(shortestPaths.hasPathTo(4));
        Iterable<DirectedEdge> path = shortestPaths.pathTo(4);
        assertNotNull(path);
    }

    /**
     * Test case: Verify the behavior when the source vertex is disconnected from the rest of the graph.
     */
    @Test
    public void testSolveDisconnectedGraph() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(4);
        graph.addEdge(new DirectedEdge(1, 2, 5.0));
        graph.addEdge(new DirectedEdge(2, 3, 10.0));

        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra.ShortestPaths shortestPaths = dijkstra.shortestPaths(0);

        assertEquals(0.0, shortestPaths.distTo(0), 0.0001);
        assertFalse(shortestPaths.hasPathTo(1));
        assertNull(shortestPaths.pathTo(1));
    }

    /**
     * Test case: Verify the solve method correctly calculates paths in a graph with a cycle.
     */
    @Test
    public void testSolveGraphWithCycle() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(4);
        graph.addEdge(new DirectedEdge(0, 1, 1.0));
        graph.addEdge(new DirectedEdge(1, 2, 2.0));
        graph.addEdge(new DirectedEdge(2, 0, 3.0));
        graph.addEdge(new DirectedEdge(2, 3, 4.0));

        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra.ShortestPaths shortestPaths = dijkstra.shortestPaths(0);

        assertEquals(0.0, shortestPaths.distTo(0), 0.0001);
        assertEquals(1.0, shortestPaths.distTo(1), 0.0001);
        assertEquals(3.0, shortestPaths.distTo(2), 0.0001);
        assertEquals(7.0, shortestPaths.distTo(3), 0.0001);

        assertTrue(shortestPaths.hasPathTo(3));
        Iterable<DirectedEdge> path = shortestPaths.pathTo(3);
        assertNotNull(path);
    }

    /**
     * Test case: Verify the exception for invalid vertex validation when solving.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSolveInvalidVertex() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(3);
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.shortestPaths(4);  // Invalid vertex, should throw an exception.
    }
}