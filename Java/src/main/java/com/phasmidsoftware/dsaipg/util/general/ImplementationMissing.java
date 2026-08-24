package com.phasmidsoftware.dsaipg.util.general;

public class ImplementationMissing extends RuntimeException {
    public ImplementationMissing() { super(location()); }

    private static String location() {
        for (StackTraceElement e : new Throwable().getStackTrace())
            if (!e.getClassName().equals(ImplementationMissing.class.getName()))
                return "You need to implement the code at " + e.getFileName() + ":" + e.getLineNumber();
        return "unknown location";
    }
}