package com.phasmidsoftware.dsaipg.graphs.undirected;

/**
 * The Position interface represents a point in a 2D space with x and y coordinates.
 * It provides methods to retrieve the x-coordinate and y-coordinate values of the position.
 */
public interface Position {

    /**
     * Retrieves the x-coordinate of the position.
     *
     * @return the x-coordinate as a double.
     */
    double getX();

    /**
     * Retrieves the y-coordinate of this position in a 2D space.
     *
     * @return the y-coordinate as a double.
     */
    double getY();
}
