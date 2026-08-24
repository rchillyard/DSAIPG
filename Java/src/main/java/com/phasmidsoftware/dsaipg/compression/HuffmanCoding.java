package com.phasmidsoftware.dsaipg.compression;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * The HuffmanCoding class provides functionality for creating and manipulating a Huffman tree used
 * for efficient data encoding and decoding based on symbol frequencies. It supports adding nodes
 * to the priority queue, building the Huffman tree, and further encoding/decoding operations
 * using nested static classes.
 */
public class HuffmanCoding {

    /**
     * Generates a Huffman tree based on the nodes in the priority queue.
     * This method iteratively combines the two nodes with the lowest frequencies
     * in the priority queue into a new parent node until only one node remains.
     * The resulting single node represents the root of the Huffman tree.
     *
     * @return The root node of the generated Huffman tree, or null if the priority queue is empty.
     */
    public Node generateCode() {
        while (priorityQueue.size() > 1) {
            Node node0 = priorityQueue.poll();
            Node node1 = priorityQueue.poll();
            assert node1 != null;
            add(new Node(node0.frequency + node1.frequency, node0.symbol + "-" + node1.symbol, node0, node1));
        }
        return priorityQueue.poll();
    }

    /**
     * Adds a node to the priority queue for further processing in the Huffman coding algorithm.
     *
     * @param node the Node to be added to the priority queue; typically represents a symbol with its frequency
     */
    public void add(Node node) {
        priorityQueue.add(node);
    }

    /**
     * Constructs a HuffmanCoding instance with a predefined priority queue of nodes.
     * The priority queue is expected to contain nodes that represent symbols and their respective
     * frequencies, ordered by their frequencies for constructing a Huffman tree.
     *
     * @param priorityQueue the priority queue of nodes to be used for Huffman coding; each node
     *                      typically contains a symbol and its frequency
     */
    public HuffmanCoding(PriorityQueue<Node> priorityQueue) {
        this.priorityQueue = priorityQueue;
    }

    /**
     * Default constructor for the HuffmanCoding class.
     * Initializes a new instance of HuffmanCoding with an empty priority queue.
     * This constructor is useful when the priority queue does not need to be
     * pre-initialized and will be populated later.
     */
    public HuffmanCoding() {
        this(new PriorityQueue<>());
    }

    private final PriorityQueue<Node> priorityQueue;

    /**
     * The {@code Value} class is responsible for managing binary encoding operations
     * within a container, using a specified number of bits. This utility class is often
     * used in conjunction with other encoding components, such as Huffman coding, to
     * efficiently manage and represent encoded binary values.
     * <p>
     * The class implements {@code AutoCloseable} to ensure proper closure and flushing
     * of any remaining available bits when the encoding process is done.
     * <p>
     * Instances of this class hold a binary value and track the remaining available
     * bits that can be utilized for encoding operations.
     */
    static class Value implements AutoCloseable {

        /**
         * Encodes the given {@code Code} object into the current encoding container.
         * If the length of the code to be encoded exceeds the available space, the remaining part
         * is returned as a new {@code Code} object.
         *
         * @param code the {@code Code} object containing the value and its length to be encoded
         * @return a {@code Code} object representing the remaining unencoded part of the input,
         * or {@code null} if the entire code fits within the available space
         */
        public Code encode(Code code) {
            Code result = null;
            long val = code.value;
            int len = code.length;
            if (available < len) {
                int shiftVal = 64 - len + available;
                result = new Code(val << shiftVal >> shiftVal, len - available);
                val = val >> (len - available);
                len = available;
            }
            encode(val, len);
            return result;
        }

        /**
         * Constructs a {@code Value} object with the specified initial value and available bits.
         *
         * @param x         the initial value to be set for the encoding container
         * @param available the number of available bits for encoding operations
         */
        public Value(long x, int available) {
            super();
            this.x = x;
            this.available = available;
        }

        /**
         * Constructs a {@code Value} object with the specified initial value.
         * Initializes the number of available bits to the default value of 64.
         *
         * @param x the initial value to be set for the encoding container
         */
        public Value(long x) {
            this(x, 64);
        }

