package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.coins;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class CoinChangerTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testMinimumCoins0() {
        CoinChanger cc = new CoinChanger(new int[]{1, 2, 5, 7, 9});
        assertEquals(12, cc.minimumCoins(100));
    }

    @Test
    public void testMinimumCoins1() {
        CoinChanger cc = new CoinChanger(new int[]{1, 11, 13, 15});
        assertEquals(4, cc.minimumCoins(40));
    }

    @Test
    public void testMinimumCoins2() {
        CoinChanger cc = new CoinChanger(new int[]{3, 6, 9, 2, 11});
        assertEquals(8, cc.minimumCoins(82));
    }
}