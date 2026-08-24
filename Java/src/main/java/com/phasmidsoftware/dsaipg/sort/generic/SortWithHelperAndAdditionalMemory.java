package com.phasmidsoftware.dsaipg.sort.generic;

import com.phasmidsoftware.dsaipg.sort.classic.ClassificationSorter;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.util.Comparator;
import java.util.function.BiFunction;

/**
 * Base class for Sort with a non-comparable Helper.
 *
 * @param <X> underlying type which extends Comparable.
 */
public abstract class SortWithHelperAndAdditionalMemory<X> extends ClassificationSorter<X, Integer> implements HasAdditionalMemory {

    /**
     * Sets the array memory capacity for the sorting process.
     * If the current array memory is uninitialized (-1), this method initializes it to the specified value
     * and adjusts the additional memory used accordingly.
     *
     * @param n the amount of memory to allocate for the array.
     */
    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    /**
     * Updates the additional memory allocation for sorting and adjusts the maximum memory used.
     *
     * @param n the amount of memory to increase, added to the current additional memory.
     */
    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    /**
     * Calculates the memory factor as the ratio of the maximum memory allowed to the currently set array memory.
     *
     * @return the memory factor as a Double. If the array memory has not been set (value is -1),
     * this method throws a {@link SortException}.
     * @throws SortException if the array memory has not been set prior to calling this method.
     */
    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }

    /**
     * Perform initializing step for this Sort.
     *
     * @param n the number of elements to be sorted.
     */
    public void init(int n) {
        setArrayMemory(n);
        super.init(n);
    }

    /**
     * Retrieves the logger instance associated with the SortWithHelperAndAdditionalMemory class.
     *
     * @return the LazyLogger instance used for logging.
     */
    public LazyLogger getLogger() {
        return SortWithHelperAndAdditionalMemory.logger;
    }

    /**
     * Closes resources associated with the sorting process and logs memory usage.
     * <p>
     * This method invokes {@code super.close()} to release resources at the base level.
     * It then calculates the memory factor by utilizing the {@code getMemoryFactor()} method,
     * which provides a ratio of maximum memory used to the allocated array memory.
     * The result is logged at the INFO level for analysis or debugging purposes.
     * <p>
     * Preconditions:
     * - Array memory must be set prior to invoking this method; otherwise,
     * a {@code SortException} will be thrown by {@code getMemoryFactor()}.
     * <p>
     * Postconditions:
     * - The superclass close behavior is executed.
     * - The memory factor is logged.
     */
    public void close() {
        super.close();
        double memoryFactor = getMemoryFactor();
        logger.info(this + ": memory factor: " + memoryFactor);
    }

    /**
     * Constructor for SortWithHelperAndAdditionalMemory, initializing with a helper and classifier function.
     *
     * @param helper     an instance of Helper<X> that provides utilities for sorting.
     * @param classifier a BiFunction that associates each element of type X with an Integer classification.
     */
    public SortWithHelperAndAdditionalMemory(Helper<X> helper, BiFunction<X, Integer, Integer> classifier) {
        super(helper, classifier);
    }

    /**
     * Constructor for SortWithHelperAndAdditionalMemory that initializes the sort with a specified description,
     * classifier function, comparator, number of elements, number of runs, and configuration settings.
     * This constructor relies on a helper created using the provided parameters.
     *
     * @param description A string describing the sorting algorithm or its configuration.
     * @param classifier  A BiFunction that maps an element of type X and an integer to an Integer classification value.
     * @param comparator  A comparator to define the order of elements during sorting.
     * @param N           The total number of elements to be sorted.
     * @param nRuns       The number of runs for testing or measurement purposes.
     * @param config      Configuration settings related to the sorting process.
     */
    public SortWithHelperAndAdditionalMemory(String description, BiFunction<X, Integer, Integer> classifier, Comparator<X> comparator, int N, int nRuns, Config config) {
        super(HelperFactory.createGeneric(description, comparator, N, nRuns, config), classifier);
    }

    protected boolean closeHelper = false;

    /**
     * A LazyLogger instance dedicated to logging activities within the {@code SortWithHelperAndAdditionalMemory} class.
     *
     * The `logger` is statically initialized and is utilized throughout the class to log important events such as
     * initialization, processing milestones, resource management, and memory usage details. This logger adheres
     * to the principles of lazy initialization, ensuring efficient management of resources used for logging.
     *
     * The associated class, {@code SortWithHelperAndAdditionalMemory}, involves sorting functionality combined with
     * helper utilities and memory management logic. This logger is commonly employed to capture debugging
     * information, performance metrics, or error messages relevant to the sorting component's lifecycle and behavior.
     */
    final static LazyLogger logger = new LazyLogger(SortWithHelperAndAdditionalMemory.class);

    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

}
