/*
 * Copyright (c) 2018-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.phasmidsoftware.dsaipg.util.Utilities.formatWhole;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

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

    private static final Random random = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) {
        int initialSize = 128; // Starting size of the array
        Consumer<Integer[]> sortMethod = Benchmark_Timer::insertionSort; // Sorting method as a Consumer

        for (int i = 0; i < 5; i++) {
            int size = initialSize << i; // Double the array size in each iteration
            Integer[] randomArray = createRandomArray(size);
            Integer[] sortedArray = createSortedArray(size);
            Integer[] partiallyOrderedArray = createPartiallyOrderedArray(size);
            Integer[] reversedArray = createReversedArray(size);

            System.out.println("Array size: " + size);
            benchmark("Random Array", randomArray, sortMethod);
            benchmark("Sorted Array", sortedArray, sortMethod);
            benchmark("Partially Ordered Array", partiallyOrderedArray, sortMethod);
            benchmark("Reversed Array", reversedArray, sortMethod);
        }
    }

    private static void benchmark(String description, Integer[] array, Consumer<Integer[]> sortMethod) {
        Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>(description, null, sortMethod, null);
        double time = timer.runFromSupplier(() -> Arrays.copyOf(array, array.length), 10);
        System.out.println(description + ": " + time + " ms");
    }

    private static Integer[] createRandomArray(int size) {
        return random.ints(size, 0, 1000).boxed().toArray(Integer[]::new);
    }

    private static Integer[] createSortedArray(int size) {
        Integer[] array = createRandomArray(size);
        Arrays.sort(array);
        return array;
    }

    private static Integer[] createPartiallyOrderedArray(int size) {
        Integer[] array = createSortedArray(size);
        // Shuffle segments of the array to create partial order
        int segmentSize = size / 10; // Arbitrary segment size
        for (int i = 0; i < size; i += segmentSize) {
            for (int j = i; j < Math.min(i + segmentSize, size) - 1; j += 2) {
                int temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;
            }
        }
        return array;
    }

    private static Integer[] createReversedArray(int size) {
        Integer[] array = createSortedArray(size);
        for (int i = 0; i < size / 2; i++) {
            int temp = array[i];
            array[i] = array[size - i - 1];
            array[size - i - 1] = temp;
        }
        return array;
    }

    private static void insertionSort(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            int current = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > current) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = current;
        }
    }
}