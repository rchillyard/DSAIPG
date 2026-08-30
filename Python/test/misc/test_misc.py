"""
Tests for the classes at the root of misc.
"""

from __future__ import annotations

import math

import pytest

from src.misc.binary_search import binary_search
from src.misc.complex_number import Complex
from src.misc.counter import Counter
from src.misc.my_date import MyDate
from src.misc.mystery import mystery
from src.misc.tail_call import call, done


class TestBinarySearch:
    xs = [1, 2, 3, 4, 5, 6, 7, 8, 9]

    def test_sequence(self):
        assert binary_search(self.xs, 0, len(self.xs), 3) == 2

    def test_every_element_is_findable(self):
        # including the first and the last, which is what pins down the half-open
        # range: narrowing to mid - 1 rather than mid would lose the low end
        for i, x in enumerate(self.xs):
            assert binary_search(self.xs, 0, len(self.xs), x) == i, f"looking for {x}"

    def test_missing_too_large(self):
        assert binary_search(self.xs, 0, len(self.xs), 11) == -1

    def test_missing_too_small(self):
        assert binary_search(self.xs, 0, len(self.xs), 0) == -1

    def test_singleton(self):
        assert binary_search([1], 0, 1, 1) == 0

    def test_empty(self):
        assert binary_search([], 0, 0, 1) == -1

    def test_out_of_order_is_not_found(self):
        # binary search on unsorted input is meaningless, and reports a miss
        assert binary_search([9, 8, 7, 6, 5, 4, 3, 2, 1], 0, 9, 3) == -1

    def test_a_sub_range(self):
        assert binary_search(self.xs, 4, len(self.xs), 3) == -1, "3 is below the range"
        assert binary_search(self.xs, 4, len(self.xs), 7) == 6

    def test_a_longer_list(self):
        xs = list(range(0, 2000, 2))
        for i, x in enumerate(xs):
            assert binary_search(xs, 0, len(xs), x) == i
        assert binary_search(xs, 0, len(xs), 1) == -1, "odd numbers are not there"


class TestCounter:
    def test_counting(self):
        c = Counter("heads")
        assert c.tally() == 0
        for _ in range(3):
            c.increment()
        assert c.tally() == 3

    def test_str(self):
        c = Counter("tails")
        c.increment()
        assert str(c) == "tails: 1"

    def test_two_counters_are_independent(self):
        heads, tails = Counter("heads"), Counter("tails")
        heads.increment()
        assert (heads.tally(), tails.tally()) == (1, 0)


class TestMystery:
    def test_it_reverses(self):
        assert mystery("The quick brown fox") == "The quick brown fox"[::-1]

    def test_short_strings(self):
        assert mystery("") == ""
        assert mystery("a") == "a"
        assert mystery("ab") == "ba"

    def test_twice_is_the_identity(self):
        for s in ("", "a", "abc", "abcd", "a longer string with spaces"):
            assert mystery(mystery(s)) == s


class TestComplex:
    def test_parts(self):
        z = Complex(3.0, 4.0)
        assert (z.real, z.imag) == (3.0, 4.0)

    def test_a_real_number(self):
        assert Complex(3.0).imag == 0.0

    def test_equality(self):
        assert Complex(1.0, 2.0) == Complex(1.0, 2.0)
        assert Complex(1.0, 2.0) != Complex(2.0, 1.0)


class TestMyDate:
    def test_ordering(self):
        assert MyDate(2024, 1, 1).compare_to(MyDate(2024, 1, 1)) == 0
        assert MyDate(2023, 12, 31).compare_to(MyDate(2024, 1, 1)) == -1
        assert MyDate(2024, 2, 1).compare_to(MyDate(2024, 1, 31)) == 1
        assert MyDate(2024, 1, 2).compare_to(MyDate(2024, 1, 1)) == 1

    def test_sorting(self):
        dates = [MyDate(2024, 3, 1), MyDate(2023, 12, 31), MyDate(2024, 1, 15)]
        assert [str(d) for d in sorted(dates)] == ["2023-12-31", "2024-1-15", "2024-3-1"]

    def test_equality_and_hashing(self):
        assert MyDate(2024, 1, 1) == MyDate(2024, 1, 1)
        assert hash(MyDate(2024, 1, 1)) == hash(MyDate(2024, 1, 1))
        assert MyDate(2024, 1, 1) != MyDate(2024, 1, 2)

    def test_day_of_week(self):
        # 29 August 2026 is a Saturday
        assert MyDate(2026, 8, 29).get_day_of_week() == 6
        assert MyDate(2026, 8, 31).get_day_of_week() == 1, "Monday is 1"

    def test_day_of_week_is_remembered(self):
        # worked out once and kept, which is what the field is for
        d = MyDate(2026, 8, 29)
        assert d.get_day_of_week() == d.get_day_of_week() == 6

    def test_the_cached_day_does_not_affect_equality(self):
        asked, unasked = MyDate(2024, 1, 1), MyDate(2024, 1, 1)
        asked.get_day_of_week()
        assert asked == unasked
        assert hash(asked) == hash(unasked)

    def test_getters(self):
        d = MyDate(2024, 6, 15)
        assert (d.get_year(), d.get_month(), d.get_day()) == (2024, 6, 15)


class TestTailCall:
    """
    Trampolining: a step returns the next step rather than calling it, so the
    recursion can be as deep as you like without the stack growing.
    """

    @staticmethod
    def factorial(n: int, acc: int = 1):
        return done(acc) if n <= 1 else call(lambda: TestTailCall.factorial(n - 1, acc * n))

    def test_a_small_case(self):
        assert self.factorial(5).invoke() == 120

    def test_the_base_case(self):
        assert self.factorial(1).invoke() == 1
        assert done(42).invoke() == 42

    def test_deeper_than_the_stack_allows(self):
        # the whole point: ten thousand steps, and the stack never grows
        assert self.factorial(10_000).invoke() == math.factorial(10_000)

    def test_plain_recursion_would_not_survive_it(self):
        def plain(n: int, acc: int = 1) -> int:
            return acc if n <= 1 else plain(n - 1, acc * n)

        with pytest.raises(RecursionError):
            plain(10_000)

    def test_asking_an_unfinished_call_for_its_result(self):
        with pytest.raises(RuntimeError):
            call(lambda: done(1)).result()

    def test_asking_a_finished_call_for_the_next_step(self):
        with pytest.raises(RuntimeError):
            done(1).get()
