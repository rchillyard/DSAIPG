package com.phasmidsoftware.dsaipg.misc.reduction;

import org.junit.Test;

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
     * Reinstated. It was disabled because Moves2's search recursed once per point
     * and overflowed the stack; it is a loop now, and this passes in milliseconds.
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
