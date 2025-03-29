package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import com.phasmidsoftware.dsaipg.sort.elementary.HeapSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_DualPivot;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.phasmidsoftware.dsaipg.util.general.Utilities.formatWhole;

/**
 * Class to extend Benchmark_Timer for sorting an array of T values.
 * The default implementation of run in this class randomly selects a subset of the array to be sorted.
 * Each sort is preceded (optionally) by a preProcessor and succeeded (optionally) by a postProcessor.
 *
 * @param <T> the underlying type to be sorted.
 */
public class SorterBenchmark<T extends Comparable<T>> extends Benchmark_Timer<T[]> {

    /**
     * Run a benchmark on a sorting problem with N elements.
     *
     * @param description the description of the task being timed.
     * @param N           the number of elements.
     *                    Not to be confused with nRuns, an instance field, which specifies the number of repetitions of the function.
     */
    public void run(String description, int N) {
        if (nRuns > 0) {
            logger.info("run: sort " + formatWhole(N) + " elements with " + this);
            sorter.init(N);
            final double time = super.runFromSupplier(() -> generateRandomArray(ts), nRuns);
            for (TimeLogger timeLogger : timeLoggers) timeLogger.log(description, time, N);
        } else
            logger.warn("run: skipping " + this);
    }

    /**
     * Returns a string representation of the SorterBenchmark object, including the class type,
     * total number of elements, and number of runs.
     *
     * @return a string containing the description of the SorterBenchmark instance.
     */
    @Override
    public String toString() {
        return "SorterBenchmark on " + tClass + " from " + formatWhole(ts.length) + " total elements and " + formatWhole(nRuns) + " runs";
    }

    /**
     * Constructor for a SorterBenchmark where we provide the following parameters:
     *
     * @param tClass        the class of T.
     * @param preProcessor  an optional pre-processor which is applied before each sort.
     * @param sorter        the sorter.
     * @param postProcessor an optional pre-processor which is applied before each sort.
     * @param ts            the array of Ts.
     * @param nRuns         the number of runs to perform in this benchmark.
     * @param timeLoggers   the time-loggers.
     */
    public SorterBenchmark(Class<T> tClass, UnaryOperator<T[]> preProcessor, SortWithHelper<T> sorter, Consumer<T[]> postProcessor, T[] ts, int nRuns, TimeLogger[] timeLoggers) {
        super(sorter.toString(), preProcessor, sorter::mutatingSort, postProcessor);
        this.sorter = sorter;
        this.tClass = tClass;
        this.ts = ts;
        this.nRuns = nRuns;
        this.timeLoggers = timeLoggers;
    }

    /**
     * Constructor for a SorterBenchmark where we provide the following parameters:
     * For this form of the constructor, the post-processor always checks that the sort was successful.
     *
     * @param tClass       the class of T.
     * @param preProcessor an optional pre-processor which is applied before each sort.
     * @param sorter       the sorter.
     * @param ts           the array of Ts.
     * @param nRuns        the number of runs to perform in this benchmark.
     * @param timeLoggers  the time-loggers.
     */
    public SorterBenchmark(Class<T> tClass, UnaryOperator<T[]> preProcessor, SortWithHelper<T> sorter, T[] ts, int nRuns, TimeLogger[] timeLoggers) {
        this(tClass, preProcessor, sorter, sorter::postProcess, ts, nRuns, timeLoggers);
    }

    /**
     * Constructor for a SorterBenchmark where we provide the following parameters:
     * For this form of the constructor, the post-processor always checks that the sort was successful.
     * For this form of the constructor, there is no pre-processor.
     *
     * @param tClass      the class of T.
     * @param sorter      the sorter.
     * @param ts          the array of Ts.
     * @param nRuns       the number of runs to perform in this benchmark.
     * @param timeLoggers the time-loggers.
     */
    public SorterBenchmark(Class<T> tClass, SortWithHelper<T> sorter, T[] ts, int nRuns, TimeLogger[] timeLoggers) {
        this(tClass, null, sorter, ts, nRuns, timeLoggers);
    }

    /**
     * Generates a random array of type T based on a given lookup array.
     *
     * @param lookupArray the array of elements that will be used as the source for generating random values.
     * @return a new array of randomly selected elements of type T, with values chosen from the lookup array.
     */
    private T[] generateRandomArray(T[] lookupArray) {
        return sorter.getHelper().random(tClass, (r) -> lookupArray[r.nextInt(lookupArray.length)]);
    }

    protected final SortWithHelper<T> sorter;
    protected final T[] ts;
    protected final int nRuns;
    protected final TimeLogger[] timeLoggers;
    private final static LazyLogger logger = new LazyLogger(SorterBenchmark.class);
    private final Class<T> tClass;


    // public static void main(String[] args) {
    //     // Create a list to store the results
    //     List<BenchmarkResult> allResults = new ArrayList<>();

    //     // Example array sizes
    //     int[] sizes = {10000, 20000, 40000, 80000, 160000, 256000};
    //     // Example algorithms
    //     String[] algorithms = {"MergeSort", "QuickSortDualPivot", "HeapSort"};

    //     for (String algorithm : algorithms) {
    //         for (int size : sizes) {
    //             // 1) Instrumentation Run
    //             Config.getInstance().setInstrument(true);

    //             SortWithHelper<Integer> sorterInstr = createSorter(algorithm, Config.getInstance());
    //             Integer[] arrayInstr = sorterInstr.getHelper().random(Integer.class, size, 0L);

    //             // Run the sort (collect instrumentation data)
    //             sorterInstr.mutatingSort(arrayInstr);

    //             // Retrieve instrumentation counts
    //             long comparisons = sorterInstr.getHelper().getCompares();
    //             long swaps = sorterInstr.getHelper().getSwaps();
    //             long copies = sorterInstr.getHelper().getCopies();
    //             long hits = sorterInstr.getHelper().getHits();

    //             // 2) Timing Run
    //             Config.getInstance().setInstrument(false);

    //             SortWithHelper<Integer> sorterTime = createSorter(algorithm, Config.getInstance());
    //             Integer[] arrayTime = sorterTime.getHelper().random(Integer.class, size, 0L);

    //             long start = System.nanoTime();
    //             sorterTime.mutatingSort(arrayTime);
    //             long end = System.nanoTime();
    //             long timeMs = (end - start) / 1_000_000;

    //             // Combine everything into a BenchmarkResult
    //             BenchmarkResult br = new BenchmarkResult(
    //                     algorithm,
    //                     size,
    //                     comparisons,
    //                     swaps,
    //                     copies,
    //                     hits,
    //                     timeMs
    //             );
    //             allResults.add(br);

    //             // Optional: reset counters if you want
    //             sorterInstr.getHelper().reset();
    //             sorterTime.getHelper().reset();
    //         }
    //     }

    //     // Export all results to CSV
    //     CsvExporter.exportResults(allResults, "benchmark_results.csv");
    // }

    // // Utility method to create a sorter
    // private static SortWithHelper<Integer> createSorter(String algorithm, Config config) {
    //     switch (algorithm) {
    //         case "MergeSort":
    //             return new MergeSort<>(config);
    //         case "QuickSortDualPivot":
    //             return new QuickSortDualPivot<>(config);
    //         case "HeapSort":
    //             return new HeapSort<>(config);
    //         default:
    //             throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
    //     }
    // }
}