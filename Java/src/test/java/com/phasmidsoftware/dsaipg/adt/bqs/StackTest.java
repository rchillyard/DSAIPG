/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class StackTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Test method for Stack
     */
    @Test
    public void testStack() throws BQSException {
        Stack<Integer> stack = new Stack_LinkedList<>();
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
        Integer item = stack.pop();
        assertEquals(Integer.valueOf(1), item);
    }

}
