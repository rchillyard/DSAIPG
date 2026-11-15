package com.phasmidsoftware.dsaipg.adt.array;

import java.util.Arrays;

public final class SortedArray<T extends Comparable<? super T>> {
    private Object[] elements = new Object[8];
    private int size = 0;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return elementAt(index);
    }

    public void insert(T value) {
        requireNonNull(value);

        int index = lowerBound(value);
        ensureCapacity();

        System.arraycopy(
                elements, index,
                elements, index + 1,
                size - index
        );

        elements[index] = value;
        size++;
    }

    // O(log n)
    public boolean contains(T value) {
        requireNonNull(value);

        int index = lowerBound(value);
        return index < size
                && elementAt(index).compareTo(value) == 0;
    }

    // O(n)
    public boolean remove(T value) {
        requireNonNull(value);

        int index = lowerBound(value);

        if (index == size
                || elementAt(index).compareTo(value) != 0) {
            return false;
        }

        System.arraycopy(
                elements, index + 1,
                elements, index,
                size - index - 1
        );

        elements[--size] = null;
        return true;
    }

    public T min() {
        if (isEmpty()) {
            throw new IllegalStateException("Array is empty");
        }
        return elementAt(0);
    }

    // O(1)
    public T max() {
        if (isEmpty()) {
            throw new IllegalStateException("Array is empty");
        }
        return elementAt(size - 1);
    }


    private int lowerBound(T value) {
        int left = 0;
        int right = size;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (elementAt(mid).compareTo(value) < 0) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
    }

    private void requireNonNull(T value) {
        if (value == null) {
            throw new NullPointerException("Null values are not supported");
        }
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(elements, size));
    }

    public static void main(String[] args) {
        SortedArray<Integer> array = new SortedArray<>();

        array.insert(8);
        array.insert(2);
        array.insert(12);
        array.insert(5);
        array.insert(7);
        array.insert(5);

        System.out.println(array);
        System.out.println(array.contains(7));
        System.out.println(array.min());
        System.out.println(array.max());

        array.remove(5);
        System.out.println(array);

        System.out.println(array.get(2));
        System.out.println(array.size());
    }
}
