/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.Arrays;

/**
 * This class represents a mathematical matrix with defined rows and columns
 * and supports basic matrix operations such as multiplication.
 */
public class Matrix {

    /**
     * Multiplies this matrix by another matrix, returning the resulting matrix.
     * The operation is only valid when the number of columns in this matrix
     * equals the number of rows in the other matrix.
     *
     * @param other the matrix to multiply with this matrix.
     * @return a new matrix resulting from the multiplication of this matrix by the other matrix.
     * @throws RuntimeException if the number of columns in this matrix does not match the number of rows in the other matrix.
     */
    public Matrix multiply(final Matrix other) {
        if (this.columns == other.rows) {
            double[][] result = new double[rows][other.columns];
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < other.columns; j++) {
                    double x = 0;
                    for (int k = 0; k < this.columns; k++) x += this.values[i][k] * other.values[k][j];
                    result[i][j] = x;
                }
            }
            return new Matrix(this.rows, other.columns, result);
        } else
            throw new RuntimeException("incompatible matrices");
    }

    /**
     * Constructs a Matrix object with the specified number of rows, columns, and initial values.
     *
     * @param rows    the number of rows in the matrix.
     * @param columns the number of columns in the matrix.
     * @param values  a 2D array representing the values of the matrix; its size must match the specified rows and columns.
     */
    public Matrix(final int rows, final int columns, final double[][] values) {
        super();
        this.rows = rows;
        this.columns = columns;
        this.values = values;
    }

    /**
     * The main method demonstrates functionality of matrix operations including:
     * sorting command-line arguments, creating two matrices, performing matrix multiplication,
     * and displaying the resulting matrix dimensions and values.
     *
     * @param args command-line arguments passed to the program. These arguments are sorted as a demonstration.
     */
    public static void main(String[] args) {
        Arrays.sort(args);
        double[][] aVals = new double[2][2];
        aVals[0][0] = 1;
        aVals[0][1] = 2;
        aVals[1][0] = 3;
        aVals[1][1] = 4;
        double[][] bVals = new double[2][2];
        bVals[0][0] = -1;
        bVals[0][1] = 2;
        bVals[1][0] = -1;
        bVals[1][1] = 0;

        Matrix a = new Matrix(2, 2, aVals);
        Matrix b = new Matrix(2, 2, bVals);
        Matrix c = a.multiply(b);
        System.out.println(c.rows);
        System.out.println(c.columns);
        for (int i = 0; i < c.rows; i++) {
            for (int j = 0; j < c.columns; j++) {
                System.out.println(c.values[i][j]);
            }
        }
    }

    private final int rows;
    private final int columns;
    private final double[][] values;

}