/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.undirected;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.util.SizedIterable;

import java.util.function.Predicate;

public class Graph_Edges<V, E> extends AbstractGraph<V, Edge<V, E>> implements EdgeGraph<V, E> {

    public SizedIterable<Edge<V, E>> edges() {
        Bag<Edge<V, E>> result = new Bag_Array<>();
        for (Iterable<Edge<V, E>> b : adjacentEdges.values())
            for (Edge<V, E> e : b)
                result.add(e);
        return result;
    }

    public void addEdge(Edge<V, E> edge, Predicate<Edge<V, E>> predicate) {
        if (predicate.test(edge)) {
            V v = edge.get();
            // First, we add the edge to the adjacency bag for the "from" vertex;
            getAdjacencyBag(v).add(edge);
            // Then, we simply ensure that the "to" vertex has an adjacency bag (which might be empty)
            getAdjacencyBag(edge.getOther(v));
        }
    }

    public void addEdge(V from, V to, E attribute, Predicate<Edge<V, E>> predicate) {
        addEdge(new Edge<>(from, to, attribute), predicate);
    }

    @Override
    public String toString() {
        return adjacentEdges.toString();
    }

}