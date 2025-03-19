package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import java.util.Random;
import java.util.Comparator;

public class HeapBenchmark {
    private static final int heap_size = 4095;
    private static final int insertions = 16000;
    private static final int removals = 4000;
    private static final Random random = new Random();

    // Track highest priority spilled element
    //private static Integer highestSpilled = null;

    public static void main(String[] args) {
        // Benchmark Basic Binary Heap
        benchmarkHeap("Binary Heap", false, false);

        // Benchmark Binary Heap with Floyd's trick
        benchmarkHeap("Binary Heap with Floyd's trick", false, true);

        // Benchmark 4-ary Heap
        benchmarkHeap("4-ary Heap", true, false);

        // Benchmark 4-ary Heap with Floyd's trick
        benchmarkHeap("4-ary Heap with Floyd's trick", true, true);
    }

    private static class BenchmarkStats {
        Integer highestSpilled = null;
        int spilledCount = 0;
    }

    private static void benchmarkHeap(String description, boolean fourAry, boolean floyd) {
        // Reset highest spilled for this test
//        highestSpilled = null;
//        int spilledCount = 0;

        // Create stats object
        BenchmarkStats stats = new BenchmarkStats();

        // Create PriorityQueue
        PriorityQueue<Integer> pq;
        if (fourAry) {
            if (floyd) {
                pq = new FourAryHeapFloyd<Integer>(heap_size, true, Comparator.naturalOrder());
            } else {
                pq = new FourAryHeap<Integer>(heap_size, true, Comparator.naturalOrder(), false);
            }
        } else {
            pq = new PriorityQueue<Integer>(heap_size, true, Comparator.naturalOrder(), floyd);
        }

        // Benchmark insertions
        Benchmark_Timer<PriorityQueue<Integer>> insertTimer = new Benchmark_Timer<>(
                description + " - Insertions",
                heap -> {
                    Integer element = random.nextInt(Integer.MAX_VALUE); // Larger range
                    if (heap.size() >= heap_size) {
                        stats.spilledCount++;
                        // Track highest spilled element
                        if (stats.highestSpilled == null || element > stats.highestSpilled) {
                            stats.highestSpilled = element;
                        }
                    } else {
                        heap.give(element);
                    }
                }
        );

        // Run insertion benchmark
        double insertTime = insertTimer.runFromSupplier(() -> pq, insertions);

        // Benchmark removals
        Benchmark_Timer<PriorityQueue<Integer>> removeTimer = new Benchmark_Timer<>(
                description + " - Removals",
                heap -> {
                    try {
                        heap.take();
                    } catch (PQException e) {
                        System.err.println("Error during removal: " + e.getMessage());
                    }
                }
        );

        // Run removal benchmark
        double removeTime = removeTimer.runFromSupplier(() -> pq, removals);

        // Print results
        System.out.println("\nResults for " + description);
        System.out.println("Average insertion time: " + insertTime + " ms");
        System.out.println("Average removal time: " + removeTime + " ms");
        System.out.println("Number of spilled elements: " + stats.spilledCount);
        System.out.println("Highest spilled element: " + stats.highestSpilled);
        System.out.println("Final heap size: " + pq.size());
    }
}