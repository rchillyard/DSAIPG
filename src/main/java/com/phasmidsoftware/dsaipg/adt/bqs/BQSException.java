/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

/**
 * This class represents a custom exception, BQSException, used in the context of operations
 * within the BQS (Bag/Queue/Stack) framework. It extends the standard Java Exception class
 * to provide specific error messages for exceptional situations that may occur during
 * handling of BQS-related data structures.
 * <p>
 * A BQSException may be thrown in scenarios such as:
 * - Attempting to add, remove, or locate an item in a data structure where the operation is invalid.
 * - Items being referenced which are not found in the relevant structure.
 * <p>
 * Constructors:
 * - The class provides a constructor to initialize the exception with a specific error message.
 */
public class BQSException extends Exception {
    public BQSException(String msg) {
        super(msg);
    }

    public BQSException(Exception x) {
        super(x);
    }
}