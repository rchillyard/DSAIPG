/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.*;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.StatPack;
import com.phasmidsoftware.dsaipg.util.Utilities;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

public class ClassicHelper<X> implements NonComparableHelper<X> {

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

    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = null;
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    public void init(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public void close() {
    }

    @Override
    public String toString() {
        return "Helper for " + description + " with " + n + " elements";
    }

    public String getDescription() {
        return description;
    }

    public Config getConfig() {
        return config;
    }

    public Helper<X> clone(String description, int N) {
        return clone(description, comparator, N);
    }

    @Override
    public Helper<X> clone(String description, Comparator<X> comparator, int N) {
        return new ClassicHelper<>(description, comparator, N, random, config);
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
    }

    private final String description;
    private final Random random;
    private final Config config;

    public Comparator<X> getComparator() {
        return comparator;
    }

    private final Comparator<X> comparator;
    private int n;

    @Override
    public long getLookups() {
        return instrumenter.getLookups();
    }

    @Override
    public void incrementLookups() {
        instrumenter.incrementLookups();
    }

    private final Instrument instrumenter;

    protected X[] randomArray;

    @Override
    public void init(int n, int nRuns) {
        instrumenter.init(n, nRuns);
    }

    @Override
    public StatPack getStatPack() {
        return instrumenter.getStatPack();
    }

    @Override
    public long getHits() {
        return instrumenter.getHits();
    }


    @Override
    public long getCopies() {
        return instrumenter.getCopies();
    }

    @Override
    public long getCompares() {
        return instrumenter.getCompares();
    }

    @Override
    public long getSwaps() {
        return instrumenter.getSwaps();
    }

    @Override
    public long getFixes() {
        return instrumenter.getFixes();
    }

    @Override
    public void incrementCopies(int n) {
        instrumenter.incrementCopies(n);
    }

    @Override
    public void incrementHits(long n) {
        instrumenter.incrementHits(n);
    }

    @Override
    public void incrementFixes(int n) {
        instrumenter.incrementFixes(n);
    }

    @Override
    public void incrementCompares() {
        instrumenter.incrementCompares();
    }

    @Override
    public void incrementSwaps(int n) {
        instrumenter.incrementSwaps(n);
    }

    @Override
    public boolean countFixes() {
        return false;
    }

    @Override
    public void gatherStatistic() {
        instrumenter.gatherStatistic();
    }

    @Override
    public boolean isShowStats() {
        return false;
    }
}