package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.Arrays;

import com.phasmidsoftware.dsaipg.util.Stopwatch;

public class ThreeSumTiming {

    public static void main(String[] args) {
        int initialN = 3200;
        int iterations = 6; 
        
        System.out.println("N, Cubic(ms), Quadratic(ms), Quadrithmic(ms)");

        for (int iter = 0; iter < iterations; iter++) {
            int N = initialN * (int) Math.pow(2, iter);
            int maxValue = 1000;
            int[] input = new Source(N, maxValue).intsSupplier(1).get();
            Arrays.sort(input);

            long timeCubic = timeCubic(input);

            long timeQuadratic = timeQuadratic(input);

            long timeQuadrithmic = timeQuadrithmic(input);

            System.out.printf("%d, %d, %d, %d%n", N, timeCubic, timeQuadratic, timeQuadrithmic);
        }
    }

    private static long timeCubic(int[] input) {
        long elapsedMs;
        try (Stopwatch sw = new Stopwatch()) {
            Triple[] result = new ThreeSumCubic(input).getTriples();
            elapsedMs = sw.lap();
        }
        return elapsedMs;
    }

    private static long timeQuadratic(int[] input) {
        long elapsedMs;
        try (Stopwatch sw = new Stopwatch()) {
            Triple[] result = new ThreeSumQuadratic(input).getTriples();
            elapsedMs = sw.lap();
        }
        return elapsedMs;
    }

    private static long timeQuadrithmic(int[] input) {
        long elapsedMs;
        try (Stopwatch sw = new Stopwatch()) {
            Triple[] result = new ThreeSumQuadrithmic(input).getTriples();
            elapsedMs = sw.lap();
        }
        return elapsedMs;
    }
}