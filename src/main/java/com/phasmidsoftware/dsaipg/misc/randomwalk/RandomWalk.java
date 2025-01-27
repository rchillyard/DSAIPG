/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
 * The RandomWalk class simulates a two-dimensional random walk. A "drunkard"
 * moves in a random direction for a specified number of steps, and the distance
 * from the starting point is measured. Additionally, multiple random walk
 * experiments can be performed to compute average distances.
 */
public class RandomWalk {

    /**
     * Method to compute the distance from the origin (the lamp-post where the drunkard starts) to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
        // TO BE IMPLEMENTED
        long dx = x;
        long dy = y;
         return Math.sqrt(dx * dx + dy * dy);
        // END SOLUTION
    }

    /**
     * Private method to move the current position, that's to say the drunkard moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
       this.x += dx;
       this.y += dy;
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        for (int i = 0; i < m; i++) {
            this.randomMove();
        }
    }

    /**
     * Private method to generate a random move according to the rules of the situation.
     * That's to say, moves can be (+-1, 0) or (0, +-1).
     */
    private void randomMove() {
        boolean ns = random.nextBoolean();
        int step = random.nextBoolean() ? 1 : -1;
        move(ns ? step : 0, ns ? 0 : step);
    }

    private int x = 0;
    private int y = 0;

    private final Random random = new Random();

    /**
     * Perform multiple random walk experiments, returning the mean distance.
     *
     * @param m the number of steps for each experiment
     * @param n the number of experiments to run
     * @return the mean distance
     */
    public static double randomWalkMulti(int m, int n) {
        double totalDistance = 0;
        for (int i = 0; i < n; i++) {
            RandomWalk walk = new RandomWalk();
            walk.randomWalk(m);
            totalDistance = totalDistance + walk.distance();
        }
        return totalDistance / n;
    }

    /**
     * The main method serves as the entry point to the RandomWalk program. It performs
     * either a single random walk experiment or several experiments, based on the
     * provided input arguments, and prints the mean distance.
     *
     * @param args command-line arguments where:
     *             args[0] specifies the number of steps for a random walk (required),
     *             and args[1] optionally specifies the number of experiments (default is 30).
     *             If args is empty, the method throws a RuntimeException indicating invalid syntax.
     */
    public static void main(String[] args) {
//        if (args.length == 0)
//            throw new RuntimeException("Syntax: RandomWalk steps [experiments]");
//        int m = Integer.parseInt(args[0]);
//        for (int i = 0; i <= 100; i++) {
//            int m = i;
//            int n = 100;
//            if (args.length > 1) n = Integer.parseInt(args[1]);
//            double meanDistance = randomWalkMulti(m, n);
//            System.out.println(m + " steps: " + meanDistance + " over " + n + " experiments");
//            System.out.println("sqrt of " + m + " steps: " + Math.sqrt(m));
//            System.out.println("-");
//        }

        String outputFilePath = "res.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (int i = 0; i <= 1000; i++) {
                int m = i;
                int n = 100;
                double meanDistance = randomWalkMulti(m, n);
                writer.write(m + " steps: " + meanDistance + " over " + n + " experiments\n");
                writer.write("sqrt of " + m + " steps: " + Math.sqrt(m) + "\n");
                writer.write("-\n");
            }
            System.out.println("Results successfully written to " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}