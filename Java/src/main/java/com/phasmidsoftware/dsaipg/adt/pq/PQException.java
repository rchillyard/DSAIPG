/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.pq;

/**
 * Represents a custom exception used in scenarios related to Priority Queue operations.
 * This exception is typically thrown to indicate specific errors or issues
 * that occur during Priority Queue processing.
 */
public class PQException extends Exception {
    /**
     * Constructs a new PQException with the specified detail message.
     *
     * @param msg the detail message, saved for later retrieval by the getMessage() method.
     */
    public PQException(String msg) {
        super(msg);
    }
}
