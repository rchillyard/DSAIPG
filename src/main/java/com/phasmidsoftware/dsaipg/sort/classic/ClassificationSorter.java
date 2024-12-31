/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.Classifier;
import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An abstract class that combines sorting and classification functionality.
 * This class is designed to sort objects of type X while leveraging classification logic
 * to aid in the sorting process. It uses a helper and a classifier function
 * to perform its tasks.
 *
 * @param <X> the type of elements to be sorted
 * @param <Y> the type of the classification criteria
 */
public abstract class ClassificationSorter<X, Y> extends SortWithHelper<X> implements Classifier<X, Y> {

    /**
     * Constructs a ClassificationSorter with the specified helper and classifier function.
     *
     * @param helper the helper instance used for sorting operations
     * @param classifier a BiFunction that determines the classification logic,
     *                   taking an element of type X and a classification criteria of type Y
     *                   to produce an Integer result
     */
    public ClassificationSorter(Helper<X> helper, BiFunction<X, Y, Integer> classifier) {
        super(helper);
        this.classifier = classifier;
    }

    /**
     * Classifies an object of type X using classification criteria of type Y.
     * The method increases the lookup count in the helper and then applies
     * the classification logic using the provided classifier function.
     *
     * @param x the object to classify
     * @param y the classification criteria
     * @return the classification result as an integer
     * @throws SortException if the classifier function is not set
     */
    @Override
    public int classify(X x, Y y) {
        helper.incrementLookups();
        if (classifier != null)
            return classifier.apply(x, y);
        throw new SortException("Classifier is not set");
    }

    /**
     * Classifies an element from an array of type X using a classification criterion of type Y.
     * This method retrieves the element at the specified index from the array,
     * then delegates to the classification logic.
     *
     * @param xs the array of elements of type X to classify
     * @param i  the index of the element in the array to be classified
     * @param y  the classification criteria of type Y
     * @return the classification result as an integer, determined by the classifier logic
     */
    @Override
    public int classify(X[] xs, int i, Y y) {
        return classify(helper.get(xs, i), y);
    }

    /**
     * Retrieves the current classifier function used for classification.
     * The classifier is a BiFunction that takes an element of type X
     * and classification criteria of type Y as inputs and produces
     * an Integer result representing the classification.
     *
     * @return the classifier function as a BiFunction<X, Y, Integer>
     */
    public BiFunction<X, Y, Integer> getClassifier() {
        return classifier;
    }

    /**
     * Sets the classifier function to be used for classifying objects.
     * The classifier is a BiFunction that takes an object of type X and classification criteria of type Y
     * as inputs and produces an Integer result representing the classification.
     *
     * @param classifier a BiFunction<X, Y, Integer> that determines the classification logic
     */
    public void setClassifier(BiFunction<X, Y, Integer> classifier) {
        this.classifier = classifier;
    }

    /**
     * Converts a Function into a BiFunction where the second parameter is of type Void.
     * The returned BiFunction applies the given Function to the first parameter and ignores the second parameter.
     *
     * @param <T> the type of the input to the original Function and the first parameter of the resulting BiFunction
     * @param <V> the type of the result produced by the original Function and the resulting BiFunction
     * @param f   the Function to be converted into a BiFunction
     * @return a BiFunction that applies the given Function to its first parameter and ignores its second parameter,
     * or null if the input Function is null
     */
    public static <T, V> BiFunction<T, Void, V> convertToBiFunction(Function<T, V> f) {
        if (f == null) return null;
        return (T t, Void u) -> f.apply(t);
    }

    protected BiFunction<X, Y, Integer> classifier;
}