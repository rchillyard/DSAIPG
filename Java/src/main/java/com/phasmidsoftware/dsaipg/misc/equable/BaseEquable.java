/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.misc.equable;

import java.util.Objects;

/**
 * BaseEquable is an abstract class that provides a foundation for defining objects with custom
 * equality and hash code behaviors based on their internal components. It relies on the concept
 * of an {@code Equable} instance to determine equality and hash code.
 * <p>
 * Classes extending this abstract class are expected to implement the {@code getEquable} method,
 * which supplies an {@code Equable} instance representing the elements of the object to be used
 * for equality and hash code calculations.
 * <p>
 * The {@code equals} method ensures that two instances of a subclass of {@code BaseEquable} are
 * equal if and only if their {@code Equable} instances are equal. Equality is determined based
 * on the underlying elements of the {@code Equable} instances.
 * <p>
 * The {@code hashCode} method computes the hash code by delegating to the {@code Equable} instance,
 * taking into consideration the elements of the {@code Equable}.
 */
public abstract class BaseEquable {

    /**
     * Provides an instance of {@code Equable} representing the elements of the subclass object.
     * The returned {@code Equable} is used for equality and hash code calculations.
     *
     * @return an {@code Equable} instance representing the elements of the object.
     */
    protected abstract Equable getEquable();

    @Override
    public int hashCode() {
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        return Objects.hash(getEquable().elements.toArray(new Object[getEquable().elements.size()]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEquable equable = (BaseEquable) o;
        return getEquable().equals(equable.getEquable());
    }

}
