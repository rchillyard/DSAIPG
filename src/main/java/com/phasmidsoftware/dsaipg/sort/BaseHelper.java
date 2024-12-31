/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.StatPack;
import com.phasmidsoftware.dsaipg.util.Utilities;

import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;

public abstract class BaseHelper<X> {
    abstract public boolean instrumented();

    /**
     * CONSIDER Change this to protected.
     */
    public final Instrument instrumenter;
    protected final String description;
    protected final Random random;
    protected final Config config;
    protected final int cutoff;
    /**
     * Keep track of the random array that was generated. This is available via the InstrumentedHelper class.
     */
    protected X[] randomArray;
    protected int n;

    public BaseHelper(String description, Random random, Instrument instrumenter, Config config, int n) {
        this.description = description;
        this.random = random;
        this.instrumenter = instrumenter;
        this.config = config;
        this.n = n;
        this.cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
    }

    @Override
    public String toString() {
        // CONSIDER swapping order of description and Helper for... (see also overrides)
        return "Helper for " + description + " with " + n + " elements" + (instrumented() ? " instrumented" : "");
    }

    public String getDescription() {
        return description;
    }

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

    public void init(int n, int nRuns) {
        instrumenter.init(n, nRuns);
    }

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

    public void close() {
    }

    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    public StatPack getStatPack() {
        return instrumenter.getStatPack();
    }

    public long getCompares() {
        return instrumenter.getCompares();
    }

    public long getSwaps() {
        return instrumenter.getSwaps();
    }

    public long getFixes() {
        return instrumenter.getFixes();
    }

    public void incrementCopies(int n) {
        instrumenter.incrementCopies(n);
    }

    public void incrementHits(long n) {
        instrumenter.incrementHits(n);
    }

    public void incrementLookups() {
        instrumenter.incrementLookups();
    }

    /**
     * If instrumenting, increment the number of fixes by n.
     *
     * @param n the number of copies made.
     */
    public void incrementFixes(int n) {
        instrumenter.incrementFixes(n);
    }

    public void incrementCompares() {
        instrumenter.incrementCompares();
    }

    public void incrementSwaps(int n) {
        instrumenter.incrementSwaps(n);
    }

    public long getHits() {
        return instrumenter.getHits();
    }

    public long getLookups() {
        return instrumenter.getLookups();
    }

    public long getCopies() {
        return instrumenter.getCopies();
    }

    public boolean countFixes() {
        return instrumenter.countFixes();
    }

    public void gatherStatistic() {
        instrumenter.gatherStatistic();
    }

    public boolean isShowStats() {
        return instrumenter.isShowStats();
    }

    public static final String INSTRUMENT = "instrument";
}