        /**
         * Encodes a specified value into the current encoding container using a given bit length.
         *
         * @param value  the binary value to be encoded
         * @param length the number of bits to use from the value for the encoding operation
         */
        private void encode(long value, int length) {
            x = x << length | value;
            available -= length;
        }

        /**
         * Finalizes the encoding operation by filling the remaining available bits with zeros.
         * This method ensures that the encoding container is appropriately padded to meet
         * the bit-length requirements, preventing any data misalignment.
         */
        public void close() {
            encode(0L, available);
        }

        /**
         * Represents the binary-encoded value in the encoding container.
         * This variable stores the data resulting from encoding operations
         * and acts as the main container for all encoded bits.
         */
        long x;
        /**
         * Represents the number of bits currently available for encoding operations
         * within the {@code Value} object. This value is decreased as bits are used
         * during encoding processes and defines the space remaining for additional
         * data encoding.
         */
        int available;
    }

    /**
     * Represents a code with a binary value and its corresponding bit length.
     * This class encapsulates the value and length, and provides methods for
     * constructing and manipulating binary codes, typically used in encoding
     * processes such as Huffman coding.
     */
    static class Code {
        public Code add(int x) {
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }

        @Override
        public String toString() {
            String prefix = "00000000000000000000000000000";
            String s = prefix + Long.toBinaryString(value);
            return s.substring(s.length() - length);
        }

        /**
         * Constructs a {@code Code} object with the specified binary value and bit length.
         *
         * @param value  the binary value represented by this code
         * @param length the bit length of the code
         */
        public Code(long value, int length) {
            this.value = value;
            this.length = length;
        }

        final long value;
        final int length;
    }

    /**
     * A utility class for encoding symbols using Huffman coding.
     * This encoder relies on a Huffman tree to generate compact binary codes for symbols
     * and provides methods to encode an array of symbols into binary values.
     */
    public static class HuffmanEncoder {
        /**
         * Encodes an array of symbols into an array of {@code Long} values using a Huffman encoding mechanism.
         * Each symbol is matched to its corresponding Huffman code, and the codes are packed into {@code Long}
         * values sequentially. If a symbol is unrecognized, an exception is thrown.
         *
         * @param symbols an array of string symbols to be encoded
         * @return an array of {@code Long} values representing the encoded binary data
         * @throws RuntimeException if a symbol in the input array does not have a corresponding Huffman code
         */
        public Long[] encode(String[] symbols) {
            List<Value> values = new ArrayList<>();
            Value current = new Value(0L);
            for (String symbol : symbols) {
                Code code = get(symbol);
                if (code == null)
                    throw new RuntimeException("unknown symbol: " + symbol);
                Code result = current.encode(code);
                if (result != null) {
                    values.add(current);
                    current = new Value(0L);
                    current.encode(code);
                }
            }
            current.close();
            values.add(current);
            Value[] xs = values.toArray(new Value[0]);
            Stream<Long> longStream = Stream.of(xs).map(v -> v.x);
            IntFunction<Long[]> intFunction = value -> new Long[xs.length];
            return longStream.toArray(intFunction);
        }

        /**
         * Retrieves the Huffman-encoded {@code Code} associated with the given key.
         * If no code is found for the provided key, the method attempts to retrieve
         * the code by appending a zero-width joiner (U+FE0F) character to the key.
         *
         * @param key the symbol for which to retrieve the corresponding {@code Code}
         * @return the {@code Code} associated with the given key, or {@code null}
         * if no such code exists
         */
        Code get(String key) {
            char c = 0xFE0F;
            Code code = encoder.get(key);
            if (code == null) code = encoder.get(key + c);
            return code;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (String key : encoder.keySet()) result.append(key).append("=").append(encoder.get(key)).append("\n");
            return result.toString();
        }

        /**
         * Constructor for the HuffmanEncoder class that initializes the encoder
         * by traversing the provided Huffman tree and mapping symbols to their corresponding codes.
         *
         * @param node the root node of the Huffman tree, which represents the hierarchical
         *             structure of symbols and their frequencies
         */
        public HuffmanEncoder(Node node) {
            encoder = getEncoder(node);
        }

