package com.phasmidsoftware.dsaipg.sort.counting.LSDStringSortStepDefinition;


import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.counting.LSDStringSort;
import com.phasmidsoftware.dsaipg.util.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class LSDStringSortTest {

    // TODO add tests which test numbers of hits, lookups, etc.

    @Test
    public void testSortMixedCase() throws IOException {
        ImmutableList<String> list = ImmutableList.of("bravos", "Campion", "ABLEXX", "aardva", "beetle");
        try (SortWithHelper<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            String[] sorted = sorter.sort(xs);
            assertTrue(sorter.isSorted(sorted));
        }
    }

    @Test
    public void testSortWithEmptyStrings() throws IOException {
        ImmutableList<String> list = ImmutableList.of("", "bravos", "", "aardva", "beetle");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"", "", "aardva", "beetle", "bravos"}, xs);
        }
    }

    @Test
    public void testSortVaryingLengths() throws IOException {
        ImmutableList<String> list = ImmutableList.of("a", "abc", "ab", "abcd");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"a", "ab", "abc", "abcd"}, xs);
        }
    }

    @Test
    public void testSortNonASCIICharacters() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Ārdva", "Beetle", "Campion");
        try (SortWithHelper<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            // Note: LSD String Sort is designed for ASCII characters only, the sorting with non-ASCII might not yield predictable results.
//            assertArrayEquals(new String[]{"Beetle", "Bravos", "Campion", "Ārdva"}, xs);
            sorter.isSorted(xs);
        }
    }

    @Test
    public void testSortEmptyArray() throws IOException {
        ImmutableList<String> list = ImmutableList.of();
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{}, xs);
        }
    }

    @Test
    public void testSort0() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "Campion"}, xs);
        }
    }

    @Test
    public void testSort1() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "Campion"}, xs);
        }
    }

    @Test
    public void testSort2() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle", "C     ");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "C     ", "Campion"}, xs);
        }
    }

    //        @Test  // Need to do implement case-independent comparisons (as an option) in LSDStringSort,
    public void testSort4() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle", "c     ");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "c     ", "Campion"}, xs);
        }
    }
}