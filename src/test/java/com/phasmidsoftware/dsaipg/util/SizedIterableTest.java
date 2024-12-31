package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SizedIterableTest {

    /**
     * Description of the class and method being tested:
     * <p>
     * The `SizedIterable` interface extends the `Iterable` interface, requiring implementing classes
     * to provide a `size()` method that returns the number of elements in the iterable collection.
     * Additionally, a default method `toList()` is defined, which converts the iterable into a `List`.
     * <p>
     * These tests focus on validating the functionality of the `size()` method.
     */

    @Test
    public void testSizeWithEmptyIterable() {
        SizedIterable<Integer> emptyIterable = new SizedIterable<>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public Iterator<Integer> iterator() {
                return Collections.emptyIterator();
            }
        };

        assertEquals(0, emptyIterable.size());
    }

    @Test
    public void testSizeWithNonEmptyIterable() {
        SizedIterable<Integer> nonEmptyIterable = new SizedIterable<>() {
            @Override
            public int size() {
                return 3;
            }

            @Override
            public Iterator<Integer> iterator() {
                return Arrays.asList(1, 2, 3).iterator();
            }
        };

        assertEquals(3, nonEmptyIterable.size());
    }

    @Test
    public void testSizeWithSingleElementIterable() {
        SizedIterable<String> singleElementIterable = new SizedIterable<String>() {
            @Override
            public int size() {
                return 1;
            }

            @Override
            public Iterator<String> iterator() {
                return List.of("element").iterator();
            }
        };

        assertEquals(1, singleElementIterable.size());
    }
}