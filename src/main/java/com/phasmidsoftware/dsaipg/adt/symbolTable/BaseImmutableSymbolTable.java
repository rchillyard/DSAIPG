/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable;

/**
 * Abstract base class for implementing an immutable symbol table.
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
}