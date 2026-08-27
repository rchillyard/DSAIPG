package com.phasmidsoftware.dsaipg.sort.classic;

/**
 * Represents a classification mechanism for elements of type {@code X}.
 * Implementations of this interface define a specific way to classify objects
 * based on their internal properties or states.
 *
 * @param <X> the type of elements to classify
 */
public interface Classify<X> {
    /**
     * Classifies an element based on the internal properties or state.
     * <p>
     * The value returned does two things: it says which class the element belongs
     * to, and it says where that class comes in the order. Classes are visited in
     * ASCENDING order of this value, so a classification sort leaves the elements
     * ordered by class -- which is what allows a following pass, typically an
     * insertion sort, to have very little left to do.
     * <p>
     * Any int is permitted; the values need not be dense, nor start at zero, nor be
     * positive. This must be a pure function: two calls on an unchanged element must
     * give the same answer, or the classes will not group.
     * <p>
     * NOTE returning {@code hashCode()} satisfies the letter of this contract and is
     * never what is wanted. A hash says nothing about order, so the classes would be
     * visited in an arbitrary sequence and the following pass would have as much work
     * as if nothing had been classified at all.
     *
     * @return an integer giving both the class of the element and its position in the
     * order in which classes are visited
     */
    int classify();
}
