package com.phasmidsoftware.dsaipg.misc.reduction;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue;
import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;

/**
 * This is from a problem in LeetCode: 780 Reaching Points (Hard)
 */
public class Moves2 {

    /**
     * This method defines the possible moves from point p
     *
     * @param p     the point
     * @param which determines which of two possible moves we want.
     * @return the point we moved to
     */
    public Point move(Point p, boolean which) {
        return which ? new Point(p.x, p.y + p.x) : new Point(p.x + p.y, p.y);
    }

    /**
     * Constructs a Moves2 instance with the specified target point.
     *
     * @param t the target point represented as a Point object
     */
    public Moves2(Point t) {
        this.t = t;
    }

    /**
     * Constructs a Moves2 object with the specified x and y coordinates, initializing it with
     * a Point object representing these coordinates.
     *
     * @param x the x-coordinate of the point to initialize with
     * @param y the y-coordinate of the point to initialize with
     */
    public Moves2(int x, int y) {
        this(new Point(x, y));
    }

    /**
     * Determines if it is possible to reach a target point from a starting point
     * by a series of moves. This implementation uses a queue-based approach
     * to perform a breadth-first check.
     *
     * @param x the x-coordinate of the starting point
     * @param y the y-coordinate of the starting point
     * @return true if the target point can be reached, otherwise false
     */
    public boolean valid(int x, int y) {
        Queue<Point> points = new Queue_Elements<>();
        points.offer(new Point(x, y));
        return inner(points, false);
    }

    /**
     * Recursively processes a queue of points to determine if a specified target point
     * can be reached based on a series of valid moves.
     *
     * @param points the queue of points to process
     * @param result the boolean result indicating if the target has been reached
     * @return true if the target point is reachable, false otherwise
     */
    private boolean inner(Queue<Point> points, boolean result) {
        // NOTE a loop, where this used to recurse once per point taken off the
        // queue. Java does not eliminate a tail call, so the depth was the number
        // of points examined and the search died of StackOverflowError long before
        // it ran out of points -- for (1,1) to (99,100) it managed about fifteen
        // milliseconds. That is why MovesTest.test2_4 had its @Test commented out.
        // The queue was always doing the remembering; the stack was along for the
        // ride.
        while (!points.isEmpty()) {
            Point x = points.poll();
            if (x.equals(t)) return true;
            if (x.x > t.x || x.y > t.y) {
                result = false;
                continue;
            }
            points.offer(move(x, true));
            points.offer(move(x, false));
        }
        return result;
    }

    /**
     * Represents the target point that the algorithm aims to determine
     * whether it can be reached through a series of defined valid moves.
     * This variable is immutable and initialized during the construction
     * of the containing class.
     */
    private final Point t;

}
