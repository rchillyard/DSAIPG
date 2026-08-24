package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The MST class is an abstract representation of a Minimum Spanning Tree (MST) in a graph.
 * It contains methods and data structures for constructing and representing the MST
 * as a collection of edges. This class must be extended by specific algorithms (e.g., Prim's, Kruskal's,
 * or Borůvka's) to compute the MST for a given graph.
 *
 * @param <V> the type of the vertices in the graph
 * @param <X> the type of the attributes associated with the edges (usually representing weight),
 *            which must be both comparable and sequenced
 */
public abstract class MST<V, X extends Comparable<X> & Sequenced> implements Iterable<Edge<V, X>> {
    public MST() {
    }

    /**
     * Abstract method to compute and retrieve the Minimum Spanning Tree (MST) of the graph.
     * This method should be implemented by subclasses, typically using specific algorithms such as
     * Prim's, Kruskal's, or Borůvka's. The resulting MST is represented as an instance of the
     * EdgeGraph interface, containing all vertices and edges that form the MST.
     *
     * @return an EdgeGraph representing the MST of the graph.
     */
    abstract EdgeGraph<V, X> getMST();

    /**
     * Returns an iterator over the edges present in the Minimum Spanning Tree (MST).
     * The iterator iterates through all edges stored in the `mst` collection.
     *
     * @return an iterator of type {@code Iterator<Edge<V, X>>}, where each element
     * represents an edge in the MST.
     */
    @NotNull
    public Iterator<Edge<V, X>> iterator() {
        Collection<Edge<V, X>> result = new ArrayList<>();
        for (Edge<V, X> edge : mst) result.add(edge);
        return result.iterator();
    }

    protected Iterable<Edge<V, X>> mst;

}
