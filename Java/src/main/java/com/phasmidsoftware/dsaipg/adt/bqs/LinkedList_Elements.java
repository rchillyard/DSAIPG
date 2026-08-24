/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Concrete class which implements LinkedList of Item as a sequence of Elements.
 * This implementation of LinkedList could theoretically allow items to be added or removed elsewhere than the head.
 * If we wanted to be strict and allow addition/removal at the head only, we would have to make the next field of
 * Element final.
 *
 * @param <Item> the underlying type of this list.
 */
public class LinkedList_Elements<Item> implements LinkedList<Item> {

    /**
     * Add the given element to the head of this list.
     *
     * @param item an item.
     */
    public void add(Item item) {
        Element<Item> tail = head;
        head = new Element<>(item, tail);
    }

    /**
     * Remove the element at the head of this list.
     *
     * @return the value of the element that was at the head of the list.
     * @throws BQSException the list is empty.
     */
    public Item remove() throws BQSException {
        if (head == null) throw new BQSException("collection is empty");
        Item result = head.item;
        head = head.next;
        return result;
    }

    /**
     * Method to get the element at the head of this list without any mutation.
     * Equivalent to add(remove()).
     *
     * @return the item at the head of the list.
     */
    public Item getHead() {
        return isEmpty() ? null : head.item;
    }

    /**
     * @return true if this list is empty.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Method to yield an iterator for this list.
     *
     * @return an Iterator.
     */
    @NotNull
    public Iterator<Item> iterator() {
        return asItemIterable().iterator();
    }

    /**
     * Method to render this List as a String.
     *
     * @return a representation of this list.
     */
    public String toString() {
        return asItemIterable().toString();
    }

    /**
     * Compares this LinkedList_Elements instance with the specified object for equality.
     * Two LinkedList_Elements instances are considered equal if they have the same head element.
     *
     * @param o the object to be compared for equality with this LinkedList_Elements instance.
     * @return true if the specified object is equal to this LinkedList_Elements instance; otherwise, false.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedList_Elements<?> that)) return false;
        return Objects.equals(head, that.head);
    }

    /**
     * Computes the hash code for this LinkedList_Elements instance.
     * The hash code computation is based on the head element of the list.
     *
     * @return an integer representing the hash code of this LinkedList_Elements instance.
     */
    public int hashCode() {
        return Objects.hash(head);
    }

    /**
     * Converts the elements of the linked list into an {@code Iterable} collection of {@code Item}.
     * Iterates through the linked list starting from the head and collects each item's value into a collection.
     *
     * @return an {@code Iterable} containing all the items in the linked list in their original order.
     */
    private Iterable<Item> asItemIterable() {
        Collection<Item> result = new ArrayList<>();
        Element<Item> x = head;
        while (x != null) {
            result.add(x.item);
            x = x.next;
        }
        return result;
    }

    /**
     * The head element of the linked list.
     * This is the first node in the linked list, which serves as the entry point to traverse the structure.
     * It holds a reference to an {@code Element} containing an {@code Item} and a reference to the next element in the list.
     * Initially, it is set to {@code null}, indicating that the list is empty.
     */
    private Element<Item> head = null;
}
