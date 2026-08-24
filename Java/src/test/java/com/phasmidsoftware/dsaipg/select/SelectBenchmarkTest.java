package com.phasmidsoftware.dsaipg.select;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class SelectBenchmarkTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Test to check the output of the runBenchmarks method for a small dataset size.
     * Verifies that the method executes without exceptions and returns a non-null result.
     */
    @Test
    public void testRunBenchmarks_SmallDataset() throws IOException {
        // Arrange
        SelectBenchmark selectBenchmark = new SelectBenchmark(10, 100);

        // Act
        String result = selectBenchmark.runBenchmarks();

        // Assert
        assertNotNull("Result should not be null for a small dataset", result);
    }

    /**
     * Test to verify the behavior of the runBenchmarks method with minimal runs and data size.
     * Ensures that the method executes correctly without errors.
     */
    @Test
    public void testRunBenchmarks_MinimalRuns() throws IOException {
        // Arrange
        SelectBenchmark selectBenchmark = new SelectBenchmark(1, 1);

        // Act
        String result = selectBenchmark.runBenchmarks();

        // Assert
        assertNotNull("Result should not be null for minimal runs and data size", result);
    }
}