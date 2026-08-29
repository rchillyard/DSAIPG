package com.phasmidsoftware.dsaipg.misc.reduction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class MovesTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void test2_1() {
        System.out.println("test1: 1,1->3,5");
        assertTrue(new Moves2(3, 5).valid(1, 1));
    }

    @Test
    public void test2_2() {
        System.out.println("test2: 1,1->2,2");
        assertFalse(new Moves2(2, 2).valid(1, 1));
    }

    @Test
    public void test2_3() {
        System.out.println("test3: 1,1->1,1");
        assertTrue(new Moves2(1, 1).valid(1, 1));
    }

    /**
     * The fifth of the case study's conditions. Moves2 does answer it, in about a
     * millisecond, examining some twelve thousand points on the way.
     * <p>
     * It was disabled, and there is no reason for it to be: measured on Java 21 it
     * passes. The recursion in Moves2.inner does have a ceiling -- it goes one
     * frame deep per point examined -- but the ceiling sits well beyond this case,
     * at a target of about 2584,4181.
     */
    @Test
    public void test2_4() {
        System.out.println("test3: 1,1->99,100");
        assertTrue(new Moves2(99, 100).valid(1, 1));
    }

    /**
     * Left disabled, and it must stay that way -- this one is not a stack problem.
     * Searching FORWARDS from (35,13) towards a target of about 4.5 x 10^8 reaches
     * more than 11 million points without exhausting the queue, and the queue is
     * still growing. No amount of fixing Moves2 will help, because the tree it
     * walks is exponential.
     * <p>
     * That is the entire point of this package. test3_5 asks Moves3 the identical
     * question and it answers immediately, because running the moves BACKWARDS
     * from the target leaves no choice at any step: the search collapses into the
     * subtractive Euclidean algorithm.
     */
    //    @Test
    public void test2_5() {
        System.out.println("test3: 35,13->455955547,420098884");
        assertFalse(new Moves2(455955547, 420098884).valid(35, 13));
    }

    /**
     * 9,5 to 12,8 -- a condition from the case study that had no test here. Neither
     * coordinate can be made to fit: going back from 12,8 gives 4,8, whose x has
     * already fallen below the start's.
     */
    @Test
    public void test2_6() {
        assertFalse(new Moves2(12, 8).valid(9, 5));
    }

    @Test
    public void test3_6() {
        assertFalse(new Moves3(9, 5).valid(12, 8));
    }

    @Test
    public void test1_6() {
        assertFalse(new Moves1(12, 8).valid(new Point(9, 5)));
    }

    /**
     * Moves1 answers the first five conditions, without any queue. It is the
     * pseudocode of slide #1a: the base case is what makes it terminate.
     */
    @Test
    public void test1_all() {
        assertTrue(new Moves1(1, 1).valid(new Point(1, 1)));
        assertFalse(new Moves1(2, 2).valid(new Point(1, 1)));
        assertTrue(new Moves1(3, 5).valid(new Point(1, 1)));
        assertFalse(new Moves1(12, 8).valid(new Point(9, 5)));
        assertTrue(new Moves1(99, 100).valid(new Point(1, 1)));
    }

    /**
     * move is the two legal moves, and is what valid recurses on. It used to
     * return null, since nothing called it.
     */
    @Test
    public void test1_move() {
        Moves1 moves = new Moves1(99, 100);
        assertEquals("grow y", new Point(2, 5), moves.move(new Point(2, 3), true));
        assertEquals("grow x", new Point(5, 3), moves.move(new Point(2, 3), false));
    }

    /**
     * A target BELOW the start is never reachable, since both coordinates only ever
     * grow. Moves3 used to say otherwise whenever the target was aligned with the
     * start: the aligned rules divide by a modulus, and (-1) % 1 is 0.
     */
    @Test
    public void test3_belowTheStart() {
        assertFalse(new Moves3(1, 2).valid(1, 1));
        assertFalse(new Moves3(1, 3).valid(1, 2));
        assertFalse(new Moves3(2, 1).valid(1, 1));
        assertFalse(new Moves3(5, 5).valid(3, 3));
    }

    /**
     * The reduction must agree with the brute-force search everywhere, not merely
     * on the six conditions. Over 8x8 starts and 60x60 targets that is 230,400
     * questions, and they used to disagree on 114 of them -- every one a target
     * below its start.
     */
    @Test
    public void test3_agreesWithBruteForceEverywhere() {
        for (int sx = 1; sx <= 8; sx++)
            for (int sy = 1; sy <= 8; sy++)
                for (int tx = 1; tx <= 60; tx++)
                    for (int ty = 1; ty <= 60; ty++)
                        assertEquals(sx + "," + sy + "->" + tx + "," + ty,
                                new Moves1(tx, ty).valid(new Point(sx, sy)),
                                new Moves3(sx, sy).valid(tx, ty));
    }

    @Test
    public void test3_1() {
        System.out.println("test1: 1,1->3,5");
        assertTrue(new Moves3(1, 1).valid(3, 5));
    }

    @Test
    public void test3_2() {
        System.out.println("test2: 1,1->2,2");
        assertFalse(new Moves3(1, 1).valid(2, 2));
    }

    @Test
    public void test3_3() {
        System.out.println("test3: 1,1->1,1");
        assertTrue(new Moves3(1, 1).valid(1, 1));
    }

    @Test
    public void test3_4() {
        System.out.println("test3: 1,1->99,100");
        assertTrue(new Moves3(1, 1).valid(99, 100));
    }

    @Test
    public void test3_5() {
        System.out.println("test3: 35,13->455955547,420098884");
        assertFalse(new Moves3(35, 13).valid(455955547, 420098884));
    }
}
