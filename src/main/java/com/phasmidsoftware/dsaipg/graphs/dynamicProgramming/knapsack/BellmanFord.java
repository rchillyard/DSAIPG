/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BellmanFord {
    /**
     * Method to return the Maximum profit after selecting particular items using Bellman Ford Algorithm.
     *
     * @param graph  the weights for each item.
     * @param source source Vertex.
     * @param target target Vertex.
     * @return Maximum profit after selecting items from the list of items available.
     */
    public static double bellmanFordAlgorithm(Graph graph, Vertex source, Vertex target) {

        int V = graph.V(), E = graph.E();

        HashMap<Vertex, Double> shortestDistance = new HashMap<>();

        shortestDistance.put(source, 0.0);

        HashMap<Vertex, LinkedList<Edge>> hm = graph.getAdjacent();
        List<Edge> allEdges = new LinkedList<>();

        for (Vertex vertex : hm.keySet()) {
            Iterator<Edge> it = graph.edges(vertex);
            while (it.hasNext()) {
                Edge edge = it.next();
                if (!allEdges.contains(edge))
                    allEdges.add(edge);
            }
        }

        for (int i = 1; i < V; ++i) {
            for (int j = 0; j < E; ++j) {
                Vertex v1 = allEdges.get(j).source();//u
                Vertex v2 = allEdges.get(j).destination();//v
                double weight = allEdges.get(j).getEdgeWeight();

                if (shortestDistance.getOrDefault(v1, Double.MAX_VALUE) != Double.MAX_VALUE && (shortestDistance.get(v1) + weight) < shortestDistance.getOrDefault(v2, Double.MAX_VALUE)) {
                    shortestDistance.put(v2, shortestDistance.get(v1) + weight);
                }
            }
        }

        // Negating the shortest Distance
        return (shortestDistance.get(target));

    }
}