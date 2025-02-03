package com.phasmidsoftware.dsaipg.adt.threesum;

import com.phasmidsoftware.dsaipg.util.Stopwatch;
import java.util.Arrays;
import java.util.Random;

/**
 * Benchmarking class to compare execution times of different ThreeSum implementations.
 */
public class ThreeSumTimings {
    public static void main(String[] args) {
        int size = 8000; // Adjust the size for better performance comparison
        int[] inputArray = generateSortedRandomArray(size, -500, 500);

        System.out.println("Benchmarking ThreeSum Implementations...");

        benchmark(new ThreeSumCubic(inputArray), "Cubic (O(N^3))");
        benchmark(new ThreeSumQuadratic(inputArray), "Quadratic (O(N^2))");
        benchmark(new ThreeSumQuadrithmic(inputArray), "Quadrithmic (O(N^2 log N))");
    }

    /**
     * Runs a ThreeSum implementation and measures execution time.
     *
     * @param threeSum  The ThreeSum implementation to test.
     * @param methodName The name of the method for display.
     */
    private static void benchmark(ThreeSum threeSum, String methodName) {
        try (Stopwatch stopwatch = new Stopwatch()) {
            threeSum.getTriples();
            long elapsed = stopwatch.lap();
            System.out.println(methodName + " execution time: " + elapsed + " ms");
        }
    }

    /**
     * Generates a sorted array of random integers within a given range.
     *
     * @param size The size of the array.
     * @param min  The minimum possible value.
     * @param max  The maximum possible value.
     * @return A sorted integer array.
     */
    private static int[] generateSortedRandomArray(int size, int min, int max) {
        Random rand = new Random();
        int[] array = rand.ints(size, min, max).toArray();
        Arrays.sort(array); // Sorting ensures compatibility with Quadrithmic
        return array;
    }
}
