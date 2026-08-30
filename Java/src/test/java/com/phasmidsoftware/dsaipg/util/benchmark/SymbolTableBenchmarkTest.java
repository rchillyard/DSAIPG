package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class SymbolTableBenchmarkTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testBenchmarkStringSortersWithValidInput() throws IOException {
        // Arrange
        String[] words = {"alpha", "beta", "gamma", "delta"};
        int nWords = 4;
        int nRuns = 5;

        SymbolTableBenchmark benchmark = new SymbolTableBenchmark(Config.load());

        // Act & Assert
        benchmark.benchmarkStringSorters(words, nWords, nRuns);
    }

    @Test
    public void testBenchmarkStringSortersWithEmptyWordsArray() throws IOException {
        // Arrange
        String[] words = {};
        int nWords = 0;
        int nRuns = 5;

        SymbolTableBenchmark benchmark = new SymbolTableBenchmark(Config.load());

        // Act & Assert
        benchmark.benchmarkStringSorters(words, nWords, nRuns);
    }

    @Test
    public void testBenchmarkStringSortersWithNullWordsArray() throws IOException {
        // Arrange
        String[] words = null;
        int nWords = 0;
        int nRuns = 5;

        SymbolTableBenchmark benchmark = new SymbolTableBenchmark(Config.load());

        // Act & Assert
        benchmark.benchmarkStringSorters(words, nWords, nRuns);
    }

    @Test
    public void testBenchmarkStringSortersWithZeroRuns() throws IOException {
        // Arrange
        String[] words = {"one", "two", "three"};
        int nWords = 3;
        int nRuns = 1;

        SymbolTableBenchmark benchmark = new SymbolTableBenchmark(Config.load());

        // Act & Assert
        benchmark.benchmarkStringSorters(words, nWords, nRuns);
    }

    @Test
    public void testBenchmarkStringSortersWithLargeInputs() throws IOException {
        // Arrange
        int nWords = 100000;
        int nRuns = 10;
        Random random = new Random();
        String[] words = new String[nWords];
        for (int i = 0; i < nWords; i++) {
            words[i] = "word_" + random.nextInt(1000000);
        }

        SymbolTableBenchmark benchmark = new SymbolTableBenchmark(Config.load());

        // Act & Assert
        benchmark.benchmarkStringSorters(words, nWords, nRuns);
    }
}