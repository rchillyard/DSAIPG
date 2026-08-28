package com.phasmidsoftware.dsaipg.graphs.undirected;

import com.phasmidsoftware.dsaipg.adt.bqs.Bag_Array;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class GraphSimpleTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void adjacent() {
        final Graph_Simple graph = new Graph_Simple();
        graph.addEdge(1, 2);
        assertEquals(1, ((Bag_Array<?>) graph.adjacent(1)).size());
        assertEquals(1, ((Bag_Array<?>) graph.adjacent(2)).size());
        assertTrue(((Bag_Array<Integer>) graph.adjacent(1)).contains(2));
        assertTrue(((Bag_Array<Integer>) graph.adjacent(2)).contains(1));
    }

    /**
     * An unknown vertex gives an empty Iterable, not null. Prim, ShortestPaths and
     * DiGraph's depth-first search all iterate the result of adjacent(), so
     * returning null handed each of them a NullPointerException in waiting.
     */
    @Test
    public void adjacentOfAnUnknownVertexIsEmptyNotNull() {
        final Graph_Simple graph = new Graph_Simple();
        graph.addEdge(1, 2);
        Iterable<Integer> adjacent = graph.adjacent(99);
        assertNotNull(adjacent);
        assertFalse(adjacent.iterator().hasNext());
    }

    @Test
    public void askingAboutAnUnknownVertexDoesNotCreateIt() {
        final Graph_Simple graph = new Graph_Simple();
        graph.addEdge(1, 2);
        graph.adjacent(99);
        assertEquals(2, graph.vertices().size());
    }

    @Test
    public void testToString() {
        final Graph_Simple graph = new Graph_Simple();
        graph.addEdge(1, 2);
        assertEquals("{1=Bag_Array{items=[2], count=1}, 2=Bag_Array{items=[1], count=1}}", graph.toString());
    }
}