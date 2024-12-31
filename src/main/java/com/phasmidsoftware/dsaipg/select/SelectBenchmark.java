/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.select;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.Benchmark;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.Config;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Class for benchmarking the performance of different selection algorithms.
 * The benchmarks compare selections from various input array configurations such as random,
 * ordered, partially ordered, and reverse-ordered arrays using specified selection methods.
 * @author Suchita Dabir, 2024
 */
public class SelectBenchmark {

    /**
     * Constructs a SelectBenchmark object with the specified number of runs and array size.
     * This class is used to benchmark selection algorithms.
     *
     * @param runs the number of benchmark runs to execute.
     * @param n the size of the array used in the benchmarks.
     */
    public SelectBenchmark(int runs, int n) {
        this.runs = runs;
        this.safetyFactor = 10;
        this.n = n;
    }

    /**
     * Executes benchmark tests for selection algorithms using a configurable number
     * of elements and a specified safety factor.
     *
     * This method calculates the total number of elements to process, loads the configuration,
     * creates an instance of a helper for the benchmarks, and performs the benchmarks for
     * comparison. The results are returned as a string containing the execution details.
     *
     * @return A string detailing the benchmark results, including performance comparisons
     *         between different selection algorithms.
     * @throws IOException If there is an issue loading the configuration or related resources.
     */
    public String runBenchmarks() throws IOException {
        int N = n * safetyFactor;
        System.out.println("SelectBenchmark: N=" + N);
        Config config = Config.load(SelectBenchmark.class);
        try (Helper<Integer> helper = new NonInstrumentingComparableHelper<>("SelectBenchmark", N, config)) {
            return quickAndSlowBenchmarks(helper, N);
        }
    }

    /**
     * Executes a set of benchmarks to compare the performance of QuickSelect and SlowSelect
     * algorithms under different types of input data: random, ordered, partially-ordered,
     * and reverse-ordered. The method initializes the helper object for generating input data,
     * performs the benchmarks, and returns the results as a formatted string.
     *
     * @param helper a helper object used to create input data for the benchmarks.
     * @param N the size of the array to be generated and used in the benchmarks.
     * @return a formatted string containing the benchmark results, including performance
     *         comparisons between QuickSelect and SlowSelect algorithms across input types.
     */
    @NotNull
    private String quickAndSlowBenchmarks(Helper<Integer> helper, int N) {
        helper.init(N);
        int k = N / 2;
        QuickSelect<Integer> quickSelect = new QuickSelect<>();
        SlowSelect<Integer> slowSelect = new SlowSelect<>(k);
        String quickSelector = "QuickSelect";
        String slowSelector = "SlowSelect";
        StringBuilder sb = new StringBuilder();

        combineResults(sb,
                resultMessage(quickSelector + "," + "random", doBenchmark(quickSelector, quickSelect, k, () -> helper.random(Integer.class, Random::nextInt), runs), N),
                resultMessage(slowSelector + "," + "random", doBenchmark(slowSelector, slowSelect, k, () -> helper.random(Integer.class, Random::nextInt), runs), N));
        combineResults(sb,
                resultMessage(quickSelector + "," + "ordered", doBenchmark(quickSelector, quickSelect, k, () -> helper.ordered(N, Integer.class, i1 -> i1), runs), N),
                resultMessage(slowSelector + "," + "ordered", doBenchmark(slowSelector, slowSelect, k, () -> helper.ordered(N, Integer.class, i2 -> i2), runs), N));
        combineResults(sb,
                resultMessage(quickSelector + "," + "partially-ordered", doBenchmark(quickSelector, quickSelect, k, () -> helper.partialOrdered(N, Integer.class, i1 -> i1), runs), N),
                resultMessage(slowSelector + "," + "partially-ordered", doBenchmark(slowSelector, slowSelect, k, () -> helper.partialOrdered(N, Integer.class, i2 -> i2), runs), N));
        combineResults(sb,
                resultMessage(quickSelector + "," + "reverse-ordered", doBenchmark(quickSelector, quickSelect, k, () -> helper.reverse(N, Integer.class, i -> i), runs), N),
                resultMessage(slowSelector + "," + "reverse-ordered", doBenchmark(slowSelector, slowSelect, k, () -> helper.reverse(N, Integer.class, i1 -> i1), runs), N));

        return sb.toString();
    }

    /**
     * Appends the results of the benchmark tests to the given StringBuilder.
     *
     * @param sb the StringBuilder to which the results will be appended.
     * @param quickResult the result of the QuickSelect algorithm benchmark.
     * @param slowResult the result of the SlowSelect algorithm benchmark.
     */
    private static void combineResults(StringBuilder sb, String quickResult, String slowResult) {
        sb.append(quickResult);
        sb.append(slowResult);
    }

    /**
     * Generates a formatted result message consisting of the input string, a specific class-level field value,
     * and the provided numeric values.
     *
     * @param s the input string to include in the result message.
     * @param d a double value to include in the result message.
     * @param n an integer value to include in the result message.
     * @return a formatted string that combines the inputs along with a class-level field value.
     */
    private String resultMessage(String s, double d, int n) {
        return s + "," + runs + "," + n + "," + d + "\n";
    }

    /**
     * Executes a benchmark for a selection algorithm and computes the average execution time.
     *
     * @param description a brief description of the benchmark or the algorithm being tested.
     * @param select the selection algorithm that implements the Select interface, used to find the k-th smallest element.
     * @param k the index (0-based) of the k-th smallest element to find in the arrays generated during benchmarking.
     * @param supplier a supplier that generates input arrays for the benchmark.
     * @param runs the number of times the benchmark will execute to compute the average time.
     * @return the average execution time of the benchmark in milliseconds.
     */
    private static double doBenchmark(String description, Select<Integer> select, int k, Supplier<Integer[]> supplier, final int runs) {
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description,
                (xs) -> Arrays.copyOf(xs, xs.length),
                (xs) -> select.select(xs, k),
                null
        );
        return benchmark.runFromSupplier(supplier, runs);
    }

    /**
     * The main method serves as the entry point to execute benchmarks comparing
     * the performance of selection algorithms. It runs benchmark tests, combines
     * the results, and writes the data to a CSV file.
     *
     * @param args the command-line arguments, which are currently not used in this method.
     * @throws IOException if an I/O error occurs while creating or writing to the output file.
     */
    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        combineResults(sb, new SelectBenchmark(100, 256).runBenchmarks(), new SelectBenchmark(50, 512).runBenchmarks());

        try {
            writeToFile(sb);
        } catch (Exception e) {
            e.printStackTrace(System.err); // TODO use logger
        }
    }

    /**
     * Writes the content of the provided StringBuilder to a file named "SelectBenchmark.csv" in the current working directory.
     * If the file exists, it will be overwritten. The method appends a header along with the content.
     *
     * @param sb the StringBuilder containing the content to be written to the file.
     * @throws IOException if an input/output error occurs while creating or writing to the file.
     */
    private static void writeToFile(StringBuilder sb) throws IOException {
        String currentDirPath = System.getProperty("user.dir");
        String OutputCsvFilename = "SelectBenchmark.csv";
        String path = Paths.get(currentDirPath, OutputCsvFilename).toString();
        System.out.println("Output CSV File Path :-> " + path);
        File file = new File(path);
        String header = "Method,Array-Ordering,Runs,N,Time\n";
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.append(header);
        bw.append(sb);

        bw.close();
    }

    private final int runs;
    private final int n;
    private final int safetyFactor;

}