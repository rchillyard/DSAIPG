/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.StatPack;
import com.phasmidsoftware.dsaipg.util.Statistics;

public class Instrumenter implements Instrument {

    public Instrumenter(boolean countCopies, boolean countSwaps, boolean countCompares, boolean countFixes, boolean countHits, boolean countLookups, boolean showStats) {
        this.countCopies = countCopies;
        this.countSwaps = countSwaps;
        this.countCompares = countCompares;
        this.countFixes = countFixes;
        this.countHits = countHits;
        this.countLookups = countLookups;
        this.showStats = showStats;
    }

    public Instrumenter(Config config) {
        this(config.getBoolean(INSTRUMENTING, COPIES), config.getBoolean(INSTRUMENTING, SWAPS), config.getBoolean(INSTRUMENTING, COMPARES), config.getBoolean(INSTRUMENTING, FIXES), config.getBoolean(INSTRUMENTING, HITS), config.getBoolean(INSTRUMENTING, LOOKUPS), config.getBoolean(INSTRUMENTING, SHOW_STATS));
    }

    public void init(int n, int nRuns) {
        resetCounters();
        // NOTE: it's an error to reset the StatPack if we've been here before
        if (statPack != null) return;
        statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, nRuns, n, COMPARES, SWAPS, COPIES, INVERSIONS, FIXES, HITS, LOOKUPS);
    }

    private void resetCounters() {
        compares = 0;
        swaps = 0;
        copies = 0;
        fixes = 0;
        hits = 0;
        lookups = 0;
    }

    public final boolean countCopies;
    public final boolean countSwaps;
    public final boolean countCompares;
    public final boolean countFixes;
    public final boolean countHits;
    public final boolean countLookups;
    public final boolean showStats;
    public StatPack statPack;
    public long compares = 0;
    public long swaps = 0;
    public long copies = 0;
    public long fixes = 0;
    public long hits = 0;
    public long lookups = 0;

    public StatPack getStatPack() {
        return statPack;
    }

    public long getHits() {
        return hits;
    }

    public long getLookups() {
        return lookups;
    }

    public long getCopies() {
        return copies;
    }

    public long getCompares() {
        return compares;
    }

    public long getSwaps() {
        return swaps;
    }

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
    public void incrementLookups() {
        if (countLookups) lookups++;
    }

    /**
     * If instrumenting, increment the number of fixes by n.
     *
     * @param n the number of copies made.
     */
    public void incrementFixes(int n) {
        if (countFixes) fixes += n;
    }

    public void incrementCompares() {
        if (countCompares)
            compares++;
    }

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

    public boolean countFixes() {
        return countFixes;
    }

    public boolean isShowStats() {
        return showStats;
    }
}