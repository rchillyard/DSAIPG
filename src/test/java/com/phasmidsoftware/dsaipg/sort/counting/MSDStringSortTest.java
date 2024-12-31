package com.phasmidsoftware.dsaipg.sort.counting;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort;
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
import static org.junit.Assert.*;

public class MSDStringSortTest {

    final String[] input = "she sells seashells by the seashore the shells she sells are surely seashells".split(" ");
    final String[] expected = "are by seashells seashells seashore sells sells she she shells surely the the".split(" ");

    @Test
    public void sort0() {
        int n = input.length;
        Config config = setupConfig("true", "false", "0", "1", "4", "").copy(HELPER, "msdcutoff", "10");
        try (Sort<String> sorter = new MSDStringSort(CodePointMapper.ASCIIExt, n, 1, config)) {
            String[] sorted = sorter.sort(input);
            System.out.println(Arrays.toString(sorted));
            assertArrayEquals(expected, sorted);
        }
    }

    @Test
    public void sort1() throws IOException {
        int n = 1000;
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = Config.load(MSDStringSortTest.class).copy(HELPER, "seed", "1");
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, n, 1, config)) {
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            assertEquals(n, xs.length);
            String[] ys = sorter.sort(xs);
            System.out.println(Arrays.toString(ys));
            assertEquals("African-American", ys[0]);
            assertEquals("Palestinian", ys[16]);

        }
    }

    @Test
    public void sort2() throws IOException {
        int n = input.length;
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, Config.load(MSDStringSortTest.class), 1)) {
            String[] sorted = sorter.sort(input);
            System.out.println(Arrays.toString(sorted));
            assertArrayEquals(expected, sorted);
        }
    }

    @Test
    public void sort3() throws IOException {
        int n = 100;
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, Config.load(MSDStringSortTest.class), 1)) {
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            assertEquals(n, xs.length);
            String[] ys = sorter.sort(xs);
            System.out.println(Arrays.toString(ys));
            assertTrue(helper.isSorted(ys));
        }
    }

    @Test
    public void sort4() throws IOException {
        int n = 1000;
        int seed = 948;
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = Config.load(MSDStringSortTest.class).copy(HELPER, "seed", String.valueOf(seed));
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, config, 1)) {
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            assertEquals(n, xs.length);
            String[] ys = sorter.sort(xs);
            System.out.println(Arrays.toString(ys));
            assertTrue(helper.isSorted(ys));
        }
    }

    //    @Test
    public void sort5() throws IOException {
        int n = 1000;
        for (int seed = 0; seed < 1000; seed++) {
            System.out.println(seed);
            String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
            Config config = Config.load(MSDStringSortTest.class).copy(HELPER, "seed", String.valueOf(seed));
            try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, config, 1)) {
                Helper<String> helper = sorter.getHelper();
                final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
                assertEquals(n, xs.length);
                String[] ys = sorter.sort(xs);
                assertTrue(helper.isSorted(ys));
            }
        }
    }

    @Test
    public void testX() {
        // CONSIDER this is the only reference to QuickSortThreeWayByFunction -- do we really need this test?
        Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<String> helper = HelperFactory.create("quick sort", input.length, config);
        QuickSort<String> sorter = new MSDStringSort.QuickSortThreeWayByFunction(helper);
        String[] sorted = sorter.sort(input);
        System.out.println(Arrays.toString(sorted));
        assertArrayEquals(expected, sorted);
    }

    @Test
    public void testY() {
        Config config = setupConfig("true", "false", "0", "1", "1", "").copy(HELPER, "msdcutoff", "1");
        MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", input.length, config, 1);
        AutoCloseable helper = sorter.getHelper();
        String[] sorted = sorter.sort(input);
        sorter.close();
        System.out.println(Arrays.toString(sorted));
        assertArrayEquals(expected, sorted);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        assertEquals(0L, privateMethodTester.invokePrivate("getSwaps"));
        assertEquals(0L, privateMethodTester.invokePrivate("getCompares"));
        assertEquals(124L, privateMethodTester.invokePrivate("getCopies"));
        // NOTE this was only 310 before. Please check.
        assertEquals(4962L, privateMethodTester.invokePrivate("getHits"));
    }

    @Test
    public void testZ0() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = setupConfig("true", "false", "0", "1", "16", "").copy(HELPER, "msdcutoff", "1");
        MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", 1024, config, 1);
        Helper<String> helper = sorter.getHelper();
        final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
        String[] sorted = sorter.sort(xs);
        sorter.close();
        assertTrue(helper.isSorted(sorted));
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        // TODO check these values
        assertEquals(0L, privateMethodTester.invokePrivate("getSwaps"));
        assertEquals(0L, privateMethodTester.invokePrivate("getCompares"));
        assertEquals(10436L, privateMethodTester.invokePrivate("getCopies"));
        // NOTE this was previousl just 26090. Please check it.
        assertEquals(293703L, privateMethodTester.invokePrivate("getHits"));
    }

    @Test
    public void testZ1() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = setupConfig("true", "false", "0", "1", "1", "").copy(HELPER, "msdcutoff", "128");
        MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", 1024, config, 1);
        Helper<String> helper = sorter.getHelper();
        final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
        String[] sorted = sorter.sort(xs);
        sorter.close();
        assertTrue(helper.isSorted(sorted));
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        // TODO check these values
        assertEquals(4157L, privateMethodTester.invokePrivate("getSwaps"));
        assertEquals(4895L, privateMethodTester.invokePrivate("getCompares"));
        // NOTE this was 16979 before. Please check.
        assertEquals(2308L, privateMethodTester.invokePrivate("getCopies"));
        assertEquals(25293L, privateMethodTester.invokePrivate("getHits"));
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
            final File file = new File(getPathname(resource, MSDStringSortTest.class));
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

    @Test
    public void testDoMSDrecursiveBasic() {
        Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<String> helper = HelperFactory.create("quick sort", input.length, config);
        final String[] input = {"banana", "apple", "grape", "orange"};
        final String[] expected = {"apple", "banana", "grape", "orange"};
        final MSDStringSort msdStringSort = new MSDStringSort(CodePointMapper.ASCII, helper);
        msdStringSort.doMSDrecursive(input, 0, input.length, 0);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testDoMSDrecursiveEmptyArray() {
        Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<String> helper = HelperFactory.create("quick sort", input.length, config);
        final String[] input = {};
        final String[] expected = {};
        final MSDStringSort msdStringSort = new MSDStringSort(CodePointMapper.ASCII, helper);
        msdStringSort.doMSDrecursive(input, 0, input.length, 0);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testDoMSDrecursiveSingleElement() {
        Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<String> helper = HelperFactory.create("quick sort", input.length, config);
        final String[] input = {"element"};
        final String[] expected = {"element"};
        final MSDStringSort msdStringSort = new MSDStringSort(CodePointMapper.ASCII, helper);
        msdStringSort.doMSDrecursive(input, 0, input.length, 0);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testDoMSDrecursiveBoundaryCondition() {
        Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<String> helper = HelperFactory.create("quick sort", input.length, config);
        final String[] input = {"apple", "apply", "app", "application"};
        final String[] expected = {"app", "apple", "application", "apply"};
        final MSDStringSort msdStringSort = new MSDStringSort(CodePointMapper.ASCII, helper);
        msdStringSort.doMSDrecursive(input, 0, input.length, 3);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testDoMSDrecursiveWithDuplication() {
        Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<String> helper = HelperFactory.create("quick sort", input.length, config);
        final String[] input = {"duplicate", "duplicate", "unique", "duplicate"};
        final String[] expected = {"duplicate", "duplicate", "duplicate", "unique"};
        final MSDStringSort msdStringSort = new MSDStringSort(CodePointMapper.ASCII, helper);
        msdStringSort.doMSDrecursive(input, 0, input.length, 0);
        assertArrayEquals(expected, input);
    }

}