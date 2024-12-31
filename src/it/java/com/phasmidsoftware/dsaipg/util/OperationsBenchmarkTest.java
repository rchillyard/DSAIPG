package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.io.IOException;

public class OperationsBenchmarkTest {

    @Test
    public void testRunBenchmarks() throws IOException {
        Config config = Config.load(OperationsBenchmark.class);
        final OperationsBenchmark benchmark = new OperationsBenchmark(config);
        benchmark.runBenchmarks();
    }
}