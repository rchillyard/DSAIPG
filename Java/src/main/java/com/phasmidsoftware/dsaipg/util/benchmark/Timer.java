package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Class which is able to time the running of functions.
 */
public class Timer {

    /**
     * A static, thread-safe logger instance used to log events within the Timer class.
     * Maintains a lazy initialization approach to optimize performance while minimizing memory usage.
     * Specifically targets the Timer class for logging purposes.
     */
    final static LazyLogger logger = new LazyLogger(Timer.class);

    /**
     * Run the given function n times, once per "lap" and then return the result of calling meanLapTime().
     * The clock will be running when the method is invoked and when it is quit.
     * <p>
     * This is the simplest form of repeat.
     *
     * @param n        the number of repetitions.
     * @param function a function which yields a T.
     * @param <T>      the type supplied by function (amy be Void).
     * @return the average milliseconds per repetition.
     */
    public <T> double repeat(int n, Supplier<T> function) {
        for (int i = 0; i < n; i++) {
            function.get();
            lap();
        }
        pause();
        final double result = meanLapTime();
        resume();
        return result;
    }

    /**
     * Run the given functions n times, once per "lap" and then return the mean lap time.
     *
     * @param n        the number of repetitions.
     * @param supplier a function which supplies a different T value for each repetition.
     * @param function a function T=>U and which is to be timed.
     * @param <T>      the type which is supplied by supplier and passed in to function.
     * @param <U>      the type which is the result of <code>function</code> (may be Void).
     * @return the average milliseconds per repetition.
     */
    public <T, U> double repeat(int n, Supplier<T> supplier, Function<T, U> function) {
        return repeat(n, false, supplier, function, null, null);
    }

    /**
     * Pause (without counting a lap); run the given functions n times while being timed, i.e., once per "lap", and finally return the result of calling meanLapTime().
     *
     * @param n            the number of repetitions.
     * @param warmup       true if this is in the warmup phase.
     * @param supplier     a function which supplies a T value.
     * @param function     a function T=>U and which is to be timed.
     * @param preFunction  a function which pre-processes a T value and which precedes the call of function, but which is not timed (may be null). The result of the preFunction, if any, is also a T.
     * @param postFunction a function which consumes a U and which succeeds the call of function, but which is not timed (may be null).
     * @param <T>          the type which is supplied by supplier, processed by prefunction (if any), and passed in to function.
     * @param <U>          the type which is the result of function and the input to postFunction (if any).
     * @return the average milliseconds per repetition.
     */
    public <T, U> double repeat(int n, boolean warmup, Supplier<T> supplier, Function<T, U> function, UnaryOperator<T> preFunction, Consumer<U> postFunction) {
        // NOTE that the timer is running when this method is called and should still be running when it returns.
        pause();
        doTrace(() -> "repeat: with " + n + " runs"); // NOTE optional
        doTrace(warmup, () -> "warmup"); // NOTE optional
        int lastx = -1;
        for (int i = 0; i < n; i++)
            lastx = doRepeatForIteration(n, warmup, supplier, function, preFunction, postFunction, lastx, i);
        final double result = meanLapTime();
        showProgress.accept("\r");
        resume();
        return result;
    }

    /**
     * Stop this Timer and return the mean lap time in milliseconds.
     *
     * @return the average milliseconds used by each lap.
     * @throws TimerException if this Timer is not running.
     */
    public double stop() {
        pauseAndLap();
        doTrace(() -> "stop timer");
        return meanLapTime();
    }

    /**
     * Return the mean lap time in milliseconds for this paused timer.
     *
     * @return the average milliseconds used by each lap.
     * @throws TimerException if this Timer is running.
     */
    public double meanLapTime() {
        if (running) throw new TimerException();
        // NOTE without this guard, no laps gives ticks/0 -- Infinity, silently, as
        // the answer to "how long did each run take". repeat(0, ...) does exactly
        // that. A benchmark reporting Infinity is better than one reporting a
        // number nobody checks, but an exception is better still.
        if (laps <= 0) throw new TimerException("meanLapTime: no laps were recorded");
        return toMillisecs(ticks) / laps;
    }

    /**
     * Pause this timer at the end of a "lap" (repetition).
     * The lap counter will be incremented by one.
     *
     * @throws TimerException if this Timer is not running.
     */
    public void pauseAndLap() {
        lap();
        ticks += getClock();
        running = false;
        doTrace(() -> "pause timer and lap after millisecs: " + ticks * 1.0E-6);
    }

    /**
     * Resume this timer to begin a new "lap" (repetition).
     *
     * @throws TimerException if this Timer is already running.
     */
    public void resume() {
        if (running) throw new TimerException();
        ticks -= getClock();
        doTrace(() -> "resume timer");
        running = true;
    }

    /**
     * Increment the lap counter without pausing.
     * This is the equivalent of calling pause and resume.
     *
     * @throws TimerException if this Timer is not running.
     */
    public void lap() {
        if (!running) throw new TimerException();
        laps++;
        doTrace(() -> "lap " + laps);
    }

    /**
     * Pause this timer during a "lap" (repetition).
     * The lap counter will remain the same.
     *
     * @throws TimerException if this Timer is not running.
     */
    public void pause() {
        pauseAndLap();
        laps--;
        doTrace(() -> "pause timer");
    }

    /**
     * Method to yield the total number of milliseconds elapsed.
     * NOTE: an exception will be thrown if this is called while the timer is running.
     *
     * @return the total number of milliseconds elapsed for this timer.
     */
    public double millisecs() {
        if (running) throw new TimerException();
        return toMillisecs(ticks);
    }

