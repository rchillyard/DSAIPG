package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Position;

/**
 * The GeoPoint interface represents a geographic point with a name and position.
 * It provides methods to retrieve the name and position of the geographic point.
 */
public interface GeoPoint {
    /**
     * Retrieves the name of the geographic point.
     *
     * @return the name of the geographic point as a String
     */
    String getName();

    /**
     * Retrieves the position of the geographic point.
     *
     * @return the position as an instance of the Position interface, representing
     * the geographic coordinates (x and y values) of the point.
     */
    Position getPosition();
}
