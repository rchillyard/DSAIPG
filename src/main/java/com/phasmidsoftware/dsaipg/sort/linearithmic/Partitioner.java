/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import java.util.List;

public interface Partitioner<X extends Comparable<X>> {

    /**
     * Method to partition the given partition into smaller partitions.
     *
     * @param partition the partition to divide up.
     * @return a list of partitions, whose length depends on the sorting method being used.
     */
    List<Partition<X>> partition(Partition<X> partition);
}