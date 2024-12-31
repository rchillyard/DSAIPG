/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.HashMap;
import java.util.function.Function;

/**
 * The StatPack class serves as a container for managing and tracking statistical
 * data across multiple keys.
 * Each key is associated with a {@code Statistics} object,
 * which provides functions to compute various statistical measures such as count, total,
 * mean, and standard deviation of the tracked data.
 * <p>
 * It is designed to handle multiple runs of statistical data and allows normalization
 * functions to be applied to the data through its components. The tracked data is stored
 * as a collection of {@code Statistics} objects mapped to unique string keys.
 * <p>
 * This class provides methods for adding data, retrieving different statistical measures,
 * and checking the validity of the current state of the StatPack object.
 * <p>
 *  TODO add key "classification" and maybe also "heap access."
 */
public class StatPack {

    /**
     * Constructor of a StatPack.
     *
     * @param normalizer the normalizers.
     * @param nRuns      the number of runs.
     * @param keys       the set of keys for properties to be tracked.
     */
    public StatPack(Function<Double, Double> normalizer, int nRuns, int size, String... keys) {
        n = nRuns;
        map = new HashMap<>();
        for (String key : keys) map.put(key, new Statistics(key, normalizer, nRuns, size));
    }

    /**
     * Adds a specified value to the statistics corresponding to the given key.
     *
     * @param key the key identifying the Statistics object where the value will be added.
     * @param x   the value to be added to the Statistics object.
     * @throws RuntimeException if the key is not valid or no Statistics object is associated with the given key.
     */
    public void add(String key, double x) {
        getStatistics(key).add(x);
    }

    /**
     * Retrieves the {@code Statistics} object associated with the specified key.
     *
     * @param key the key used to identify the {@code Statistics} object in the map
     * @return the {@code Statistics} object associated with the provided key
     * @throws RuntimeException if the key is not valid or does not exist in the map
     */
    public Statistics getStatistics(String key) {
        final Statistics statistics = map.get(key);
        if (statistics == null) throw new RuntimeException("StatPack.getStatistics(" + key + "): key not valid");
        return statistics;
    }

    /**
     * Retrieves the count of occurrences or items for the specified key from the statistics.
     *
     * @param key the key representing the specific statistic to retrieve the count for.
     * @return the count of occurrences tracked for the given key as an integer.
     */
    public int getCount(String key) {
        return getStatistics(key).getCount();
    }

    /**
     * Computes the total value for a specified key based on the statistics tracked.
     *
     * @param key the identifier for the specific statistical data to retrieve and compute the total.
     * @return the total sum of all the values associated with the given key.
     */
    public double total(String key) {
        return getStatistics(key).total();
    }

    /**
     * Computes the arithmetic mean of the values associated with the specified key.
     *
     * @param key the key representing the data collection for which the mean needs to be calculated.
     * @return the arithmetic mean as a double of the values for the given key.
     * @throws RuntimeException if the key is not valid or does not exist in the statistics map.
     */
    public double mean(String key) {
        return getStatistics(key).mean();
    }

    /**
     * Computes and returns the standard deviation of the data associated with the given key.
     * The standard deviation quantifies the amount of variation or dispersion of a set of values.
     *
     * @param key the key for which the standard deviation is to be computed.
     * @return the standard deviation of the data as a double.
     */
    public double stdDev(String key) {
        return getStatistics(key).stdDev();
    }

    /**
     * Determines if the current state is invalid based on the value of the variable n.
     *
     * @return true if n is less than or equal to 0; false otherwise.
     */
    public boolean isInvalid() {
        return n <= 0;
    }

    /**
     * Returns the string representation of the StatPack object.
     * The representation
     * includes all the keys and their associated statistics within the StatPack,
     * formatted in a human-readable way. If the map is empty, the representation
     * will indicate that the StatPack is empty.
     *
     * @return a string representation of the StatPack object, including all tracked keys and their statistics.
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("StatPack {");
        if (map.isEmpty()) stringBuilder.append("<empty>}");
        for (String key : map.keySet()) {
            final Statistics statistics = map.get(key);
            String string = statistics.toString();
            stringBuilder.append(string).append("; ");
        }
        return stringBuilder.toString().replaceAll("; $", "}");
    }

    private final HashMap<String, Statistics> map;
    private final int n;

}