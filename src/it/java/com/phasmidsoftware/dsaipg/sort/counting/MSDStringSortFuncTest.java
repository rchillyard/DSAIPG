package com.phasmidsoftware.dsaipg.sort.counting;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_3way;
import com.phasmidsoftware.dsaipg.util.*;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.phasmidsoftware.dsaipg.sort.Instrument.HITS;
import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.HELPER;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;

public class MSDStringSortFuncTest {

    final String[] input = "she sells seashells by the seashore the shells she sells are surely seashells".split(" ");
    final String[] expected = "are by seashells seashells seashore sells sells she she shells surely the the".split(" ");

    @Test
    public void testQ256() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortFuncTest::lineAsList);
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(33_223_218L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "256"), 1_048_576));
    }

    @Test
    public void testQ512() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortFuncTest::lineAsList);
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(34_142_279L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "512"), 1_048_576));
    }

    @Test
    public void testQ2048() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(38_106_181L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "2048"), 1_048_576));
    }

    @Test
    public void testQ4096() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(41_782_532L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "4096"), 1_048_576));
    }

    @Test
    public void testQ8192() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(45_142_230L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "8192"), 1_048_576));
    }

    @Test
    public void testQ16384() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(48_315_204L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "16384"), 1_048_576));
    }

    //    @Test XXX this is slow
    public void testQ32M() {
        Config baseConfig = setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(1_366_801_150L, runMSD(baseConfig.copy(HELPER, "msdcutoff", "256"), 32_768_000));
    }

    @Test
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
        try {
            final File file = new File(getPathname(resource, MSDStringSortFuncTest.class));
            final String[] result = getWordArray(file, stringListFunction, 2);
            System.out.println("getWords: testing with " + Utilities.formatWhole(result.length) + " unique words: from " + file);
            return result;
        } catch (final FileNotFoundException e) {
            System.out.println("Cannot find resource: " + resource);
            return new String[0];
        }
    }

    private static List<String> getWordList(final FileReader fr, final Function<String, List<String>> stringListFunction, final int minLength) {
        final List<String> words = new ArrayList<>();
        for (final Object line : new BufferedReader(fr).lines().toArray())
            words.addAll(stringListFunction.apply((String) line));
        return words.stream().distinct().filter(s -> s.length() >= minLength).collect(Collectors.toList());
    }


    /**
     * Method to read given file and return a String[] of its content.
     *
     * @param file               the file to read.
     * @param stringListFunction a function which takes a String and splits into a List of Strings.
     * @param minLength          the minimum acceptable length for a word.
     * @return an array of Strings.
     */
    static String[] getWordArray(final File file, final Function<String, List<String>> stringListFunction, final int minLength) {
        try (final FileReader fr = new FileReader(file)) {
            return getWordList(fr, stringListFunction, minLength).toArray(new String[0]);
        } catch (final IOException e) {
            System.out.println("Cannot open file: " + file);
            return new String[0];
        }
    }

    static List<String> lineAsList(final String line) {
        final List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }

    private static String getPathname(final String resource, @SuppressWarnings("SameParameterValue") final Class<?> clazz) throws FileNotFoundException {
        final URL url = clazz.getClassLoader().getResource(resource);
        if (url != null) return url.getPath();
        throw new FileNotFoundException(resource + " in " + clazz);
    }

}