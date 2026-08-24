package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;

/**
 * The GeoKruskal class extends the Kruskal algorithm to work with geo-referenced graphs.
 * This class specializes Kruskal's algorithm for generating a minimum spanning tree (MST)
 * and outputs the results in a format compatible with geographical data representations.
 * <p>
 * The generic type parameters specify the types for vertices and edge attributes:
 * - V extends GeoPoint: Vertices are defined as GeoPoint objects, which represent
 * geographical locations with name and position information.
 * - X extends Comparable<X> & Sequenced: Edge attributes must implement Comparable for
 * weight comparison and Sequenced for maintaining a sequence order.
 * <p>
 * This class operates on graphs provided by the EdgeGraph interface.
 *
 * @param <V> the type of vertices in the graph, extending GeoPoint.
 * @param <X> the type of edge attributes, extending Comparable and implementing Sequenced.
 */
public class GeoKruskal<V extends GeoPoint, X extends Comparable<X> & Sequenced> extends Kruskal<V, X> implements GeoMST<V, X> {
    /**
     * Method to generate a graph of the MST, given an empty BaseGeoGraph
     *
     * @param geoGraph an empty GeoGraph which will be filled with edges before being returned.
     * @return the geoGraph that was passed as the parameter, but filled with the MST edges.
     */
    public Geo<V, X> getGeoMST(Geo<V, X> geoGraph) {
        EdgeGraph<V, X> mst = super.getMST();
        for (Edge<V, X> e : mst.edges())
            geoGraph.addEdge(createEdge(e));
        return geoGraph;
    }

    /**
     * Creates a new GeoEdge instance based on the provided edge.
     *
     * @param edge the input edge containing two vertices and an attribute. The vertices represent geographic
     *             points (of type V), and the attribute (of type X) specifies properties of the connection
     *             between the two vertices.
     * @return a new GeoEdge instance that connects the same vertices as the input edge and carries
     *         the same attribute.
     */
    public Edge<V, X> createEdge(Edge<V, X> edge) {
        V v = edge.get();
        return new GeoEdge<>(v, edge.getOther(v), edge.getAttribute());
    }

    /**
     * Constructs a GeoKruskal instance for finding the minimum spanning tree (MST) of the given geo-referenced graph.
     * This constructor initializes the base Kruskal algorithm with the provided EdgeGraph, which represents a
     * structure of vertices and edges with geographical context.
     *
     * @param graph the edge-weighted geographic graph for which the MST is to be calculated.
     *              It must provide access to vertices and edges via methods defined in the EdgeGraph interface.
     */
    public GeoKruskal(EdgeGraph<V, X> graph) {
        super(graph);
    }

}
