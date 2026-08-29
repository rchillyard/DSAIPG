"""
Tests for misc/greedy: the greedy method, and Zeckendorf's representation, which
is the case where greedy is provably right.
"""

from __future__ import annotations

from src.misc.greedy.fibonacci import Fibonacci
from src.misc.greedy.greedy import Greedy
from src.misc.greedy.zeckendorf import Zeckendorf


class TestFibonacci:
    def test_it_starts_with_two_ones(self):
        f = Fibonacci()
        assert f.size() == 2
        assert (f.fib(0), f.fib(1)) == (1, 1)

    def test_extend_doubles_the_series(self):
        f = Fibonacci()
        f._extend()
        assert f.size() == 4
        assert (f.fib(2), f.fib(3)) == (2, 3)
        f._extend()
        assert f.size() == 8
        assert [f.fib(i) for i in range(4, 8)] == [5, 8, 13, 21]

    def test_ensure_grows_only_as_far_as_it_must(self):
        # the sizes are the Java's, which is what makes the doubling visible
        f = Fibonacci()
        for value, size in [(1, 2), (2, 4), (3, 4), (4, 8), (5, 8)]:
            f.ensure(value)
            assert f.size() == size, f"after ensure({value})"

    def test_get_largest(self):
        f = Fibonacci()
        assert f.get_largest(1) == 1
        f.ensure(2)
        assert f.get_largest(2) == 2
        assert f.get_largest(4) == 3
        f.ensure(5)
        assert f.get_largest(6) == 5

    def test_the_series_is_right_a_long_way_out(self):
        f = Fibonacci()
        f.ensure(1_000_000)
        for i in range(2, f.size()):
            assert f.fib(i) == f.fib(i - 1) + f.fib(i - 2)


class TestZeckendorf:
    """
    Every positive integer is the sum of non-consecutive Fibonacci numbers, in
    exactly one way, and taking the largest that fits at each step finds it.
    """

    def test_the_javas_four_cases(self):
        assert Zeckendorf().get(10) == [8, 2]
        assert Zeckendorf().get(100) == [89, 8, 3]
        assert Zeckendorf().get(1000) == [987, 13]
        assert Zeckendorf().get(10000) == [6765, 2584, 610, 34, 5, 2]

    def test_small_values(self):
        assert Zeckendorf().get(1) == [1]
        assert Zeckendorf().get(2) == [2]
        assert Zeckendorf().get(3) == [3]
        assert Zeckendorf().get(4) == [3, 1]

    def test_nothing_makes_nothing(self):
        assert Zeckendorf().get(0) == []

    def test_every_representation_sums_to_its_value(self):
        z = Zeckendorf()
        for x in range(1, 200):
            assert sum(z.get(x)) == x

    def test_no_two_are_consecutive_fibonacci_numbers(self):
        # this is the property that makes the representation unique
        z = Zeckendorf()
        fibs = [1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144]
        for x in range(1, 200):
            indices = [fibs.index(f) for f in z.get(x)]
            assert all(a - b >= 2 for a, b in zip(indices, indices[1:])), x


class TestGreedy:
    def test_zeckendorf_by_the_general_greedy_method(self):
        # the same answers as Zeckendorf, reached through Greedy's shape rather
        # than a loop written out
        fibonacci = Fibonacci()

        def run(x: int) -> list[int]:
            fibonacci.ensure(x)
            return Greedy(
                fibonacci.get_largest,
                lambda total, taken: total - taken,
                lambda taken, so_far: [*so_far, taken],
                lambda total: total <= 0,
            ).run(x, [])

        assert run(10) == [8, 2]
        assert run(100) == [89, 8, 3]
        assert run(1000) == [987, 13]

    def test_it_stops_at_once_if_there_is_nothing_to_do(self):
        never_called = Greedy(
            lambda t: 1 / 0,
            lambda t, g: 1 / 0,
            lambda g, r: 1 / 0,
            lambda t: True,
        )
        assert never_called.run(99, "untouched") == "untouched"

    def test_greedy_is_not_always_right(self):
        # 6 from denominations 1, 3, 4: greedy takes 4 and then two 1s, where
        # 3 + 3 would do. The shape is sound; whether it gives the right answer
        # is a property of the problem, which is the point of the class.
        coins = [4, 3, 1]
        result = Greedy(
            lambda remaining: next(c for c in coins if c <= remaining),
            lambda remaining, coin: remaining - coin,
            lambda coin, so_far: [*so_far, coin],
            lambda remaining: remaining <= 0,
        ).run(6, [])
        assert result == [4, 1, 1]
        assert sum(result) == 6
        assert len(result) > 2, "and 3 + 3 would have been better"
