package com.phasmidsoftware.dsaipg.adt.array;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

import static org.junit.Assert.*;

public class OrderedArrayTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testConstructor0() {
        Integer[] integers = {3, 1, 4, 2, 0};
        OrderedArray<Integer> target = new OrderedArray<>(integers, false, Integer::compareTo);
        assertEquals(Integer.valueOf(0), integers[0]);
        assertEquals(Integer.valueOf(0), target.get(0));
    }

    @Test
    public void testConstructor1() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        OrderedArray<Integer> target = new OrderedArray<Integer>(list, (o1, o2) -> -o1.compareTo(o2));
        assertEquals(Integer.valueOf(1), target.get(0));
    }

    @Test
    public void testFrom0() {
        Integer[] integers = {3, 1, 4, 2, 0};
        OrderedArray<Integer> target = OrderedArray.from(integers, Integer::compareTo);
        assertEquals(Integer.valueOf(0), target.get(0));
    }

    @Test
    public void testFrom1() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        OrderedArray<Integer> target = OrderedArray.from(list, (o1, o2) -> -o1.compareTo(o2));
        assertEquals(Integer.valueOf(1), target.get(0));
    }

    @Test
    public void testFrom2() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        OrderedArray<Integer> target = OrderedArray.from(list);
        assertEquals(Integer.valueOf(0), target.get(0));
    }

    @Test
    public void testFrom3() {
        Integer[] integers = {3, 1, 4, 2, 0};
        OrderedArray<Integer> target = OrderedArray.from(integers);
        assertEquals(Integer.valueOf(0), target.get(0));
    }

    @Test
    public void testAddElements0() {
        OrderedArray<Integer> orderedArray = OrderedArray.from(3, 1, 4);
        orderedArray.addElements(new Integer[]{2, 0});
        Iterator<Integer> iterator = orderedArray.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next());
    }

    @Test
    public void testAddElements1() {
        OrderedArray<Integer> orderedArray = OrderedArray.from(3, 1, 4);
        orderedArray.addElements(ImmutableList.of(2, 0));
        Iterator<Integer> iterator = orderedArray.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next());
    }

    @Test
    public void testIterator() {
        OrderedArray<Integer> orderedArray = OrderedArray.from(3, 1, 4, 2, 0);
        Iterator<Integer> iterator = orderedArray.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next());
    }

    @Test
    public void testIndexOf() {
        OrderedArray<Integer> orderedArray = OrderedArray.from(3, 1, 4, 2, 0);
        assertEquals(4, orderedArray.indexOf(4));
        assertEquals(-1, orderedArray.indexOf(-2));
    }

    @Test
    public void testGetSize() {
        OrderedArray<Integer> orderedArray = OrderedArray.from(3, 1, 4, 2, 0);
        assertEquals(5, orderedArray.getSize());
    }

    @Test
    public void testGetArray() {
        OrderedArray<Integer> orderedArray = OrderedArray.from(3, 1, 4, 2, 0);
        assertArrayEquals(new Integer[]{0, 1, 2, 3, 4}, orderedArray.getArray());
    }
}