        /**
         * Generates a mapping of symbols to their corresponding Huffman codes by traversing
         * the provided Huffman tree. The method performs a depth-first search on the tree and
         * uses the structure of the tree to assign specific binary codes to each symbol.
         *
         * @param node the root node of the Huffman tree. This node can have child nodes
         *             representing the binary branches, or can be a leaf node containing a symbol.
         * @return a map where the keys are the symbols from the Huffman tree, and the values
         * are the corresponding Huffman codes represented as {@link Code} objects.
         */
        private Map<String, Code> getEncoder(Node node) {
            Map<String, Code> result = new HashMap<>();
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }

        /**
         * A mapping between symbols and their corresponding Huffman-encoded {@link Code} objects.
         * This map serves as the primary data structure for storing and retrieving the binary
         * codes associated with symbols during the encoding process.
         *
         * The variable is initialized by traversing a Huffman tree in the constructor
         * of the containing class, {@code HuffmanEncoder}. The encoding process relies
         * on this map to retrieve the binary representation for any given symbol.
         */
        private final Map<String, Code> encoder;
    }

    /**
     * The {@code HuffmanDecoder} class provides functionality to decode Huffman-encoded binary data
     * represented as an array of long values. It utilizes a Huffman tree for the decoding process.
     *
     * This class is designed to work with a pre-constructed Huffman tree, where leaf nodes represent
     * symbols and internal nodes represent branches based on the binary values in the input data.
     */
    public static class HuffmanDecoder {
        /**
         * Decodes a series of Huffman-encoded binary data segments represented as an array of long values.
         * Iteratively traverses the Huffman tree based on the binary data to reconstruct the original sequence
         * of symbols.
         *
         * @param array an array of long values where each value represents a portion of Huffman-encoded binary data
         * @return the decoded string constructed from the Huffman tree, or a partial result if decoding is interrupted
         */
        public String decode(long[] array) {
            StringBuilder stringBuilder = new StringBuilder();
            Node state = node;
            for (long x : array) {
                state = decode(stringBuilder, state, x);
                if (state == null) break;
            }
            return stringBuilder.toString();
        }

        /**
         * Decodes a portion of Huffman-encoded binary data represented by a {@code long} value.
         * Traverses the Huffman tree to reconstruct the original sequence of symbols and appends
         * the decoded symbols to the provided {@code StringBuilder}.
         *
         * @param stringBuilder the {@code StringBuilder} used to accumulate the decoded symbols
         * @param state the current {@code Node} in the Huffman tree used as the starting point for
         *              this decoding operation
         * @param x the {@code long} value representing a portion of the Huffman-encoded binary data
         * @return the resulting {@code Node} after processing the specified {@code long} value,
         *         or {@code null} if the decoding process encounters an invalid state
         */
        private Node decode(StringBuilder stringBuilder, Node state, long x) {
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }

        /**
         * Constructs a new {@code HuffmanDecoder} instance using the given Huffman tree node as the
         * root of the decoding tree. The root node represents the starting point of the decoding
         * process, where internal nodes are used to traverse the tree based on binary input data
         * and leaf nodes represent decoded symbols.
         *
         * @param node the root node of the Huffman tree to be used for decoding; typically calculated
         *             based on the frequency of symbols in the data
         */
        public HuffmanDecoder(Node node) {
            this.node = node;
        }

        private final Node node;
    }

    /**
     * Represents a node in a Huffman tree used for encoding symbols based on their frequency.
     * Each Node can either be a leaf node containing a symbol or an internal node
     * linking to child nodes.
     */
    public static class Node implements Comparable<Node> {
        /**
         * Compares this Node with the specified Node for order based on their frequency values.
         *
         * @param o the Node to be compared with this Node.
         * @return a negative integer, zero, or a positive integer as this Node's frequency
         * is less than, equal to, or greater than the specified Node's frequency.
         */
        public int compareTo(Node o) {
            return Integer.compare(frequency, o.frequency);
        }

        /**
         * Determines if the current node is a leaf node in the Huffman tree.
         * A leaf node does not have any child nodes.
         *
         * @return {@code true} if the current node is a leaf node (i.e., both child nodes are null),
         * {@code false} otherwise.
         */
        public boolean isLeaf() {
            return one == null & zero == null;
        }

