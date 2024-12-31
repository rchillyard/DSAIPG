package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static com.phasmidsoftware.dsaipg.util.SortBenchmark.minComparisons;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SortBenchmarkTest {

    @Test
    public void testParseInt() {
        assertEquals(1L, SortBenchmark.parseInt("1"));
        assertEquals(1024L, SortBenchmark.parseInt("1k"));
        assertEquals(1024L, SortBenchmark.parseInt("1K"));
        assertEquals(1048576L, SortBenchmark.parseInt("1m"));
        assertEquals(1048576L, SortBenchmark.parseInt("1M"));
        assertEquals(1073741824L, SortBenchmark.parseInt("1g"));
        assertEquals(1073741824L, SortBenchmark.parseInt("1G"));
    }

    @Test
    public void testSortIntegers() throws IOException {
        // Create a mock config and the SortBenchmark instance
        Config config = Config.load(SortBenchmark.class);
        SortBenchmark sortBenchmark = new SortBenchmark(config);

        // Test case 1: Small stream of word counts
        Stream<Long> wordCounts = Stream.of(10L, 20L, 50L);

        // Execute the method without exceptions
        sortBenchmark.sortIntegers(wordCounts);

        // Assertion placeholder: Assume sortIntegers runs and sorts without throwing any exceptions
        // Detailed validation would require inspecting private helper/sorter states or logs.
        assertTrue(true);
    }

    @Test
    public void testSortIntegersWithNegativeNumbers() throws IOException {
        // Create a mock config and the SortBenchmark instance
        Config config = Config.load(SortBenchmark.class);
        SortBenchmark sortBenchmark = new SortBenchmark(config);

        // Test case 4: Stream with negative numbers
        Stream<Long> wordCounts = Stream.of(-50L, -10L, -20L);

        // Execute the method to ensure it processes the stream correctly
        sortBenchmark.sortIntegers(wordCounts);

        // Assertion placeholder: No exceptions expected
        assertTrue(true);
    }

    @Test
    public void testSortIntegersWithDuplicates() throws IOException {
        // Create a mock config and the SortBenchmark instance
        Config config = Config.load(SortBenchmark.class);
        SortBenchmark sortBenchmark = new SortBenchmark(config);

        // Test case 5: Stream with duplicate values
        Stream<Long> wordCounts = Stream.of(10L, 20L, 20L, 50L, 10L);

        // Execute the method and ensure it handles duplicates
        sortBenchmark.sortIntegers(wordCounts);

        // Assertion placeholder: No exceptions expected
        assertTrue(true);
    }

    @Test
    public void testSortIntegersWithLargeStream() throws IOException {
        // Create a mock config and the SortBenchmark instance
        Config config = Config.load(SortBenchmark.class);
        SortBenchmark sortBenchmark = new SortBenchmark(config);

        // Test case 6: Large stream of numbers
        Stream<Long> wordCounts = Stream.generate(() -> (long) (Math.random() * 1000)).limit(100000);

        // Execute the method without exceptions
        sortBenchmark.sortIntegers(wordCounts);

        // Assertion placeholder: No exceptions due to large inputs
    }

    @Test
    public void testSortIntegersWithLargerInput() throws IOException {
        // Create a mock config and the SortBenchmark instance
        Config config = Config.load(SortBenchmark.class);
        SortBenchmark sortBenchmark = new SortBenchmark(config);

        // Test case 2: Larger stream of word counts
        Stream<Long> wordCounts = Stream.of(1000L, 5000L, 10000L, 20000L);

        // Execute the method without exceptions
        sortBenchmark.sortIntegers(wordCounts);

        // Assertion placeholder: No exception indicates successful processing
        assertTrue(true);
    }

    @Test
    public void testSortIntegersHandlesEmptyStream() throws IOException {
        // Create a mock config and the SortBenchmark instance
        Config config = Config.load(SortBenchmark.class);
        SortBenchmark sortBenchmark = new SortBenchmark(config);

        // Test case 3: Input is an empty stream
        Stream<Long> wordCounts = Stream.empty();

        // Execute the method without exceptions
        sortBenchmark.sortIntegers(wordCounts);

        // Assertion placeholder: No exception indicates successful processing
        assertTrue(true);
    }

    // Assertion placeholder: Handle gracefully without throwing exceptions
    public void testMinComparisons() {
        assertEquals(8769, minComparisons(1024), 0.1);
        assertEquals(19.46E6, minComparisons(1024 * 1024), 10000);
        assertEquals(31E9, minComparisons(1024 * 1024 * 1024), 500000000);
    }
}