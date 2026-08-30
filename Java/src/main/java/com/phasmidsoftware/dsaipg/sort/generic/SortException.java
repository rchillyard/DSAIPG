package com.phasmidsoftware.dsaipg.sort.generic;

/**
 * Class SortException.
 */
public class SortException extends RuntimeException {

    /**
     * Constructs a new SortException with the specified detail message.
     *
     * @param message the detail message, which can be retrieved later using the getMessage method.
     *                This message provides additional context about the exception.
     */
    public SortException(String message) {
        super(message);
    }

    /**
     * Constructs a new SortException with a specified message and cause.
     *
     * @param message the detail message, saved for later retrieval by the {@link Throwable#getMessage()} method.
     * @param cause   the cause of the exception, saved for later retrieval by the {@link Throwable#getCause()} method.
     *                A null value is permitted and indicates that the cause is nonexistent or unknown.
     */
    public SortException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for SortException with a throwable cause.
     *
     * @param cause the cause of the exception, typically another throwable.
     */
    public SortException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new SortException with the specified detail message, cause, suppression settings,
     * and writable stack trace settings.
     *
     * @param message            the detailed message explaining the exception.
     * @param cause              the cause of the exception (a null value is permitted, and indicates
     *                            that the cause is nonexistent or unknown).
     * @param enableSuppression  whether or not suppression is enabled or disabled.
     * @param writableStackTrace whether or not the stack trace should be writable.
     */
    public SortException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
