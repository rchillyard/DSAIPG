/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

/**
 * Interface to define the operations of a classifier.
 *
 * @param <X> the underlying type.
 */
public interface Classifier<X, Y> {

    /**
     * Classify the given X, Y values as an integer.
     *
     * @param x the value of X.
     * @param y an additional parameter of type Y. Usually, an int -- the length of the prefix to be ignored.
     * @return the class to which x belongs.
     */
    int classify(X x, Y y);

    /**
     * Classify the array element xs[i] as an integer.
     *
     * @param xs an array of Xs.
     * @param i  the index of the element to classify.
     * @param y  an additional parameter of type Y. Usually, an int -- the length of the prefix to be ignored.
     * @return the class to which xs[i] belongs.
     */
    int classify(X[] xs, int i, Y y);

}