/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.misc.randomwalk;

import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class RandomWalkTest {

    @Test
    public void testMove0() {
        RandomWalk rw = new RandomWalk();
        PrivateMethodTester pmt = new PrivateMethodTester(rw);
        pmt.invokePrivate("move", 1, 0);
        assertEquals(1.0, rw.distance(), 1.0E-7);
    }

    /**
     * Test that distance returns 0.0 for a RandomWalk object in its initial state.
     */
    @Test
    public void testDistanceInitial() {
        RandomWalk rw = new RandomWalk();
        assertEquals(0.0, rw.distance(), 1.0E-7);
    }

    /**
     * Test that distance calculates correctly after a series of movements.
     */
    @Test
    public void testDistanceAfterMove() {
        RandomWalk rw = new RandomWalk();
        PrivateMethodTester pmt = new PrivateMethodTester(rw);
        pmt.invokePrivate("move", 3, 4); // Moves to (3, 4)
        assertEquals(5.0, rw.distance(), 1.0E-7); // sqrt(3^2 + 4^2) = 5
        pmt.invokePrivate("move", -3, -4); // Moves back to (0, 0)
        assertEquals(0.0, rw.distance(), 1.0E-7);
    }

    /**
     * Test distance for edge cases, such as large movements.
     */
    @Test
    public void testDistanceEdgeCases() {
        RandomWalk rw = new RandomWalk();
        PrivateMethodTester pmt = new PrivateMethodTester(rw);
        pmt.invokePrivate("move", Integer.MAX_VALUE, 0); // Large positive move
        assertEquals(Integer.MAX_VALUE, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 0, -Integer.MAX_VALUE); // Neutralize the movement
        assertEquals(Math.sqrt(2) * Integer.MAX_VALUE, rw.distance(), 1.0E-7);
    }

    @Test
    public void testMove1() {
        RandomWalk rw = new RandomWalk();
        PrivateMethodTester pmt = new PrivateMethodTester(rw);
        pmt.invokePrivate("move", 1, 0);
        assertEquals(1.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 1, 0);
        assertEquals(2.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", -1, 0);
        assertEquals(1.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", -1, 0);
        assertEquals(0.0, rw.distance(), 1.0E-7);
    }

    /**
     *
     */
    @Test
    public void testMove2() {
        RandomWalk rw = new RandomWalk();
        PrivateMethodTester pmt = new PrivateMethodTester(rw);
        pmt.invokePrivate("move", 0, 1);
        assertEquals(1.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 0, 1);
        assertEquals(2.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 0, -1);
        assertEquals(1.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 0, -1);
        assertEquals(0.0, rw.distance(), 1.0E-7);
    }

    /**
     *
     */
    @Test
    public void testMove3() {
        RandomWalk rw = new RandomWalk();
        double root2 = Math.sqrt(2);
        PrivateMethodTester pmt = new PrivateMethodTester(rw);
        pmt.invokePrivate("move", 1, 1);
        assertEquals(root2, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 1, 1);
        assertEquals(2 * root2, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", 0, -2);
        assertEquals(2.0, rw.distance(), 1.0E-7);
        pmt.invokePrivate("move", -2, 0);
        assertEquals(0.0, rw.distance(), 1.0E-7);
    }

    /**
     *
     */
    @Test // Slow
    public void testRandomWalk() {
        for (int i = 0; i < 1000; i++)
            assertEquals(10, RandomWalk.randomWalkMulti(100, 100), 4);
    }

    @Test
    public void testRandomWalk2() {
        for (int i = 0; i < 5000; i++)
            assertNotSame(0, RandomWalk.randomWalkMulti(1, 1));
    }
}