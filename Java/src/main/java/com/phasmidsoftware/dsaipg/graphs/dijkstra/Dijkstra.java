/**
 * The {@code Dijkstra} class represents a data type for solving the
 * single-source shortest paths problem in edge-weighted digraphs
 * where the edge weights are non-negative.
 * <p>
 * This implementation uses <em>Dijkstra's algorithm</em> with a
 * <em>binary heap</em>. The constructor takes
 * &Theta;(<em>E</em> log <em>V</em>) time in the worst case,
 * where <em>V</em> is the number of vertices and <em>E</em> is
 * the number of edges. Each instance method takes &Theta;(1) time.
 * It uses &Theta;(<em>V</em>) extra space (not including the
 * edge-weighted digraph).
 * <p>
 * This correctly computes shortest paths if all arithmetic performed is
 * without floating-point rounding error or arithmetic overflow.
 * This is the case if all edge weights are integers and if none of the
 * intermediate results exceeds 2<sup>52</sup>. Since all intermediate
 * results are sums of edge weights, they are bounded by <em>V C</em>,
 * where <em>V</em> is the number of vertices and <em>C</em> is the maximum
 * weight of any edge.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */

package com.phasmidsoftware.dsaipg.graphs.dijkstra;

import java.util.Stack;
import java.util.function.BiConsumer;

/**
 * The {@code Dijkstra} class provides a data structure and methods for solving the single-source shortest
 * paths problem on an edge-weighted directed graph. It uses Dijkstra's algorithm to compute
 * shortest paths from a source vertex to all other vertices in the graph.
 */
public class Dijkstra {

    /**
     * Method to solve Dijkstra's Shortest Paths algorithm for the starting point {@code s}.
     *
     * @param s the starting point.
     */
    public ShortestPaths shortestPaths(int s) {
        ShortestPaths result = new ShortestPaths();
        result.solve(s);
        return result;
    }

