package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.graphs.union_find.TypedUF;
import com.phasmidsoftware.dsaipg.graphs.union_find.TypedUF_HWQUPC;
import com.phasmidsoftware.dsaipg.graphs.union_find.UFException;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a generic solution for Boruvka's algorithm to find the minimum spanning tree of an edge-weighted graph
 *
 * @param <V> is the type of each vertex.
 * @author Shen Wang (I think)
 */
public class Boruvka<V, X extends Comparable<X> & Sequenced> extends MST<V, X> {

    /**
     * Method to compute and return the Minimum Spanning Tree (MST).
     * The MST is computed by assigning a sequence number to each edge and adding it to the resulting graph.
     *
     * @return an EdgeGraph representing the Minimum Spanning Tree of the current graph.
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
     * Creates and returns a new Edge instance connecting two specified vertices with an associated attribute.
     *
     * @param v1  the first vertex of the edge
     * @param v2  the second vertex of the edge
     * @param x   the attribute associated with the edge, typically representing an edge property such as weight
     * @param <V> the type of the vertices
     * @param <X> the type of the attribute, must extend Comparable
     * @return a new Edge instance connecting the given vertices with the specified attribute
     */
    public static <V, X extends Comparable<X>> Edge<V, X> createEdge(V v1, V v2, X x) {
        return new Edge<>(v1, v2, x);
    }

    /**
     * Constructs a Boruvka instance with the specified graph. This constructor initializes
     * the internal structures required to compute the Minimum Spanning Tree (MST) using Borůvka's algorithm.
     *
     * @param graph the graph for which the MST should be computed. The graph is represented as an
     *              instance of {@code EdgeGraph<V, X>}, where {@code V} is the type of the vertices
     *              and {@code X} is the type of the edge attributes (typically the weight of the edge).
     */
    public Boruvka(EdgeGraph<V, X> graph) {
        this.vertexToInteger = new HashMap<>();
        int count = 0;
        for (V vertex : graph.vertices()) {
            vertexToInteger.put(vertex, count++);
        }
        this.graph = graph;
        this.queue = new Queue_Elements<>();
        SizedIterable<V> vertices = graph.vertices();
        this.uf = createUF(vertices);  // Pass the vertices directly to createUF.
        this.size = vertices.size();
        this.mst = new ArrayList<>();  // Initialize mst to an empty list.
        try {
            mst = runBoruvka();
        } catch (com.phasmidsoftware.dsaipg.util.general.ImplementationMissing e) {
            // An unwritten exercise must not be dressed up as an empty MST. Swallowing
            // it here left the student tree reporting three tunnels tests as genuine
            // failures -- "the MST is empty" -- when the real answer was that
            // UF_HWQUPC.find has yet to be written. Rethrowing lets
            // CancelOnNotImplemented report them as skipped, which is the truth.
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); // TODO log this
        }
    }

    /**
     * Executes Borůvka's algorithm to compute the Minimum Spanning Tree (MST) of the graph.
     * This method iteratively finds the cheapest edge for each component and merges components
     * using the union-find data structure until a single spanning tree is created or all vertices
     * are connected.
     *
     * @return an Iterable of edges representing the edges in the Minimum Spanning Tree (MST).
     * @throws UFException if there is an error with union-find operations during MST construction.
     */
    private Iterable<Edge<V, X>> runBoruvka() throws UFException {
        // Create an array to hold the cheapest edge for each component.
        // Initially, there are as many components as the vertices
        // and all cheapest edge of each component is null, at this stage,
        // to be added
        // List<Edge<V, X>> cheapest = new ArrayList<Edge<V, X>>();
        // Edge<V, X>[] cheapest = new Edge[size];
        // queue is a queue of all edges in current mst
        // size is total number vertices in the graph
        // since MST is a DAG, the number of edges = number of vertices - 1
        // once this condition is fulfilled, we get a MST
        // before the queue of edges has size-1 elements, keep adding edges to the MST.
        for (int t = 1; t < size && this.queue.size() < size - 1; t = t + t) {

            // foreach tree in forest, find the closest edge
            // if edge weights are equal, ties are broken in favor of first edge in G.edges()
            //noinspection unchecked
            Edge<V, X>[] closest = new Edge[size];
            for (Edge<V, X> e : graph.edges()) {
                V v = e.get(), w = e.getOther(v);
                int vi = vertexToInteger.get(v), wi = vertexToInteger.get(w);
                int i = uf.find(vi), j = uf.find(wi);
                if (i == j) continue;   // same tree
                if (closest[i] == null || e.getAttribute().compareTo(closest[i].getAttribute()) < 0) closest[i] = e;
                if (closest[j] == null || e.getAttribute().compareTo(closest[j].getAttribute()) < 0) closest[j] = e;
            }

            // add newly discovered edges to MST
            for (int i = 0; i < size; i++) {
                Edge<V, X> e = closest[i];
                if (e != null) {
                    V v = e.get(), w = e.getOther(v);
                    int vi = vertexToInteger.get(v), wi = vertexToInteger.get(w);
                    // don't add the same edge twice
                    if (uf.find(vi) != uf.find(wi)) {
                        queue.offer(e);
                        //weight += e.weight();
                        uf.union(v, w);
                    }
                }
            }
        }
        // Convert the queue to an ArrayList and return it.
        List<Edge<V, X>> result = new ArrayList<>();
        for (Edge<V, X> edge : queue) result.add(edge);
        return result;
    }

    /**
     * Creates a new instance of a union-find (TypedUF) data structure for the specified set of vertices.
     * The implementation used is based on the TypedUF_HWQUPC class, which represents a
     * height-weighted quick union with path compression.
     *
     * @param vertices the collection of vertices, represented as a SizedIterable, for which
     *                 the union-find structure will be initialized. Each unique vertex will be
     *                 assigned an internal index within the structure.
     * @return a TypedUF<V> instance initialized with the provided vertices.
     */
    private TypedUF<V> createUF(SizedIterable<V> vertices) {
        return new TypedUF_HWQUPC<>(vertices);
    }

    /**
     * The graph used to find the Minimum Spanning Tree (MST).
     * This represents the structure containing vertices and weighted edges,
     * where the weights are used to determine the MST.
     */
    private final EdgeGraph<V, X> graph;

    /**
     * A queue used to store the edges of the minimum spanning tree (MST) during the execution
     * of the Boruvka algorithm. This queue follows the FIFO (First In, First Out) principle
     * and ensures edges are processed in the correct order for MST computation.
     */
    // The queue to store the edges in the MST.
    private final Queue_Elements<Edge<V, X>> queue;

    /**
     * The union-find data structure used to efficiently manage sets of connected components
     * within the graph represented by this class.
     * <p>
     * This field is an implementation of the {@link TypedUF} interface, which allows the
     * use of generic types {@code V} for graph elements, extending the traditional union-find,
     * which typically operates on integer indices. The {@code uf} field enables operations
     * such as determining connectivity between two vertices, as well as merging their respective
     * components.
     * <p>
     * It plays a critical role in the implementation of graph operations, particularly during
     * Minimum Spanning Tree (MST) computation in the Boruvka algorithm.
     *
     * @see TypedUF
     * @see #createUF(SizedIterable)
     * @see #runBoruvka()
     */
    private final TypedUF<V> uf;  // Change the type from Integer to V.

    /**
     * Represents the number of vertices in the input graph.
     * This value is used to initialize and manage structures
     * and algorithms related to the graph, such as Union-Find and Boruvka's MST algorithm.
     */
    private final int size;

    /**
     * A mapping that associates each vertex (of generic type V) to a unique integer.
     * This mapping facilitates efficient lookups and operations on vertices,
     * commonly used in graph algorithms requiring vertex indexing.
     */
    private final Map<V, Integer> vertexToInteger;
}


