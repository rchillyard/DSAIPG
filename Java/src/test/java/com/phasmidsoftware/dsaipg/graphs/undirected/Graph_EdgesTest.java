package com.phasmidsoftware.dsaipg.graphs.undirected;

import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Graph_Edges.
 * <p>
 * NOTE all four methods here were empty bodies, so this class passed without
 * exercising anything. See {@link GraphTest}, which covers the constructor and
 * the simplest addEdge; these cover the predicate, the two-vertex form, and the
 * asymmetry in how an edge is stored.
 */
public class Graph_EdgesTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void edges() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        target.addEdge(1, 2, 1.0);
        target.addEdge(3, 4, 2.0);
        List<Double> attributes = new ArrayList<>();
        for (Edge<Integer, Double> edge : target.edges()) attributes.add(edge.getAttribute());
        assertEquals(2, attributes.size());
        assertTrue(attributes.contains(1.0));
        assertTrue(attributes.contains(2.0));
    }

    /**
     * An edge is incident on BOTH of its vertices, so it appears in both adjacency
     * bags -- and edges() reports it once all the same, by collecting an edge only
     * from the bag of the vertex Edge.get() returns.
     * <p>
     * This test used to assert the opposite: that adjacent(2) was empty, because an
     * edge went into the "from" bag alone. That made adjacent(v) report the edges
     * WRITTEN with v first rather than the edges at v, so an algorithm walking by
     * adjacency saw an arbitrary subset of the graph. Prim returned a spanning
     * forest with a vertex missing.
     */
    @Test
    public void edgesAreNotDoubleCounted() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        target.addEdge(new Edge<>(1, 2, 1.0));
        assertEquals(1, target.edges().size());
        assertEquals(1, ((SizedIterable<?>) target.adjacent(1)).size());
        assertEquals("an edge is adjacent to its second vertex too",
                1, ((SizedIterable<?>) target.adjacent(2)).size());
    }

    /**
     * A self-loop is incident on one vertex, so it belongs in one bag once -- and
     * must not be reported twice by edges() either.
     */
    @Test
    public void aSelfLoopIsHeldOnce() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        target.addEdge(new Edge<>(1, 1, 1.0));
        assertEquals(1, target.edges().size());
        assertEquals(1, ((SizedIterable<?>) target.adjacent(1)).size());
        assertEquals(1, target.vertices().size());
    }

    @Test
    public void addEdge() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        Edge<Integer, Double> edge = new Edge<>(1, 2, 1.0);
        target.addEdge(edge, e -> true);
        assertEquals(1, target.edges().size());
        assertEquals(edge, target.edges().iterator().next());
    }

    @Test
    public void addEdgeRejectedByPredicate() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        target.addEdge(new Edge<>(1, 2, 1.0), e -> false);
        assertEquals(0, target.edges().size());
        assertEquals("a rejected edge must not leave its vertices behind",
                0, target.vertices().size());
    }

    /**
     * The two-vertex form of addEdge, which builds the Edge for you.
     */
    @Test
    public void addEdge1() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        target.addEdge(1, 2, 1.0);
        assertEquals(1, target.edges().size());
        assertEquals(new Edge<>(1, 2, 1.0), target.edges().iterator().next());
        assertEquals(2, target.vertices().size());
    }

    @Test
    public void addEdge1WithPredicate() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        for (double attribute : new double[]{1.0, 2.0, 3.0, 4.0})
            target.addEdge(0, (int) attribute, attribute, e -> e.getAttribute() > 2.0);
        assertEquals(2, target.edges().size());
    }

    @Test
    public void toStringTest() {
        EdgeGraph<Integer, Double> target = new Graph_Edges<>();
        target.addEdge(1, 2, 1.0);
        assertEquals("{1=Bag_Array{items=[1-2: 1.0], count=1}, 2=Bag_Array{items=[1-2: 1.0], count=1}}",
                target.toString());
    }
}
