package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
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
    }

    @Test
    public void swapStable() {
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
    public void swapStableConditional() {
    }

    @Test
    public void testCompare1() {
    }
}