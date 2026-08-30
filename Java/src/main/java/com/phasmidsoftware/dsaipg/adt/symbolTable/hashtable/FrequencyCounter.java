/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.BaseImmutableSymbolTable;
import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;

/**
 * This class defines a specialized type of symbol table where the value corresponding to a key
 * is the count of the number of times increment has been called for that key.
 *
 * @param <Key> the key type.
 */
public class FrequencyCounter<Key> extends BaseImmutableSymbolTable<Key, Integer> {

    /**
     * Calculates the relative frequency of a specified key.
     * The relative frequency is determined by dividing the number of occurrences of the key
     * by the total number of increments across all keys.
     *
     * @param key the key whose relative frequency is to be calculated.
     * @return the relative frequency of the specified key as a double.
     */
    public double relativeFrequency(Key key) {
        return 1.0 * get(key) / total;
    }

    /**
     * Calculates the relative frequency of a specified key as a percentage.
     * The relative frequency is determined by multiplying the result of the method {@code relativeFrequency(Key)} by 100.
     *
     * @param key the key whose relative frequency as a percentage is to be calculated.
     * @return the relative frequency of the specified key as a percentage, represented as a double.
     */
    public double relativeFrequencyAsPercentage(Key key) {
        return 100.0 * relativeFrequency(key);
    }

    /**
     * Increments the frequency count of the specified key in the symbol table.
     * If the key does not exist in the table, it initializes its count to 1.
     * Additionally, increments the total frequency count across all keys.
     *
     * @param s the key whose frequency count is to be incremented
     */
    public void increment(Key s) {
        validateKey(s);
        // TO BE IMPLEMENTED 
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * Constructor to create a FrequencyCounter with a pre-defined symbol table.
     *
     * @param map the symbol table (ST<Key, Integer>) to be used for tracking frequencies.
     */
    public FrequencyCounter(ST<Key, Integer> map) {
        super(map, () -> 0);
    }

    /**
     * Default constructor to create a FrequencyCounter.
     * This constructor initializes the FrequencyCounter with a default instance of STMap.
     */
    public FrequencyCounter() {
        this(new STMap<>());
    }

    /**
     * Method to get the total number of increments over all existing keys.
     *
     * @return the total number of times increment has been called.
     */
    public long total() {
        return total;
    }

    public String toString() {
        return map.toString();
    }

    @SuppressWarnings("CanBeFinal")
    private long total = 0L;
}
