package com.phasmidsoftware.dsaipg.adt.threesum;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class SourceTest {

    @Test
    public void intsSupplier() {
        int n = 20;
        Source source = new Source(n, 10, 0L);
        Supplier<int[]> supplier = source.intsSupplier(10);
        int[] ints = supplier.get();
        assertEquals(n, ints.length);
    }

    @Test
    public void intsSupplierDistinctElements() {
        int n = 15;
        Source source = new Source(n, 10, 0L);
        Supplier<int[]> supplier = source.intsSupplier(5);
        int[] ints = supplier.get();
        long uniqueCount = Arrays.stream(ints).distinct().count();
        assertEquals(n, uniqueCount);
    }

    @Test
    public void intsSupplierSortedArray() {
        int n = 25;
        Source source = new Source(n, 20, 12345L);
        Supplier<int[]> supplier = source.intsSupplier(8);
        int[] ints = supplier.get();
        for (int i = 1; i < ints.length; i++) {
            assertTrue(ints[i - 1] <= ints[i]);
        }
    }

    @Test
    public void intsSupplierConsistentOutputWithSeed() {
        int n = 10;
        long seed = 98765L;
        Source source1 = new Source(n, 5, seed);
        Source source2 = new Source(n, 5, seed);
        Supplier<int[]> supplier1 = source1.intsSupplier(3);
        Supplier<int[]> supplier2 = source2.intsSupplier(3);
        assertArrayEquals(supplier1.get(), supplier2.get());
    }
}