/*
 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

package com.phasmidsoftware.dsaipg.graphs.traversal;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The {@code Prims} class implements Prim's algorithm to compute
 * the Minimum Spanning Tree (MST) of a connected, edge-weighted graph.
 * It maintains a priority queue to efficiently select the next minimum
 * weight edge, ensuring that the graph's MST is formed optimally.
 * <p>
 * This data type assumes that the graph is connected and edge-weighted.
 * It also maintains methods to return the MST edges.
 *
 * @author Urvi Aryamane
 */
public class Prims {
    /**
     * Returns an iterable collection of edges that are part of the
     * Minimum Spanning Tree (MST) computed by Prim's algorithm.
     *
     * @return an iterable containing the edges of the MST
     */
    public Iterable<Edge> edges() {
        return mst;
    }

    /**
     * Constructs a new instance of the Prim's algorithm and computes the
     * Minimum Spanning Tree (MST) of the given edge-weighted graph.
     *
     * @param G the edge-weighted graph for which the MST is to be computed
     */
    public Prims(EdgeWeightedGraph G) {
        pq = new MinPQ<>();
        marked = new boolean[G.V()];
        mst = new LinkedList<>();
        visit(G, 0); // assumes G is connected
        while (!pq.isEmpty()) {
            Edge e = pq.delMin(); // Get lowest-weight
            int v = e.either(), w = e.other(v); // edge from pq.
            if (marked[v] && marked[w]) continue; // Skip if ineligible.
            mst.add(e); // Add edge to tree.
            if (!marked[v]) visit(G, v); // Add vertex to tree
            if (!marked[w]) visit(G, w); // (either v or w).
        }
    }

    /**
     * Marks the specified vertex and adds all edges connecting it to unmarked vertices
     * to the priority queue, ensuring they are considered for the Minimum Spanning Tree (MST).
     *
     * @param G the edge-weighted graph being processed
     * @param v the vertex to be marked and processed
     */
    private void visit(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v))
            if (!marked[e.other(v)]) pq.insert(e);
    }

    private final boolean[] marked; // MST vertices
    private final Queue<Edge> mst; // MST edges
    private final MinPQ<Edge> pq; // crossing (and ineligible) edges
}
