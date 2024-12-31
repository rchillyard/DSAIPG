package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HelperTest {
    final Config config = setupConfig("true", "false", "0", "1", "", "");

    final BaseComparatorHelper<String> helper = new InstrumentedComparatorHelper<>("test", String::compareToIgnoreCase, 20, config);

    @Test
    public void sortPair() {
        Instrument instrumenter = helper.instrumenter;
        String[] ab = new String[]{"a", "b"};
        String[] xs0 = new String[]{"a", "b"};
        helper.sortPair(xs0, 0, 2);
        assertArrayEquals(ab, xs0);
        assertEquals(1L, instrumenter.getCompares());
        assertEquals(0L, instrumenter.getSwaps());
        assertEquals(2L, instrumenter.getHits());
        assertEquals(2, instrumenter.getLookups());
        String[] xs1 = new String[]{"b", "a"};
        helper.sortPair(xs1, 0, 2);
        assertArrayEquals(ab, xs1);
        assertEquals(2L, instrumenter.getCompares());
        assertEquals(1, instrumenter.getSwaps());
        assertEquals(6, instrumenter.getHits());
        assertEquals(4, instrumenter.getLookups());
    }

    @Test
    public void sortTrio() {
        Instrument instrumenter = helper.instrumenter;
        String[] abc = new String[]{"a", "b", "c"};
        String[] xs0 = new String[]{"a", "b", "c"};
        helper.sortTrio(xs0, 0, 3);
        assertArrayEquals(abc, xs0);
        assertEquals(2L, instrumenter.getCompares());
        assertEquals(0L, instrumenter.getSwaps());
        assertEquals(3L, instrumenter.getHits());
        assertEquals(4, helper.getLookups());
        String[] xs1 = new String[]{"a", "c", "b"};
        helper.sortTrio(xs1, 0, 3);
        assertArrayEquals(abc, xs1);
        assertEquals(5L, instrumenter.getCompares());
        assertEquals(1L, instrumenter.getSwaps());
        assertEquals(9L, instrumenter.getHits());
        assertEquals(10, helper.getLookups());
        String[] xs2 = new String[]{"c", "a", "b"};
        helper.sortTrio(xs2, 0, 3);
        assertArrayEquals(abc, xs2);
        assertEquals(8, instrumenter.getCompares());
        assertEquals(3, instrumenter.getSwaps());
        assertEquals(17, instrumenter.getHits());
        assertEquals(16, helper.getLookups());
        String[] xs3 = new String[]{"b", "a", "c"};
        helper.sortTrio(xs3, 0, 3);
        assertArrayEquals(abc, xs3);
        assertEquals(10, instrumenter.getCompares());
        assertEquals(4, instrumenter.getSwaps());
        assertEquals(22, instrumenter.getHits());
        assertEquals(20, helper.getLookups());
        String[] xs4 = new String[]{"b", "c", "a"};
        helper.sortTrio(xs4, 0, 3);
        assertArrayEquals(abc, xs4);
        assertEquals(13, instrumenter.getCompares());
        assertEquals(6, instrumenter.getSwaps());
        assertEquals(30, instrumenter.getHits());
        assertEquals(26, helper.getLookups());
        String[] xs5 = new String[]{"c", "b", "a"};
        helper.sortTrio(xs5, 0, 3);
        assertArrayEquals(abc, xs5);
        assertEquals(16, instrumenter.getCompares());
        assertEquals(9, instrumenter.getSwaps());
        assertEquals(40, instrumenter.getHits());
        assertEquals(32, helper.getLookups());
    }
}