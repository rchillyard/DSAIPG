/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import java.util.Comparator;

/**
 * Basic Helper interface for non-comparable X types.
 * CONSIDER pulling up all the methods into Helper.
 * <p>
 * A Helper provides all of the utilities that are needed by sort methods, for example, compare and swap.
 * <p>
 * CONSIDER having the concept of a current sub-array, then we could dispense with the lo, hi parameters.
 *
 * @param <X>
 */
public interface NonComparableHelper<X> extends Helper<X> {

    /**
     * Initialize this Helper with the size of the array to be managed.
     *
     * @param n the size to be managed.
     */
    void init(int n);

    /**
     * Method to perform a stable swap using half-exchanges,
     * i.e. between xs[i] and xs[j] such that xs[j] is moved to index i,
     * and xs[i] through xs[j-1] are all moved up one.
     * This type of swap is used by insertion sort.
     *
     * @param xs the array of Xs.
     * @param i  the index of the destination of xs[j].
     * @param j  the index of the right-most element to be involved in the swap.
     */
    default void swapInto(X[] xs, int i, int j) {
        if (j > i) {
            X x = xs[j];
            copyBlock(xs, i, xs, i + 1, j - i);
            xs[i] = x;
        }
    }

    default String showFixes(X[] xs) {
        return "";
    }

    Comparator<String> CASE_INDEPENDENT = String.CASE_INSENSITIVE_ORDER;

}