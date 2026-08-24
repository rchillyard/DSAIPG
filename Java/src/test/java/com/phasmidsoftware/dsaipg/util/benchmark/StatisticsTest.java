package com.phasmidsoftware.dsaipg.util.benchmark;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class StatisticsTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testMeanWithNegativeAndPositiveValues() {
        final Statistics statistics = new Statistics("test", x -> x * 1.0, 5, 1);
        statistics.add(-3);
        statistics.add(-2);
        statistics.add(0);
        statistics.add(2);
        statistics.add(3);
        assertEquals(0, statistics.mean(), 1E-7);
    }

    @Test
    public void testMeanWithZeroValues() {
        final Statistics statistics = new Statistics("test", x -> x * 1.0, 3, 1);
        statistics.add(0);
        statistics.add(0);
        statistics.add(0);
        assertEquals(0, statistics.mean(), 1E-7);
    }


    @Test
    public void testAdd() {
        final Statistics statistics = new Statistics("test", x -> x * 1.0, 3, 1);
        statistics.add(-1);
        statistics.add(0);
        statistics.add(1);
        assertEquals(3, statistics.getCount());
    }

    @Test
    public void testMean() {
        final Statistics statistics = new Statistics("test", x -> x * 1.0, 4, 1);
        statistics.add(-1);
        statistics.add(0);
        statistics.add(1);
        statistics.add(2);
        assertEquals(0.5, statistics.mean(), 1E-7);
    }

    @Test
    public void testStdDev() {
        final Statistics statistics = new Statistics("test", x -> x * 1.0, 4, 1);
        statistics.add(-1);
        statistics.add(0);
        statistics.add(1);
        statistics.add(4);
        assertEquals(Math.sqrt(3.5), statistics.stdDev(), 1E-7);
    }

    @Test
    public void testToString() {
        final Statistics statistics = new Statistics("test", Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 4, 2);
        statistics.add(-1);
        statistics.add(0);
        statistics.add(1);
        statistics.add(4);
        assertEquals("test: n=4; mean=1; stdDev=2; normalized=0.721", statistics.toString());
    }

    @Test
    public void testNormalizedMean() {
        int n = 2;
        final Statistics statistics = new Statistics("test", Statistics.NORMALIZER_LINEARITHMIC_NATURAL, n, 2);
        statistics.add(0);
        statistics.add(2);
        assertEquals(1.0 / n / Math.log(n), statistics.normalizedMean(), 1E-10);
    }
}