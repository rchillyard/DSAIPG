package com.phasmidsoftware.dsaipg.adt.bqs;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class UnorderedIteratorTest {

    @Test
    public void testEmptyList() {
        Iterator<Integer> target = UnorderedIterator.createDeterministic(new ArrayList<>(), 0L);
        assertFalse(target.hasNext());
    }

    @Test
    public void testList1() {
        ArrayList<Integer> list = new ArrayList<>(ImmutableList.of(1, 2, 3));
        Iterator<Integer> target = UnorderedIterator.createDeterministic(list, 0L);
        assertTrue(target.hasNext());
        assertEquals(Integer.valueOf(1), target.next());
        assertTrue(target.hasNext());
        assertEquals(Integer.valueOf(3), target.next());
        assertTrue(target.hasNext());
        assertEquals(Integer.valueOf(2), target.next());
        assertFalse(target.hasNext());
    }

    @Test
    public void testList2() {
        ArrayList<Integer> list = new ArrayList<>(ImmutableList.of(1, 2, 3));
        Iterator<Integer> target = UnorderedIterator.createDeterministic(list, 2L);
        assertTrue(target.hasNext());
        assertEquals(Integer.valueOf(2), target.next());
        assertTrue(target.hasNext());
        assertEquals(Integer.valueOf(1), target.next());
        assertTrue(target.hasNext());
        assertEquals(Integer.valueOf(3), target.next());
        assertFalse(target.hasNext());
    }

    @Test
    public void testArray() {
        Integer[] array = new Integer[]{1, 2, 3};
        Iterator<Integer> target = new UnorderedIterator<>(array);
        target.next();
        target.next();
        target.next();
        assertFalse(target.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextOnEmptyList() {
        Iterator<Integer> target = UnorderedIterator.createDeterministic(new ArrayList<>(), 0L);
        target.next(); // Should throw NoSuchElementException
    }

    @Test
    public void testNextMultipleCalls() {
        ArrayList<Integer> list = new ArrayList<>(ImmutableList.of(10, 20, 30));
        Iterator<Integer> target = UnorderedIterator.createDeterministic(list, 123L); // Fixed seed
        assertEquals(Integer.valueOf(30), target.next()); // First random element
        assertEquals(Integer.valueOf(10), target.next()); // Second random element
        assertEquals(Integer.valueOf(20), target.next()); // Third random element
        assertFalse(target.hasNext()); // No more elements
    }

    @Test
    public void testNextWithRandomness() {
        ArrayList<Integer> list = new ArrayList<>(ImmutableList.of(5, 15, 25, 35, 45));
        Iterator<Integer> target = new UnorderedIterator<>(list);
        HashSet<Integer> set = new HashSet<>();
        while (target.hasNext()) {
            set.add(target.next());
        }
        assertEquals(5, set.size()); // Confirm all distinct elements are retrieved
        assertTrue(set.contains(5));
        assertTrue(set.contains(15));
        assertTrue(set.contains(25));
        assertTrue(set.contains(35));
        assertTrue(set.contains(45));
    }
}