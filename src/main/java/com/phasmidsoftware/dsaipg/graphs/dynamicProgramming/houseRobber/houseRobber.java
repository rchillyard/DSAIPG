/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.houseRobber;


import com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.Graph;
import com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.Vertex;

import static com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.BellmanFord.bellmanFordAlgorithm;

public class houseRobber {
    /**
     * Method to return the Maximum profit after selecting the houses.
     *
     * @param houseValue the profit which a robber can get from different houses.
     * @return Maximum profit robber can rob items from different house.
     */
    public static double solveHouseRobber(double[] houseValue) {
        //Source Vertex with id:0
        Vertex source = new Vertex("0", 0);
        Graph graph = new Graph(source);
        graph.addVertex(source);
        //Destination Vertex with id:9999
        Vertex target = new Vertex("9999", 0);
        graph.addVertex(target);

        Graph graph1 = buildTheGraph(graph, houseValue, source, target, 0);

        return (Math.abs(bellmanFordAlgorithm(graph, source, target)));
    }

    /**
     * Method to return the graph which has all the edges and vertices.
     *
     * @param graph       Graph object.
     * @param source      source vertex
     * @param target      target vertex
     * @param houseValues the profit which a robber can get from different houses.
     * @param counter     has the index position
     * @return Graph which will have all the possibilities a robber can rob a house .
     */
    public static Graph buildTheGraph(Graph graph, double[] houseValues, Vertex source, Vertex target, int counter) {
        if (counter >= houseValues.length) {
            graph.addEdge(source, target, 0);
            return graph;
        }

        Vertex vertex1 = new Vertex(source.getId() + "0", source.getCurrentBagWeight());
        graph.addVertex(vertex1);
        graph.addEdge(source, vertex1, 0);
        buildTheGraph(graph, houseValues, vertex1, target, counter + 1);

        if (((source.getId()).charAt((source.getId()).length() - 1)) != '1') {
            Vertex vertex2 = new Vertex(source.getId() + "1", source.getCurrentBagWeight() + houseValues[counter]);
            graph.addVertex(vertex2);
            graph.addEdge(source, vertex2, -houseValues[counter]);
            buildTheGraph(graph, houseValues, vertex2, target, counter + 1);
        }

        return graph;
    }
}