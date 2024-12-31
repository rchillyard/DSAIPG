/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.select;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.Partition;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_Basic;

import java.util.List;
import java.util.Random;

import static com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort.createPartition;

/**
 * The QuickSelect class implements the QuickSelect algorithm to find the k-th smallest element
 * in an array. It uses a randomized approach based on quicksort partitioning to efficiently
 * locate the desired element within the specified array.
 *
 * @param <X> the type of elements in the array, which must implement the Comparable interface.
 */
public class QuickSelect<X extends Comparable<X>> implements Select<X> {

    /**
     * Selects the k-th smallest element from the given array.
     * This method rearranges the elements in the array using a randomized selection algorithm
     * to find the k-th smallest element.
     * The input array may be modified as part of the process.
     *
     * @param xs the input array of elements to search within. All elements must implement the Comparable interface.
     * @param k the index (0-based) of the smallest element to find; must be between 0 and a.length - 1.
     * @return the k-th smallest element in the input array.
     * @throws IllegalArgumentException if the value of k is out of the valid range (0 to a.length - 1).
     */
    public X select(X[] xs, int k) {
        if (k < 0 || k >= xs.length) throw new IllegalArgumentException("k must be between 0 and " + (xs.length - 1));
        shuffle(xs);
        int from = 0, to = xs.length;
        while (to > from + 1) {
            int[] partitionList = partition(createPartition(xs, from, to));
            int lt = partitionList[0];
            int gt = partitionList[1];
            if (k < lt) to = lt;
            else if (k >= gt) from = gt;
            else return xs[lt];
        }
        return xs[k];
    }

    /**
     * Constructs a QuickSelect instance.
     * This constructor initializes the QuickSelect object with a partitioner
     * for performing QuickSort-based partitioning operations. The partitioner
     * leverages a helper designed for non-instrumenting operation with comparable elements.
     */
    public QuickSelect() {
        Helper<X> helper = NonInstrumentingComparableHelper.getHelper(QuickSelect.class);
        this.partitioner = new QuickSort_Basic.Partitioner_Basic<>(helper);
    }

    /**
     * Package-private method to partition the given partition into smaller partitions.
     *
     * @param partition the partition to divide up.
     * @return an array of integers lt and gt.
     */
    int[] partition(Partition<X> partition) {
        List<Partition<X>> partitionList = partitioner.partition(partition);
        return new int[]{partitionList.get(0).to, partitionList.get(1).from};
    }

    /***
     * Knuth shuffle
     * In iteration i, pick integer r between zero and i uniformly at random
     * Swap a[i] and a[r].
     *
     * @param a the array
     */
    private static void shuffle(Object[] a) {
        Random random = new Random();
        for (int i = 0; i < a.length; i++) swap(a, i, random.nextInt(i + 1));
    }

    /**
     * exchange a[i] and a[r]
     *
     * @param a the array.
     * @param i one index.
     * @param r other index.
     */
    private static void swap(Object[] a, int i, int r) {
        Object temp = a[i];
        a[i] = a[r];
        a[r] = temp;
    }

    private final QuickSort_Basic.Partitioner_Basic<X> partitioner;
}