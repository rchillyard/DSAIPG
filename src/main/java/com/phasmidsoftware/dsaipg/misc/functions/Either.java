/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.functions;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A generic container that represents one of two possible types of values: a left value (L) or a right value (R).
 * This class can be used to model computations or data that may yield a result in two distinct forms, such as success and failure.
 *
 * @param <L> the type of the left value
 * @param <R> the type of the right value
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class Either<L, R> {
    /**
     * Creates an `Either` representing a left value.
     *
     * @param <L>   the type of the left value
     * @param <R>   the type of the right value
     * @param value the value to be wrapped as the left value
     * @return an `Either` containing the given left value
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(Optional.of(value), Optional.empty());
    }

    /**
     * Creates an `Either` representing a right value.
     *
     * @param <L> the type of the left value
     * @param <R> the type of the right value
     * @param value the value to be wrapped as the right value
     * @return an `Either` containing the given right value
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(Optional.empty(), Optional.of(value));
    }

    /**
     * Determines whether this instance represents a right value.
     *
     * @return true if this instance contains a right value and no left value, false otherwise
     */
    public boolean isRight() {
        return right.isPresent() && left.isEmpty();
    }

    /**
     * Retrieves the right value contained in this Either instance, if it exists.
     * If this Either represents a left value, this method returns null.
     * <p>
     * CONSIDER throwing an Exception if {@code this} is a left value.
     *
     * @return the right value if present, otherwise null
     */
    public R getRight() {
        return right.orElse(null);
    }

    /**
     * Retrieves the left value contained in this Either instance, if it exists.
     * If this Either represents a right value, this method returns null.
     * <p>
     * CONSIDER throwing an Exception if {@code this} is a right value.
     *
     * @return the left value if present, otherwise null
     */
    public L getLeft() {
        return left.orElse(null);
    }

    /**
     * Package-private constructor for an `Either` instance with the specified left and right optional values.
     *
     * @param l the optional left value, representing a value of type `L`
     * @param r the optional right value, representing a value of type `R`
     */
    Either(Optional<L> l, Optional<R> r) {
        left = l;
        right = r;
    }

    /**
     * Applies the provided transformation functions to the value contained in the `Either` instance.
     * If the `Either` contains a left value, the `lFunc` function is applied to it.
     * If the `Either` contains a right value, the `rFunc` function is applied to it.
     *
     * @param lFunc the function to be applied if this `Either` contains a left value
     * @param rFunc the function to be applied if this `Either` contains a right value
     * @param <T>   the type of the resulting value
     * @return the result of applying the appropriate function (either `lFunc` or `rFunc`) to the value contained in the `Either`
     */
    public <T> T map(
            Function<? super L, ? extends T> lFunc,
            Function<? super R, ? extends T> rFunc) {
        return left.<T>map(lFunc).orElseGet(() -> right.map(rFunc).orElse(null));
    }

    /**
     * Applies the provided function to the left value of this `Either` if it exists.
     * If this `Either` instance contains a right value, it remains unchanged.
     *
     * @param <T>   the type of the new left value after applying the function
     * @param lFunc the function to apply to the left value if present
     * @return a new `Either` containing the transformed left value, or the original right value if no left value is present
     */
    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> lFunc) {
        return new Either<>(left.map(lFunc), right);
    }

    /**
     * Applies the provided function to the right value of this `Either` if it exists.
     * If this `Either` instance contains a left value, it remains unchanged.
     *
     * @param <T>   the type of the new right value after applying the function
     * @param rFunc the function to apply to the right value if present
     * @return a new `Either` containing the transformed right value, or the original left value if no right value is present
     */
    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rFunc) {
        return new Either<>(left, right.map(rFunc));
    }

    /**
     * Applies the provided consumers to the value contained in this `Either` instance.
     * If the `Either` contains a left value, the `lFunc` consumer is applied to it.
     * If the `Either` contains a right value, the `rFunc` consumer is applied to it.
     *
     * @param lFunc the consumer to be applied if this `Either` contains a left value
     * @param rFunc the consumer to be applied if this `Either` contains a right value
     */
    public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
        left.ifPresent(lFunc);
        right.ifPresent(rFunc);
    }

    private final Optional<L> left;
    private final Optional<R> right;

}