/*
        while (((SizedIterable<?>) queue).size() < size - 1) {
            //System.out.println("Doing search round: "+ round++);
            // Find the cheapest edge for each component.
            for (Edge<V, X> edge : graph.edges()) { // iterate over the edge list in the graph
                V v = edge.get(), w = edge.getOther(v); // v and w are the 2 vertices of it
                int vi = vertexToInteger.get(v);
                int wi = vertexToInteger.get(w);
                //System.out.println(uf.find(vi) + uf.find(wi));
                if (uf.connected(v,w)) {//If v and w are in the same component, set both to lowest
                    //System.out.println(uf.find(vi) +" and "+ uf.find(wi)+" are connected");
                }
                else {
                    // if they are not in the same component yet, get the indices vi of v and wi of w
                    //int vi = vertexToInteger.get(v);
                    //int wi = vertexToInteger.get(w);
                    //System.out.println(uf.find(vi) +" and "+ uf.find(wi) + " are not connected");
                    if (cheapest[vi] == null || edge.getAttribute().compareTo(cheapest[vi].getAttribute()) < 0) cheapest[vi] = edge;
                    if (cheapest[wi] == null || edge.getAttribute().compareTo(cheapest[wi].getAttribute()) < 0) cheapest[wi] = edge;
                }
            }

            // Add the cheapest edges to the MST and merge their components.
            for (V v : graph.vertices()) {
                int vi = vertexToInteger.get(v);
                Edge<V, X> edge = cheapest[vi];
                if (edge != null) {
                    V w = edge.getOther(v);
                    int wi = vertexToInteger.get(w);
                    if (!uf.connected(v, w)) {  // Ignore if vertices v and w are already in the same component.
                        queue.enqueue(edge);
                        uf.union(v, w);
                        System.out.println(v +" and "+ w+" are "+ uf.connected(v, w) +" connected." );
                    }
                }
            }
 */