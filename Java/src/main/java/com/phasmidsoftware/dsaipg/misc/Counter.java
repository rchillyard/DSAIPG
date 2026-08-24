package com.phasmidsoftware.dsaipg.misc;

import java.util.Random;

/**
 * This class represents a counter that tracks the count of occurrences of an event or action.
 */
public class Counter {

    /**
     * Constructor to construct a new Counter with count initialized to zero.
     *
     * @param id the identifier by which we can distinguish this Counter.
     */
    public Counter(String id) {
        this.id = id;
    }

    /**
     * Mutating method to increase the count.
     */
    public void increment() {
        count++;
    }

    /**
     * Non-mutating method to return the current count.
     *
     * @return the value of count.
     */
    public int tally() {
        return count;
    }

    @Override
    public String toString() {
        return id + ": " + count;
    }

    /**
     * The main method simulates flipping a coin 100 times, using two counters to tally the number of "heads" and "tails"
     * and then prints the results.
     *
     * @param args the command-line arguments, not utilized in this program.
     */
    public static void main(String[] args) {
        Counter heads = new Counter("heads");
        Counter tails = new Counter("tails");
        Random random = new Random();
        for (int i = 0; i < 100; i++)
            if (random.nextBoolean()) heads.increment();
            else tails.increment();
        System.out.println(heads);
        System.out.println(tails);
    }

    /**
     * Represents the unique identifier for a specific instance of the Counter class.
     * It is immutable and serves to distinguish one Counter object from another.
     */
    private final String id;
    /**
     * Tracks the count of occurrences of an event or action.
     * This variable represents a mutable integer that is incremented
     * to reflect the number of times an event or action has occurred.
     */
    private int count = 0;
}
