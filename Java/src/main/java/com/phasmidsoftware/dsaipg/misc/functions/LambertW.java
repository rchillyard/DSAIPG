package com.phasmidsoftware.dsaipg.misc.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an implementation of the Lambert W function, commonly used in mathematical computations
 * involving non-algebraic equations.
 * The function is defined as the inverse of f(W) = W * exp(W).
 * It supports both the principal branch (W0) and the -1 branch (W-1).
 */
public class LambertW {

    /**
     * Maximum number of iterations allowed for Newton's approximation.
     */
    public static final int MAX_TRIES = 20;

    /**
     * See <a href="https://en.wikipedia.org/wiki/Lambert_W_function">https://en.wikipedia.org/wiki/Lambert_W_function</a>
     * See also <a href="https://math.stackexchange.com/questions/1700919/how-to-derive-the-lambert-w-function-series-expansion">https://math.stackexchange.com/questions/1700919/how-to-derive-the-lambert-w-function-series-expansion</a>
     *
     * @param j         the branch.
     * @param z         the (real) z-value.
     * @param tolerance the tolerance which controls the convergence of the approximation.
     * @return the value of Wj(z)
     * @throws LambertException if j or z is out of range.
     * @throws RuntimeException if the function doesn't converge to within the required tolerance.
     */
    public double W(int j, double z, double tolerance) throws LambertException {
        final double estimate = estimateW(j, z);
//        System.out.println("W: "+j+", z="+z+", estimate=: "+estimate);
        final Newton newton = new Newton("x exp(x) - z = 0", x -> x * Math.exp(x) - z, x -> (1 + x) * Math.exp(x));
        final Either<String, Double> solution = newton.solve(estimate, MAX_TRIES, tolerance);
        if (solution.isRight()) return solution.getRight();
        else throw new RuntimeException(solution.getLeft());
    }

    /**
     * Computes the Lambert W function for all valid branches for a given real z-value and specified tolerance.
     * This method internally calls another W function for each branch (j = 0, j = -1) and collects the results.
     * If a branch computation fails due to a LambertException, it is ignored.
     *
     * @param z         the (real) z-value for which the Lambert W function is computed.
     * @param tolerance the tolerance that controls the convergence of the approximation.
     * @return an array of Double containing the computed values of the Lambert W function for the valid branches.
     */
    public Double[] W(double z, double tolerance) {
        List<Double> result = new ArrayList<>();
        for (int j = 0; j >= -1; j--)
            try {
                result.add(W(j, z, tolerance));
            } catch (LambertException e) {
                // XXX eat this exception;
            }
        return result.toArray(new Double[0]);
    }

    /**
     * Exception class to represent errors specific to the Lambert W function calculations.
     * This exception is used to signal invalid input values or scenarios where Lambert W function
     * computations cannot proceed or converge as expected.
     */
    public static class LambertException extends Exception {
        public LambertException(String message) {
            super(message);
        }
    }

    /**
     * Estimates the Lambert W function for a given branch and input value.
     * This method provides an approximation for the Lambert W function (Wj(x))
     * for the specified branch (j) and input value (x). It supports branches j = 0 and j = -1
     * within certain ranges of x. For other values of j or unsupported ranges of x, it throws an exception.
     *
     * @param j the branch of the Lambert W function to evaluate. Supported values are 0 and -1.
     * @param x the input value for which the Lambert W function is to be evaluated. Restrictions on x
     *          depend on the branch:
     *          - For j = 0: x must be in the range (-1, 1/e].
     *          - For j = -1: x must be in the range [-1/e, 0].
     * @return the approximated value of Wj(x) for the specified branch and input value.
     * @throws LambertException if the branch (j) is unsupported or if the input value (x)
     *                          is out of the valid range for the specified branch.
     */
    private double estimateW(int j, double x) throws LambertException {
        if (j == 0) {
            if (x > -1 && x < 1 / Math.E) {
                double result = 0;
                for (int i = 1; i < MAX_TRIES; i++) result += term0(x, i);
                return result;
            } else if (x >= 1 / Math.E) {
                final double logX = Math.log(x);
                return logX - ((logX > 0) ? Math.log(logX) : 0);
            } else throw new LambertException("LambertW: W(j,x): not supported for j=0, x < -1");
        } else if (j == -1) {
            if (x >= -1 / Math.E && x <= 0) {
                final double log_X = Math.log(-x);
                return log_X - ((log_X > 0) ? Math.log(log_X) : 0);
            } else throw new LambertException("LambertW: W(j,x): not supported for j=-1, x > 0 or x < -1/e");
        } else throw new LambertException("LambertW: W(j,x): not supported for j=" + j);
    }

    /**
     * Calculates a specific term used in the series expansion of the Lambert W function.
     *
     * @param x the input value for which the term is computed.
     * @param p the index of the term in the series expansion.
     * @return the computed term value for the given parameters.
     */
    private static double term0(double x, int p) {
        return Math.pow(x, p) * Math.pow(-p, p - 1) / factorial(p);
    }

    /**
     * Computes the factorial of a given non-negative integer.
     *
     * @param n the non-negative integer for which the factorial is to be calculated.
     *          If n is 0 or 1, the method returns 1 as 0! and 1! are defined to be 1.
     * @return the factorial of the specified integer as a double.
     * Returns 1 if the input is less than 2.
     */
    private static double factorial(int n) {
        if (n < 2) return 1;
        else return n * factorial(n - 1);
    }
}
