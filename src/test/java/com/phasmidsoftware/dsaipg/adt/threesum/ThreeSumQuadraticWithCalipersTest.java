package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ThreeSumQuadraticWithCalipersTest {

    /**
     * Test case: Ensure `getTriples` finds all unique triples that sum to zero in a simple input.
     */
//    @Test
    public void testGetTriplesSimple() {
        // FIXME
        int[] input = {-1, 0, 1, 2, -1, -4};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] expected = {new Triple(-1, -1, 2), new Triple(-1, 0, 1)};
        Triple[] result = threeSum.getTriples();
        Arrays.sort(result); // For consistent ordering
        System.out.println(Arrays.toString(result));
        Arrays.sort(expected);
        assertArrayEquals(expected, result);
    }

    /**
     * Test case: Validate `getTriples` handles input with no triples summing to zero.
     */
    @Test
    public void testGetTriplesNoTriples() {
        int[] input = {1, 2, 3, 4, 5};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] result = threeSum.getTriples();
        assertEquals(0, result.length);
    }

    /**
     * Test case: Check `getTriples` handles input containing duplicates properly and avoids duplicate triples.
     */
//    @Test
    public void testGetTriplesWithDuplicates() {
        // FIXME
        int[] input = {-1, -1, -1, 2, 2, 0, 0, 1, 1};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] expected = {new Triple(-1, -1, 2), new Triple(-1, 0, 1)};
        Triple[] result = threeSum.getTriples();
        Arrays.sort(result);
        System.out.println(Arrays.toString(result));
        Arrays.sort(expected);
        assertArrayEquals(expected, result);
    }

    /**
     * Test case: Validate `getTriples` on an empty array.
     */
    @Test
    public void testGetTriplesEmptyInput() {
        int[] input = {};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] result = threeSum.getTriples();
        assertEquals(0, result.length);
    }

    /**
     * Test case: Validate `getTriples` on an array with less than three elements.
     */
    @Test
    public void testGetTriplesInsufficientElements() {
        int[] input = {1, -1};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] result = threeSum.getTriples();
        assertEquals(0, result.length);
    }

    /**
     * Test case: Validate `getTriples` handles input where all elements are zero.
     */
    @Test
    public void testGetTriplesAllZeros() {
        int[] input = {0, 0, 0, 0};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] expected = {new Triple(0, 0, 0)};
        Triple[] result = threeSum.getTriples();
        assertArrayEquals(expected, result);
    }

    /**
     * Test case: Validate `getTriples` on a large input with only one valid triple.
     */
//    @Test
    public void testGetTriplesSingleValidTriple() {
        // FIXME
        int[] input = {-10, -7, -3, 0, 7, 10, 3};
        ThreeSum threeSum = new ThreeSumQuadraticWithCalipers(input);
        Triple[] expected = {new Triple(-10, 0, 10)};
        Triple[] result = threeSum.getTriples();
        System.out.println(Arrays.toString(result));
        assertArrayEquals(expected, result);
    }
}