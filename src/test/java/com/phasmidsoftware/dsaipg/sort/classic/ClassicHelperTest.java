package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.util.Config;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ClassicHelperTest {

    private static Config config;
    private Helper<Integer> helper;

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load(ClassicHelperTest.class);
    }

    @Before
    public void before() {
        Random random = new Random(0L);
        helper = new ClassicHelper<>("test", Integer::compare, 20, random, config);
    }

    @Test
    public void swapInto() {
    }

    @Test
    public void showFixes() {
    }

    @Test
    public void copyArray() {
    }

    @Test
    public void preProcess() {
    }

    @Test
    public void swap() {
    }

    @Test
    public void testSwap() {
    }

    @Test
    public void testSwap1() {
    }

    @Test
    public void testSwap2() {
    }

    @Test
    public void swapStable() {
    }

    @Test
    public void get() {
    }

    @Test
    public void set() {
    }

    @Test
    public void copy() {
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void copyBlock() {
    }

    @Test
    public void distributeBlock() {
    }

    @Test
    public void ordered() {
    }

    @Test
    public void partialOrdered() {
    }

    @Test
    public void reverse() {
    }

    @Test
    public void random1() {
        Integer[] integers = helper.random(Integer.class, r -> r.nextInt(100));
        assertEquals(60, integers[0].intValue());
        assertEquals(48, integers[1].intValue());
        assertEquals(20, integers[19].intValue());
    }

    @Test
    public void random2() {
        Integer[] integers = helper.random(20, Integer.class, r -> r.nextInt(100));
        assertEquals(60, integers[0].intValue());
        assertEquals(48, integers[1].intValue());
        assertEquals(20, integers[19].intValue());
    }

    @Test
    public void randomPair() {
    }

    @Test
    public void compare() {
    }

    @Test
    public void testCompare() {
    }

    @Test
    public void testCompare1() {
    }

    @Test
    public void less() {
    }

    @Test
    public void testLess() {
    }

    @Test
    public void testLess1() {
    }

    @Test
    public void testLess2() {
    }

    @Test
    public void inSequence() {
    }

    @Test
    public void swapConditional() {
    }

    @Test
    public void testSwapConditional() {
    }

    @Test
    public void testSwapConditional1() {
    }

    @Test
    public void testSwapConditional2() {
    }

    @Test
    public void swapStableConditional() {
    }

    @Test
    public void swapIntoSorted() {
    }

    @Test
    public void fixInversion() {
    }

    @Test
    public void testFixInversion() {
    }

    @Test
    public void findInversion() {
    }

    @Test
    public void testFindInversion() {
    }

    @Test
    public void isSorted() {
    }

    @Test
    public void testIsSorted() {
    }

    @Test
    public void postProcess() {
    }

    @Test
    public void cutoff() {
    }

    @Test
    public void MSDCutoff() {
    }

    @Test
    public void discriminate() {
    }

    @Test
    public void discriminateString() {
    }

    @Test
    public void compareSubstrings() {
    }

    @Test
    public void inversions() {
    }

    @Test
    public void registerDepth() {
    }

    @Test
    public void maxDepth() {
    }

    @Test
    public void showStats() {
    }

    @Test
    public void reversed() {
    }

    @Test
    public void reverseOrder() {
    }

    @Test
    public void naturalOrder() {
    }

    @Test
    public void instrumented() {
    }

    @Test
    public void testCompare2() {
    }

    @Test
    public void pureComparison() {
    }

    @Test
    public void testRandom() {
    }

    @Test
    public void init() {
    }

    @Test
    public void getN() {
    }

    @Test
    public void close() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void getDescription() {
    }

    @Test
    public void getConfig() {
    }

    @Test
    public void getComparator() {
    }

    @Test
    public void testInit() {
    }

    @Test
    public void getStatPack() {
    }

    @Test
    public void getCompares() {
    }

    @Test
    public void getSwaps() {
    }
}