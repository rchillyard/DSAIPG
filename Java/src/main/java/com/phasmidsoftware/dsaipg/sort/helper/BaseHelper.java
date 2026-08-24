package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.Utilities;

import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.*;

/**
 * The BaseHelper abstract class provides utility methods and configuration options
 * for managing and instrumenting operations related to a collection of elements of type X.
 * It is designed to track various performance metrics such as comparisons, swaps, and other
 * operations through instrumentation. Subclasses are expected to implement specific behavior.
 *
 * @param <X> the type of elements managed by this helper.
 */
public abstract class BaseHelper<X> implements Helper<X> {
    /**
     * Determines if instrumentation is enabled for this helper.
     *
     * @return true if instrumentation is enabled, false otherwise.
     */
    abstract public boolean instrumented();

    /**
     * Retrieves the description associated with the helper instance.
     *
     * @return a string containing the description of the helper.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the configuration object associated with this helper instance.
     *
     * @return the configuration object of type Config.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @param n the size to be managed.
     * @throws HelperException if n is inconsistent.
     */
    public void init(int n) {
        if (this.n == 0 || this.n == n) this.n = n;
        else throw new HelperException("Helper: n is already set to a different value");
    }

    /**
     * Initializes the helper instance with a specified size and number of runs.
     * Delegates the initialization process to the associated instrumenter.
     *
     * @param n     the size to be managed by the helper
     * @param nRuns the number of runs for initialization
     */
    public void init(int n, int nRuns) {
        instrumenter.init(n, nRuns);
    }

    /**
     * Retrieves the value of the variable 'n'.
     *
     * @return the integer value of the field 'n'.
     */
    public int getN() {
        return n;
    }

    /**
     * Get the configured cutoff value.
     *
     * @return a value for cutoff.
     */
    public int cutoff() {
        // NOTE that a cutoff value of 0 or less will result in an infinite recursion for any recursive method that uses it.
        return (cutoff >= 1) ? cutoff : CUTOFF_DEFAULT;
    }

    /**
     * Closes and performs necessary cleanup for this helper instance.
     * Typically used to release any resources or finalize any operations
     * associated with the helper. The exact behavior depends on the
     * implementation details of the containing class and its fields.
     */
    public void close() {
    }

    /**
     * Generates an array of random elements of type X using a specified function and class type.
     *
     * @param m     the number of random elements to generate. Must be greater than 0.
     * @param clazz the class type of the elements to generate.
     * @param f     a function that accepts a Random object and generates an instance of type X.
     * @return an array of type X filled with randomly generated elements.
     * @throws HelperException if the requested number of elements (m) is less than or equal to 0.
     */
    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    /**
     * Retrieves the StatPack instance containing various collected statistical data.
     *
     * @return a StatPack object providing access to statistical metrics such as swaps,
     * comparisons, copies, and other tracked operations.
     */
    public StatPack getStatPack() {
        return instrumenter.getStatPack();
    }

    /**
     * Retrieves the total number of comparison operations performed.
     * This method delegates the request to the associated instrumenter.
     *
     * @return the total count of comparison operations as a long value
     */
    public long getCompares() {
        return instrumenter.getCompares();
    }

    /**
     * Retrieves the total number of swap operations recorded by the instrumenter.
     *
     * @return the total count of swaps as a long value.
     */
    public long getSwaps() {
        return instrumenter.getSwaps();
    }

    /**
     * Retrieves the total number of "fixes" performed by the associated instrumenter.
     * <p>
     * A "fix" generally refers to a corrective operation tracked during algorithm execution.
     *
     * @return the total count of fixes as a long value.
     */
    public long getFixes() {
        return instrumenter.getFixes();
    }

    /**
     * Increments the copy count by delegating to the instrumenter.
     *
     * @param n the number of copies to increment
     */
    public void incrementCopies(int n) {
        instrumenter.incrementCopies(n);
    }

    /**
     * Increments the hit count by delegating to the associated instrumenter.
     * This method is used to track the number of "hits" recorded during the execution
     * of an algorithm or process, which aids in performance measurement and analysis.
     *
     * @param n the number by which to increase the hit count
     */
    public void incrementHits(long n) {
        instrumenter.incrementHits(n);
    }

    /**
     * Increments the count of lookups performed by delegating to the associated instrumenter.
     * This method is used to track the number of lookup operations for analysis
     * and performance measurement purposes.
     *
     * @param n the number of lookups to be counted.
     */
    public void incrementLookups(long n) {
        instrumenter.incrementLookups(n);
    }

