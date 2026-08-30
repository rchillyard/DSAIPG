package com.phasmidsoftware.dsaipg.compression.runLengthEncoding;

/**
 * This class supports the concept of run-length encoding.
 * <p>
 * The RunLength class provides functionality to calculate the probability
 * of runs of a specific length and color (black or white) in a sequence
 * based on the given probability of a black pixel.
 */
public class RunLength {

    /**
     * Calculates the probability of a run of a specific length and color (black or white).
     * A run is defined as a consecutive sequence of pixels of the same color.
     *
     * @param n     The length of the run.
     * @param black A boolean indicating the color of the run.
     *              If true, calculates the probability for black runs; otherwise, for white runs.
     * @return The probability of a run of the given length and color.
     */
    public double probabilityOfRunLength(int n, boolean black) {
        if (black) return Math.pow(p_pixel_black, n) * (1 - p_pixel_black);
        else return Math.pow(1 - p_pixel_black, n) * p_pixel_black;
    }

    /**
     * The entry point for the program. Calculates and displays the probabilities,
     * totals, and expectations of run lengths for black and white pixels based
     * on the given probability of a black pixel and a maximum run length.
     *
     * @param args Command-line arguments. The first argument (optional) represents
     *             the maximum run length (default is 25). The second argument
     *             (optional) represents the probability of a black pixel (default is 0.25).
     */
    public static void main(String[] args) {
        int n = Integer.parseInt(args.length > 0 ? args[0] : "25");
        double p_black = Double.parseDouble(args.length > 1 ? args[1] : "0.25");
        System.out.println("RunLength with n = " + n +
                " and p(black) = " + p_black);
        System.out.println("In the following, i is the length of a run of the given color; p(i) is the probability of a run with that length");
        RunLength runLength = new RunLength(p_black);
        double total_black = 0;
        double total_white = 0;
        double expectation_black = 0;
        double expectation_white = 0;
        for (int i = 1; i < n; i++) {
            double p_i_black = runLength.probabilityOfRunLength(i, true);
            double p_i_white = runLength.probabilityOfRunLength(i, false);
            total_black += p_i_black;
            total_white += p_i_white;
            expectation_black += p_i_black * i;
            expectation_white += p_i_white * i;
            System.out.println("i = " + i + " (black), p(i) = " + p_i_black);
            System.out.println("i = " + i + " (white), p(i) = " + p_i_white);
        }
        System.out.println("total = " + total_black + " for black");
        System.out.println("total = " + total_white + " for white");
        System.out.println("expectation = " + expectation_black + " for black");
        System.out.println("expectation = " + expectation_white + " for white");
    }

    /**
     * Constructs a RunLength object with the specified probability of a black pixel.
     *
     * @param p_pixel_black the probability of a black pixel, a value between 0.0 and 1.0
     */
    public RunLength(double p_pixel_black) {
        this.p_pixel_black = p_pixel_black;
    }

    private final double p_pixel_black;
}
