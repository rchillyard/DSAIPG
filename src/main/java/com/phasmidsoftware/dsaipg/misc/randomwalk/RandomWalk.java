/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * The RandomWalk class simulates a two-dimensional random walk. A "drunkard"
 * moves in a random direction for a specified number of steps, and the distance
 * from the starting point is measured. Additionally, multiple random walk
 * experiments can be performed to compute average distances.
 */
public class RandomWalk {

    /**
     * Method to compute the distance from the origin (the lamp-post where the
     * drunkard starts) to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
        return Math.sqrt((double) x * x + (double) y * y);
    }

    /**
     * Private method to move the current position, that's to say the drunkard
     * moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        for (int i = 0; i < m; i++) {
            randomMove();
        }
    }

    /**
     * Private method to generate a random move according to the rules of the
     * situation. That's to say, moves can be (+-1, 0) or (0, +-1).
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
     * The main method serves as the entry point to the RandomWalk program. It
     * performs either a single random walk experiment or several experiments,
     * based on the provided input arguments, and prints the mean distance.
     *
     * @param args command-line arguments where: args[0] specifies the number of
     * steps for a random walk (required), and args[1] optionally specifies the
     * number of experiments (default is 30). If args is empty, the method
     * throws a RuntimeException indicating invalid syntax.
     */

    public static void main(String[] args) throws IOException {
    int[] stepsArray = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000};
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 30;
        try (FileWriter writer = new FileWriter("random_walk_results.csv")) {
            writer.write("Steps,Mean Distance\n");
            for (int m : stepsArray) {
                double meanDistance = randomWalkMulti(m, n);
                writer.write(m + "," + meanDistance + "\n");
                System.out.println(m + " steps: " + meanDistance + " over " + n + " experiments");
            }
        }
    }
}