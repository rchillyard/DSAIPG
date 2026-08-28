package com.phasmidsoftware.dsaipg.graphs.tunnels;

import com.phasmidsoftware.dsaipg.graphs.gis.Kruskal;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.graphs.tunnels.MSTFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests of Kruskal's algorithm. The graphs and the expected answers are in
 * {@link MSTFixture}, which explains why they are shared.
 */
public class KruskalTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testKruskal_kalimantan() {
        assertMST(KALIMANTAN_MST, 286, new Kruskal<>(kalimantan()));
    }

    @Test
    public void testKruskal_ChinaShippingCost() {
        assertMST(CHINA_MST, 9100, new Kruskal<>(chinaShippingCost()));
    }

    /**
     * A spanning tree of n vertices has n - 1 edges, and getMST() must report the
     * same tree that iterating reports.
     */
    @Test
    public void testKruskal_getMST() {
        EdgeGraph<String, Route> mst = new Kruskal<>(kalimantan()).getMST();
        assertEquals(5, mst.edges().size());
        assertEquals(6, mst.vertices().size());
        assertMST(KALIMANTAN_MST, 286, mst.edges());
    }

    /**
     * getMST() numbers the edges in the order the algorithm chose them, which is
     * what Kml relies on to draw a tunnel network in sequence.
     */
    @Test
    public void testKruskal_sequencesTheEdges() {
        List<Integer> sequences = new ArrayList<>();
        for (Edge<String, Route> edge : new Kruskal<>(kalimantan()).getMST().edges())
            sequences.add(edge.getAttribute().getSequence());
        sequences.sort(null);
        assertEquals(List.of(0, 1, 2, 3, 4), sequences);
    }

    /**
     * On a graph that is not connected there is no spanning tree, so what the
     * algorithm yields is a spanning FOREST: every component spanned, and no edge
     * joining one component to another.
     */
    @Test
    public void testKruskal_spansEachComponentOfADisconnectedGraph() {
        Graph_Edges<String, Route> g = new Graph_Edges<>();
        g.addEdge(new Edge<>("A", "B", new Route(1)));
        g.addEdge(new Edge<>("C", "D", new Route(2)));
        g.addEdge(new Edge<>("C", "E", new Route(3)));
        assertMST(List.of("A-B(1)", "C-D(2)", "C-E(3)"), 6, new Kruskal<>(g));
    }

    /**
     * The empty case, and the one-vertex case: a tree of one vertex has no edges.
     */
    @Test
    public void testKruskal_degenerateGraphs() {
        assertTrue("no vertices, no edges", isEmpty(new Kruskal<>(new Graph_Edges<String, Route>())));
        Graph_Edges<String, Route> one = new Graph_Edges<>();
        one.addVertex("A");
        assertTrue("one vertex, no edges", isEmpty(new Kruskal<>(one)));
    }

    private static boolean isEmpty(Iterable<Edge<String, Route>> mst) {
        return !mst.iterator().hasNext();
    }
}
