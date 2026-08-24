package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue;
import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;
import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue_BinaryHeap;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The Prim class represents a solution to the Minimum Spanning Tree (MST) problem using Prim's
 * algorithm. It extends the MST abstract class and provides an implementation for finding the MST
 * for a given edge-weighted graph. This class uses a lazy approach where edges are processed
 * incrementally during the traversal of the graph.
 *
 * @param <V> the type of the vertices in the graph
 * @param <X> the type of the attributes associated with the edges (e.g., weights), which must be
 *            both Comparable and Sequenced.
 */
public class Prim<V, X extends Comparable<X> & Sequenced> extends MST<V, X> implements Iterable<Edge<V, X>> {

    /**
     * Creates a new edge connecting two vertices with a specific attribute.
     *
     * @param v1  the first vertex of the edge
     * @param v2  the second vertex of the edge
     * @param x   the attribute associated with the edge
     * @param <V> the type of the vertices
     * @param <X> the type of the attribute, which must be comparable
     * @return a new instance of {@code Edge<V, X>} representing the edge between {@code v1} and {@code v2} with the specified attribute
     */
    public static <V, X extends Comparable<X>> Edge<V, X> createEdge(V v1, V v2, X x) {
        return new Edge<>(v1, v2, x);
    }

    /**
     * Generates and returns the Minimum Spanning Tree (MST) for the edge-weighted graph.
     * Each edge in the MST is assigned a unique sequence number based on its order of inclusion.
     *
     * @return an EdgeGraph object representing the MST of the given graph.
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
     * Returns an iterator over the edges in the Minimum Spanning Tree (MST) of the graph.
     * The edges are iterated in the order they were added to the MST.
     *
     * @return an iterator over the edges in the MST of type {@code Edge<V, X>}.
     */
    @NotNull
    public Iterator<Edge<V, X>> iterator() {
        Collection<Edge<V, X>> result = new ArrayList<>();
        for (Edge<V, X> edge : mst) result.add(edge);
        return result.iterator();
    }

    /**
     * Constructor to initialize the Prim object that calculates the Minimum Spanning Tree (MST)
     * for an edge-weighted graph using Prim's algorithm.
     * CONSIDER having a simpler constructor which just sets up the necessary structures, then having a run method which takes a graph and outputs an Iterable.
     *
     * @param graph the edge-weighted graph over which Prim's algorithm will be executed.
     *              The graph must provide its vertices and edges for the algorithm.
     */
    public Prim(EdgeGraph<V, X> graph) {
        this.queue = new Queue_Elements<>();
        this.vertexToInteger = new HashMap<>();
        this.graph = graph;
        int size = graph.vertices().size();
        marked = new boolean[size];
        // TO BE IMPLEMENTED  : finish construction
         this.pq = null;
         this.mst = null;
        // END SOLUTION
    }

    /**
     * Executes Prim's algorithm to compute the minimum spanning forest of the graph.
     * The algorithm starts from all vertices, ensuring that disconnected components
     * are also processed to form a spanning forest.
     *
     * @return an Iterable containing the edges that constitute the minimum spanning forest.
     * @throws PQException if an issue occurs with the priority queue operations during the algorithm.
     */
    private Iterable<Edge<V, X>> runPrim() throws PQException {
        // TO BE IMPLEMENTED  : finish construction
         throw new RuntimeException("implementation missing");
        // END SOLUTION
    }

    /**
     * Executes Prim's algorithm to compute the minimum spanning tree (MST) starting from a given vertex.
     * The algorithm explores all edges connected to the selected vertex and employs a priority queue
     * to manage the edges in ascending order based on their weights. The process continues until
     * the MST is fully constructed or the priority queue becomes empty.
     *
     * @param v the starting vertex from which the algorithm begins computing the MST.
     * @throws PQException if an error occurs in the priority queue operations, such as attempting to retrieve
     *                     an element from an empty queue.
     */
    private void prim(V v) throws PQException {
        scan(v); // scan vertex v and add all its edges to the fringe vertices to the pq
        while (!pq.isEmpty()) {                        // better to stop when mst has V-1 edges
            Edge<V, X> e = pq.take();                      // smallest edge on pq
            V u = e.get(), w = e.getOther(u);        // two endpoints
            int ui = vertexToInteger.get(u), wi = vertexToInteger.get(w); // their indices
            assert marked[ui] || marked[wi];
            if (marked[ui] && marked[wi]) continue;      // lazy, both v and w already scanned
            queue.offer(e);                            // add e to queue
            //weight += e.weight();
            if (!marked[ui]) scan(v);               // v becomes part of tree
            if (!marked[wi]) scan(w);               // w becomes part of tree
        }
    }

