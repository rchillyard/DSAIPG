package com.phasmidsoftware.dsaipg.util.general;

import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * JUnit rule which reports a test that hit an unwritten exercise as skipped
 * rather than failed.
 * <p>
 * This is the Java counterpart of the CancelOnNotImplemented mixin used in the
 * Scala repositories, where an unimplemented method throws NotImplementedError
 * and the trait converts a Failed outcome into a Canceled one. Here the
 * equivalent signal is {@link ImplementationMissing}, which every stub throws,
 * or {@link NotYetImplemented}, which a few skeletons throw; and the equivalent
 * of Canceled is an assumption failure, which JUnit and Surefire both report as
 * skipped and which does not fail the build.
 * <p>
 * The point is that roughly a third of the tests in the student tree fail purely
 * because the exercise has not been done yet. Red is therefore the correct
 * result, which makes the pass/fail signal useless: a student cannot tell a
 * genuine mistake from work not yet started, and neither can CI. With this rule
 * an untouched checkout runs green with a long list of skips, and anything red
 * is a real problem.
 * <p>
 * NOTE this only sees an exception which escapes the test method. If a test
 * swallows it -- inside a try/catch, or an assertThrows expecting a broad type --
 * the rule never gets the chance. The Scala version has a family of helpers
 * (tryOrCancel, assertThrowsOrCancel and so on) for exactly those cases.
 * <p>
 * Declared in each test class as:
 * <pre>
 *     &#64;Rule
 *     public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();
 * </pre>
 * JUnit 4 has no way to register a rule globally, which is why it has to appear
 * in every test class rather than in one place.
 */
public class CancelOnNotImplemented implements TestRule {

    /**
     * The maximum number of causes to follow, so that a self-referencing cause
     * chain cannot spin.
     */
    private static final int MAX_CAUSE_DEPTH = 20;

    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable thrown) {
                    Throwable unimplemented = findUnimplemented(thrown);
                    if (unimplemented == null) throw thrown;
                    Assume.assumeNoException(unimplemented.getMessage(), unimplemented);
                }
            }
        };
    }

    /**
     * Look for an unimplemented-exercise exception, at the top of the given
     * throwable or anywhere in its cause chain. Checking the chain matters
     * because a stub reached through a stream, a lambda or a reflective call
     * often arrives wrapped.
     *
     * @param thrown the exception which escaped the test.
     * @return the unimplemented-exercise exception, or null if this is a real
     * failure that should be reported as such.
     */
    private static Throwable findUnimplemented(Throwable thrown) {
        Throwable x = thrown;
        for (int depth = 0; x != null && depth < MAX_CAUSE_DEPTH; depth++) {
            if (x instanceof ImplementationMissing || x instanceof NotYetImplemented) return x;
            if (x.getCause() == x) break;
            x = x.getCause();
        }
        return null;
    }
}
