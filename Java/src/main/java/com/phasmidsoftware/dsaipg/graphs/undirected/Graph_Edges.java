package com.phasmidsoftware.dsaipg.graphs.undirected;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

import java.util.function.Predicate;

/**
 * Represents a graph structure where vertices are connected by edges, with support for edge-specific attributes.
 * This class extends the {@code AbstractGraph} class for generic graph behavior and implements the
 * {@code EdgeGraph} interface for edge-based operations.
 *
 * @param <V> the vertex type.
 * @param <E> the edge attribute type.
 */
public class Graph_Edges<V, E> extends AbstractGraph<V, Edge<V, E>> implements EdgeGraph<V, E> {

    /**
     * Retrieves all edges in the graph as a collection.
     * Combines all edges from adjacency lists into a single iterable.
     *
     * @return a SizedIterable containing all edges present in the graph.
     */
    public SizedIterable<Edge<V, E>> edges() {
        Bag<Edge<V, E>> result = new Bag_Array<>();
        for (Iterable<Edge<V, E>> b : adjacentEdges.values())
            for (Edge<V, E> e : b)
                result.add(e);
        return result;
    }

    /**
     * Adds an edge to the graph if it satisfies the given predicate condition.
     * The edge is added to the adjacency bag of its "from" vertex,
     * and an adjacency bag is ensured for the "to" vertex.
     *
     * @param edge      the edge to be added, defined by its two vertices and optional attributes.
     * @param predicate a condition that determines whether the edge should be added to the graph.
     */
    public void addEdge(Edge<V, E> edge, Predicate<Edge<V, E>> predicate) {
        if (predicate.test(edge)) {
            V v = edge.get();
            // First, we add the edge to the adjacency bag for the "from" vertex;
            getAdjacencyBag(v).add(edge);
            // Then, we simply ensure that the "to" vertex has an adjacency bag (which might be empty)
            getAdjacencyBag(edge.getOther(v));
        }
    }

    /**
     * Adds an edge to the graph using the specified vertices, attribute, and predicate.
     * The edge is only added if the given predicate evaluates to true.
     *
     * @param from      the starting vertex of the edge.
     * @param to        the ending vertex of the edge.
     * @param attribute the attribute associated with the edge.
     * @param predicate a condition to determine if the edge should be added.
     */
    public void addEdge(V from, V to, E attribute, Predicate<Edge<V, E>> predicate) {
        addEdge(new Edge<>(from, to, attribute), predicate);
    }

    @Override
    public String toString() {
        return adjacentEdges.toString();
    }

}
