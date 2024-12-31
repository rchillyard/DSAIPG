/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Class to demonstrate that it is possible to create a LazyList in Java.
 * NOTE: this does not represent a true lazy list in the sense of the Scala LazyList.
 *
 * @param <T> the underlying type of the lazy list.
 */
public class LazyList<T> {

    /**
     * Method to prepend the value <code>t</code> to the head of this LazyList.
     *
     * @param t a value of type T.
     * @return a new LazyList whose head is <code>t</code> and whose tail is <code>this</code>.
     */
    public LazyList<T> prepend(T t) {
        return new LazyList<>(t, () -> this);
    }

    /**
     * Method to take a number (<code>n</code>) of elements from this LazyList.
     * NOTE: the result is not a new LazyList (as it really should be) but a List.
     *
     * @param n the number of elements to take from this LazyList.
     * @return a List with at most <code>n</code> elements.
     */
    public List<T> take(int n) {
        List<T> result = new ArrayList<>();
        LazyList<T> cursor = this;
        while (n > 0 && cursor != null) {
            result.add(cursor.head);
            cursor = cursor.tailFunction.get();
            n--;
        }
        return result;
    }

    /**
     * Method to take elements from this LazyList as long as they satisfy the given <code>predicate</code>.
     * NOTE: the result is not a new LazyList (as it really should be) but a List.
     *
     * @param predicate the test for T values to be included in the result.
     * @return a List with only elements that satisfy <code>predicate</code>.
     */
    public List<T> takeWhile(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        LazyList<T> cursor = this;
        while (cursor != null && predicate.test(cursor.head)) {
            result.add(cursor.head);
            cursor = cursor.tailFunction.get();
        }
        return result;
    }

    /**
     * Constructor of a lazy list.
     *
     * @param head         the head.
     * @param tailFunction a function to evaluate the tail, if necessary.
     */
    public LazyList(T head, Supplier<LazyList<T>> tailFunction) {
        this.head = head;
        this.tailFunction = tailFunction;
    }

    public final T head;
    public final Supplier<LazyList<T>> tailFunction;

    /**
     * Method to map a LazyList given a function.
     *
     * @param list the LazyList to operate on.
     * @param f    the function to convert a T into a U.
     * @param <T>  the underlying input type.
     * @param <U>  the underlying output type.
     * @return a new LazyList.
     */
    public static <T, U> LazyList<U> map(LazyList<T> list, Function<T, U> f) {
        if (list == null) throw new NullPointerException("list is null");
        T head = list.head;
        if (head != null) return new LazyList<>(f.apply(head), () -> map(list.tailFunction.get(), f));
        else throw new NullPointerException("head is null");
    }

    /**
     * Method to create a LazyList given a starting value and a function.
     *
     * @param start the starting value.
     * @param next  the function to yield the next value.
     * @param <T>   the underlying type.
     * @return a new LazyList.
     */
    public static <T> LazyList<T> iterate(final T start, Function<T, T> next) {
        final Supplier<LazyList<T>> supplier = () -> iterate(next.apply(start), next);
        return new LazyList<>(start, supplier);
    }

    /**
     * Method to create a LazyList of Integers given a starting value and an increment.
     *
     * @param start the starting value.
     * @param step  the increment.
     * @return a new LazyList.
     */
    public static LazyList<Integer> from(final int start, int step) {
        return iterate(start, x -> x + step);
    }

    /**
     * Method to create a LazyList of Integers given a starting value.
     *
     * @param start the starting value.
     * @return a new LazyList.
     */
    public static LazyList<Integer> from(final int start) {
        return from(start, 1);
    }

    /**
     * Method to create a LazyList of Integers given a starting value.
     *
     * @param start the starting value.
     * @return a new LazyList.
     */
    public static LazyList<Integer> fibonacci(final int start) {
        return from(start, 1);
    }

}