/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phasmidsoftware.dsaipg.compression.codelength;

/**
 * The Solution class provides a method to calculate a specific probability value
 * based on certain iterative operations on a multi-dimensional array.
 * @author prospace
 *
 * The class utilizes dynamic programming to compute probabilities
 * with constraints on iterations and dimensions.
 */
public class Solution {

    /**
     * Computes a specific probability value based on iterative operations
     * applied to a multi-dimensional array using dynamic programming.
     *
     * @param n      the number of iterations to perform (constraints on the range will apply)
     * @param length the target length used as a determinant during the computation
     * @return the resultant probability value after processing
     */
    public double work1(int n, int length) {
        f[0][0][0] = 1.0;
        double ans = 0.0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j < Math.min(n, 100); j++)
                f[1][i][j] = f[0][i - 1][j - 1] * 0.25;
            for (int j = 0; j < Math.min(n, 100); j++) {
                if (j == length) ans += f[0][i - 1][j] * 0.75;
                else f[1][i][0] += f[0][i - 1][j] * 0.75;
            }
            for (int j = 0; j < Math.min(n, 100); j++) {
                f[0][i][j] = f[1][i][j];
                f[1][i][j] = 0;
            }
        }

        ans += f[0][n][length];

        return ans;
    }

    private final double[][][] f = new double[2][5100][53];
}
