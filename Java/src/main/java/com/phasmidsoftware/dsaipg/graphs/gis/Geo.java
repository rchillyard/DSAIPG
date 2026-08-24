package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

/**
 * An interface defining the behavior of a geographic graph (Geo), where vertices represent
 * geographic points, and edges represent relationships between these points, both with specific attributes.
 * This interface extends the EdgeGraph interface, which models a graph structure with edges as primary entities.
 *
 * @param <V> the vertex type, which must extend GeoPoint. Represents points in the graph.
 * @param <E> the edge attribute type, representing properties of the connections between vertices.
 */
public interface Geo<V extends GeoPoint, E> extends EdgeGraph<V, E> {
    /**
     * Get the edges of this Geo instance as GeoEdges
     *
     * @return an iterable of GeoEdge
     */
    SizedIterable<Edge<V, E>> geoEdges();

    /**
     * Get the length of an edge
     *
     * @param edge the edge
     * @return the length of the edge
     */
    double length(Edge<V, E> edge);
}
