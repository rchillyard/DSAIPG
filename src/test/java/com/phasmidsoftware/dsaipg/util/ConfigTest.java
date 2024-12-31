package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper;
import com.phasmidsoftware.dsaipg.sort.Instrumenter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;
import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void testConfig() throws IOException {
        final Config config = Config.load();
        String name = config.get("main", "version");
        System.out.println("ConfigTest: " + name);
    }

    @Test
    public void testConfigFixed() {
        final Config config = setupConfig(TRUE, "false", "0", "10", "", "");
        assertTrue(isInstrumented(config));
        assertEquals(0L, Config_Benchmark.getSeed(config));
        assertEquals(10, config.getInt(INSTRUMENTING, INVERSIONS, 0));
    }

    @Test
    public void testCopy1() {
        final Config config = setupConfig(FALSE, "false", "0", "", "", "");
        long originalSeed = Config_Benchmark.getSeed(config);
        Config config1 = config.copy(HELPER, SEED, "1");
        assertEquals(originalSeed, Config_Benchmark.getSeed(config));
        assertEquals(1, Config_Benchmark.getSeed(config1));
    }

    @Test
    public void testCopy2() {
        final Config config = setupConfig(FALSE, "false", "", "", "", "");
        String junk = "junk";
        assertEquals(-1, config.getInt(HELPER, junk, -1));
        Config config1 = config.copy(HELPER, junk, "1");
        assertEquals(1, config1.getInt(HELPER, junk, -1));
    }

    @Test
    public void testCopy3() {
        final Config config = setupConfig(FALSE, "false", "0", "10", "", "");
        Config config1 = config.copy(INSTRUMENTING, INVERSIONS, "20");

        // Verify the original configuration is untouched
        assertEquals(10, config.getInt(INSTRUMENTING, INVERSIONS, 0));
        // Verify the copied configuration has the updated value
        assertEquals(20, config1.getInt(INSTRUMENTING, INVERSIONS, 0));
    }

    @Test
    public void testCopy4() {
        final Config config = setupConfig(FALSE, "false", "0", "10", "", "");
        Config config1 = config.copy("newSection", "newOption", "newValue");

        // Verify the original configuration does not include the new section
        assertNull(config.get("newSection", "newOption"));
        // Verify the copied configuration includes the new section and value
        assertEquals("newValue", config1.get("newSection", "newOption"));
    }

    // NOTE: we ignore this for now, because this would need to run before any other tests in order to work as originally designed.
    @Ignore
    public void testUnLogged() throws IOException {
        final Config config = Config.load();
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(config);
        assertTrue((Boolean) privateMethodTester.invokePrivate("unLogged", HELPER + "." + SEED));
        assertFalse((Boolean) privateMethodTester.invokePrivate("unLogged", HELPER + "." + SEED));
    }

    public static final String TRUE = "true";
    public static final String FALSE = "";
    public static final String INSTRUMENTING = InstrumentedComparatorHelper.INSTRUMENTING;
    public static final String INVERSIONS = Instrumenter.INVERSIONS;
    public static final String SWAPS = Instrumenter.SWAPS;
    public static final String COMPARES = Instrumenter.COMPARES;
    public static final String COPIES = Instrumenter.COPIES;
    public static final String FIXES = Instrumenter.FIXES;
    public static final String INSURANCE = "";
    public static final String NOCOPY = "";
}