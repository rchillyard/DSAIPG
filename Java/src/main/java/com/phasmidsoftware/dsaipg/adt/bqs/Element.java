/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.Objects;

/**
 * This class represents a node in a singly linked data structure.
 * It is generic and used to store elements of type {@code Item}.
 * Each node contains a reference to an item and a reference to the next node in the structure.
 *
 * @param <Item> The type of the item contained in this element.
 */
public class Element<Item> {
    /**
     * Constructs a new Element with the given item and reference to the next element.
     *
     * @param x the item to be stored in this element
     * @param n the next element in the linked structure, or null if this is the last element
     */
    Element(Item x, Element<Item> n) {
        item = x;
        next = n;
    }

    /**
     * Constructs a new Element with the given item and sets the next element to null.
     *
     * @param x the item to be stored in this element
     */
    Element(Item x) {
        this(x, null);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the object to compare with this element for equality
     * @return true if the specified object is equal to this element, otherwise false
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Element<?> element)) return false;
        return item.equals(element.item) && Objects.equals(next, element.next);
    }

    /**
     * Computes the hash code for this element using its item and next reference.
     * <p>
     * CONSIDER is that proper? Should we only base the hash on `item`?
     *
     * @return an integer hash code computed based on the item and next element
     */
    public int hashCode() {
        return Objects.hash(item, next);
    }

    public String toString() {
        return item + (next == null ? " (last)" : "");
    }

    /**
     * The item stored in this {@code Element}.
     * This is a generic type parameter {@code Item}, determined at the time
     * of instantiation of the containing {@code Element} class.
     * It represents the main content of this node in a singly linked data structure.
     * The value of this field is immutable, as it is declared {@code final}.
     */
    final Item item;

    /**
     * The reference to the next element in the linked data structure.
     * Points to another {@code Element} of type {@code Item} or {@code null}
     * if there are no subsequent elements.
     * NOTE that this field is mutable which means that we can update the list anywhere
     * (not just at the head).
     */
    Element<Item> next;
}
