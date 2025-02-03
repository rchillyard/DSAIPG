package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.Random;
import java.util.Arrays;

public class ThreeSumTime {

    public static void main(String[] args) {
        int[] sizes = {100, 200, 400, 800, 1600}; // using the doubling method for at least five values of N
        for (int N : sizes) {
            int[] data = generateRandomArray(N);

            System.out.println("N = " + N);

            // ThreeSumCubic
            long start = System.nanoTime();
            ThreeSumCubic cubic = new ThreeSumCubic(data);
            cubic.getTriples();
            long end = System.nanoTime();
            System.out.printf("Cubic: %.3f seconds\n", (end - start) / 1e9);

            // ThreeSumQuadratic
            start = System.nanoTime();
            ThreeSumQuadratic quadratic = new ThreeSumQuadratic(data);
            quadratic.getTriples();
            end = System.nanoTime();
            System.out.printf("Quadratic: %.3f seconds\n", (end - start) / 1e9);

            // ThreeSumQuadrithmic
            start = System.nanoTime();
            ThreeSumQuadrithmic quadrithmic = new ThreeSumQuadrithmic(data);
            quadrithmic.getTriples();
            end = System.nanoTime();
            System.out.printf("Quadrithmic: %.3f seconds\n", (end - start) / 1e9);
        }
    }

    private static int[] generateRandomArray(int N) {
        Random rand = new Random();
        int[] array = new int[N];
        for (int i = 0; i < N; i++) {
            array[i] = rand.nextInt(200) - 100;
        }
        Arrays.sort(array);
        return array;
    }
}
