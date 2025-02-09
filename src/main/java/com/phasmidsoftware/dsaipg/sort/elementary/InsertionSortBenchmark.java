package com.phasmidsoftware.dsaipg.sort.elementary;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import java.util.Random;
import java.util.function.Supplier;

public class InsertionSortBenchmark {

    public static void main(String[] args) {
        System.out.println("Benchmarking InsertionSortComparator...");

        // Doubling method: Testing for n = 1K, 2K, 4K, 8K, 16K
        int[] sizes = {1000, 2000, 4000, 8000, 16000};

        for (int n : sizes) {
            System.out.println("\nSorting " + n + " elements:");

            // Randomly shuffled array
            benchmarkSort(n, "Random Order", () -> generateRandomArray(n));

            // Already sorted array
            benchmarkSort(n, "Ordered", () -> generateOrderedArray(n));

            // Partially sorted array (50% sorted, 50% random)
            benchmarkSort(n, "Partially Ordered", () -> generatePartiallyOrderedArray(n));

            // Reverse ordered array
            benchmarkSort(n, "Reverse Ordered", () -> generateReverseOrderedArray(n));
        }
    }

    /**
     * Runs a benchmark test for sorting an array of size `n` under a specific condition.
     *
     * @param n        Size of the array
     * @param label    Description of the order type
     * @param supplier Function that generates the array
     */
    private static void benchmarkSort(int n, String label, Supplier<Integer[]> supplier) {
        Benchmark_Timer<Integer[]> benchmark = new Benchmark_Timer<>(
                "InsertionSort - " + label,
                array -> InsertionSortComparator.sort(array)
        );

        Supplier<Integer[]> arraySupplier = () -> supplier.get();

        double avgTime = benchmark.runFromSupplier(arraySupplier, 10);

        System.out.printf("   %s: %.4f ms\n", label, avgTime);
    }
    /**
     * Generates an array of random integers.
     *
     * @param n size of the array
     * @return Randomized Integer array
     */
    private static Integer[] generateRandomArray(int n) {
        Integer[] array = new Integer[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(n);
        }
        return array;
    }

    /**
     * Generates an ordered array.
     *
     * @param n size of the array
     * @return Ordered Integer array
     */
    private static Integer[] generateOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = i;
        }
        return array;
    }

    /**
     * Generates a partially ordered array (first half sorted, second half random).
     *
     * @param n size of the array
     * @return Partially ordered Integer array
     */
    private static Integer[] generatePartiallyOrderedArray(int n) {
        Integer[] array = new Integer[n];
        Random random = new Random();
        for (int i = 0; i < n / 2; i++) {
            array[i] = i;  // Sorted half
        }
        for (int i = n / 2; i < n; i++) {
            array[i] = random.nextInt(n);  // Random half
        }
        return array;
    }

    /**
     * Generates a reverse ordered array.
     *
     * @param n size of the array
     * @return Reverse ordered Integer array
     */
    private static Integer[] generateReverseOrderedArray(int n) {
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = n - i;
        }
        return array;
    }
}