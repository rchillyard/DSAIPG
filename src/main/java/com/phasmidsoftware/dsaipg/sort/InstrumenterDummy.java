/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.StatPack;

public class InstrumenterDummy implements Instrument {
    public InstrumenterDummy() {
    }

    public InstrumenterDummy(Config config) {
        this();
    }

    public void init(int n, int nRuns) {
    }

    public StatPack getStatPack() {
        return null;
    }

    public long getCompares() {
        return 0;
    }

    public long getSwaps() {
        return 0;
    }

    public long getFixes() {
        return 0;
    }

    /**
     * If instrumenting, increment the number of copies by n.
     *
     * @param n the number of copies made.
     */
    public void incrementCopies(int n) {
    }

    /**
     * Method to keep track of hits (array accesses that MAY not be in cache)...
     * but only if instrumenting.
     *
     * @param n the number of hits.
     */
    public void incrementHits(long n) {
    }

    /**
     * If instrumenting, increment the number of fixes by n.
     *
     * @param n the number of copies made.
     */
    public void incrementFixes(int n) {
    }

    public void incrementCompares() {
    }

    public void incrementSwaps(int n) {
    }

    public long getHits() {
        return 0;
    }

    public long getCopies() {
        return 0;
    }

    public boolean countFixes() {
        return false;
    }

    @Override
    public void gatherStatistic() {
    }

    @Override
    public boolean isShowStats() {
        return false;
    }

    @Override
    public long getLookups() {
        return 0;
    }

    @Override
    public void incrementLookups() {

    }
}