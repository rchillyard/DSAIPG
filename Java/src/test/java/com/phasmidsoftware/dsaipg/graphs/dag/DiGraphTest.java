package com.phasmidsoftware.dsaipg.graphs.dag;

import com.phasmidsoftware.dsaipg.adt.bqs.BQSException;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class DiGraphTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testVertices() {
        DiGraph<String, Integer> graph = new DiGraph<>();
        graph.addEdge(new Edge<>("A", "B", 3));
        SizedIterable<String> vertices = graph.vertices();
        assertEquals(2, vertices.size());
    }

    @Test
    public void testAdjacent() {
        DiGraph<String, Integer> graph = new DiGraph<>();
        Edge<String, Integer> edgeAB = new Edge<>("A", "B", 3);
        graph.addEdge(edgeAB);
        Iterable<Edge<String, Integer>> edges = graph.adjacent("A");
        Iterator<Edge<String, Integer>> iterator = edges.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(edgeAB, iterator.next());
    }

    @Test
    public void testEdges() {
        DiGraph<String, Integer> graph = new DiGraph<>();
        graph.addEdge(new Edge<>("A", "B", 3));
        SizedIterable<Edge<String, Integer>> edges = graph.edges();
        assertEquals(1, edges.size());
    }

    /**
     * A vertex with no edge at either end must survive reverse(), and must appear
     * in the kernel DAG as a component of its own.
     * <p>
     * reverse() used to rebuild the graph from its edges alone, so such a vertex
     * simply vanished — and because kernelDAG() walks
     * reverse().reversePostOrderDFS(), it was then missing from the
     * strongly-connected-component decomposition too. A graph consisting of one
     * lone vertex produced no kernels at all. It went unnoticed because every
     * other test graph here has an edge at every vertex.
     */
    @Test
    public void testReverseKeepsAnIsolatedVertex() {
        DiGraph<String, Integer> graph = new DiGraph<>();
        graph.addEdge(new Edge<>("A", "B", 1));
        graph.addVertex("Z");
        assertEquals(3, graph.vertices().size());
        assertEquals(3, graph.reverse().vertices().size());
        assertEquals(1, graph.reverse().edges().size());
    }

    @Test
    public void testKernelDAGOfASingleIsolatedVertex() {
        DiGraph<String, Integer> graph = new DiGraph<>();
        graph.addVertex("A");
        final SizedIterable<DiGraph.Kernel<String>> kernels = graph.kernelDAG().vertices();
        assertEquals(1, kernels.size());
        assertEquals("[A]", kernels.iterator().next().toString());
    }

    @Test
    public void testKernelDAG() {
        DiGraph<String, Integer> graph = creatTestGraph();
        SizedIterable<Edge<String, Integer>> edges0 = graph.edges();
        assertEquals(7, edges0.size());
        final DAG<DiGraph.Kernel<String>, Integer> kernelDAG = graph.kernelDAG();
        final SizedIterable<Edge<DiGraph.Kernel<String>, Integer>> edges1 = kernelDAG.edges();
        assertEquals(2, edges1.size());
        final SizedIterable<DiGraph.Kernel<String>> kernels = kernelDAG.vertices();
        assertEquals(3, kernels.size());
        for (DiGraph.Kernel<String> kernel : kernels) {
            final String s = kernel.toString();
            assertTrue(s.equals("[F]") || s.equals("[D, E]") || s.equals("[A, B, C]"));
        }
    }

    /**
     * Reinstated. Five of this test's six assertions were commented out and it
     * popped five values into locals it never looked at, so it checked only that
     * the first vertex was "A" and that six things came off the stack.
     * <p>
     * The commented assertions cannot come back as they were: this graph has two
     * cycles, so its reverse post-order genuinely depends on which edge the bag
     * hands out first, and pinning one permutation would be asserting an accident.
     * What is true regardless is asserted instead.
     * <p>
     * "A" first IS sound, and worth keeping: everything is reachable from A and A
     * is the first vertex the search starts from, so A finishes last and therefore
     * pops first, whatever order the bags choose.
     */
    @Test
    public void testReversePostOrderDFS1() throws BQSException {
        DiGraph<String, Integer> graph = creatTestGraph();
        final Stack<String> reversePostOrder = graph.reversePostOrderDFS();
        assertEquals("everything is reachable from A, so A finishes last and pops first",
                "A", reversePostOrder.pop());
        final List<String> rest = new ArrayList<>();
        while (!reversePostOrder.isEmpty()) rest.add(reversePostOrder.pop());
        Collections.sort(rest);
        assertEquals("every other vertex appears exactly once",
                List.of("B", "C", "D", "E", "F"), rest);
    }

    /**
     * The order within the run is not fixed, but the SET of vertices is, and so is
     * the fact that nothing is visited twice. That holds for any bag ordering.
     */
    @Test
    public void reversePostOrderVisitsEveryVertexOnce() throws BQSException {
        for (long seed = 0; seed < 5; seed++) {
            DiGraph<String, Integer> graph = creatTestGraph(new Random(seed));
            final Stack<String> stack = graph.reversePostOrderDFS();
            final List<String> popped = new ArrayList<>();
            while (!stack.isEmpty()) popped.add(stack.pop());
            assertEquals("seed " + seed + ": A always pops first", "A", popped.get(0));
            final List<String> sorted = new ArrayList<>(popped);
            Collections.sort(sorted);
            assertEquals("seed " + seed, List.of("A", "B", "C", "D", "E", "F"), sorted);
        }
    }

    @Test
    public void testReversePostOrderDFS2() throws BQSException {
        DiGraph<String, Integer> graph = creatTestGraph();
        final Stack<String> reversePostOrder = graph.reverse().reversePostOrderDFS();
        assertEquals("F", reversePostOrder.pop());
        assertEquals("D", reversePostOrder.pop());
        assertEquals("E", reversePostOrder.pop());
        assertEquals("A", reversePostOrder.pop());
        assertEquals("C", reversePostOrder.pop());
        assertEquals("B", reversePostOrder.pop());
        assertTrue(reversePostOrder.isEmpty());
    }

    private DiGraph<String, Integer> creatTestGraph() {
        return creatTestGraph(new Random());
    }

    private DiGraph<String, Integer> creatTestGraph(Random random) {
        DiGraph<String, Integer> graph = new DiGraph<>(random);
//         /------->---------D------->------F
//        A--->B           ^  |
//         <-   |          | ->
//          \   >           E
//           ---C
        graph.addEdge(new Edge<>("A", "B", 1));
        graph.addEdge(new Edge<>("B", "C", 2));
        graph.addEdge(new Edge<>("C", "A", 3));
        graph.addEdge(new Edge<>("A", "D", 4));
        graph.addEdge(new Edge<>("D", "E", 5));
        graph.addEdge(new Edge<>("E", "D", 6));
        graph.addEdge(new Edge<>("D", "F", 7));
        return graph;
    }

    /**
     * The depth-first search used a TreeSet for its marked vertices, which
     * silently required V to be Comparable — a constraint the type does not
     * express, so a perfectly ordinary vertex type failed at runtime with a
     * ClassCastException. It is a HashSet now, which asks only for equals and
     * hashCode.
     */
    @Test
    public void aVertexTypeNeedNotBeComparable() {
        record Point(int x, int y) {
        }
        DiGraph<Point, Integer> graph = new DiGraph<>();
        Point a = new Point(0, 0), b = new Point(1, 1), c = new Point(2, 2);
        graph.addEdge(new Edge<>(a, b, 1));
        graph.addEdge(new Edge<>(b, c, 2));
        assertEquals(3, graph.vertices().size());
        assertEquals(3, graph.kernelDAG().vertices().size());
        // Stack has no size(), so count what comes off it.
        int popped = 0;
        for (Point ignored : graph.reversePostOrderDFS()) popped++;
        assertEquals(3, popped);
    }

    @Test
    public void testToString() {
        DiGraph<String, Integer> graph = new DiGraph<>();
        graph.addEdge(new Edge<>("A", "B", 3));
        assertEquals("{A=Bag_Array{items=[3: A->B], count=1}, B=Bag_Array{items=[], count=0}}", graph.toString());
    }
}