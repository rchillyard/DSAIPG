/*
 * Copyright (c) 2024. Robin Hillyard
 */

 package com.phasmidsoftware.dsaipg.sort.linearithmic;

 import com.phasmidsoftware.dsaipg.sort.Helper;
 import com.phasmidsoftware.dsaipg.sort.SortException;
 import com.phasmidsoftware.dsaipg.util.Config;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;
 
 /**
  * Class QuickSort_DualPivot which extends QuickSort.
  *
  * @param <X> the underlying comparable type.
  */
 public class QuickSort_DualPivot<X extends Comparable<X>> extends QuickSort<X> {
 
     public static final String DESCRIPTION = "QuickSort dual pivot";
 
     public QuickSort_DualPivot(String description, int N, int nRuns, Config config) {
         super(description, N, nRuns, config);
         setPartitioner(createPartitioner());
     }
 
     /**
      * Constructor for QuickSort_DualPivot
      *
      * @param helper an explicit instance of Helper to be used.
      */
     public QuickSort_DualPivot(Helper<X> helper) {
         super(helper);
         setPartitioner(createPartitioner());
     }
 
     /**
      * Constructor for QuickSort_DualPivot
      *
      * @param N      the number elements we expect to sort.
      * @param nRuns  the number of runs.
      * @param config the configuration.
      */
     public QuickSort_DualPivot(int N, int nRuns, Config config) {
         this(DESCRIPTION, N, nRuns, config);
     }
 
     /**
      * Constructor for QuickSort_DualPivot
      *
      * @param N      the number elements we expect to sort.
      * @param config the configuration.
      */
     public QuickSort_DualPivot(int N, Config config) {
         this(DESCRIPTION, N, getRunsConfig(config), config);
     }
 
     public Partitioner<X> createPartitioner() {
         return new Partitioner_DualPivot(getHelper());
     }
 
     public class Partitioner_DualPivot implements Partitioner<X> {
 
         public Partitioner_DualPivot(Helper<X> helper) {
             this.helper = helper;
         }
 
         /**
          * Method to partition the given partition into smaller partitions.
          *
          * @param partition the partition to divide up.
          * @return a list of partitions, whose length depends on the sorting method being used.
          */
         public List<Partition<X>> partition(Partition<X> partition) {
             int n = partition.to - partition.from;
             if (n < 3) throw new SortException("cannot use DualPivot partitioning when size is less than 3");
             final X[] xs = partition.xs;
             final int p1 = partition.from;
             final int p2 = partition.to - 1;
             helper.swapConditional(xs, p1, p2);  // Ensure p1 and p2 are in correct order
             int lt = p1 + 1;
             int gt = p2 - 1;
             int i = lt;
             X v1 = xs[p1];
             X v2 = xs[p2];
 
             while (i <= gt) {
                 X x = xs[i];
                 if (x.compareTo(v1) < 0) {
                     swap(xs, lt++, i++);
                 } else if (x.compareTo(v2) > 0) {
                     swap(xs, i, gt--);
                 } else {
                     i++;
                 }
             }
 
             swap(xs, p1, --lt);
             swap(xs, p2, ++gt);
 
             List<Partition<X>> partitions = new ArrayList<>();
             partitions.add(new Partition<>(xs, p1, lt));
             partitions.add(new Partition<>(xs, lt + 1, gt));
             partitions.add(new Partition<>(xs, gt + 1, p2 + 1));
             return partitions;
         }
 
         // Swap utility method
         private void swap(X[] ys, int i, int j) {
             X temp = ys[i];
             ys[i] = ys[j];
             ys[j] = temp;
         }
 
         private final Helper<X> helper;
     }
 }
 