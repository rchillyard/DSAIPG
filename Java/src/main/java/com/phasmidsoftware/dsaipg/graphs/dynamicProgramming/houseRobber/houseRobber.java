package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.houseRobber;

import com.phasmidsoftware.dsaipg.graphs.dag.DiGraph;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;
import com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.Vertex;

import static com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack.BellmanFord.bellmanFordAlgorithm;

/**
 * The house-robber problem, solved as a shortest path.
 * <p>
 * Given the values of a row of houses, take the largest total that does not
 * include two adjacent houses. The graph is the decision tree: at each house the
 * path either skips it, at no cost, or takes it, at a cost of MINUS its value —
 * so the shortest path through the tree is the most valuable selection, and
 * Bellman-Ford finds it because it tolerates negative weights.
 * <p>
 * A vertex's id records the decisions made so far as a string of 0s and 1s, and
 * the rule that no two houses may be adjacent is enforced by refusing to append a
 * 1 to an id that already ends in one.
 * <p>
 * NOTE this used to build a Graph declared in the knapsack package — a fourth
 * graph implementation in this tree. It uses {@link DiGraph} now.
 *
 * @author Chandan Anandachari (original, with its own Graph)
 */
public class houseRobber {

    /**
     * @param houseValue the value of each house, in a row.
     * @return the largest total obtainable without robbing two adjacent houses.
     */
    public static double solveHouseRobber(double[] houseValue) {
        Vertex source = new Vertex("0", 0);
        Vertex target = new Vertex("9999", 0);
        DiGraph<Vertex, Double> graph = new DiGraph<>();
        graph.addVertex(source);
        graph.addVertex(target);
        buildTheGraph(graph, houseValue, source, target, 0);
        // NOTE abs, because the edge weights are negated so that "shortest" means
        // "most valuable".
        return Math.abs(bellmanFordAlgorithm(graph, source, target));
    }

    /**
     * Build the decision tree below source, recursively.
     *
     * @param graph       the graph to add to.
     * @param houseValues the values.
     * @param source      the decision reached so far.
     * @param target      the vertex every complete decision leads to.
     * @param counter     which house is being decided.
     * @return the graph, for convenience.
     */
    public static DiGraph<Vertex, Double> buildTheGraph(DiGraph<Vertex, Double> graph, double[] houseValues,
                                                        Vertex source, Vertex target, int counter) {
        if (counter >= houseValues.length) {
            graph.addEdge(new Edge<>(source, target, 0.0));
            return graph;
        }
        // skip this house: no gain, no constraint
        Vertex skip = new Vertex(source.getId() + "0", source.getCurrentBagWeight());
        graph.addEdge(new Edge<>(source, skip, 0.0));
        buildTheGraph(graph, houseValues, skip, target, counter + 1);
        // take it, unless the previous house was taken
        if (source.getId().charAt(source.getId().length() - 1) != '1') {
            Vertex take = new Vertex(source.getId() + "1", source.getCurrentBagWeight() + houseValues[counter]);
            graph.addEdge(new Edge<>(source, take, -houseValues[counter]));
            buildTheGraph(graph, houseValues, take, target, counter + 1);
        }
        return graph;
    }
}
