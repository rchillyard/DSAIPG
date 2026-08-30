package com.phasmidsoftware.dsaipg.sort.linearithmic;

import java.util.List;

/**
 * The Partitioner interface defines the contract for different partitioning strategies
 * that can be used during the sorting process, such as in Quicksort implementations.
 *
 * @param <X> the type of elements in the partition, which must be Comparable.
 */
public interface Partitioner<X extends Comparable<X>> {

    /**
     * Method to partition the given partition into smaller partitions.
     *
     * @param partition the partition to divide up.
     * @return a list of partitions, whose length depends on the sorting method being used.
     */
    List<Partition<X>> partition(Partition<X> partition);
}
