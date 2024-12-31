package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TwoSumWithCalipersTest {

    /**
     * Test class for TwoSumWithCalipers.
     * This class verifies the functionality of the `getPairs` method which finds unique pairs of integers
     * in a sorted array where the sum of the pair equals zero.
     */
    @Test
    public void testGetPairsWithValidInput() {
        // Input array
        int[] input = {-4, -2, 0, 2, 4};
        TwoSumWithCalipers twoSumWithCalipers = new TwoSumWithCalipers(input);

        // Expected output
        Pair[] expected = {new Pair(-4, 4), new Pair(-2, 2)};

        // Actual output
        Pair[] actual = twoSumWithCalipers.getPairs();

        // Assertion
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetPairsWithEmptyArray() {
        // Input array
        int[] input = {};
        TwoSumWithCalipers twoSumWithCalipers = new TwoSumWithCalipers(input);

        // Expected output
        Pair[] expected = {};

        // Actual output
        Pair[] actual = twoSumWithCalipers.getPairs();

        // Assertion
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetPairsWithNoPairs() {
        TwoSumWithCalipers twoSumWithCalipers = new TwoSumWithCalipers(new int[]{-3, -1, 2, 5});
        assertArrayEquals(new Pair[]{}, twoSumWithCalipers.getPairs());
    }

    @Test
    public void testGetPairsWithDuplicateNumbersAndPairs() {
        // Input array
        int[] input = {-2, -2, 2, 2};
        TwoSumWithCalipers twoSumWithCalipers = new TwoSumWithCalipers(input);

        // Expected output
        Pair[] expected = {new Pair(-2, 2)};

        // Actual output
        Pair[] actual = twoSumWithCalipers.getPairs();

        // Assertion
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetPairsWithLargeInput() {
        // Input array
        int[] input = {-10, -5, -1, 0, 1, 5, 10};
        TwoSumWithCalipers twoSumWithCalipers = new TwoSumWithCalipers(input);

        // Expected output
        Pair[] expected = {new Pair(-10, 10), new Pair(-5, 5), new Pair(-1, 1)};

        // Actual output
        Pair[] actual = twoSumWithCalipers.getPairs();

        // Assertion
        assertArrayEquals(expected, actual);
    }
}