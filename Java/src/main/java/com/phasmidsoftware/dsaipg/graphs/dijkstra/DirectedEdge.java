package com.phasmidsoftware.dsaipg.graphs.dijkstra;
/*Copyright © 2000–2019, Robert Sedgewick and Kevin Wayne.*/

/**
 * The {@code DirectedEdge} class represents a weighted edge in an
 * {@link EdgeWeightedDigraph}. Each edge consists of two integers
 * (naming the two vertices) and a real-value weight. The data type
 * provides methods for accessing the two endpoints of the directed edge and
 * the weight.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class DirectedEdge {
    /**
     * Returns the tail vertex of the directed edge.
     *
     * @return the tail vertex of the directed edge
     */
    public int from() {
        return v;
    }

    /**
     * Returns the head vertex of the directed edge.
     *
     * @return the head vertex of the directed edge
     */
    public int to() {
        return w;
    }

    /**
     * Returns the weight of the directed edge.
     *
     * @return the weight of the directed edge
     */
    public double weight() {
        return weight;
    }

    /**
     * Returns a string representation of the directed edge.
     *
     * @return a string representation of the directed edge
     */
    public String toString() {
        return v + "->" + w + " " + String.format("%5.2f", weight);
    }

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given {@code weight}.
     *
     * @param v      the tail vertex
     * @param w      the head vertex
     * @param weight the weight of the directed edge
     * @throws IllegalArgumentException if either {@code v} or {@code w}
     *                                  is a negative integer
     * @throws IllegalArgumentException if {@code weight} is {@code NaN}
     */
    public DirectedEdge(int v, int w, double weight) {
        if (v < 0) throw new IllegalArgumentException("Vertex names must be non-negative integers");
        if (w < 0) throw new IllegalArgumentException("Vertex names must be non-negative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    /**
     * The tail vertex of the directed edge.
     * Represents the starting point of the edge in a directed graph.
     */
    private final int v;
    /**
     * The head vertex of the directed edge.
     * Represents the endpoint of the edge in a directed graph.
     */
    private final int w;
    /**
     * The weight of the directed edge.
     * Represents the real-valued weight associated with the directed edge,
     * typically used to signify cost, distance, or capacity in an edge-weighted graph.
     */
    private final double weight;
}