        /**
         * Performs a depth-first search (DFS) traversal on the Huffman tree starting from the current node.
         * This method applies the given depth function and consumer action to each node during the traversal.
         *
         * @param depthFunction  a function that calculates the depth or context for the current node
         *                       based on the previous depth indicator and the branch value.
         *                       The branch value can be {@code 0} or {@code 1} to indicate the path taken.
         * @param consumer       a consumer that performs an operation with the current node and the calculated depth.
         * @param depthIndicator an initial value or context passed to the depth function, typically representing
         *                       the starting point for calculating the depth or state in the traversal.
         * @param branch         the indicator for the branch (binary path) being traversed. It can be
         *                       {@code null} for the root node or {@code 0}/{@code 1} for child nodes.
         */
        public void dfs(BiFunction<Object, Integer, Object> depthFunction, BiConsumer<Node, Object> consumer, Object depthIndicator, Integer branch) {
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }

        /**
         * Constructs a Node object representing a single node in a Huffman tree,
         * with information about frequency, symbol, and its child nodes.
         *
         * @param frequency the frequency of the symbol associated with this node
         * @param symbol    the symbol represented by this node, or a composite symbol
         *                  if this node combines two or more symbols
         * @param zero      the left child node, representing the binary value 0
         * @param one       the right child node, representing the binary value 1
         */
        public Node(int frequency,
                    String symbol,
                    Node zero,
                    Node one) {
            this.frequency = frequency;
            this.symbol = symbol;
            this.zero = zero;
            this.one = one;
        }

        /**
         * Constructs a Node object representing a single node in a Huffman tree,
         * initialized with a symbol and its frequency.
         *
         * @param symbol    the symbol represented by this node, or a composite symbol
         *                  if this node combines two or more symbols
         * @param frequency the frequency of the symbol associated with this node
         */
        public Node(String symbol, int frequency) {
            this(frequency, symbol, null, null);
        }

        private final int frequency;
        private final String symbol;
        private final Node zero;
        private final Node one;
    }

    /**
     * The entry point of the application that demonstrates Huffman coding.
     * This method creates a HuffmanCoding instance, generates a Huffman tree,
     * and initializes both encoder and decoder for displaying their behaviors.
     *
     * TODO put this into unit test.
     *
     * @param args command-line arguments passed to the program; they are not utilized in this implementation
     */
    public static void main(String[] args) {
        HuffmanCoding huffmanCoding = createHuffmanCoding();
        Node tree = huffmanCoding.generateCode();
        HuffmanEncoder huffmanEncoder = new HuffmanEncoder(tree);
        showEncoder(huffmanEncoder);
        HuffmanDecoder huffmanDecoder = new HuffmanDecoder(tree);
        showTree(tree);
    }

    /**
     * Parses a given string representing a LIN (bridge hand format) into an array of strings.
     * The input string is processed and split into various components, such as cards and events,
     * based on the LIN format specification.
     *
     * @param w the input string representing the LIN format containing bridge game data
     * @return an array of strings extracted and processed from the LIN format, where each element represents a component of the bridge data
     */
    static String[] parseLin(String w) {
//        char c = 0xFE0F;
//        StringBuffer z = new StringBuffer();
//        z.append(c);
//        StringBuffer y = new StringBuffer();
//        String[] splits = w.toUpperCase().replace(z,y).replaceAll("S", "♠").replaceAll("H", "♥️").replaceAll("D", "♦").replaceAll("C", "♣").split("\\|");
        String[] splits = w.toUpperCase().split("\\|");
//        System.out.println(Arrays.toString(splits));
        String deal = splits[5].substring(2);
        List<String> result = new ArrayList<>(stringToStrings(deal));
        for (int i = 13; i < splits.length; i += 2)
            result.addAll(stringToStrings(splits[i]));
        result.add(null);
        return result.toArray(new String[0]);
    }

    /**
     * Converts a given string into a list of strings by extracting its non-space and non-comma characters.
     * Each character is converted into a single-element string and added to the resulting list.
     *
     * @param w the input string to be processed, containing characters to be extracted
     * @return a list of strings where each element is a single character from the input string, excluding spaces and commas
     */
    private static List<String> stringToStrings(String w) {
        List<String> result = new ArrayList<>();
        char[] chars = w.toCharArray();
        for (char x : chars) if (x != ' ' && x != ',') result.add(String.valueOf(x));
        return result;
    }

