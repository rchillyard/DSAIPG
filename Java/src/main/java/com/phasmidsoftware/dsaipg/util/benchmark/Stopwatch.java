package com.phasmidsoftware.dsaipg.util.benchmark;

/**
 * Simple benchmarking tool: Stopwatch.
 * There is no warm-up here, no pause/resume and no repetition (for these use Timer).
 * It is simply a convenient way to time an execution with results in milliseconds (the default),
 * microseconds, nanoseconds, or seconds.
 * <p>
 * Once the Stopwatch is started (i.e. constructed), you "read" the stopwatch by calling lap(),
 * which returns the number of time units since the previous lap (or since the start).
 * <p>NOTE that Stopwatch is not intended for long laps because it lacks the required precision
 * unless you use seconds as the time unit.</p>
 */
public class Stopwatch implements AutoCloseable {

    /**
     * Constructs a Stopwatch instance with a specified time factor to determine the time unit
     * used for measuring elapsed time.
     *
     * @param timeFactor the factor that defines the granularity of time measurement;
     *                   corresponds to the number of nanoseconds in one time unit.
     */
    private Stopwatch(int timeFactor) {
        this.timeFactor = timeFactor;
        start = System.nanoTime();
    }

    /**
     * Constructs a Stopwatch instance initialized with the specified time units.
     * The time units determine the granularity for measuring elapsed time.
     *
     * @param units the name of the time unit to be used for measurement, e.g., "milliseconds",
     *              "seconds", or "microseconds". If an invalid or unsupported unit is supplied,
     *              an IllegalArgumentException is thrown.
     */
    public Stopwatch(String units) {
        this (calculateTimeFactor(units));
    }

    /**
     * Construct and start a Stopwatch which yields times in milliseconds.
     */
    public Stopwatch() {
        this("milliseconds");
    }

    /**
     * @return the number of time units elapsed since the last lap (or start).
     * @throws AssertionError if this Stopwatch has been closed already.
     */
    public long lap() {
        assert start != null : "Stopwatch is closed";
        final long lapStart = start;
        start = System.nanoTime();
        // NOTE divide the elapsed time, not each reading. Dividing the readings
        // separately truncates each one independently, so the result could be
        // out by one unit in either direction.
        return (start - lapStart) / timeFactor;
    }

    /**
     * Close this Stopwatch.
     * NOTE that you should run the Stopwatch within a try-with-resource statement. See unit tests.
     */
    public void close() {
        start = null;
    }

    static final int MILLION = 1_000_000;

    /**
     * Determines the time factor based on the provided time unit.
     * The time factor is used to adjust time measurements to the specified unit.
     *
     * @param units the name of the time unit, which can be "milliseconds", "seconds",
     *              "microseconds", or "nanoseconds". If null or "milliseconds" is provided,
     *              the factor corresponds to one million nanoseconds.
     * @return an integer value representing the time factor for the specified unit.
     * @throws IllegalArgumentException if the provided time unit is invalid or unsupported.
     */
    private static int calculateTimeFactor(String units) {
        if (units == null || units.equals("milliseconds"))
            return MILLION;
        else if (units.equals("seconds"))
            return 1_000 * MILLION;
        else if (units.equals("microseconds"))
            return 1_000;
        else if (units.equals("nanoseconds"))
            return 1;
        else throw new IllegalArgumentException("Invalid time units: " + units);
    }

    private Long start;

    private final int timeFactor;
}
