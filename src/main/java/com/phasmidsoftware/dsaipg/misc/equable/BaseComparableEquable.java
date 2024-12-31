/*
 * Copyright (c) 2017-2024. Robin Hillyard
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

    protected int compareTo(BaseEquable o) {
        return ((ComparableEquable) getEquable()).compareTo((ComparableEquable) o.getEquable());
    }
}