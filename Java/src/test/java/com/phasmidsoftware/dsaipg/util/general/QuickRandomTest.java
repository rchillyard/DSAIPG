package com.phasmidsoftware.dsaipg.util.general;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class QuickRandomTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test(expected = IllegalArgumentException.class)
    public void testBadN() {
        new QuickRandom(0);
    }

    @Test
    public void testGetAllValuesInSmallRange() {
        QuickRandom random = new QuickRandom(5, 12345L);
        boolean[] found = new boolean[5];
        for (int i = 0; i < 100; i++) {
            int value = random.get();
            assertTrue(value >= 0 && value < 5);
            found[value] = true;
        }
        for (boolean b : found) {
            assertTrue("All values in range 0-4 should be generated", b);
        }
    }

    @Test
    public void testGetUniqueValuesInLargeRange() {
        QuickRandom random = new QuickRandom(1000000, 67890L);
        Set<Integer> uniqueValues = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            int value = random.get();
            assertTrue(value >= 0 && value < 1000000);
            uniqueValues.add(value);
        }
        assertTrue("Generated values should be mostly unique", uniqueValues.size() > 900);
    }

    @Test
    public void testGetWithNegativeM() {
        QuickRandom random = new QuickRandom(100, 54321L);
        try {
            random.get(-10);
            fail("Should throw an IllegalArgumentException for negative m");
        } catch (IllegalArgumentException e) {
            // Expected exception for negative m
        }
    }

    @Test
    public void testGet0() {
        QuickRandom random = new QuickRandom(0L);
        assertEquals(0x2BFF5, random.get());
        assertEquals(0x287AB56B, random.get());
        assertEquals(0x242AAA00, random.get());
        assertEquals(0x5C38C415, random.get());
    }

    @Test
    public void testSmallRangeGet() {
        QuickRandom random = new QuickRandom(10, 12345L);
        assertTrue(random.get() < 10);
        assertTrue(random.get() >= 0);
        assertTrue(random.get() < 10);
        assertTrue(random.get() >= 0);
    }

    @Test
    public void testGetCloseToN() {
        QuickRandom random = new QuickRandom(100, 54321L);
        assertTrue(random.get(95) >= 95);
        assertTrue(random.get(95) < 100);
        assertTrue(random.get(99) >= 99);
        assertTrue(random.get(99) < 100);
    }

    @Test
    public void testNoNegativeValues() {
        QuickRandom random = new QuickRandom(Integer.MAX_VALUE, 12345L);
        for (int i = 0; i < 1000; i++) {
            assertTrue(random.get() >= 0);
        }
    }

    @Test
    public void testEdgeCasesForGetWithM() {
        QuickRandom random = new QuickRandom(100, 67890L);
        assertTrue(random.get(0) >= 0);
        assertTrue(random.get(0) < 100);
        assertEquals(99, random.get(99));
    }

    @Test
    public void testGet1() {
        QuickRandom random = new QuickRandom(1000, 0);
        assertEquals(213, random.get());
        assertEquals(475, random.get());
        assertEquals(808, random.get());
        assertEquals(85, random.get());
        assertEquals(320, random.get());
    }

    @Test
    public void testGet2() {
        QuickRandom random = new QuickRandom(1000, 0);
        assertEquals(713, random.get(500));
        assertEquals(975, random.get(500));
        assertEquals(808, random.get(500));
        assertEquals(585, random.get(500));
        assertEquals(820, random.get(500));
    }


    @Test
    public void testGet3() {
        int n = 1000;
        QuickRandom random = new QuickRandom(n);
        Map<Integer, Integer> freq = new HashMap<>();
        for (int i = 0; i < 100 * n; i++) {
            int x = random.get();
            Integer y = freq.getOrDefault(x, 0);
            freq.put(x, y + 1);
        }
        for (int i = 0; i < n; i++) {
            assertTrue(freq.getOrDefault(i, 0) > 0);
        }
    }
}