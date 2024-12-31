/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.sort.GenericSortWithHelper;
import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.ProcessingSort;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.util.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This sort method sorts elements according to their class, i.e. the sort key is the value of x.classify().
 *
 * @param <X> the underlying type which must extend Classify.
 */
public class ClassicSort<X extends Classify<X>> extends GenericSortWithHelper<X> implements ProcessingSort<X> {

    public static final String DESCRIPTION = "Classic sort";

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Sorts the specified portion of the array based on the classification of its elements.
     * Elements are grouped into classes defined by their classify() method, and sorted accordingly.
     *
     * @param xs the array of elements to be sorted
     * @param from the starting index (inclusive) of the portion of the array to be sorted
     * @param to the ending index (exclusive) of the portion of the array to be sorted
     * @throws SortException if a logic error occurs during the sorting process
     */
    public void sort(X[] xs, int from, int to) {
        Map<Integer, Bag<X>> map = new HashMap<>();
        for (int i = from; i < to; i++) {
            int classs = xs[i].classify();
            Bag<X> xBag = map.getOrDefault(classs, new Bag_Array<>());
            xBag.add(xs[i]);
            map.put(classs, xBag);
        }

        // Iterate over the bags in class order, copying each bag back to the original array.
        Set<Integer> classes = map.keySet();
        int i = from;
        for (int classs : classes) {
            if (i >= to) throw new SortException("ClassicSort: logic error: " + i + ", " + to);
            Bag<X> xBag = map.get(classs);
            // FIXME Apparently, we can't use asArray. So, we will use iterator instead.
//            X[] array = xBag.asArray();
//            System.arraycopy(array, 0, xs, i, array.length);
//            i += array.length;

            // XXX alternative code
            for (X x : xBag) xs[i++] = x;
        }
    }

    @Override
    public String toString() {
        return getHelper().toString();
    }

    /**
     * Perform initializing step for this Sort.
     *
     * @param n the number of elements to be sorted.
     */
    public void init(int n) {
        // NOTE this does nothing.
    }

    /**
     * Post-process the given array, i.e. after sorting has been completed.
     *
     * @param xs an array of Xs.
     */
    public void postProcess(X[] xs) {
        // XXX do nothing.
    }

    /**
     * Closes resources associated with this sort instance, if applicable.
     *
     * Specifically, if the instance was initialized with a Helper that requires cleanup,
     * this method ensures that the associated Helper's {@code close()} method is invoked.
     * This is used to free any resources allocated by the Helper during the lifecycle of this object.
     */
    public void close() {
        if (closeHelper) getHelper().close();
    }

    /**
     * Constructs a ClassicSort instance with the specified helper.
     * This constructor uses the provided helper for sorting operations and sets
     * the closeHelper flag to true, indicating that the helper will require cleanup.
     * NOTE not used currently.
     *
     * @param helper the helper instance used to assist with the sorting process
     */
    ClassicSort(Helper<X> helper) {
        super(helper);
        closeHelper = true;
    }

    /**
     * Constructs a ClassicSort instance using default settings.
     * </br>
     * This no-argument constructor initializes the sort instance with a null comparator,
     * default run size and configuration settings, and assigns the description from the class field.
     * Additionally, it ensures that resources associated with the helper will be closed
     * after the sort instance is no longer in use by setting the closeHelper flag to {@code true}.
     *
     * @throws IOException if there is an error during configuration loading.
     */
    ClassicSort() throws IOException {
        // NOTE: the comparator is null here.
        super(DESCRIPTION, null, 0, 1, Config.load(ClassicSort.class));
        closeHelper = true;
    }

    private final boolean closeHelper;

}