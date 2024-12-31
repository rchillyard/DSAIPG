package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the Range class.
 * <p>
 * The Range class provides functionality to represent an integer range
 * and check if a specified number is within the range.
 * The contains method checks if a given number is within the bounds
 * of the range.
 */
public class RangeTest {

    @Test
    public void testContains_SingleValueWithinRange() {
        Range range = Range.inclusive(5, 10);
        assertTrue("The range should contain the number 7", range.contains(7));
    }

    @Test
    public void testContains_BoundaryLowValue() {
        Range range = Range.inclusive(5, 10);
        assertTrue("The range should contain the lower boundary value 5", range.contains(5));
    }

    @Test
    public void testContains_BoundaryHighValue() {
        Range range = Range.inclusive(5, 10);
        assertTrue("The range should contain the upper boundary value 10", range.contains(10));
    }

    @Test
    public void testContains_ValueBelowRange() {
        Range range = Range.inclusive(5, 10);
        assertFalse("The range should not contain the value 4", range.contains(4));
    }

    @Test
    public void testContains_ValueAboveRange() {
        Range range = Range.inclusive(5, 10);
        assertFalse("The range should not contain the value 11", range.contains(11));
    }

    @Test
    public void testContains_ExclusiveRangeValueOnBoundary() {
        Range range = Range.exclusive(5, 10);
        assertFalse("The range should not contain the upper exclusive boundary value 10", range.contains(10));
    }

    @Test
    public void testContains_ExclusiveRangeInnerValue() {
        Range range = Range.exclusive(5, 10);
        assertTrue("The range should contain the value 9 in an exclusive range from 5 to 10", range.contains(9));
    }

    @Test
    public void testContains_SingleElementRange() {
        Range range = Range.inclusive(7, 7);
        assertTrue("The range should contain the single value 7", range.contains(7));
    }

    @Test
    public void testContains_EmptyRange() {
        Range range = Range.exclusive(7, 6);
        assertFalse("The range should not contain any value in an empty range", range.contains(7));
    }
}