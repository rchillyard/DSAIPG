package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class SorterBenchmarkTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Before
    public void setUp() throws IOException {
        String[] strings = {"Hello", "Goodbye", "Ciao", "Willkommen"};
        benchmark = new SorterBenchmark<>(String.class, new InsertionSort<>(Config.load(getClass())), strings, 100, new TimeLogger[]{new TimeLogger("test", n -> n * 1.0)});
    }

    @SuppressWarnings("EmptyMethod")
    @After
    public void tearDown() {
        // NOTE: Nothing to do.
    }

    @Test
    public void run() {
        benchmark.run("test", 4);
    }

    SorterBenchmark<String> benchmark = null;

}