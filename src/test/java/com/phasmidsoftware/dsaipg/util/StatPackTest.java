package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatPackTest {

    final static String key1 = "test1";
    final static String key2 = "test2";

    @Test
    public void testAdd() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 10, 2, key1, key2);
        statPack.add(key1, -1);
        statPack.add(key1, 0);
        statPack.add(key1, 1);
        statPack.add(key2, 1);
        assertEquals(3, statPack.getCount(key1));
        assertEquals(1, statPack.getCount(key2));
        assertEquals("StatPack {" + key2 + ": n=10; mean=1; normalized=0.721; " + key1 + ": n=10; mean=0; stdDev=1; normalized=0.000}", statPack.toString());
    }

    @Test
    public void testGetStatistics1() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 10, 2, key1, key2);
        assertNotNull(statPack.getStatistics(key1));
        assertNotNull(statPack.getStatistics(key2));
    }

    @Test(expected = RuntimeException.class)
    public void testGetStatistics2() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 10, 2, key1, key2);
        statPack.getStatistics("invalid");
    }

    @Test
    public void testTotal() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 4, 2, key1, key2);
        statPack.add(key1, -1);
        statPack.add(key1, 0);
        statPack.add(key1, 1);
        statPack.add(key2, 1);
        assertEquals(0, statPack.total(key1), 0);
        assertEquals(1, statPack.total(key2), 0);
    }

    @Test
    public void testMean() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 5, 2, key1, key2);
        statPack.add(key1, -1);
        statPack.add(key1, 0);
        statPack.add(key1, 1);
        statPack.add(key2, 1);
        statPack.add(key1, 2);
        assertEquals(0.5, statPack.mean(key1), 0);
        assertEquals(1, statPack.mean(key2), 0);
    }

    @Test
    public void testStdDev() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 5, 2, key1, key2);
        statPack.add(key1, -1);
        statPack.add(key1, 0);
        statPack.add(key1, 1);
        statPack.add(key2, 1);
        statPack.add(key1, 4);
        assertEquals(Math.sqrt(3.5), statPack.stdDev(key1), 1E-7);
        assertEquals(0, statPack.stdDev(key2), 0);
    }


    @Test
    public void testBig() {
        final StatPack statPack = new StatPack(Statistics.NORMALIZER_LINEARITHMIC_NATURAL, 4, 2, key1, key2);
        statPack.add(key1, 2_100_000_000);
        statPack.add(key1, 1_000_000_000);
        statPack.add(key1, 1_000_000_000);
        statPack.add(key2, 2_100_000_000);
        statPack.add(key1, 2_100_000_000);
        statPack.add(key2, 1_000_000_000);
        assertEquals(1.55E9, statPack.mean(key1), 0);
        assertEquals(5.5E8, statPack.stdDev(key1), 0);
        assertEquals(1.55E9, statPack.mean(key2), 0);
        assertEquals(5.5E8, statPack.stdDev(key2), 0);
    }

}