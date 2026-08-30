package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;

/**
 * A GeoEdge represents a geographic edge in a graph, connecting two GeoPoint vertices
 * with an attribute. This class extends the generic Edge class and is specifically designed
 * for geographic graphs.
 *
 * @param <V> the vertex type, which must extend GeoPoint, representing geographic points in the graph
 * @param <E> the edge attribute type, representing properties of the connection between vertices
 */
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

    /**
     * Creates a new GeoEdge instance from the given Edge.
     *
     * @param edge the input edge, which contains two vertices and an attribute. The vertices must
     *             be of a type that extends GeoPoint, representing geographic points, while the
     *             attribute represents properties of the connection between the vertices.
     * @param <V>  the type of the vertices, which must extend GeoPoint.
     * @param <E>  the type of the edge attribute.
     * @return a new GeoEdge instance that connects the same vertices as the input edge and carries
     * the same attribute.
     */
    public static <V extends GeoPoint, E> Edge<V, E> create(Edge<V, E> edge) {
        V v = edge.get();
        return new GeoEdge<>(v, edge.getOther(v), edge.getAttribute());
    }
}
