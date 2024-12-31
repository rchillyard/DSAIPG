/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;

import java.util.Comparator;

/**
 * Base class for Sort with a non-comparable Helper.
 *
 * @param <X> underlying type which extends Comparable.
 */
public abstract class SortWithHelper<X> implements ProcessingSort<X> {

    public SortWithHelper(Helper<X> helper) {
        this.helper = helper;
    }

    public SortWithHelper(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        this(HelperFactory.createGeneric(description, comparator, N, nRuns, config));
        closeHelper = true;
    }

    /**
     * Get the Helper associated with this Sort.
     * CONSIDER: now that we have made helper protected, let's replace getHelper() with helper in subclasses.
     *
     * @return the Helper
     */
    public Helper<X> getHelper() {
        return helper;
    }

    @Override
    public String getDescription() {
        return helper.getDescription();
    }

    /**
     * Perform initializing step for this Sort.
     *
     * @param n the number of elements to be sorted.
     */
    public void init(int n) {
        helper.init(n);
    }

    /**
     * Perform pre-processing step for this Sort.
     *
     * @param xs the elements to be pre-processed.
     */
    public X[] preProcess(X[] xs) {
        return helper.preProcess(xs);
    }

    /**
     * Method to post-process an array after sorting.
     * <p>
     * In this implementation, we delegate the post-processing to the helper.
     *
     * @param xs the array to be post-processed.
     */
    public void postProcess(X[] xs) {
        try {
            helper.postProcess(xs);
        } catch (Exception e) {
            logger.info(getDescription() + ": postProcess: exception: " + e.getLocalizedMessage());
        }
    }

    /**
     * Return true if xs is sorted, i.e., has no inversions.
     *
     * @param xs an array of Xs.
     * @return true if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, false.
     */
    public boolean isSorted(X[] xs) {
        return helper.isSorted(xs);
    }

    public LazyLogger getLogger() {
        return SortWithHelper.logger;
    }

    @Override
    public String toString() {
        return helper.toString();
    }

    public void close() {
        if (!open) return;
        open = false;
        if (closeHelper) helper.close();
    }

    protected final Helper<X> helper;
    protected boolean closeHelper = false;
    private boolean open = true;

    final static LazyLogger logger = new LazyLogger(SortWithHelper.class);

}