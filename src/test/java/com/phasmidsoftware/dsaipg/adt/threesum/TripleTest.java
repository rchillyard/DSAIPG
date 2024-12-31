package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TripleTest {

    /**
     * Tests the `sum` method of the Triple class.
     * The `sum` method computes the sum of the three integer fields (x, y, z) of the Triple instance.
     */

    @Test
    public void testSum_AllPositive() {
        Triple triple = new Triple(1, 2, 3);
        int result = triple.sum();
        assertEquals(6, result);
    }

    @Test
    public void testSum_AllNegative() {
        Triple triple = new Triple(-1, -2, -3);
        int result = triple.sum();
        assertEquals(-6, result);
    }

    @Test
    public void testSum_PositiveAndNegative() {
        Triple triple = new Triple(-1, 2, 3);
        int result = triple.sum();
        assertEquals(4, result);
    }

    @Test
    public void testSum_WithZero() {
        Triple triple = new Triple(0, 2, 3);
        int result = triple.sum();
        assertEquals(5, result);
    }

    @Test
    public void testSum_AllZero() {
        Triple triple = new Triple(0, 0, 0);
        int result = triple.sum();
        assertEquals(0, result);
    }
}