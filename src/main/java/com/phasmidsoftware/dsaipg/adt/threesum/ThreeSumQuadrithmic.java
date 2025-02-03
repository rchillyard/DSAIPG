/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of ThreeSum which follows the simple optimization of
 * requiring a sorted array, then using binary search to find an element x where
 * -x the sum of a pair of elements.
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
//        this.setBorder();
    }

    /**
     * Retrieves an array of unique, sorted triples that represent combinations
     * of three integers in the underlying sorted array whose sum equals zero.
     * <p>
     * This method iterates over all pairs of elements in the array, uses a helper
     * method to find the third element that satisfies the condition, and returns
     * all such combinations as distinct {@code Triple} objects sorted in natural order.
     *
     * @return an array of unique {@code Triple} objects representing all valid combinations
     * of three integers in the array that sum to zero.
     */
    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++)
            for (int j = i + 1; j < length; j++) {
                Triple triple = getTriple(i, j);
                if (triple != null) triples.add(triple);
            }
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Finds a "triple" consisting of three integers from the sorted array such that their sum equals zero,
     * given two indices representing the first two elements of the triple.
     *
     * @param i the index of the first element in the triple.
     * @param j the index of the second element in the triple. Must satisfy j > i.
     * @return a {@code Triple} object representing the three integers if such a combination exists,
     * or {@code null} if no such triple can be found.
     */
    Triple getTriple(int i, int j) {
        // TO BE IMPLEMENTED  : use binary search to find the third element
        // END SOLUTION
        int sum = a[i] + a[j];
        int target = -sum;
        int low = 0;
        int high = length - 1;


        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (a[mid] == target) {
                if (mid == i || mid == j) {
                    return null;
                }
                return new Triple(a[i], a[j], a[mid]);
            } else if (a[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }


//        int sum = a[i] + a[j];
//        int target = -sum;
//        int low, high;
//
//        if (target >= 0) {
//            low = this.border;
//            high = length - 1;
//
//        } else {
//            low = 0;
//            high = this.border - 1;
//        }
//        // 修正循环条件为 low <= high
//        while (low <= high) {
//            int mid = low + (high - low) / 2;
//            if (a[mid] == target) {
//                // 检查 k 是否等于 i 或 j
//                if (mid == i || mid == j) {
//                    continue;
//                }
//                return new Triple(a[i], a[j], a[mid]);
//            } else if (a[mid] < target) {
//                low = mid + 1;
//            } else {
//                high = mid - 1;
//            }
//        }
        return null;

    }

//    private void setBorder() {
//        int border = this.findBorder(this.a);
//        System.out.println(border);
//        this.border = border;
//    }

//    private int findBorder(int[] nums) {
//        if (nums == null || nums.length == 0) return -1;
//        int firstZeroIndex = findFirstZero(nums);
//        if (firstZeroIndex != -1) {
//            return firstZeroIndex;
//        }
//
//        int firstPositiveIndex = findFirstPositive(nums);
//        return firstPositiveIndex;
//    }
//
//    private int findFirstZero(int[] nums) {
//        int left = 0;
//        int right = nums.length - 1;
//        int result = -1;
//        while (left <= right) {
//            int mid = left + (right - left) / 2;
//            if (nums[mid] == 0) {
//                result = mid;
//                right = mid - 1;
//            } else if (nums[mid] < 0) {
//                left = mid + 1;
//            } else {
//                right = mid - 1;
//            }
//        }
//        return result;
//    }
//
//
//    private int findFirstPositive(int[] nums) {
//        int left = 0;
//        int right = nums.length - 1;
//        int result = -1;
//        while (left <= right) {
//            int mid = left + (right - left) / 2;
//            if (nums[mid] > 0) {
//                result = mid;
//                right = mid - 1;
//            } else {
//                left = mid + 1;
//            }
//        }
//        return result;
//    }

//    public static void main(String args[]) {
//        int[] nums1 = {-15, -10, -8, -1, 0, 1, 2, 5, 10};
//        int[] nums2 = {-15, -10, -8, -2, -1, -1, 2, 5, 10};
//        ThreeSumQuadrithmic test = new ThreeSumQuadrithmic(nums2);
//
//    }

//    private int border;
    private final int[] a;
    private final int length;
}