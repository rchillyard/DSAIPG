/*
 * Copyright (c) 2024. Robin Hillyard
 */

/* @author Urvi Aryamane */

package com.phasmidsoftware.dsaipg.graphs.generic_BFS_and_prims;

import java.util.*;

public class GBFS<T> {
    //private boolean[] marked;
    private final HashMap<T, Boolean> marked;

    public GBFS(Graph G, T s) {
        //marked = new boolean[G.V()];
        marked = new HashMap<>();
        //edgeTo = (T[]) new Object[G.V()];
        //private T[] edgeTo;
        HashMap<T, T> edgeTo = new HashMap<>();
        // Source vertex
        //bfs(G,s);

    }

    public List<T> bfs(Graph G, T s) {
        //QueueG<T> queue = new QueueG<T>(G.V());
        LinkedList<T> queue = new LinkedList<>();
        //marked[s]=true;
        marked.put(s, true);
        queue.add(s);
        List<T> output = new ArrayList<>();
        while (!queue.isEmpty()) {
            s = queue.poll();
            output.add(s);
            @SuppressWarnings("unchecked") Iterator<T> i = G.adj(s);
            while (i.hasNext()) {
                T n = i.next();
//                System.out.println(n);
                if (!marked.containsKey(n)) {
                    marked.put(n, true);
                    queue.add(n);
                }
            }
        }
        return output;
    }
}