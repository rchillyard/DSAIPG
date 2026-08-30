package com.phasmidsoftware.dsaipg.misc.reduction;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue;
import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * The reaching-points problem, searching forwards, with the two improvements that
 * suggest themselves once the plain queue search has been written.
 * <p>
 * The first is that {@link Moves2} puts both successors on the queue without
 * troubling over which should be dealt with first, though it can make a great
 * difference which path is followed. So the successor nearer the target goes on
 * first.
 * <p>
 * The second is to remember the points already eliminated, in a Set, so that no
 * point is examined twice.
 * <p>
 * The search is also an iteration rather than a recursion, which is what
 * {@link Moves2} shows the need for.
 * <p>
 * NOTE both improvements are worth measuring rather than assuming, and the tests
 * measure them. The cache NEVER hits: from a given start every reachable point has
 * exactly one predecessor, so no point can be reached twice and there is nothing
 * to remember. That is worth knowing for its own sake, because the reason is the
 * observation that eventually solves the problem -- see {@link Moves3}. The
 * ordering does not change the number of points examined either, a queue being
 * level-by-level: whichever sibling goes on first, both are dealt with before
 * anything they lead to.
 * <p>
 * What the iteration does buy is the fifth test case, 1,1 to 99,100, which
 * {@link Moves2} cannot reach at all. It gets nowhere near the sixth.
 */
public class Moves2A implements Moves {

    /**
     * @param p the point to move from.
     * @param which true to grow y, false to grow x.
     * @return where that move lands.
     */
    public Point move(Point p, boolean which) {
        return which ? new Point(p.x, p.y + p.x) : new Point(p.x + p.y, p.y);
    }

    /**
     * @param p the point to start from.
     * @return true if the target can be reached from it.
     */
    public boolean valid(Point p) {
        Queue<Point> points = new Queue_Elements<>();
        points.offer(p);
        return inner(points);
    }

    /**
     * @param x the starting x coordinate.
     * @param y the starting y coordinate.
     * @return true if the target can be reached from there.
     */
    public boolean valid(int x, int y) {
        return valid(new Point(x, y));
    }

    /**
     * @return how many points the last search examined.
     */
    public long getExamined() {
        return examined;
    }

    /**
     * @return how many times the cache spared the search a point it had already
     * eliminated. Expected to be zero: see the class comment.
     */
    public long getCacheHits() {
        return cacheHits;
    }

    /**
     * Take points off the queue until one is the target or none are left.
     *
     * @param points the points still to consider.
     * @return true if the target was reached.
     */
    private boolean inner(Queue<Point> points) {
        examined = 0;
        cacheHits = 0;
        Set<Point> eliminated = new HashSet<>();
        while (!points.isEmpty()) {
            examined++;
            Point p = points.poll();
            if (p.equals(t)) return true;
            if (p.x > t.x || p.y > t.y) continue;   // overshot: this path is dead
            if (!eliminated.add(p)) {
                cacheHits++;
                continue;
            }
            Point a = move(p, true), b = move(p, false);
            // the nearer of the two goes on first
            if (distance(a) <= distance(b)) {
                points.offer(a);
                points.offer(b);
            } else {
                points.offer(b);
                points.offer(a);
            }
        }
        return false;
    }

    /**
     * @param p a point not beyond the target.
     * @return how far it is from the target, by the number of units in each
     * direction still to be covered.
     */
    private long distance(Point p) {
        return (long) (t.x - p.x) + (t.y - p.y);
    }

    /**
     * @param t the target.
     */
    public Moves2A(Point t) {
        this.t = t;
    }

    /**
     * @param x the target's x coordinate.
     * @param y the target's y coordinate.
     */
    public Moves2A(int x, int y) {
        this(new Point(x, y));
    }

    private final Point t;
    private long examined;
    private long cacheHits;
}