    @Override
    public String toString() {
        return "Timer{" +
                "ticks=" + ticks +
                ", laps=" + laps +
                ", running=" + running +
                '}';
    }

    /**
     * Constructs a new Timer instance and initializes it with the given progress display function.
     * The timer starts running immediately after creation.
     *
     * @param showProgress a {@code Consumer<String>} function that consumes progress messages for display or logging purposes.
     */
    public Timer(Consumer<String> showProgress) {
        this.showProgress = showProgress;
        Supplier<String> f = () -> "create new timer";
        doTrace(f);
        resume();
    }

    /**
     * Constructs a new Timer instance using the specified configuration.
     * The Timer is initialized with the progress display function derived from the configuration.
     *
     * @param config the configuration object that provides the settings used to initialize the Timer.
     *               Includes whether to show progress via a {@code "timer.showprogress"} boolean key.
     */
    public Timer(Config config) {
        this(progressFunction(config.getBoolean("timer", "showprogress")));
    }

    /**
     * Executes a single iteration of a timed operation, optionally performing pre-processing and post-processing
     * functions, and updates status based on the iteration progress.
     *
     * @param n            the total number of iterations to be performed.
     * @param warmup       indicates if the operation is in the warmup phase (true) or actual execution (false).
     * @param supplier     a supplier function that provides the initial input value for the operation.
     * @param function     the main function to be timed, which processes the input value.
     * @param preFunction  an optional function applied to preprocess the input before timing (may be null).
     * @param postFunction an optional function applied to post-process the result after timing (may be null).
     * @param lastx        the previous state value used for status updates.
     * @param i            the current iteration index, used for progress calculations.
     * @return the updated lastx value after status update.
     */
    private <T, U> int doRepeatForIteration(int n, boolean warmup, Supplier<T> supplier, Function<T, U> function, UnaryOperator<T> preFunction, Consumer<U> postFunction, int lastx, int i) {
        // TO BE IMPLEMENTED : note that the timer should be paused when this method is invoked. You may use doPrintStatus to show progress (but optional).
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * Updates the status display by printing progress markers or a decrement value based on the input parameters.
     * Used for visual feedback during processes that involve incremental progress.
     *
     * @param lastx the previous state or value of x being tracked.
     * @param x     the current state or value of x being tracked; must remain constant within this method's execution.
     * @return the updated current value of x.
     */
    private int doPrintStatus(int lastx, final int x) {
        if (x != lastx) {
            if (x % 10 == 0)
                showProgress.accept("" + (10 - x / 10));
            else
                showProgress.accept(".");
        }
        return x;
    }

    /**
     * Logs a trace message if the specified condition is true. The message is generated lazily
     * using the provided {@code Supplier<String>} function.
     *
     * @param condition       a boolean specifying whether to log the trace message.
     * @param messageFunction a {@code Supplier<String>} that provides the trace message to log.
     */
    private void doTrace(final boolean condition, Supplier<String> messageFunction) {
        if (condition) logger.trace(messageFunction);
    }

    /**
     * Logs a trace message using the given message supplier (that's to say this method is lazy).
     *
     * @param f a {@code Supplier<String>} that provides the string message to be traced.
     */
    private void doTrace(Supplier<String> f) {
        doTrace(true, f);
    }

    private long ticks = 0L;
    private int laps = 0;
    private boolean running = false;

    /**
     * Retrieves the current number of ticks recorded by the Timer.
     * NOTE: Used by unit tests
     *
     * @return the number of ticks stored in the Timer.
     */
    private long getTicks() {
        return ticks;
    }

    /**
     * Retrieves the current number of laps recorded by the Timer.
     * NOTE: Used by unit tests
     *
     * @return the number of laps stored in the Timer.
     */
    private int getLaps() {
        return laps;
    }

    /**
     * Determines if the Timer is currently running.
     * NOTE: Used by unit tests
     *
     * @return true if the Timer is running, false otherwise.
     */
    private boolean isRunning() {
        return running;
    }

    private final Consumer<String> showProgress;

    /**
     * Get the number of ticks from the system clock.
     * <p>
     * NOTE: (Maintain consistency) There are two system methods for getting the clock time.
     * Ensure that this method is consistent with toMillisecs.
     *
     * @return the number of ticks for the system clock. Currently defined as nano time.
     */
    private static long getClock() {
        // TO BE IMPLEMENTED 
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    static Consumer<String> progressFunction(boolean showProgress) {
        return showProgress ? System.out::print : (s) -> {
        };
    }

    /**
     * NOTE: (Maintain consistency) There are two system methods for getting the clock time.
     * Ensure that this method is consistent with getTicks.
     *
     * @param ticks the number of clock ticks -- currently in nanoseconds.
     * @return the corresponding number of milliseconds.
     */
    private static double toMillisecs(long ticks) {
        // TO BE IMPLEMENTED 
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * TimerException is a custom unchecked exception used to indicate errors or invalid states
     * specifically related to operations on the Timer class.
     * <p>
     * This exception is thrown by methods in the Timer class when an operation is
     * performed under circumstances that violate expected behavior, such as attempting
     * to stop a timer that is not running or resuming a timer that is already active.
     */
    static class TimerException extends RuntimeException {
        public TimerException() {
        }

        public TimerException(String message) {
            super(message);
        }

        public TimerException(String message, Throwable cause) {
            super(message, cause);
        }

        public TimerException(Throwable cause) {
            super(cause);
        }
    }
}
