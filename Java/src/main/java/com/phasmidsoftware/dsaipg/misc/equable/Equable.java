/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.misc.equable;

import java.util.Collection;
import java.util.Iterator;

/**
 * The Equable class provides an implementation for equality and hash code based on a collection of elements.
 * Instances of this class are considered equal if they contain the same elements in the same order. Additionally,
 * the hash code is calculated based on the elements in the collection to ensure consistent behavior with equals.
 * <p>
 * This class is designed to be extended by other classes that require customizable equability behavior based on
 * collections of elements. It uses element-wise comparison to determine equality, ensuring that all elements
 * in both instances must match exactly.
 * <p>
 * The equality check ensures the following:
 * - The collections being compared must have the same number of elements.
 * - Each corresponding element in the collections must be equal.
 * <p>
 * The class leverages the `Collection` interface for storing and iterating over the elements. It is the caller's
 * responsibility to ensure that the elements in the collection provide appropriate implementations of hashCode
 * and equals for expected behavior of Equable instances.
 */
public class Equable {

    /**
     * Constructor for the Equable class, which initializes an instance with a specified collection of elements.
     * The elements in the collection are used as the basis for equality and hash code calculations.
     *
     * @param elements the collection of elements that will determine this Equable instance's equality
     *                 and hash code values. All elements in the collection are expected to provide
     *                 appropriate implementations of equals and hashCode for consistent behavior.
     */
    public Equable(Collection<?> elements) {
        this.elements = elements;
    }

    /**
     * Compares this object with the specified object for equality. This method checks if the given object is
     * an instance of the same class and compares the contained elements element-by-element to determine equality.
     * Two objects are considered equal if:
     * - They are of the same class.
     * - They contain the same number of elements.
     * - Each element in the collection matches the corresponding element in the other object's collection.
     *
     * @param o the object to compare with this instance for equality. It may be null.
     * @return true if the specified object is equal to this object; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equable equable = (Equable) o;
        // NOTE the length check. The loop walks THIS one's elements, so without it a
        // shorter Equable matching a prefix of a longer one ran out and returned
        // true, while the longer one compared with the shorter returned false.
        // equals was not symmetric, which the Object contract requires and which
        // every hash-based collection relies on.
        if (elements.size() != equable.elements.size()) return false;
        Iterator<?> thisIterator = elements.iterator();
        Iterator<?> thatIterator = equable.elements.iterator();
        while (thisIterator.hasNext())
            if (!thisIterator.next().equals(thatIterator.next())) return false;
        return true;
    }

    /**
     * Computes the hash code for this object based on the hash codes of its internal elements.
     * The method iterates through the collection of elements and combines their hash codes
     * using the formula: {@code result = 31 * result + element.hashCode()}.
     *
     * @return the computed hash code as an integer, ensuring that equal objects have the
     * same hash code and unequal objects are likely to have different hash codes.
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (Object element : elements) result = 31 * result + element.hashCode();
        return result;
    }

    protected final Collection<?> elements;

}
