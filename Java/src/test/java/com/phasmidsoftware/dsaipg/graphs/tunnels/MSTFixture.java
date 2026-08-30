package com.phasmidsoftware.dsaipg.graphs.tunnels;

import com.phasmidsoftware.dsaipg.graphs.gis.Sequenced;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The graphs and the assertions shared by {@link PrimTest}, {@link KruskalTest}
 * and {@link BoruvkaTest}.
 * <p>
 * NOTE shared, so that the three algorithms are asked the same questions about
 * the same graphs, and each is exercised by a test that names it.
 * <p>
 * Every weight in both graphs is distinct, which means each has exactly ONE
 * minimum spanning tree. So the answer can be asserted outright, and the same
 * assertion applies to all three algorithms -- none of them needs to be told what
 * order to produce its edges in.
 */
class MSTFixture {

    /**
     * A route between two places, weighted by what it costs.
     */
    static class Route implements Comparable<Route>, Sequenced {

        public int getSequence() {
            return sequence;
        }

        /**
         * NOTE this must actually record the value: getMST assigns the sequence
         * numbers through it, and Kml draws the network in that order.
         *
         * @param sequence the sequence number to record.
         */
        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public int compareTo(Route o) {
            return Double.compare(cost, o.cost);
        }

        public double getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return String.valueOf((int) cost);
        }

        public Route(double cost) {
            this.cost = cost;
        }

        private final double cost;
        private int sequence;
    }

    /**
     * Six places in Kalimantan, with the cost of a route between each pair.
     * <pre>
     *        Po   Pa   Ban  Bal  S    T
     *   Po        80   101  123  237  417
     *   Pa             56   64   83   187
     *   Ban                 73   95   203
     *   Bal                      23   89
     *   S                             63
     * </pre>
     *
     * @return a complete graph on six vertices, with fifteen edges.
     */
    static EdgeGraph<String, Route> kalimantan() {
        Graph_Edges<String, Route> g = new Graph_Edges<>();
        addEdge(g, "Po", "Pa", 80);
        addEdge(g, "Po", "Ban", 101);
        addEdge(g, "Po", "Bal", 123);
        addEdge(g, "Po", "S", 237);
        addEdge(g, "Po", "T", 417);
        addEdge(g, "Pa", "Ban", 56);
        addEdge(g, "Pa", "Bal", 64);
        addEdge(g, "Pa", "S", 83);
        addEdge(g, "Pa", "T", 187);
        addEdge(g, "Ban", "Bal", 73);
        addEdge(g, "Ban", "S", 95);
        addEdge(g, "Ban", "T", 203);
        addEdge(g, "Bal", "S", 23);
        addEdge(g, "Bal", "T", 89);
        addEdge(g, "S", "T", 63);
        return g;
    }

    /**
     * Eight Chinese cities and the cost of shipping between them.
     *
     * @return a graph on eight vertices, with fourteen edges.
     */
    static EdgeGraph<String, Route> chinaShippingCost() {
        Graph_Edges<String, Route> g = new Graph_Edges<>();
        addEdge(g, "BeiJing", "ShangHai", 3000);
        addEdge(g, "BeiJing", "ZhengZhou", 1000);
        addEdge(g, "XiAn", "BeiJing", 3500);
        addEdge(g, "XiAn", "ZhengZhou", 2000);
        addEdge(g, "ZhengZhou", "WuHan", 1100);
        addEdge(g, "GuangZhou", "XiAn", 2800);
        addEdge(g, "WuHan", "ShangHai", 1200);
        addEdge(g, "ShangHai", "ZhengZhou", 2300);
        addEdge(g, "GuangZhou", "ZhengZhou", 3200);
        addEdge(g, "GuangZhou", "ShenZhen", 400);
        addEdge(g, "ShenZhen", "WuHan", 2200);
        addEdge(g, "ShenZhen", "FuZhou", 1900);
        addEdge(g, "FuZhou", "ShangHai", 1600);
        addEdge(g, "FuZhou", "WuHan", 1500);
        return g;
    }

    /**
     * The one minimum spanning tree of {@link #kalimantan}, costing 286.
     */
    static final List<String> KALIMANTAN_MST =
            List.of("Bal-S(23)", "Ban-Pa(56)", "Bal-Pa(64)", "S-T(63)", "Pa-Po(80)")
                    .stream().sorted().toList();

    /**
     * The one minimum spanning tree of {@link #chinaShippingCost}, costing 9100.
     */
    static final List<String> CHINA_MST =
            List.of("GuangZhou-ShenZhen(400)", "BeiJing-ZhengZhou(1000)", "WuHan-ZhengZhou(1100)",
                            "ShangHai-WuHan(1200)", "FuZhou-WuHan(1500)", "FuZhou-ShenZhen(1900)",
                            "XiAn-ZhengZhou(2000)")
                    .stream().sorted().toList();

    /**
     * Asserts that the given MST is the expected one, by its edges and by what it
     * costs. Both are checked because either alone can be right by accident: a
     * wrong tree can have the right total, and the right total says nothing about
     * which places were actually joined.
     *
     * @param expected the edges the MST should consist of, as {@link #describe} renders them.
     * @param cost     the total cost those edges should come to.
     * @param mst      the minimum spanning tree produced by one of the algorithms.
     */
    static void assertMST(List<String> expected, double cost, Iterable<Edge<String, Route>> mst) {
        List<String> actual = new ArrayList<>();
        double total = 0;
        for (Edge<String, Route> edge : mst) {
            actual.add(describe(edge));
            total += edge.getAttribute().getCost();
        }
        Collections.sort(actual);
        assertEquals("the minimum spanning tree", expected, actual);
        assertEquals("the cost of the minimum spanning tree", cost, total, 0.5);
    }

    /**
     * Renders an edge with its endpoints in alphabetical order, so that an edge
     * reads the same whichever way round an algorithm happens to report it.
     *
     * @param edge the edge to describe.
     * @return for example, "Bal-S(23)".
     */
    private static String describe(Edge<String, Route> edge) {
        String v = edge.get(), w = edge.getOther(v);
        String first = v.compareTo(w) <= 0 ? v : w, second = v.compareTo(w) <= 0 ? w : v;
        return first + "-" + second + "(" + (int) edge.getAttribute().getCost() + ")";
    }

    private static void addEdge(Graph_Edges<String, Route> g, String v, String w, double cost) {
        g.addEdge(new Edge<>(v, w, new Route(cost)));
    }
}
