package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;

/**
 * Interface to define the operations of instrumentation.
 */
public interface Instrument {
    /**
     * In the swap operation, two elements of the same array are exchanged.
     * There will never be any lookups incurred, but there may be hits (usually, there are).
     * A swap fixes some number (one or more) of inversions.
     */
    String SWAPS = "swaps";

    /**
     * A compare operation compares two values, either (or both) of which may be the result of a hit.
     * Additionally, either value may incur a lookup (unless it is a primitive value).
     * In such cases, there may be additional lookups required, but we don't attempt to count those here.
     */
    String COMPARES = "compares";

    /**
     * A copy is just what it sounds like. The copying of an element from one array to another.
     * Some swaps involve copying, but we don't currently track such swaps.
     */
    String COPIES = "copies";

    /**
     * An inversion is the property of an input array: whereby two elements are out of order.
     * The usual convention is that the smaller element of a pair should be on the left.
     */
    String INVERSIONS = "inversions";

    /**
     * A fix is a single inversion that is no longer inverted.
     * A swap may fix one or more inversions.
     */
    String FIXES = "fixes";

    /**
     * A hit is the approximation of an (amortized) cache page fault when accessing an array element.
     * The larger the array and the smaller the cache, the more likely a random array access will cause a page fault.
     * But we can't really take account of all that in this program, so we just keep track of every time an array element is accessed.
     */
    String HITS = "hits";

    /**
     * A lookup is the approximation of an (amortized) cache page fault when accessing an object on the heap.
     * A lookup is much more likely to incur a cache page fault than an array access (especially if the array is accessed sequentially).
     * Thus hits and lookups both contribute to the overall time of an algorithm but it is to be expected that a lookup requires more time on average than a hit.
     * A lookup will typically occur only for a comparison or for a classification.
     */
    String LOOKUPS = "lookups";

    /**
     * A constant string indicating that the system is currently in an instrumenting mode.
     * <p>
     * The "instrumenting" state typically refers to a mode in which performance metrics or
     * operational statistics, such as swaps, comparisons, or fixes, are tracked and recorded.
     */
    String INSTRUMENTING = "instrumenting";
    /**
     * A constant used to represent the key or identifier for enabling or displaying
     * statistical data related to performance metrics.
     *
     * This variable is typically used to determine whether the statistics, such as swaps,
     * comparisons, copies, and others, should be shown or processed within the context
     * of the instrument's operations.
     */
    String SHOW_STATS = "showStats";

    /**
     * Initializes the instrument with the specified number of elements and runs.
     *
     * @param n       the number of elements to initialize
     * @param nRuns   the number of runs to perform
     */
    void init(int n, int nRuns);

    /**
     * Retrieves the StatPack instance containing statistical data.
     *
     * @return a StatPack object encapsulating various statistics such as swaps, compares,
     *         copies, and other tracked metrics.
     */
    StatPack getStatPack();

    /**
     * Retrieves the number of compare operations performed.
     *
     * @return the total number of compare operations as a long value
     */
    long getCompares();

    /**
     * Retrieves the total number of swap operations recorded.
     *
     * @return The total count of swaps performed.
     */
    long getSwaps();

    /**
     * Returns the total count of "fixes" that have been recorded.
     *
     * A "fix" typically represents a specific corrective action
     * or operation counted during the execution of a process.
     *
     * @return the total number of fixes.
     */
    long getFixes();

    /**
     * Retrieves the current count of "hits" recorded by the instrument.
     *
     * @return the total number of hits as a long value
     */
    long getHits();

    /**
     * Retrieves the total number of lookup operations recorded.
     *
     * @return the number of lookups as a long value
     */
    long getLookups();

    /**
     * Retrieves the number of copies performed by the instrument.
     *
     * @return the total number of copies as a long value.
     */
    long getCopies();

    /**
     * Increments the count of copies by the specified value.
     *
     * @param n the number by which to increase the copy count
     */
    void incrementCopies(int n);

    /**
     * Increments the hit count by a specified value.
     *
     * @param n the number by which to increase the hit count
     */
    void incrementHits(long n);

    /**
     * Increments the count of lookup operations by the specified value.
     *
     * @param n the number to increase the lookup count; must be a non-negative value
     */
    void incrementLookups(long n);

    /**
     * Increments the count of fixes by the specified amount.
     *
     * @param n the number of fixes to increment; must be a positive integer
     */
    void incrementFixes(int n);

    /**
     * Increments the count of comparison operations performed during the execution
     * of an algorithm or process. This method is used for tracking and recording
     * the number of comparisons made, which can be helpful for performance
     * measurement and analysis.
     */
    void incrementCompares();

    /**
     * Increments the swap count by the specified value.
     *
     * @param n the number of swaps to increment; must be a positive integer
     */
    void incrementSwaps(int n);

    /**
     * Determines whether the system should count the number of fixes during its operation.
     *
     * @return true if counting fixes is enabled, otherwise false
     */
    boolean countFixes();

    /**
     * Collects and records statistical data related to various operations
     * such as swaps, comparisons, copies, inversions, fixes, hits, and lookups.
     * This method is typically used to aggregate and store the current metrics
     * for later analysis or reporting.
     */
    void gatherStatistic();

    /**
     * Determines if statistics should be shown.
     *
     * @return true if statistics are enabled to be displayed; false otherwise.
     */
    boolean isShowStats();
}
