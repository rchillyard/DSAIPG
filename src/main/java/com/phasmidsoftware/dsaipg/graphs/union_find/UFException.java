/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.union_find;

/**
 * UFException is a custom exception that extends {@code Exception}.
 * It is designed to handle error cases specific to union-find operations
 * within the context of disjoint-set data structures.
 * <p>
 * This exception is typically thrown when an invalid operation is attempted,
 * such as providing invalid elements, indices, or other usage violations in union-find implementations.
 */
public class UFException extends Exception {
    /**
     * Constructs a new UFException with the specified detail message.
     *
     * @param msg the detail message, providing information about the cause or context
     *            of the exception
     */
    public UFException(String msg) {
        super(msg);
    }
}