/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;

/**
 * Class MergeSort.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the expected number of runs.
     * @param config the configuration.
     */
    public MergeSort(int N, int nRuns, Config config) {
        super(DESCRIPTION + getConfigString(config), N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("MergeSort: insertion sort"));
    }

    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        additionalMemory(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        additionalMemory(-xs.length);
        return result;
    }

    public void sort(X[] a, int from, int to) {
        Config config = helper.getConfig();
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        // CONSIDER don't copy but just allocate according to the xs/aux interchange optimization
        @SuppressWarnings("unchecked") X[] aux = noCopy ? helper.copyArray(a) : (X[]) new Comparable[a.length];
        sort(a, aux, from, to);
    }

    private void sort(X[] a, X[] aux, int from, int to) {
        /*Config config = helper.getConfig();
        boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        if (to <= from + helper.cutoff()) { // XXX check that a cutoff value of 1 effectively stops the cutoff mechanism.
            insertionSort.sort(a, from, to);
            return;
        }*/
        Config config = helper.getConfig();
        boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
    
        // 当子数组大小小于等于 cutoff 时，直接调用插入排序
        if (to - from <= helper.cutoff()) {
            insertionSort.sort(a, from, to);
            return;
        }
    
        // 递归分割数组
        int mid = from + (to - from) / 2;
        sort(a, aux, from, mid);
        sort(a, aux, mid, to);
    
        // 保险优化：如果左侧最后一个元素不大于右侧第一个元素，
        // 表示该区间已整体有序，若未启用无复制优化，则复制到辅助数组中
        if (insurance && !helper.less(a[mid], a[mid - 1])) {
            if (!noCopy) {
                for (int i = from; i < to; i++) {
                    aux[i] = a[i];
                }
            }
            return;
        }
    
        // 合并两个有序子数组，将结果存入辅助数组 aux 中
        merge(a, aux, from, mid, to);
    
        // 如果未启用无复制优化，则将辅助数组 aux 中的合并结果复制回原数组 a
        if (!noCopy) {
            for (int i = from; i < to; i++) {
                a[i] = aux[i];
            }
        }


    }

    // CONSIDER combine with MergeSortBasic, perhaps.
    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        int i = from;
        int j = mid;
        X v = helper.get(sorted, i);
        X w = helper.get(sorted, j);
        for (int k = from; k < to; k++) {
            if (i >= mid) {
                helper.copy(w, result, k);
                if (++j < to) w = helper.get(sorted, j);
            } else if (j >= to) {
                helper.copy(v, result, k);
                if (++i < mid) v = helper.get(sorted, i);
            } else if (helper.less(w, v)) {
                helper.incrementFixes(mid - i);
                helper.copy(w, result, k);
                if (++j < to) w = helper.get(sorted, j);
            } else {
                helper.copy(v, result, k);
                if (++i < mid) v = helper.get(sorted, i);
            }
        }
    }

    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        if (config.getBoolean(MERGESORT, NOCOPY)) stringBuilder.append(" with no copy");
        int cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
        if (cutoff != CUTOFF_DEFAULT) {
            if (cutoff == 1) stringBuilder.append(" with no cutoff");
            else stringBuilder.append(" with cutoff ").append(cutoff);
        }
        return stringBuilder.toString();
    }

    private final InsertionSort<X> insertionSort;


    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }

}
