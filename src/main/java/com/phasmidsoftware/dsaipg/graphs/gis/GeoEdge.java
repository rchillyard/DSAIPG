/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;

public class GeoEdge<V extends GeoPoint, E> extends Edge<V, E> {
    /**
     * Edge constructor.
     *
     * @param a         a vertex.
     * @param b         the other vertex.
     * @param attribute the attribute.
     */
    public GeoEdge(V a, V b, E attribute) {
        super(a, b, attribute);
    }

    public static <V extends GeoPoint, E> Edge<V, E> create(Edge<V, E> edge) {
        V v = edge.get();
        return new GeoEdge<>(v, edge.getOther(v), edge.getAttribute());
    }
}