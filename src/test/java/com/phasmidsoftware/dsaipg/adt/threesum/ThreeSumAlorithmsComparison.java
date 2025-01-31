package com.phasmidsoftware.dsaipg.adt.threesum;
import java.util.Random;
import java.util.Arrays;
import com.phasmidsoftware.dsaipg.util.Stopwatch;


public class ThreeSumAlorithmsComparison {
    public static void main(String[] args) {
        int[] sizes = {250, 500, 1000, 2000, 4000, 8000, 16000, 32000};
        System.out.println("N,Cubic (ms),Quadratic (ms),Quadratic With Calipers (ms),Quadrithmic (ms)");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (int N : sizes) {
            int[] input = generateRandomArray(N);
            double timeCubic = timeThreeSum(new ThreeSumCubic(input));
            double timeQuadratic = timeThreeSum(new ThreeSumQuadratic(Arrays.copyOf(input, input.length)));
            double timeCalipers = timeThreeSum(new ThreeSumQuadraticWithCalipers(Arrays.copyOf(input, input.length)));
            double timeQuadrithmic = timeThreeSum(new ThreeSumQuadrithmic(Arrays.copyOf(input, input.length)));

            System.out.printf("%d,%.2f,%.2f,%.2f,%.2f%n",
                    N, timeCubic, timeQuadratic, timeCalipers, timeQuadrithmic);
        }
    }

    private static double timeThreeSum(ThreeSum threeSum) {
        try (Stopwatch stopwatch = new Stopwatch()) {
            threeSum.getTriples();
            return stopwatch.lap();
        }
    }

    private static int[] generateRandomArray(int N) {
        Random rand = new Random();
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) {
                arr[i] = rand.nextInt(2001) - 1000;
        }
        return arr;
    }
}