package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import org.junit.Test;

import java.util.*;

import static java.util.Collections.shuffle;
import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class PriorityQueueTest {

    @Test
    public void testInverted1a() {
        String[] binHeap = new String[3];
        binHeap[1] = "A";
        binHeap[2] = "B";
        boolean max = false;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(max, binHeap, 1, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(max, tester.invokePrivate("inverted", 1, 2));
    }

    @Test
    public void testInverted1b() {
        String[] binHeap = new String[3];
        binHeap[0] = "A";
        binHeap[1] = "B";
        boolean max = false;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(max, binHeap, 0, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(max, tester.invokePrivate("inverted", 0, 1));
    }

    @Test
    public void testInverted2() {
        String[] binHeap = new String[3];
        binHeap[1] = "A";
        binHeap[2] = "B";
        boolean max = true;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(max, binHeap, 1, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(max, tester.invokePrivate("inverted", 1, 2));
    }

    @Test
    public void testSwimUp0() {
        String[] binHeap = new String[3];
        String a = "A";
        String b = "B";
        binHeap[0] = a;
        binHeap[1] = b;
        // Create PQ which uses the 0th index.
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 0, 2, Comparator.comparing(String::toString), true);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(a, tester.invokePrivate("peek", 0));
        tester.invokePrivate("swimUp", 1);
        assertEquals(b, tester.invokePrivate("peek", 0));
    }

    @Test
    public void testSwimUp1() {
        String[] binHeap = new String[3];
        String a = "A";
        String b = "B";
        binHeap[1] = a;
        binHeap[2] = b;
        // Create PQ which does not use the 0th index.
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 2, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(a, tester.invokePrivate("peek", 1));
        tester.invokePrivate("swimUp", 2);
        assertEquals(b, tester.invokePrivate("peek", 1));
    }

    @Test
    public void testSwimUp2() {
        String[] binHeap = new String[5];
        binHeap[1] = "Z";
        binHeap[2] = "A";
        binHeap[3] = "B";
        binHeap[4] = "C";
        // Create PQ as a max-heap.
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 4, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("swimUp", 4); // Swim "C" upward.
        assertEquals("C", tester.invokePrivate("peek", 2)); // Peek at root.
    }

    @Test
    public void testSwimUp3() {
        String[] binHeap = new String[5];
        binHeap[1] = "D";
        binHeap[2] = "C";
        binHeap[3] = "E";
        binHeap[4] = "B";
        // Create PQ as a min-heap.
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(false, binHeap, 1, 4, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("swimUp", 4); // Swim "B" upward.
        assertEquals("B", tester.invokePrivate("peek", 1)); // Peek at root.
    }

    @Test
    public void testSink0a() {
        String[] binHeap = new String[3];
        String a = "A";
        String b = "B";
        String c = "C";
        binHeap[0] = b;
        binHeap[1] = c;
        binHeap[2] = a;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 0, 3, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("sink", 0);
        assertEquals(c, tester.invokePrivate("peek", 0));
        assertEquals(a, tester.invokePrivate("peek", 2));
    }

    @Test
    public void testSink0b() {
        String[] binHeap = new String[4];
        String a = "A";
        String b = "B";
        String c = "C";
        binHeap[1] = a;
        binHeap[2] = b;
        binHeap[3] = c;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 3, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("sink", 1);
        assertEquals(c, tester.invokePrivate("peek", 1));
        assertEquals(a, tester.invokePrivate("peek", 3));
    }

    @Test
    public void testSink1() {
        String[] binHeap = new String[4];
        String a = "A";
        String b = "B";
        String c = "C";
        binHeap[1] = a;
        binHeap[2] = b;
        binHeap[3] = c;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 3, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        tester.invokePrivate("sink", 1);
        assertEquals(c, tester.invokePrivate("peek", 1));
        assertEquals(a, tester.invokePrivate("peek", 3));
    }

    @Test
    public void testGive1() {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(10, Comparator.comparing(String::toString));
        String key = "A";
        pq.give(key);
        assertEquals(1, pq.size());
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(key, tester.invokePrivate("peek", 0));
    }

    @Test
    public void testGive2() {
        // Test that we can comfortably give more elements than the the PQ has capacity for
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(1, Comparator.comparing(String::toString));
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        String key = "A";
        pq.give(null); // This will never survive so it might as well be null
        assertEquals(1, pq.size());
        assertNull(tester.invokePrivate("peek", 0));
        pq.give(key);
        assertEquals(1, pq.size());
        assertEquals(key, tester.invokePrivate("peek", 0));
    }

    @Test
    public void testTake1() throws PQException {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(10, Comparator.comparing(String::toString));
        String key = "A";
        pq.give(key);
        assertEquals(key, pq.take());
        assertTrue(pq.isEmpty());
    }

    @Test
    public void testTake2() throws PQException {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(10, Comparator.comparing(String::toString));
        String a = "A";
        String b = "B";
        pq.give(a);
        pq.give(b);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(a, tester.invokePrivate("peek", 1));
        assertEquals(b, tester.invokePrivate("peek", 0));
        assertEquals(b, pq.take());
        assertEquals(a, pq.take());
        assertTrue(pq.isEmpty());

    }

    @Test(expected = PQException.class)
    public void testTake3() throws PQException {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(10, Comparator.comparing(String::toString));
        pq.give("A");
        pq.take();
        pq.take();
    }

    @Test
    public void isEmpty() {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(10, false, Comparator.comparing(String::toString));
        assertTrue(pq.isEmpty());
    }

    @Test
    public void size() throws PQException {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(10, false, Comparator.comparing(String::toString));
        assertEquals(0, pq.size());
        pq.give("A");
        assertEquals(1, pq.size());
        pq.take();
        assertEquals(0, pq.size());
    }

    @Test
    public void doTake01() throws PQException {
        String[] binHeap = new String[3];
        binHeap[0] = "A";
        binHeap[1] = "B";
        binHeap[2] = "C";
        PriorityQueue_BinaryHeap<String> pq = new PriorityQueue_BinaryHeap<>(false, binHeap, 0, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::snake);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 0));
    }

    @Test
    public void doTake02() throws PQException {
        String[] binHeap = new String[3];
        binHeap[0] = "C";
        binHeap[1] = "A";
        binHeap[2] = "B";
        PriorityQueue_BinaryHeap<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 0, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::sink);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 0));
    }

    @Test
    public void doTake11() throws PQException {
        String[] binHeap = new String[4];
        binHeap[1] = "A";
        binHeap[2] = "B";
        binHeap[3] = "C";
        PriorityQueue_BinaryHeap<String> pq = new PriorityQueue_BinaryHeap<>(false, binHeap, 1, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::snake);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 1));
    }

    @Test
    public void doTake12() throws PQException {
        String[] binHeap = new String[4];
        binHeap[1] = "C";
        binHeap[2] = "A";
        binHeap[3] = "B";
        PriorityQueue_BinaryHeap<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 3, Comparator.comparing(String::toString), false);
        pq.doTake(pq::sink);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals("B", tester.invokePrivate("peek", 1));
    }

    @Test
    public void iterator0() {
        String[] binHeap = new String[3];
        binHeap[0] = "C";
        binHeap[1] = "B";
        binHeap[2] = "D";
        PriorityQueue_BinaryHeap<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 0, 3, Comparator.comparing(String::toString), false);
        assertEquals(3, pq.size());
        Iterator<String> iterator = pq.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(binHeap[0], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(binHeap[1], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(binHeap[2], iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, pq.size());
    }

    @Test
    public void iterator1() {
        String[] binHeap = new String[4];
        binHeap[1] = "C";
        binHeap[2] = "B";
        binHeap[3] = "D";
        PriorityQueue_BinaryHeap<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 3, Comparator.comparing(String::toString), false);
        assertEquals(3, pq.size());
        Iterator<String> iterator = pq.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(binHeap[1], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(binHeap[2], iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(binHeap[3], iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, pq.size());
    }

    @Test
    public void testGetMax() {
        Iterable<String> pq = new PriorityQueue_BinaryHeap<>(10, false, Comparator.comparing(String::toString));
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(false, tester.invokePrivate("getMax"));
    }

    @Test
    public void testTake4() throws PQException {
        String[] binHeap = new String[5];
        binHeap[1] = "D";
        binHeap[2] = "A";
        binHeap[3] = "C";
        binHeap[4] = "B";
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(true, binHeap, 1, 4, Comparator.comparing(String::toString), false);
        String takenValue = pq.take();
        assertEquals("D", takenValue); // Ensure the max-heap returns the largest element.
        assertEquals(3, pq.size()); // Ensure size is reduced after take.
    }

    @Test
    public void testTake5() throws PQException {
        String[] binHeap = new String[5];
        binHeap[1] = "A";
        binHeap[2] = "C";
        binHeap[3] = "B";
        binHeap[4] = "Z";
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(false, binHeap, 1, 4, Comparator.comparing(String::toString), false);
        String takenValue = pq.take();
        assertEquals("A", takenValue); // Ensure the min-heap returns the smallest element.
        assertEquals(3, pq.size()); // Ensure size is reduced after take.
    }

    @Test(expected = PQException.class)
    public void testTake6() throws PQException {
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(5, Comparator.comparing(String::toString));
        pq.take(); // Attempting to take from an empty queue should throw PQException.
    }

    @Test
    public void testPriorityQueueList() throws PQException {
        Integer[] binHeap = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        List<Integer> list = Arrays.asList(binHeap);
        shuffle(list, new Random());
        PriorityQueue<Integer> pq = new PriorityQueue_BinaryHeap<>(list, Integer::compare);
        assertEquals(Integer.valueOf(19), pq.take());
        assertEquals(Integer.valueOf(18), pq.take());
        assertEquals(Integer.valueOf(17), pq.take());
        assertEquals(Integer.valueOf(16), pq.take());
        assertEquals(Integer.valueOf(15), pq.take());
        assertEquals(Integer.valueOf(14), pq.take());
        assertEquals(Integer.valueOf(13), pq.take());
        assertEquals(Integer.valueOf(12), pq.take());
        assertEquals(Integer.valueOf(11), pq.take());
        assertEquals(Integer.valueOf(10), pq.take());
        assertEquals(Integer.valueOf(9), pq.take());
        assertEquals(Integer.valueOf(8), pq.take());
        assertEquals(Integer.valueOf(7), pq.take());
        assertEquals(Integer.valueOf(6), pq.take());
        assertEquals(Integer.valueOf(5), pq.take());
        assertEquals(Integer.valueOf(4), pq.take());
        assertEquals(Integer.valueOf(3), pq.take());
        assertEquals(Integer.valueOf(2), pq.take());
        assertEquals(Integer.valueOf(1), pq.take());
        assertEquals(Integer.valueOf(0), pq.take());
    }

    @Test
    public void testHeapConstructor() throws PQException {
        Integer[] binHeap = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        List<Integer> list = Arrays.asList(binHeap);
        shuffle(list, new Random());
        PriorityQueue<Integer> pq = new PriorityQueue_BinaryHeap<>(true, list.toArray(new Integer[0]), 0, binHeap.length, Integer::compare, false);
        pq.heapConstructor();
        assertEquals(Integer.valueOf(19), pq.take());
        assertEquals(Integer.valueOf(18), pq.take());
        assertEquals(Integer.valueOf(17), pq.take());
        assertEquals(Integer.valueOf(16), pq.take());
        assertEquals(Integer.valueOf(15), pq.take());
        assertEquals(Integer.valueOf(14), pq.take());
        assertEquals(Integer.valueOf(13), pq.take());
        assertEquals(Integer.valueOf(12), pq.take());
        assertEquals(Integer.valueOf(11), pq.take());
        assertEquals(Integer.valueOf(10), pq.take());
        assertEquals(Integer.valueOf(9), pq.take());
        assertEquals(Integer.valueOf(8), pq.take());
        assertEquals(Integer.valueOf(7), pq.take());
        assertEquals(Integer.valueOf(6), pq.take());
        assertEquals(Integer.valueOf(5), pq.take());
        assertEquals(Integer.valueOf(4), pq.take());
        assertEquals(Integer.valueOf(3), pq.take());
        assertEquals(Integer.valueOf(2), pq.take());
        assertEquals(Integer.valueOf(1), pq.take());
        assertEquals(Integer.valueOf(0), pq.take());
    }

    @Test
    public void testDoHeapifya() throws PQException {
        String[] binHeap = new String[]{null, "C", "D", "A", "E", "B"};
        boolean max = false;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(max, binHeap, 1, 5, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(5, tester.invokePrivate("doHeapifyStandard", 2));
        assertEquals(3, tester.invokePrivate("doHeapifyStandard", 1));

    }

    @Test
    public void testDoHeapifyb() throws PQException {
        String[] binHeap = new String[]{"C", "D", "A", "E", "B"};
        boolean max = false;
        PriorityQueue<String> pq = new PriorityQueue_BinaryHeap<>(max, binHeap, 0, 5, Comparator.comparing(String::toString), false);
        final PrivateMethodTester tester = new PrivateMethodTester(pq);
        assertEquals(4, tester.invokePrivate("doHeapifyStandard", 1));
        assertEquals(2, tester.invokePrivate("doHeapifyStandard", 0));

    }
}
