from src.adt.bqs.lazy_list import LazyList


class TestLazyList:
    def test_from_start(self):
        lazy = LazyList.from_start(1)
        assert lazy.head == 1
        taken = lazy.take(3)
        assert taken == [1, 2, 3]

    def test_take_while(self):
        lazy = LazyList.from_start(1)
        taken = lazy.take_while(lambda x: x < 5)
        assert taken == [1, 2, 3, 4]

    def test_map(self):
        lazy = LazyList.from_start(1)
        mapped = LazyList.map(lazy, lambda x: x * 2)
        taken = mapped.take(3)
        assert taken == [2, 4, 6]
