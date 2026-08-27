package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSortBasic;
import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class ComparableHelperTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private static Helper<Integer> helper;

    @BeforeClass
    public static void setup() throws IOException {
        Config config = Config.load();
        helper = new InstrumentedComparableHelper<>("test comparableHelper", 0, 0L, config);
    }

    /**
     * A Helper of its own, because the one above is static and shared by every
     * test in this class, so its counters accumulate and any absolute count
     * would depend on the order the tests happened to run in.
     */
    private static Helper<Integer> fresh() throws IOException {
        return new InstrumentedComparableHelper<>("test comparableHelper", 0, 0L, Config.load());
    }

    @Test
    public void testCompare() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            int cf0 = Integer.compare(pair[0], pair[1]);
            int cf1 = helper.compare(pair[0], pair[1]);
            int cf2 = helper.compare(pair, 0, 1);
            int cf3 = helper.compare(pair, 0, pair[1]);
            assertEquals(cf0, cf1);
            assertEquals(cf0, cf2);
            assertEquals(cf0, cf3);
        }
    }

    @Test
    public void testNotInverted1() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            boolean cf0 = pair[0] < pair[1];
            boolean cf1 = helper.notInverted(pair[0], pair[1]);
            boolean cf2 = helper.notInverted(pair, 0, 1);
            boolean cf3 = helper.notInverted(pair, pair[0], 1);
            boolean cf4 = helper.notInverted(pair, 0, pair[1]);
            assertEquals(cf0, cf1);
            assertEquals(cf0, cf2);
            assertEquals(cf0, cf3);
            assertEquals(cf0, cf4);
        }
    }

    @Test
    public void testInverted() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            boolean cf0 = pair[0] > pair[1];
            boolean cf2 = helper.inverted(pair, 0, 1);
            assertEquals(cf0, cf2);
        }
    }

    @Test
    public void swapConditional() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            int cf0 = Integer.compare(pair[0], pair[1]);
            boolean swapped = helper.swapConditional(pair, 0, 1);
            int cf1 = Integer.compare(pair[0], pair[1]);
            assertEquals(cf0 > 0, swapped);
            assertTrue(cf1 <= 0);
        }
    }

    @Test
    public void swapStableConditional() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            int cf0 = Integer.compare(pair[0], pair[1]);
            boolean swapped = helper.swapStableConditional(pair, 1);
            int cf1 = Integer.compare(pair[0], pair[1]);
            assertEquals(cf0 > 0, swapped);
            assertTrue(cf1 <= 0);
        }
    }

    @Test
    public void swapInto() throws IOException {
        Helper<Integer> h = fresh();
        Integer[] xs = {2, 3, 4, 1};
        h.swapInto(xs, 0, 3);
        assertArrayEquals("xs[3] moves to 0, the rest shift up", new Integer[]{1, 2, 3, 4}, xs);
        assertEquals(1, h.getSwaps());
        assertEquals("one copy per element shifted, not two", 3, h.getCopies());
    }

    @Test
    public void swapIntoSorted() throws IOException {
        Helper<Integer> h = fresh();
        Integer[] xs = {1, 3, 5, 7, 4};
        h.swapIntoSorted(xs, 0, 4);
        assertArrayEquals(new Integer[]{1, 3, 4, 5, 7}, xs);
    }

    @Test
    public void fixInversion() throws IOException {
        Helper<Integer> h = fresh();
        Integer[] xs = {3, 1};
        h.fixInversion(xs, 0, 1);
        assertArrayEquals(new Integer[]{1, 3}, xs);
    }

    @Test
    public void testFixInversion() throws IOException {
        Helper<Integer> h = fresh();
        Integer[] xs = {1, 3, 2};
        h.fixInversion(xs, 2);
        assertArrayEquals(new Integer[]{1, 2, 3}, xs);
    }

    @Test
    public void sorted() {
        assertTrue(helper.isSorted(new Integer[]{1, 2, 3}));
        assertFalse(helper.isSorted(new Integer[]{1, 3, 2}));
        assertTrue("equal elements are sorted", helper.isSorted(new Integer[]{1, 1, 1}));
        assertEquals(-1, helper.findInversion(new Integer[]{1, 2, 3}));
        assertEquals(2, helper.findInversion(new Integer[]{1, 3, 2}));
    }

    @Test
    public void inversions0() {
        assertEquals(0, MergeSortBasic.countInversions(new Integer[]{1, 2, 3}));
        assertEquals(1, MergeSortBasic.countInversions(new Integer[]{1, 3, 2}));
        assertEquals(1, MergeSortBasic.countInversions(new Integer[]{2, 1, 3}));
        assertEquals(2, MergeSortBasic.countInversions(new Integer[]{2, 3, 1}));
        assertEquals(2, MergeSortBasic.countInversions(new Integer[]{3, 1, 2}));
        assertEquals(3, MergeSortBasic.countInversions(new Integer[]{3, 2, 1}));
    }

    @Test
    public void postProcess() throws IOException {
        Helper<Integer> h = fresh();
        h.init(3);
        h.postProcess(new Integer[]{1, 2, 3});
        // An instrumented Helper always checks, regardless of checksorted.
        assertThrows(RuntimeException.class, () -> h.postProcess(new Integer[]{1, 3, 2}));
    }

    @Test
    public void cutoff() {
        // config.ini leaves cutoff empty, so the default applies.
        assertEquals(20, helper.cutoff());
    }

    @Test
    public void init() throws IOException {
        Helper<Integer> h = fresh();
        h.init(5);
        assertEquals(5, h.getN());
        h.init(5);
        assertEquals("the same value again is fine", 5, h.getN());
        assertThrows("a different n is an error", HelperException.class, () -> h.init(6));
    }

    @Test
    public void incrementCopies() throws IOException {
        Helper<Integer> h = fresh();
        h.incrementCopies(3);
        assertEquals(3, h.getCopies());
    }

    @Test
    public void incrementFixes() throws IOException {
        // config.ini has fixes false, because counting them costs more than the
        // sort itself, so this counter stays at zero.
        Helper<Integer> h = fresh();
        h.incrementFixes(3);
        assertFalse(h.countFixes());
        assertEquals(0, h.getFixes());
    }

    @Test
    public void incrementHits() throws IOException {
        Helper<Integer> h = fresh();
        h.incrementHits(3);
        assertEquals(3, h.getHits());
    }

    @Test
    public void preProcess() throws IOException {
        Helper<Integer> h = fresh();
        h.init(3);
        Integer[] xs = {3, 1, 2};
        assertSame(xs, h.preProcess(xs));
    }

    @Test
    public void registerDepth() throws IOException {
        Helper<Integer> h = fresh();
        h.registerDepth(3);
        h.registerDepth(7);
        h.registerDepth(5);
        assertEquals("the deepest, not the last", 7, h.maxDepth());
    }

    @Test
    public void maxDepth() throws IOException {
        assertEquals(0, fresh().maxDepth());
    }

    @Test
    public void showStats() throws IOException {
        Helper<Integer> h = fresh();
        h.init(2);
        assertTrue(h.showStats().contains("test"));
    }

    @Test
    public void showFixes() throws IOException {
        // showStats with a context names both the Helper and the context.
        Helper<Integer> h = fresh();
        h.init(2);
        assertTrue(h.showStats("merge").contains("merge"));
    }
}