    /**
     * Creates and initializes a HuffmanCoding instance with predefined nodes and their associated frequencies.
     * The nodes represent symbols and their frequencies, which will form the basis for building a Huffman tree.
     * This method adds multiple nodes with specified symbols and frequencies to the HuffmanCoding instance.
     *
     * @return A HuffmanCoding object pre-populated with nodes containing symbols and their frequencies,
     *         ready for further processing such as generating a Huffman tree.
     */
    static HuffmanCoding createHuffmanCoding() {
        HuffmanCoding huffmanCoding = new HuffmanCoding();
        huffmanCoding.add(new Node("W", 28));
        huffmanCoding.add(new Node("N", 28));
        huffmanCoding.add(new Node("E", 28));
        huffmanCoding.add(new Node("Z", 28)); // currently, we don't actually use S
        huffmanCoding.add(new Node("P", 20));
        huffmanCoding.add(new Node("1", 12));
        huffmanCoding.add(new Node("2", 14));
        huffmanCoding.add(new Node("3", 12));
        huffmanCoding.add(new Node("S️", 12)); // NOTE: this is now the same as South
        huffmanCoding.add(new Node("H️", 12));
        huffmanCoding.add(new Node("D️", 12));
        huffmanCoding.add(new Node("C️", 12));
//        huffmanCoding.add(new Node(12, "♠️"));
//        huffmanCoding.add(new Node(12, "♥️"));
//        huffmanCoding.add(new Node(12, "♦️"));
//        huffmanCoding.add(new Node(12, "♣️"));
        huffmanCoding.add(new Node("4", 11));
        huffmanCoding.add(new Node("5", 10));
        huffmanCoding.add(new Node("6", 9));
        huffmanCoding.add(new Node("7", 8));
        huffmanCoding.add(new Node("8", 8));
        huffmanCoding.add(new Node("9", 8));
        huffmanCoding.add(new Node("T", 8));
        huffmanCoding.add(new Node("A", 8));
        huffmanCoding.add(new Node("K", 8));
        huffmanCoding.add(new Node("Q", 8));
        huffmanCoding.add(new Node("J", 8));
        huffmanCoding.add(new Node("NT", 3));
        huffmanCoding.add(new Node("X", 2));
        huffmanCoding.add(new Node(null, 1));
        huffmanCoding.add(new Node("XX", 1));
        return huffmanCoding;
    }

    /**
     * Displays the structure of the given Huffman tree by performing
     * a depth-first traversal and printing each node's details to the console.
     *
     * @param tree the root node of the Huffman tree to be displayed; represents
     *             the hierarchical encoding structure of symbols and frequencies
     */
    public static void showTree(Node tree) {
        PrintWriter pw = new PrintWriter(System.out);
        showTree(tree, pw);
        pw.flush();
        pw.close();
    }

    /**
     * Displays the structure of the given Huffman tree by performing a depth-first traversal
     * and printing the details of each node to the specified {@code PrintWriter}.
     *
     * @param tree the root node of the Huffman tree to be displayed; represents the hierarchical
     *             encoding structure of symbols and their corresponding frequencies
     * @param pw   the {@code PrintWriter} used to output the details of each node during the traversal
     */
    public static void showTree(Node tree, PrintWriter pw) {
        tree.dfs((o, b) -> o + "  " + (b != null ? b : ""),
                (node, o) -> pw.println(o + ": " + node.symbol + " (" + node.frequency + ") "),
                "", null);
    }

    /**
     * Displays the details of the provided HuffmanEncoder by invoking its {@code toString} method.
     * This method outputs a textual representation of the encoder to the console,
     * typically including the mapping of symbols to their corresponding Huffman codes.
     *
     * @param encoder the {@code HuffmanEncoder} instance whose details are to be displayed;
     *                must not be null
     */
    public static void showEncoder(final HuffmanEncoder encoder) {
        System.out.println(encoder.toString());
    }

}
