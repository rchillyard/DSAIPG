/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.misc.equable;

/**
 * BaseComparableEquable is an abstract class extending BaseEquable to provide
 * a mechanism for comparing objects in addition to their equability.
 * It serves as a foundation for creating classes where objects are both equable
 * and comparable, and relies on the {@link ComparableEquable} class for comparison logic.
 * <p>
 * The class implements a protected compareTo method which delegates the comparison logic
 * to the compareTo method of a ComparableEquable instance, ensuring that the equable objects
 * being compared are compatible with the Comparable interface.
 * <p>
 * Subclasses of BaseComparableEquable are expected to implement the getEquable method
 * that supplies an Equable such as the ComparableEquable instance with which objects can
 * be compared.
 */
public abstract class BaseComparableEquable extends BaseEquable {

    /**
     * Compares this object to the specified {@code BaseEquable} object for order.
     * The comparison is delegated to the {@code compareTo} method of the underlying
     * {@code ComparableEquable} instances obtained from the {@code getEquable} method.
     *
     * @param o the {@code BaseEquable} object to be compared with this object.
     *          The object must provide a {@code ComparableEquable} instance via its {@code getEquable} method.
     * @return a negative integer, zero, or a positive integer if this object is less than,
     * equal to, or greater than the specified object, respectively.
     */
    protected int compareTo(BaseEquable o) {
        return ((ComparableEquable) getEquable()).compareTo((ComparableEquable) o.getEquable());
    }
}
