/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The {@code Graph} class represents a graph with edges and vertices.
 */
public class Graph {

    private final HashMap<Vertex, LinkedList<Edge>> adjacent;
    private final Vertex root;
    private int V;
    private int E;

    public Vertex getVertex() {
        return root;
    }

    /**
     * Initializes the root vertex of the graph and also adjacent list.
     *
     * @param root source vertex
     */
    public Graph(Vertex root) {
        this.V = 0;
        this.E = 0;
        this.root = root;
        adjacent = new HashMap<>();
    }

    /**
     * Adds a new Vertex into the adjacency list of vertices.
     */
    public void addVertex(Vertex v) {
        V++;
        adjacent.put(v, new LinkedList<>());
    }

    /**
     * Adds entry to the given Vertex as an adjacent vertex.
     */
    public void addEdge(Vertex u, Vertex v, double edgeWeight) {
        E++;
        adjacent.getOrDefault(u, new LinkedList<>()).add(new Edge(u, v, edgeWeight));
    }

    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     */
    public Iterator<Edge> edges(Vertex v) {
        if (adjacent.get(v) != null)
            return adjacent.get(v).iterator();
        else
            return null;
    }

    /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int V() {
        return V;
    }

    /**
     * Yields the number of edges in this graph.
     *
     * @return the number of edges in this graph
     */
    public int E() {
        return E;
    }

    /**
     * Yields a List of adjacent edges of a particular vertex in this graph.
     *
     * @return a HashMap of adjacent edges of a particular vertex in this graph
     */
    public HashMap<Vertex, LinkedList<Edge>> getAdjacent() {
        return adjacent;
    }
}