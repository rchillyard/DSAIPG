package com.phasmidsoftware.dsaipg.util;

import java.util.Arrays;
import java.util.Random;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;

public class BenchmarkInsertionSort {

    public static void main(String[] args) {
        int initialSize = 250;
        int numberOfSizes = 5;
        int runs = 10;

        for (int k = 0; k < numberOfSizes; k++) {
            int n = initialSize * (1 << k);
            System.out.println("Array size n = " + n);

            Integer[] baseRandom = generateRandomArray(n);
            Integer[] baseOrdered = generateOrderedArray(n);
            Integer[] basePartial = generatePartialOrderedArray(n);
            Integer[] baseReverse = generateReverseArray(n);

            Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>("InsertionSort", array -> {
                InsertionSortComparator.sort(array);
            });

            double timeRandom = timer.runFromSupplier(() -> Arrays.copyOf(baseRandom, baseRandom.length), runs);
            double timeOrdered = timer.runFromSupplier(() -> Arrays.copyOf(baseOrdered, baseOrdered.length), runs);
            double timePartial = timer.runFromSupplier(() -> Arrays.copyOf(basePartial, basePartial.length), runs);
            double timeReverse = timer.runFromSupplier(() -> Arrays.copyOf(baseReverse, baseReverse.length), runs);

            System.out.printf("  Random: %.3f ms, Ordered: %.3f ms, Partial: %.3f ms, Reverse: %.3f ms%n",
                    timeRandom, timeOrdered, timePartial, timeReverse);
            System.out.println();
        }
    }

    private static Integer[] generateRandomArray(int n) {
        Integer[] arr = new Integer[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(n);
        }
        return arr;
    }

    private static Integer[] generateOrderedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }

    private static Integer[] generatePartialOrderedArray(int n) {
        Integer[] arr = generateOrderedArray(n);
        for (int i = 1; i < n; i += 2) {
            int temp = arr[i];
            arr[i] = arr[i - 1];
            arr[i - 1] = temp;
        }
        return arr;
    }

    private static Integer[] generateReverseArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        return arr;
    }
}