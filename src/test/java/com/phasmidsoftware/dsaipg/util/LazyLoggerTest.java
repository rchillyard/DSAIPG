package com.phasmidsoftware.dsaipg.util;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LazyLoggerTest {

    static class StringEvaluator {

        public boolean isEvaluated() {
            return evaluated;
        }

        boolean evaluated = false;

        String evaluateMessage(String message) {
            System.out.println("Evaluate message: " + message);
            evaluated = true;
            return message;
        }

    }

    public String evaluateMessage(String message) {
        System.out.println("evaluate message: " + message);
        return message;
    }

    @Test
    public void testTraceLazy() {
        StringEvaluator se = new StringEvaluator();
        logger.trace(() -> "Hello " + se.evaluateMessage("trace message"));
        assertEquals(logger.isTraceEnabled(), se.isEvaluated());
    }

    @SuppressWarnings("EmptyMethod")
    @Ignore
    public void testTraceLazyException() {
    }

    @Test
    public void testTraceMethod() {
        StringEvaluator se = new StringEvaluator();
        logger.trace(() -> "Hello " + se.evaluateMessage("trace test message"));
        assertEquals(logger.isTraceEnabled(), se.isEvaluated());
    }

    @Test
    public void testTraceWithThrowable() {
        StringEvaluator se = new StringEvaluator();
        Throwable t = new RuntimeException("Test Exception");
        logger.trace(() -> "Hello " + se.evaluateMessage("trace exception message"), t);
        assertEquals(logger.isTraceEnabled(), se.isEvaluated());
    }

    @Test
    public void testDebugLazy() {
        StringEvaluator se = new StringEvaluator();
        logger.debug(() -> "Hello " + se.evaluateMessage("debug message"));
        assertEquals(logger.isDebugEnabled(), se.isEvaluated());
    }

    @SuppressWarnings("EmptyMethod")
    @Ignore
    public void testDebugLazyException() {
    }

    static final LazyLogger logger = new LazyLogger(LazyLoggerTest.class);
}