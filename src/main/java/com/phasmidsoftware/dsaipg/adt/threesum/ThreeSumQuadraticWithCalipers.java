/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import com.phasmidsoftware.dsaipg.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of ThreeSum which follows the approach of dividing the solution-space into
 * N sub-spaces where each subspace corresponds to a fixed value for the middle index of the three values.
 * Each subspace is then solved by expanding the scope of the other two indices outwards from the starting point.
 * Since each subspace can be solved in O(N) time, the overall complexity is O(N^2).
 * <p>
 * The array provided in the constructor MUST be ordered.
 */
public class ThreeSumQuadraticWithCalipers implements ThreeSum {
    /**
     * Construct ints ThreeSumQuadratic on ints.
     *
     * @param ints a sorted array.
     */
    public ThreeSumQuadraticWithCalipers(int[] ints) {
        this.a = ints;
        length = ints.length;
    }

    /**
     * Get an array or Triple containing all of those triples for which sum is zero.
     *
     * @return a Triple[].
     */
    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        Collections.sort(triples); // ???
        for (int i = 0; i < length - 2; i++)
            triples.addAll(calipers(a, i, Triple::sum));

        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Get a set of candidate Triples such that the first index is the given value i.
     * Any candidate triple is added to the result if it yields zero when passed into function.
     *
     * @param a        a sorted array of ints. This method is concerned only with the partition of a starting with index i+1.
     * @param i        the index of the first element of resulting triples.
     * @param function a function which takes a triple and returns a value which will be compared with zero.
     * @return a List of Triples.
     */
    public static List<Triple> calipers(int[] a, int i, Function<Triple, Integer> function) {
        List<Triple> triples = new ArrayList<>();
        int left = i+1;
        int right = a.length-1;
        while (left<right) {
            int sum = a[left] + a[i] + a[right];
            if (sum > 0) {
                right -= 1;
            }
            else if (sum < 0) {
                left += 1;
            }
            else {
                triples.add(new Triple(a[i], a[left ], a[right]));
                left++;
                right--;
                while (left < i && left > 0 && a[left] == a[left - 1]) left++;
                while (right > i && right < a.length - 1 && a[right] == a[right + 1]) right--;
            }

        }
        return triples;
    }

    private final int[] a;
    private final int length;
}