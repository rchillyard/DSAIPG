package com.phasmidsoftware.dsaipg.adt.threesum; 

import com.phasmidsoftware.dsaipg.util.Stopwatch;
import com.phasmidsoftware.dsaipg.adt.threesum.ThreeSumCubic;
import com.phasmidsoftware.dsaipg.adt.threesum.ThreeSumQuadratic;
import com.phasmidsoftware.dsaipg.adt.threesum.ThreeSumQuadrithmic;

public class ThreeSumTime {
     public static void main(String[] args) {
        int[] sizes = {400, 800, 1600, 3200, 6400};     
        for (int N : sizes) {
            int[] inputArray = generateRandomArray(N);
            System.out.println("N = " + N);
             
            // Measure time for Cubic
            Stopwatch timer = new Stopwatch();
            new ThreeSumCubic(inputArray).getTriples();
            double cubicTime = timer.lap();
            System.out.println("Cubic time: " + cubicTime + " milli-seconds");
            
            // Measure time for Quadratic
            timer = new Stopwatch();
            new ThreeSumQuadratic(inputArray).getTriples();
            double quadraticTime = timer.lap();
            System.out.println("Quadratic time: " + quadraticTime + " milliseconds");
            
            // Measure time for Quadrithmic
            timer = new Stopwatch();
            new ThreeSumQuadrithmic(inputArray).getTriples();
            double quadrithmicTime = timer.lap();
            System.out.println("Quadrithmic time: " + quadrithmicTime + " milliseconds");
            
            System.out.println("-----------------------------------");
        }
    }
    
    private static int[] generateRandomArray(int N) {
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) {
            arr[i] = (int) (Math.random() * 2000) - 1000; // Random numbers from -1000 to 1000
            }
            return arr;
        }
    }
