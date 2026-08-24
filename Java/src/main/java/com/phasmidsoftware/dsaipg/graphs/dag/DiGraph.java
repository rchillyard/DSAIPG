package com.phasmidsoftware.dsaipg.graphs.dag;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack_LinkedList;
import com.phasmidsoftware.dsaipg.graphs.undirected.AbstractGraph;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a directed graph with vertices of type V and edges of type Edge<V, E>.
 * This class extends AbstractGraph, where each edge is represented as an object of type Edge.
 * It provides methods to manipulate and query the directed graph, such as reversing the graph,
 * retrieving edges, constructing kernel DAGs, and performing depth-first searches.
 *
 * @param <V> the type of vertex in the graph.
 * @param <E> the type of attribute associated with edges in the graph.
 */
public class DiGraph<V, E> extends AbstractGraph<V, Edge<V, E>> {

    /**
     * Reverse the sense of this DAG.
     *
     * @return a DAG whose edges all point in the opposite direction to those in this DAG.
     */
    public DiGraph<V, E> reverse() {
        DiGraph<V, E> result = new DiGraph<>();
        for (Edge<V, E> e : edges()) result.addEdge(e.reverse());
        return result;
    }

    /**
     * Adds a directed edge to the graph by updating the adjacency list for the source vertex
     * and ensuring the adjacency structure exists for the destination vertex.
     *
     * @param edge the directed edge to add, represented as an instance of Edge<V, E>.
     */
    public void addEdge(Edge<V, E> edge) {
        getAdjacencyBag(edge.getFrom()).add(edge);
        getAdjacencyBag(edge.getTo());
    }

    /**
     * Returns an iterable collection of all edges in the graph.
     * This method aggregates edges from all adjacency lists in the graph
     * and provides them as a single {@code SizedIterable}.
     *
     * @return a {@code SizedIterable} containing all edges in the graph.
     */
    public SizedIterable<Edge<V, E>> edges() {
        Bag<Edge<V, E>> result = new Bag_Array<>();
        for (Iterable<Edge<V, E>> b : adjacentEdges.values())
            for (Edge<V, E> e : b)
                result.add(e);
        return result;
    }

    /**
     * Performs a reverse post-order depth-first search (DFS) on the graph.
     * It generates a stack containing the vertices of the graph in reverse post-order,
     * where the last visited vertex during DFS is at the top of the stack.
     *
     * @return a Stack containing the vertices of the graph in reverse post-order.
     */
    protected Stack<V> reversePostOrderDFS() {
        Stack<V> postOrderStack = new Stack_LinkedList<>();
        new DepthFirstSearch(new TreeSet<>(), null, postOrderStack::push).innerDfs();
        return postOrderStack;
    }

    /**
     * Constructs a directed acyclic graph (DAG) of kernels from the current graph.
     * A kernel is a set of strongly connected vertices such that no edge exists within the set,
     * but edges may exist to other kernels.
     * The method performs a reverse post-order depth-first search (DFS) to identify the kernels
     * and constructs the resulting DAG using these kernels as vertices.
     *
     * @return a new DAG where each vertex is a kernel (strongly connected component) of the original graph,
     * and edges represent relationships between these kernels based on the original graph's edges.
     */
    DAG<Kernel<V>, E> kernelDAG() {
        final DAG_Impl<Kernel<V>, E> result = new DAG_Impl<>(new Random(0L));
        final TreeSet<V> marked = new TreeSet<>();
        for (V vertex : reverse().reversePostOrderDFS()) {
            Kernel<V> kernel = new Kernel<>();
            new DepthFirstSearch(marked, kernel::add, null).innerDfs(vertex);
            if (!kernel.vertices.isEmpty())
                result.addVertex(kernel);
        }
        final List<Kernel<V>> kernels = result.vertices().toList();
        for (Edge<V, E> edge : edges()) {
            final Kernel<V> from = kernels.stream().filter((k) -> k.vertices.contains(edge.getFrom())).findAny().orElse(null);
            final Kernel<V> to = kernels.stream().filter((k) -> k.vertices.contains(edge.getTo())).findAny().orElse(null);
            if (from != null && to != null && from != to)
                result.addEdge(new Edge<>(from, to, edge.getAttributes()));
        }
        return result;
    }

    @Override
    public String toString() {
        return adjacentEdges.toString();
    }

