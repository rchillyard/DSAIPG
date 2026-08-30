package com.phasmidsoftware.dsaipg.adt.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A Trie (prefix tree) is a tree-like data structure that stores a dynamic set of strings. This class provides
 * efficient insertion, search, deletion, and autocomplete operations for strings.
 *
 * @author Shivani Sugurushetty (https://github.com/shivanisugurushetty)
 * (<a href="https://github.com/shivamsugurushetty/DSAIPG">DSAIPG (fork)</a>)
 */
public class Trie {

    /**
     * Inserts a word into the Trie. This method adds all characters of the given word
     * to the Trie, creating new nodes as necessary. It marks the last character's
     * node as the end of a complete word.
     *
     * @param word the word to be inserted into the Trie
     */
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    /**
     * Searches for the specified word in the Trie and determines whether it exists as a complete word.
     * <p>
     * The method iteratively traverses the Trie based on the characters of the input word.
     * If all characters are found in the Trie and the last node corresponds to the end of a word,
     * the word is considered present. Otherwise, it is not found.
     *
     * @param word the word to search for in the Trie
     * @return true if the word exists in the Trie and is marked as a complete word, false otherwise
     */
    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return false;
        }
        return current.isEndOfWord;
    }

    /**
     * Retrieves a list of words from the Trie that begin with the specified prefix.
     * This method traverses the Trie to find the node corresponding to the end of the given prefix
     * and then uses a depth-first search (DFS) to collect all words that start with the prefix.
     *
     * @param prefix the prefix string for which autocomplete suggestions are to be generated.
     *               It is case-sensitive and should not be null.
     * @return a list of words that begin with the specified prefix. If no words match the prefix,
     * an empty list is returned.
     */
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return results;
        }
        dfs(current, prefix, results);
        return results;
    }

    /**
     * Deletes a word from the Trie. If the word exists and is fully removed, this method returns true.
     * If the word does not exist or is a prefix of another word in the Trie, it returns false.
     *
     * @param word the word to be deleted from the Trie
     * @return true if the word was successfully removed, false otherwise
     */
    public boolean delete(String word) {
        return delete(root, word, 0);
    }

    /**
     * Constructs an instance of a Trie.
     * A Trie is a prefix tree data structure that supports efficient insertion,
     * search, deletion, and autocompletion of strings.
     * <p>
     * This constructor initializes the root node of the Trie as an empty TrieNode.
     */
    public Trie() {
        root = new TrieNode();
    }

    private final TrieNode root;

    /**
     * Performs a depth-first search (DFS) from the given TrieNode to collect all words
     * starting from the specified prefix. This method is used as a helper to traverse
     * the Trie and gather word suggestions for autocompletion or other purposes.
     *
     * @param node    the current TrieNode to process during the DFS traversal.
     * @param prefix  the prefix string representing the path from the root to the current node.
     * @param results the list where the matched words will be added as the DFS progresses.
     */
    private void dfs(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord) results.add(prefix);
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            dfs(entry.getValue(), prefix + entry.getKey(), results);
        }
    }

    /**
     * Recursively deletes a word from the Trie, starting from the given node at the specified index.
     * This method traverses the Trie based on the characters of the input word. If the word exists,
     * it removes the corresponding nodes only if they are not shared with other words in the Trie.
     *
     * @param current the current TrieNode being processed
     * @param word    the word to be deleted from the Trie
     * @param index   the current index of the character in the word being processed
     * @return true if the current node should be deleted, false otherwise
     */
    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) return false;
            current.isEndOfWord = false;
            return current.children.isEmpty();
        }

        char ch = word.charAt(index);
        TrieNode node = current.children.get(ch);
        if (node == null) return false;

        boolean shouldDeleteCurrentNode = delete(node, word, index + 1);

        if (shouldDeleteCurrentNode) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }

        return false;
    }

}

