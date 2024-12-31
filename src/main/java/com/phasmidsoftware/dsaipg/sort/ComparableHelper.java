/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import java.util.Comparator;

/**
 * Helper interface which extends NonComparableHelper.
 * <p>
 * A Helper provides all the utilities that are needed by sort methods, for example, compare and swap.
 * CONSIDER renaming this interface as ComparableHelper and the NonComparableHelper as Helper.
 * <p>
 *
 * @param <X>
 */
public interface ComparableHelper<X extends Comparable<X>> extends NonComparableHelper<X> {

    /**
     * Compare value v with value w.
     * <p>
     * CONSIDER using the commented-out code in order that ALL Comparable comparisons go through pureComparison.
     *
     * @param v the first value.
     * @param w the second value.
     * @return -1 if v is less than w; 1 if v is greater than w; otherwise 0.
     */
    default int compare(X v, X w) {
//        return pureComparison(v, w);
        return v.compareTo(w);
    }

    default Comparator<X> getComparator() {
        return Comparable::compareTo;
    }

}