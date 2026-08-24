package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Comparator;

/**
 * Abstract class GenericSortWithHelper which extends Sort.
 *
 * @param <X> the underlying type which does not have to be Comparable.
 */
public abstract class GenericSortWithHelper<X> implements Sort<X> {

    /**
     * Get the Helper associated with this Sort.
     *
     * @return the Helper
     */
    public Helper<X> getHelper() {
        return helper;
    }

    /**
     * Closes the resources associated with this instance, if applicable.
     * If the `closeHelper` flag is set to true, the associated helper is closed
     * to release any resources it might be holding.
     */
    public void close() {
        if (closeHelper) helper.close();
    }

    /**
     * Returns a string representation of the helper associated with this class.
     *
     * @return a string representation of the helper object
     */
    @Override
    public String toString() {
        return helper.toString();
    }

    /**
     * Constructor for GenericSortWithHelper.
     *
     * @param helper the Helper instance used by the sorting algorithm.
     */
    public GenericSortWithHelper(Helper<X> helper) {
        this.helper = helper;
    }

    /**
     * Constructs a GenericSortWithHelper instance using a description, comparator, size,
     * number of runs, and configuration. Creates a Helper with the specified parameters
     * and initializes the GenericSortWithHelper.
     *
     * @param description a description of the sort method, used for identification purposes
     * @param comparator  the comparator to define the order of the elements being sorted
     * @param N           the size of the dataset to be sorted
     * @param nRuns       the number of times the sorting process will be executed
     * @param config      the configuration object to set up the Helper and related behavior
     */
    public GenericSortWithHelper(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        this(HelperFactory.createGeneric(description, comparator, N, nRuns, config));
        closeHelper = true;
    }

    private final Helper<X> helper;  // CONSIDER making this protected
    protected boolean closeHelper = false;

}
