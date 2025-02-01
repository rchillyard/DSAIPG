package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ThreeSumQuadraticTest {

    /**
     * Test class for ThreeSumQuadratic.
     * Focuses on verifying the behavior of the getTriples method,
     * which identifies unique triplets in an array such that their sum is zero.
     */

    @Test
    public void testGetTriplesWithValidTriplets() {
        int[] inputArray = {-4, -1, -1, 0, 1, 2};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        Triple[] expected = {
                new Triple(-1, -1, 2),
                new Triple(-1, 0, 1)
        };

        System.out.println(Arrays.toString(result));
        assertArrayEquals(expected, result);
    }

    @Test
    public void testGetTriplesWithNoTriplets() {
        int[] inputArray = {1, 2, 3, 4, 5};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    @Test
    public void testGetTriplesWithAllZeros() {
        int[] inputArray = {0, 0, 0, 0};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(1, result.length);
        assertTrue(containsTriple(result, new Triple(0, 0, 0)));
    }

    @Test
    public void testGetTriplesWithDuplicateValues() {
        int[] inputArray = {-2, -2, 2, 2};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    @Test
    public void testGetTriplesWithEmptyArray() {
        int[] inputArray = {};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    @Test
    public void testGetTriplesWithSingleElement() {
        int[] inputArray = {1};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    @Test
    public void testGetTriplesWithLargeArray() {
        int[] inputArray = new int[1000];
        for (int i = 0; i < 1000; i++) {
            inputArray[i] = i - 500;
        }
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertTrue(result.length > 0);
    }

    @Test
    public void testGetTriplesWithNegativeNumbersOnly() {
        int[] inputArray = {-7, -5, -3, -1};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    @Test
    public void testGetTriplesWithPositiveNumbersOnly() {
        int[] inputArray = {1, 2, 3, 4, 5};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    @Test
    public void testGetTriplesWithEdgeCaseValues() {
        int[] inputArray = {Integer.MIN_VALUE + 1, 0, Integer.MAX_VALUE};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(1, result.length);
        assertTrue(containsTriple(result, new Triple(Integer.MIN_VALUE + 1, 0, Integer.MAX_VALUE)));
    }

    @Test
    public void testGetTriplesWithTwoElements() {
        int[] inputArray = {1, -1};
        ThreeSum threeSum = new ThreeSumQuadratic(inputArray);

        Triple[] result = threeSum.getTriples();

        assertEquals(0, result.length);
    }

    private boolean containsTriple(Triple[] triples, Triple target) {
        for (Triple triple : triples) {
            if (triple.equals(target)) {
                return true;
            }
        }
        return false;
    }
}