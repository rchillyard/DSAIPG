package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.phasmidsoftware.dsaipg.graphs.dag.DiGraph;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Bellman-Ford single-source shortest-path algorithm.
 * <p>
 * Relax every edge, |V| - 1 times. After k passes every shortest path of k edges
 * or fewer has been found, and no shortest path in a graph without negative cycles
 * uses more than |V| - 1 edges, so that many passes suffice. Unlike Dijkstra it
 * copes with negative weights, which is why it is here: the knapsack formulation
 * gives an edge a NEGATIVE weight for the value gained, so that the shortest path
 * is the most valuable packing.
 * <p>
 * NOTE this used to run on a Graph, Edge and Vertex declared in this package — a
 * fourth graph implementation in a tree that already had three. It runs on
 * {@link DiGraph} now, which models it exactly: a vertex is a {@link Vertex} and
 * an edge attribute is the weight. Removing the local classes also removed four
 * defects that came with them, listed in the notes on this package in
 * `Deferred work.md`.
 *
 * @author Chandan Anandachari (original, with its own Graph)
 */
public class BellmanFord {

    /**
     * Find the length of the shortest path from source to target.
     *
     * @param graph  the graph, whose edge attributes are the weights.
     * @param source the vertex to start from.
     * @param target the vertex to reach.
     * @return the total weight of the shortest path.
     * @throws IllegalArgumentException if target cannot be reached from source.
     */
    public static double bellmanFordAlgorithm(DiGraph<Vertex, Double> graph, Vertex source, Vertex target) {
        Map<Vertex, Double> shortestDistance = new HashMap<>();
        shortestDistance.put(source, 0.0);

        // NOTE gathered once. The original rebuilt this list on every call and
        // filtered it with allEdges.contains(edge) -- which never removed anything,
        // because Edge had no equals and so compared by identity, and which cost
        // O(E^2) to achieve that.
        List<Edge<Vertex, Double>> allEdges = new ArrayList<>();
        for (Edge<Vertex, Double> edge : graph.edges()) allEdges.add(edge);

        int v = graph.vertices().size();
        for (int i = 1; i < v; i++)
            for (Edge<Vertex, Double> edge : allEdges) {
                Double fromDistance = shortestDistance.get(edge.getFrom());
                if (fromDistance == null) continue;  // not yet reached
                double candidate = fromDistance + edge.getAttributes();
                Double toDistance = shortestDistance.get(edge.getTo());
                if (toDistance == null || candidate < toDistance)
                    shortestDistance.put(edge.getTo(), candidate);
            }

        Double result = shortestDistance.get(target);
        // NOTE the original returned the map lookup directly, so an unreachable
        // target gave a NullPointerException on unboxing -- from a method declared
        // to return a double, which gives the caller nothing to work with.
        if (result == null)
            throw new IllegalArgumentException("bellmanFordAlgorithm: " + target + " is not reachable from " + source);
        return result;
    }
}
