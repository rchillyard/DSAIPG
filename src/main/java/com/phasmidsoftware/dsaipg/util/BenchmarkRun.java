package com.phasmidsoftware.dsaipg.util;

import java.util.Arrays;
import java.util.Random;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;

public class BenchmarkRun {
    
    private static final int[] N_VALUES = {1000, 2000, 4000, 8000, 16000};
    private static final int RUNS = 100;
    
    public static void main(String[] args) {
        System.out.println("Benchmarking Sorting Algorithms with Different Array Orders\n");
        for (int n : N_VALUES) {
            Integer[] randomArray = generateRandomArray(n);
            Integer[] orderedArray = generateOrderedArray(n);
            Integer[] partiallyOrderedArray = generatePartiallyOrderedArray(n);
            Integer[] reverseOrderedArray = generateReverseOrderedArray(n);
            
            System.out.println("Array Size: " + n);
            benchmarkSort("Random Order", randomArray);
            benchmarkSort("Ordered", orderedArray);
            benchmarkSort("Partially Ordered", partiallyOrderedArray);
            benchmarkSort("Reverse Ordered", reverseOrderedArray);
            System.out.println("--------------------------------------------------------");
        }
    }
    
    private static void benchmarkSort(String type, Integer[] array) {
        long totalTime = 0;
    
        for (int i = 0; i < RUNS; i++) {
            InsertionSort<Integer> sorter = new InsertionSort<>();
            Integer[] copy = Arrays.copyOf(array, array.length); 
            long startTime = System.nanoTime();
            sorter.sort(copy);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        double avgTime = totalTime / (double) RUNS / 1_000_000; 
        System.out.printf("%s Sorting Time: %.3f ms\n", type, avgTime);
    }
    
    
    private static Integer[] generateRandomArray(int size) {
        Integer[] array = new Integer[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 10);
        }
        return array;
    }
    
    private static Integer[] generateOrderedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }
    
    private static Integer[] generatePartiallyOrderedArray(int size) {
        Integer[] array = generateOrderedArray(size);
        Random random = new Random();
        for (int i = size / 2; i < size; i++) { // Randomize half of the array
            array[i] = random.nextInt(size * 10);
        }
        return array;
    }
    
    private static Integer[] generateReverseOrderedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        return array;
    }
}
