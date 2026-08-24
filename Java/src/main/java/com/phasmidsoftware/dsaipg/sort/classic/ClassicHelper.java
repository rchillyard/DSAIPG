package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.helper.*;
import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.Utilities;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.HELPER;

/**
 * The ClassicHelper class provides an implementation of the NonComparableHelper interface,
 * which in turn implements Helper, and is designed to assist sorting operations
 * and other utilities for non-comparable types, with support for instrumenting various actions like swaps, comparisons, and more.
 * This class uses an external comparator for comparing elements.
 *
 * @param <X> the type of elements managed by this helper class.
 */
public class ClassicHelper<X> implements NonComparableHelper<X> {

    /**
     * Compares two objects of type X using the specified comparison logic.
     *
     * @param x1 the first object to compare.
     * @param x2 the second object to compare.
     * @return a negative integer, zero, or a positive integer depending on whether the first object
     *         is less than, equal to, or greater than the second object.
     */
    public int compare(X x1, X x2) {
        return pureComparison(x1, x2);
    }

    /**
     * Use the comparator field to do a comparison of x1 and x2.
     *
     * @param x1 the first X value.
     * @param x2 the second X value.
     * @return comparator.compare(x1, x2).
     */
    public int pureComparison(X x1, X x2) {
        if (comparator != null) return comparator.compare(x1, x2);
        else throw new RuntimeException("ClassicHelper: comparator has not been set");
    }

    /**
     * Generates an array of random elements of the specified type using the provided function.
     *
     * @param m      the number of random elements to generate. Must be greater than zero.
     * @param clazz  the class type of the elements to be generated.
     * @param f      a function that takes a {@code Random} object and generates an element of type {@code X}.
     * @return an array of randomly generated elements of type {@code X}.
     * @throws HelperException if the requested number of elements is less than or equal to zero.
     */
    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = null;
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    /**
     * Initializes the instance with the given value.
     *
     * @param n the number of elements or value to initialize the instance with
     */
    public void init(int n) {
        this.n = n;
    }

    /**
     * Retrieves the value of the field 'n'.
     *
     * @return the integer value of the field 'n'.
     */
    public int getN() {
        return n;
    }

    /**
     * Closes any resources or finalizes operations associated with this ClassicHelper instance.
     * This method should be called to release any open resources or allocated memory
     * to ensure proper cleanup or shutdown of the helper when it is no longer needed.
     */
    public void close() {
    }

    /**
     * Returns a string representation of this helper, including its description
     * and the number of elements it contains.
     *
     * @return a textual representation of the helper, specifying the description
     *         and the count of elements.
     */

    public String toString() {
        return "Helper for " + description + " with " + n + " elements";
    }

    /**
     * Retrieves the description of this instance.
     *
     * @return the description of this instance as a String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the current configuration for this ClassicHelper instance.
     *
     * @return the configuration object associated with this instance.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Creates a new cloned instance of {@code Helper<X>} with the specified description and number of elements,
     * incorporating the original comparator for comparison logic.
     *
     * @param description       the description for the cloned helper instance.
     * @param N                 the number of elements to initialize in the cloned helper instance.
     * @param shareInstrumenter
     * @return a new {@code Helper<X>} instance that is a clone of the current object with the given description and element count.
     */
    public Helper<X> clone(String description, int N, boolean shareInstrumenter) {
        return clone(description, comparator, N, shareInstrumenter);
    }

    /**
     * Creates and returns a new instance of ClassicHelper with the specified parameters.
     *
     * @param description       a description of the helper instance, primarily for identification or documentation purposes.
     * @param comparator        a Comparator used to compare objects of type X within this helper instance.
     * @param N                 the number of elements or value expected to be managed by the helper instance.
     * @param shareInstrumenter
     * @return a new instance of ClassicHelper initialized with the given description, comparator, and N value.
     */

    public Helper<X> clone(String description, Comparator<X> comparator, int N, boolean shareInstrumenter) {
        return new ClassicHelper<>(description, comparator, N, random, config);
    }

    /**
     * Retrieves the comparator associated with this instance.
     *
     * @return the Comparator of type X used for comparison operations.
     */
    public Comparator<X> getComparator() {
        return comparator;
    }

    /**
     * Retrieves the number of lookups performed by the instrumenter.
     *
     * @return the total count of lookups as a long value.
     */
    public long getLookups() {
        return instrumenter.getLookups();
    }

    /**
     * Increments the count of lookups performed by the instrumenter by the specified value.
     * This method is used to track and record the number of lookup operations,
     * which can be useful for performance measurement or analysis.
     *
     * @param n the number of lookups to increment. Must be a non-negative value.
     */
    public void incrementLookups(long n) {
        instrumenter.incrementLookups(n);
    }

