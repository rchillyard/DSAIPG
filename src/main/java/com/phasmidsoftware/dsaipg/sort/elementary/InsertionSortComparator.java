/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;
import com.phasmidsoftware.dsaipg.util.Timer;

import java.io.IOException;
import java.util.Comparator;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;

/**
 * A class for performing insertion sort using a comparator, extending functionality from SortWithHelper.
 * This includes methods for initialization and invocation of insertion sort,
 * along with specific utilities like counting inversions.
 *
 * @param <X> the type of elements to be sorted, which can be compared using a provided comparator.
 */
public class InsertionSortComparator<X> extends SortWithHelper<X> {
    /**
     * Constructor for InsertionSortComparator, which initializes the comparator with the provided helper.
     *
     * @param helper the Helper object to be used for managing the sorting process.
     */
    public InsertionSortComparator(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for any subclasses to use.
     *
     * @param description the description.
     * @param comparator  the comparator to use.
     * @param N           the number of elements expected.
     * @param nRuns       the number of runs to be expected (this is only significant when instrumenting).
     * @param config      the configuration.
     */
    protected InsertionSortComparator(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        super(description, comparator, N, nRuns, config);
    }

    /**
     * Constructor for InsertionSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to be expected (this is only significant when instrumenting).
     * @param config the configuration.
     */
    public InsertionSortComparator(Comparator<X> comparator, int N, int nRuns, Config config) {
        this(DESCRIPTION, comparator, N, nRuns, config);
    }

    /**
     * Sort the sub-array xs:from:to using insertion sort.
     *
     * @param xs   sort the array xs from "from" to "to".
     * @param from the index of the first element to sort
     * @param to   the index of the first element not to sort
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        
    
        for (int i = from + 1; i < to; i++) {
            X current = xs[i];
            int j = i - 1;

        
            while (j >= from && helper.compare(xs[j], current) > 0) {
            xs[j + 1] = xs[j]; 
            j--; 
        }

      
        xs[j + 1] = current;
    }
    
        
        // TO BE IMPLEMENTED 
// throw new RuntimeException("implementation missing");
    }

    public static final String DESCRIPTION = "Insertion sort";

    /**
     * Sorts the given array in-place using the provided insertion sort comparator.
     *
     * @param <T> the generic type parameter that extends Comparable.
     * @param ts  the array of elements to be sorted, where elements must implement {@code Comparable}.
     *            The method modifies this array directly to produce the sorted order.
     * @throws RuntimeException if an IOException occurs during the sorting process.
     */
    public static <T extends Comparable<T>> void sort(T[] ts) {
        try (InsertionSortComparator<T> sort = new InsertionSortComparator<>(DESCRIPTION, Comparable::compareTo, ts.length, 1, Config.load(InsertionSortComparator.class))) {
            sort.mutatingSort(ts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a case-insensitive string sorter using an insertion sort comparator.
     *
     * @param n      the expected number of elements to be sorted.
     * @param config the configuration object containing necessary settings.
     * @return a {@code SortWithHelper<String>} instance configured for case-insensitive string sorting.
     */
    public static Sort<String> stringSorterCaseInsensitive(int n, Config config) {
        return new InsertionSortComparator<>(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, n, getRunsConfig(config), config);
    }

    /**
     * This method is designed to count inversions in quadratic time, using insertion sort.
     *
     * @param ts  an array of comparable T elements.
     * @param <T> the underlying type of the elements.
     * @return the number of inversions in ts, which remains unchanged.
     */
    public static <T> long countInversions(T[] ts, Comparator<T> comparator) {
        final Config config = Config_Benchmark.setupConfigFixes();
        try (InsertionSortComparator<T> sorter = new InsertionSortComparator<>(comparator, ts.length, getRunsConfig(config), config)) {
            Helper<T> helper = sorter.getHelper();
            sorter.sort(ts, true);
            return helper.getFixes();
        }
    }

    public static void main(String[] args) {

        int[] sizes = {100, 200, 400, 800, 1600}; 
        String[] arrayTypes = {"Random", "Ordered", "PartiallyOrdered", "ReverseOrdered"};
    
  
        for (int size : sizes) {
            System.out.println("Array size: " + size);
    
      
            for (String type : arrayTypes) {
                Integer[] array = generateArray(type, size); 
                System.out.println("Testing " + type + " array:");
    
            
                Timer timer = new Timer();
                double time = timer.repeat(10, () -> { 
               
                    Integer[] arrayToSort = generateArray(type, size); 
                    InsertionSortComparator.sort(arrayToSort);
                    return null; 
                });
                System.out.println(type + " array took " + time + " ms for " + size + " elements.");
            }
        }
    }
    
    

public static Integer[] generateRandomArray(int size) {
    Integer[] array = new Integer[size];
    for (int i = 0; i < size; i++) {
        array[i] = (int) (Math.random() * 1000); 
    }
    return array;
}


public static Integer[] generateOrderedArray(int size) {
    Integer[] array = new Integer[size];
    for (int i = 0; i < size; i++) {
        array[i] = i; 
    }
    return array;
}


public static Integer[] generatePartiallyOrderedArray(int size) {
    Integer[] array = generateRandomArray(size);
    for (int i = 0; i < size / 2; i++) {
        array[i] = i; 
    }
    return array;
}


public static Integer[] generateReverseOrderedArray(int size) {
    Integer[] array = new Integer[size];
    for (int i = 0; i < size; i++) {
        array[i] = size - i - 1; 
    }
    return array;
}


public static Integer[] generateArray(String type, int size) {
    switch (type) {
        case "Random":
            return generateRandomArray(size);
        case "Ordered":
            return generateOrderedArray(size);
        case "PartiallyOrdered":
            return generatePartiallyOrderedArray(size);
        case "ReverseOrdered":
            return generateReverseOrderedArray(size);
        default:
            throw new IllegalArgumentException("Unknown array type: " + type);
    }
}



}