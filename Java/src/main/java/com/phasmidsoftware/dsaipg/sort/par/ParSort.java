package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * ParSort is a class implementing a parallel sorting algorithm.
 * The sorting is executed using a fork-and-join approach,
 * where large arrays are divided into smaller portions and sorted concurrently.
 * Designed to optimize performance for sorting large integer arrays.
 * This code has been fleshed out by...
 * @author Ziyao Qiao. Thanks very much.
 */
final class ParSort {

    /**
     * Specifies the cutoff value used to determine when to switch from parallel sorting
     * to single-threaded sorting. If the size of the range to be sorted is smaller than
     * this value, {@link Arrays#sort} is used for single-threaded sorting. Otherwise,
     * the range is divided into smaller subarrays, which are sorted in parallel.
     * A larger cutoff value reduces the overhead of thread management but may limit
     * the advantages of parallelism.
     */
    public static int cutoff = 1000;
    public static int maxDepth = 0;

    /**
     * Sorts the specified portion of the input array using a parallel sorting algorithm.
     * If the range to be sorted is smaller than a predefined cutoff value, the method
     * utilizes a single-threaded sorting based on {@link Arrays#sort}. For larger ranges,
     * the array is divided into subarrays which are recursively sorted concurrently,
     * and the results are merged into a single sorted array.
     *
     * @param array the array to be sorted
     * @param from  the starting index (inclusive) of the portion of the array to be sorted
     * @param to    the ending index (exclusive) of the portion of the array to be sorted
     */
    public static void sort(int[] array, int from, int to) {
        int mid = (from + to) / 2;
        if (to - from >= cutoff) {
            CompletableFuture<int[]> completableFuture1 = asyncSort(array, from, mid, 0);
            CompletableFuture<int[]> completableFuture2 = asyncSort(array, mid, to, 0);


            CompletableFuture<int[]> completableFuture = completableFuture1.thenCombine(completableFuture2, ParSort::doMerge);
            completableFuture.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
            completableFuture.join();
        } else
            Arrays.sort(array, from, to);
    }

    /**
     * Recursively sorts a specified portion of the input array and returns a new sorted array.
     * This method extracts the specified range, sorts it using a defined sorting mechanism,
     * and provides the sorted result as a new array, leaving the input array unchanged.
     *
     * @param array the input array from which a portion will be sorted
     * @param from  the starting index (inclusive) of the portion of the array to be sorted
     * @param to    the ending index (exclusive) of the portion of the array to be sorted
     * @return a new sorted array containing the elements from the specified range of the input array
     */
    static int[] sortRecursive(int[] array, int from, int to, int depth) {
        int[] result = new int[to - from];
        int size = to - from;

        // if size of array < cutoff or recursion depth > this.depth
        if (size < cutoff || depth >= maxDepth){
            result = Arrays.copyOfRange(array, from, to);
            Arrays.sort(array, from, to); // using a defined sorting mechanism
            return result;
        }
        else{
            int mid = (from + to) / 2;
            CompletableFuture<int[]> left = CompletableFuture.supplyAsync(() ->
                    sortRecursive(array, from, mid, depth+1));

            CompletableFuture<int[]> right = CompletableFuture.supplyAsync(() ->
                    sortRecursive(array, mid, to, depth+1));

            return left.thenCombine(right, ParSort::doMerge).join();

        }
    }

    /**
     * Merges two sorted arrays into a single sorted array.
     * The method assumes that both input arrays are already sorted in ascending order,
     * and combines them into a new sorted array.
     *
     * @param xs1 the first sorted input array
     * @param xs2 the second sorted input array
     * @return a new sorted array containing all elements from both input arrays
     */
    static int[] doMerge(int[] xs1, int[] xs2) {
        int[] result = new int[xs1.length + xs2.length];
        int i = 0;
        int j = 0;
        for (int k = 0; k < result.length; k++) {
            if (i >= xs1.length) result[k] = xs2[j++];
            else if (j >= xs2.length) result[k] = xs1[i++];
            else if (xs2[j] < xs1[i]) result[k] = xs2[j++];
            else result[k] = xs1[i++];
        }
        return result;
    }

    /**
     * Asynchronously sorts the specified portion of the input array using a parallel sorting algorithm.
     * This method extracts a subsection of the given array, sorts it, and returns a CompletableFuture
     * containing the sorted portion of the array.
     *
     * @param array the input array to extract and sort
     * @param from  the starting index (inclusive) of the portion of the array to be sorted
     * @param to    the ending index (exclusive) of the portion of the array to be sorted
     * @return a CompletableFuture containing the sorted section of the array
     */
    static CompletableFuture<int[]> asyncSort(int[] array, int from, int to, int depth) {
        return CompletableFuture.supplyAsync(
                () -> sortRecursive(array, from, to, depth)
        );
    }
}