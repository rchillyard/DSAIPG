/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;
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
         return Math.sqrt((double)this.x * (double)this.x + (double) this.y * (double)this.y);
        // END SOLUTION
    }

    /**
     * Private method to move the current position, that's to say the drunkard moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
        // TO BE IMPLEMENTED  do move
         this.x += dx;
         this.y += dy;
        // END SOLUTION
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        this.x = this.y =0;
        for(int i = 1; i <= m; i++)
            randomMove();
        // TO BE IMPLEMENTED

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
    public static void main(String[] args) throws  Exception{
//        if (args.length == 0)
//            throw new RuntimeException("Syntax: RandomWalk steps [experiments]");
//        int m = Integer.parseInt(args[0]);
        int n,m,gap;


        File file = new File("D:\\NortheasternUniversity\\INFO 6205 Program Structure and Algorithms\\Assignment1\\Gap.csv");
        PrintWriter output = new PrintWriter(file) ;


        output.println("m,d,sqrt(m)");
        for(int i = 100; i <= 10000;i++){

            m = i;
            n = 30;
            if (args.length > 1) n = Integer.parseInt(args[1]);
            double meanDistance = randomWalkMulti(m, n);
            System.out.println(m + " steps: " + meanDistance + " over " + n + " experiments \n");
            //System.out.printf("Gap between Sqrt(m) and actual value %.1f%%\n",Math.abs(Math.sqrt(m)-meanDistance)/meanDistance*100);
            if(i%20==0)
           output.printf("%d,%.1f,%.1f\n",m,meanDistance,Math.sqrt(m));
           //System.out.printf("m= %d meanDistance=%.1f sqrtm=%.1f\n",m,meanDistance,Math.sqrt(m));
        }
        output.close();
    }
}