/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.graphs.dag;

import com.phasmidsoftware.dsaipg.adt.bqs.BQSException;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack_LinkedList;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class DAGTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Test method for DAG()
     */
    @Test
    public void testDAG() {
        DAG<Integer, Double> target = new DAG_Impl<>(new Random(0L));
        assertNotNull(target);
        assertEquals(0, target.edges().size());
        assertEquals(0, target.vertices().size());
        assertFalse(target.edges().iterator().hasNext());
        assertFalse(target.vertices().iterator().hasNext());
    }

    /**
     * Test method for addEdge
     */
    @Test
    public void testAddEdge() {
        DAG_Impl<Integer, Double> target = new DAG_Impl<>(new Random(0L));
        Edge<Integer, Double> edge = new Edge<>(1, 2, Math.PI);
        target.addEdge(edge);
        assertEquals(1, target.edges().size());
        assertEquals(2, target.vertices().size());
        assertTrue(target.edges().iterator().hasNext());
        assertTrue(target.vertices().iterator().hasNext());
        assertEquals(edge, target.edges().iterator().next());
        assertEquals(Integer.valueOf(1), target.vertices().iterator().next());
    }

    /**
     * Reinstated. It was commented out with "FIXME this fails because bags are
     * iterated randomly now", and it did — it asserted which edge came out of the
     * bag first, which is exactly what an unordered bag refuses to promise.
     * <p>
     * DiGraph is seedable now, so the traversal is repeatable. But a seeded
     * exact-order assertion is still only a change-detector: it breaks on any
     * refactor that alters how many times a bag is iterated, including one in
     * production code with no test change at all. So this asserts what the graph
     * actually guarantees — which vertices are adjacent to which — and leaves the
     * order alone.
     */
    @Test
    public void testDag2() {
        DAG_Impl<Integer, Double> target = setupStandardDAG(new Random(5L));
        assertEquals(11, target.edges().size());
        assertEquals(7, target.vertices().size());
        assertEquals("every edge leaves the vertex it is filed under",
                Set.of(1, 2, 5), destinationsFrom(target, 0));
        assertEquals(Set.of(4), destinationsFrom(target, 1));
        assertEquals("nothing leaves vertex 2", Set.of(), destinationsFrom(target, 2));
        assertEquals(Set.of(2, 4, 5, 6), destinationsFrom(target, 3));
    }

    /**
     * The destinations of the edges leaving a vertex, as a Set — because the order
     * they come out of the bag is not part of the contract.
     *
     * @param graph  the graph.
     * @param vertex the vertex to look from.
     * @return the vertices it points to.
     */
    private static Set<Integer> destinationsFrom(DiGraph<Integer, Double> graph, int vertex) {
        Set<Integer> result = new HashSet<>();
        for (Edge<Integer, Double> e : graph.adjacent(vertex)) result.add(e.getTo());
        return result;
    }

    /**
     * Reinstated, for the same reason as testDag2. Reversing must turn every edge
     * around and keep every vertex; which order they are stored in is the bag's
     * business.
     */
    @Test
    public void testReverse() {
        DiGraph<Integer, Double> target = setupStandardDAG(new Random(5L));
        DiGraph<Integer, Double> reversed = target.reverse();
        assertEquals(11, reversed.edges().size());
        assertEquals(7, reversed.vertices().size());
        Set<List<Integer>> forwards = new HashSet<>();
        for (Edge<Integer, Double> e : target.edges()) forwards.add(List.of(e.getFrom(), e.getTo()));
        Set<List<Integer>> backwards = new HashSet<>();
        for (Edge<Integer, Double> e : reversed.edges()) backwards.add(List.of(e.getTo(), e.getFrom()));
        assertEquals("every edge is reversed and none is lost", forwards, backwards);
        assertEquals(Set.of(6), destinationsFrom(reversed, 0));
        assertEquals("nothing points at 3 in the original, so nothing leaves it reversed",
                Set.of(), destinationsFrom(reversed, 3));
    }

    /**
     * Test method for DFS
     * <p>
     * FIXME
     */
    @Test
    public void testDFS() {
        Queue<Integer> preOrder = new LinkedList<>();
        Queue<Integer> postOrder = new LinkedList<>();
        Stack<Integer> reversePostOrder = new Stack_LinkedList<>();
        DAG_Impl<Integer, Double> target = setupStandardDAG(new Random(0L));
        Consumer<Integer> pre = preOrder::add;
        Consumer<Integer> post = (v) -> {
            postOrder.add(v);
            reversePostOrder.push(v);
        };

        target.dfs(0, pre, post);
        assertEquals(Integer.valueOf(0), ((LinkedList<Integer>) preOrder).getFirst());
        assertEquals(Integer.valueOf(5), ((LinkedList<Integer>) preOrder).getLast());
        assertEquals(Integer.valueOf(4), ((LinkedList<Integer>) postOrder).getFirst());
        assertEquals(Integer.valueOf(0), ((LinkedList<Integer>) postOrder).getLast());
        try {
            assertEquals(Integer.valueOf(0), (reversePostOrder).pop());
            assertEquals(Integer.valueOf(5), (reversePostOrder).pop());
            assertEquals(Integer.valueOf(2), (reversePostOrder).pop());
            assertEquals(Integer.valueOf(1), (reversePostOrder).pop());
            assertEquals(Integer.valueOf(4), (reversePostOrder).pop());
        } catch (BQSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for sorted
     */
    @Test
    public void testSorted() {
        DAG<Integer, Double> target = setupStandardDAG(new Random(0L));
        Iterable<Integer> sorted = target.sorted();
        System.out.println(sorted);
        Iterator<Integer> iterator = sorted.iterator();
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(6), iterator.next());
        assertEquals(Integer.valueOf(0), iterator.next());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Reinstated. It had no {@code @Test} at all — not commented out, simply never
     * annotated, so it looked like a test and was invisible to the ratchet — with
     * the note "TODO reinstate this test but the result is not really predictable".
     * <p>
     * It is not predictable, and cannot be: a graph with a cycle has no topological
     * order, so no assertion about the sequence can be right. What IS true is that
     * sorted() still returns every vertex exactly once, and that is what this now
     * asserts.
     */
    @Test
    public void testSortedWithCycle() {
        DAG_Impl<Integer, Double> target = setupStandardDAG(new Random(0L));
        target.addEdge(new Edge<>(4, 3, 1.0));
        List<Integer> sorted = new ArrayList<>();
        for (Integer v : target.sorted()) sorted.add(v);
        List<Integer> ascending = new ArrayList<>(sorted);
        Collections.sort(ascending);
        assertEquals("every vertex appears exactly once, even with a cycle",
                List.of(0, 1, 2, 3, 4, 5, 6), ascending);
    }

    private DAG_Impl<Integer, Double> setupStandardDAG(Random random) {
        DAG_Impl<Integer, Double> target = new DAG_Impl<>(random);
        target.addEdge(0, 1, 1.0);
        target.addEdge(0, 2, 1.0);
        target.addEdge(0, 5, 1.0);
        target.addEdge(1, 4, 1.0);
        target.addEdge(3, 2, 1.0);
        target.addEdge(3, 4, 1.0);
        target.addEdge(3, 5, 1.0);
        target.addEdge(3, 6, 1.0);
        target.addEdge(5, 2, 1.0);
        target.addEdge(6, 0, 1.0);
        target.addEdge(6, 4, 1.0);
        return target;
    }

}
