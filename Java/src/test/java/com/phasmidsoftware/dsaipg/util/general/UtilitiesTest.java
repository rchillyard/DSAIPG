package com.phasmidsoftware.dsaipg.util.general;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class UtilitiesTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void fillRandomArray1() {
        int n = 100;
        int max = 10;
        Integer[] integers = Utilities.fillRandomArray(Integer.class, new Random(0), n, r -> r.nextInt(max));
        int[] count = new int[max];
        for (Integer x : integers) count[x]++;
        int sum = 0;
        for (int x : count) sum += x;
        assertEquals(8, count[0]);
        assertEquals(12, count[2]);
        assertEquals(17, count[7]);
        assertEquals(5, count[9]);
        assertEquals(n, sum);
    }

    @Test
    public void fillRandomArray2() {
        int n = 1000;
        int max = 100;
        int seed = 1;
        Integer[] integers = Utilities.fillRandomArray(Integer.class, new Random(seed), n, r -> r.nextInt(max));
        int[] count = new int[max];
        for (Integer x : integers) count[x]++;
        int sum = 0;
        for (int x : count) sum += x;
        assertEquals(n, sum);
    }

    @Test
    public void testAsArrayValidCollection() {
        Collection<String> collection = List.of("A", "B", "C");
        String[] array = Utilities.asArray(collection, String.class);
        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("B", array[1]);
        assertEquals("C", array[2]);
    }

    /**
     * An empty collection is fine now. The old version rejected it, because it
     * derived the component type from the first element and an empty collection
     * has none.
     */
    @Test
    public void testAsArrayEmptyCollection() {
        Collection<String> collection = List.of();
        String[] array = Utilities.asArray(collection, String.class);
        assertEquals(0, array.length);
    }

    /**
     * The reason for passing the class. Deriving the component type from the first
     * element gave an Integer[] here and then threw ArrayStoreException on the
     * Double — and the other way round if the Double came first, so neither
     * ordering was safe. Any collection whose static type is a supertype of its
     * contents was at risk, which is exactly what Sort.sort deals in.
     */
    @Test
    public void testAsArrayHeterogeneousCollection() {
        Collection<Number> collection = List.of(1, 2.5, 3L);
        Number[] array = Utilities.asArray(collection, Number.class);
        assertEquals(3, array.length);
        assertEquals(Number[].class, array.getClass());
        assertEquals(1, array[0]);
        assertEquals(2.5, array[1]);
        assertEquals(3L, array[2]);
    }

    @Test
    public void testAsArrayComponentTypeIsTheOneAskedFor() {
        // Not the runtime class of the elements: a Number[] holding Integers.
        Number[] array = Utilities.asArray(List.of(1, 2), Number.class);
        assertEquals(Number[].class, array.getClass());
    }
}