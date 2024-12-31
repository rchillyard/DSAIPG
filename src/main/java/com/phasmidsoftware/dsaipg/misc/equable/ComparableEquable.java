/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.equable;

import java.util.Collection;
import java.util.Iterator;

/**
 * A ComparableEquable is a specialized implementation of the Equable class
 * that supports both equability and comparability. This class is designed
 * to determine the natural order of its instances by comparing the elements
 * they hold, provided the elements implement the Comparable interface.
 * <p>
 * ComparableEquable enforces a structured comparison by element, ensuring
 * that only elements that are instances of Comparable can be compared.
 * Additionally, all ComparableEquable instances compared must have the same
 * number of elements; otherwise, an exception is thrown.
 * <p>
 * This class relies on its parent class, Equable, for equality and hash code
 * logic. It defines the compareTo method to compare itself with another
 * ComparableEquable instance.
 */
public class ComparableEquable extends Equable implements Comparable<ComparableEquable> {

    /**
     * Constructor for the ComparableEquable class, which extends Equable and implements Comparable.
     * This constructor initializes a ComparableEquable instance with a collection of elements.
     *
     * @param elements the collection of elements to be stored in this instance.
     *                 These elements must implement the Comparable interface if they are to be compared.
     */
    public ComparableEquable(Collection<?> elements) {
        super(elements);
    }

    /**
     * Compares this ComparableEquable instance with the specified ComparableEquable object for order.
     * Comparison is performed element by element using their natural order, provided the elements in
     * question implement the Comparable interface. If elements differ in their natural ordering, the
     * method returns the result of their comparison. If all compared elements are equal, the method returns zero.
     *
     * @param o the ComparableEquable object to be compared with this instance. The elements contained
     *          within this object must implement the Comparable interface and have the same length as
     *          the elements in this instance.
     * @return a negative integer, zero, or a positive integer if this object is less than, equal to, or
     *         greater than the specified object, respectively.
     * @throws ComparableEquableException if the elements being compared are not Comparable or if the
     *         two ComparableEquable instances do not have the same number of elements.
     */
    public int compareTo(ComparableEquable o) {
        Iterator<?> thisIterator = elements.iterator();
        Iterator<?> thatIterator = o.elements.iterator();
        while (thisIterator.hasNext()) {
            if (thatIterator.hasNext()) {
                final Object next1 = thisIterator.next();
                final Object next2 = thatIterator.next();
                if (next1 instanceof Comparable<?>) {
                    //noinspection unchecked
                    final Comparable<Object> comparable1 = (Comparable<Object>) next1;
                    int cf = comparable1.compareTo(next2);
                    if (cf != 0)
                        return cf;
                } else
                    throw new ComparableEquableException("ComparableEquable can only compare elements which are themselves Comparable");
            } else
                throw new ComparableEquableException("ComparableEquable can only compare Equables of the same length");
        }
        return 0;
    }

    /**
     * Exception thrown to signal issues when comparing objects within a ComparableEquable instance.
     * This could occur due to one of the following conditions:
     * <p>
     * 1. The elements being compared are not instances of the Comparable interface.
     * 2. The two ComparableEquable instances being compared do not contain the same number of elements.
     * <p>
     * This exception is used within the ComparableEquable class during comparison operations where
     * the natural ordering of elements is required but cannot be determined due to incompatibilities
     * or inconsistencies in the elements or instance lengths.
     */
    public static class ComparableEquableException extends RuntimeException {
        public ComparableEquableException(String s) {
            super(s);
        }
    }
}