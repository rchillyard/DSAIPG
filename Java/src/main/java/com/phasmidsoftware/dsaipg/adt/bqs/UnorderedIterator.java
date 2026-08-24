package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.*;

/**
 * Class to implement an Iterator of T based on a Collection or Array of T.
 * The order of elements in the iterator is random.
 *
 * @param <T> the underlying type.
 */
public class UnorderedIterator<T> implements Iterator<T> {

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    public boolean hasNext() {
        return !list.isEmpty();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    public T next() {
        if (list.isEmpty()) throw new NoSuchElementException();
        int i = random.nextInt(list.size());
        try {
            return list.remove(i);
        } catch (UnsupportedOperationException e) {
            throw new RuntimeException("UnorderedIterator: cannot remove element " + i + " from list of type: " + list.getClass());
        }
    }

    /**
     * Creates a deterministic UnorderedIterator from the given collection and random source.
     * The iteration order is determined based on the provided random source.
     *
     * @param collection the collection over which to iterate, whose contents will not be modified by this method
     * @param random     the random source used to determine the iteration order
     * @param <X>        the type of elements in the collection and the iterator
     * @return a new UnorderedIterator with deterministic iteration order based on the provided random source
     */
    public static <X> UnorderedIterator<X> createDeterministic(Collection<X> collection, Random random) {
        return new UnorderedIterator<>(copyCollection(collection), random);
    }

    /**
     * Creates a deterministic UnorderedIterator from the given collection and seed value.
     * The iteration order is determined based on the random source initialized with the provided seed.
     *
     * @param collection the collection over which to iterate, whose contents will not be modified by this method
     * @param seed       the seed value to initialize the random source for determining the iteration order
     * @param <X>        the type of elements in the collection and the iterator
     * @return a new UnorderedIterator with deterministic iteration order based on the given seed
     */
    public static <X> UnorderedIterator<X> createDeterministic(Collection<X> collection, long seed) {
        return createDeterministic(collection, new Random(seed));
    }

    /**
     * Secondary constructor which takes a collection.
     *
     * @param collection the collection of T over which to iterate.
     * @param random     an explicit random source.
     */
    public UnorderedIterator(Collection<T> collection, Random random) {
        this(copyCollection(collection), random);
    }

    /**
     * Secondary constructor which takes a collection.
     *
     * @param collection the collection of T over which to iterate.
     */
    public UnorderedIterator(Collection<T> collection) {
        this(collection, new Random());
    }

    /**
     * Secondary constructor which takes an array.
     *
     * @param array  an array of T.
     * @param random an explicit random source.
     */
    public UnorderedIterator(T[] array, Random random) {
        this(copyCollection(Arrays.asList(array)), random);
    }

    /**
     * Secondary constructor which takes an array.
     *
     * @param array an array of T.
     */
    public UnorderedIterator(T[] array) {
        this(array, new Random());
    }

    /**
     * Primary (private) constructor.
     *
     * @param list   a mutable list of T which will be mutated, ending as empty.
     * @param random a Random source.
     */
    private UnorderedIterator(List<T> list, Random random) {
        this.list = list;
        this.random = random;
    }

    /**
     * Creates a copy of the provided collection.
     * If the input collection is an instance of List,
     * a copy is ade with the same size and elements as the input.
     * Otherwise, a new List containing the elements of the collection is returned.
     *
     * @param <X>        the type of elements in the collection
     * @param collection the collection to be copied
     * @return a new List containing the elements of the specified collection
     */
    private static <X> List<X> copyCollection(Collection<X> collection) {
        if (collection instanceof ArrayList) {
            List<X> result = new ArrayList<>(collection);
            Collections.copy(result, (List<X>) collection);
            return result;
        } else return new ArrayList<>(collection);
    }

    /**
     * A mutable list of elements of type T used internally to manage the iteration order.
     * This list represents the current state of elements available in the iterator.
     * Elements are removed from this list as they are iterated over.
     * The list is intended to be modified dynamically during the iteration process.
     */
    private final List<T> list;

    /**
     * An instance of Random used to determine the order of iteration in this UnorderedIterator.
     * The Random instance ensures that the iteration order is either explicitly controlled
     * (when provided as a parameter to a constructor) or consistent with a default seed if not specified.
     * <p>
     * This field is immutable and is intended to provide randomization functionality
     * across various constructors and methods where deterministic or non-deterministic iteration
     * is required based on a Random source.
     */
    private final Random random;
}
