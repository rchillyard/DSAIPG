/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShuntingYardTest {

    /**
     * Test method for Stack
     */
    @Test
    public void testTwoStack() throws BQSException {
        assertEquals(1, new ShuntingYard("1").evaluate());
        assertEquals(3, new ShuntingYard("1 + 2").evaluate());
        assertEquals(14, new ShuntingYard("2 * ( 4 + 3 )").evaluate());
        assertEquals(2, new ShuntingYard("2 * ( 4 - 3 )").evaluate());
        assertEquals(101, new ShuntingYard("1 + ( ( 2 + 3 ) * ( 4 * 5 ) )").evaluate());
    }

    /**
     * Test method for Stack
     */
    @Test(expected = BQSException.class)
    public void testTwoStackFail1() throws BQSException {
        new ShuntingYard("").evaluate();
    }


    /**
     * Test method for Stack
     */
    @Test(expected = BQSException.class)
    public void testTwoStackFail2() throws BQSException {
        new ShuntingYard("(").evaluate();
    }

    /**
     * Test method for Stack
     */
    @Test(expected = BQSException.class)
    public void testTwoStackFail3() throws BQSException {
        new ShuntingYard(")").evaluate();
    }

    /**
     * Test method for Stack
     */
    @Test(expected = ArithmeticException.class)
    public void testTwoStackFail4() throws BQSException {
        new ShuntingYard("1 / 0").evaluate();
    }

    /**
     * Test complex expression with parentheses
     */
    @Test
    public void testComplexExpression() throws BQSException {
        assertEquals(50, new ShuntingYard("( 2 + 3 ) * ( 4 + 6 )").evaluate());
    }

    /**
     * Test simple subtraction
     */
    @Test
    public void testSimpleSubtraction() throws BQSException {
        assertEquals(5, new ShuntingYard("10 - 5").evaluate());
    }

    /**
     * Test simple division
     */
    @Test
    public void testSimpleDivision() throws BQSException {
        assertEquals(4, new ShuntingYard("8 / 2").evaluate());
    }

    /**
     * Test mixed operators
     */
    @Test
    public void testMixedOperators() throws BQSException {
        assertEquals(7, new ShuntingYard("1 + 2 * 3").evaluate());
    }

    /**
     * Test invalid operator
     */
    @Test(expected = BQSException.class)
    public void testInvalidOperator() throws BQSException {
        new ShuntingYard("2 $ 2").evaluate();
    }

}