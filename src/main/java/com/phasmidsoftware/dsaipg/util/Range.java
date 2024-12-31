/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

/**
 * Represents a numerical range defined by a lower and upper bound.
 * The range is inclusive of the lower and upper bounds.
 */
public class Range {
    /**
     * Constructor
     *
     * @param low  the lowest number considered in range.
     * @param high the highest number considered in range.
     */
    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    /**
     * Method to determine if a number is in this Range.
     *
     * @param number the number to be tested.
     * @return true if the number is in this Range (inclusive).
     */
    public boolean contains(int number) {
        return (number >= low && number <= high);
    }

    /**
     * Creates and returns a new instance of the Range class with the specified lower and upper bounds.
     *
     * @param low  the lower boundary of the range (inclusive).
     * @param high the upper boundary of the range (inclusive).
     * @return a new Range instance representing the specified numerical range.
     */
    public static Range inclusive(int low, int high) {
        return new Range(low, high);
    }

    /**
     * Creates and returns a new instance of the Range class with the specified lower and upper bounds.
     *
     * @param from the lower boundary of the range (inclusive).
     * @param to   the upper boundary of the range (exclusive).
     * @return a new Range instance representing the specified numerical range.
     */
    public static Range exclusive(int from, int to) {
        return new Range(from, to - 1);
    }

    private final int low;
    private final int high;
}