/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of ThreeSum which follows the simple optimization of requiring
 * a sorted array, then using binary search to find an element x where -x the
 * sum of a pair of elements.
 * <p>
 * The array provided in the constructor MUST be ordered.
 * <p>
 * This algorithm runs in O(N^2 log N) time.
 */
class ThreeSumQuadrithmic implements ThreeSum {

    /**
     * Construct a ThreeSumQuadrithmic on a.
     *
     * @param a a sorted array.
     */
    public ThreeSumQuadrithmic(int[] a) {
        this.a = a;
        length = a.length;
    }

    /**
     * Retrieves an array of unique, sorted triples that represent combinations
     * of three integers in the underlying sorted array whose sum equals zero.
     * <p>
     * This method iterates over all pairs of elements in the array, uses a
     * helper method to find the third element that satisfies the condition, and
     * returns all such combinations as distinct {@code Triple} objects sorted
     * in natural order.
     *
     * @return an array of unique {@code Triple} objects representing all valid
     * combinations of three integers in the array that sum to zero.
     */
    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                Triple triple = getTriple(i, j);
                if (triple != null) {
                    triples.add(triple);
                }
            }
        }
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Finds a "triple" consisting of three integers from the sorted array such
     * that their sum equals zero, given two indices representing the first two
     * elements of the triple.
     *
     * @param i the index of the first element in the triple.
     * @param j the index of the second element in the triple. Must satisfy j >
     * i.
     * @return a {@code Triple} object representing the three integers if such a
     * combination exists, or {@code null} if no such triple can be found.
     */
    Triple getTriple(int i, int j) {
        int target = -(a[i] + a[j]);
        int lo = j + 1;
        int hi = length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < target) {
                lo = mid + 1;
            } else if (a[mid] > target) {
                hi = mid - 1;
            } else {
                return new Triple(a[i], a[j], a[mid]);
            }
        }
        return null;
    }

    private final int[] a;
    private final int length;
}