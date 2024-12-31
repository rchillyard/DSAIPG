package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.io.IOException;

public class SortBenchmarkFuncTest {

    @Test
    public void testDoMain() throws IOException {
        Config config1 = Config.load(SortBenchmark.class).copy("helper", "instrument", "false");
        new SortBenchmark(config1).doMain(new String[]{"1000"});
        Config config2 = config1.copy("helper", "instrument", "true");
        new SortBenchmark(config2).doMain(new String[]{"1000"});
    }
}