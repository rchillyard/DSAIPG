package com.phasmidsoftware.dsaipg.sort.helper;

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
     * Provides a string representation of fixes or adjustments for the given array of X objects.
     *
     * @param xs the array of objects of type X for which fixes or adjustments are to be shown
     * @return a string representation of the fixes or adjustments
     */
    default String showFixes(X[] xs) {
        return "";
    }

    Comparator<String> CASE_INDEPENDENT = String.CASE_INSENSITIVE_ORDER;

}
