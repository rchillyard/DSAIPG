package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CoinsTest {


    @Test
    public void value0() {
        Coins coins = new Coins();
        assertEquals(coins.zero, coins.number(0));
        assertEquals(0, coins.subProblems());
    }

    @Test
    public void value1() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0), coins.number(1));
        assertEquals(1, coins.subProblems());
    }

    @Test
    public void value2() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0).increment(1), coins.number(6));
        assertEquals(6, coins.subProblems());
    }

    @Test
    public void value87() {
        Coins coins = new Coins();
        int amount = 87;
        int[] counts = {2, 0, 1, 3}; // two pennies, one dime, three quarters.
        Coins.Solution expected = coins.new Solution(6, counts);
        assertEquals(expected, coins.number(amount));
        assertEquals(amount, coins.subProblems());
    }

    @Test
    public void mu0() {
        Coins coins = new Coins();
        assertEquals(coins.zero, coins.mu(0));
    }

    @Test
    public void mu1() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0), coins.mu(1));
    }

    @Test
    public void mu2() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0).increment(1), coins.mu(6));
    }
}