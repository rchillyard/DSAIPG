package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.Utilities;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class BaseComparableHelperTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    public static final String xA = "a";
    public static final String xB = "b";

    @BeforeClass
    public static void setupClass() {
        try {
            config = Config.load(BaseComparableHelperTest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * NOTE this is used only by BaseComparableHelperTest
     *
     * @param <X>
     */
    static class BaseComparableHelperWithSortedTest<X extends Comparable<X>> extends BaseComparableHelper<X> {
        public boolean instrumented() {
            return false;
        }

        /**
         * Method to generate an array of randomly chosen X elements.
         *
         * @param m     the number of random elements required.
         * @param clazz the class of X.
         * @param f     a function which takes a Random and generates a random value of X.
         * @return an array of X of length determined by the current value according to setN.
         */
        public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
            if (m <= 0)
                throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
            randomArray = Utilities.fillRandomArray(clazz, random, m, f);
            return randomArray;
        }

        public BaseComparableHelperWithSortedTest() {
            super("test", BaseComparableHelperTest.config);
        }

        public BaseComparableHelperWithSortedTest(int i, long l) {
            super("test", i, l, BaseComparableHelperTest.config);
        }

        /**
         * Method to post-process the array xs after sorting.
         * By default, this method does nothing.
         *
         * @param xs the array to be tested.
         */
        public void postProcess(X[] xs) {
            if (!isSorted(xs)) throw new HelperException("Array is not sorted");
        }

        public Helper<X> clone(String description, int N, boolean shareInstrumenter) {
            return null;
        }
    }

    @Test
    public void instrumented() {
        assertFalse(new BaseComparableHelperWithSortedTest<String>().instrumented());
    }

    @Test
    public void notInverted() {
        assertTrue(new BaseComparableHelperWithSortedTest<String>().notInverted(xA, xB));
    }

    @Test
    public void compare() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            assertEquals(-1, helper.compare(xs, 0, 1));
            assertEquals(0, helper.compare(xs, 0, 0));
            assertEquals(1, helper.compare(xs, 1, 0));
        }
    }

    @Test
    public void swap0() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            helper.swap(xs, 0, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.swap(xs, 0, 1);
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void swap1() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            helper.swapV(xA, xs, 0, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.swapV(xB, xs, 0, 1);
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void swap2() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            helper.swapW(xB, xs, 0, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.swapW(xA, xs, 0, 1);
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void swap3() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            helper.swapVW(xA, xB, xs, 0, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.swapVW(xB, xA, xs, 0, 1);
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void sorted() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            assertTrue(helper.isSorted(xs));
            helper.swap(xs, 0, 1);
            assertEquals(1, helper.findInversion(xs));
        }
    }

    // NOTE it doesn't make sense to try to get inversions from a non-instrumenting Helper.
//    @Test
    public void inversions() {
        String[] xs = new String[]{xA, xB};
        try (final Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            assertEquals(0, helper.inversions(xs));
            helper.swap(xs, 0, 1);
            assertEquals(1, helper.inversions(xs));
        }
    }

    @Test
    public void postProcess1() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            helper.postProcess(xs);
        }
    }

    @Test(expected = HelperException.class)
    public void postProcess2() {
        String[] xs = new String[]{xB, xA};
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>()) {
            helper.postProcess(xs);
        }
    }

    @Test
    public void testRandom() {
        String[] words = new String[]{"Hello", "World"};
        final String[] strings;
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L)) {
            strings = helper.random(String.class, r -> words[r.nextInt(2)]);
        }
        assertArrayEquals(new String[]{"World", "World", "Hello"}, strings);
    }

    @Test
    public void testOrdered() {
        String[] words = new String[]{"Hello", "World"};
        final String[] strings;
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L)) {
            strings = helper.ordered(2, String.class, i -> words[i]);
        }
        assertArrayEquals(new String[]{"Hello", "World"}, strings);
    }

    @Test
    public void testPartialOrdered() {
        String[] words = new String[]{"Hello", "World"};
        final String[] strings;
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L)) {
            strings = helper.partialOrdered(2, String.class, i -> words[i]);
        }
        assertArrayEquals(new String[]{"World", "Hello"}, strings);
    }

    @Test
    public void testReverse() {
        String[] words = new String[]{"Hello", "World"};
        final String[] strings;
        try (Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L)) {
            strings = helper.reverse(2, String.class, i -> words[i]);
        }
        assertArrayEquals(new String[]{"World", "Hello"}, strings);
    }

    @Test
    public void testToString() {
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", 3, config)) {
            assertEquals("Helper for test with 3 elements", helper.toString());
        }
    }

    @Test
    public void getDescription() {
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", 3, config)) {
            assertEquals("test", helper.getDescription());
        }
    }

    @Test(expected = RuntimeException.class)
    public void getSetN() {
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", 3, config)) {
            assertEquals(3, helper.getN());
            helper.init(4);
            assertEquals(4, helper.getN());
        }
    }

    @Test
    public void getSetNBis() {
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            assertEquals(0, helper.getN());
            helper.init(4);
            assertEquals(4, helper.getN());
        }
    }

    @Test
    public void close() throws Exception {
        // NOTE since we explicitly call close, we don't use the try-with-resources mechanism
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.close();
    }

    @Test
    public void swapStable() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.swapStable(xs, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.swapStable(xs, 1);
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void fixInversion1() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.fixInversion(xs, 1); // XXX Deprecated
            assertArrayEquals(new String[]{xA, xB}, xs);
            helper.swapStable(xs, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.fixInversion(xs, 1); // XXX Deprecated
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void testFixInversion2() {
        String[] xs = new String[]{xA, xB};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.fixInversion(xs, 0, 1);
            assertArrayEquals(new String[]{xA, xB}, xs);
            helper.swap(xs, 0, 1);
            assertArrayEquals(new String[]{xB, xA}, xs);
            helper.fixInversion(xs, 0, 1);
        }
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void testSwapInto() {
        String[] xs = new String[]{xA, xB, "c"};
        try (NonComparableHelper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.swapInto(xs, 0, 2);
            assertArrayEquals(new String[]{"c", xA, xB}, xs);
            helper.swapInto(xs, 0, 1);
            assertArrayEquals(new String[]{xA, "c", xB}, xs);
            helper.swapInto(xs, 0, 0);
        }
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
    }


    @Test
    public void testSwapIntoSorted0() {
        String[] xs = new String[]{xA, xB, "c"};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.swapIntoSorted(xs, 0, 2);
        }
        assertArrayEquals(new String[]{xA, xB, "c"}, xs);
    }

    @Test
    public void testSwapIntoSorted1() {
        String[] xs = new String[]{xA, "c", xB};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.swapIntoSorted(xs, 0, 2);
        }
        assertArrayEquals(new String[]{xA, xB, "c"}, xs);
    }

    @Test
    public void testSwapIntoSorted2() {
        String[] xs = new String[]{xA, "c", xB};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.swapIntoSorted(xs, 0, 1);
        }
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
    }

    @Test
    public void testSwapIntoSorted3() {
        String[] xs = new String[]{xA, "c", xB};
        try (Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config)) {
            helper.swapIntoSorted(xs, 0, 0);
        }
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
    }

    static Config config;

}