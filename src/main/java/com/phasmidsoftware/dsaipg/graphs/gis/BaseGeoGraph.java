/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.util.SizedIterable;
import com.phasmidsoftware.dsaipg.util.SizedIterableImpl;

import java.util.ArrayList;

public abstract class BaseGeoGraph<V extends GeoPoint, E> extends Graph_Edges<V, E> implements Geo<V, E> {

    public SizedIterable<Edge<V, E>> goeEdges() {
        ArrayList<Edge<V, E>> result = new ArrayList<>();
        for (Edge<V, E> edge : super.edges()) result.add(edge);
        return SizedIterableImpl.create(result);
    }

}