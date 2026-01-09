import unittest
from src.adt.pq.priority_queue_binary_heap import PriorityQueueBinaryHeap
from src.adt.pq.pq_exception import PQException

class TestPriorityQueueBinaryHeap(unittest.TestCase):

    def test_max_pq_basic(self):
        pq = PriorityQueueBinaryHeap(max_priority=True)
        self.assertTrue(pq.is_empty())
        self.assertEqual(pq.size(), 0)

        pq.give(10)
        self.assertFalse(pq.is_empty())
        self.assertEqual(pq.size(), 1)
        self.assertEqual(pq.peek(0), 10)

        pq.give(20)
        self.assertEqual(pq.size(), 2)
        self.assertEqual(pq.peek(0), 20) # Max should be at root

        pq.give(5)
        self.assertEqual(pq.size(), 3)
        self.assertEqual(pq.peek(0), 20)

        self.assertEqual(pq.take(), 20)
        self.assertEqual(pq.size(), 2)
        self.assertEqual(pq.take(), 10)
        self.assertEqual(pq.take(), 5)
        self.assertTrue(pq.is_empty())

    def test_min_pq_basic(self):
        pq = PriorityQueueBinaryHeap(max_priority=False)
        pq.give(10)
        pq.give(20)
        pq.give(5)

        self.assertEqual(pq.take(), 5)
        self.assertEqual(pq.take(), 10)
        self.assertEqual(pq.take(), 20)

    def test_floyd_trick(self):
        # Floyd's trick is used in 'take'.
        # We need a large enough heap to potentially see a difference in execution path,
        # but for correctness, small is fine.
        pq = PriorityQueueBinaryHeap(max_priority=True, floyd=True)
        data = [10, 20, 5, 30, 15, 25]
        for x in data:
            pq.give(x)
        
        expected = sorted(data, reverse=True)
        result = []
        while not pq.is_empty():
            result.append(pq.take())
        
        self.assertEqual(result, expected)

    def test_custom_comparator(self):
        # Sort strings by length
        def length_comparator(s1, s2):
            if len(s1) > len(s2): return 1
            if len(s1) < len(s2): return -1
            return 0

        pq = PriorityQueueBinaryHeap(max_priority=True, comparator=length_comparator)
        pq.give("a")
        pq.give("ccc")
        pq.give("bb")

        self.assertEqual(pq.take(), "ccc")
        self.assertEqual(pq.take(), "bb")
        self.assertEqual(pq.take(), "a")

    def test_first_index_1(self):
        # 1-based indexing
        pq = PriorityQueueBinaryHeap(max_priority=True, first=1)
        pq.give(10)
        # Internal heap should have None at index 0
        # We can't easily access private _heap, but we can check peek behavior if we knew how peek works with offset.
        # The peek method in our implementation takes an absolute index into the underlying list?
        # Let's check implementation:
        # def peek(self, k: int) -> Optional[K]:
        #    if 0 <= k < len(self._heap): return self._heap[k]
        
        # If first=1, heap has [None, 10].
        # peek(0) -> None
        # peek(1) -> 10
        self.assertIsNone(pq.peek(0))
        self.assertEqual(pq.peek(1), 10)
        
        pq.give(20)
        # Heap: [None, 20, 10]
        self.assertEqual(pq.peek(1), 20)
        self.assertEqual(pq.take(), 20)
        self.assertEqual(pq.take(), 10)

    def test_exception_empty(self):
        pq = PriorityQueueBinaryHeap()
        with self.assertRaises(PQException):
            pq.take()

    def test_initial_data(self):
        data = [1, 5, 3, 9, 2]
        pq = PriorityQueueBinaryHeap(initial_data=data)
        self.assertEqual(pq.size(), 5)
        self.assertEqual(pq.take(), 9)

if __name__ == '__main__':
    unittest.main()
