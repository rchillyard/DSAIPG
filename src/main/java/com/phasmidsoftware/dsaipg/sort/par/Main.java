/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        int[] arraySizes = {500000, 1000000, 2000000, 5000000};
        int[] threadCounts = {2, 4, 8}; // Controlling parallelism
        String outputFile = "./src/result.csv";

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {
            bw.write("ArraySize,Cutoff,Threads,Time(ms)\n"); // CSV Header

            for (int size : arraySizes) {
                for (int threads : threadCounts) {
                    ForkJoinPool customPool = new ForkJoinPool(threads);
                    int[] array = new int[size];
                    Random random = new Random();

                    for (int cutoffFactor = 1; cutoffFactor <= 10; cutoffFactor++) {
                        int cutoff = Math.min(size / 2, 10000 * cutoffFactor);
                        ParSort.cutoff = cutoff;
                        long totalTime = 0;

                        for (int t = 0; t < 5; t++) { // Average over 5 runs
                            for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                            long startTime = System.currentTimeMillis();
                            customPool.submit(() -> ParSort.sort(array, 0, array.length)).join();
                            long endTime = System.currentTimeMillis();
                            totalTime += (endTime - startTime);
                        }

                        long avgTime = totalTime / 5;
                        bw.write(size + "," + cutoff + "," + threads + "," + avgTime + "\n");
                        System.out.println("Size: " + size + ", Cutoff: " + cutoff + ", Threads: " + threads + " -> Time: " + avgTime + " ms");
                    }
                    customPool.shutdown();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}