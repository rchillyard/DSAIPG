/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

public class HelperException extends RuntimeException {

    public HelperException(String message) {
        super(message);
    }

    public HelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public HelperException(Throwable cause) {
        super(cause);
    }

    public HelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}