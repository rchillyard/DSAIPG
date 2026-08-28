package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.math.BigInteger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class LucasTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testGet1() {
        Lucas lucas = new Lucas();
        assertEquals(BigInteger.valueOf(2L), lucas.get(0));
        assertEquals(BigInteger.valueOf(1L), lucas.get(1));
        assertEquals(BigInteger.valueOf(3L), lucas.get(2));
        assertEquals(BigInteger.valueOf(4L), lucas.get(3));
        assertEquals(BigInteger.valueOf(7L), lucas.get(4));
        assertEquals(BigInteger.valueOf(11L), lucas.get(5));
        assertEquals(BigInteger.valueOf(18L), lucas.get(6));
        assertEquals(BigInteger.valueOf(29L), lucas.get(7));
        assertEquals(BigInteger.valueOf(47L), lucas.get(8));
        assertEquals(BigInteger.valueOf(76L), lucas.get(9));
        assertEquals(BigInteger.valueOf(123L), lucas.get(10));
        assertEquals(BigInteger.valueOf(199L), lucas.get(11));
    }

    @Test
    public void testGet2() {
        assertEquals(BigInteger.valueOf(2L), new Lucas().get(0));
        assertEquals(BigInteger.valueOf(1L), new Lucas().get(1));
        assertEquals(BigInteger.valueOf(3L), new Lucas().get(2));
        assertEquals(BigInteger.valueOf(4L), new Lucas().get(3));
        assertEquals(BigInteger.valueOf(87403803L), new Lucas().get(38));
    }

    @Test
    public void testGet3() {
        assertEquals(BigInteger.valueOf(28143753123L), new Lucas().get(50));
    }

    @Test
    public void testGet4() {
        assertEquals(BigInteger.valueOf(6440026026380244498L), new Lucas().get(90));
    }
}