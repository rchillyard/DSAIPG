package com.phasmidsoftware.dsaipg.misc.reduction;

/**
 * This is from a problem in LeetCode: 780 Reaching Points (Hard)
 */
public class Moves1 implements Moves {

    /**
     * Determines if the point (x, y) satisfies a specific condition, delegating the logic to another method.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return true if the point (x, y) satisfies the condition, false otherwise
     */
    public boolean valid(int x, int y) {
        return valid(new Point(x, y));
    }

    /**
     * Determines whether it is possible to reach the target point (tx, ty) starting from
     * the given point p through a series of valid moves. A valid move is defined as either
     * adding the x-coordinate to the y-coordinate or adding the y-coordinate to the x-coordinate.
     *
     * @param p the starting point from which to determine if it is possible to reach the target
     * @return true if the target point (tx, ty) can be reached from the given point p, otherwise false
     */
    public boolean valid(Point p) {
        // The base case for failure is what makes this terminate at all. Both
        // coordinates only ever grow, so once either has passed the target that
        // path is dead. Without inBounds the search walks q1, q1, q1, ... for
        // ever, never reaching its second recursive call, and can only ever
        // return true -- and then only if the target happens to lie on that one
        // path.
        return inBounds(p) && (p.equals(t) || valid(move(p, true)) || valid(move(p, false)));
    }

    /**
     * @param p a point.
     * @return true if neither coordinate has passed the target's.
     */
    private boolean inBounds(Point p) {
        return p.x <= t.x && p.y <= t.y;
    }

    /**
     * This method determines one of the possible moves from a given point based on a particular strategy.
     *
     * @param p     the point from which the move originates
     * @param which a boolean value that determines the specific move strategy to apply
     * @return a new Point representing the result of the move
     */
    public Point move(Point p, boolean which) {
        return which ? new Point(p.x, p.x + p.y) : new Point(p.x + p.y, p.y);
    }

    /**
     * The point to be reached. Immutable, and the only thing the search is
     * measured against: a point is in bounds while neither coordinate has passed
     * the target's, and the answer is found when both are equal to it.
     */
    private final Point t;

    /**
     * Constructs an instance of the Moves1 class with the specified target coordinates.
     *
     * @param tx the x-coordinate of the target point
     * @param ty the y-coordinate of the target point
     */
    public Moves1(int tx, int ty) {
        this(new Point(tx, ty));
    }

    /**
     * @param t the target.
     */
    public Moves1(Point t) {
        this.t = t;
    }
}
