package com.phasmidsoftware.dsaipg.adt.trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single node in a Trie (prefix tree) data structure.
 * Each TrieNode maintains a mapping of characters to its child TrieNodes
 * and a boolean indicating whether this node marks the end of a word.
 * <p>
 * The TrieNode class is the building block of the Trie structure, which
 * provides operations such as insertion, search, deletion, and autocompletion
 * of strings.
 *
 * @author Shivani Sugurushetty (<a href="https://github.com/shivanisugurushetty">shivanisugurushetty</a>)
 * (<a href="https://github.com/shivamsugurushetty/DSAIPG">DSAIPG (fork)</a>)
 */
public class TrieNode {

    /**
     * Constructs a new TrieNode instance.
     * <p>
     * This constructor initializes a TrieNode with an empty map for its children,
     * where each key represents a character, and the corresponding value represents
     * a child TrieNode.
     * It also initializes the node to not mark the end of any word.
     */
    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }

    /**
     * A mapping of characters to their corresponding child TrieNodes.
     * This map represents the children of the current TrieNode, where each key
     * is a character, and the associated value is another TrieNode.
     * It is used to navigate between levels of the Trie and store the structural
     * hierarchy of characters in the prefix tree.
     */
    Map<Character, TrieNode> children;
    /**
     * A boolean flag that indicates whether the current node in the Trie marks the end of a valid word.
     * <p>
     * If true, the node represents the last character of a complete word stored in the Trie.
     * Otherwise, it signifies that the node is part of a word or a prefix but not the end of a complete word.
     */
    boolean isEndOfWord;
}

