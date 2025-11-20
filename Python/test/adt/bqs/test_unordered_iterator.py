from src.adt.bqs.unordered_iterator import UnorderedIterator


class TestUnorderedIterator:
    def test_iteration(self):
        items = [1, 2, 3, 4, 5]
        iterator = UnorderedIterator(items)
        result = list(iterator)
        assert len(result) == 5
        assert set(result) == set(items)
        # Order might be different, but hard to test randomness deterministically without seed.

    def test_deterministic(self):
        items = [1, 2, 3, 4, 5]
        # Same seed should produce same order
        iter1 = UnorderedIterator.create_deterministic(items, 42)
        iter2 = UnorderedIterator.create_deterministic(items, 42)

        list1 = list(iter1)
        list2 = list(iter2)

        assert list1 == list2