    /**
     * If instrumenting, increment the number of fixes by n.
     *
     * @param n the number of copies made.
     */
    public void incrementFixes(int n) {
        instrumenter.incrementFixes(n);
    }

    /**
     * Increments the comparison count by delegating to the associated instrumenter.
     * This method is used to track the number of comparison operations performed during
     * the execution of an algorithm or process, facilitating performance measurement and analysis.
     */
    public void incrementCompares() {
        instrumenter.incrementCompares();
    }

    /**
     * Increments the swap count by the specified value using the instrumenter.
     *
     * @param n the number of swaps to increment
     */
    public void incrementSwaps(int n) {
        instrumenter.incrementSwaps(n);
    }

    /**
     * Retrieves the total number of "hits" recorded by the instrumenter.
     *
     * @return the total number of hits as a long value
     */
    public long getHits() {
        return instrumenter.getHits();
    }

    /**
     * Retrieves the total number of lookup operations performed.
     *
     * @return the total count of lookups as a long value
     */
    public long getLookups() {
        return instrumenter.getLookups();
    }

    /**
     * Retrieves the number of copies performed by the instrumenter associated with this helper.
     *
     * @return the total number of copies as a long value.
     */
    public long getCopies() {
        return instrumenter.getCopies();
    }

    /**
     * Determines whether the system should count the number of fixes during its operation.
     *
     * @return true if counting fixes is enabled, otherwise false
     */
    public boolean countFixes() {
        return instrumenter.countFixes();
    }

    /**
     * Collects and records statistical data related to various operations such as swaps, comparisons,
     * copies, inversions, fixes, hits, and lookups. This method delegates the task of gathering
     * statistics to the instrumenter associated with this helper instance.
     */
    public void gatherStatistic() {
        instrumenter.gatherStatistic();
    }

    /**
     * Determines if statistics display is enabled for this helper.
     *
     * @return true if statistics are enabled to be shown; false otherwise.
     */
    public boolean isShowStats() {
        return instrumenter.isShowStats();
    }

    @Override
    public String toString() {
        // CONSIDER swapping order of description and Helper for... (see also overrides)
        return "Helper for " + description + " with " + n + " elements" + (instrumented() ? " instrumented" : "");
    }

    public String showInterimStats(String context) {
        return description + "/" + context + ": " + instrumenter.toString();
    }

    /**
     * Constructor for the BaseHelper class.
     *
     * @param description  a short textual description of this helper instance
     * @param random       an instance of Random for generating random numbers
     * @param instrumenter an Instrument instance for capturing performance metrics
     * @param config       a configuration object containing settings such as cutoff values
     * @param n            an integer representing the size to be managed
     */
    public BaseHelper(String description, Random random, Instrument instrumenter, Config config, int n) {
        this.description = description;
        this.random = random;
        this.instrumenter = instrumenter;
        this.config = config;
        this.n = n;
        this.cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
    }

    /**
     * An instrument instance used for capturing and recording performance metrics of various operations
     * during the execution of algorithms or processes.
     * <p>
     * The instrumenter can track metrics such as swaps, comparisons, copies, hits, lookups, fixes, and inversions.
     * It provides statistical analysis and is primarily used for performance measurement and optimization.
     * <p>
     * This object is designed to integrate with the {@link BaseHelper} class to enable or disable
     * instrumentation dynamically and collect relevant statistical data for analyzing algorithm behavior.
     */
    public final Instrument instrumenter;
    /**
     * A brief, human-readable textual description of this helper instance.
     * Primarily used to describe the purpose or characteristics of the helper object.
     */
    protected final String description;
    /**
     * A randomly initialized {@code Random} instance used for generating random values.
     * This variable is declared final to ensure that each instance of the helper class
     * gets a distinct but constant random number generator throughout its lifecycle.
     * It is used by methods for generating randomized input or performing operations
     * that require randomization.
     */
    protected final Random random;
    /**
     * A configuration object containing settings and parameters
     * required for the operation of this helper instance.
     * The configuration is immutable and ensures consistent settings
     * during the execution of the program.
     */
    protected final Config config;
    /**
     * The cutoff value used to determine a threshold within the helper operations.
     * This threshold may influence decision-making processes such as when to switch
     * between different algorithms, techniques, or strategies during execution.
     * It is intended to be configured to optimize performance based on the specific use case.
     */
    protected final int cutoff;
    /**
     * Keep track of the random array that was generated. This is available via the InstrumentedHelper class.
     */
    protected X[] randomArray;
    protected int n;

    public static final String INSTRUMENT = "instrument";
}
