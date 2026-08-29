package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag;
import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import com.phasmidsoftware.dsaipg.sort.generic.ProcessingSort;
import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.sort.helper.GenericSortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups elements by class, and emits the classes in ascending order of class.
 * <p>
 * Each element is put in the bag for its class, and the bags are emptied back into
 * the array in ascending order of the value returned by {@code classify()}. Nothing
 * is compared except the class values, and those only once each.
 * <p>
 * Note what this does NOT do: it does not order the elements <i>within</i> a class.
 * A {@link com.phasmidsoftware.dsaipg.adt.bqs.Bag} iterates in a deliberately
 * arbitrary order, so all that holds afterwards is that the classes appear in
 * order. Ordering within a class is a following pass's job -- which is how
 * {@link BucketSort} uses the same idea, running an insertion sort over the whole
 * array once the buckets have been unloaded. That pass is cheap precisely because
 * the classes are already in order: over 2,000 elements in 8 classes, emitting the
 * classes out of order left 806,848 inversions for it against 124,863.
 * <p>
 * So this is a sort in the sense that it establishes the class ordering, not in the
 * sense that its output is sorted.
 *
 * @param <X> the underlying type which must extend Classify.
 */
public class ClassicSort<X extends Classify<X>> extends GenericSortWithHelper<X> implements ProcessingSort<X> {

    public static final String DESCRIPTION = "Classic sort";

    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Groups the specified portion of the array by class, writing the groups back in
     * ascending order of class. The elements within a class are in no particular
     * order; see the class comment.
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
        // NOTE the classes must be put in order explicitly. The keySet of a HashMap
        // comes out in bucket order, which is ascending only while every class is
        // smaller than the table -- true of dense classes but not of sparse ones,
        // and classify() may return any int. Classes {100, 5, 20} come out of the
        // keySet as 100, 20, 5, because 100 and 20 collide in bucket 4.
        // This is not cosmetic: the point of classifying first is that a following
        // insertion sort has little left to do, and that only holds if the classes
        // are in order. Measured over 2,000 elements in 8 sparse classes, the wrong
        // order leaves 806,848 inversions for the second pass against 124,863.
        // The ordering is done once over the distinct classes, not once per element:
        // k log k comparisons of ints, where k is at most the number of elements and
        // usually far smaller. A sorted map would instead compare on every insertion,
        // which is the cost a classification sort exists to avoid.
        List<Integer> classes = new ArrayList<>(map.keySet());
        Collections.sort(classes);
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

    /**
     * Returns a string representation of this ClassicSort instance.
     * The string is generated using the associated Helper's {@code toString()} method,
     * providing a detailed description of the state of the Helper.
     *
     * @return a string representation of the ClassicSort object
     */
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