    /**
     * Constructs a Dijkstra object with the given edge-weighted directed graph.
     * The graph must not contain negative edge weights.
     *
     * @param G the edge-weighted directed graph to perform Dijkstra's algorithm on
     * @throws IllegalArgumentException if the graph contains an edge with negative weight
     */
    public Dijkstra(EdgeWeightedDigraph G) {
        this.G = G;
        n = G.V();
        for (DirectedEdge e : G.edges())
            if (e.weight() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
    }

    /**
     * The {@code ShortestPaths} class provides methods for computing and retrieving
     * the shortest paths from a source vertex to other vertices in a weighted directed graph.
     * It implements Dijkstra's algorithm for solving the single-source shortest path problem.
     * The graph must not contain negative edge weights.
     */
    public class ShortestPaths {

        /**
         * Returns the length of the shortest path from the source vertex to the specified vertex.
         *
         * @param v the vertex for which the shortest path distance is to be retrieved
         * @return the shortest path distance to vertex {@code v}
         * @throws IllegalArgumentException if {@code v} is not a valid vertex
         */
        public double distTo(int v) {
            validateVertex(v);
            return distTo[v];
        }

        /**
         * Determines whether there is a path to the specified vertex in the graph.
         *
         * @param v the vertex to check for a path
         * @return true if there is a path to the specified vertex, false otherwise
         */
        public boolean hasPathTo(int v) {
            validateVertex(v);
            return distTo[v] < Double.POSITIVE_INFINITY;
        }

        /**
         * Returns an iterable collection of directed edges that form the shortest path
         * to the specified vertex {@code v}.
         * If no path exists, the method returns {@code null}.
         *
         * @param v the destination vertex
         * @return an iterable collection of {@code DirectedEdge} objects that represent
         * the shortest path to vertex {@code v}, or {@code null} if no such path exists
         * or if {@code v} is invalid
         */
        public Iterable<DirectedEdge> pathTo(int v) {
            validateVertex(v);
            if (!hasPathTo(v)) return null;
            Stack<DirectedEdge> path = new Stack<>();
            for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) path.push(e);
            return path;
        }

        /**
         * Computes shortest paths from a source vertex {@code s} in a directed, edge-weighted graph
         * using Dijkstra's algorithm. Initializes the distance to each vertex to infinity,
         * sets the source distance to zero, and iteratively relaxes edges to find the shortest paths.
         *
         * @param s the source vertex from which shortest paths are calculated
         * @throws IllegalArgumentException if {@code s} is not a valid vertex
         */
        void solve(int s) {
            validateVertex(s);
            for (int v = 0; v < n; v++)
                distTo[v] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;
            pq.insert(s, distTo[s]);
            while (!pq.isEmpty()) {
                int v = pq.delMin();
                for (DirectedEdge e : G.adj(v))
                    relax(e);
            }
            assert check(G, s);
        }

        /**
         * Constructs an instance of the ShortestPaths class.
         * This constructor initializes the necessary data structures and variables
         * used for calculating the shortest paths in a weighted directed graph.
         * <p>
         * The `pq` field is initialized as an index priority queue to facilitate
         * efficient operations on graph vertices. The arrays `distTo` and `edgeTo`
         * are set up to store the shortest known distances and corresponding edges to each vertex.
         * <p>
         * The `keyDecreaser` and `keyInserter` fields are assigned with function references
         * to handle priority queue operations related to key management.
         * <p>
         * The constructor assumes a graph with `n` vertices has already been defined in the context.
         */
        ShortestPaths() {
            pq = new IndexMinPQ<>(n);
            distTo = new double[n];
            edgeTo = new DirectedEdge[n];
            keyDecreaser = pq::decreaseKey;
            keyInserter = pq::insert;
        }

        /**
         * Relaxes the given directed edge and updates the shortest path estimates and
         * priority queue accordingly. If the current shortest path to the destination vertex
         * of the edge is greater than the path through the given edge, the shortest path estimate
         * and the edge leading to the destination vertex are updated. The priority queue is also
         * updated to reflect the change in the shortest path estimate.
         *
         * @param e the directed edge to be relaxed
         *          (contains the source vertex, destination vertex, and edge weight)
         */
        private void relax(DirectedEdge e) {
            int v = e.from(), w = e.to();
            double weight = e.weight();
            final double eDistance = distTo[v] + weight;
            if (distTo[w] > eDistance) {
                distTo[w] = eDistance;
                edgeTo[w] = e;
                (pq.contains(w) ? keyDecreaser : keyInserter).accept(w, distTo[w]);
            }
        }

        /**
         * Verifies the correctness of shortest path calculations in the provided edge-weighted
         * directed graph from a given source vertex.
         *
         * The method checks for the following:
         * - There are no negative edge weights in the graph.
         * - The source vertex maintains a consistent shortest path estimate.
         * - Consistency between distance estimates and edge-to relations.
         * - Relaxation of all edges.
         * - Tightness of all shortest paths.
         *
         * This method outputs error messages to the standard error stream if any inconsistencies
         * or errors are detected during the verification process.
         *
         * @param G the edge-weighted directed graph to be validated
         * @param s the source vertex from which paths were computed
         * @return true if the shortest path calculations are consistent and correct, false otherwise
         */
        private boolean check(EdgeWeightedDigraph G, int s) {
            if (checkWeights(G)) return false;
            if (checkConsistency1(s)) return false;
            if (checkConsistency2(G, s)) return false;
            if (checkRelaxation(G)) return false;
            return !checkDistances(G);
        }

        /**
         * Checks the correctness of computed shortest path distances in the given edge-weighted directed graph.
         *
         * This method verifies that the shortest path distances stored in the distTo array
         * and the edges stored in edgeTo are consistent and satisfy the properties of shortest paths.
         * If any inconsistency is detected, an error message is printed, and the method returns true.
         * Otherwise, it returns false.
         *
         * @param G the edge-weighted directed graph to validate
         *          Must have vertices and edges defined with non-negative weights.
         * @return true if a discrepancy in shortest path calculations is found; false otherwise
         */
        private boolean checkDistances(EdgeWeightedDigraph G) {
            for (int w = 0; w < G.V(); w++) {
                if (edgeTo[w] == null) continue;
                DirectedEdge e = edgeTo[w];
                int v = e.from();
                if (w != e.to()) return true;
                if (distTo[v] + e.weight() != distTo[w]) {
                    System.err.println("edge " + e + " on shortest path not tight");
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks whether the edges in the edge-weighted directed graph {@code G} have been properly relaxed
         * according to the shortest path calculation. If an edge is found where the current shortest path
         * estimate can be improved, it indicates that the edge has not been relaxed correctly.
         *
         * @param G the edge-weighted directed graph to check for proper relaxation of edges
         * @return true if any edge in the graph is found to violate the relaxation condition; false otherwise
         */
        private boolean checkRelaxation(EdgeWeightedDigraph G) {
            for (int v = 0; v < G.V(); v++) {
                for (DirectedEdge e : G.adj(v)) {
                    int w = e.to();
                    if (distTo[v] + e.weight() < distTo[w]) {
                        System.err.println("edge " + e + " not relaxed");
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Checks the consistency of the shortest path calculations for a given vertex {@code s}.
         * Specifically, it verifies if the source vertex has a distance of 0 in the `distTo` array
         * and no associated edge in the `edgeTo` array. If these conditions are not met,
         * the method logs an error message and indicates a consistency issue.
         *
         * @param s the source vertex to be checked for consistency
         * @return true if an inconsistency is found for the source vertex, false otherwise
         */
        private boolean checkConsistency1(int s) {
            if (distTo[s] != 0.0 || edgeTo[s] != null) {
                System.err.println("distTo[s] and edgeTo[s] inconsistent");
                return true;
            }
            return false;
        }

        /**
         * Verifies the consistency of shortest path calculations by checking the relationship
         * between the `distTo` and `edgeTo` arrays for all vertices except the source vertex.
         * If a vertex does not have an edge in the `edgeTo` array but has a finite distance
         * in the `distTo` array, an inconsistency is detected.
         *
         * @param G the edge-weighted directed graph to be validated
         * @param s the source vertex from which shortest paths were computed
         * @return true if an inconsistency is found between the `distTo` and `edgeTo` arrays,
         *         false otherwise
         */
        private boolean checkConsistency2(EdgeWeightedDigraph G, int s) {
            for (int v = 0; v < G.V(); v++) {
                if (v == s) continue;
                if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                    System.err.println("distTo[] and edgeTo[] inconsistent");
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks whether any edge in the given edge-weighted directed graph has a negative weight.
         * If a negative edge weight is detected, an error message is printed to the standard
         * error stream, and the method returns true. Otherwise, it returns false.
         *
         * @param G the edge-weighted directed graph to be checked for negative edge weights
         * @return true if a negative edge weight is detected in the graph, false otherwise
         */
        private static boolean checkWeights(EdgeWeightedDigraph G) {
            for (DirectedEdge e : G.edges()) {
                if (e.weight() < 0) {
                    System.err.println("negative edge weight detected");
                    return true;
                }
            }
            return false;
        }

        /**
         * Validates that the specified vertex {@code v} is a valid index within the graph.
         * Throws an {@link IllegalArgumentException} if {@code v} is outside the valid range
         * of vertices.
         *
         * @param v the vertex to be validated
         *          Must satisfy {@code 0 <= v < V}, where {@code V} is the number of vertices in the graph.
         * @throws IllegalArgumentException if {@code v} is not a valid vertex index
         */
        private void validateVertex(int v) {
            int V = distTo.length;
            if (v < 0 || v >= V)
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
        }

        private final BiConsumer<Integer, Double> keyDecreaser;
        private final BiConsumer<Integer, Double> keyInserter;
        private final double[] distTo;          // distTo[v] = distance  of shortest s->v path
        private final DirectedEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
        private final IndexMinPQ<Double> pq;    // priority queue of vertices
    }

    /**
     * The original edge-weighted directed graph passed into the constructor.
     * This graph is utilized to compute shortest paths using Dijkstra's algorithm.
     * It must not contain edges with negative weights.
     */
    private final EdgeWeightedDigraph G;    // G is the original graph passed in to the constructor.

    /**
     * Represents the number of vertices in the edge-weighted directed graph {@code G}.
     * It is a fixed constant set at the time of constructing the Dijkstra object.
     */
    private final int n;
}
