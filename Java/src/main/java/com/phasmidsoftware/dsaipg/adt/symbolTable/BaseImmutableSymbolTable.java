package com.phasmidsoftware.dsaipg.adt.symbolTable;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Abstract base class for implementing an immutable symbol table with default value.
 * <p>
 * This class serves as a foundation for creating immutable symbol tables by
 * providing the basic structure and functionality defined by the {@link ImmutableSymbolTable} interface.
 * Concrete subclasses should extend this class to define specific implementations
 * of an immutable symbol table.
 *
 * @param <Key>   the type of keys maintained by this symbol table.
 * @param <Value> the type of values associated with the keys.
 */
abstract public class BaseImmutableSymbolTable<Key, Value> implements ImmutableSymbolTable<Key, Value> {

    /**
     * Retrieves the value associated with the given key from the symbol table.
     * If the key is not present in the symbol table, the method returns a default value of 0.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value associated with the specified key, or 0 if the key is not present.
     */
    public Value get(Key key) {
        validateKey(key);
        return map.getOrDefault(key, defaultSupplier);
    }

    /**
     * Get the set of keys in this symbol table.
     *
     * @return the Set of keys.
     */
    public Set<Key> keys() {
        return map.keys();
    }

    /**
     * Get the size of this FrequencyCounter.
     *
     * @return the current size.
     */
    public int size() {
        return map.size();
    }

    /**
     * Constructs an instance of BaseImmutableSymbolTable with a specified map and a default value supplier.
     *
     * @param map             the symbol table (ST<Key, Value>) used to store key-value pairs. This defines the underlying structure for the symbol table.
     * @param defaultSupplier the supplier used to provide default values when a requested key is not present in the map.
     */
    public BaseImmutableSymbolTable(ST<Key, Value> map, Supplier<Value> defaultSupplier) {
        this.map = map;
        this.defaultSupplier = defaultSupplier;
    }

    /**
     * A symbol table (ST) to track key-value pairs where the key is of a
     * generic type and the value is an {@code Integer}.
     * This symbol table is used for managing frequencies of keys, allowing operations such as insertion, retrieval,
     * and frequency counting in the context of the {@code FrequencyCounter}.
     * The map is initialized to maintain the frequency counts for each key.
     */
    protected final ST<Key, Value> map;

    /**
     * A supplier of default values for the symbol table.
     * If a key is not present in the symbol table, the value provided
     * by this supplier will be returned as the default.
     * This ensures that a valid value is always available even when
     * the key being queried is absent.
     */
    private final Supplier<Value> defaultSupplier;
}
