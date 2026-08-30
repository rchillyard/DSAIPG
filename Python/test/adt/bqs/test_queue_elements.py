from src.adt.bqs.queue_elements import QueueElements

# These tests mirror QueueTest.java.  They assert FIFO behaviour, so all but
# the first fail until the `TO BE IMPLEMENTED` bodies of `offer` and `poll`
# have been written.  That is the intended state of the exercise.


class TestQueueElements:
    def test_empty(self):
        q = QueueElements[int]()
        assert q.is_empty()
        assert q.poll() is None
        assert q.is_empty()
        assert len(q) == 0

    def test_offer_then_poll(self):
        q = QueueElements[int]()
        assert q.is_empty()
        q.offer(1)
        assert not q.is_empty()
        assert q.poll() == 1
        assert q.is_empty()

    def test_offer_two_then_poll_in_order(self):
        q = QueueElements[int]()
        q.offer(1)
        q.offer(2)
        assert not q.is_empty()
        assert q.poll() == 1
        assert q.poll() == 2
        assert q.is_empty()

    def test_interleaved_offer_and_poll(self):
        q = QueueElements[int]()
        q.offer(1)
        q.offer(2)
        assert not q.is_empty()
        assert q.poll() == 1
        q.offer(3)
        q.offer(4)
        assert q.poll() == 2
        assert q.poll() == 3
        assert q.poll() == 4
        assert q.is_empty()
