package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.Timer;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SortingBenchmark {
    private static final int[] SIZES = {100, 200, 400, 800, 1600};

    public static void main(String[] args) {
        for (int size : SIZES) {
            Integer[] randomArray = generateRandomArray(size);
            Integer[] sortedArray = generateSortedArray(size);
            Integer[] partiallySortedArray = generatePartiallySortedArray(size);
            Integer[] reverseSortedArray = generateReverseSortedArray(size);

            benchmarkSorting("Random Array", randomArray);
            benchmarkSorting("Sorted Array", sortedArray);
            benchmarkSorting("Partially Sorted Array", partiallySortedArray);
            benchmarkSorting("Reverse Sorted Array", reverseSortedArray);
        }
    }

    private static void benchmarkSorting(String label, Integer[] array) {
        Supplier<Integer[]> supplier = () -> Arrays.copyOf(array, array.length);
        Consumer<Integer[]> sorter = SortingBenchmark::insertionSort;

        Benchmark_Timer<Integer[]> benchmark = new Benchmark_Timer<>(label, sorter);
        double time = benchmark.runFromSupplier(supplier, 10);

        System.out.printf("%s (size %d): %.2f ns%n", label, array.length, time);
    }

    private static void insertionSort(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    private static Integer[] generateRandomArray(int size) {
        Random rand = new Random();
        return rand.ints(size, 0, 10000).boxed().toArray(Integer[]::new);
    }

    private static Integer[] generateSortedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) array[i] = i;
        return array;
    }

    private static Integer[] generatePartiallySortedArray(int size) {
        Integer[] array = generateSortedArray(size);
        Random rand = new Random();
        for (int i = 0; i < size / 10; i++) {
            int idx1 = rand.nextInt(size);
            int idx2 = rand.nextInt(size);
            int temp = array[idx1];
            array[idx1] = array[idx2];
            array[idx2] = temp;
        }
        return array;
    }

    private static Integer[] generateReverseSortedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) array[i] = size - i;
        return array;
    }
}

