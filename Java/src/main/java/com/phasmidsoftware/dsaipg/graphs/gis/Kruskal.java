package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue;
import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;
import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue_BinaryHeap;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.graphs.union_find.TypedUF;
import com.phasmidsoftware.dsaipg.graphs.union_find.TypedUF_HWQUPC;
import com.phasmidsoftware.dsaipg.graphs.union_find.UFException;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

/**
 * The {@code Kruskal} class implements Kruskal's algorithm for finding the minimum spanning tree (MST)
 * of an edge-weighted, undirected graph. It extends the {@code MST} abstract class and utilizes
 * data structures such as a priority queue and a union-find structure for efficient processing.
 *
 * This class operates on graphs represented by the {@code EdgeGraph<V, X>} interface, where edges
 * are defined by a pair of vertices and an associated attribute. The attribute must implement the
 * {@code Comparable} interface for edge weight comparisons, and the {@code Sequenced} interface
 * for sequence assignment in the resulting MST.
 *
 * The algorithm processes edges in non-decreasing order of their weights, adding them to the MST
 * if they connect disjoint components without forming cycles.
 *
 * @param <V> the type representing vertices in the graph.
 * @param <X> the type of the edge attributes; must implement {@code Comparable<X>} and {@code Sequenced}.
 */
public class Kruskal<V, X extends Comparable<X> & Sequenced> extends MST<V, X> {

    /**
     * Creates an undirected edge connecting two vertices with an associated attribute.
     *
     * @param <V> the vertex type
     * @param <X> the attribute type; must implement {@code Comparable<X>}
     * @param v1  the first vertex
     * @param v2  the second vertex
     * @param x   the attribute associated with the edge
     * @return an {@code Edge<V, X>} instance that represents the connection between {@code v1} and {@code v2} with the specified attribute
     */
    public static <V, X extends Comparable<X>> Edge<V, X> createEdge(V v1, V v2, X x) {
        return new Edge<>(v1, v2, x);
    }

    /**
     * Retrieves the minimum spanning tree (MST) as an edge-weighted graph.
     * This method constructs and returns an {@code EdgeGraph} instance containing
     * edges from the priority queue in the order they are processed. Each edge in
     * the resulting graph is assigned a unique sequence number during this operation.
     *
     * @return an {@code EdgeGraph<V, X>} instance representing the minimum spanning tree (MST).
     */
    public EdgeGraph<V, X> getMST() {
        int sequence = 0;
        EdgeGraph<V, X> result = new Graph_Edges<>();
        for (Edge<V, X> edge : queue) {
            edge.getAttribute().setSequence(sequence++);
            result.addEdge(edge);
        }
        return result;
    }

    /**
     * Constructs a Kruskal algorithm instance for finding the minimum spanning tree (MST)
     * of the given edge-weighted graph. The constructor initializes the necessary data structures
     * such as a priority queue for edges and a union-find structure for connected components,
     * and executes the algorithm to compute the MST.
     * CONSIDER having a simpler constructor which just sets up the necessary structures, then having a run method which takes a graph and outputs an Iterable.
     *
     * @param graph the edge-weighted graph for which the MST is to be calculated. It must
     *              provide access to its vertices and edges via methods defined in the EdgeGraph interface.
     */
    public Kruskal(EdgeGraph<V, X> graph) {
        this.queue = new Queue_Elements<>();
//        showEdgesInSequence(graph);
        this.pq = createPQ(graph.edges());
        this.uf = createUF(graph.vertices());
        this.size = uf.size();
        try {
            mst = runKruskal();
        } catch (com.phasmidsoftware.dsaipg.util.general.ImplementationMissing e) {
            // An unwritten exercise must not be dressed up as an empty MST.
            // Swallowing it here reported the tunnels tests as genuine failures --
            // "the MST is empty" -- when the real answer is that UF_HWQUPC.find has
            // yet to be written. Rethrowing lets CancelOnNotImplemented report them
            // as skipped, which is the truth. Inert in this tree, where the
            // exercises are written.
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this
        }
    }

    /**
     * Executes Kruskal's algorithm to find the minimum spanning tree (MST) of an edge-weighted graph.
     * This method uses a union-find data structure to identify and merge connected components and a
     * priority queue to retrieve the edges in the order of increasing weights.
     * <p>
     * During the process, edges are added to the MST only if they connect two disjoint components,
     * ensuring no cycles are formed.
     *
     * @return an iterable collection of edges that form the minimum spanning tree.
     * @throws PQException if there is an error during priority queue operations.
     * @throws UFException if there is an error in union-find operations.
     */
    private Iterable<Edge<V, X>> runKruskal() throws PQException, UFException {
        while (!pq.isEmpty() && ((SizedIterable<?>) queue).size() < size - 1) {
            Edge<V, X> edge = pq.take();
            V s1 = edge.get(), s2 = edge.getOther(s1);
            if (!uf.connected(s1, s2)) {
                uf.union(s1, s2);
                queue.offer(edge);
            }
        }
        Collection<Edge<V, X>> result = new ArrayList<>();
        for (Edge<V, X> edge : queue) result.add(edge);
        return result;
    }

    /**
     * Creates and initializes a union-find data structure for the given collection of vertices.
     * This method utilizes a height-weighted quick union with path compression algorithm
     * to manage the disjoint sets of the given vertices.
     *
     * @param vertices the collection of vertices over which the union-find operations will be performed.
     *                 It must implement the SizedIterable interface, ensuring size and iteration capabilities.
     * @return an instance of TypedUF representing the union-find structure for the provided vertices.
     */
    private TypedUF<V> createUF(SizedIterable<V> vertices) {
        return new TypedUF_HWQUPC<>(vertices);
    }

    /**
     * Creates a priority queue of edges using a binary heap implementation.
     * The priority queue is constructed with a custom comparator that orders the edges
     * based on their attributes.
     *
     * This method initializes the priority queue with the total capacity equal to the
     * number of edges provided and populates it with the edges from the input Iterable.
     *
     * @param edges the collection of edges to be included in the priority queue. It must
     *              implement the SizedIterable interface to determine the total number of edges.
     * @return a new PriorityQueue instance containing the edges ordered by their attributes.
     */
    private PriorityQueue<Edge<V, X>> createPQ(SizedIterable<Edge<V, X>> edges) {
        PriorityQueue<Edge<V, X>> result = new PriorityQueue_BinaryHeap<>(edges.size(), false, Comparator.comparing(Edge::getAttribute), false);
        for (Edge<V, X> e : edges) result.give(e);
        return result;
    }

    /**
     * Displays the edges of the given graph in a sequence by using a priority queue to sort them.
     * Each edge is printed to the console in the order it is dequeued from the priority queue.
     * This method is intended for debugging purposes only.
     *
     * @param graph the edge-weighted graph whose edges will be displayed in sequence.
     *              The edges must be accessible through the edges() method of the graph.
     */
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
}
