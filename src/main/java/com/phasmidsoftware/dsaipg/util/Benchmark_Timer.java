/*
 * Copyright (c) 2018-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.*;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue.FourAryHeap;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue.FibonacciHeap;

import static com.phasmidsoftware.dsaipg.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least one and at most the lower of four or m/15.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(1, Integer.min(3, m / 15));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        // Warmup phase
        new Timer().repeat(getWarmupRuns(m), true, supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, false, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with the option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with the option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static void main(String[] args) {
        int[] M_values = {4095, 8191, 16383, 32767, 65535, 131071}; 
        Random random = new Random();
    
        for (int M : M_values) {
            int numInsertions = (M / 4095) * 16000;
            int numDeletions = (M / 4095) * 4000;
    
            System.out.println("\n==== Running Benchmark for M = " + M + " ====\n");
            System.out.println("Insertions: " + numInsertions + ", Deletions: " + numDeletions);

             // Binary Heap 
            PriorityQueue<Integer> binaryHeap = new PriorityQueue<>(M, Comparator.naturalOrder());
            Benchmark_Timer<PriorityQueue<Integer>> binaryHeapTimer = new Benchmark_Timer<>(
                    "Binary Heap Benchmark",
                    heap -> performHeapOperations(heap, numInsertions, numDeletions, random)
            );
            double binaryHeapTime = binaryHeapTimer.run(binaryHeap, 10);
            System.out.println("Binary Heap Execution Time: " + binaryHeapTime + " ms");
            System.out.println("\n");

            PriorityQueue<Integer> floydBinaryHeap = new PriorityQueue<>(M, Comparator.naturalOrder());
            Benchmark_Timer<PriorityQueue<Integer>> floydBinaryHeapTimer = new Benchmark_Timer<>(
                    "Binary Heap + Floyd Trick Benchmark",
                    heap -> performHeapOperationsWithFloyd(heap, numInsertions, numDeletions, random)
            );
            double floydBinaryHeapTime = floydBinaryHeapTimer.run(floydBinaryHeap, 10);
            System.out.println("Binary Heap + Floyd Trick Execution Time: " + floydBinaryHeapTime + " ms");
            System.out.println("\n");

            // 4-ary Heap
            FourAryHeap<Integer> fourAryHeap = new FourAryHeap<>();
            Benchmark_Timer<FourAryHeap<Integer>> fourAryHeapTimer = new Benchmark_Timer<>(
                    "4-ary Heap Benchmark",
                    heap -> performHeapOperations(heap, numInsertions, numDeletions, random)
            );
            double fourAryHeapTime = fourAryHeapTimer.run(fourAryHeap, 10);
            System.out.println("4-ary Heap Execution Time: " + fourAryHeapTime + " ms");
            System.out.println("\n");

            FourAryHeap<Integer> floydFourAryHeap = new FourAryHeap<>();
            Benchmark_Timer<FourAryHeap<Integer>> floydFourAryHeapTimer = new Benchmark_Timer<>(
                    "4-ary Heap + Floyd Trick Benchmark",
                    heap -> performHeapOperationsWithFloyd(heap, numInsertions, numDeletions, random)
            );
            double floydFourAryHeapTime = floydFourAryHeapTimer.run(floydFourAryHeap, 10);
            System.out.println("4-ary Heap + Floyd Trick Execution Time: " + floydFourAryHeapTime + " ms");
            System.out.println("\n");

            // Fibonacci Heap
            FibonacciHeap<Integer> fibonacciHeap = new FibonacciHeap<>();
            Benchmark_Timer<FibonacciHeap<Integer>> fibonacciHeapTimer = new Benchmark_Timer<>(
                    "Fibonacci Heap Benchmark",
                    heap -> performHeapOperations(heap, numInsertions, numDeletions, random)
            );
            double fibonacciHeapTime = fibonacciHeapTimer.run(fibonacciHeap, 10);
            System.out.println("Fibonacci Heap Execution Time: " + fibonacciHeapTime + " ms");
            System.out.println("\n");
        }
    }

    private static void performHeapOperations(PriorityQueue<Integer> heap, int numInsertions, int numDeletions, Random random) {
        Integer highestSpilled = null; 
        for (int i = 0; i < Math.max(numInsertions, numDeletions); i++) {
            if (i < numInsertions) {
                int value = random.nextInt(1000000);
                heap.add(value);
                if (heap.size() > 4095) { 
                    int removed = heap.poll();
                    if (highestSpilled == null || removed > highestSpilled) {
                        highestSpilled = removed;
                    }
                }
            }
            if (i < numDeletions && !heap.isEmpty()) {
                heap.poll();
            }
        }
        System.out.println("Highest priority spilled element: " + highestSpilled);
    }
    

    private static void performHeapOperations(FourAryHeap<Integer> heap, int numInsertions, int numDeletions, Random random) {
        Integer highestSpilled = null;
        for (int i = 0; i < Math.max(numInsertions, numDeletions); i++) {
            if (i < numInsertions) {
                int value = random.nextInt(1000000);
                heap.insert(value);
    
                if (heap.getSize() > 4095) {
                    int removed = heap.removeMin();
                    if (highestSpilled == null || removed > highestSpilled) {
                        highestSpilled = removed;
                    }
                }
            }
            if (i < numDeletions && !heap.isEmpty()) {
                heap.removeMin();
            }
        }
        System.out.println("Highest priority spilled element: " + highestSpilled);
    }
    

    private static void performHeapOperations(FibonacciHeap<Integer> heap, int numInsertions, int numDeletions, Random random) {
        Integer highestSpilled = null;
        for (int i = 0; i < Math.max(numInsertions, numDeletions); i++) {
            if (i < numInsertions) {
                int value = random.nextInt(1000000);
                heap.insert(value);
                if (heap.size > 4095) {
                    int removed = heap.removeMin();
                    if (highestSpilled == null || removed > highestSpilled) {
                        highestSpilled = removed;
                    }
                }
            }
            if (i < numDeletions && !heap.isEmpty()) {
                heap.removeMin();
            }
        }
        System.out.println("Highest priority spilled element: " + highestSpilled);
    }
    
    
    private static void performHeapOperationsWithFloyd(PriorityQueue<Integer> heap, int numInsertions, int numDeletions, Random random) {
        List<Integer> elements = new ArrayList<>();
        Integer highestSpilled = null;
    
        // 1️⃣ 生成随机数据
        for (int i = 0; i < numInsertions; i++) {
            elements.add(random.nextInt(1000000));
        }
    
        // 2️⃣ 直接用 Floyd’s Trick 一次性建堆
        heap = new PriorityQueue<>(elements.size(), Comparator.naturalOrder()); 
        heap.addAll(elements);  // 一次性添加所有元素，构造堆
    
        // 3️⃣ 移除超出 M 的元素，记录最高优先级溢出元素
        while (heap.size() > 4095) {
            int removed = heap.poll();
            if (highestSpilled == null || removed > highestSpilled) {
                highestSpilled = removed;
            }
        }
    
        // 4️⃣ 执行删除操作
        for (int i = 0; i < numDeletions && !heap.isEmpty(); i++) {
            heap.poll();
        }
    
        System.out.println("Highest priority spilled element: " + highestSpilled);
    }
    
    

    private static void performHeapOperationsWithFloyd(FourAryHeap<Integer> heap, int numInsertions, int numDeletions, Random random) {
        List<Integer> elements = new ArrayList<>();
        Integer highestSpilled = null;
        for (int i = 0; i < numInsertions; i++) {
            elements.add(random.nextInt(1000000));
        }
    
        heap = new FourAryHeap<>();
        for (int e : elements) {
            heap.insert(e);
        }
    
        for (int i = (heap.getSize() / 4); i >= 0; i--) {
            heap.heapifyDown(i);
        }
    
        while (heap.getSize() > 4095) {
            int removed = heap.removeMin();  
            if (highestSpilled == null || removed > highestSpilled) {
                highestSpilled = removed;
            }
        }
    
        for (int i = 0; i < numDeletions && !heap.isEmpty(); i++) {
            heap.removeMin();
        }
        System.out.println("Highest priority spilled element: " + highestSpilled);
    }
    
      
}