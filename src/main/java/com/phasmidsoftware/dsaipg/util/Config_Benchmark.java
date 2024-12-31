/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.sort.BaseHelper;
import com.phasmidsoftware.dsaipg.sort.Instrumenter;
import org.ini4j.Ini;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparableHelper.*;
import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.INSURANCE;
import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.NOCOPY;

/**
 * Utility class to handle configuration settings and related operations for benchmarking
 * and testing.
 * This class contains methods for setting up and retrieving configuration details,
 * as well as providing default settings for various parameters.
 */
public class Config_Benchmark {

    public static final String HELPER = "helper";
    public static final String INSTRUMENT = BaseHelper.INSTRUMENT;
    public static final String SEED = "seed";
    public static final String CUTOFF = "cutoff";
    public static final String MSDCUTOFF = "msdcutoff";
    public static final int CUTOFF_DEFAULT = 20;

    /**
     * Retrieves the seed value from the configuration.
     * If the seed value is not explicitly set or cannot be retrieved,
     * the current system time in milliseconds is used as the default value.
     *
     * @return the seed value as a long, or the current system time in milliseconds if not specified.
     */
    public static long getSeed(Config config) {
        return config.getLong(HELPER, SEED, System.currentTimeMillis());
    }

    /**
     * Method to determine if this configuration has an instrumented helper.
     * NOTE: we would prefer to place this logic in the Helper class but we put it here for now.
     *
     * @return true if helper is instrumented
     */
    public static boolean isInstrumented(Config config) {
        return config.getBoolean(HELPER, INSTRUMENT);
    }

    /**
     * Configures and sets up a new Config object based on the input parameters.
     * The configuration includes various settings for instrumentation, seed, cutoffs,
     * inversions, fixes, and interim inversions.
     *
     * @param instrumenting     the instrumenting value used for configuration, applied to multiple metrics
     *                          such as swaps, compares, and copies.
     * @param fixes             the value that specifies the number of fixes to be configured.
     * @param seed              the seed value used for initialization in the configuration.
     * @param inversions        the inversions setting to be applied within the configuration.
     * @param cutoff            the cutoff value used for configuring the benchmark helper.
     * @param interimInversions the value for interim inversions used in the configuration.
     * @return a new Config object containing the configured settings.
     */
    public static Config setupConfig(final String instrumenting, String fixes, final String seed, final String inversions, String cutoff, String interimInversions) {
        final Ini ini = new Ini();
        final String sInstrumenting = INSTRUMENTING;
        ini.put(Config_Benchmark.HELPER, Config_Benchmark.INSTRUMENT, instrumenting);
        ini.put(Config_Benchmark.HELPER, SEED, seed);
        ini.put(Config_Benchmark.HELPER, CUTOFF, cutoff);
        ini.put(sInstrumenting, Instrumenter.INVERSIONS, inversions);
        ini.put(sInstrumenting, SWAPS, instrumenting);
        ini.put(sInstrumenting, COMPARES, instrumenting);
        ini.put(sInstrumenting, COPIES, instrumenting);
        ini.put(sInstrumenting, FIXES, fixes);
        ini.put(sInstrumenting, HITS, instrumenting);
        ini.put(sInstrumenting, LOOKUPS, instrumenting);
        ini.put("huskyhelper", "countinteriminversions", interimInversions);
        return new Config(ini);
    }


    /**
     * Configures and sets up a new Config object based on the provided parameters.
     * The configuration includes various settings such as instrumentation, seed, inversions,
     * cutoffs, interim inversions, insurance, and whether copying is disabled.
     *
     * @param instrumenting the instrumenting value to be applied within the configuration,
     *                      used for metrics such as swaps, compares, and copies.
     * @param seed the seed value used for initialization in the configuration.
     * @param inversions the value specifying the inversions setting for the configuration.
     * @param cutoff the cutoff value used for configuring the helper in the benchmark.
     * @param interimInversions the value for interim inversions used in the configuration.
     * @param insurance the insurance value for additional configuration.
     * @param noCopy the value indicating whether copying is disabled in the configuration.
     * @return a new Config object containing the configured settings.
     */
    public static Config setupConfig2(final String instrumenting, final String seed, final String inversions, String cutoff, String interimInversions, String insurance, String noCopy) {
        final Ini ini = new Ini();
        final String sInstrumenting = INSTRUMENTING;
        ini.put(Config_Benchmark.HELPER, Config_Benchmark.INSTRUMENT, instrumenting);
        ini.put(Config_Benchmark.HELPER, SEED, seed);
        ini.put(Config_Benchmark.HELPER, CUTOFF, cutoff);
        ini.put(sInstrumenting, Instrumenter.INVERSIONS, inversions);
        ini.put(sInstrumenting, SWAPS, instrumenting);
        ini.put(sInstrumenting, COMPARES, instrumenting);
        ini.put(sInstrumenting, COPIES, instrumenting);
        ini.put(sInstrumenting, FIXES, instrumenting);
        ini.put("huskyhelper", "countinteriminversions", interimInversions);
        ini.put(Config_Benchmark.HELPER, INSURANCE, insurance);
        ini.put(Config_Benchmark.HELPER, NOCOPY, noCopy);
        return new Config(ini);
    }

    /**
     * Configures and sets up a new Config object with predefined settings.
     * This method initializes a configuration with specific options, including enabling instrumented helpers,
     * setting a default cutoff value, and enabling fixes during instrumentation.
     *
     * @return a new Config object pre-configured with the defined settings.
     */
    public static Config setupConfigFixes() {
        final Ini ini = new Ini();
        ini.put(Config_Benchmark.HELPER, Config_Benchmark.INSTRUMENT, true);
        ini.put(Config_Benchmark.HELPER, CUTOFF, CUTOFF_DEFAULT);
        ini.put(INSTRUMENTING, FIXES, true);
        return new Config(ini);
    }

}