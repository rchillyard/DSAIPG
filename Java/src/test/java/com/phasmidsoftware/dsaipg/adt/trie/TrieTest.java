package com.phasmidsoftware.dsaipg.adt.trie;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class TrieTest {

    @Test
    public void testInsertAndSearch() {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");
        assertTrue(trie.search("apple"));
        assertTrue(trie.search("app"));
        assertFalse(trie.search("appl"));
    }

    @Test
    public void testAutocomplete() {
        Trie trie = new Trie();
        String[] words = {"cat", "car", "cart", "carbon", "dog"};
        for (String word : words) trie.insert(word);

        List<String> results = trie.autocomplete("car");
        List<String> expected = List.of("car", "cart", "carbon");

        Collections.sort(results);
        List<String> sortedExpected = new ArrayList<>(expected);
        Collections.sort(sortedExpected);

        assertEquals(sortedExpected, results);
    }


    @Test
    public void testAutocompleteNoMatch() {
        Trie trie = new Trie();
        trie.insert("hello");
        assertTrue(trie.autocomplete("xyz").isEmpty());
    }

    @Test
    public void testDeleteWord() {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");

        assertTrue(trie.search("apple"));
        trie.delete("apple");
        assertFalse(trie.search("apple"));
        assertTrue(trie.search("app"));
    }

}

