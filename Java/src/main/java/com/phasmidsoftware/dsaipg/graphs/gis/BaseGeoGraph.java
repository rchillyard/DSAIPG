package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterableImpl;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a base class for a geographic graph where vertices are points on Earth's surface,
 * and edges represent connections between them. This graph operates within spherical coordinates.
 *
 * <p>This class extends the {@link Graph_Edges} class, inheriting the structure and behavior of
 * a generic edge-based graph. It also implements the {@link Geo} interface, ensuring that all
 * graphs of this type are capable of specific geographic operations.
 *
 * <p>The class provides a concrete implementation for the {@code goeEdges} method, which
 * retrieves the edges of the graph as a {@link SizedIterable}.
 *
 * @param <V> the vertex type, which must extend {@link GeoPoint}. Represents the points on Earth's surface.
 * @param <E> the edge type, representing the attributes or properties of the connection between vertices.
 */
public abstract class BaseGeoGraph<V extends GeoPoint, E> extends Graph_Edges<V, E> implements Geo<V, E> {

    /**
     * Retrieves all edges of the geographic graph as a {@code SizedIterable}.
     * The method collects all edges and returns them in a container that supports both
     * iteration and efficient size retrieval.
     *
     * @return a {@code SizedIterable} containing all edges within the graph. Each edge is of type
     * {@code Edge<V, E>}, where {@code V} represents the vertex type and {@code E}
     * represents the edge attribute type.
     */
    public SizedIterable<Edge<V, E>> geoEdges() {
        Collection<Edge<V, E>> result = new ArrayList<>();
        for (Edge<V, E> edge : super.edges()) result.add(edge);
        return SizedIterableImpl.create(result);
    }

}
