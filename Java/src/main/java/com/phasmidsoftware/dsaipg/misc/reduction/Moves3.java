package com.phasmidsoftware.dsaipg.misc.reduction;

/**
 * This is from a problem in LeetCode: 780 Reaching Points (Hard)
 */
public class Moves3 implements Moves {

    /**
     * Constructs a Moves3 object with a specific starting point.
     *
     * @param s the initial point for the Moves3 instance
     */
    public Moves3(Point s) {
        this.s = s;
    }

    /**
     * Constructs an instance of the Moves3 class using the specified x and y coordinates,
     * which are converted into a Point object.
     *
     * @param x the x-coordinate of the starting point
     * @param y the y-coordinate of the starting point
     */
    public Moves3(int x, int y) {
        this(new Point(x, y));
    }

    /**
     * Determines whether a given point is valid according to the defined rules.
     * The rules are based on the properties of the point and its alignment in specific areas
     * relative to a reference point.
     *
     * @param t the point to validate
     * @return true if the point is valid based on the conditions, false otherwise
     */
    public boolean valid(Point t) {
        // DO NOT PUBLISH THIS IN PUBLIC REPO!!!!
        Point p = t;
        while (true) {
            // TO BE IMPLEMENTED   Sorry, but you have to do this one yourself!
             return false;
            // END SOLUTION
        }
    }

    /**
     * This method defines the possible moves from point p
     *
     * @param p     the point
     * @param which ignored
     * @return the point we moved to
     */
    public Point move(Point p, boolean which) {
        return (p.y > p.x) ? new Point(p.x, p.y - p.x) : new Point(p.x - p.y, p.y);
    }

    /**
     * Determines whether the point with the given x and y coordinates is valid according to
     * the defined rules.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return true if the point is valid based on the conditions, false otherwise
     */
    public boolean valid(int x, int y) {
        return valid(new Point(x, y));
    }

    /**
     * Represents the initial starting point for the Moves3 instance.
     * This point serves as the origin for operations like validation and movement calculations
     * based on specific rules.
     */
    private final Point s;
}
