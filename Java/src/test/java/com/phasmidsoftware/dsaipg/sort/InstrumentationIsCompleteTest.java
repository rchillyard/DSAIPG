package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.elementary.*;
import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.linearithmic.*;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A sort's reported comparison count must equal the number of comparisons it
 * actually made.
 * <p>
 * The comparator counts its own invocations, which gives the true figure whatever
 * the Helper chooses to record. Any shortfall means the sort is doing work behind
 * the Helper's back, and its published statistics are wrong.
 * <p>
 * This exists because TimSort's were, badly. It was reimplemented from the JDK so
 * that it could be instrumented, and then only binarySort ever was; run detection,
 * galloping and merging went through nothing that counted. On 1,000 random ints it
 * reported 3,677 comparisons out of 8,702 actually made, and on already-sorted
 * input it reported 0 out of 999 -- and zero is impossible for any comparison
 * sort, which must make at least n - 1 comparisons to establish an order. The
 * sorted case was the tell: on sorted input the run detector finds one run and
 * binarySort, the only instrumented method, is never called at all.
 * <p>
 * That is fixed. TimSortWrapper now passes the same check as every other sort:
 * 8,670 comparisons reported out of 8,670 made on random input, 999 out of 999 on
 * sorted input, and hits up from 4,601 to 39,836 once the reads, the writes and
 * the block moves were counted too.
 * <p>
 * NOTE the information-theoretic bound lg(n!) does NOT settle this on its own: it
 * bounds the worst case over all inputs, not any particular input, and a sort may
 * legitimately do far fewer comparisons on input that is already ordered. Which is
 * exactly what Timsort is built to exploit. Counting directly avoids the question.
 */
public class InstrumentationIsCompleteTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private static final int N = 500;

    private static Integer[] randomArray() {
        Random random = new Random(42);
        Integer[] xs = new Integer[N];
        for (int i = 0; i < N; i++) xs[i] = random.nextInt(10000);
        return xs;
    }

    private static Integer[] sortedArray() {
        Integer[] xs = new Integer[N];
        for (int i = 0; i < N; i++) xs[i] = i;
        return xs;
    }

    /**
     * @param name  the sort's name, for the failure message.
     * @param make  builds the sort from a Helper.
     * @param xs    the input, which is sorted in place.
     * @return the number of comparisons NOT counted by the Helper.
     */
    private long uncounted(String name, Function<Helper<Integer>, Sort<Integer>> make, Integer[] xs) {
        AtomicLong actual = new AtomicLong();
        Comparator<Integer> counting = (a, b) -> {
            actual.incrementAndGet();
            return Integer.compare(a, b);
        };
        // NOTE fixes MUST stay off (the second argument). enumerateFixes compares
        // through pureComparison, which reaches the comparator but deliberately
        // does not count as a comparison -- so switching fixes on makes every
        // sort look as though it were under-counting. Measured on InsertionSort
        // with 200 elements: reported 9,777 either way, actual 9,777 with fixes
        // off and 19,359 with them on.
        Config config = setupConfig("true", "false", "0", "1", "", "");
        Helper<Integer> helper = HelperFactory.createGeneric(name, counting, N, 1, config);
        try (Sort<Integer> sorter = make.apply(helper)) {
            sorter.sort(xs, 0, xs.length);
        }
        long reported = helper.getCompares();
        // NOTE read the counters BEFORE checking sortedness: isSorted compares
        // through pureComparison, which the counting comparator sees but the
        // Helper deliberately does not count.
        long missing = actual.get() - reported;
        for (int i = 1; i < xs.length; i++)
            assertTrue(name + " did not sort, at index " + i, xs[i] >= xs[i - 1]);
        return missing;
    }

    private void check(String name, Function<Helper<Integer>, Sort<Integer>> make) {
        assertEquals(name + ": comparisons made but not counted, on random input",
                0, uncounted(name, make, randomArray()));
        assertEquals(name + ": comparisons made but not counted, on sorted input",
                0, uncounted(name, make, sortedArray()));
    }

    @Test
    public void testInsertionSort() {
        check("InsertionSort", InsertionSort::new);
    }

    @Test
    public void testInsertionSortOpt() {
        check("InsertionSortOpt", InsertionSortOpt::new);
    }

    @Test
    public void testBubbleSort() {
        check("BubbleSort", BubbleSort::new);
    }

    @Test
    public void testSelectionSort() {
        check("SelectionSort", SelectionSort::new);
    }

    @Test
    public void testHeapSort() {
        check("HeapSort", HeapSort::new);
    }

    @Test
    public void testMergeSort() {
        check("MergeSort", MergeSort::new);
    }

    @Test
    public void testMergeSortBasic() {
        check("MergeSortBasic", MergeSortBasic::new);
    }

    @Test
    public void testQuickSortClassic() {
        check("QuickSort_Classic", QuickSort_Classic::new);
    }

    @Test
    public void testQuickSort3way() {
        check("QuickSort_3way", QuickSort_3way::new);
    }

    @Test
    public void testQuickSortDualPivot() {
        check("QuickSort_DualPivot", QuickSort_DualPivot::new);
    }

    @Test
    public void testQuickSortExp() {
        check("QuickSort_Exp", QuickSort_Exp::new);
    }

    @Test
    public void testIntroSort() {
        check("IntroSort", IntroSort::new);
    }

    /**
     * TimSortWrapper is the reason this test exists: it wraps the JDK's sort, so
     * every array access has to be routed through the Helper deliberately rather
     * than falling out of the implementation.
     */
    @Test
    public void testTimSortWrapper() {
        check("TimSortWrapper", TimSortWrapper::new);
    }
}
