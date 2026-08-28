package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class GeoKruskalTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * What an edge of one of these graphs carries: a cost to compare by, and the
     * sequence number the MST algorithm writes back.
     * <p>
     * NOTE deliberately local rather than borrowing TunnelProperties. This is a
     * test of Kruskal, which has nothing to do with tunnels -- and TunnelProperties
     * keeps its fields package-private, so a test in this package could not read
     * them anyway.
     */
    static class Cost implements Comparable<Cost>, Sequenced {
        Cost(int cost) {
            this.cost = cost;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public int compareTo(Cost o) {
            return Integer.compare(cost, o.cost);
        }

        @Override
        public String toString() {
            return String.valueOf(cost);
        }

        final int cost;
        private int sequence;
    }

    private final GeoPoint la = new MockGeoPoint("Lake Hall", new Position_Spherical(42.3384215, -71.0930697));
    private final GeoPoint ka = new MockGeoPoint("Kariotis Hall", new Position_Spherical(42.3386223, -71.0931943));
    private final GeoPoint sh = new MockGeoPoint("Shillman Hall", new Position_Spherical(42.3375055, -71.090202));

    /**
     * Three places, and the three routes that might join them. The costs are all
     * different, so the cheapest pair -- LA-KA and KA-SH, costing 300 between them
     * -- is the only minimum spanning tree there is.
     *
     * @return the graph.
     */
    private GeoGraphSpherical<GeoPoint, Cost> triangle() {
        GeoGraphSpherical<GeoPoint, Cost> graph = new GeoGraphSpherical<>();
        graph.addEdge(new GeoEdge<>(la, ka, new Cost(100)));
        graph.addEdge(new GeoEdge<>(ka, sh, new Cost(200)));
        graph.addEdge(new GeoEdge<>(la, sh, new Cost(300)));
        return graph;
    }

    private static List<Integer> costsOf(Iterable<Edge<GeoPoint, Cost>> edges) {
        List<Integer> result = new ArrayList<>();
        for (Edge<GeoPoint, Cost> e : edges) result.add(e.getAttribute().cost);
        Collections.sort(result);
        return result;
    }

    @Test
    public void getMST() {
        EdgeGraph<GeoPoint, Cost> mst = new GeoKruskal<>(triangle()).getMST();
        assertEquals("three places need two routes", 2, mst.edges().size());
        assertEquals(3, mst.vertices().size());
        assertEquals("the dearest of the three is left out", List.of(100, 200), costsOf(mst.edges()));
    }

    /**
     * getMST numbers the edges as it goes, in the order Kruskal chose them --
     * cheapest first. Kml draws the network in that order.
     */
    @Test
    public void getMSTAssignsSequenceNumbers() {
        EdgeGraph<GeoPoint, Cost> mst = new GeoKruskal<>(triangle()).getMST();
        for (Edge<GeoPoint, Cost> e : mst.edges())
            assertEquals("cheapest chosen first, so cost 100 is sequence 0",
                    e.getAttribute().cost == 100 ? 0 : 1, e.getAttribute().getSequence());
    }

    @Test
    public void iterator() {
        Iterator<Edge<GeoPoint, Cost>> iterator = new GeoKruskal<>(triangle()).iterator();
        assertTrue(iterator.hasNext());
        assertEquals("Kruskal takes the cheapest edge first", 100, iterator.next().getAttribute().cost);
        assertTrue(iterator.hasNext());
        assertEquals(200, iterator.next().getAttribute().cost);
        assertFalse("and there is no third edge in a tree of three vertices", iterator.hasNext());
    }

    @Test
    public void iteratorOfAnEmptyGraph() {
        assertFalse(new GeoKruskal<GeoPoint, Cost>(new GeoGraphSpherical<>()).iterator().hasNext());
    }

    /**
     * getGeoMST fills the graph it is given, and hands that same graph back.
     */
    @Test
    public void getGeoMST() {
        GeoGraphSpherical<GeoPoint, Cost> empty = new GeoGraphSpherical<>();
        Geo<GeoPoint, Cost> result = new GeoKruskal<>(triangle()).getGeoMST(empty);
        assertSame("the parameter is the result", empty, result);
        assertEquals(2, result.edges().size());
        assertEquals(3, result.vertices().size());
        assertEquals(List.of(100, 200), costsOf(result.edges()));
    }

    /**
     * A Geo graph can measure its own edges, which is the whole reason for putting
     * the MST into one. These three are real Northeastern buildings, so the
     * distances are real: a few tens of metres apart.
     */
    @Test
    public void getGeoMSTEdgesHaveALength() {
        Geo<GeoPoint, Cost> result = new GeoKruskal<>(triangle()).getGeoMST(new GeoGraphSpherical<>());
        for (Edge<GeoPoint, Cost> e : result.geoEdges())
            assertTrue("every route is somewhere between 1m and 1km long",
                    result.length(e) > 1 && result.length(e) < 1000);
    }
}
