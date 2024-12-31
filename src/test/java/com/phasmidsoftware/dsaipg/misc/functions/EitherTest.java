package com.phasmidsoftware.dsaipg.misc.functions;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EitherTest {

    /**
     * Tests the {@link Either#map(Function, Function)} method, which applies the respective function
     * based on whether the current instance is a left or right Either. Returns the result of the function.
     */

    // Test case: mapping over a Left Either
    @Test
    public void testMapForLeft() {
        Either<String, Integer> either = Either.left("left value");
        Function<? super String, ? extends String> lFunc = value -> "Mapped " + value;
        Function<? super Integer, ? extends String> rFunc = integer -> "Right " + integer;

        String result = either.map(lFunc, rFunc);

        assertEquals("Mapped left value", result);
    }

    // Test case: mapping over a Right Either
    @Test
    public void testMapForRight() {
        Either<String, Integer> either = Either.right(42);
        Function<? super String, ? extends String> lFunc = value -> "Left " + value;
        Function<? super Integer, ? extends String> rFunc = integer -> "Mapped " + integer;

        String result = either.map(lFunc, rFunc);

        assertEquals("Mapped 42", result);
    }

    // Test case: mapping Left when it is null with a function returning a non-null result
    @Test(expected = NullPointerException.class)
    public void testMapNullLeft() {
        Either<String, Integer> either = Either.left(null);
    }

    // Test case: mapping Right when it is null with a function returning a non-null result
    @Test(expected = NullPointerException.class)
    public void testMapNullRight() {
        Either<String, Integer> either = Either.right(null);
    }

    // Test case: mapping when both sides of the Either are empty (edge case)
    @Test
    public void testMapWithEmptyEither() {
        Either<String, Integer> either = new Either<>(Optional.empty(), Optional.empty());
        Function<? super String, ? extends String> lFunc = value -> "Mapped " + value;
        Function<? super Integer, ? extends String> rFunc = integer -> "Mapped " + integer;

        String result = either.map(lFunc, rFunc);

        assertNull(result);
    }
}