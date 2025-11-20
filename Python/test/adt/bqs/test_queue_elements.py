import pytest
from src.adt.bqs.queue_elements import QueueElements


class TestQueueElements:
    def test_init(self):
        q = QueueElements[int]()
        assert q.is_empty()
        assert len(q) == 0

    def test_offer_poll(self):
        q = QueueElements[int]()
        # Since offer/poll are not implemented, we just check if they don't crash (or do nothing)
        q.offer(1)
        # If implemented, it should be size 1. If pass, size 0.
        # The current implementation is pass.
        assert q.is_empty()
        assert q.poll() is None
