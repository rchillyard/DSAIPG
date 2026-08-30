package com.phasmidsoftware.dsaipg.sort.helper;

/**
 * HelperException is a custom exception class that extends RuntimeException.
 * It is used to represent application-specific exceptions where utility or helper methods encounter errors.
 * This exception provides various constructors to support detailed error messages and cause tracing.
 */
public class HelperException extends RuntimeException {

    /**
     * Constructs a new HelperException with the specified detail message.
     *
     * @param message the detail message which can be retrieved later using the getMessage() method.
     *                This message provides more information about the exception.
     */
    public HelperException(String message) {
        super(message);
    }

    /**
     * Constructs a new HelperException with the specified detail message and cause.
     *
     * @param message the detail message, which can be retrieved later by the getMessage() method.
     * @param cause   the cause of the exception, which can be retrieved later by the getCause() method.
     *                A null value indicates that the cause is nonexistent or unknown.
     */
    public HelperException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new HelperException with the specified cause.
     *
     * @param cause the underlying throwable that caused this exception to be thrown
     */
    public HelperException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new HelperException with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message the detail message. The detail message is saved for later
     *        retrieval by the {@link Throwable#getMessage()} method.
     * @param cause the cause. The cause is saved for later retrieval by the
     *        {@link Throwable#getCause()} method. A null value is permitted, and
     *        indicates that the cause is nonexistent or unknown.
     * @param enableSuppression whether or not suppression is enabled or disabled.
     * @param writableStackTrace whether or not the stack trace should be writable.
     */
    public HelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
