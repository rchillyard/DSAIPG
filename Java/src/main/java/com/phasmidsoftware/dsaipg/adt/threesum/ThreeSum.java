package com.phasmidsoftware.dsaipg.adt.threesum;

/**
 * The ThreeSum interface provides a method to retrieve an array of unique triples
 * from a given data set of integers, where the sum of the three integers in each triple is zero.
 * Implementations of this interface may vary in terms of algorithmic approach and complexity.
 * Requirements:
 * - The input array should be sorted and contain distinct elements for optimal performance.
 * - If the implementation can handle unsorted or non-distinct arrays, this will depend on the specific implementation details.
 * <p>
 * NOTE these three implementations are also the clearest place in the repository to see
 * that a growth rate belongs to the algorithm and not to the language. Timing each of them
 * at a series of sizes recovers the exponents 3, 2 and 2 in Java and in Python alike, even
 * though Python takes some forty times longer to get there. Measured numbers, and the point
 * at which Python's quadratic overtakes Java's cubic — around n = 500 — are in
 * {@code docs/Java vs Python.md}.
 */
public interface ThreeSum {
    /**
     * Method to get the triples from this instance of ThreeSum.
     * <p>
     * NOTE: it is essential that the array to be operated on is ordered and distinct.
     * The cubic implementation of ThreeSum doesn't care about order of distinction, however.
     *
     * @return an ordered, distinct, array of Triple.
     */
    Triple[] getTriples();
}
