/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue;
import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;
import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.graphs.union_find.TypedUF;
import com.phasmidsoftware.dsaipg.graphs.union_find.TypedUF_HWQUPC;
import com.phasmidsoftware.dsaipg.graphs.union_find.UFException;
import com.phasmidsoftware.dsaipg.util.SizedIterable;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This is a generic solution for Kruskal's algorithm to find the minimum spanning tree of an edge-weighted graph
 *
 * @param <V> is the type of each vertex.
 */
public class Kruskal<V, X extends Comparable<X> & Sequenced> extends MST<V, X> {

    // CONSIDER having a simpler constructor which just sets up the necessary structures, then having a run method which takes a graph and outputs an Iterable.
    public Kruskal(EdgeGraph<V, X> graph) {
        this.queue = new Queue_Elements<>();
//        showEdgesInSequence(graph);
        this.pq = createPQ(graph.edges());
        this.uf = createUF(graph.vertices());
        this.size = uf.size();
        try {
            mst = runKruskal();
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this
        }
    }

    public EdgeGraph<V, X> getMST() {
        int sequence = 0;
        EdgeGraph<V, X> result = new Graph_Edges<>();
        for (Edge<V, X> edge : queue) {
            edge.getAttribute().setSequence(sequence++);
            result.addEdge(edge);
        }
        return result;
    }


    private Iterable<Edge<V, X>> runKruskal() throws PQException, UFException {
        while (!pq.isEmpty() && ((SizedIterable<?>) queue).size() < size - 1) {
            Edge<V, X> edge = pq.take();
            V s1 = edge.get(), s2 = edge.getOther(s1);
            if (!uf.connected(s1, s2)) {
                uf.union(s1, s2);
                queue.offer(edge);
            }
        }
        ArrayList<Edge<V, X>> result = new ArrayList<>();
        for (Edge<V, X> edge : queue) result.add(edge);
        return result;
    }

    private TypedUF<V> createUF(SizedIterable<V> vertices) {
        return new TypedUF_HWQUPC<>(vertices);
    }

    private PriorityQueue<Edge<V, X>> createPQ(SizedIterable<Edge<V, X>> edges) {
        PriorityQueue<Edge<V, X>> result = new PriorityQueue<>(edges.size(), false, Comparator.comparing(Edge::getAttribute), false);
        for (Edge<V, X> e : edges) result.give(e);
        return result;
    }

    private void showEdgesInSequence(EdgeGraph<V, X> graph) {
        // TODO remove this debugging code
        PriorityQueue<Edge<V, X>> tempPQ = createPQ(graph.edges());
        while (!tempPQ.isEmpty()) {
            try {
                System.out.println(tempPQ.take());
            } catch (PQException e) {
                e.printStackTrace(); // TODO log this
            }
        }
    }

    private final Queue<Edge<V, X>> queue;
    private final PriorityQueue<Edge<V, X>> pq;
    private final TypedUF<V> uf;
    private final int size;


    public static <V, X extends Comparable<X>> Edge<V, X> createEdge(V v1, V v2, X x) {
        return new Edge<>(v1, v2, x);
    }
}