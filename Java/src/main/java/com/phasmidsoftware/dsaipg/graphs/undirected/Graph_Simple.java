package com.phasmidsoftware.dsaipg.graphs.undirected;

/**
 * Graph_Simple is a basic implementation of an undirected graph where vertices are represented by integers.
 * It extends the AbstractGraph class, managing adjacency relationships for vertices without attributes.
 */
public class Graph_Simple extends AbstractGraph<Integer, Integer> {
    /**
     * Retrieves the vertices adjacent to the specified vertex in the graph.
     *
     * @param vertex the vertex whose adjacent vertices are to be retrieved.
     * @return an Iterable containing the adjacent vertices of the given vertex.
     */
    public Iterable<Integer> adjacent(int vertex) {
        return super.adjacent(vertex);
    }

    /**
     * Adds an undirected edge between two vertices in the graph by updating their adjacency bags.
     *
     * @param v1 the first vertex.
     * @param v2 the second vertex.
     */
    public void addEdge(int v1, int v2) {
        getAdjacencyBag(v1).add(v2);
        getAdjacencyBag(v2).add(v1);
    }

    /**
     * Returns the string representation of the adjacency relationships of the graph.
     * This method delegates to the string representation of the internal adjacency structure.
     *
     * @return a string describing the adjacency relationships of the graph.
     */
    @Override
    public String toString() {
        return adjacentEdges.toString();
    }

    /**
     * Default constructor for the Graph_Simple class.
     * Initializes an empty instance of a simple, undirected graph.
     * In this graph implementation, vertices are represented by integers, and adjacency relationships
     * are managed without any edge-specific attributes.
     */
    public Graph_Simple() {
    }
}