    /**
     * This class implements Depth First Search (DFS) traversal for a graph.
     * It is designed to operate on a directed graph (DiGraph) and allows for both pre-order and post-order processing of vertices during the traversal.
     *
     * @param <V> the type of the vertices in the graph
     * @param <E> the type of the edge attributes in the graph
     */
    class DepthFirstSearch {

        /**
         * Constructs a DepthFirstSearch instance to perform depth-first traversal on a graph.
         * The traversal allows optional pre-order and post-order Consumer functions to be
         * executed on vertices during traversal. Either one of the Consumers (pre or post) must be non-null.
         *
         * @param marked a TreeSet to keep track of visited vertices to prevent cycles and revisits.
         * @param pre    a Consumer function that is executed on each vertex before visiting its adjacent vertices (pre-order action).
         * @param post   a Consumer function that is executed on each vertex after all its adjacent vertices have been visited (post-order action).
         * @throws RuntimeException if both pre and post Consumers are null.
         */
        public DepthFirstSearch(TreeSet<V> marked, Consumer<V> pre, Consumer<V> post) {
            this.pre = pre;
            this.post = post;
            this.marked = marked;
            if (pre == null && post == null)
                throw new RuntimeException("DepthFirstSearch: pre and post cannot both be null");
        }

        /**
         * Performs a depth-first search (DFS) traversal starting from all vertices in the graph.
         * This method iterates through all vertices in the graph and invokes the vertex-specific `innerDfs(V v)`
         * method for each vertex to perform a recursive DFS traversal. It ensures all vertices are visited
         * while avoiding revisits of previously marked vertices.
         * <p>
         * The traversal may execute optional pre-order and post-order operations on each vertex,
         * as specified by the `pre` and `post` Consumer functions respectively, passed to the
         * `DepthFirstSearch` constructor.
         * <p>
         * This method is designed for use in directed graphs represented with adjacency edges, ensuring that
         * all connected components of the graph are explored.
         * <p>
         * Preconditions:
         * - The `vertices()` method must return all vertices in the graph.
         * <p>
         * Postconditions:
         * - Each vertex in the graph will be visited exactly once, provided it is reachable.
         * - The `pre` and `post` Consumer functions, if defined, will be executed on each vertex
         * during the traversal.
         */
        void innerDfs() {
            for (V v : vertices()) innerDfs(v);
        }

        /**
         * Recursively performs a depth-first search (DFS) traversal starting from a given vertex.
         * This method explores all reachable vertices from the specified starting vertex, marking them as visited
         * and optionally executing pre-order and post-order Consumer functions on each vertex.
         *
         * @param v the starting vertex from where the DFS traversal will commence.
         */
        void innerDfs(V v) {
            // TODO create a HashMap of V and Boolean
            if (marked.contains(v)) return;
            marked.add(v);
            if (pre != null) pre.accept(v);
            for (Edge<V, E> e : adjacentEdges.get(v)) {
                V v1 = e.getTo();
                if (!marked.contains(v1)) innerDfs(v1);
            }
            if (post != null) post.accept(v);
        }

        private final TreeSet<V> marked;
        private final Consumer<V> pre;
        private final Consumer<V> post;
    }

    /**
     * The Kernel class represents a collection of vertices in a graph.
     * It can be used to model strongly connected components or subsets of vertices in various graph algorithms.
     *
     * @param <T> the type of vertex stored in the kernel
     */
    static class Kernel<T> {
        /**
         * Represents a collection of vertices within the context of a graph or subgraph structure.
         * It is a private final field that stores the vertices as a collection and is intended to remain immutable
         * after initialization. This collection plays a significant role in defining the structure or subset of a graph,
         * such as for strongly connected components or subgraph kernels.
         *
         * @param <T> the type of vertices stored in the collection.
         */
        private final Collection<T> vertices;

        /**
         * Adds a vertex to the collection of vertices in the kernel.
         *
         * @param t the vertex to be added; must not be null.
         */
        public void add(T t) {
            vertices.add(t);
        }

        /**
         * Constructs a Kernel with the given collection of vertices.
         *
         * @param vertices the collection of vertices for the Kernel; must not be null.
         */
        public Kernel(Collection<T> vertices) {
            this.vertices = vertices;
        }

        /**
         * Default constructor for the Kernel class.
         * This constructor initializes an empty Kernel backed by an empty ArrayList to store its vertices.
         * It provides a simple way to create an empty Kernel instance.
         */
        public Kernel() {
            this(new ArrayList<>());
        }

        @Override
        public String toString() {
            return vertices.toString();
        }
    }
}
