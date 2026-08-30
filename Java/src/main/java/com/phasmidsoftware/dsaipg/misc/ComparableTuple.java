/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.misc;

import com.phasmidsoftware.dsaipg.misc.equable.BaseComparableEquable;
import com.phasmidsoftware.dsaipg.misc.equable.BaseEquable;
import com.phasmidsoftware.dsaipg.misc.equable.ComparableEquable;
import com.phasmidsoftware.dsaipg.misc.equable.Equable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The ComparableTuple class represents a tuple consisting of an integer and a double value.
 * It extends the BaseComparableEquable class to implement both equability and comparability
 * behaviors as defined by the BaseEquable and BaseComparableEquable classes.
 * <p>
 * This class models an immutable pair of values (x, y) and provides a mechanism for comparing
 * and determining equality based on its internal elements. The comparison operation is
 * delegated to the logic implemented by its parent class through the use of
 * ComparableEquable.
 * <p>
 * The class overrides the toString method to provide a human-readable string representation
 * of the tuple's elements, and it implements the getEquable method to supply an Equable
 * instance representing the tuple's internal elements for the purposes of determining equality
 * and hash code values.
 */
public class ComparableTuple extends BaseComparableEquable implements Comparable<BaseEquable> {

    /**
     * Returns an Equable instance representing the tuple's internal elements.
     * This Equable is constructed using the elements of the tuple (x, y)
     * stored in a collection for the purposes of determining equality and hash codes.
     *
     * @return an instance of Equable that encapsulates the tuple's internal elements
     *         and allows element-wise equality and hash code computations.
     */
    public Equable getEquable() {
        Collection<Object> elements = new ArrayList<>();
        elements.add(x);
        elements.add(y);
        return new ComparableEquable(elements);
    }

    /**
     * Compares this object with the specified {@code BaseEquable} object for order.
     * This method delegates the comparison to the {@code compareTo} method of the superclass,
     * which utilizes the comparison logic defined in the parent class.
     *
     * @param o the {@code BaseEquable} object to be compared with this object.
     *          This object must provide a valid {@code Equable} instance for comparison.
     * @return a negative integer, zero, or a positive integer if this object is less than,
     *         equal to, or greater than the specified object, respectively.
     */
    public int compareTo(@NotNull BaseEquable o) {
        return super.compareTo(o);
    }

    @Override
    public String toString() {
        return "Tuple(" + x + ", " + y + ")";
    }

    /**
     * Constructs a new ComparableTuple instance with the specified integer and double values.
     *
     * @param x the integer value representing the first element of the tuple
     * @param y the double value representing the second element of the tuple
     */
    public ComparableTuple(int x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The integer component of the ComparableTuple class that represents the first element of the tuple.
     * This variable is immutable and serves as part of the key for equality and comparison operations
     * performed on instances of the ComparableTuple class.
     */
    private final int x;
    /**
     * The y variable represents the second element of the tuple in the ComparableTuple class.
     * It holds a double value that, together with the x variable, forms the immutable pair of values
     * constituting the tuple. The y value is used in determining equality, generating hash codes,
     * and forming the string representation of the tuple. It is final, ensuring its immutability
     * once initialized.
     */
    private final double y;
}
