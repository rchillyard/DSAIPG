import pytest
from src.adt.bqs.queue_array import QueueArray


class TestQueueArray:
    def test_poll_empty(self):
        queue = QueueArray[int](capacity=7)
        assert queue.poll() is None
        queue.offer(1)
        assert queue.poll() == 1

    def test_is_empty(self):
        queue = QueueArray[int](capacity=7)
        assert queue.is_empty()
        queue.offer(1)
        assert not queue.is_empty()

    def test_iterator(self):
        queue = QueueArray[int](capacity=5)
        queue.offer(1)
        queue.offer(2)
        queue.offer(3)
        queue.offer(4)

        iterator = iter(queue)
        assert next(iterator) == 1
        assert next(iterator) == 2
        assert next(iterator) == 3
        assert next(iterator) == 4
        with pytest.raises(StopIteration):
            next(iterator)

    def test_size(self):
        queue = QueueArray[int](capacity=7)
        assert queue.size() == 0
        queue.offer(1)
        assert queue.size() == 1

    def test_offer_basic(self):
        queue = QueueArray[int](capacity=7)
        for i in range(1, 8):
            queue.offer(i)
        assert list(queue) == [1, 2, 3, 4, 5, 6, 7]

    def test_offer_resize(self):
        queue = QueueArray[int](capacity=7)
        for i in range(1, 9):
            queue.offer(i)
        assert queue.size() == 8
        assert list(queue) == [1, 2, 3, 4, 5, 6, 7, 8]

    def test_offer_wrap_around(self):
        # Create a queue with capacity 5
        queue = QueueArray[int](capacity=5)
        # Add 4 items
        queue.offer(10)
        queue.offer(11)
        queue.offer(12)
        queue.offer(13)
        # Remove 2 items to move head
        queue.poll()
        queue.poll()
        # Add 2 more items to wrap around
        queue.offer(14)
        queue.offer(15)

        assert queue.size() == 4
        assert list(queue) == [12, 13, 14, 15]

        # Trigger resize from wrapped state
        queue.offer(16)

        assert list(queue) == [12, 13, 14, 15, 16]