    /**
     * Initializes the helper instance with the specified number of elements and runs.
     * Delegates the initialization process to the associated instrumenter.
     *
     * @param n      the number of elements to initialize.
     * @param nRuns  the number of runs or iterations for which the helper is being initialized.
     */
    public void init(int n, int nRuns) {
        instrumenter.init(n, nRuns);
    }

    /**
     * Retrieves the StatPack object associated with this instance.
     *
     * @return a StatPack object containing statistical data and metrics gathered by the instrumenter.
     */
    public StatPack getStatPack() {
        return instrumenter.getStatPack();
    }

    /**
     * Retrieves the total number of hit operations recorded by the instrumenter.
     *
     * @return the total count of hits as a long value.
     */
    public long getHits() {
        return instrumenter.getHits();
    }

    /**
     * Retrieves the number of copy operations performed by the instrumenter.
     *
     * @return the total count of copy operations as a long value.
     */
    public long getCopies() {
        return instrumenter.getCopies();
    }

    /**
     * Retrieves the number of comparison operations performed by the instrumenter.
     *
     * @return the total count of comparisons as a long value.
     */
    public long getCompares() {
        return instrumenter.getCompares();
    }

    /**
     * Retrieves the total number of swaps performed.
     *
     * @return the count of swaps as a long value.
     */
    public long getSwaps() {
        return instrumenter.getSwaps();
    }

    /**
     * Retrieves the number of fixes performed by the instrumenter.
     *
     * @return the total count of fixes as a long value.
     */
    public long getFixes() {
        return instrumenter.getFixes();
    }

    /**
     * Increments the number of copy operations recorded by the internal instrumenter by the specified amount.
     *
     * @param n the number of copies to increment. Must be a positive integer.
     */
    public void incrementCopies(int n) {
        instrumenter.incrementCopies(n);
    }

    /**
     * Increments the hit count for the associated instrumenter by the specified value.
     * This method delegates the operation to the {@code instrumenter}'s {@code incrementHits(long n)} method.
     *
     * @param n the value by which the hit count should be increased
     */
    public void incrementHits(long n) {
        instrumenter.incrementHits(n);
    }

    /**
     * Increments the count of fixes by the specified value.
     *
     * @param n the number of fixes to increment. This value is passed to the instrumenter to update its internal fix count.
     */
    public void incrementFixes(int n) {
        instrumenter.incrementFixes(n);
    }

    /**
     * Increments the internal counter tracking the number of comparison operations.
     * This method delegates the increment operation to the {@code incrementCompares} method
     * of the associated {@code instrumenter} instance.
     */
    public void incrementCompares() {
        instrumenter.incrementCompares();
    }

    /**
     * Increments the swap count by the specified number using the instrumenter.
     *
     * @param n the number of swaps to increment. Must be a non-negative integer.
     */
    public void incrementSwaps(int n) {
        instrumenter.incrementSwaps(n);
    }

    /**
     * Determines whether the count of fixes should be considered or accounted for within the helper instance.
     *
     * @return a boolean value indicating whether the fixes are being counted.
     */
    public boolean countFixes() {
        return false;
    }

    /**
     * Gathers statistical data by delegating the operation to the associated instrumenter.
     * This method facilitates the collection of statistics, such as counts of operations
     * or metrics, as defined and managed by the instrumenter instance.
     */
    public void gatherStatistic() {
        instrumenter.gatherStatistic();
    }

    /**
     * Determines whether statistics should be displayed for this instance.
     *
     * @return true if statistics are enabled to be shown, false otherwise.
     */
    public boolean isShowStats() {
        return false;
    }

    /**
     * Performs any necessary post-processing on the provided array of elements.
     * This implementation does nothing by default and can be overridden as needed.
     *
     * @param xs an array of elements of type X to be post-processed
     */
    @Override
    public void postProcess(X[] xs) {
        NonComparableHelper.super.postProcess(xs);
        if (checkSorted && !isSorted(xs))
            throw new NonInstrumentingComparatorHelper.HelperException("ClassicHelper.postProcess: array is not sorted");
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description the description of this Helper (for humans).
     * @param comparator  a comparator for comparing X values.
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random      a random number generator.
     */
    public ClassicHelper(String description, Comparator<X> comparator, int n, Random random, Config config) {
        this.n = n;
        this.description = description;
        this.random = random;
        this.config = config;
        this.comparator = comparator;
        this.instrumenter = new InstrumenterDummy();
        checkSorted = config.getBoolean(HELPER, "checksorted");
    }

    protected X[] randomArray;

    private final String description;
    private final Random random;
    private final Config config;
    private final Comparator<X> comparator;
    private int n;
    private final Instrument instrumenter;
    private final boolean checkSorted;
}
