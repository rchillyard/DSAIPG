package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.phasmidsoftware.dsaipg.graphs.dag.DiGraph;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.BellmanFord.bellmanFordAlgorithm;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class BellmanFordTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();
    @Test
    public void test0() {
        Vertex a = new Vertex("A", 0);
        DiGraph<Vertex, Double> graph = new DiGraph<>();
        graph.addVertex(a);
        Vertex b = new Vertex("B", 0);
        graph.addVertex(b);
        Vertex c = new Vertex("C", 0);
        graph.addVertex(c);
        Vertex d = new Vertex("D", 0);
        graph.addVertex(d);
        Vertex e = new Vertex("E", 0);
        graph.addVertex(e);
        graph.addEdge(new Edge<>(a, b, -1.0));
        graph.addEdge(new Edge<>(a, c, 4.0));
        graph.addEdge(new Edge<>(b, e, 2.0));
        graph.addEdge(new Edge<>(b, d, 2.0));
        graph.addEdge(new Edge<>(b, c, 3.0));
        graph.addEdge(new Edge<>(d, b, 1.0));
        graph.addEdge(new Edge<>(d, c, 5.0));
        graph.addEdge(new Edge<>(e, d, -3.0));
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
        DiGraph<Vertex, Double> graph = new DiGraph<>();
        graph.addVertex(a);
        Vertex b = new Vertex("B", 0);
        graph.addVertex(b);
        Vertex c = new Vertex("C", 0);
        graph.addVertex(c);
        Vertex d = new Vertex("D", 0);
        graph.addVertex(d);
        Vertex e = new Vertex("E", 0);
        graph.addVertex(e);
        graph.addEdge(new Edge<>(a, b, 2.0));
        graph.addEdge(new Edge<>(a, c, 2.0));
        graph.addEdge(new Edge<>(b, d, 3.0));
        graph.addEdge(new Edge<>(c, d, 6.0));
        graph.addEdge(new Edge<>(c, e, 4.0));
        graph.addEdge(new Edge<>(e, d, -5.0));
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
        DiGraph<Vertex, Double> graph = new DiGraph<>();
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
        graph.addEdge(new Edge<>(a, d, 5.0));
        graph.addEdge(new Edge<>(b, e, -1.0));
        graph.addEdge(new Edge<>(c, b, -2.0));
        graph.addEdge(new Edge<>(c, e, 3.0));
        graph.addEdge(new Edge<>(d, c, -2.0));
        graph.addEdge(new Edge<>(d, f, -1.0));
        graph.addEdge(new Edge<>(e, f, 3.0));
        bellmanFordAlgorithm(graph, a, f);
        assertEquals(1.0, bellmanFordAlgorithm(graph, a, b), 0);
        assertEquals(3.0, bellmanFordAlgorithm(graph, a, c), 0);
        assertEquals(5.0, bellmanFordAlgorithm(graph, a, d), 0);
        assertEquals(0.0, bellmanFordAlgorithm(graph, a, e), 0);
        assertEquals(3.0, bellmanFordAlgorithm(graph, a, f), 0);
        assertEquals(0, bellmanFordAlgorithm(graph, a, a), 0);
    }


    /**
     * An edge may be added without declaring its source first.
     * <p>
     * The Graph this used to run on could not do that. Its addEdge read
     * {@code adjacent.getOrDefault(u, new LinkedList<>()).add(...)} -- and
     * getOrDefault returns the default WITHOUT putting it in the map, so an edge
     * whose source had not been addVertex'd was added to a throwaway list and
     * silently lost. E++ still counted it, so the graph reported an edge it did
     * not have. Every test called addVertex first, so it never showed.
     */
    @Test
    public void anEdgeNeedsNoPriorAddVertex() {
        Vertex a = new Vertex("A", 0);
        Vertex b = new Vertex("B", 0);
        DiGraph<Vertex, Double> graph = new DiGraph<>();
        graph.addEdge(new Edge<>(a, b, 3.0));
        assertEquals(1, graph.edges().size());
        assertEquals(3.0, bellmanFordAlgorithm(graph, a, b), 0);
    }

    /**
     * An unreachable target says so, rather than throwing NullPointerException on
     * unboxing a null out of a method declared to return a double.
     */
    @Test
    public void anUnreachableTargetIsReported() {
        Vertex a = new Vertex("A", 0);
        Vertex b = new Vertex("B", 0);
        Vertex island = new Vertex("Z", 0);
        DiGraph<Vertex, Double> graph = new DiGraph<>();
        graph.addEdge(new Edge<>(a, b, 1.0));
        graph.addVertex(island);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bellmanFordAlgorithm(graph, a, island));
        assertTrue(e.getMessage().contains("not reachable"));
    }

    /**
     * Negative weights are the whole reason for using Bellman-Ford here: the
     * knapsack and house-robber formulations negate a value so that the shortest
     * path is the most valuable one.
     */
    @Test
    public void negativeWeightsAreHandled() {
        Vertex a = new Vertex("A", 0);
        Vertex b = new Vertex("B", 0);
        Vertex c = new Vertex("C", 0);
        DiGraph<Vertex, Double> graph = new DiGraph<>();
        graph.addEdge(new Edge<>(a, b, 5.0));
        graph.addEdge(new Edge<>(a, c, 10.0));
        graph.addEdge(new Edge<>(b, c, -20.0));
        assertEquals("via b, at 5 - 20, beats the direct 10", -15.0,
                bellmanFordAlgorithm(graph, a, c), 0);
    }
}
