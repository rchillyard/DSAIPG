package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.CUTOFF_DEFAULT;

/**
 * Interface to define all the helper methods (and which does not require the underlying type to be Comparable).
 * CONSIDER pulling the methods up from NonComparableHelper.
 *
 * @param <X> the underlying type.
 */
public interface Helper<X> extends AutoCloseable, Comparator<X>, Instrument {

    /**
     * Creates and returns a copy of the provided array.
     *
     * @param a the array to be copied
     * @return a new array containing the same elements as the input array
     */
    default X[] copyArray(X[] a) {
        return Arrays.copyOf(a, a.length);
    }

    /**
     * Implementation of comparison that does absolutely nothing else!!
     *
     * @param x1 the first X value.
     * @param x2 the second X value.
     * @return -1, 0, or 1 as appropriate.
     */
    int pureComparison(X x1, X x2);

    /**
     * @return true if this is an instrumented Helper.
     */
    default boolean instrumented() {
        return false;
    }

    /**
     * Retrieves a comparator for ordering elements of type X.
     *
     * @return a Comparator<X> instance that defines the ordering of elements of type X
     */
    Comparator<X> getComparator();

    /**
     * Method to do any required preProcessing.
     *
     * @param xs the array to be sorted.
     * @return the array after any pre-processing.
     */
    default X[] preProcess(X[] xs) {
        // CONSIDER invoking init from here.
        return xs;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swap(X[] xs, int i, int j) {
        X x = xs[j];
        xs[j] = xs[i];
        xs[i] = x;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     * It is expected, but not required, that i != j.
     *
     * @param v  the value of xs[i].
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swapV(X v, X[] xs, int i, int j) {
        xs[i] = xs[j];
        xs[j] = v;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j].
     * It is expected, but not required, that i != j.
     *
     * @param w  the value of xs[j].
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swapW(X w, X[] xs, int i, int j) {
        xs[j] = xs[i];
        xs[i] = w;
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param v  the value of xs[i].
     * @param w  the value of xs[j].
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    default void swapVW(X v, X w, X[] xs, int i, int j) {
//        System.out.println("Swapping " + v + " with " + w + " at indices " + i + " and " + j);
        xs[j] = v;
        xs[i] = w;
    }

    /**
     * Method to perform a stable swap, i.e., between xs[i] and xs[i-1]
     *
     * @param xs the array of X elements.
     * @param i  the index of the higher of the adjacent elements to be swapped.
     */
    default void swapStable(X[] xs, int i) {
        swap(xs, i - 1, i);
    }

    /**
     * Get the element at xs[i].
     *
     * @param xs the source array.
     * @param i  the target index.
     * @return the value of xs[i].
     */
    default X get(X[] xs, int i) {
        return xs[i];
    }

    /**
     * Set the element at xs[i].
     *
     * @param xs the destination array.
     * @param i  the target index.
     * @param x  the value to assign to xs[i].
     */
    default void set(X[] xs, int i, X x) {
        xs[i] = x;
    }

    /**
     * Copies the specified element from the source array to the target array starting at the specified positions.
     *
     * @param source the source array from which the element will be copied
     * @param i      the index of the element in the source array to be copied
     * @param target the target array where the element will be copied to
     * @param j      the index in the target array where the element will be placed
     */
    default void copy(X[] source, int i, X[] target, int j) {
        copy(source[i], target, j);
    }

    /**
     * Copies the given element into the specified position of the target array.
     *
     * @param x the element to be copied
     * @param target the target array where the element will be copied
     * @param j the index in the target array where the element will be placed
     */
    default void copy(X x, X[] target, int j) {
        target[j] = x;
    }

    /**
     * Copies a block of elements from the source array to the target array.
     *
     * @param source the source array from which elements are to be copied
     * @param i the starting position in the source array
     * @param target the target array into which elements are to be copied
     * @param j the starting position in the target array
     * @param n the number of elements to copy
     */
    default void copyBlock(X[] source, int i, X[] target, int j, int n) {
        System.arraycopy(source, i, target, j, n);
        // CONSIDER something like the following (which comes from binarySort within TimSort):
//        switch (n) {
//            case 2:  a[left + 2] = a[left + 1];
//            case 1:  a[left + 1] = a[left];
//                break;
//            default: System.arraycopy(a, left, a, left + 1, n);
//        }
//        a[left] = pivot;

    }

    /**
     * Distributes elements from the source array to the target array based on the provided function.
     *
     * @param source the array containing the elements to be distributed
     * @param from the starting index in the source array (inclusive)
     * @param to the ending index in the source array (exclusive)
     * @param target the array where the elements will be distributed
     * @param f a function that determines the position in the target array for each element from the source array
     */
    default void distributeBlock(X[] source, int from, int to, X[] target, Function<X, Integer> f) {
        for (int i = from; i < to; i++) {
            X value = source[i];
            target[f.apply(value)] = value;
        }
    }

    /**
     * Method to generate an ordered array of X elements.
     *
     * @param m     the number of elements required.
     * @param clazz the class represented by X.
     * @param f     a function which takes an Integer index and generates an ordered value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] ordered(int m, Class<X> clazz, Function<Integer, X> f) {
        @SuppressWarnings("unchecked") X[] result = (X[]) Array.newInstance(clazz, m);
        for (int i = 0; i < m; i++) result[i] = f.apply(i);
        return result;
    }

    /**
     * Method to generate a partially ordered array of X elements.
     *
     * @param m     the number of elements required.
     * @param clazz the class represented by X.
     * @param f     a function which takes an Integer index and generates an ordered value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] partialOrdered(int m, Class<X> clazz, Function<Integer, X> f) {
        @SuppressWarnings("unchecked") X[] result = (X[]) Array.newInstance(clazz, m);
        for (int i = 0; i < m; i++) result[i] = f.apply(i);
        for (int i = 1; i < m; i += 2) swapStable(result, i);
        return result;
    }

    /**
     * Method to generate an reverse-ordered array of X elements.
     *
     * @param m     the number of elements required.
     * @param clazz the class represented by X.
     * @param f     a function which takes an Integer index and generates an ordered value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] reverse(int m, Class<X> clazz, Function<Integer, X> f) {
        @SuppressWarnings("unchecked") X[] result = (X[]) Array.newInstance(clazz, m);
        for (int i = 0; i < m; i++) result[i] = f.apply(m - i - 1);
        return result;
    }

    /**
     * Method to generate an array of randomly chosen X elements.
     *
     * @param m     the number of random elements required.
     * @param clazz the class of X.
     * @param f     a function which takes a Random and generates a random value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    X[] random(int m, Class<X> clazz, Function<Random, X> f);

    /**
     * Method to generate an array of randomly chosen X elements.
     * The length of the returned array is dependent on the value of n used to initialize this Helper.
     *
     * @param clazz the class of X.
     * @param f     a function which takes a Random and generates a random value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] random(Class<X> clazz, Function<Random, X> f) {
        return random(getN(), clazz, f);
    }

    /**
     * Method to generate an array of two randomly chosen X elements.
     *
     * @param clazz the class of X.
     * @param f     a function which takes a Random and generates a random value of X.
     * @return an array of X of length determined by the current value according to setN.
     */
    default X[] randomPair(Class<X> clazz, Function<Random, X> f) {
        return random(2, clazz, f);
    }

    /**
     * @return the description of this Helper.
     */
    String getDescription();

    /**
     * Get the configuration associated with this Helper.
     *
     * @return an instance of Config.
     */
    Config getConfig();

    /**
     * Compare v with element j.
     *
     * @param xs the array.
     * @param j  the index of the second comparand.
     * @param v  the first comparand.
     * @return the result of comparing xs[i] to w.
     */
    default int compare(X[] xs, X v, int j) {
        return compare(v, xs[j]);
    }

    /**
     * Compare element i of xs with w.
     *
     * @param xs the array.
     * @param i  the index of the first comparand.
     * @param w  the other comparand.
     * @return the result of comparing xs[i] to w.
     */
    default int compare(X[] xs, int i, X w) {
        return compare(xs[i], w);
    }

    /**
     * Compare elements i and j of xs within the subarray lo...hi
     *
     * @param xs the array.
     * @param i  one of the indices.
     * @param j  the other index.
     * @return the result of comparing xs[i] to xs[j]
     */
    default int compare(X[] xs, int i, int j) {
        return compare(xs[i], xs[j]);
    }

    /**
     * Compare values v and w and return true if v is less than w, i.e., not inverted.
     * TODO remove the "notInverted" methods and replace by calls to "inverted."
     *
     * @param v the first value.
     * @param w the second value.
     * @return true if v is less than w.
     */
    default boolean notInverted(X v, X w) {
        return compare(v, w) < 0;
    }

    /**
     * Compare values xs[i] and w and return true if xs[i] is less than w, i.e., not inverted.
     * NOTE: only used by unit tests
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param w  the second value.
     * @return true if v is less than w.
     */
    default boolean notInverted(X[] xs, int i, X w) {
        return notInverted(xs[i], w);
    }

    /**
     * Compare values xs[i] and w and return true if xs[i] is less than w, i.e., not inverted.
     *
     * @param xs the array.
     * @param v  the first value.
     * @param j  the index of the second value.
     * @return true if v is less than w.
     */
    default boolean notInverted(X[] xs, X v, int j) {
        return notInverted(v, xs[j]);
    }

    /**
     * Compare values xs[i] and xs[j] and return true if xs[i] is less than xs[j], i.e., not inverted.
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param j  the index of the second value.
     * @return true if v is less than w.
     */
    default boolean notInverted(X[] xs, int i, int j) {
        return notInverted(xs, xs[i], j);
    }

    /**
     * Compare values xs[i] and xs[j] and return true if xs[i] is less than xs[j], i.e., not inverted.
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param j  the index of the second value.
     * @return true if v is less than w.
     */
    default boolean notInvertedWithLookups(X[] xs, int i, int j, int lookups) {
        return notInverted(xs, xs[i], j);
    }

    /**
     * Compare values xs[i] and xs[j] and return true if xs[i] is more than xs[j], i.e., they are inverted.
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param j  the index of the second value.
     * @return true if xs[i] is more than xs[j].
     */
    default boolean inverted(X[] xs, int i, int j) {
        return compare(xs, i, j) > 0;
    }

    /**
     * Compare values v and xs[j] and return true if v is more than xs[j], i.e., they are inverted.
     *
     * @param xs the array.
     * @param v  the first value.
     * @param j  the index of the second value.
     * @return true if v is more than xs[j].
     */
    default boolean inverted(X[] xs, X v, int j) {
        return compare(v, xs[j]) > 0;
    }

    /**
     * Compare values xs[i] and w and return true if xs[i] is more than w, i.e., they are inverted.
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param w  the second value
     * @return true if xs[i] is more than w.
     */
    default boolean inverted(X[] xs, int i, X w) {
        return compare(xs[i], w) > 0;
    }

    /**
     * Compare values v and w and return true if v is more than w, i.e., they are inverted.
     *
     * @param v the first value.
     * @param w the second value
     * @return true if v is more than w.
     */
    default boolean inverted(X v, X w) {
        return compare(v, w) > 0;
    }

    /**
     * Method to determine if a pair of adjacent elements of an array is in sequence.
     * Used by sorted method.
     * It is an attempt to optimize the process, although it's questionable if it really does.
     * NOTE no statistics are affected by this method--it is NOT an equivalent of inverted or compare.
     *
     * @param xs the array of X elements.
     * @param x  the left-hand element (should be smaller or equal).
     * @param i  the index of the right-hand element.
     * @return the right-hand element if x <= xs[i], otherwise return null;
     */
    default X inSequence(X[] xs, X x, int i) {
        X x1 = xs[i];
        if (pureComparison(x, x1) <= 0) return x1;
        else return null;
    }

    /**
     * Method to sort a pair of adjacent elements.
     * It is the caller's responsibility to ensure that to - from = 2
     *
     * @param xs   the array of X elements.
     * @param from the index of the first element.
     * @param to   one plus the index of the second element.
     */
    default boolean sortPair(X[] xs, int from, int to) {
        if (to == from + 2)
            return swapConditional(xs, from, to - 1);
        return false;
    }

    /**
     * Method to sort a trio of adjacent elements.
     * It is the caller's responsibility to ensure that to - from = 3
     *
     * NOTE I believe we can revert to the original here because this is the non-instrumenting case.
     * But first let's check that the other implementation is correct.
     *
     * @param xs   the array of X elements.
     * @param from the index of the first element.
     * @param to   one plus the index of the third element.
     */
    default void sortTrio(X[] xs, int from, int to) {
        if (to == from + 3) {
            int from_1 = from + 1;
            X xFrom = get(xs, from);
            X xFrom1 = get(xs, from_1);
            boolean swappedXY = swapConditional(xs, lookup(xFrom), from, from_1, lookup(xFrom1));
            if (swappedXY) {
                xFrom = xs[from];
                xFrom1 = xs[from_1];
            }
            int from_2 = from + 2;
            X xFrom2 = get(xs, from_2);
            boolean swappedYZ = swapConditional(xs, xFrom1, from_1, from_2, lookup(xFrom2));
            if (!swappedXY && !swappedYZ) return; // xyz
            if (swappedYZ)
                swapConditional(xs, xFrom, from, from_1, xs[from_1]);
            else
                swapConditional(xs, xFrom, from, from_2, xs[from_2]);
        }
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, int i, int j) {
        if (i == j) return false;
        return swapConditional(xs, xs[i], i, j);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @param w  the value of xs[j].
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, int i, int j, X w) {
        return swapConditional(xs, xs[i], i, j, w);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param v  the value of xs[i].
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, X v, int i, int j) {
        if (i == j) return false;
        return swapConditional(xs, v, i, j, xs[j]);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param v  the value of xs[i].
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @param w  the value of xs[j].
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapConditional(X[] xs, X v, int i, int j, X w) {
        if (i == j) return false;
        if (i > j) return swapConditional(xs, w, j, i, v);
        boolean exchange = compare(v, w) > 0;
        if (exchange) swapVW(v, w, xs, i, j);
        return exchange;
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[i-1], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    default boolean swapStableConditional(X[] xs, int i) {
        return swapConditional(xs, i - 1, i);
    }

    /**
     * Method to perform a stable swap using half-swaps,
     * i.e., between xs[i] and xs[j] such that xs[j] is moved to index i,
     * and xs[i] through xs[j-1] are all moved up one place.
     * This type of swap is used by InsertionSortOpt.
     *
     * @param xs the array of Xs.
     * @param i  the index of the destination of xs[j].
     * @param j  the index of the right-most element to be involved in the swap.
     * @param x  the value of xs[j].
     */
    default void swapInto(X[] xs, int i, int j, X x) {
        if (j > i) {
            copyBlock(xs, i, xs, i + 1, j - i);
            xs[i] = x;
        }
    }


    /**
     * Method to perform a stable swap using half-exchanges,
     * i.e., between xs[i] and xs[j] such that xs[j] is moved to index i,
     * and xs[i] through xs[j-1] are all moved up one place.
     * This type of swap is used by InsertionSortOpt.
     *
     * @param xs the array of Xs.
     * @param i  the index of the destination of xs[j].
     * @param j  the index of the right-most element to be involved in the swap.
     */
    default void swapInto(X[] xs, int i, int j) {
        swapInto(xs, i, j, get(xs, j));
    }

    /**
     * Method to perform a stable swap using half-exchanges, and binary search, i.e., x[i] is moved leftwards to its proper place, and all elements from the destination of x[i] through x[i-1] are moved up one place.
     * This type of swap is used by insertion sort.
     *
     * @param xs   the array of X elements, whose elements 0 through i-1 MUST be sorted.
     * @param from the first index of the sorted partition into which we want to insert the element at index i.
     * @param i    the index of the element to be swapped into the ordered array xs[0...i-1].
     */
    default void swapIntoSorted(X[] xs, int from, int i) {
        X x = get(xs, i);
        int j = binarySearchUpperBound(xs, from, i, x);
        if (j < i) swapInto(xs, j, i, x);
    }

    /**
     * Find where x belongs in the sorted range xs[from..to), placing it AFTER any
     * elements equal to it.
     * <p>
     * That is what makes this sort stable: an element must not move past one that
     * compares equal to it. It also means no element is moved unnecessarily, so
     * the number of elements moved is exactly the number of inversions.
     * <p>
     * NOTE this is written in terms of compare and get, both of which the
     * instrumented Helper overrides, so there is one implementation rather than
     * one per Helper. binarySearch has two -- Arrays.binarySearch here and a copy
     * in InstrumentedComparatorHelper -- and duplication of exactly that kind is
     * what let several counting and comparison bugs survive in this package.
     *
     * @param xs   the array, which must be sorted over the given range.
     * @param from the index of the first element of the range.
     * @param to   one past the index of the last element of the range.
     * @param x    the value to place.
     * @return the index of the first element greater than x, or to if there is none.
     */
    default int binarySearchUpperBound(X[] xs, int from, int to, X x) {
        int low = from;
        int high = to;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (compare(get(xs, mid), x) <= 0) low = mid + 1;
            else high = mid;
        }
        return low;
    }

    /**
     * CONSIDER eliminate this method as it has been superseded by swapConditional. However, maybe the latter is a better name.
     * Method to fix a potentially unstable inversion.
     *
     * @param xs the array of X elements.
     * @param i  the index of the lower of the two elements to be swapped.
     * @param j  the index of the higher of the two elements to be swapped.
     */
    default void fixInversion(X[] xs, int i, int j) {
        swapConditional(xs, i, j);
    }

    /**
     * CONSIDER eliminate this method as it has been superseded by swapStableConditional. However, maybe the latter is a better name.
     * Method to fix a stable inversion.
     *
     * @param xs the array of X elements.
     * @param i  the index of the higher of the two adjacent elements to be swapped.
     */
    default void fixInversion(X[] xs, int i) {
        swapStableConditional(xs, i);
    }

    /**
     * Return index of first inversion in xs.
     *
     * @param xs an array of Xs.
     * @return -1 if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, it returns the index of the offending element.
     */
    default int findInversion(X[] xs, int from, int to) {
        X x = xs[from];
        for (int i = from + 1; i < to; i++) {
            x = inSequence(xs, x, i);
            if (x == null) return i;
        }
        return -1;
    }

    /**
     * Return index of first inversion in xs.
     *
     * @param xs an array of Xs.
     * @return -1 if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, it returns the index of the offending element.
     */
    default int findInversion(X[] xs) {
        return findInversion(xs, 0, xs.length);
    }

    /**
     * Return true if xs is sorted, i.e., has no inversions.
     *
     * @param xs an array of Xs.
     * @return true if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, false.
     */
    default boolean isSorted(X[] xs) {
        if (xs.length < 2) return true;
        return findInversion(xs) == -1;
    }

    /**
     * Return true if xs is sorted, i.e., has no inversions.
     *
     * @param xs an array of Xs.
     * @return true if each successive element is greater than (or equal to) its predecessor.
     * Otherwise, false.
     */
    default boolean isSorted(X[] xs, int from, int to) {
        return findInversion(xs, from, to) == -1;
    }

    /**
     * Performs any necessary post-processing on the provided array of elements.
     * This implementation does nothing by default and can be overridden as needed.
     *
     * @param xs an array of elements of type X to be post-processed
     */
    default void postProcess(X[] xs) {
        // XXX do nothing
    }

    /**
     * Retrieves the default cutoff value.
     *
     * @return the default cutoff value as an integer.
     */
    default int cutoff() {
        return CUTOFF_DEFAULT;
    }

    /**
     * Retrieves the default cutoff below which MSD (Most Significant Digit) radix
     * sort hands the remaining sub-array to quicksort.
     *
     * @return the default cutoff value as an integer
     */
    default int MSDCutoff() {
        return CUTOFF_DEFAULT;
    }

    /**
     * This method discriminates <code>x</code> according to the value <code>d</code>.
     * In the typical situation, where X is String, this method yields a substring of <code>x</code> starting at index <code>d</code>.
     *
     * @param x the X value to be discriminated (typically a String).
     * @param d the discriminator, assuming X = String, this is the index of the first significant character.
     * @return the substring, as an X.
     */
    @SuppressWarnings("unchecked")
    default X discriminate(X x, int d) {
        if (x instanceof String) {
            return (X) discriminateString((String) x, d);
        } else throw new SortException("subString not defined for " + x.getClass());
    }

    /**
     * This method discriminates <code>x</code> according to the value <code>d</code>.
     * This method yields a substring of <code>x</code> starting at index <code>d</code>.
     *
     * @param x the String to be discriminated.
     * @param d the discriminator, this is the index of the first significant character.
     * @return the substring.
     */
    static String discriminateString(String x, int d) {
        if (d < x.length()) return x.substring(d);
        else return " ";
    }

    /**
     * Compares substrings derived from two instances of type X based on a specific discriminator.
     *
     * @param x1 the first instance of type X
     * @param x2 the second instance of type X
     * @param d the discriminator used to extract substrings from the instances
     * @return a negative integer, zero, or a positive integer as the first substring
     *         is less than, equal to, or greater than the second substring, respectively
     */
    default int compareSubstrings(X x1, X x2, int d) {
        return compare(discriminate(x1, d), discriminate(x2, d));
    }

    /**
     * @param n the size to be managed.
     * @throws HelperException if n is inconsistent.
     */
    void init(int n);

    /**
     * Get the current value of N.
     *
     * @return the value of N.
     */
    int getN();

    /**
     * Close this Helper, freeing up any resources used.
     * NOTE this method does not throw Exception like its parent.
     * CONSIDER declaring a thrown exception.
     */
    void close();

    /**
     * Count the number of inversions of this array.
     *
     * @param xs an array of Xs.
     * @return the number of inversions.
     */
    default long inversions(X[] xs) {
        return 0;
    }

    /**
     * Registers the provided depth value for further processing or tracking.
     * The default implementation does nothing.
     *
     * @param depth the depth value to be registered. It should be a non-negative integer.
     */
    default void registerDepth(int depth) {
    }

    /**
     * Calculates the maximum depth of a structure or element that
     * this method is designed to measure.
     * The default implementation returns 0.
     *
     * @return the maximum depth as an integer
     */
    default int maxDepth() {
        return 0;
    }

    /**
     * Provides a displayable representation of the statistical information.
     * The default implementation returns an empty string.
     *
     * @return a string containing formatted statistical details.
     */
    default String showStats() {
        return "";
    }

    default public String showStats(String context) {
        return "";
    }

    /**
     * Creates and returns a new instance of Helper with the specified description and size.
     *
     * @param description       the description of the Helper instance to be cloned
     * @param N                 the size parameter for the Helper instance to be cloned
     * @param shareInstrumenter
     * @return a new Helper instance with the given description and size
     */
    Helper<X> clone(String description, int N, boolean shareInstrumenter);

    /**
     * Creates a new clone of the current Helper object with the specified description, comparator, and limit.
     *
     * @param description       a string representing the description for the new cloned Helper
     * @param comparator        a Comparator used to define the sorting or comparison logic for the clone
     * @param N                 an integer specifying the limit or size constraint for the clone
     * @param shareInstrumenter
     * @return a new Helper instance with the specified parameters applied
     */
    Helper<X> clone(String description, Comparator<X> comparator, int N, boolean shareInstrumenter);

    /**
     * Creates and returns a clone of the current object with the specified description.
     *
     * @param description       the description for the cloned object
     * @param shareInstrumenter
     * @return a new instance of Helper with the provided description
     */
    default Helper<X> clone(String description, boolean shareInstrumenter) {
        return clone(description, getN(), shareInstrumenter);
    }

    /**
     * Get the value from the heap represented by the reference `x`.
     * In reality, this is a no-op, other than incrementing the lookup count.
     *
     * @param x the input object to be looked up and returned
     * @return the same input object that was provided
     */
    default X lookup(X x) {
        incrementLookups(1);
        return x;
    }

    /**
     * Compares two elements in the given array at the specified indices, while also incrementing the lookup count.
     *
     * @param xs      the array containing the elements to be compared
     * @param i       the index of the first element to compare
     * @param j       the index of the second element to compare
     * @param lookups the number of lookups to increment; must be between 0 and 2 (inclusive)
     * @return a negative integer, zero, or a positive integer as the element at index {@code i} is less than,
     *         equal to, or greater than the element at index {@code j}
     */
    default int compareWithLookups(X[] xs, int i, int j, int lookups) {
        assert lookups >= 0 && lookups <= 2;
        incrementLookups(lookups);
        return compare(xs, i, j);
    }

}
