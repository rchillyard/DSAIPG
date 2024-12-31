/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

/**
 * The TwoSum interface specifies a contract for finding all pairs of integers within a dataset
 * such that the sum of each pair satisfies a certain property or condition.
 * Implementations of this interface may vary in terms of efficiency, algorithmic approach,
 * and assumptions about the input data.
 */
public interface TwoSum {
    /**
     * Method to get the pairs from this instance of TwoSum.
     * <p>
     * NOTE: it is essential that the array to be operated on is ordered and distinct.
     * The quadratic implementation of TwoSum doesn't care about order of distinction, however.
     *
     * @return an ordered, distinct, array of Pair.
     */
    Pair[] getPairs();
}