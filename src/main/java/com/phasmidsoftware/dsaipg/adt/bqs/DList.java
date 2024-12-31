/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Implementation of a doubly-linked list
 *
 * @param <Item>
 */
public class DList<Item> implements Iterable<Item> {

    /**
     * Constructor which seeds the DList with one item
     *
     * @param item the Item to place into the new DList.
     */
    public DList(Item item) {
        super();
        addBeforeElement(item, null);
    }

    /**
     * Constructor which creates an empty DList
     */
    public DList() {
        super();
    }

    /**
     * Add an item immediately before the given element
     *
     * @param item the item to be added.
     * @param next may be null, in which case the item will be the only item on the list
     */
    public void addBefore(Item item, Item next) throws BQSException {
        if (next == null) addBeforeElement(item, null);
        else {
            D_Element first = findFirst(next);
            if (first != null)
                addBeforeElement(item, first);
            else
                throw new BQSException("item not found: " + next);
        }
    }

    /**
     * Add an item immediately before the given element
     *
     * @param item the item to be added.
     * @param prev may NOT be null
     */
    public void addAfter(Item item, Item prev) throws BQSException {
        {
            D_Element last = findLast(prev);
            if (last != null)
                addAfterElement(item, last);
            else
                throw new BQSException("item not found: " + prev);
        }
    }

    /**
     * Remove the first element matching item from this DList
     *
     * @param item the item to be removed.
     */
    public void remove(Item item) throws BQSException {
        D_Element last = findLast(item);
        if (last != null)
            remove(last);
        else
            throw new BQSException("item not found: " + item);
    }

    /**
     * Add an item immediately before the given element
     *
     * @param item the item to be added.
     * @param next may be null, in which case the item will be the only item on the list
     */
    public void addBeforeElement(Item item, D_Element next) {
        // TO BE IMPLEMENTED 
throw new RuntimeException("implementation missing");
    }

    /**
     * Add an item immediately before the given element
     *
     * @param item the item to be added.
     * @param prev may NOT be null
     */
    public void addAfterElement(Item item, D_Element prev) {
        // TO BE IMPLEMENTED 
throw new RuntimeException("implementation missing");
    }

    /**
     * Remove the element given from this DList
     *
     * @param element the element to be removed.
     */
    public void remove(D_Element element) {
        // TO BE IMPLEMENTED 
throw new RuntimeException("implementation missing");
    }

    public D_Element findFirst(Item item) {
        // TO BE IMPLEMENTED 
         return null;
        // END SOLUTION
    }

    public D_Element findLast(Item item) {
        // TO BE IMPLEMENTED 
         return null;
        // END SOLUTION
    }

    /**
     * Checks if the doubly linked list (DList) is empty.
     *
     * @return true if the list has no elements, false otherwise.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Returns the number of elements in the doubly linked list (DList).
     *
     * @return the count of elements in the list.
     */
    public int size() {
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item i : this) sb.append(i).append(", ");
        return sb.toString();
    }

    /**
     * Provides an iterator over the items in the doubly linked list (DList).
     * If the list is empty, it returns an empty iterator.
     *
     * @return an iterator over the items in the list. If the list is empty, returns an empty iterator.
     */
    @NotNull
    public Iterator<Item> iterator() {
        return head != null ? head.iterator() : Collections.emptyIterator();
    }

    /**
     * D_Element represents a node in a doubly-linked list. It contains an item, a reference to the previous element,
     * and a reference to the next element in the list.
     */
    public class D_Element implements Iterable<Item> {
        /**
         * Constructs a D_Element, which is a node in a doubly-linked list, initialized with the specified item,
         * a reference to the previous element, and a reference to the next element.
         *
         * @param x the item to be stored in this D_Element
         * @param p the reference to the previous D_Element
         * @param n the reference to the next D_Element
         */
        D_Element(Item x, D_Element p, D_Element n) {
            item = x;
            prev = p;
            next = n;
        }

        /**
         * Constructs a D_Element with the specified item and sets the previous and next elements to null.
         *
         * @param x the item to be stored in this D_Element
         */
        D_Element(Item x) {
            this(x, null, null);
        }

        /**
         * Returns an iterator over the items stored in this D_Element and subsequent elements in the linked list.
         * The iteration starts from the current D_Element and proceeds to the next elements in sequence until null is encountered.
         *
         * @return an iterator over the items in this doubly-linked list starting from this element
         */
        @NotNull
        public Iterator<Item> iterator() {
            Collection<Item> result = new ArrayList<>();
            D_Element cursor = this;
            while (cursor != null) {
                result.add(cursor.item);
                cursor = cursor.next;
            }
            return result.iterator();
        }

        final Item item;
        D_Element prev;
        D_Element next;
    }


    private D_Element head = null;
    private D_Element tail = null;
    private int count = 0;
}