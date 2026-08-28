package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.math.BigInteger;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class FibonacciTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testGet1() {
        Fibonacci fibonacci = new Fibonacci();
        assertEquals(BigInteger.valueOf(1L), fibonacci.get(0));
        assertEquals(BigInteger.valueOf(1L), fibonacci.get(1));
        assertEquals(BigInteger.valueOf(2L), fibonacci.get(2));
        assertEquals(BigInteger.valueOf(3L), fibonacci.get(3));
        assertEquals(BigInteger.valueOf(5L), fibonacci.get(4));
        assertEquals(BigInteger.valueOf(8L), fibonacci.get(5));
        assertEquals(BigInteger.valueOf(13L), fibonacci.get(6));
        assertEquals(BigInteger.valueOf(21L), fibonacci.get(7));
    }

    @Test
    public void testGet2() {
        assertEquals(BigInteger.valueOf(1L), new Fibonacci().get(0));
        assertEquals(BigInteger.valueOf(1L), new Fibonacci().get(1));
        assertEquals(BigInteger.valueOf(2L), new Fibonacci().get(2));
        assertEquals(BigInteger.valueOf(3L), new Fibonacci().get(3));
        assertEquals(BigInteger.valueOf(5L), new Fibonacci().get(4));
        assertEquals(BigInteger.valueOf(8L), new Fibonacci().get(5));
        assertEquals(BigInteger.valueOf(13L), new Fibonacci().get(6));
        assertEquals(BigInteger.valueOf(21L), new Fibonacci().get(7));
    }

    /**
     * Beyond a 32-bit int, which is where this used to wrap silently: fib(47) is
     * 4,807,526,976 against an int maximum of 2,147,483,647. The test stopped at
     * get(7), so nothing ever showed it.
     */
    @Test
    public void getBeyondAnInt() {
        assertEquals(BigInteger.valueOf(4807526976L), new Fibonacci().get(47));
        assertTrue("fib(47) does not fit in an int",
                new Fibonacci().get(47).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0);
    }

    /**
     * Far beyond any primitive, to show there is no ceiling left.
     */
    @Test
    public void getVeryLarge() {
        assertEquals(new BigInteger("354224848179261915075"), new Fibonacci().get(99));
    }
}