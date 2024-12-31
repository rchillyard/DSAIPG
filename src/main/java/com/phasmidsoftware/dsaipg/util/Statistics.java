/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.sort.SortException;

import java.util.function.Function;

/**
 * The Statistics class is designed to analyze and store statistical data for a series of numerical values.
 * This class calculates statistical measures such as total, mean, standard deviation,
 * and a normalized mean based on the specified normalizing function.
 */
public class Statistics {

    /**
     * Constructor for Statistics.
     *
     * @param property   the particular property we're keeping track of.
     * @param normalizer the normalizer for the results (for example 1/n lg n).
     * @param nRuns      the number of runs.
     * @param size       the size of the problem.
     */
    public Statistics(String property, Function<Double, Double> normalizer, int nRuns, int size) {
        this.property = property;
        this.normalizer = normalizer;
        doubles = new double[nRuns];
        this.size = size;
    }

    /**
     * Adds a specified value to the internal data structure of this Statistics instance.
     * Expands the storage if necessary and marks statistical calculations as stale.
     *
     * @param x the value to be added to the Statistics object.
     * @throws SortException if the underlying data structure is empty.
     */
    public void add(double x) {
        if (doubles.length == 0)
            throw new SortException("Statistics: doubles is empty");
        if (count >= doubles.length) resize(2 * doubles.length);
        doubles[count] = x;
        count = count + 1;
        stale();
    }

    /**
     * Retrieves the count of items or occurrences being tracked.
     *
     * @return the current count as an integer.
     */
    public int getCount() {
        return count;
    }

    /**
     * Computes the total sum of all the elements in the array of doubles.
     * If the total has already been
     * calculated and cached, it returns the cached value.
     * Otherwise, it calculates the total, caches it, and returns it.
     *
     * @return the total sum of the elements in the doubles array.
     */
    public double total() {
        if (total == null) {
            double sum = 0;
            for (int i = 0; i < count; i++) sum += doubles[i];
            total = sum;
        }
        return total;
    }

    /**
     * Calculates the arithmetic mean by dividing the total value by the count.
     *
     * @return the calculated mean as a double.
     * If the count is zero, this method will result in a division by zero exception.
     */
    public double mean() {
        return total() / count;
    }

    /**
     * Computes and returns the standard deviation of the data stored in this Statistics object.
     * The standard deviation is calculated using the formula:
     * sqrt(sum((x - mean)^2) / count), where x represents each data point.
     * The result is cached and subsequent calls return the cached value.
     *
     * @return the standard deviation of the data as a double.
     */
    public double stdDev() {
        if (stdDev == null) {
            double mean = mean();
            double variance = 0;
            for (int i = 0; i < count; i++) variance += (doubles[i] - mean) * (doubles[i] - mean);
            stdDev = Math.sqrt(variance / count);
        }
        return stdDev;
    }

    /**
     * Returns a string representation of the current state of the object.
     * The representation includes the property, the number of elements,
     * the mean, the standard deviation if applicable, and the normalized mean if the data has been updated.
     * If the data has not been updated, the representation indicates that the values are unset.
     *
     * @return a string summarizing the object's state, including statistical details if available,
     * or indicating unset state otherwise.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder().append(property).append(": ");
        if (updated) {
            final boolean stats = stdDev() > 0.0;
            sb.append("n=").append(doubles.length);
            final String s = "; mean=";
            sb.append(s).append(Utilities.asInt(mean()));
            if (stats)
                sb.append("; stdDev=").append(Utilities.asInt(stdDev()));
            sb.append("; normalized=").append(Utilities.formatDecimal3Places(normalizedMean()));
        } else
            sb.append("<unset>");
        return sb.toString();
    }

    /**
     * A pre-defined linearithmic normalizer function that applies the operation {@code Math.log(x) * x}
     * to a given input value of type {@code Double}.
     * <p>
     * This function is useful for normalizing or scaling numerical data according to a linearithmic scheme,
     * which combines logarithmic and linear growth properties.
     * <p>
     * It is expressed as a functional mapping:
     * For an input {@code x > 0}, the function computes {@code x * log(x)}.
     * <p>
     * Note: The input must be a positive number ({@code x > 0}) as natural logarithm
     * is undefined for zero or negative values.
     * Providing such values will result
     * in a runtime exception.
     */
    public static final Function<Double, Double> NORMALIZER_LINEARITHMIC_NATURAL = x -> Math.log(x) * x;

    /**
     * Computes the normalized mean value for the data set represented by this Statistics instance.
     * The normalization is achieved by dividing the mean of the data by the value obtained after
     * applying the normalizer function to the size of the data set.
     *
     * @return the normalized mean of the dataset, calculated as {@code mean() / normalizer.apply((double) size)}.
     */
    public double normalizedMean() {
        return mean() / normalizer.apply((double) size);
    }

    /**
     * Resizes the internal array of doubles to the specified size.
     * If the new size is larger than the current size, the additional elements will be uninitialized.
     *
     * @param n the new size for the array of doubles.
     */
    private void resize(int n) {
        double[] result = new double[n];
        System.arraycopy(doubles, 0, result, 0, doubles.length);
        doubles = result;
    }

    /**
     * Marks the state of the Statistics object as stale, resetting specific cached calculations.
     * This method nullifies the `total` and `stdDev` fields, indicating that previously calculated
     * values are no longer valid.
     * It also sets the `updated` flag to true, signaling that the
     * object's state has changed and calculations need to be refreshed.
     */
    private void stale() {
        total = null;
        stdDev = null;
        updated = true;
    }

    private Double total;
    private Double stdDev;

    private int count = 0;
    private final Function<Double, Double> normalizer;
    private double[] doubles;
    private final int size;
    private final String property;
    private boolean updated = false;

}