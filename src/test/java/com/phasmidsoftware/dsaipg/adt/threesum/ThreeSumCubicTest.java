package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ThreeSumCubicTest {

    /**
     * Test case for checking when the input array has multiple valid triples
     * that sum to zero.
     */
    @Test
    public void testMultipleTriples() {
        int[] inputArray = {-1, 0, 1, 2, -1, -4};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {
                new Triple(-1, 0, 1),
                new Triple(-1, 2, -1),
                new Triple(-0, 1, -1)
        };
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }

    /**
     * Test case for checking when the input array has no triples that sum to zero.
     */
    @Test
    public void testNoTriples() {
        int[] inputArray = {1, 2, 3, 4, 5};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {};
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }

    /**
     * Test case for checking when the input array has only zeros.
     */
    @Test
    public void testAllZeros() {
        int[] inputArray = {0, 0, 0, 0};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {
                new Triple(0, 0, 0)
        };
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }

    /**
     * Test case for checking when the input array is empty.
     */
    @Test
    public void testEmptyArray() {
        int[] inputArray = {};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {};
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }

    /**
     * Test case for checking when the input array has only one element.
     */
    @Test
    public void testSingleElement() {
        int[] inputArray = {1};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {};
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }

    /**
     * Test case for checking when the input array has only two elements.
     */
    @Test
    public void testTwoElements() {
        int[] inputArray = {1, -1};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {};
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }

    /**
     * Test case for input with duplicate triples to ensure the output is distinct.
     */
    @Test
    public void testDuplicateTriples() {
        int[] inputArray = {-1, -1, 2, 2, 0, 1, 1};
        ThreeSum threeSum = new ThreeSumCubic(inputArray);
        Triple[] expected = {
                new Triple(-1, -1, 2),
                new Triple(-1, 0, 1)
        };
        Triple[] actual = threeSum.getTriples();
        assertArrayEquals(expected, actual);
    }
}