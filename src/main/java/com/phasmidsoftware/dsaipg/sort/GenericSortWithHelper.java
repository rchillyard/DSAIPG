/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Comparator;

/**
 * Abstract class GenericSortWithHelper which extends Sort.
 *
 * @param <X> the underlying type which does not have to be Comparable.
 */
public abstract class GenericSortWithHelper<X> implements Sort<X> {

    public GenericSortWithHelper(Helper<X> helper) {
        this.helper = helper;
    }

    public GenericSortWithHelper(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        this(HelperFactory.createGeneric(description, comparator, N, nRuns, config));
        closeHelper = true;
    }

    /**
     * Get the Helper associated with this Sort.
     *
     * @return the Helper
     */
    public Helper<X> getHelper() {
        return helper;
    }

    @Override
    public String toString() {
        return helper.toString();
    }

    public void close() {
        if (closeHelper) helper.close();
    }

    private final Helper<X> helper;  // CONSIDER making this protected
    protected boolean closeHelper = false;

}