package com.phasmidsoftware.dsaipg.sort.generic;

import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperException;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.util.Comparator;

/**
 * Base class for Sort with a non-comparable Helper.
 *
 * @param <X> underlying type which extends Comparable.
 */
public abstract class SortWithHelper<X> implements ProcessingSort<X> {

    /**
     * Get the Helper associated with this Sort.
     * CONSIDER: now that we have made helper protected, let's replace getHelper() with helper in subclasses.
     *
     * @return the Helper
     */
    public Helper<X> getHelper() {
        return helper;
    }

    /**
     * Retrieves the description of the associated Helper.
     *
     * @return a string representing the description of the Helper.
     */
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
     * Post-process the array, which for most Helpers means checking that it really
     * is sorted.
     * <p>
     * A {@link HelperException} means the Helper found something wrong with the
     * result — in practice, that the array is not in order — and is rethrown. That
     * is the whole point of the check: a sort which did not sort must not be
     * allowed to look like a success.
     * <p>
     * Anything else came from the post-processing logic itself rather than from the
     * sort, so it is logged and swallowed. Those are incidental and should not fail
     * a run that sorted correctly.
     * <p>
     * NOTE this used to catch {@code Exception} and log everything, which made the
     * check unreachable: every Helper's "array is not sorted" was reported at INFO
     * level while the test passed. Whether the check ran at all was moot.
     *
     * @param xs the array which has been sorted.
     * @throws HelperException if the Helper found the result unacceptable.
     */
    public void postProcess(X[] xs) {
        try {
            helper.postProcess(xs);
        } catch (HelperException e) {
            throw e;
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

    /**
     * Retrieves the logger associated with the SortWithHelper class.
     *
     * @return the LazyLogger instance used for logging within the SortWithHelper class.
     */
    public LazyLogger getLogger() {
        return SortWithHelper.logger;
    }

    /**
     * Returns the string representation of this object by delegating to the associated Helper's toString method.
     *
     * @return a string representation of this object, as provided by the Helper.
     */
    @Override
    public String toString() {
        return helper.toString();
    }

    /**
     * Closes the SortWithHelper instance and releases any associated resources.
     * <p>
     * This method ensures that the instance is closed only once by checking the "open" flag.
     * If the helper-associated resources need to be released, it delegates the close operation
     * to the helper's close method, provided the "closeHelper" flag is set to true.
     * <p>
     * It is important to call this method to free resources and maintain efficiency,
     * especially in cases where the SortWithHelper instance or its helper manages external resources.
     */
    public void close() {
        if (!open) return;
        open = false;
        if (closeHelper) helper.close();
    }

    /**
     * Constructor for SortWithHelper, which initializes the sort using the provided Helper.
     *
     * @param helper the Helper instance that provides utilities and operations
     *               necessary for the sorting process.
     */
    public SortWithHelper(Helper<X> helper) {
        this.helper = helper;
    }

    /**
     * Constructor for SortWithHelper which initializes a sorting helper using the provided parameters.
     *
     * @param description a string describing the sorting process or usage.
     * @param comparator  a comparator that determines the order of elements to be sorted.
     * @param N           the size of the dataset that the sort is intended to handle.
     * @param nRuns       the number of runs or executions the helper is expected to perform.
     * @param config      a configuration object containing settings or parameters for the sort/helper.
     */
    public SortWithHelper(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        this(HelperFactory.createGeneric(description, comparator, N, nRuns, config));
        closeHelper = true;
    }

    protected final Helper<X> helper;
    protected boolean closeHelper = false;
    private boolean open = true;

    final static LazyLogger logger = new LazyLogger(SortWithHelper.class);
}
