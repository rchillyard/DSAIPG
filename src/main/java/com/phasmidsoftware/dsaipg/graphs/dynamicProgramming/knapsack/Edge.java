/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

/**
 * The {@code Edge} class represents the Weighted Edge with two Vertices.
 */
public class Edge {
    private final Vertex U;
    private final Vertex V;
    private final double edgeWeight;

    /**
     * Initializes an edge between vertices {@code U} and {@code V} of
     * the given {@code edgeWeight}.
     *
     * @param U          source vertex
     * @param V          destination vertex
     * @param edgeWeight the weight of this edge
     */
    public Edge(Vertex U, Vertex V, double edgeWeight) {
        this.U = U;
        this.V = V;
        this.edgeWeight = edgeWeight;
    }

    /**
     * Returns the weight of this edge.
     *
     * @return the edgeWeight of this edge
     */
    public double getEdgeWeight() {
        return this.edgeWeight;
    }

    /**
     * Returns source endpoint of this edge.
     *
     * @return source endpoint of this edge
     */
    public Vertex source() {
        return this.U;
    }

    /**
     * Returns destination endpoint of this edge.
     *
     * @return destination endpoint of this edge
     */
    public Vertex destination() {
        return this.V;
    }

}