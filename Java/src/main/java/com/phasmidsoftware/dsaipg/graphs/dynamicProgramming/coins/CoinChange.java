package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.coins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The CoinChange class provides a way to determine the smallest number of coins needed to make up a given amount
 * using a specified set of coin denominations.
 */
public class CoinChange {

    /**
     * Method to return the smallest number of coins which can make up the amount.
     *
     * @param amount the amount for which we need change.
     * @return the number of coins required.
     */
    public int solveForAmount(int amount) {
        Node root = new Node(amount);
        boolean satisfied = root.expand();
        if (satisfied)
            root.traverse(System.out::println);
        return 0;
    }

    /**
     * Constructs a CoinChange instance and initializes it with the given coin denominations.
     * The denominations are sorted in ascending order to optimize the solution process.
     *
     * @param coins an array of integers representing the denominations of coins available.
     */
    public CoinChange(int[] coins) {
        this.coins = coins;
        Arrays.sort(coins);
    }

    /**
     * This is the main method which processes input arguments to solve the coin change problem.
     * It initializes the CoinChange instance with the provided coin denominations and computes
     * the solution for the specified amount.
     *
     * @param args command-line arguments where the first argument represents the amount to change
     *             and the subsequent arguments represent the available coin denominations.
     */
    public static void main(String[] args) {
        int amount = Stream.of(args).map(Integer::valueOf).limit(1).mapToInt(x1 -> x1).toArray()[0];
        CoinChange coinChange = new CoinChange(Stream.of(args).map(Integer::valueOf).skip(1).mapToInt(x -> x).toArray());
        coinChange.solveForAmount(amount);
    }

    /**
     * Represents a node in a tree structure used for solving the coin change problem.
     * Each node corresponds to a specific state in the solution process, storing
     * information such as the current coin used, the remaining required amount,
     * and its children nodes.
     * <p>
     * The class provides methods for expanding nodes recursively to explore possible solutions,
     * adding child nodes, and traversing the tree for processing or output.
     */
    class Node {

        /**
         * Retrieves the required amount associated with this node, representing the remaining balance
         * in the process of solving the coin change problem.
         *
         * @return the remaining required amount as an integer.
         */
        public int getRequired() {
            return required;
        }

        /**
         * Recursively expands the current node by generating child nodes if the required amount
         * is greater than zero. Each child node represents a valid state in the solution tree,
         * reducing the remaining amount by using a specific coin denomination.
         *
         * The method iterates through available coin denominations in reverse order,
         * creating child nodes for those denominations that do not exceed the remaining amount.
         * If a valid solution path is discovered recursively, the child node is added to the current node's children.
         *
         * @return true if the current node has valid children or the required amount is zero,
         *         indicating a valid solution exists. Otherwise, returns false.
         */
        public boolean expand() {
            if (required > 0) {
                for (int i = coins.length; i > 0; i--) {
                    if (coins[i - 1] <= required) {
                        Node node = new Node(coins[i - 1], this);
                        if (node.expand()) {
                            add(node);
                            break;
                        }
                    }
                }
                return !children.isEmpty();
            } else
                return true;
        }

        /**
         * Traverses the current node and all its children, applying a given consumer
         * function to each node. The traversal is performed in a depth-first manner.
         *
         * @param consumer a {@link Consumer} function to process the string representation
         *                 of each node in the traversal.
         */
        public void traverse(Consumer<String> consumer) {
            consumer.accept(toString());
            children.forEach(n -> n.traverse(consumer));
        }

        /**
         * Adds the specified node to the list of child nodes of the current node.
         *
         * @param node the {@code Node} object to add as a child.
         * @return {@code true} if the child node was successfully added, {@code false} otherwise.
         */
        public boolean add(Node node) {
            return children.add(node);
        }

        @Override
        public String toString() {
            return "Node: " + "nCoins: " + nCoins + ", " + "coin: " + coin + ", " + "required: " + required;
        }

        /**
         * Constructs a Node object, representing a state in the solution tree of a coin change problem.
         *
         * @param coin    the coin denomination used to reach this node, or 0 if this is the root node.
         * @param required the remaining amount to be fulfilled in the coin change process.
         * @param parent   the parent node in the solution tree. If this is the root node, the parent is null.
         */
        public Node(int coin, int required, Node parent) {
            this.coin = coin;
            this.required = required;
            children = new ArrayList<>();
            if (coin > 0 && parent != null) nCoins = parent.nCoins + 1;
        }

        /**
         * Constructs a new Node instance representing a state in solving the coin change problem.
         * The required balance is calculated as the difference between the parent's required amount
         * and the value of the coin used.
         *
         * @param coin   the value of the coin used to reach this state.
         * @param parent the parent node representing the previous state in the solution tree.
         */
        public Node(int coin, Node parent) {
            this(coin, parent.getRequired() - coin, parent);
        }

        /**
         * Constructs a Node instance initialized with the specified required amount.
         * This represents the root node in the coin change solution tree with no parent
         * and an initial coin value of 0.
         *
         * @param amount the required amount associated with this node, representing the
         *               initial amount to be solved in the coin change problem.
         */
        public Node(int amount) {
            this(0, amount, null);
        }

        private final int required;
        private final int coin;
        private int nCoins = 0;
        private final List<Node> children;
    }

    private final int[] coins;
}
