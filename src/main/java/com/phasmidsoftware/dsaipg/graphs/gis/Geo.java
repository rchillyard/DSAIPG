/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.util.SizedIterable;

public interface Geo<V extends GeoPoint, E> extends EdgeGraph<V, E> {
    /**
     * Get the edges of this Geo instance as GeoEdges
     *
     * @return an iterable of GeoEdge
     */
    SizedIterable<Edge<V, E>> goeEdges();

    /**
     * Get the length of an edge
     *
     * @param edge the edge
     * @return the length of the edge
     */
    double length(Edge<V, E> edge);
}