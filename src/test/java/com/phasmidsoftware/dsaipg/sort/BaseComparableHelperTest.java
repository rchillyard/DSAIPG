package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Utilities;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import static org.junit.Assert.*;

public class BaseComparableHelperTest {

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
        @Override
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

        @Override
        public Helper<X> clone(String description, int N) {
            return null;
        }
    }

    @Test
    public void instrumented() {
        assertFalse(new BaseComparableHelperWithSortedTest<String>().instrumented());
    }

    @Test
    public void less() {
        assertTrue(new BaseComparableHelperWithSortedTest<String>().less(xA, xB));
    }

    @Test
    public void compare() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        assertEquals(-1, helper.compare(xs, 0, 1));
        assertEquals(0, helper.compare(xs, 0, 0));
        assertEquals(1, helper.compare(xs, 1, 0));
    }

    @Test
    public void swap0() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        helper.swap(xs, 0, 1);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.swap(xs, 0, 1);
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void swap1() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        helper.swap(xs, xA, 0, 1);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.swap(xs, xB, 0, 1);
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void swap2() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        helper.swap(xs, 0, 1, xB);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.swap(xs, 0, 1, xA);
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void swap3() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        helper.swap(xs, xA, 0, 1, xB);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.swap(xs, xB, 0, 1, xA);
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void sorted() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        assertTrue(helper.isSorted(xs));
        helper.swap(xs, 0, 1);
        assertEquals(1, helper.findInversion(xs));
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
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        helper.postProcess(xs);
    }

    @Test(expected = HelperException.class)
    public void postProcess2() {
        String[] xs = new String[]{xB, xA};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>();
        helper.postProcess(xs);
    }

    @Test
    public void testRandom() {
        String[] words = new String[]{"Hello", "World"};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L);
        final String[] strings = helper.random(String.class, r -> words[r.nextInt(2)]);
        assertArrayEquals(new String[]{"World", "World", "Hello"}, strings);
    }

    @Test
    public void testOrdered() {
        String[] words = new String[]{"Hello", "World"};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L);
        final String[] strings = helper.ordered(2, String.class, i -> words[i]);
        assertArrayEquals(new String[]{"Hello", "World"}, strings);
    }

    @Test
    public void testPartialOrdered() {
        String[] words = new String[]{"Hello", "World"};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L);
        final String[] strings = helper.partialOrdered(2, String.class, i -> words[i]);
        assertArrayEquals(new String[]{"World", "Hello"}, strings);
    }

    @Test
    public void testReverse() {
        String[] words = new String[]{"Hello", "World"};
        final Helper<String> helper = new BaseComparableHelperWithSortedTest<>(3, 0L);
        final String[] strings = helper.reverse(2, String.class, i -> words[i]);
        assertArrayEquals(new String[]{"World", "Hello"}, strings);
    }

    @Test
    public void testToString() {
        final AutoCloseable helper = new NonInstrumentingComparableHelper<>("test", 3, config);
        assertEquals("Helper for test with 3 elements", helper.toString());
    }

    @Test
    public void getDescription() {
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", 3, config);
        assertEquals("test", helper.getDescription());
    }

    @Test(expected = RuntimeException.class)
    public void getSetN() {
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", 3, config);
        assertEquals(3, helper.getN());
        helper.init(4);
        assertEquals(4, helper.getN());
    }

    @Test
    public void getSetNBis() {
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        assertEquals(0, helper.getN());
        helper.init(4);
        assertEquals(4, helper.getN());
    }

    @Test
    public void close() throws Exception {
        final AutoCloseable helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.close();
    }

    @Test
    public void swapStable() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.swapStable(xs, 1);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.swapStable(xs, 1);
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void fixInversion1() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.fixInversion(xs, 1); // XXX Deprecated
        assertArrayEquals(new String[]{xA, xB}, xs);
        helper.swapStable(xs, 1);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.fixInversion(xs, 1); // XXX Deprecated
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void testFixInversion2() {
        String[] xs = new String[]{xA, xB};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.fixInversion(xs, 0, 1);
        assertArrayEquals(new String[]{xA, xB}, xs);
        helper.swap(xs, 0, 1);
        assertArrayEquals(new String[]{xB, xA}, xs);
        helper.fixInversion(xs, 0, 1);
        assertArrayEquals(new String[]{xA, xB}, xs);
    }

    @Test
    public void testSwapInto() {
        String[] xs = new String[]{xA, xB, "c"};
        final NonComparableHelper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.swapInto(xs, 0, 2);
        assertArrayEquals(new String[]{"c", xA, xB}, xs);
        helper.swapInto(xs, 0, 1);
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
        helper.swapInto(xs, 0, 0);
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
    }


    @Test
    public void testSwapIntoSorted0() {
        String[] xs = new String[]{xA, xB, "c"};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.swapIntoSorted(xs, 2);
        assertArrayEquals(new String[]{xA, xB, "c"}, xs);
    }

    @Test
    public void testSwapIntoSorted1() {
        String[] xs = new String[]{xA, "c", xB};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.swapIntoSorted(xs, 2);
        assertArrayEquals(new String[]{xA, xB, "c"}, xs);
    }

    @Test
    public void testSwapIntoSorted2() {
        String[] xs = new String[]{xA, "c", xB};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.swapIntoSorted(xs, 1);
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
    }

    @Test
    public void testSwapIntoSorted3() {
        String[] xs = new String[]{xA, "c", xB};
        final Helper<String> helper = new NonInstrumentingComparableHelper<>("test", config);
        helper.swapIntoSorted(xs, 0);
        assertArrayEquals(new String[]{xA, "c", xB}, xs);
    }

    static Config config;

}