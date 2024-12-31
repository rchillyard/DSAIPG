package com.phasmidsoftware.dsaipg.sort.elementary;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class InsertionSortBasicTest {

    @Test
    public void testSortFull1() {
        String[] words = new String[]{"Dog", "Cat", "ferret", "Aardvark", "Fox", "Bat"};
        String[] expectedNormal = new String[]{"Aardvark", "Bat", "Cat", "Dog", "Fox", "ferret"};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.sort(words);
        assertArrayEquals(expectedNormal, words);
    }

    @Test
    public void testSortFull2() {
        String[] words = new String[]{"Dog", "Cat", "ferret", "Aardvark", "Fox", "Bat"};
        String[] expectedIgnoreCase = new String[]{"Aardvark", "Bat", "Cat", "Dog", "ferret", "Fox"};
        InsertionSortBasic<String> sorter = new InsertionSortBasic<>(String.CASE_INSENSITIVE_ORDER);
        sorter.sort(words);
        assertArrayEquals(expectedIgnoreCase, words);
    }

    @Test
    public void testSortFull3() {
        String[] words = new String[]{"Dog", "Cat", "ferret", "Aardvark", "Fox", "Bat"};
        String[] expectedIgnoreCase = new String[]{"Fox", "ferret", "Dog", "Cat", "Bat", "Aardvark"};
        InsertionSortBasic<String> sorter = new InsertionSortBasic<>(String.CASE_INSENSITIVE_ORDER.reversed());
        sorter.sort(words);
        assertArrayEquals(expectedIgnoreCase, words);
    }

    @Test
    public void testInsertionSortSingleton() {
        String[] words = new String[]{"Delta"};
        String[] expected = new String[]{"Delta"};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.sort(words);
        assertArrayEquals(expected, words);
    }

    @Test
    public void testInsertionSortEmpty() {
        String[] words = new String[]{};
        String[] expected = new String[]{};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.sort(words);
        assertArrayEquals(expected, words);
    }

    @Test
    public void testSortPartition() {
        String[] words = new String[]{"Dog", "Cat", "ferret", "Aardvark", "Fox", "Bat"};
        String[] expectedNormal = new String[]{"Dog", "Cat", "Aardvark", "ferret", "Fox", "Bat"};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.sort(words, 2, 4);
        assertArrayEquals(expectedNormal, words);
    }

    @Test
    public void testInsertMiddle() {
        String[] words = new String[]{"Cat", "Dog", "Fox", "Bat", "Aardvark"};
        String[] expected = new String[]{"Bat", "Cat", "Dog", "Fox", "Aardvark"};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.insert(words, 0, 3);
        assertArrayEquals(expected, words);
    }

    @Test
    public void testInsertEnd() {
        String[] words = new String[]{"Alpha", "Beta", "Delta", "Gamma", "Epsilon"};
        String[] expected = new String[]{"Alpha", "Beta", "Delta", "Epsilon", "Gamma"};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.insert(words, 0, 4);
        assertArrayEquals(expected, words);
    }

    @Test
    public void testInsertStart() {
        String[] words = new String[]{"Delta", "Alpha", "Beta", "Gamma", "Epsilon"};
        String[] expected = new String[]{"Alpha", "Delta", "Beta", "Gamma", "Epsilon"};
        InsertionSortBasic<String> sorter = InsertionSortBasic.create();
        sorter.insert(words, 0, 1);
        assertArrayEquals(expected, words);
    }
}