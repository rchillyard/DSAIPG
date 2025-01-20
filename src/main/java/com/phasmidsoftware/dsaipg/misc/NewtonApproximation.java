/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc;

class NewtonApproximation {
    public static void main(String[] args) {
        // Newton's Approximation to solve cos(x) = x
//        double x = 1.0;
//        int left = 200;
//        for (; left > 0; left--) {
//            final double y = Math.cos(x) - x;
//            if (Math.abs(y) < 1E-7) {
//                System.out.println("the solution to cos(x)=x is: " + x);
//                System.exit(0);
//            }
//            x = x + y / (Math.sin(x) + 1);
//        }
        MyNewtonApproximation();


    }

    public static void MyNewtonApproximation() {
        // Newton's Approximation to solve x^3-2x^2+1 = 0
        double[] initialGuesses = {-1.0, 0.5 , 2.0};

        int left = 200;
        boolean has_solution = false;

        for (double guess : initialGuesses) {
            double x = guess;
            for (; left > 0; left--) {
                double fx = Math.pow(x, 3) - 2 * Math.pow(x, 2) + 1;
                double fxDerivative = 3 * Math.pow(x, 2) - 4 * x;

                if (Math.abs(fx) < 1E-7 )  {
                    System.out.println("one solution to x^3-2x^2+1 = 0 is: " + x);
                    has_solution = true;
                    break;
                }

                if(Math.abs(fxDerivative) < 1E-7) {
                    System.out.println("Derivative is too small to continue.");
                    break;
                }

                x = x - fx / fxDerivative;
            }
        }

        if (!has_solution) {
            System.out.println("No solution found.");
        }
    }
}