    /**
     * Scans the specified vertex to add all edges incident to it into the priority queue,
     * if the edges connect to vertices that have not yet been marked (processed).
     *
     * @param v the vertex to be scanned. It represents the starting point for finding edges
     *          that connect to unmarked vertices.
     */
    private void scan(V v) {
        int vi = vertexToInteger.get(v);
        assert !marked[vi];
        marked[vi] = true;
        for (Edge<V, X> e : graph.adjacent(v)) {
            V w = e.getOther(v);
            int wi = vertexToInteger.get(w);
            if (!marked[wi])
                pq.give(e);
        }
    }

    /**
     * Creates and returns a priority queue of edges based on a specified comparator for their attributes.
     * The priority queue is initialized with the given edges and configured as a min-priority queue.
     * This method is copied from Kruskal.java, needed for creating a priority queue of edges
     * those edges connecting the mst to the fringe vertices
     *
     * @param edges the collection of edges to be added to the priority queue.
     *              The size of this collection defines the initial capacity of the priority queue.
     * @return a PriorityQueue of type {@code Edge<V, X>} containing all the elements from the input collection,
     *         ordered by their attributes in ascending order.
     */
    private PriorityQueue<Edge<V, X>> createPQ(SizedIterable<Edge<V, X>> edges) {
        PriorityQueue<Edge<V, X>> result = new PriorityQueue_BinaryHeap<>(edges.size(), false, Comparator.comparing(Edge::getAttribute), false);
        for (Edge<V, X> e : edges) result.give(e);
        return result;
    }

    /**
     * A queue that holds the edges included in the Minimum Spanning Tree (MST)
     * during the execution of Prim's algorithm.
     * The edges are added to the queue in the sequence they are selected as part of the MST.
     * This provides a First-In-First-Out (FIFO) collection of edges, which maintains
     * the order of inclusion in the MST.
     * <p>
     * The queue is implemented using the {@code Queue} interface to provide basic
     * queue operations such as adding, removing, and checking emptiness. It is only
     * used within the computation of the MST and is immutable once populated.
     *
     * @param <V> the vertex type associated with each edge.
     * @param <X> the attribute type for the edge, typically the weight or cost, which must be comparable.
     */
    private final Queue<Edge<V, X>> queue;
    /**
     * Represents an iterable collection of edges that form the Minimum Spanning Tree (MST)
     * of an edge-weighted graph. Each edge in the collection connects two vertices and
     * may have an associated attribute (e.g., weight).
     *
     * The MST is computed using Prim's algorithm and stored as an iterable object to allow
     * sequential access to the edges in the order they were added during the algorithm.
     * This attribute is initialized when the MST computation is executed and can be
     * accessed for traversal or analysis of the resulting tree structure.
     *
     * @param <V> the vertex type of the edges in the MST
     * @param <X> the attribute type of the edges in the MST; must be comparable to allow
     *            sorting or prioritization (e.g., by weight)
     */
    private Iterable<Edge<V, X>> mst;
    /**
     * Priority queue of edges used by Prim's algorithm to construct the minimum spanning tree (MST).
     * This priority queue maintains edges with one endpoint inside the spanning tree and the other outside,
     * facilitating the edge selection process based on minimum weight.
     * <p>
     * The elements of the priority queue are instances of {@code Edge<V, X>}, where:
     * - {@code V} represents the type of vertices connected by the edges.
     * - {@code X} represents the type of the edge attribute (e.g., weight), which must be comparable.
     * <p>
     * The priority is determined by the edge attributes, ensuring that the edge with the smallest attribute
     * is efficiently accessed and added to the MST during the algorithm's execution.
     * <p>
     * This field is private and final to ensure immutability and maintain encapsulation. It is dynamically
     * updated as edges are scanned and processed during the algorithm's execution.
     */
    private final PriorityQueue<Edge<V, X>> pq;
    /**
     * Boolean array that keeps track of visited vertices during the execution of Prim's algorithm.
     * Each index in the array corresponds to a vertex in the graph, where:
     * - {@code true} indicates that the vertex has been marked as processed.
     * - {@code false} indicates that the vertex has not yet been processed.
     */
    private final boolean[] marked;
    /**
     * Represents the edge-weighted graph that Prim's algorithm operates on.
     * The graph is an instance of the {@link EdgeGraph} interface, which provides
     * the necessary methods to manage vertices and edges within the graph.
     *
     * @param <V> the type of vertices in the graph
     * @param <X> the type of attributes (weights) associated with edges, which must be comparable
     */
    private final EdgeGraph<V, X> graph;
    /**
     * A mapping that associates each vertex of type {@code V} with a unique integer.
     * This map is used to facilitate vertex identification and provide an efficient way to manage
     * and reference vertices in the context of graph processing, particularly in Prim's algorithm
     * for finding the Minimum Spanning Tree (MST).
     */
    private final Map<V, Integer> vertexToInteger;

}
