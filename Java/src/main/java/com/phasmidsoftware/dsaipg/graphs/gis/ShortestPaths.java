package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.dag.DiGraph;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * The ShortestPaths class is designed to compute the shortest paths from a starting vertex
 * to all other vertices in a directed graph using Dijkstra's algorithm. This class is generic
 * and works with any vertex and edge types, provided that edges have numeric weights.
 *
 * @param <V> the type of vertices in the graph.
 * @param <E> the type of edge weights, extending Number.
 */
public class ShortestPaths<V, E extends Number> {
    public double cost(V v) {
        Vertex vertex = table.getOrDefault(v, new Vertex(v));
        return vertex.cost;
    }

    /**
     * Determines whether there is a path from the starting vertex to the specified vertex.
     *
     * @param v the vertex to check for a path from the starting vertex
     * @return true if a path exists to the specified vertex, false otherwise
     */
    public boolean hasPathTo(V v) {
        return table.containsKey(v);
    }

    /**
     * Computes and returns an iterable sequence of edges representing the path from the starting vertex
     * to the specified target vertex in the graph. The path is determined based on the shortest paths
     * previously calculated and stored in the data structure.
     *
     * @param target the target vertex for which the path from the start vertex is to be retrieved
     * @return an iterable of edges representing the path from the start vertex to the target vertex;
     * returns an empty iterable if no path exists
     */
    public Iterable<Edge<V, E>> pathTo(V target) {
        Stack<Edge<V, E>> edges = new Stack<>();
        if (hasPathTo(target)) {
            V v = target;
            for (Vertex vertex = table.get(v); vertex.edgeTo != null; ) {
                Edge<V, E> edgeTo = vertex.edgeTo;
                if (edgeTo.getTo() != v) throw new RuntimeException("assertion error");
                edges.push(edgeTo);
                v = edgeTo.getFrom();
            }
        }
        return edges;
    }

    @Override
    public String toString() {
        return "ShortestPaths{" +
                "table=" + table +
                '}';
    }

    /**
     * Constructs a ShortestPaths object to calculate the shortest paths from a given starting vertex
     * within a directed graph using Dijkstra's algorithm.
     *
     * @param graph the directed graph on which shortest path calculations will be performed
     * @param start the starting vertex from which shortest paths will be computed
     */
    public ShortestPaths(DiGraph<V, E> graph, V start) {
        this.graph = graph;
        this.start = start;
        this.table = dijkstra();
    }

    /**
     * Computes the shortest path from a start vertex to all other vertices in the graph using Dijkstra's algorithm.
     * <p>
     * Iteratively processes vertices by their minimum known distance from the start vertex, maintaining a priority
     * queue for efficient selection of the next vertex to process. Updates path costs and predecessors as necessary.
     *
     * @return a map of vertices to their corresponding Vertex objects, each detailing the shortest path cost and
     * the edge leading to the vertex from its predecessor in the shortest path.
     */
    private Map<V, Vertex> dijkstra() {
        Map<V, Vertex> result = new HashMap<>();
        PriorityQueue<V> pq = new PriorityQueue<>();
        pq.offer(start);
        result.put(start, new Vertex(start, 0, null));
        while (!pq.isEmpty()) relax(graph, pq.poll(), result, pq);
        return result;
    }

    /**
     * Relaxes edges adjacent to the given vertex and updates the cost table and priority queue
     * to facilitate accurate shortest path calculations.
     *
     * @param graph the directed graph containing the vertices and edges
     * @param vertex the vertex whose adjacent edges are being relaxed
     * @param table a map tracking the shortest known costs and associated edges for each vertex
     * @param pq a priority queue used to manage vertices based on their current shortest distance
     */
    private void relax(DiGraph<V, E> graph, V vertex, Map<V, Vertex> table, PriorityQueue<V> pq) {
        for (Edge<V, E> e : graph.adjacent(vertex)) {
            V w = e.getTo();
            Vertex vertexW = table.getOrDefault(w, new Vertex(w));
            table.put(w, vertexW);
            double relaxedCost = table.get(e.getFrom()).cost + e.getAttributes().doubleValue();
            if (vertexW.cost > relaxedCost) {
                vertexW.relax(relaxedCost, e);
                pq.remove(w);
                pq.offer(w);
            }
        }
    }

    private final DiGraph<V, E> graph;
    private final V start;
    private final Map<V, Vertex> table;

    /**
     * The Vertex class represents a node in a graph, containing information about its associated
     * vertex, the cost to reach it, and the edge that leads to it in the context of shortest-path computation.
     *
     * This class implements the Comparable interface to allow comparison of vertices based on their cost,
     * which is particularly useful in graph algorithms like Dijkstra's shortest-path algorithm.
     *
     * @param <V> the type of the vertex this class represents
     * @param <E> the type of the associated edge attributes, if any
     */
    class Vertex implements Comparable<Vertex> {
        /**
         * Compares this vertex with the specified vertex for order based on their cost.
         * The comparison is primarily used in graph algorithms such as Dijkstra's shortest-path,
         * where vertices need to be ordered by their cost.
         *
         * @param o the vertex to be compared with this vertex
         * @return a negative integer, zero, or a positive integer as this vertex's cost
         *         is less than, equal to, or greater than the specified vertex's cost
         */
        public int compareTo(Vertex o) {
            return Double.compare(cost, o.cost);
        }

        /**
         * Updates the cost to reach this vertex and the edge that leads to it,
         * if a better (lower-cost) path has been discovered.
         *
         * @param cost   the new calculated cost to reach this vertex
         * @param edgeTo the edge that provides the improved path to this vertex
         */
        void relax(double cost, Edge<V, E> edgeTo) {
//            System.out.println("relaxing entry for vertex"+this+" to cost "+cost+" with edgeTo: "+edgeTo);
            this.cost = cost;
            this.edgeTo = edgeTo;
        }

        public Vertex(V vertex, double cost, Edge<V, E> edgeTo) {
            this.vertex = vertex;
            this.cost = cost;
            this.edgeTo = edgeTo;
        }

        public Vertex(V vertex) {
            this(vertex, Double.POSITIVE_INFINITY, null);
        }

        @Override
        public String toString() {
            return "Vertex {" + vertex +
                    ": cost=" + cost +
                    ", edgeTo=" + edgeTo +
                    '}';
        }

        private final V vertex;
        private double cost;
        private Edge<V, E> edgeTo;
    }
}
