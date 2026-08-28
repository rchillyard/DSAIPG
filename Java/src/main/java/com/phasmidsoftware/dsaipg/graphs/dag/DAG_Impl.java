package com.phasmidsoftware.dsaipg.graphs.dag;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

import java.util.Random;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * TODO this should extend AbstractGraph
 *
 * @param <V> the vertex type.
 * @param <E> the edge type.
 */
public class DAG_Impl<V, E> extends DiGraph<V, E> implements DAG<V, E> {

    /**
     * Executes a depth-first search (DFS) on the graph starting from the specified vertex.
     * The method visits vertices in a depth-first manner, while applying the provided pre-consumer
     * and post-consumer functions at different stages of the traversal.
     *
     * @param vertex the starting vertex for the DFS traversal.
     * @param pre    a consumer function that is executed on each vertex before recursively calling DFS for its neighbors.
     * @param post   a consumer function that is executed on each vertex after all its neighbors have been visited.
     */
    public void dfs(V vertex, Consumer<V> pre, Consumer<V> post) {
        new DepthFirstSearch(new HashSet<>(), pre, post).innerDfs(vertex);
    }

    /**
     * Returns the vertices of the directed acyclic graph (DAG) in a topologically sorted order.
     * The method utilizes a depth-first search (DFS) approach to compute the reverse postorder of the graph.
     *
     * @return an Iterable containing the vertices of the graph in topological order.
     */
    public Iterable<V> sorted() {
        return reversePostOrderDFS();
    }

    /**
     * Retrieves all the edges of the directed acyclic graph (DAG).
     * This method iterates through all adjacency lists and collects all edges into a single collection.
     *
     * @return a SizedIterable containing all the edges of the graph.
     */
    public SizedIterable<Edge<V, E>> edges() {
        Bag<Edge<V, E>> result = new Bag_Array<>(random);
        for (Bag<Edge<V, E>> b : adjacentEdges.values())
            for (Edge<V, E> e : b)
                result.add(e);
        return result;
    }

    /**
     * Adds a directed edge to the graph by updating the adjacency list for the source vertex
     * and ensuring the adjacency structure exists for the destination vertex.
     *
     * @param edge the directed edge to add, represented as an instance of Edge<V, E>.
     */
    public void addEdge(Edge<V, E> edge) {
        // First, we add the edge to the adjacency bag for the "from" vertex;
        getAdjacencyBag(edge.getFrom()).add(edge);
        // Then, we simply ensure that the "to" vertex has an adjacency bag (which might be empty)
        getAdjacencyBag(edge.getTo());
    }

    /**
     * Adds a directed edge to the graph from a source vertex to a destination vertex,
     * with associated attributes.
     *
     * @param from       the source vertex of the edge.
     * @param to         the destination vertex of the edge.
     * @param attributes the attributes associated with the edge.
     */
    public void addEdge(V from, V to, E attributes) {
        addEdge(new Edge<>(from, to, attributes));
    }

    /**
     * Retrieves or initializes the adjacency bag for a given vertex in the graph.
     * An adjacency bag contains all the edges that originate from the specified vertex.
     * If the adjacency bag for the vertex does not exist, it is created and stored.
     *
     * @param vertex the vertex for which the adjacency bag is requested.
     * @return the Bag of edges adjacent to the given vertex.
     */
    protected Bag<Edge<V, E>> getAdjacencyBag(V vertex) {
        return adjacentEdges.computeIfAbsent(vertex, k -> new Bag_Array<>(random));
    }

    /**
     * Constructor for the DAG_Impl class.
     * Initializes an instance of the directed acyclic graph with a provided random number generator.
     *
     * @param random a Random instance used for probabilistic or randomized operations.
     */
    public DAG_Impl(Random random) {
        this.random = random;
    }

    /**
     * Default constructor for the DAG_Impl class.
     * This constructor initializes an instance of the directed acyclic graph using a new instance of {@link Random}.
     * It serves as a convenience constructor to create a DAG_Impl without providing a specific random number generator.
     */
    public DAG_Impl() {
        this(new Random());
    }

    /**
     * A final instance of {@link Random} used for generating random numbers in the DAG_Impl class.
     * This instance facilitates randomized operations, such as probabilistic graph processing
     * or randomized graph traversal algorithms.
     */
    private final Random random;

}
