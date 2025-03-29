package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.sort.elementary.HeapSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_DualPivot;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyInstrumentationRunner {

    public static void main(String[] args) {

        Config config;
        try {
            config = Config.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.ini", e);
        }


        List<BenchmarkResult> allResults = new ArrayList<>();


        int[] sizes = {10000, 20000, 40000, 80000, 160000, 256000};
        String[] algorithms = {"MergeSort", "QuickSortDualPivot", "HeapSort"};


        for (String algorithm : algorithms) {
            for (int size : sizes) {

                config.get("helper").put("instrument", "true");


                SortWithHelper<Integer> sorterInstr = createSorter(algorithm, config, size, 1);

                Integer[] arrayInstr = sorterInstr.getHelper().random(size, Integer.class, r -> r.nextInt(1_000_000));


                sorterInstr.mutatingSort(arrayInstr);

                long comparisons = sorterInstr.getHelper().getCompares();
                long swaps = sorterInstr.getHelper().getSwaps();
                long copies = sorterInstr.getHelper().getCopies();
                long hits = sorterInstr.getHelper().getHits();

                config.get("helper").put("instrument", "false");

                SortWithHelper<Integer> sorterTime = createSorter(algorithm, config, size, 1);
                Integer[] arrayTime = sorterTime.getHelper().random(size, Integer.class, r -> r.nextInt(1_000_000));

                long start = System.nanoTime();
                sorterTime.mutatingSort(arrayTime);
                long end = System.nanoTime();
                long timeMs = (end - start) / 1_000_000; // convert ns to ms


                BenchmarkResult br = new BenchmarkResult(
                        algorithm,
                        size,
                        comparisons,
                        swaps,
                        copies,
                        hits,
                        timeMs
                );
                allResults.add(br);
            }
        }

        CsvExporter.exportResults(allResults, "benchmark_results.csv");

        System.out.println("Done! Created benchmark_results.csv");
    }


    private static SortWithHelper<Integer> createSorter(String algorithm, Config config, int n, int nRuns) {
        switch (algorithm) {
            case "MergeSort":
                return new MergeSort<>(n, nRuns, config);
            case "QuickSortDualPivot":
                return new QuickSort_DualPivot<>(n, nRuns, config);
            case "HeapSort":

                return new HeapSort(n, nRuns, config);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}