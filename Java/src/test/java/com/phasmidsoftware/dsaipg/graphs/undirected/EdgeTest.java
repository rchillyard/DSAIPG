/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.graphs.undirected;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class EdgeTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for Edge
     */
    @Test
    public void testEdge() {
        Edge<Integer, String> target = new Edge<>(1, 2, "hello");
        assertNotNull(target);
        final Integer v = target.get();
        assertEquals(Integer.valueOf(1), v);
        final Integer other = target.getOther(v);
        assertEquals(Integer.valueOf(2), other);
        assertEquals(Integer.valueOf(1), target.getOther(other));
        assertEquals("hello", target.getAttribute());
    }

    @Test
    public void equalsTest() {
        Edge<Integer, String> target1 = new Edge<>(1, 2, "hello");
        Edge<Integer, String> target2 = new Edge<>(2, 1, "hello");
        assertEquals(target1, target2);
    }

    @Test
    public void equalsTestNegative() {
        Edge<Integer, String> target = new Edge<>(1, 2, "hello");
        assertNotEquals(target, new Edge<>(1, 2, "goodbye"));
        assertNotEquals(target, new Edge<>(1, 3, "hello"));
        assertNotEquals(target, "1-2: hello");
    }

    /**
     * The contract which makes {@link #equalsTest} useful. Two edges which differ
     * only in the order of their vertices are equal, so they must hash alike --
     * otherwise they would compare equal yet land in different buckets, and a Set
     * would hold both.
     */
    @Test
    public void hashCodeTest() {
        Edge<Integer, String> target1 = new Edge<>(1, 2, "hello");
        Edge<Integer, String> target2 = new Edge<>(2, 1, "hello");
        assertEquals(target1.hashCode(), target2.hashCode());
        Set<Edge<Integer, String>> set = new HashSet<>();
        set.add(target1);
        set.add(target2);
        assertEquals(1, set.size());
    }

    @Test
    public void toStringTest() {
        assertEquals("1-2: hello", new Edge<>(1, 2, "hello").toString());
    }
}
