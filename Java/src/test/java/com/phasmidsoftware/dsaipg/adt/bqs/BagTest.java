/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class BagTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

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

    /**
     * clear() resets the count and nothing else, so contains used to scan past it
     * and still answer true for items the bag no longer held — while multiplicity
     * answered zero, because it happened to have an isEmpty() guard that contains
     * lacked. Two methods disagreeing about the same question.
     */
    @Test
    public void clearReallyForgetsTheItems() {
        Bag<String> bag = new Bag_Array<>();
        bag.add("x");
        bag.add("y");
        bag.clear();
        assertTrue(bag.isEmpty());
        assertEquals(0, bag.size());
        assertFalse("contains must not see past the count", bag.contains("x"));
        assertEquals(0, bag.multiplicity("x"));
    }

    /**
     * The staleness was position-dependent, which would have made a bug report
     * baffling: adding one item overwrote slot 0, so the old first element stopped
     * being found while the second was still there.
     */
    @Test
    public void noStaleEntryIsVisibleAfterReuse() {
        Bag<String> bag = new Bag_Array<>();
        bag.add("x");
        bag.add("y");
        bag.clear();
        bag.add("z");
        assertTrue(bag.contains("z"));
        assertFalse(bag.contains("x"));
        assertFalse(bag.contains("y"));
    }

    /**
     * Growth DOUBLES the capacity, which is what makes the amortized cost of an
     * addition O(1) rather than O(n).
     * <p>
     * NOTE what this does and does not guard. The factor is chosen by
     * {@code add}, which calls {@code grow(items, 2 * capacity())}, and add is not
     * part of the exercise — so a student cannot get the doubling wrong. What it
     * does catch is growFrom returning an array of the wrong length, and a later
     * change to add's growth policy. Growing by a constant would pass every other
     * test in this class, so the policy is worth pinning down somewhere.
     * <p>
     * It is why {@link Bag_Array#capacity} is package-private.
     */
    @Test
    public void growthDoublesTheCapacity() {
        Bag_Array<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 32; i++) bag.add(i);
        assertEquals("no growth until it is actually full", 32, bag.capacity());
        bag.add(32);
        assertEquals(64, bag.capacity());
    }

    /**
     * And it keeps doubling: the third growth takes it to 256, not to 32 + 3k for
     * some k.
     */
    @Test
    public void growthKeepsDoubling() {
        Bag_Array<Integer> bag = new Bag_Array<>();
        for (int i = 0; i <= 128; i++) bag.add(i);
        assertEquals(256, bag.capacity());
    }

    /**
     * of allocates directly, so its capacity is decided at construction and no
     * growth has happened.
     */
    @Test
    public void ofAllocatesRoomWithoutGrowing() {
        assertEquals(32, Bag_Array.of(1, 2, 3).capacity());
        Integer[] items = new Integer[64];
        for (int i = 0; i < items.length; i++) items[i] = i;
        assertEquals(128, Bag_Array.of(items).capacity());
    }

    /**
     * A direct test of the exercise's contract rather than of its use, and it
     * earns its place: {@code add} only ever calls
     * {@code grow(items, 2 * capacity())}, where the source is the whole backing
     * array, so an implementation returning an array of {@code from.length * 2}
     * is accidentally right at every call site and passes every other test in
     * this class — while ignoring the size it was given. Only calling it directly
     * can tell the difference.
     */
    @Test
    public void growFromHonoursTheRequestedSize() {
        assertArrayEquals(new Object[]{1, 2, null, null, null},
                Bag_Array.growFrom(new Object[]{1, 2}, 5));
        assertArrayEquals(new Object[]{null, null, null},
                Bag_Array.growFrom(new Object[]{}, 3));
        assertArrayEquals(new Object[]{1, 2, 3},
                Bag_Array.growFrom(new Object[]{1, 2, 3}, 3));
    }

    @Test
    public void growthPreservesTheItemsAndTheirOrder() {
        Bag_Array<Integer> bag = new Bag_Array<>();
        Integer[] expected = new Integer[33];
        for (int i = 0; i < 33; i++) {
            bag.add(i);
            expected[i] = i;
        }
        assertEquals(33, bag.size());
        assertArrayEquals(expected, bag.asArray());
    }

    /**
     * Several doublings in a row, not just the first.
     */
    @Test
    public void repeatedGrowth() {
        Bag_Array<Integer> bag = new Bag_Array<>();
        int n = 32 * 8 + 1;
        for (int i = 0; i < n; i++) bag.add(i);
        assertEquals(n, bag.size());
        Object[] actual = bag.asArray();
        for (int i = 0; i < n; i++) assertEquals(i, actual[i]);
    }

    /**
     * The unused tail of the grown array must be null rather than stale entries.
     * multiplicity scans the whole backing store rather than stopping at count, so
     * a growth which duplicated anything into the tail shows up as a count above
     * one.
     */
    @Test
    public void growthLeavesNoStaleEntries() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 33; i++) bag.add(i);
        for (int i = 0; i < 33; i++)
            assertEquals("item " + i + " should appear exactly once", 1, bag.multiplicity(i));
    }

    @Test
    public void growthAfterClear() {
        Bag<Integer> bag = new Bag_Array<>();
        for (int i = 0; i < 33; i++) bag.add(i);
        bag.clear();
        assertTrue(bag.isEmpty());
        bag.add(1);
        assertArrayEquals(new Integer[]{1}, bag.asArray());
    }

    /**
     * The varargs constructor, which allocates directly and so never needs
     * growFrom.
     */
    @Test
    public void of() {
        Bag_Array<Integer> bag = Bag_Array.of(1, 2, 3);
        assertEquals(3, bag.size());
        assertArrayEquals(new Integer[]{1, 2, 3}, bag.asArray());
    }

    @Test
    public void ofNothing() {
        Bag_Array<Integer> bag = Bag_Array.of();
        assertTrue(bag.isEmpty());
        // NOTE Object[], not Integer[], for the reason given in testAsArray: the
        // array really is an Object[] at runtime, so using the result at its
        // declared type is a ClassCastException waiting to happen.
        Object[] items = bag.asArray();
        assertEquals(0, items.length);
    }

    /**
     * A bag from {@code of} has room to spare, so the next add does not
     * immediately need growFrom. Sizing exactly would put the dependency back.
     */
    @Test
    public void ofLeavesRoomToGrow() {
        Bag_Array<Integer> bag = Bag_Array.of(1, 2, 3);
        bag.add(4);
        assertArrayEquals(new Integer[]{1, 2, 3, 4}, bag.asArray());
    }

    @Test
    public void ofMoreThanTheInitialCapacity() {
        Integer[] items = new Integer[64];
        for (int i = 0; i < items.length; i++) items[i] = i;
        Bag_Array<Integer> bag = Bag_Array.of(items);
        assertEquals(items.length, bag.size());
        assertArrayEquals(items, bag.asArray());
    }

    @Test
    public void ofKeepsDuplicates() {
        Bag_Array<Integer> bag = Bag_Array.of(1, 1, 2);
        assertEquals(3, bag.size());
        assertEquals(2, bag.multiplicity(1));
    }
}
