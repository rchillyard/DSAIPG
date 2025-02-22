package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

public class HeapBenchmark {

    /**
     * Runs the benchmark for a given heap instance, number of insertions, and removals.
     *
     * @param heap      the heap instance
     * @param heapName  the name of the heap (for reporting)
     * @param numInsert number of insertions to perform
     * @param numRemove number of removals to perform
     */
    public static void benchmarkHeap(PriorityQueueInterface<Integer> heap, String heapName, int numInsert, int numRemove) {
        List<Integer> spilled = new ArrayList<>();
        Random rand = new Random();

        // Benchmark insertion
        Benchmark_Timer<List<Integer>> insertTimer = new Benchmark_Timer<>(
            heapName + " - Insertion",
            (List<Integer> list) -> {
                for (int i = 0; i < numInsert; i++) {
                    heap.give(rand.nextInt());
                }
            },
            null  // no post-function
        );
        double insertTime = insertTimer.runFromSupplier(() -> spilled, 1);
        System.out.println(heapName + " (n=" + numInsert + ") Insertion time: " + insertTime + " seconds");

        // Benchmark removal
        Benchmark_Timer<List<Integer>> removeTimer = new Benchmark_Timer<>(
            heapName + " - Removal",
            (List<Integer> list) -> {
                for (int i = 0; i < numRemove; i++) {
                    if (heap.size() == 0) {  // Stop if heap is empty.
                        break;
                    }
                    try {
                        if (heap.size() > 4095) {
                            spilled.add(heap.take());
                        } else {
                            heap.take();
                        }
                    } catch (PQException e) {
                        e.printStackTrace();
                    }
                }
            },
            null
        );
        double removeTime = removeTimer.runFromSupplier(() -> spilled, 1);
        System.out.println(heapName + " (n=" + numInsert + ") Removal time: " + removeTime + " seconds");

        int bestSpilled = spilled.stream().min(Integer::compareTo).orElse(Integer.MAX_VALUE);
        System.out.println(heapName + " (n=" + numInsert + ") Best spilled element: " + bestSpilled);
        System.out.println();
    }

    public static void main(String[] args) {
        // Define different input sizes (number of insertions) for the benchmark.
        int[] inputSizes = {16000, 32000, 48000, 64000};
        // We'll use the same proportion for removals; for example, always remove 4000.
        // (Alternatively, you could scale removals proportionally.)
        int numRemove = 4000;

        // Fixed capacity for each heap (as required, maintain up to M = 4095 elements).
        int capacity = 5000;
        boolean max = true; // true for a max-heap; if using a min-heap, adjust comparator accordingly.
        Comparator<Integer> comparator = Integer::compareTo;

        // For each input size, create new heap instances and benchmark them.
        for (int size : inputSizes) {
            System.out.println("=== Benchmarking for input size: " + size + " ===");

            // Create new instances for each benchmark run.
            PriorityQueueInterface<Integer> basicBinaryHeap = new PriorityQueue<>(capacity, max, comparator, false);
            PriorityQueueInterface<Integer> binaryHeapFloyd = new PriorityQueue<>(capacity, max, comparator, true);
            PriorityQueueInterface<Integer> basic4aryHeap = new FourAryHeap<>(capacity, max, comparator, false);
            PriorityQueueInterface<Integer> fouraryFloydHeap = new FourAryHeap<>(capacity, max, comparator, true);
            PriorityQueueInterface<Integer> fibonacciHeap = new FibonacciHeap<>(capacity, max, comparator);

            // Run the benchmark for each implementation.
            benchmarkHeap(basicBinaryHeap, "Basic Binary Heap", size, numRemove);
            benchmarkHeap(binaryHeapFloyd, "Binary Heap with Floyd's Trick", size, numRemove);
            benchmarkHeap(basic4aryHeap, "Basic 4-ary Heap", size, numRemove);
            benchmarkHeap(fouraryFloydHeap, "4-ary Heap with Floyd's Trick", size, numRemove);
            benchmarkHeap(fibonacciHeap, "Fibonacci Heap", size, numRemove);
        }
    }
}
