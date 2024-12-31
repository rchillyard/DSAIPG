/*
 * Copyright (c) 2024. Robin Hillyard
 */

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
     *
     * @return an integer representing the classification result of the element
     */
    int classify();
}