import unittest
import random
from src.adt.pq.priority_queue_binary_heap import PriorityQueueBinaryHeap
from src.adt.pq.pq_exception import PQException

class TestPriorityQueueBinaryHeapPorted(unittest.TestCase):

    def test_inverted_1a(self):
        bin_heap = [None, "A", "B"]
        max_pq = False
        # Note: In Python implementation, we pass initial_data or rely on internal list manipulation for testing.
        # Since we want to test specific internal state, we'll initialize and then manually set _heap if needed,
        # or pass initial_data carefully.
        # However, the Java test passes a pre-filled array. Our constructor takes initial_data (Iterable).
        # To match the Java test exactly (which sets specific indices), we might need to be careful.
        # Java: new PriorityQueue_BinaryHeap<>(max, binHeap, 1, 2, ...)
        # This implies 1-based indexing, heap size 2.
        
        pq = PriorityQueueBinaryHeap(max_priority=max_pq, first=1)
        pq._heap = [None, "A", "B"]
        
        # Java: inverted(1, 2)
        self.assertEqual(max_pq, pq._inverted(1, 2))

    def test_inverted_1b(self):
        # Java: binHeap[0]="A", binHeap[1]="B", first=0
        max_pq = False
        pq = PriorityQueueBinaryHeap(max_priority=max_pq, first=0)
        pq._heap = ["A", "B"]
        
        # Java: inverted(0, 1)
        self.assertEqual(max_pq, pq._inverted(0, 1))

    def test_inverted_2(self):
        # Java: binHeap[1]="A", binHeap[2]="B", max=true
        max_pq = True
        pq = PriorityQueueBinaryHeap(max_priority=max_pq, first=1)
        pq._heap = [None, "A", "B"]
        
        # Java: inverted(1, 2)
        self.assertEqual(max_pq, pq._inverted(1, 2))

    def test_swim_up_0(self):
        a = "A"
        b = "B"
        # Java: binHeap[0]=a, binHeap[1]=b, first=0, max=true
        # We need to construct it such that it DOESN'T heapify automatically if we want to test swimUp manually?
        # The Java constructor with array DOES NOT heapify automatically unless heapConstructor is called?
        # Wait, Java constructor: this.binHeap = (K[]) binHeap; ...
        # It just assigns the array.
        # Our Python constructor with initial_data calls heap_constructor()!
        # "if initial_data: ... self.heap_constructor()"
        
        # To strictly replicate the test where we manually set state and then call swimUp,
        # we should instantiate empty and manually modify _heap.
        
        pq = PriorityQueueBinaryHeap(max_priority=True, first=0)
        pq._heap = [a, b] # Manually set heap state
        
        self.assertEqual(a, pq.peek(0))
        pq._swim_up(1)
        self.assertEqual(b, pq.peek(0))

    def test_swim_up_1(self):
        a = "A"
        b = "B"
        # Java: binHeap[1]=a, binHeap[2]=b, first=1
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq._heap = [None, a, b]
        
        self.assertEqual(a, pq.peek(1))
        pq._swim_up(2)
        self.assertEqual(b, pq.peek(1))

    def test_swim_up_2(self):
        # Java: binHeap[1]="Z", [2]="A", [3]="B", [4]="C", max=true
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq._heap = [None, "Z", "A", "B", "C"]
        
        pq._swim_up(4) # Swim "C"
        self.assertEqual("C", pq.peek(2)) # Peek at root? Wait.
        # Java: assertEquals("C", tester.invokePrivate("peek", 2));
        # In Java test, peek(2) checks index 2.
        # Initial: [None, Z, A, B, C]
        # Swim C (index 4) up. Parent of 4 is 2 (A).
        # C > A? Yes. Swap.
        # Heap: [None, Z, C, B, A]
        # Parent of 2 is 1 (Z).
        # C > Z? No (Z > C). Stop.
        # So C ends up at index 2.
        self.assertEqual("C", pq.peek(2))

    def test_swim_up_3(self):
        # Java: [1]="D", [2]="C", [3]="E", [4]="B", max=false (min-heap)
        pq = PriorityQueueBinaryHeap(max_priority=False, first=1)
        pq._heap = [None, "D", "C", "E", "B"]
        
        pq._swim_up(4) # Swim "B"
        # Parent of 4 is 2 ("C").
        # B < C? Yes. Swap.
        # Heap: [None, D, B, E, C]
        # Parent of 2 is 1 ("D").
        # B < D? Yes. Swap.
        # Heap: [None, B, D, E, C]
        # B is at root (index 1).
        
        self.assertEqual("B", pq.peek(1))

    def test_sink_0a(self):
        a, b, c = "A", "B", "C"
        # Java: [0]=b, [1]=c, [2]=a, first=0, max=true
        pq = PriorityQueueBinaryHeap(max_priority=True, first=0)
        pq._heap = [b, c, a]
        
        pq._sink(0)
        # Sink b at 0. Children: 1(c), 2(a).
        # Max child: c (index 1).
        # b < c? Yes. Swap.
        # Heap: [c, b, a]
        
        self.assertEqual(c, pq.peek(0))
        self.assertEqual(a, pq.peek(2))

    def test_sink_0b(self):
        a, b, c = "A", "B", "C"
        # Java: [1]=a, [2]=b, [3]=c. first=1. max=true
        # Wait, Java testSink0b uses binHeap size 4.
        # binHeap[1]=a, [2]=b, [3]=c.
        # This implies heap size is 3 (indices 1,2,3).
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq._heap = [None, a, b, c]
        
        pq._sink(1)
        # Sink a at 1. Children: 2(b), 3(c).
        # Max child: c (index 3).
        # a < c? Yes. Swap.
        # Heap: [None, c, b, a]
        
        self.assertEqual(c, pq.peek(1))
        self.assertEqual(a, pq.peek(3))

    def test_sink_1(self):
        # Same as sink0b but named testSink1 in Java
        a, b, c = "A", "B", "C"
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq._heap = [None, a, b, c]
        
        pq._sink(1)
        self.assertEqual(c, pq.peek(1))
        self.assertEqual(a, pq.peek(3))

    def test_give_1(self):
        pq = PriorityQueueBinaryHeap(max_priority=True) # default first=0
        key = "A"
        pq.give(key)
        self.assertEqual(1, pq.size())
        self.assertEqual(key, pq.peek(0))

    def test_give_2(self):
        # Java tests fixed capacity behavior (give(null)).
        # Python implementation is dynamic and doesn't support null/None as value usually,
        # but let's see.
        pq = PriorityQueueBinaryHeap(max_priority=True)
        key = "A"
        # We can't really test "give more than capacity" since capacity is infinite.
        # But we can test giving multiple items.
        pq.give("IGNORED") # Placeholder for the "null" in Java test
        self.assertEqual(1, pq.size())
        pq.give(key)
        self.assertEqual(2, pq.size())
        # In Java, the first one was overwritten or lost?
        # "if (m == binHeap.length - first) m--;" -> Overwrites last element if full.
        # Python: just appends.
        # So this test is slightly different. We verify both are there.
        self.assertEqual("IGNORED", pq.peek(0)) # "IGNORED" > "A"? I > A. Yes.
        # Wait, "IGNORED" vs "A". 'I' is 73, 'A' is 65.
        # So "IGNORED" is root.

    def test_take_1(self):
        pq = PriorityQueueBinaryHeap(max_priority=True)
        key = "A"
        pq.give(key)
        self.assertEqual(key, pq.take())
        self.assertTrue(pq.is_empty())

    def test_take_2(self):
        pq = PriorityQueueBinaryHeap(max_priority=True)
        a, b = "A", "B"
        pq.give(a)
        pq.give(b)
        # Heap should be [B, A] (since B > A)
        self.assertEqual(b, pq.peek(0))
        self.assertEqual(a, pq.peek(1))
        
        self.assertEqual(b, pq.take())
        self.assertEqual(a, pq.take())
        self.assertTrue(pq.is_empty())

    def test_take_3(self):
        pq = PriorityQueueBinaryHeap(max_priority=True)
        pq.give("A")
        pq.take()
        with self.assertRaises(PQException):
            pq.take()

    def test_is_empty(self):
        pq = PriorityQueueBinaryHeap(max_priority=False)
        self.assertTrue(pq.is_empty())

    def test_size(self):
        pq = PriorityQueueBinaryHeap(max_priority=False)
        self.assertEqual(0, pq.size())
        pq.give("A")
        self.assertEqual(1, pq.size())
        pq.take()
        self.assertEqual(0, pq.size())

    def test_do_take_01(self):
        # Java: [0]="A", [1]="B", [2]="C", first=0, max=false (min)
        # Snake
        pq = PriorityQueueBinaryHeap(max_priority=False, first=0, floyd=True)
        pq._heap = ["A", "B", "C"]
        
        # doTake(snake)
        # This is effectively calling take() but we want to verify internal state?
        # Java: pq.doTake(pq::snake)
        # Our take() calls _do_take.
        
        # We can just call take() since floyd=True is set.
        val = pq.take() # Should be "A"
        self.assertEqual("A", val)
        # Remaining: B, C. Min heap.
        # Root should be B (since B < C).
        self.assertEqual("B", pq.peek(0))

    def test_do_take_02(self):
        # Java: [0]="C", [1]="A", [2]="B", first=0, max=true
        # Sink
        pq = PriorityQueueBinaryHeap(max_priority=True, first=0, floyd=False)
        pq._heap = ["C", "A", "B"]
        
        val = pq.take() # "C"
        self.assertEqual("C", val)
        # Remaining: A, B. Max heap.
        # Root should be B (B > A).
        self.assertEqual("B", pq.peek(0))

    def test_do_take_11(self):
        # Java: [1]="A", [2]="B", [3]="C", first=1, max=false
        # Snake
        pq = PriorityQueueBinaryHeap(max_priority=False, first=1, floyd=True)
        pq._heap = [None, "A", "B", "C"]
        
        val = pq.take() # "A"
        self.assertEqual("A", val)
        self.assertEqual("B", pq.peek(1))

    def test_do_take_12(self):
        # Java: [1]="C", [2]="A", [3]="B", first=1, max=true
        # Sink
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1, floyd=False)
        pq._heap = [None, "C", "A", "B"]
        
        val = pq.take() # "C"
        self.assertEqual("C", val)
        self.assertEqual("B", pq.peek(1))

    def test_iterator_0(self):
        # Java: [0]="C", [1]="B", [2]="D", first=0, max=true
        pq = PriorityQueueBinaryHeap(max_priority=True, first=0)
        pq._heap = ["C", "B", "D"]
        
        self.assertEqual(3, pq.size())
        items = list(pq)
        self.assertEqual(3, len(items))
        self.assertEqual("C", items[0])
        self.assertEqual("B", items[1])
        self.assertEqual("D", items[2])

    def test_iterator_1(self):
        # Java: [1]="C", [2]="B", [3]="D", first=1
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq._heap = [None, "C", "B", "D"]
        
        self.assertEqual(3, pq.size())
        items = list(pq)
        self.assertEqual(3, len(items))
        self.assertEqual("C", items[0])
        self.assertEqual("B", items[1])
        self.assertEqual("D", items[2])

    def test_get_max(self):
        pq = PriorityQueueBinaryHeap(max_priority=False)
        self.assertFalse(pq._max)

    def test_take_4(self):
        # Java: [1]="D", [2]="A", [3]="C", [4]="B", max=true
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq._heap = [None, "D", "A", "C", "B"]
        
        val = pq.take()
        self.assertEqual("D", val)
        self.assertEqual(3, pq.size())

    def test_take_5(self):
        # Java: [1]="A", [2]="C", [3]="B", [4]="Z", max=false
        pq = PriorityQueueBinaryHeap(max_priority=False, first=1)
        pq._heap = [None, "A", "C", "B", "Z"]
        
        val = pq.take()
        self.assertEqual("A", val)
        self.assertEqual(3, pq.size())

    def test_take_6(self):
        pq = PriorityQueueBinaryHeap()
        with self.assertRaises(PQException):
            pq.take()

    def test_priority_queue_list(self):
        data = list(range(20))
        random.shuffle(data)
        pq = PriorityQueueBinaryHeap(max_priority=True, initial_data=data)
        
        for i in range(19, -1, -1):
            self.assertEqual(i, pq.take())

    def test_heap_constructor(self):
        data = list(range(20))
        random.shuffle(data)
        # Pass data but don't let it auto-construct? 
        # Our constructor auto-constructs if initial_data is passed.
        # Java test: new PQ(..., list.toArray(), ...); pq.heapConstructor();
        # To replicate "manual" construction:
        pq = PriorityQueueBinaryHeap(max_priority=True, first=0)
        pq._heap = data[:] # Just copy raw data
        pq.heap_constructor() # Now heapify
        
        for i in range(19, -1, -1):
            self.assertEqual(i, pq.take())

    def test_do_heapify_a(self):
        # Java: [None, "C", "D", "A", "E", "B"], max=false
        # doHeapifyStandard(2) -> returns 5
        # doHeapifyStandard(1) -> returns 3
        pq = PriorityQueueBinaryHeap(max_priority=False, first=1)
        pq._heap = [None, "C", "D", "A", "E", "B"]
        
        # We need to expose the return value of _do_heapify_standard or just check the effect?
        # Java test checks return value (final index).
        # Our _do_heapify returns int.
        
        res = pq._do_heapify_standard(2)
        self.assertEqual(5, res)
        
        res = pq._do_heapify_standard(1)
        self.assertEqual(3, res)

    def test_do_heapify_b(self):
        # Java: ["C", "D", "A", "E", "B"], max=false, first=0
        pq = PriorityQueueBinaryHeap(max_priority=False, first=0)
        pq._heap = ["C", "D", "A", "E", "B"]
        
        res = pq._do_heapify_standard(1)
        self.assertEqual(4, res)
        
        res = pq._do_heapify_standard(0)
        self.assertEqual(2, res)

if __name__ == '__main__':
    unittest.main()
