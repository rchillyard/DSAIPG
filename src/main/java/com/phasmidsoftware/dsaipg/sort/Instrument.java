/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.StatPack;

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

    String INSTRUMENTING = "instrumenting";
    String SHOW_STATS = "showStats";

    void init(int n, int nRuns);

    StatPack getStatPack();

    long getCompares();

    long getSwaps();

    long getFixes();

    long getHits();

    long getLookups();

    long getCopies();

    void incrementCopies(int n);

    void incrementHits(long n);

    void incrementLookups();

    void incrementFixes(int n);

    void incrementCompares();

    void incrementSwaps(int n);

    boolean countFixes();

    void gatherStatistic();

    boolean isShowStats();
}