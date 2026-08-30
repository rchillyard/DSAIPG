package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.assertEquals;

public class TimeLoggerTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * The pattern's integer part must end in "0", not "#", or a value below one
     * loses its leading zero and 0.5 renders as ".5000".
     */
    @Test
    public void testFormatTimeBelowOne() {
        assertEquals("0.5000", TimeLogger.formatTime(0.5));
        assertEquals("0.0000", TimeLogger.formatTime(0.0));
        assertEquals("-0.5000", TimeLogger.formatTime(-0.5));
    }

    @Test
    public void testFormatTimeRoundsToFourPlaces() {
        assertEquals("12.3457", TimeLogger.formatTime(12.3456789));
        assertEquals("0.0000", TimeLogger.formatTime(0.00004));
    }

    /**
     * The integer part is never truncated to the width of the pattern, and there
     * are no thousands separators.
     */
    @Test
    public void testFormatTimeLargeValues() {
        assertEquals("1234567.5000", TimeLogger.formatTime(1234567.5));
        assertEquals("1000000.0000", TimeLogger.formatTime(1e6));
    }

    @Test
    public void testLogRawTime() {
        // With no complexity function the time is logged as it stands.
        new TimeLogger("Raw time per run (mSec):", null).log("test", 0.5, 1000);
    }

    @Test
    public void testLogNormalizedTime() {
        new TimeLogger("Normalized time per run:", n -> n * Math.log(n)).log("test", 0.5, 1000);
    }
}
