package com.phasmidsoftware.dsaipg.sort.counting;

import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_3way;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.benchmark.SortBenchmark;
import com.phasmidsoftware.dsaipg.util.benchmark.SortBenchmarkHelper;
import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.CodePointMapper;
import com.phasmidsoftware.dsaipg.util.general.Utilities;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.phasmidsoftware.dsaipg.sort.helper.Instrument.HITS;
import static com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper.getRunsConfig;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.HELPER;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;

/**
 * A class to test the functionality and performance of MSD (Most Significant Digit) string sorting algorithm.
 * This class contains test methods to evaluate the sorting performance under different configurations,
 * as well as utility methods to retrieve and process words from various resources.
 * <p>
 * FIXME restore the tests. Maybe it has something to do with instrumentation and the config.ini file in the test directory
 */
public class MSDStringSortFuncTest {

    final String[] input = "she sells seashells by the seashore the shells she sells are surely seashells".split(" ");
    final String[] expected = "are by seashells seashells seashore sells sells she she shells surely the the".split(" ");

    public void testQ256() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortFuncTest::lineAsList);
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(33_223_218L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "256"), 1_048_576));
    }

    public void testQ512() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortFuncTest::lineAsList);
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(34_142_279L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "512"), 1_048_576));
    }

    public void testQ2048() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(38_106_181L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "2048"), 1_048_576));
    }

    public void testQ4096() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(41_782_532L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "4096"), 1_048_576));
    }

    public void testQ8192() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(45_142_230L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "8192"), 1_048_576));
    }

    public void testQ16384() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(48_315_204L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "16384"), 1_048_576));
    }

    //    @Test XXX this is slow
    public void testQ32M() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(1_366_801_150L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "256"), 32_768_000));
    }

    public void testQuick() {
        Config config = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(81_132_989L, runQuick(config));
    }

    private static long runMSD(Config config, final int n) {
        String resource = "eng-uk_web_2002_" + "100K" + "-sentences.txt";
        try {
            String[] words = SortBenchmarkHelper.getWords(resource, SortBenchmark::getLeipzigWords);
            MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", n, config, 1);
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            String[] sorted = sorter.sort(xs);
            helper.postProcess(sorted);
            sorter.close();
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
            return (long) statPack.getStatistics(HITS).mean();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return -1L;
        }
    }

    private static long runQuick(Config config) {
        String resource = "eng-uk_web_2002_" + "100K" + "-sentences.txt";
        try {
            String[] words = SortBenchmarkHelper.getWords(resource, SortBenchmark::getLeipzigWords);
            SortWithHelper<String> sorter = new QuickSort_3way<>(1048576, getRunsConfig(config), config);
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            System.out.println(Arrays.toString(Arrays.stream(xs, 0, 200).toArray()));
            String[] sorted = sorter.sort(xs);
            sorter.close();
            helper.isSorted(sorted);
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            return (Long) privateMethodTester.invokePrivate("getHits");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return -1L;
        }
    }

    /**
     * Method to open a resource relative to this class and from the corresponding File, get an array of Strings.
     *
     * @param resource           the URL of the resource containing the Strings required.
     * @param stringListFunction a function which takes a String and splits into a List of Strings.
     * @return an array of Strings.
     */
    static String[] getWords(final String resource, final Function<String, List<String>> stringListFunction) {
        // Delegates to SortBenchmarkHelper, which reads the resource as a stream. The private
        // copies that used to live here went via `url.getPath()`, which does not URL-decode, so a
        // space anywhere in the path arrived as "%20" and the resource was not found. They then
        // swallowed that and returned an empty array, which surfaced in every test as
        // "IllegalArgumentException: bound must be positive" from `r.nextInt(words.length)` --
        // pointing nowhere near the cause.
        try {
            final String[] result = SortBenchmarkHelper.getWords(resource, stringListFunction::apply);
            System.out.println("getWords: testing with " + Utilities.formatWholeWithCommas(result.length) + " unique words: from " + resource);
            return result;
        } catch (final FileNotFoundException e) {
            throw new AssertionError("test resource not found: " + resource, e);
        }
    }


    static List<String> lineAsList(final String line) {
        final List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }


}