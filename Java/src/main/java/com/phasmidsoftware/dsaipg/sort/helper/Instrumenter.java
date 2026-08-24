package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.benchmark.Statistics;
import com.phasmidsoftware.dsaipg.util.config.Config;

/**
 * The Instrumenter class implements the Instrument interface and is used
 * to record and analyze specific metrics during the execution of algorithms,
 * such as the number of copies, swaps, comparisons, fixes, hits, and lookups.
 */
public class Instrumenter implements Instrument {

    /**
     * Initializes the instrumenter with a specific size and number of runs, and
     * sets up necessary statistics tracking if not already initialized.
     *
     * @param n     the size parameter, typically representing the size of the dataset being tested.
     * @param nRuns the number of runs for which statistics will be gathered.
     */
    public void init(int n, int nRuns) {
        resetCounters();
        // NOTE: it's an error to reset the StatPack if we've been here before
        if (statPack != null) return;
        statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, nRuns, n, COMPARES, SWAPS, COPIES, INVERSIONS, FIXES, HITS, LOOKUPS);
    }

    /**
     * Retrieves the {@code StatPack} object associated with this instrumenter.
     * The {@code StatPack} contains statistical tracking data for various operations
     * performed during the instrumentation process, such as counts of certain events
     * or measurements of performance metrics.
     *
     * @return the {@code StatPack} instance containing the tracked statistical data.
     */
    public StatPack getStatPack() {
        return statPack!=null ? statPack : StatPack.EMPTY;
    }

    /**
     * Retrieves the current value of the hits counter.
     * The hits counter typically tracks the number of array accesses
     * that may be performed during instrumentation.
     *
     * @return the number of hits recorded as a long.
     */
    public long getHits() {
        return hits;
    }

    /**
     * Retrieves the number of lookups counted by the instrumenter.
     *
     * @return the total number of lookups recorded.
     */
    public long getLookups() {
        return lookups;
    }

    /**
     * Retrieves the current count of copy operations recorded by the instrumenter.
     *
     * @return the number of copies performed as a long value.
     */
    public long getCopies() {
        return copies;
    }

    /**
     * Retrieves the current count of comparisons made during instrumentation.
     * The value represents the number of operations where elements have been compared.
     *
     * @return the total number of comparisons made.
     */
    public long getCompares() {
        return compares;
    }

    /**
     * Retrieves the total number of swaps that have been recorded by the instrumenter.
     *
     * @return the number of swaps performed as a long value.
     */
    public long getSwaps() {
        return swaps;
    }

    /**
     * Retrieves the count of fixes recorded by the instrumenter.
     *
     * @return the total number of fixes tracked during the instrumentation process.
     */
    public long getFixes() {
        return fixes;
    }

    /**
     * If instrumenting, increment the number of copies by n.
     *
     * @param n the number of copies made.
     */
    public void incrementCopies(int n) {
        if (countCopies) copies += n;
    }

    /**
     * Method to keep track of hits (array accesses that MAY not be in cache)...
     * but only if instrumenting.
     *
     * @param n the number of hits.
     */
    public void incrementHits(long n) {
        if (countHits) hits += n;
    }

    /**
     * Method to keep track of hits (array accesses that MAY not be in cache)...
     * but only if instrumenting.
     */
    public void incrementLookups(long n) {
        if (countLookups) lookups += n;
    }

    /**
     * If instrumenting, increment the number of fixes by n.
     *
     * @param n the number of copies made.
     */
    public void incrementFixes(int n) {
        if (countFixes) fixes += n;
    }

    /**
     * Increments the comparison counter if comparison counting is enabled.
     * This method checks if the `countCompares` flag is true, and if so,
     * increases the `compares` counter by one. It is used for tracking
     * the number of comparisons made during certain operations.
     */
    public void incrementCompares() {
        if (countCompares)
            compares++;
    }

    /**
     * Increments the number of swaps by the specified value if the instrumenter is configured to count swaps.
     *
     * @param n the number of swaps to increment by.
     */
    public void incrementSwaps(int n) {
        if (countSwaps)
            swaps += n;
    }

    public void gatherStatistic() {
        if (getStatPack() == null)
            throw new HelperException("InstrumentedComparableHelper.postProcess: no StatPack");
        if (getStatPack().isInvalid()) return;
        if (countCompares)
            getStatPack().add(COMPARES, getCompares());
        if (countSwaps)
            getStatPack().add(SWAPS, getSwaps());
        if (countCopies)
            getStatPack().add(COPIES, getCopies());
        if (countFixes)
            getStatPack().add(FIXES, getFixes());
        if (countHits)
            getStatPack().add(HITS, getHits());
        if (countLookups)
            getStatPack().add(LOOKUPS, getLookups());
        resetCounters();
    }

    /**
     * Determines whether fixes should be counted during the instrumentation process.
     *
     * @return true if fixes are being counted, false otherwise.
     */
    public boolean countFixes() {
        return countFixes;
    }

    /**
     * Determines whether statistics display is enabled.
     *
     * @return true if the display of statistics is enabled; false otherwise.
     */
    public boolean isShowStats() {
        return showStats;
    }

    /**
     * Constructs an Instrumenter instance with the specified options for tracking counts and statistics display.
     *
     * @param countCopies   whether to count the number of copies during instrumentation.
     * @param countSwaps    whether to count the number of swaps during instrumentation.
     * @param countCompares whether to count the number of comparisons during instrumentation.
     * @param countFixes    whether to count the number of fixes during instrumentation.
     * @param countHits     whether to count array hits during instrumentation.
     * @param countLookups  whether to count lookups during instrumentation.
     * @param showStats     whether to display collected statistics.
     */
    public Instrumenter(boolean countCopies, boolean countSwaps, boolean countCompares, boolean countFixes, boolean countHits, boolean countLookups, boolean showStats) {
        this.countCopies = countCopies;
        this.countSwaps = countSwaps;
        this.countCompares = countCompares;
        this.countFixes = countFixes;
        this.countHits = countHits;
        this.countLookups = countLookups;
        this.showStats = showStats;
    }

    /**
     * Constructs an Instrumenter instance configured using the provided Config object.
     *
     * @param config the Config object containing settings for toggling specific instrumentation features, such as counting copies, swaps, compares, fixes, hits, lookups, and whether
     *               to show statistics.
     */
    public Instrumenter(Config config) {
        this(config.getBoolean(INSTRUMENTING, COPIES), config.getBoolean(INSTRUMENTING, SWAPS), config.getBoolean(INSTRUMENTING, COMPARES), config.getBoolean(INSTRUMENTING, FIXES), config.getBoolean(INSTRUMENTING, HITS), config.getBoolean(INSTRUMENTING, LOOKUPS), config.getBoolean(INSTRUMENTING, SHOW_STATS));
    }

    @Override
    public String toString() {
        return "Instrumenter{" +
                "compares=" + compares +
                ", copies=" + copies +
                ", fixes=" + fixes +
                ", hits=" + hits +
                ", lookups=" + lookups +
                ", swaps=" + swaps +
                '}';
    }

    private void resetCounters() {
        compares = 0;
        swaps = 0;
        copies = 0;
        fixes = 0;
        hits = 0;
        lookups = 0;
    }

    public StatPack statPack;
    public final boolean countCopies;
    public final boolean countSwaps;
    public final boolean countCompares;
    public final boolean countFixes;
    public final boolean countHits;
    public final boolean countLookups;
    public final boolean showStats;
    public long compares = 0;
    public long swaps = 0;
    public long copies = 0;
    public long fixes = 0;
    public long hits = 0;
    public long lookups = 0;
}