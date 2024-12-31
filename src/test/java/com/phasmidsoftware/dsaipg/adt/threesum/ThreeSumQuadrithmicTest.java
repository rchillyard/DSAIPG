package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ThreeSumQuadrithmicTest {

    /**
     * Test case for getTriple method when the array contains a valid triple.
     */
//    @Test
    public void testGetTriple_ValidTriple() {
        // FIXME
        int[] inputArray = {-1, 0, 1, 2, -1, -4};
        Arrays.sort(inputArray); // Ensuring binarySearch works on a sorted array
        ThreeSumQuadrithmic threeSum = new ThreeSumQuadrithmic(inputArray);

        Triple result = threeSum.getTriple(0, 2); // Assuming index 0 = -1, index 2 = 1
        assertNotNull(result);
        assertEquals(0, result.sum());
        assertEquals(new Triple(-1, 0, 1), result);
    }

    /**
     * Test case for getTriple method when no valid triple exists.
     */
    @Test
    public void testGetTriple_NoTripleExists() {
        int[] inputArray = {1, 2, 3, 4, 5};
        Arrays.sort(inputArray);
        ThreeSumQuadrithmic threeSum = new ThreeSumQuadrithmic(inputArray);

        Triple result = threeSum.getTriple(0, 1); // Indices do not form a valid triple
        assertNull(result);
    }

    /**
     * Test case for getTriple method when negative numbers are involved.
     */
//    @Test
    public void testGetTriple_WithNegatives() {
        // FIXME
        int[] inputArray = {-5, -2, 0, 4, 6};
        Arrays.sort(inputArray);
        ThreeSumQuadrithmic threeSum = new ThreeSumQuadrithmic(inputArray);

        Triple result = threeSum.getTriple(1, 3); // Assuming a valid triple exists
        assertNotNull(result);
        assertEquals(0, result.sum());
        assertEquals(new Triple(-2, 4, -2), result);
    }
}