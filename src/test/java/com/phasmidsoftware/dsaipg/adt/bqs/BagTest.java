/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

import static org.junit.Assert.*;

public class BagTest {

    /**
     * Test method for Bag
     */
    @Test
    public void testBagAdd1() {
        Bag<Integer> bag = new Bag_Array<>();
        assertEquals(0, bag.size());
        assertTrue(bag.isEmpty());
        assertFalse((bag.iterator()).hasNext());
        bag.add(1);
        assertEquals(1, bag.size());
        assertFalse(bag.isEmpty());
        assertTrue((bag.iterator()).hasNext());
        assertEquals(Integer.valueOf(1), bag.iterator().next());
    }

    /**
     * Test method for Bag
     */
    @Test
    public void testBagAdd2() {
        Bag<Integer> bag = new Bag_Array<>(new Random(1L));
        assertEquals(0, bag.size());
        assertTrue(bag.isEmpty());
        Iterator<Integer> iterator1 = bag.iterator();
        assertFalse(iterator1.hasNext());
        for (int i = 0; i < 32; i++)
            bag.add(i);
        bag.add(32);
        assertEquals(33, bag.size());
        assertFalse(bag.isEmpty());
        Iterator<Integer> iterator2 = bag.iterator();
        assertTrue(iterator2.hasNext());
        assertEquals(Integer.valueOf(15), iterator2.next());
    }

    /**
     * Test method for Bag
     */
    @Test
    public void testBagIterator() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 1; i <= 4; i++)
            bag.add(i);
        assertEquals(4, bag.size());
        int sum = 0;
        for (Integer x : bag) sum += x;
        assertEquals(10, sum);
    }

    /**
     * Test method for asArray
     */
    @Test
    public void testAsArray() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 1; i <= 4; i++)
            bag.add(i);
        assertEquals(4, bag.size());
        // NOTE we can (successfully) cast an individual object but not an array.
        Object[] integers = bag.asArray();
        int sum = 0;
        for (Object x : integers) sum += (Integer) x;
        assertEquals(10, sum);
    }

    @Test
    public void clear() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 10; i++)
            bag.add(i);
        assertEquals(10, bag.size());
        bag.clear();
        assertTrue(bag.isEmpty());
    }

    @Test
    public void contains() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 10; i++)
            bag.add(i);
        assertTrue(bag.contains(0));
        assertTrue(bag.contains(9));
        assertFalse(bag.contains(10));
    }

    @Test
    public void multiplicity() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 10; i++)
            bag.add(i);
        for (int i = 0; i < 10; i += 2)
            bag.add(i);
        assertEquals(2, bag.multiplicity(0));
        assertEquals(1, bag.multiplicity(9));
        assertEquals(0, bag.multiplicity(10));
    }

    @Test
    public void testToString() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 10; i++)
            bag.add(i);
        assertEquals("Bag_Array{items=[0, 1, 2, 3, 4, 5, 6, 7, 8, 9], count=10}", bag.toString());
    }
}