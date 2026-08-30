/* @author Urvi Aryamane */

package com.phasmidsoftware.dsaipg.graphs.traversal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The BFS class implements the Breadth-First Search (BFS) algorithm for traversing or searching
 * graph data structures. It operates by exploring the graph's vertices layer by layer, starting
 * from a source vertex and visiting all its adjacent vertices before moving on to the next level
 * of vertices.
 * <p>
 * This class is specifically designed for directed or undirected graphs represented using an
 * adjacency list representation.
 *
 * @author Urvi Aryamane
 */
public class BFS {
    /**
     * Adds a directed edge from vertex {@code v} to vertex {@code w} in the graph.
     * This method updates the adjacency list to include {@code w} in the list of
     * adjacent vertices for {@code v}.
     *
     * @param v the source vertex of the directed edge
     * @param w the destination vertex of the directed edge
     */
    public void addEdge(int v, int w) {
        adj[v].add(w);
    }

    /**
     * Performs a Breadth-First Search (BFS) traversal of the graph starting from the specified source vertex.
     * The method visits all vertices reachable from the source vertex in a layer-by-layer approach
     * and returns a list of visited vertices in the order they were traversed.
     *
     * @param s the source vertex from which the BFS traversal begins
     * @return a list of integers representing the order in which vertices were visited during traversal
     */
    public List<Integer> traverse(int s) {
        boolean[] visited = new boolean[V];

        LinkedList<Integer> queue = new LinkedList<>();

        visited[s] = true;
        queue.add(s);
        List<Integer> output = new ArrayList<>();

        while (!queue.isEmpty()) {
            s = queue.poll();
            output.add(s);

            for (int n : adj[s]) {
                //                System.out.println(n);
                if (!visited[n]) {
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
        return output;
    }

    /**
     * Constructs a Breadth-First Search (BFS) graph with the specified number of vertices.
     * The graph is represented using an adjacency list.
     *
     * @param v the number of vertices in the graph
     */
    @SuppressWarnings("unchecked")
    public BFS(int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList<>();
    }

    private final int V;   // No. of vertices
    private final LinkedList<Integer>[] adj; //Adjacency Lists
}
