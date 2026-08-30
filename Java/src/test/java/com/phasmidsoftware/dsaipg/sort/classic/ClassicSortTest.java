package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class ClassicSortTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    static class MyClass implements Classify<MyClass> {
        public MyClass(int value) {
            this.value = value;
        }

        public int classify() {
            return value / 1000;
        }

        private final int value;

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @Test
    public void mutatingSort() throws IOException {
        ClassicSort<MyClass> sorter = new ClassicSort<>();
        Helper<MyClass> helper = sorter.getHelper();
        int n = 100;
        helper.init(n);
        MyClass[] xs = helper.random(MyClass.class, (random -> new MyClass(random.nextInt(100000))));
        sorter.mutatingSort(xs);
        // Check sorted
        for (int i = 1; i < n; i++) assertTrue(xs[i - 1].classify() <= xs[i].classify());
    }

    /**
     * A value classified by the int it is given. classify() may return any int, so
     * sparse and negative values are both legal.
     */
    record Item(int cls) implements Classify<Item> {
        public int classify() {
            return cls;
        }

        public String toString() {
            return String.valueOf(cls);
        }
    }

    /**
     * Classes are visited in ascending order, whatever their values.
     * <p>
     * The bags are collected in a HashMap, whose keySet comes out in bucket order.
     * That is ascending only while every class is smaller than the table, which is
     * why mutatingSort above cannot catch this: it uses classes 0..99 in a table
     * grown to 256. Here 100 and 20 collide in bucket 4, so the keySet offers
     * [100, 20, 5] and the sort has to put them in order itself.
     */
    @Test
    public void testSortSparseClasses() throws IOException {
        Item[] xs = {new Item(100), new Item(5), new Item(20)};
        try (ClassicSort<Item> sorter = new ClassicSort<>()) {
            sorter.getHelper().init(xs.length);
            sorter.mutatingSort(xs);
        }
        assertArrayEquals(new Item[]{new Item(5), new Item(20), new Item(100)}, xs);
    }

    /**
     * Negative classes are legal, and are the case a hash table is least likely to
     * order by accident.
     */
    @Test
    public void testSortNegativeClasses() throws IOException {
        Item[] xs = {new Item(7), new Item(-40), new Item(-3), new Item(0)};
        try (ClassicSort<Item> sorter = new ClassicSort<>()) {
            sorter.getHelper().init(xs.length);
            sorter.mutatingSort(xs);
        }
        assertArrayEquals(new Item[]{new Item(-40), new Item(-3), new Item(0), new Item(7)}, xs);
    }

    /**
     * With several elements per class, only the class order can be asserted: a Bag
     * iterates in a deliberately random order, so ClassicSort groups by class but
     * does not order within one. Putting the elements of a class in order is the
     * following pass's job -- which is what BucketSort does, running an insertion
     * sort over the whole array once the buckets have been unloaded.
     */
    @Test
    public void testSortRepeatedClasses() throws IOException {
        Item[] xs = {new Item(60), new Item(3), new Item(60), new Item(3), new Item(17)};
        try (ClassicSort<Item> sorter = new ClassicSort<>()) {
            sorter.getHelper().init(xs.length);
            sorter.mutatingSort(xs);
        }
        for (int i = 1; i < xs.length; i++)
            assertTrue("classes out of order at " + i + ": " + Arrays.toString(xs),
                    xs[i - 1].classify() <= xs[i].classify());
    }
}