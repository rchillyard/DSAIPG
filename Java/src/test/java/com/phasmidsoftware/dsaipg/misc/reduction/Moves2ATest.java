package com.phasmidsoftware.dsaipg.misc.reduction;

import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

/**
 * The forward search with the two improvements that suggest themselves once the
 * plain queue search has been written: deal first with the successor nearer the
 * target, and remember the points already eliminated.
 * <p>
 * The measurements matter as much as the answers here, so they are asserted too.
 */
public class Moves2ATest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void test1() {
        assertTrue(new Moves2A(1, 1).valid(1, 1));
    }

    @Test
    public void test2() {
        assertFalse(new Moves2A(2, 2).valid(1, 1));
    }

    @Test
    public void test3() {
        assertTrue(new Moves2A(3, 5).valid(1, 1));
    }

    @Test
    public void test4() {
        assertFalse(new Moves2A(12, 8).valid(9, 5));
    }

    /**
     * The fifth of the case study's conditions. Moves2 answers this too; what the
     * iteration buys is the ceiling, not this case. Moves2 overflows the stack at
     * a target of about 2584,4181 and this does not overflow at all.
     */
    @Test
    public void test5() {
        assertTrue(new Moves2A(99, 100).valid(1, 1));
    }

    /**
     * The cache never hits, on any of the cases.
     * <p>
     * From a given start, every reachable point has exactly ONE predecessor -- of
     * (x - y, y) and (x, y - x) only one can have both coordinates positive -- so
     * no point can be arrived at twice and there is nothing for a cache to
     * remember. Worth measuring rather than assuming: it is the same observation
     * that makes {@link Moves3} work, met here in the form of an improvement that
     * turns out to be worth nothing.
     */
    @Test
    public void theCacheNeverHits() {
        Moves2A moves = new Moves2A(99, 100);
        assertTrue(moves.valid(1, 1));
        assertEquals("no point is ever reached twice", 0, moves.getCacheHits());
        Moves2A small = new Moves2A(3, 5);
        small.valid(1, 1);
        assertEquals(0, small.getCacheHits());
    }

    /**
     * Nor does the ordering change how much work is done.
     * <p>
     * A queue is level-by-level, so whichever successor goes on first, both are
     * dealt with before anything either of them leads to. Ordering siblings can
     * only help on the last level. The 12,090 points here are what a plain queue
     * examines too.
     */
    @Test
    public void theOrderingDoesNotReduceTheWork() {
        Moves2A moves = new Moves2A(99, 100);
        assertTrue(moves.valid(1, 1));
        assertEquals("as many points as a plain queue examines", 12090, moves.getExamined());
    }

    /**
     * Why neither improvement can rescue this approach, and why the sixth case --
     * 35,13 to 455955547,420098884 -- is out of reach however the search is tuned.
     * <p>
     * Consecutive Fibonacci numbers are the worst targets, needing the longest
     * path. Stepping from one pair to the next multiplies the target by only
     * phi, about 1.618, and roughly doubles the work. So the work grows as a
     * power of the target, near its square: over the range below the target grows
     * 29-fold and the work 85-fold.
     * <p>
     * A constant factor -- which is all that ordering or caching could ever be
     * worth -- does not touch that. Only turning the problem round does, which is
     * what {@link Moves3} does.
     */
    @Test
    public void theWorkGrowsAsAPowerOfTheTarget() {
        int[][] targets = {{5, 8}, {8, 13}, {13, 21}, {21, 34}, {34, 55}, {55, 89}, {89, 144}, {144, 233}};
        long previous = 0;
        long first = 0, last = 0;
        for (int[] t : targets) {
            Moves2A moves = new Moves2A(t[0], t[1]);
            moves.valid(1, 1);
            long examined = moves.getExamined();
            assertTrue("a bigger target is never less work", examined >= previous);
            previous = examined;
            if (first == 0) first = examined;
            last = examined;
        }
        assertEquals(24, first);
        assertEquals(2048, last);
        assertTrue("the work outgrows the target by a long way: 85-fold against 29-fold",
                last / first > 80 && 144 / 5 < 30);
    }
}
