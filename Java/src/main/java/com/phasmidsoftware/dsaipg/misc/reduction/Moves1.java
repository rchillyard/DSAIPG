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
        if (p.x == tx && p.y == ty) return true;
        //noinspection SimplifiableIfStatement
        if (p.x > tx || p.y > ty) return false;
        return valid(p.x, p.x + p.y) || valid(p.x + p.y, p.y);
    }

    /**
     * This method determines one of the possible moves from a given point based on a particular strategy.
     *
     * @param p     the point from which the move originates
     * @param which a boolean value that determines the specific move strategy to apply
     * @return a new Point representing the result of the move
     */
    public Point move(Point p, boolean which) {
        return null;
    }

    /**
     * Represents the target x-coordinate of the desired point (tx, ty) in the context of
     * solving problems involving reaching a specific point through a series of valid moves.
     * A valid move typically involves adding the x-coordinate and y-coordinate of the
     * current point to compute the next point.
     * <p>
     * This variable is immutable and serves as one of the key components in determining
     * the feasibility of reaching the target point from a given starting point.
     */
    private final int tx;
    /**
     * Represents the y-coordinate of the target point to be reached.
     * This value is constant and initialized at the time of object creation.
     */
    private final int ty;

    /**
     * Constructs an instance of the Moves1 class with the specified target coordinates.
     *
     * @param tx the x-coordinate of the target point
     * @param ty the y-coordinate of the target point
     */
    public Moves1(int tx, int ty) {
        this.tx = tx;
        this.ty = ty;
    }
}
