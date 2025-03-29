
// package com.phasmidsoftware.dsaipg.util.benchmark;
// import java.util.Random;

// import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
// import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_DualPivot; 
// import com.phasmidsoftware.dsaipg.sort.elementary.HeapSort;
// import com.phasmidsoftware.dsaipg.util.instrument.InstrumentedComparableHelper;
// import com.phasmidsoftware.dsaipg.util.benchmark.Timer;



// public class SortBenchmarkRunner {

//     public static void main(String[] args) {
//         // Parse command-line sizes; e.g., "10000 20000 40000 80000 160000 256000"
//         int[] sizes = new int[args.length];
//         for (int i = 0; i < args.length; i++) {
//             sizes[i] = Integer.parseInt(args[i]);
//         }

//         // Print CSV header
//         System.out.println("Algorithm,Array Size,Comparisons,Swaps/Copies,Hits,Memory,ExecutionTime(ms)");

//         // Run benchmarks for each algorithm and each array size
//         for (int size : sizes) {
//             // Generate a random array for the given size
//             Integer[] originalArray = generateRandomArray(size);

//             // MERGE SORT
//             runBenchmark("MergeSort", size, originalArray, new Sorter<Integer>() {
//                 @Override
//                 public void sort(Integer[] array, InstrumentedComparableHelper<Integer> helper) {
//                     MergeSort<Integer> sorter = new MergeSort<>(helper);
//                     sorter.sort(array);
//                 }
//             });

//             // DUAL-PIVOT QUICK SORT
//             runBenchmark("QuickSortDualPivot", size, originalArray, new Sorter<Integer>() {
//                 @Override
//                 public void sort(Integer[] array, InstrumentedComparableHelper<Integer> helper) {
//                     QuickSortDualPivot<Integer> sorter = new QuickSortDualPivot<>(helper);
//                     sorter.sort(array);
//                 }
//             });

//             // HEAP SORT
//             runBenchmark("HeapSort", size, originalArray, new Sorter<Integer>() {
//                 @Override
//                 public void sort(Integer[] array, InstrumentedComparableHelper<Integer> helper) {
//                     HeapSort<Integer> sorter = new HeapSort<>(helper);
//                     sorter.sort(array);
//                 }
//             });
//         }
//     }

//     // Interface to allow passing different sorters easily
//     interface Sorter<T> {
//         void sort(T[] array, InstrumentedComparableHelper<T> helper);
//     }

//     // Runs both an instrumented run and a timing run, then prints results in CSV format.
//     private static void runBenchmark(String algorithmName, int size, Integer[] originalArray, Sorter<Integer> sorter) {
//         // Instrumented run to count operations
//         InstrumentedComparableHelper<Integer> helper = new InstrumentedComparableHelper<>();
//         Integer[] arrayForInstrument = originalArray.clone();
//         sorter.sort(arrayForInstrument, helper);
//         long comparisons = helper.getComparisons();
//         long swaps = helper.getSwaps();
//         long hits = helper.getHits();
//         long memoryUsed = helper.getMemoryUsed();  // if supported by your helper

//         // Timing run (non-instrumented)
//         Timer timer = new Timer();
//         Integer[] arrayForTiming = originalArray.clone();
//         timer.start();
//         // For timing, you may disable instrumentation via configuration
//         sorter.sort(arrayForTiming, new InstrumentedComparableHelper<>()); 
//         timer.stop();
//         long execTime = timer.getElapsedTime();

//         // Print results in CSV format
//         System.out.printf("%s,%d,%d,%d,%d,%d,%d%n", algorithmName, size, comparisons, swaps, hits, memoryUsed, execTime);
//     }

//     // Generates an array of random integers
//     private static Integer[] generateRandomArray(int size) {
//         Integer[] array = new Integer[size];
//         Random rnd = new Random(0); // seed for reproducibility
//         for (int i = 0; i < size; i++) {
//             array[i] = rnd.nextInt();
//         }
//         return array;
//     }
// }
