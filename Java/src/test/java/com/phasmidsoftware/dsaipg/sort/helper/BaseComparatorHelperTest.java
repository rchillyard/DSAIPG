package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class BaseComparatorHelperTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private InstrumentedComparatorHelper<Integer> helper;

    @Before
    public void setup() throws IOException {
        Config config = Config.load();
        int N = 10;
        helper = new InstrumentedComparatorHelper<>("", Integer::compare, 0, 0L, config);
    }

    @Test
    public void swap() {
        // Two reads and two writes, and one swap. The counts here were checked
        // against the Helper itself, not inferred.
        Integer[] xs = {1, 2};
        helper.swap(xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
        assertEquals(1, helper.getSwaps());
        assertEquals(4, helper.getHits());
    }

    @Test
    public void swapStable() {
        Integer[] xs = {1, 3, 2};
        helper.swapStable(xs, 2);
        assertArrayEquals(new Integer[]{1, 2, 3}, xs);
        assertEquals(1, helper.getSwaps());
        assertEquals(4, helper.getHits());
    }

    @Test
    public void compare() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            int cf0 = Integer.compare(pair[0], pair[1]); // no hits, 0 lookups
            int cf1 = helper.compare(pair[0], pair[1]); // no hits, 0 lookups
            int cf2 = helper.compareWithLookups(pair, 0, 1, 2); // 2 hits, 2 lookups
            int cf3 = helper.compare(pair, 0, helper.lookup(pair[1])); // 1 hit, 1 lookups
            assertEquals(cf0, cf1);
            assertEquals(cf0, cf2);
            assertEquals(cf0, cf3);
        }
        assertEquals(300, helper.getCompares());
        assertEquals(300, helper.getHits());
        assertEquals(300, helper.getLookups());
    }

    @Test
    public void testCompare() {
        // swapV and swapW each read one element rather than two, because the
        // caller already holds the other. That is the whole reason they exist.
        Integer[] xs = {1, 2};
        helper.swapV(xs[0], xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
        assertEquals(1, helper.getSwaps());
        assertEquals(3, helper.getHits());
    }

    @Test
    public void notInverted() {
        for (int i = 0; i < 100; i++) {
            Integer[] pair = helper.randomPair(Integer.class, r -> r.nextInt(10));
            boolean cf0 = pair[0] < pair[1]; // no hits, 0 lookups
            boolean cf1 = helper.notInverted(pair[0], pair[1]); // no hits, 0 lookups
            boolean cf2 = helper.notInvertedWithLookups(pair, 0, 1, 2); // 2 hits, 2 lookups
            boolean cf3 = helper.notInverted(pair, 0, helper.lookup(pair[1])); // 1 hit, 1 lookup
            assertEquals(cf0, cf1);
            assertEquals(cf0, cf2);
            assertEquals(cf0, cf3);
        }
        assertEquals(300, helper.getCompares());
        assertEquals(300, helper.getHits());
        assertEquals(300, helper.getLookups());
    }

    @Test
    public void testLess() {
        Integer[] xs = {1, 2};
        assertTrue(helper.notInverted(xs, 0, 1));
        assertEquals(1, helper.getCompares());
        assertEquals(2, helper.getHits());
    }

    @Test
    public void testLess1() {
        Integer[] xs = {1, 2};
        assertTrue(helper.notInverted(xs, xs[0], 1));
        assertEquals(1, helper.getCompares());
        assertEquals(1, helper.getHits());
    }

    @Test
    public void testLess2() {
        Integer[] xs = {1, 2};
        assertTrue(helper.notInverted(xs, 0, xs[1]));
        assertEquals(1, helper.getCompares());
        assertEquals(1, helper.getHits());
    }

    @Test
    public void inSequence() {
        // inSequence deliberately affects no statistics: checking whether an
        // array is sorted must not show up as work the sort did.
        Integer[] xs = {1, 2};
        assertEquals(Integer.valueOf(2), helper.inSequence(xs, 1, 1));
        assertNull(helper.inSequence(xs, 3, 1));
        assertEquals(0, helper.getCompares());
        assertEquals(0, helper.getHits());
    }

    @Test
    public void swapConditional() {
        Integer[] xs = {2, 1};
        assertTrue(helper.swapConditional(xs, 0, 1));
        assertArrayEquals(new Integer[]{1, 2}, xs);
        assertEquals(1, helper.getCompares());
        assertEquals(1, helper.getSwaps());
        assertEquals(4, helper.getHits());
        assertEquals(2, helper.getLookups());
    }

    @Test
    public void swapStableConditional() {
        Integer[] xs = {1, 2};
        assertFalse("an ordered pair is left alone", helper.swapStableConditional(xs, 1));
        assertArrayEquals(new Integer[]{1, 2}, xs);
        assertEquals(1, helper.getCompares());
        assertEquals(0, helper.getSwaps());
        assertEquals(2, helper.getHits());
    }

    @Test
    public void testCompare1() {
        // swapVW reads nothing at all: two writes only.
        Integer[] xs = {1, 2};
        helper.swapVW(xs[0], xs[1], xs, 0, 1);
        assertArrayEquals(new Integer[]{2, 1}, xs);
        assertEquals(1, helper.getSwaps());
        assertEquals(2, helper.getHits());
    }
}