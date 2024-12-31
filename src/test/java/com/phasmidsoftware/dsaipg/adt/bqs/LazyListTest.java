package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LazyListTest {

    @Test
    public void testIterate() {
        LazyList<Integer> lazyList = LazyList.iterate(1, x -> x + 1);
        Integer one = lazyList.head;
        assertEquals(Integer.valueOf(1), one);
        Integer two = lazyList.tailFunction.get().head;
        assertEquals(Integer.valueOf(2), two);
    }

    @Test
    public void testPrepend() {
        LazyList<Integer> lazyList = LazyList.iterate(1, x -> x + 1);
        LazyList<Integer> target = lazyList.prepend(0);
        Integer zero = target.head;
        assertEquals(Integer.valueOf(0), zero);
        Integer one = target.tailFunction.get().head;
        assertEquals(Integer.valueOf(1), one);
    }

    @Test
    public void testFrom() {
        LazyList<Integer> lazyList = LazyList.from(1, 1);
        Integer one = lazyList.head;
        assertEquals(Integer.valueOf(1), one);
        Integer two = lazyList.tailFunction.get().head;
        assertEquals(Integer.valueOf(2), two);
    }

    @Test
    public void testMap() {
        LazyList<Integer> from1 = LazyList.from(1);
        LazyList<Integer> even = LazyList.map(from1, (Integer x) -> x * 2);
        Integer two = even.head;
        assertEquals(Integer.valueOf(2), two);
        Integer four = even.tailFunction.get().head;
        assertEquals(Integer.valueOf(4), four);
    }

    @Test
    public void testTake() {
        LazyList<Integer> lazyList = LazyList.from(1);
        List<Integer> list = lazyList.take(3);
        assertEquals(3, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
    }

    @Test
    public void testTakeWhile() {
        LazyList<Integer> lazyList = LazyList.from(1);
        List<Integer> list = lazyList.takeWhile(t -> t <= 3);
        assertEquals(3, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
    }
}