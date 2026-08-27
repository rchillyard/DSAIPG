package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;

/**
 * Tests for Helper.binarySearchUpperBound.
 * <p>
 * This is what swapIntoSorted uses to find where an element belongs, and getting
 * it wrong is not obvious from the outside: the sort still produces a sorted
 * array, but an unstable one, having moved more elements than it needed to.
 * <p>
 * It returns the index of the first element GREATER than the key, so it lands
 * after any run of elements equal to it -- unlike Arrays.binarySearch, which
 * lands somewhere in the middle of such a run and offers no guarantee which.
 */
public class BinarySearchUpperBoundTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private static final Integer[] ASCENDING = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    private Helper<Integer> helper;

    @Before
    public void before() {
        Config config = setupConfig("false", "", "0", "0", "", "");
        helper = HelperFactory.create("upper bound", 10, config);
    }

    private int search(Integer[] xs, int key) {
        return helper.binarySearchUpperBound(xs, 0, xs.length, key);
    }

    @Test
    public void testKeyPresent() {
        // The first element greater than 3 is the 4 at index 3.
        assertEquals(3, search(ASCENDING, 3));
        assertEquals(0, search(ASCENDING, 0));
        assertEquals(8, search(ASCENDING, 8));
    }

    @Test
    public void testKeyAbsent() {
        Integer[] gaps = {1, 3, 5, 7};
        assertEquals(1, search(gaps, 2));
        assertEquals(2, search(gaps, 4));
        assertEquals(0, search(gaps, 0));
    }

    @Test
    public void testKeyBeyondTheEnd() {
        assertEquals(ASCENDING.length, search(ASCENDING, 100));
    }

    @Test
    public void testKeyBeforeTheStart() {
        assertEquals(0, search(ASCENDING, -100));
    }

    @Test
    public void testDuplicates() {
        // The point of an upper bound: it lands after the whole run, not in it.
        Integer[] duplicates = {1, 5, 5, 5, 5, 5, 9};
        assertEquals(6, search(duplicates, 5));
    }

    @Test
    public void testAllEqual() {
        Integer[] all = {7, 7, 7, 7};
        assertEquals(4, search(all, 7));
        assertEquals(0, search(all, 6));
    }

    @Test
    public void testEmpty() {
        assertEquals(0, search(new Integer[0], 3));
    }

    @Test
    public void testSingleton() {
        assertEquals(0, search(new Integer[]{5}, 4));
        assertEquals(1, search(new Integer[]{5}, 5));
        assertEquals(1, search(new Integer[]{5}, 6));
    }

    /**
     * The answer must agree with a direct scan, at every key, for every prefix.
     */
    @Test
    public void testAgainstALinearScan() {
        Integer[] xs = {2, 2, 4, 6, 6, 6, 9};
        for (int key = 0; key <= 11; key++) {
            int expected = 0;
            while (expected < xs.length && xs[expected] <= key) expected++;
            assertEquals("key " + key, expected, search(xs, key));
        }
    }
}
