package com.phasmidsoftware.dsaipg.graphs.undirected;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

import java.util.Map;
import java.util.Objects;
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
     * Retrieves all edges in the graph as a collection, each appearing exactly once.
     * <p>
     * NOTE an edge sits in the adjacency bag of BOTH its endpoints, so gathering
     * every bag would report each edge twice. An edge is therefore collected only
     * from the bag of the vertex that {@link Edge#get} returns, which is one
     * endpoint and not the other. A self-loop occupies one bag once, and is
     * likewise reported once.
     *
     * @return a SizedIterable containing all edges present in the graph.
     */
    public SizedIterable<Edge<V, E>> edges() {
        Bag<Edge<V, E>> result = new Bag_Array<>();
        for (Map.Entry<V, Bag<Edge<V, E>>> entry : adjacentEdges.entrySet())
            for (Edge<V, E> e : entry.getValue())
                if (Objects.equals(e.get(), entry.getKey()))
                    result.add(e);
        return result;
    }

    /**
     * Adds an edge to the graph if it satisfies the given predicate condition.
     * The edge is added to the adjacency bag of BOTH its vertices, because this
     * graph is undirected and an edge is incident on each of its endpoints alike.
     * <p>
     * NOTE it used to go into the bag of the "from" vertex only, the "to" vertex
     * merely being given an empty bag. That made {@code adjacent(v)} report not the
     * edges at v but the edges that happened to be WRITTEN with v first -- so an
     * algorithm walking the graph by adjacency saw an arbitrary subset of it.
     * {@code Prim} did exactly that and returned a spanning forest missing a
     * vertex. Kruskal and Boruvka were unaffected only because they read
     * {@link #edges} instead.
     * {@link Graph_Simple#addEdge} always recorded both directions, so this now
     * agrees with it.
     *
     * @param edge      the edge to be added, defined by its two vertices and optional attributes.
     * @param predicate a condition that determines whether the edge should be added to the graph.
     */
    public void addEdge(Edge<V, E> edge, Predicate<Edge<V, E>> predicate) {
        if (predicate.test(edge)) {
            V v = edge.get(), w = edge.getOther(v);
            getAdjacencyBag(v).add(edge);
            // A self-loop is incident on one vertex, so it belongs in one bag once.
            if (Objects.equals(v, w)) return;
            getAdjacencyBag(w).add(edge);
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
