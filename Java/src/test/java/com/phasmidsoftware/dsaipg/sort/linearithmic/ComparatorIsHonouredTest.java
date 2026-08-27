package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;

/**
 * Every sort must order by the Helper's comparator, whether or not it is
 * instrumented.
 * <p>
 * Five sorts here got this wrong in the same way. Each has two partition loops --
 * one going through the Helper and a faster one meant only to skip the counting --
 * and the faster one compared with {@code compareTo}, which is the natural
 * ordering and ignores the comparator entirely. So an uninstrumented sort with a
 * custom comparator quietly produced the wrong order:
 * <pre>
 *     QuickSort_Classic   instrument=false  NOT SORTED at 1: "Arab" &gt; "about"
 *     QuickSort_DualPivot instrument=false  NOT SORTED at 1: "Arab" &gt; "about"
 *     QuickSort_Exp       instrument=false  NOT SORTED at 8: "Olympic" &gt; "about"
 *     IntroSort           instrument=false  NOT SORTED at 1: "Arab" &gt; "about"
 * </pre>
 * <p>
 * Two conditions had to meet for it to show, which is why it lasted: a comparator
 * that is not the natural ordering, and instrumentation off. Note that a test
 * using Integer cannot find it, because Integer's natural ordering IS
 * Integer::compare.
 * <p>
 * This runs every sort in the package against both settings, so a sixth instance
 * cannot appear quietly.
 */
public class ComparatorIsHonouredTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Case-insensitive, so it disagrees with String's natural ordering on exactly
     * the pairs below -- uppercase sorts first naturally, but not here.
     */
    private static final Comparator<String> CASE_INSENSITIVE = String.CASE_INSENSITIVE_ORDER;

    private static final List<String> WORDS = Arrays.asList(
            "Arab", "abroad", "British", "bear", "French", "fair", "Italian", "idea",
            "Muslim", "mask", "Olympic", "object", "Republican", "recording", "Spanish",
            "sensitive", "apple", "army", "art", "about", "above", "abuse", "academic",
            "accept", "zebra", "Zulu", "yellow", "Yale", "xylophone", "Xerox", "Delta",
            "dawn", "Echo", "east");

    private void check(String name, Function<Helper<String>, Sort<String>> make, boolean instrumented) {
        String[] xs = WORDS.toArray(new String[0]);
        Config config = setupConfig(String.valueOf(instrumented), "false", "0", "0", "", "");
        Helper<String> helper = HelperFactory.createGeneric(name, CASE_INSENSITIVE, xs.length, 1, config);
        try (Sort<String> sorter = make.apply(helper)) {
            sorter.sort(xs, 0, xs.length);
        }
        String[] expected = WORDS.toArray(new String[0]);
        Arrays.sort(expected, CASE_INSENSITIVE);
        // Compare by the comparator, since equal-but-different strings may legally
        // appear in either order.
        for (int i = 0; i < xs.length; i++)
            assertEquals(name + " (instrumented=" + instrumented + ") at " + i,
                    0, CASE_INSENSITIVE.compare(expected[i], xs[i]));
    }

    private void checkBoth(String name, Function<Helper<String>, Sort<String>> make) {
        check(name, make, false);
        check(name, make, true);
    }

    @Test
    public void testQuickSortClassic() {
        checkBoth("QuickSort_Classic", QuickSort_Classic::new);
    }

    @Test
    public void testQuickSort3way() {
        checkBoth("QuickSort_3way", QuickSort_3way::new);
    }

    @Test
    public void testQuickSortDualPivot() {
        checkBoth("QuickSort_DualPivot", QuickSort_DualPivot::new);
    }

    @Test
    public void testQuickSortExp() {
        checkBoth("QuickSort_Exp", QuickSort_Exp::new);
    }

    @Test
    public void testIntroSort() {
        checkBoth("IntroSort", IntroSort::new);
    }

    @Test
    public void testMergeSort() {
        checkBoth("MergeSort", MergeSort::new);
    }

    @Test
    public void testMergeSortBasic() {
        checkBoth("MergeSortBasic", MergeSortBasic::new);
    }
}
