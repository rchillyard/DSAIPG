package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.benchmark.Stopwatch;
import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class NonInstrumentingComparatorHelperTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testSwap() {
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

//    private static void fastSwap(long[] ys, int i, int j) {
//        long temp = ys[i];
//        ys[i] = ys[j];
//        ys[j] = temp;
//    }

    @BeforeClass
    public static void beforeClass() {
        config = setupConfig("", "", "0", "0", "", "");
    }

    private static Config config;

}