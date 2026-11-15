package com.phasmidsoftware.dsaipg.util.config;

import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link Config_Benchmark#shouldRecurse}, the single place the cutoff comparison is
 * written. Added 2026-09-30, when an audit of both this project and HuskySort found that the
 * same question was being asked in four different spellings, two of them off by one and one of
 * them non-terminating at a cutoff of 1.
 */
public class ShouldRecurseTest {

    /**
     * The convention: a cutoff of k means ranges of up to k elements stop.
     */
    @Test
    public void aCutoffOfKStopsAtK() {
        for (int cutoff = 1; cutoff <= 20; cutoff++) {
            assertFalse("a range of exactly the cutoff must stop", shouldRecurse(cutoff, cutoff));
            assertTrue("one more than the cutoff must recurse", shouldRecurse(cutoff + 1, cutoff));
            assertFalse("and anything smaller must stop", shouldRecurse(cutoff - 1, cutoff));
        }
    }

    /**
     * A cutoff of 1 turns the mechanism off: only ranges of 0 or 1 stop, and those are sorted
     * already. This is the value config.ini tells you to use, and the value the tests that mean
     * to exercise the recursion should set.
     */
    @Test
    public void aCutoffOfOneTurnsTheMechanismOff() {
        assertFalse(shouldRecurse(0, 1));
        assertFalse(shouldRecurse(1, 1));
        assertTrue(shouldRecurse(2, 1));
        assertTrue(shouldRecurse(1000, 1));
    }

    /**
     * <b>The reason this is a method rather than an expression.</b> A cutoff of 0 or less would
     * let a range of one element recurse, and a one-element range splits into an empty half and
     * itself, so the recursion never ends. Measured before the floor existed: ParSort at a cutoff
     * of 0 or 1 ran forever, and HuskySort's MSDStringSort at 0 ended in StackOverflowError.
     */
    @Test
    public void noCutoffValueCanProduceAnInfiniteRecursion() {
        for (int cutoff : new int[]{0, -1, -100, Integer.MIN_VALUE}) {
            assertFalse("a single element must never recurse, whatever the cutoff", shouldRecurse(1, cutoff));
            assertFalse("nor an empty range", shouldRecurse(0, cutoff));
            assertTrue("but two elements still must", shouldRecurse(2, cutoff));
        }
    }

    /**
     * The Config overload reads {@code helper.cutoff}, with 0 or less meaning "unset" -- which is
     * how an empty entry in config.ini reads, so this is the ordinary path, not an edge case.
     */
    @Test
    public void theConfigOverloadResolvesAnUnsetCutoffToTheDefault() throws Exception {
        Config config = Config.load(ShouldRecurseTest.class);
        assertEquals(CUTOFF_DEFAULT, getCutoff(config.copy(HELPER, CUTOFF, "0")));
        assertEquals(CUTOFF_DEFAULT, getCutoff(config.copy(HELPER, CUTOFF, "")));
        assertEquals(7, getCutoff(config.copy(HELPER, CUTOFF, "7")));
        assertFalse(shouldRecurse(7, config.copy(HELPER, CUTOFF, "7")));
        assertTrue(shouldRecurse(8, config.copy(HELPER, CUTOFF, "7")));
        assertFalse("unset means the default, not 0",
                shouldRecurse(CUTOFF_DEFAULT, config.copy(HELPER, CUTOFF, "")));
        assertTrue(shouldRecurse(CUTOFF_DEFAULT + 1, config.copy(HELPER, CUTOFF, "")));
    }
}
