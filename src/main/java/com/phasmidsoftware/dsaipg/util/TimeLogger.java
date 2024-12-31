/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

/**
 * Class to handle logging of times, both raw and normalized.
 */
public class TimeLogger {

    /**
     * Method to log the time (in mSecs).
     * If minimumComparisons is null, we just log the raw time.
     * Otherwise, we log the normalized time based on minimumComparisons.
     *
     * @param description the description of the task being timed.
     * @param time        the raw time.
     * @param N           the size of the problem.
     */
    public void log(String description, double time, int N) {
        double t = minimumComparisons == null ? time : time / minimumComparisons.apply(N) * 1e6;
        logger.info(description + ": " + prefix + " " + formatTime(t));
    }

    /**
     * Constructor for the TimeLogger class.
     *
     * @param prefix             the prefix to be used when logging time.
     * @param minimumComparisons a Function to determine the normalization factor for time
     *                           based on the problem size.
     *                           If null, time will not be normalized.
     */
    public TimeLogger(String prefix, Function<Integer, Double> minimumComparisons) {
        this.prefix = prefix;
        this.minimumComparisons = minimumComparisons;
    }

    /**
     * Formats the given time value into a string based on a predefined decimal pattern.
     *
     * @param time the time value to be formatted, in seconds or milliseconds, represented as a double.
     * @return a string representation of the formatted time.
     */
    private static String formatTime(double time) {
        decimalFormat.applyPattern(timePattern);
        return decimalFormat.format(time);
    }

    final static LazyLogger logger = new LazyLogger(TimeLogger.class);

    private static final Locale locale = new Locale("en", "US");
    private static final String timePattern = "######.0000";
    private static final DecimalFormat decimalFormat = (DecimalFormat)
            NumberFormat.getNumberInstance(locale);

    private final String prefix;
    private final Function<Integer, Double> minimumComparisons;
}