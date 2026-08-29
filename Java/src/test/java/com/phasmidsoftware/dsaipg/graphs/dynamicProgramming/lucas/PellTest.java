package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.math.BigInteger;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class PellTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testGet1() {
        Pell pell = new Pell();
        assertEquals(BigInteger.valueOf(0L), pell.get(0));
        assertEquals(BigInteger.valueOf(1L), pell.get(1));
        assertEquals(BigInteger.valueOf(2L), pell.get(2));
        assertEquals(BigInteger.valueOf(5L), pell.get(3));
        assertEquals(BigInteger.valueOf(12L), pell.get(4));
        assertEquals(BigInteger.valueOf(29L), pell.get(5));
        assertEquals(BigInteger.valueOf(70L), pell.get(6));
        assertEquals(BigInteger.valueOf(169L), pell.get(7));
        assertEquals(BigInteger.valueOf(408L), pell.get(8));
        assertEquals(BigInteger.valueOf(985L), pell.get(9));
        assertEquals(BigInteger.valueOf(2378L), pell.get(10));
        assertEquals(BigInteger.valueOf(5741L), pell.get(11));
    }

    @Test
    public void testGet2() {
        assertEquals(BigInteger.valueOf(0L), new Pell().get(0));
        assertEquals(BigInteger.valueOf(1L), new Pell().get(1));
        assertEquals(BigInteger.valueOf(2L), new Pell().get(2));
        assertEquals(BigInteger.valueOf(5L), new Pell().get(3));
        assertEquals(BigInteger.valueOf(124145519261542L), new Pell().get(38));
    }

    @Test
    public void testGet3() {
        assertEquals(BigInteger.valueOf(4866752642924153522L), new Pell().get(50));
    }

    /**
     * The 90th Pell number, which needs 113 bits.
     * <p>
     * This used to assert 7,052,354,271,195,710,746 — not the 90th Pell number,
     * but what a long held once that value had wrapped. Pell numbers grow by a
     * factor of about 2.414, so they pass a 64-bit long at around n = 62, and the
     * test had been pinning the overflow ever since.
     */
    @Test
    public void testGet4() {
        assertEquals(new BigInteger("9960168529794442859224531878561050"), new Pell().get(90));
    }

    /**
     * The 90th Pell number does not fit in a long. What a long holds instead is
     * 7052354271195710746 — nothing like the real value — so that is asserted as
     * the thing which must NOT come back.
     */
    @Test
    public void testGet4IsNoLongerTheWrappedValue() {
        assertNotEquals(BigInteger.valueOf(7052354271195710746L), new Pell().get(90));
        assertTrue("the 90th Pell number does not fit in a long",
                new Pell().get(90).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0);
    }
}