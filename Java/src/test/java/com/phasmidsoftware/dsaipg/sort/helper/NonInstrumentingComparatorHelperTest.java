package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.util.benchmark.Stopwatch;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.HELPER;
import static org.junit.Assert.*;

/**
 * Tests for NonInstrumentingComparatorHelper.
 * <p>
 * NOTE these bodies were generated empty by the IDE and therefore passed without
 * asserting anything. These 83 were written for ClassicHelper, a 355-line class
 * which duplicated this one rather than extending it, and which drifted from it
 * unnoticed for as long as its tests were empty. ClassicHelper has since been
 * folded in and deleted; the tests moved here, to the class they were really
 * exercising all along.
 * <p>
 * This Helper is NOT instrumented: it holds an InstrumenterDummy, so every
 * counter stays at zero. These tests therefore assert behaviour, not counts;
 * the counts are covered by BaseComparatorHelperTest and
 * InstrumentedComparableHelperTest.
 */
public class NonInstrumentingComparatorHelperTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private static Config config;
    private Helper<Integer> helper;

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load(NonInstrumentingComparatorHelperTest.class);
    }

    @Before
    public void before() {
        helper = new NonInstrumentingComparatorHelper<>("test", Integer::compare, 20, new Random(0L), config);
    }

    private Helper<String> stringHelper() {
        return new NonInstrumentingComparatorHelper<>("strings", String.CASE_INSENSITIVE_ORDER, 20, new Random(0L), config);
    }

    // ---- reading and writing --------------------------------------------

    @Test
    public void get() {
        assertEquals(Integer.valueOf(7), helper.get(new Integer[]{7, 8}, 0));
    }

    @Test
    public void set() {
        Integer[] xs = {1, 2};
        helper.set(xs, 0, 9);
        assertArrayEquals(new Integer[]{9, 2}, xs);
    }

    @Test
    public void copyArray() {
        Integer[] xs = {1, 2, 3};
        Integer[] ys = helper.copyArray(xs);
        assertArrayEquals(xs, ys);
        assertNotSame("copyArray must not alias its argument", xs, ys);
    }

    @Test
    public void copy() {
        Integer[] target = {0, 0};
        helper.copy(5, target, 1);
        assertArrayEquals(new Integer[]{0, 5}, target);
    }

    @Test
    public void testCopy() {
        Integer[] target = {0, 0};
        helper.copy(new Integer[]{7, 8}, 1, target, 0);
        assertArrayEquals(new Integer[]{8, 0}, target);
    }

    @Test
    public void copyBlock() {
        Integer[] target = {0, 0, 0, 0};
        helper.copyBlock(new Integer[]{1, 2, 3}, 0, target, 1, 3);
        assertArrayEquals(new Integer[]{0, 1, 2, 3}, target);
    }

    @Test
    public void copyBlockWithinOneArray() {
        // This overlapping case is how swapInto shifts elements up one place.
        Integer[] xs = {1, 2, 3, 4};
        helper.copyBlock(xs, 0, xs, 1, 3);
        assertArrayEquals(new Integer[]{1, 1, 2, 3}, xs);
    }

    @Test
    public void distributeBlock() {
        Integer[] target = new Integer[3];
        helper.distributeBlock(new Integer[]{2, 0, 1}, 0, 3, target, x -> x);
        assertArrayEquals(new Integer[]{0, 1, 2}, target);
    }

    // ---- swapping --------------------------------------------------------

    @Test
    public void swap() {
        Integer[] xs = {1, 2};
        helper.swap(xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
    }

    @Test
    public void testSwap() {
        // swapV: the caller already holds xs[i], so it is not read again.
        Integer[] xs = {1, 2};
        helper.swapV(xs[0], xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
    }

    @Test
    public void testSwap1() {
        // swapW: the caller already holds xs[j].
        Integer[] xs = {1, 2};
        helper.swapW(xs[1], xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
    }

    @Test
    public void testSwap2() {
        // swapVW: the caller holds both, so nothing is read.
        Integer[] xs = {1, 2};
        helper.swapVW(xs[0], xs[1], xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
    }

    @Test
    public void swapStable() {
        Integer[] xs = {1, 3, 2};
        helper.swapStable(xs, 2);
        assertArrayEquals(new Integer[]{1, 2, 3}, xs);
    }

    @Test
    public void swapInto() {
        Integer[] xs = {2, 3, 4, 1};
        helper.swapInto(xs, 0, 3);
        assertArrayEquals("xs[3] moves to 0 and the rest shift up", new Integer[]{1, 2, 3, 4}, xs);
    }

    @Test
    public void swapIntoSorted() {
        Integer[] xs = {1, 3, 5, 7, 4};
        helper.swapIntoSorted(xs, 0, 4);
        assertArrayEquals(new Integer[]{1, 3, 4, 5, 7}, xs);
    }

    @Test
    public void swapIntoSortedAtTheFront() {
        Integer[] xs = {2, 4, 6, 1};
        helper.swapIntoSorted(xs, 0, 3);
        assertArrayEquals(new Integer[]{1, 2, 4, 6}, xs);
    }

    // ---- comparing -------------------------------------------------------

    @Test
    public void compare() {
        assertTrue(helper.compare(1, 2) < 0);
        assertTrue(helper.compare(2, 1) > 0);
        assertEquals(0, helper.compare(1, 1));
    }

    @Test
    public void testCompare() {
        Integer[] xs = {1, 2};
        assertTrue(helper.compare(xs, 0, 1) < 0);
    }

    @Test
    public void testCompare1() {
        Integer[] xs = {1, 2};
        assertTrue(helper.compare(xs, xs[0], 1) < 0);
    }

    @Test
    public void testCompare2() {
        Integer[] xs = {1, 2};
        assertTrue(helper.compare(xs, 0, xs[1]) < 0);
    }

    @Test
    public void pureComparison() {
        assertTrue(helper.pureComparison(1, 2) < 0);
        assertEquals(0, helper.pureComparison(1, 1));
    }

    @Test
    public void pureComparisonUsesTheComparator() {
        // A case-insensitive comparator must be honoured, not the natural
        // ordering. This is the fault that was found in QuickSort_3way.
        assertEquals(0, stringHelper().pureComparison("ARAB", "arab"));
        assertTrue(stringHelper().pureComparison("abroad", "Arab") < 0);
    }

    @Test
    public void getComparator() {
        assertTrue(helper.getComparator().compare(1, 2) < 0);
    }

    @Test
    public void less() {
        // notInverted is the modern name.
        assertTrue(helper.notInverted(1, 2));
        assertFalse(helper.notInverted(2, 1));
    }

    @Test
    public void testLess() {
        Integer[] xs = {1, 2};
        assertTrue(helper.notInverted(xs, 0, 1));
    }

    @Test
    public void testLess1() {
        Integer[] xs = {1, 2};
        assertTrue(helper.notInverted(xs, xs[0], 1));
    }

    @Test
    public void testLess2() {
        Integer[] xs = {1, 2};
        assertTrue(helper.notInverted(xs, 0, xs[1]));
    }

    @Test
    public void inverted() {
        Integer[] xs = {2, 1};
        assertTrue(helper.inverted(xs, 0, 1));
        assertTrue(helper.inverted(2, 1));
        assertFalse(helper.inverted(1, 2));
    }

    @Test
    public void inSequence() {
        Integer[] xs = {1, 2};
        assertEquals(Integer.valueOf(2), helper.inSequence(xs, 1, 1));
        assertNull("null means the pair is out of order", helper.inSequence(xs, 3, 1));
    }

    // ---- conditional swaps ----------------------------------------------

    @Test
    public void swapConditional() {
        Integer[] xs = {2, 1};
        assertTrue(helper.swapConditional(xs, 0, 1));
        assertArrayEquals(new Integer[]{1, 2}, xs);
    }

    @Test
    public void swapConditionalLeavesAnOrderedPair() {
        Integer[] xs = {1, 2};
        assertFalse(helper.swapConditional(xs, 0, 1));
        assertArrayEquals(new Integer[]{1, 2}, xs);
    }

    @Test
    public void testSwapConditional() {
        Integer[] xs = {2, 1};
        assertTrue(helper.swapConditional(xs, 0, 1, xs[1]));
        assertArrayEquals(new Integer[]{1, 2}, xs);
    }

    @Test
    public void testSwapConditional1() {
        Integer[] xs = {2, 1};
        assertTrue(helper.swapConditional(xs, xs[0], 0, 1));
        assertArrayEquals(new Integer[]{1, 2}, xs);
    }

    @Test
    public void testSwapConditional2() {
        Integer[] xs = {2, 1};
        assertTrue(helper.swapConditional(xs, xs[0], 0, 1, xs[1]));
        assertArrayEquals(new Integer[]{1, 2}, xs);
    }

    @Test
    public void swapConditionalWithTheSameIndexTwice() {
        assertFalse(helper.swapConditional(new Integer[]{1, 2}, 1, 1));
    }

    @Test
    public void swapStableConditional() {
        Integer[] xs = {1, 3, 2};
        assertTrue(helper.swapStableConditional(xs, 2));
        assertArrayEquals(new Integer[]{1, 2, 3}, xs);
    }

    @Test
    public void fixInversion() {
        Integer[] xs = {3, 1};
        helper.fixInversion(xs, 0, 1);
        assertArrayEquals(new Integer[]{1, 3}, xs);
    }

    @Test
    public void testFixInversion() {
        Integer[] xs = {1, 3, 2};
        helper.fixInversion(xs, 2);
        assertArrayEquals(new Integer[]{1, 2, 3}, xs);
    }

    @Test
    public void sortPair() {
        Integer[] xs = {2, 1};
        assertTrue(helper.sortPair(xs, 0, 2));
        assertArrayEquals(new Integer[]{1, 2}, xs);
    }

    @Test
    public void sortTrio() {
        // Every arrangement of three must come out sorted.
        Integer[][] arrangements = {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}};
        for (Integer[] arrangement : arrangements) {
            Integer[] xs = arrangement.clone();
            helper.sortTrio(xs, 0, 3);
            assertArrayEquals("sortTrio " + Arrays.toString(arrangement), new Integer[]{1, 2, 3}, xs);
        }
    }

    // ---- searching and sortedness ---------------------------------------

    @Test
    public void findInversion() {
        assertEquals(-1, helper.findInversion(new Integer[]{1, 2, 3}));
        assertEquals(2, helper.findInversion(new Integer[]{1, 3, 2}));
    }

    @Test
    public void testFindInversion() {
        assertEquals(-1, helper.findInversion(new Integer[]{9, 1, 2, 3}, 1, 4));
    }

    @Test
    public void isSorted() {
        assertTrue(helper.isSorted(new Integer[]{1, 2, 3}));
        assertFalse(helper.isSorted(new Integer[]{1, 3, 2}));
        assertTrue("equal elements are sorted", helper.isSorted(new Integer[]{1, 1, 1}));
    }

    @Test
    public void testIsSorted() {
        assertTrue(helper.isSorted(new Integer[]{9, 1, 2, 3}, 1, 4));
    }

    @Test
    public void inversions() {
        // This Helper does not count inversions: it is not instrumented, and
        // counting them costs more than the sort.
        assertEquals(0L, helper.inversions(new Integer[]{3, 2, 1}));
    }

    // ---- generating arrays ----------------------------------------------

    @Test
    public void testRandom() {
        Integer[] xs = helper.random(10, Integer.class, r -> r.nextInt(100));
        assertEquals(10, xs.length);
    }

    @Test
    public void randomIsRepeatableForAGivenSeed() {
        Integer[] first = new NonInstrumentingComparatorHelper<Integer>("a", Integer::compare, 20, new Random(42L), config)
                .random(10, Integer.class, r -> r.nextInt(1000));
        Integer[] second = new NonInstrumentingComparatorHelper<Integer>("b", Integer::compare, 20, new Random(42L), config)
                .random(10, Integer.class, r -> r.nextInt(1000));
        assertArrayEquals(first, second);
    }

    @Test
    public void ordered() {
        assertArrayEquals(new Integer[]{0, 1, 2}, helper.ordered(3, Integer.class, i -> i));
    }

    @Test
    public void partialOrdered() {
        assertEquals(10, helper.partialOrdered(10, Integer.class, i -> i).length);
    }

    @Test
    public void reverse() {
        Integer[] xs = helper.reverse(3, Integer.class, i -> i);
        assertTrue("reverse yields a descending array", helper.inverted(xs, 0, 2));
    }

    @Test
    public void randomPair() {
        assertEquals(2, helper.randomPair(Integer.class, r -> r.nextInt(10)).length);
    }

    // ---- discrimination, for radix sorts ---------------------------------

    @Test
    public void discriminate() {
        assertEquals("ello", stringHelper().discriminate("hello", 1));
    }

    @Test
    public void discriminateNotDefinedForNonStrings() {
        assertThrows(SortException.class, () -> helper.discriminate(42, 1));
    }

    @Test
    public void discriminateString() {
        assertEquals("llo", Helper.discriminateString("hello", 2));
        assertEquals("a space, so that a shorter string sorts first", " ",
                Helper.discriminateString("ab", 5));
    }

    @Test
    public void compareSubstrings() {
        Helper<String> h = stringHelper();
        assertTrue(h.compareSubstrings("xabc", "xabd", 1) < 0);
        assertEquals(0, h.compareSubstrings("xabc", "yabc", 1));
    }

    // ---- lifecycle and configuration ------------------------------------

    @Test
    public void init() {
        helper.init(20);
        assertEquals(20, helper.getN());
    }

    @Test
    public void testInit() {
        // Re-initialising to the SAME n is fine; changing it is not. ClassicHelper
        // used to have no guard and silently accepted a different n; this test
        // recorded that, and its failing was the signal that the fold had worked.
        helper.init(20);
        assertEquals(20, helper.getN());
        assertThrows(HelperException.class, () -> helper.init(3));
    }

    @Test
    public void getN() {
        assertEquals(20, helper.getN());
    }

    @Test
    public void close() {
        helper.close();
    }

    @Test
    public void getDescription() {
        assertEquals("test", helper.getDescription());
    }

    @Test
    public void getConfig() {
        assertSame(config, helper.getConfig());
    }

    @Test
    public void testToString() {
        assertTrue(helper.toString().contains("test"));
    }

    @Test
    public void instrumented() {
        assertFalse("an uninstrumented Helper holds an InstrumenterDummy", helper.instrumented());
    }

    @Test
    public void cutoff() {
        // The configured cutoff is honoured. ClassicHelper used to ignore it and
        // always return Helper's default of 20; this test recorded that
        // divergence, and its failing was the signal that the fold had worked.
        Config withCutoff = config.copy(HELPER, "cutoff", "8");
        Helper<Integer> h = new NonInstrumentingComparatorHelper<>("test", Integer::compare, 20, new Random(0L), withCutoff);
        assertEquals("the configured cutoff is honoured", 8, h.cutoff());
    }

    @Test
    public void MSDCutoff() {
        // The documented default, from config.ini. This used to be 20 here,
        // because MSDCutoff() was overridden only on the instrumented Helper,
        // so an uninstrumented MSDStringSort cut over to quicksort far earlier
        // than an instrumented one.
        assertEquals(256, helper.MSDCutoff());
        Config withMSD = config.copy(HELPER, "msdcutoff", "64");
        Helper<Integer> h = new NonInstrumentingComparatorHelper<>("test", Integer::compare, 20, new Random(0L), withMSD);
        assertEquals(64, h.MSDCutoff());
    }

    @Test
    public void clone_() {
        Helper<Integer> clone = helper.clone("other", 5, false);
        assertEquals("other", clone.getDescription());
        assertEquals(5, clone.getN());
        assertTrue("the comparator must survive cloning", clone.compare(1, 2) < 0);
    }

    // ---- pre- and post-processing ---------------------------------------

    @Test
    public void preProcess() {
        Integer[] xs = {3, 1, 2};
        assertSame(xs, helper.preProcess(xs));
    }

    /**
     * checksorted is true in test/resources/config.ini, so an unsorted array is
     * rejected. That is the point of setting it there: a test must not be able to
     * pass on a sort which did not sort.
     * <p>
     * NOTE this test used to assert the opposite — "checksorted is not set in
     * config.ini, so nothing is checked" — which was true, and was the problem.
     */
    @Test
    public void postProcess() {
        assertThrows(HelperException.class,
                () -> helper.postProcess(new Integer[]{1, 3, 2}));
    }

    @Test
    public void postProcessAcceptsASortedArray() {
        helper.postProcess(new Integer[]{1, 2, 3});
    }

    /**
     * With the flag off — as in main/resources/config.ini, so that benchmarks do
     * not measure the check — nothing is verified.
     */
    @Test
    public void postProcessIsSilentWhenNotAsked() {
        Config notChecking = config.copy(HELPER, "checksorted", "");
        Helper<Integer> h = new NonInstrumentingComparatorHelper<>("test", Integer::compare, 20, new Random(0L), notChecking);
        h.postProcess(new Integer[]{1, 3, 2});
    }

    @Test
    public void postProcessChecksWhenAsked() {
        Config checking = config.copy(HELPER, "checksorted", "true");
        Helper<Integer> h = new NonInstrumentingComparatorHelper<>("test", Integer::compare, 20, new Random(0L), checking);
        assertThrows(RuntimeException.class, () -> h.postProcess(new Integer[]{1, 3, 2}));
    }

    // ---- counters, all of which stay at zero ----------------------------

    @Test
    public void getStatPack() {
        assertNull("an uninstrumented Helper gathers no statistics", helper.getStatPack());
    }

    @Test
    public void getCompares() {
        helper.compare(1, 2);
        assertEquals(0L, helper.getCompares());
    }

    @Test
    public void getSwaps() {
        Integer[] xs = {2, 1};
        helper.swap(xs, 0, 1);
        assertEquals(0L, helper.getSwaps());
    }

    @Test
    public void countersStayAtZero() {
        Integer[] xs = {2, 1};
        helper.get(xs, 0);
        helper.compare(1, 2);
        helper.swap(xs, 0, 1);
        helper.incrementCopies(3);
        helper.incrementHits(3);
        helper.incrementFixes(3);
        helper.incrementLookups(3);
        assertEquals(0L, helper.getHits());
        assertEquals(0L, helper.getCopies());
        assertEquals(0L, helper.getFixes());
        assertEquals(0L, helper.getLookups());
        assertFalse(helper.countFixes());
        assertFalse(helper.isShowStats());
    }

    @Test
    public void gatherStatisticIsHarmless() {
        helper.gatherStatistic();
    }

    @Test
    public void showFixes() {
        // Not implemented for an uninstrumented Helper.
        assertEquals("", helper.showStats());
    }

    @Test
    public void showStats() {
        assertEquals("", helper.showStats());
    }

    @Test
    public void registerDepth() {
        helper.registerDepth(7);
        assertEquals("an uninstrumented Helper does not track depth", 0, helper.maxDepth());
    }

    @Test
    public void maxDepth() {
        assertEquals(0, helper.maxDepth());
    }

    // ---- Comparator default methods, inherited via Helper ---------------

    @Test
    public void reversed() {
        assertTrue(helper.reversed().compare(1, 2) > 0);
    }

    @Test
    public void reverseOrder() {
        assertTrue(java.util.Comparator.<Integer>reverseOrder().compare(1, 2) > 0);
    }

    @Test
    public void naturalOrder() {
        assertTrue(java.util.Comparator.<Integer>naturalOrder().compare(1, 2) < 0);
    }

    /**
     * Carried over from the previous NonInstrumentingComparatorHelperTest, which
     * held this and nothing else. It is really a rough timing harness -- 10,000
     * elements, a hundred repetitions, printing the average -- but it does assert
     * something: swapping every element with its mirror twice restores the
     * original order.
     */
    @Test
    public void swappingEveryElementTwiceRestoresTheOrder() {
        try (Helper<Object> helper = new NonInstrumentingComparatorHelper<>("test", null, config)) {
            Integer[] xs = new Integer[10000];
            int length = xs.length;
            for (int i = 0; i < length; i++) xs[i] = i;
            Object[] ys = new Object[length];
            System.arraycopy(xs, 0, ys, 0, length);
            doSwaps(length, helper, ys);
            try (Stopwatch stopwatch = new Stopwatch("microseconds")) {
                int n = 100;
                for (int i = 0; i < n; i++)
                    doSwaps(length, helper, ys);
                System.out.println("average milliseconds: " + stopwatch.lap() / 1000.0 / n);
            }
            assertArrayEquals(xs, ys);
        }
    }

    private static void doSwaps(int length, Helper<Object> helper, Object[] ys) {
        for (int i = 0; i < length; i++) helper.swap(ys, i, length - 1 - i);
        for (int i = 0; i < length; i++) helper.swap(ys, i, length - 1 - i);
    }
}
