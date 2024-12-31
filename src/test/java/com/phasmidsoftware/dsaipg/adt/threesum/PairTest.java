package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PairTest {

    /**
     * Test case for verifying that the sum of two positive integers is calculated correctly.
     */
    @Test
    public void testSum_PositiveIntegers() {
        Pair pair = new Pair(3, 5);
        int result = pair.sum();
        assertEquals(8, result);
    }

    /**
     * Test case for verifying that the sum of two negative integers is calculated correctly.
     */
    @Test
    public void testSum_NegativeIntegers() {
        Pair pair = new Pair(-4, -7);
        int result = pair.sum();
        assertEquals(-11, result);
    }

    /**
     * Test case for verifying that the sum of a positive and a negative integer is calculated correctly.
     */
    @Test
    public void testSum_PositiveAndNegativeInteger() {
        Pair pair = new Pair(10, -3);
        int result = pair.sum();
        assertEquals(7, result);
    }

    /**
     * Test case for verifying that the sum of two zeros is zero.
     */
    @Test
    public void testSum_ZeroValues() {
        Pair pair = new Pair(0, 0);
        int result = pair.sum();
        assertEquals(0, result);
    }

    /**
     * Test case for verifying that the sum of a zero and a non-zero value is the non-zero value.
     */
    @Test
    public void testSum_ZeroAndNonZeroValue() {
        Pair pair = new Pair(0, 12);
        int result = pair.sum();
        assertEquals(12, result);
    }
}