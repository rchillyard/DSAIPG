package com.phasmidsoftware.dsaipg.graphs.undirected;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterableImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for Graphs.
 * This should be extended by both directed and undirected graphs.
 *
 * @param <V>   the vertex type.
 * @param <Adj> the adjacency type: can be vertex or edge.
 */
abstract public class AbstractGraph<V, Adj> implements Graph<V, Adj> {

    /**
     * Method to add a vertex (without having to add an edge).
     *
     * @param vertex the vertex to be added.
     */
    public void addVertex(V vertex) {
        adjacentEdges.put(vertex, new Bag_Array<>());
    }

    /**
     * Retrieves an iterable collection of all vertices in the graph.
     *
     * @return a {@code SizedIterable<V>} that provides access to all vertices in the graph,
     * with efficient size retrieval and iteration capabilities.
     */
    public SizedIterable<V> vertices() {
        return SizedIterableImpl.create(adjacentEdges.keySet());
    }

    /**
     * Retrieves the entities adjacent to the specified vertex.
     *
     * @param v the vertex whose adjacent entities are to be retrieved.
     * @return an Iterable containing the adjacent entities for the given vertex.
     */
    public Iterable<Adj> adjacent(V v) {
        // NOTE an unknown vertex gives an empty Iterable, not the map's null.
        // Every caller here iterates the result -- Prim, ShortestPaths and
        // DiGraph's depth-first search all do -- so returning null handed each of
        // them a NullPointerException in waiting. Returning empty is also what
        // the Python port does.
        //
        // The result is NOT attached to the graph: it is immutable and asking
        // about a vertex does not create it. Use getAdjacencyBag for that.
        Bag<Adj> bag = adjacentEdges.get(v);
        return bag != null ? bag : Collections.emptyList();
    }

    /**
     * Retrieves the adjacency bag associated with a given vertex.
     * If the vertex does not already have an adjacency bag, a new empty bag is created,
     * added to the map, and returned.
     *
     * @param vertex the vertex for which to retrieve or create an adjacency bag
     * @return the adjacency bag associated with the specified vertex
     */
    protected Bag<Adj> getAdjacencyBag(V vertex) {
        return adjacentEdges.computeIfAbsent(vertex, k -> new Bag_Array<>());
    }

    /**
     * A map that associates each vertex with a collection (Bag) of its adjacent entities.
     * The keys of the map represent vertices of the graph, while the values are Bags that
     * contain the adjacency information for each vertex, allowing multiple occurrences of
     * adjacent entities.
     *
     * @param <V>   the type representing vertices of the graph.
     * @param <Adj> the type representing the adjacent entities associated with each vertex.
     */
    protected final Map<V, Bag<Adj>> adjacentEdges = new HashMap<>();
}
