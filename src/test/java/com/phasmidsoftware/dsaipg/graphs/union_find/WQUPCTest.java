/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.graphs.union_find;

import org.junit.Test;

import static org.junit.Assert.*;

public class WQUPCTest {

    /**
     *
     */
    @Test
    public void testFind0() {
        WQUPC h = new WQUPC(10);
        assertEquals(0, h.find(0));
    }

    /**
     *
     */
    @Test
    public void testFind1() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
    }

    /**
     *
     */
    @Test
    public void testFind2() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        h.union(2, 1);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        assertEquals(0, h.find(2));
    }

    /**
     *
     */
    @SuppressWarnings("Duplicates")
    @Test
    public void testFind3() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        h.union(0, 2);
        h.union(3, 4);
        h.union(3, 5);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        assertEquals(0, h.find(2));
        assertEquals(3, h.find(3));
        assertEquals(3, h.find(4));
        assertEquals(3, h.find(5));
        h.union(0, 3);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        assertEquals(0, h.find(2));
        assertEquals(0, h.find(3));
        assertEquals(0, h.find(4));
        assertEquals(0, h.find(5));
    }

    /**
     * CONSIDER this appears to be identical to testFind3. What's going on here?
     */
    @SuppressWarnings("Duplicates")
    @Test
    public void testFind4() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        h.union(0, 2);
        h.union(3, 4);
        h.union(3, 5);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        assertEquals(0, h.find(2));
        assertEquals(3, h.find(3));
        assertEquals(3, h.find(4));
        assertEquals(3, h.find(5));
        h.union(0, 3);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        assertEquals(0, h.find(2));
        assertEquals(0, h.find(3));
        assertEquals(0, h.find(4));
        assertEquals(0, h.find(5));
    }

    /**
     *
     */
    @Test
    public void testConnected01() {
        WQUPC h = new WQUPC(10);
        assertFalse(h.connected(0, 1));
    }

    /**
     * Test the union method by connecting two elements and verifying their connectivity.
     */
    @Test
    public void testUnion() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        assertEquals(0, h.find(0));
        assertEquals(0, h.find(1));
        assertTrue(h.connected(0, 1));
    }

    /**
     * Test the union method with multiple unions and size-based balancing.
     */
    @Test
    public void testUnionWithMultipleUnions() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        h.union(2, 3);
        h.union(0, 2);
        assertEquals(0, h.find(1));
        assertEquals(0, h.find(2));
        assertEquals(0, h.find(3));
        assertTrue(h.connected(0, 3));
        assertTrue(h.connected(1, 2));
    }

    /**
     * Test the union method when attempting to union already connected elements.
     */
    @Test
    public void testUnionAlreadyConnected() {
        WQUPC h = new WQUPC(10);
        h.union(0, 1);
        h.union(1, 0);  // Calling union again on already connected elements
        assertEquals(0, h.find(1));
        assertTrue(h.connected(0, 1));
    }
}