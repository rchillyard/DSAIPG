package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparatorHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Comparator;
import java.util.Random;

/**
 * A Helper for the classification sorts, which compares with an external
 * Comparator and gathers no statistics.
 * <p>
 * NOTE this used to implement {@code NonComparableHelper} directly, duplicating
 * 355 lines of {@link com.phasmidsoftware.dsaipg.sort.helper.BaseHelper} and
 * {@link NonInstrumentingComparatorHelper} rather than extending them. Sharing no
 * code with the hierarchy, it drifted from it, and every fix to BaseHelper had to
 * be applied here a second time — {@code MSDCutoff()} was the last one. Two
 * differences had accumulated and gone unnoticed while this class's tests were
 * empty bodies:
 * <ul>
 *     <li>no {@code cutoff()} override, so it ignored the configured
 *     {@code [helper] cutoff} and always returned the default of 20;</li>
 *     <li>{@code init(n)} had no guard, so {@code init(10)} followed by
 *     {@code init(3)} silently succeeded where every other Helper throws.</li>
 * </ul>
 * Both are now inherited and therefore correct, which is what this class always
 * meant to do.
 * <p>
 * What remains is the one thing that was ever specific to it: an
 * {@code InstrumenterDummy}, so nothing is counted. The superclass constructor
 * supplies that too, so this class is now the constructor and nothing else.
 *
 * @param <X> the type of elements managed by this helper.
 */
public class ClassicHelper<X> extends NonInstrumentingComparatorHelper<X> {

    /**
     * Construct a ClassicHelper.
     *
     * @param description the description of this Helper (for humans).
     * @param comparator  the comparator to use when comparing elements.
     * @param n           the number of elements expected to be sorted.
     * @param random      the source of random elements.
     * @param config      the configuration.
     */
    public ClassicHelper(String description, Comparator<X> comparator, int n, Random random, Config config) {
        super(description, comparator, n, random, config);
    }
}
