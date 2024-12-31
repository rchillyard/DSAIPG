package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.houseRobber;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HouseRobberTest {
    @Test
    public void test0() {
        double cost = houseRobber.solveHouseRobber(new double[]{});
        assertEquals(0.0, cost, 0);
    }

    @Test
    public void test1() {
        double cost = houseRobber.solveHouseRobber(new double[]{1, 2, 3, 1});
        assertEquals(4.0, cost, 0);
    }

    @Test
    public void test2() {
        double cost = houseRobber.solveHouseRobber(new double[]{2, 7, 9, 3, 1});
        assertEquals(12.0, cost, 0);
    }

    @Test
    public void test3() {
        double cost = houseRobber.solveHouseRobber(new double[]{5, 3, 4, 11, 2});
        assertEquals(16.0, cost, 0);
    }

    @Test
    public void test4() {
        double cost = houseRobber.solveHouseRobber(new double[]{5, 1, 1, 1, 1, 11, 2});
        assertEquals(17.0, cost, 0);
    }

    @Test
    public void test5() {
        double cost = houseRobber.solveHouseRobber(new double[]{2, 12, 9, 3, 4});
        assertEquals(16.0, cost, 0);
    }
}