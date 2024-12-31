/*
 * Copyright (c) 2017-2024. Robin Hillyard
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equable equable = (Equable) o;
        Iterator<?> thisIterator = elements.iterator();
        Iterator<?> thatIterator = equable.elements.iterator();
        while (thisIterator.hasNext())
            if (thatIterator.hasNext()) {
                if (!thisIterator.next().equals(thatIterator.next())) return false;
            } else
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Object element : elements) result = 31 * result + element.hashCode();
        return result;
    }

    protected final Collection<?> elements;

}