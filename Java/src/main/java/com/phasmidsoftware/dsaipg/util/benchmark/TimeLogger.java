package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

/**
 * Class to handle logging of times, both raw and normalized.
 */
public class TimeLogger {

    /**
     * A lazy logger instance used for logging messages or events related
     * to the {@link TimeLogger} class. This logger facilitates efficient
     * logging while delaying log message construction until it is certain
     * that the message will be logged, thereby reducing unnecessary overhead.
     */
    final static LazyLogger logger = new LazyLogger(TimeLogger.class);

    /**
     * Method to log the time (in mSecs).
     * If `complexity` is null, we just log the raw time.
     * Otherwise, we log the normalized time based on `complexity`.
     *
     * @param description the description of the task being timed.
     * @param time        the raw time.
     * @param N           the size of the problem.
     */
    public void log(String description, double time, int N) {
        double t = complexity == null ? time : time / complexity.apply(N) * 1e6;
        logger.info(description + ": " + prefix + " " + formatTime(t));
    }

    /**
     * Constructor for the TimeLogger class.
     *
     * @param prefix             the prefix to be used when logging time.
     * @param complexity a Function to determine the normalization factor for time
     *                           based on the problem size.
     *                           If null, time will not be normalized.
     */
    public TimeLogger(String prefix, Function<Integer, Double> complexity) {
        this.prefix = prefix;
        this.complexity = complexity;
    }

    /**
     * Formats the given time value into a string based on a predefined decimal pattern.
     * The time may or may not be normalized.
     *
     * @param time the time value to be formatted, in seconds or milliseconds, represented as a double.
     * @return a string representation of the formatted time.
     */
    private static String formatTime(double time) {
        decimalFormat.applyPattern(timePattern);
        return decimalFormat.format(time);
    }

    private static final Locale locale = new Locale("en", "US");
    private static final String timePattern = "######.0000";
    private static final DecimalFormat decimalFormat = (DecimalFormat)
            NumberFormat.getNumberInstance(locale);

    private final String prefix;
    private final Function<Integer, Double> complexity;
}
