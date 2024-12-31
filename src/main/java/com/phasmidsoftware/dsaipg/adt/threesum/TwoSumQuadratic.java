/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The TwoSumQuadratic class provides an implementation of the TwoSum interface
 * that determines all pairs of integers in a dataset whose sum equals zero.
 * This implementation operates with quadratic time complexity (O(n^2)).
 * The resulting pairs are returned as an ordered and distinct array of Pair objects.
 * This class assumes no particular property of the input array, such as being sorted or distinct.
 */
class TwoSumQuadratic implements TwoSum {
    /**
     * Constructs a new instance of the TwoSumQuadratic class with the specified array of integers.
     * The array is used as the dataset for finding pairs of integers whose sum equals zero.
     *
     * @param a the input array of integers to be used for finding pairs.
     *         The array can have any integers,
     *          and no specific properties (e.g., sorted or distinct) are assumed.
     */
    public TwoSumQuadratic(int[] a) {
        this.a = a;
        length = a.length;
    }

    /**
     * Finds and returns an array of unique pairs of integers from the dataset whose sum equals zero.
     * The pairs are sorted and do not contain duplicates.
     *
     * @return an array of Pair objects representing unique pairs of integers whose sum is zero,
     * sorted in natural order.
     */
    public Pair[] getPairs() {
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < length; i++)
            for (int j = i + 1; j < length; j++) {
                if (a[i] + a[j] == 0)
                    pairs.add(new Pair(a[i], a[j]));
            }
        Collections.sort(pairs);
        return pairs.stream().distinct().toArray(Pair[]::new);
    }

    private final int[] a;
    private final int length;
}