/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable.FrequencyCounter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FrequencyCounterFuncTest {
    @Test
    public void testIncrementVeryLargeCount() {
        FrequencyCounter<String> fc = new FrequencyCounter<>();
        String key = "LargeKey";
        for (int i = 0; i < Integer.MAX_VALUE / 2; i++) {
            fc.increment(key);
            if (i % 100000000 == 0) { // Periodic checkpoint to avoid exceeding memory limit
                assertEquals(i + 1, fc.get(key).intValue());
            }
        }
        assertEquals((long) Integer.MAX_VALUE / 2, fc.get(key).intValue());
    }
}