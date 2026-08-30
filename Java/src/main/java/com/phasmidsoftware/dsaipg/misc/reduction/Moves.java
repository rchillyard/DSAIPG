package com.phasmidsoftware.dsaipg.misc.reduction;

/**
 * The Moves interface defines the contract for determining valid moves and executing movements
 * based on a specific strategy or rule set.
 */
public interface Moves {

    /**
     * Method to determine if we can move successfully from point x
     *
     * @param x the point
     * @return true if there is such a path
     */
    boolean valid(Point x);

    /**
     * This method defines the possible moves from point p
     *
     * @param p     the point
     * @param which the strategy to use
     * @return the point we moved to
     */
    Point move(Point p, boolean which);

}
