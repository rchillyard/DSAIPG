package com.phasmidsoftware.dsaipg.adt.trie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The TrieBenchmark class is used to benchmark the performance of various operations
 * like inserting words, searching for prefix matches, and deleting words using a Trie (prefix tree) data structure.
 * It also compares the performance of Trie-based prefix searches with a linear search approach.
 * <p>
 * The benchmarking involves measuring the execution time for:
 * - Inserting words into a Trie.
 * - Searching for auto-complete suggestions based on a given prefix using the Trie.
 * - Performing a linear prefix search on a list of words.
 * - Deleting words from the Trie.
 *
 * @author Shivani Sugurushetty (https://github.com/shivanisugurushetty)
 * (<a href="https://github.com/shivamsugurushetty/DSAIPG">DSAIPG (fork)</a>)
 */
public class TrieBenchmark {

    /**
     * The main method serves as the entry point for the application and benchmarks the performance
     * of various operations related to the Trie data structure. These operations include:
     * - Inserting words into the Trie.
     * - Searching for auto-complete results based on a prefix using the Trie.
     * - Comparing the search performance of the Trie with a linear search.
     * - Deleting words from the Trie.
     * <p>
     * TODO use Stopwatch instead of System.nanoTime()
     *
     * @param args command-line arguments (not used in this implementation).
     *             The method expects no arguments for its execution.
     * @throws IOException if there is an error reading the dictionary file from the file system.
     */
    public static void main(String[] args) throws IOException {
        List<String> words = Files.readAllLines(Paths.get("src/main/resources/dictionary.txt"));

        Trie trie = new Trie();
        long start = System.nanoTime();
        for (String word : words) trie.insert(word);
        long trieInsertTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<String> result = trie.autocomplete("ap");
        long trieSearchTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<String> linearResults = new ArrayList<>();
        for (String word : words) if (word.startsWith("ap")) linearResults.add(word);
        long linearSearchTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (String word : words) trie.delete(word);
        long trieDeleteTime = System.nanoTime() - start;

        System.out.printf("Trie insert time: %d ns%n", trieInsertTime);
        System.out.printf("Trie search time: %d ns%n", trieSearchTime);
        System.out.printf("Linear search time: %d ns%n", linearSearchTime);
        System.out.printf("Trie delete time: %d ns%n", trieDeleteTime);
    }
}

