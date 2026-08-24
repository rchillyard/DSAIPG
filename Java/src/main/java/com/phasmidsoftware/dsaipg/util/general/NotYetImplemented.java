package com.phasmidsoftware.dsaipg.util.general;

public class NotYetImplemented extends RuntimeException{
    public NotYetImplemented() {
        super("Not yet implemented");
    }

    public NotYetImplemented(String message) {
        super(message);
    }

    public NotYetImplemented(String message, Throwable cause) {
        super(message, cause);
    }

    public NotYetImplemented(Throwable cause) {
        super(cause);
    }
}
