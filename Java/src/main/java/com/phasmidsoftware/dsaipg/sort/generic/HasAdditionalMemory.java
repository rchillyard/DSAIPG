package com.phasmidsoftware.dsaipg.sort.generic;

/**
 * Interface representing memory management functionalities for sorting algorithms.
 * This interface allows setting and managing memory usage during sorting operations.
 */
public interface HasAdditionalMemory {
    /**
     * Sets the additional memory for array operations.
     * This method establishes the number of elements for which additional memory will be allocated.
     *
     * @param n the number of elements for which additional memory is to be allocated.
     */
    void setArrayMemory(int n);

    /**
     * Allocates or adjusts additional memory for sorting operations based on the specified parameter.
     *
     * @param n the amount of additional memory required, typically expressed as a factor or multiplier.
     *          The exact meaning of this parameter depends on the implementation. For example, it might
     *          represent the number of elements for which additional memory should be allocated.
     */
    void additionalMemory(int n);

    /**
     * Retrieves the memory factor associated with the implementation.
     * The memory factor is a measure of additional memory usage relative
     * to the array being processed during sorting operations.
     *
     * @return a Double representing the memory factor, or null if not set.
     */
    Double getMemoryFactor();
}
