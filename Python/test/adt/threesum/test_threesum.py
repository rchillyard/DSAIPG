import dataclasses
import random
from random import Random

import pytest

from src.adt.threesum.pair import Pair
from src.adt.threesum.source import Source
from src.adt.threesum.three_sum_benchmark import (
    TIME_LOGGERS_CUBIC,
    TIME_LOGGERS_QUADRATIC,
    TIME_LOGGERS_QUADRITHMIC,
    ThreeSumBenchmark,
)
from src.adt.threesum.three_sum_cubic import ThreeSumCubic
from src.adt.threesum.three_sum_quadratic import ThreeSumQuadratic
from src.adt.threesum.three_sum_quadrithmic import ThreeSumQuadrithmic
from src.adt.threesum.triple import Triple
from src.adt.threesum.two_sum_benchmark import TwoSumBenchmark
from src.adt.threesum.two_sum_quadratic import TwoSumQuadratic
from src.adt.threesum.two_sum_with_calipers import TwoSumWithCalipers, calipers

# NOTE the Java tests pin exact counts against data from Source seeded with a
# given long, for instance "Source(10, 15, 2L) gives one triple". Those numbers
# cannot be reproduced here: Source uses java.util.Random, whose sequence is not
# Python's. Where a Java test depends on the seed, the Python test checks the
# same property against the cubic implementation instead -- which is a better
# test anyway, since it compares an algorithm against a known-good one rather
# than against a number someone once observed.


def sorted_distinct(n: int, seed: int, bound: int = 200) -> list[int]:
    """Sorted distinct values spanning zero, which is what the fast sorts need."""
    rng = random.Random(seed)
    return sorted({rng.randrange(-bound, bound) for _ in range(n)})


class TestPair:
    def test_sum(self):
        assert Pair(3, -3).sum() == 0
        assert Pair(2, 5).sum() == 7

    def test_equality(self):
        assert Pair(1, 2) == Pair(1, 2)
        assert Pair(1, 2) != Pair(2, 1)

    def test_it_is_hashable(self):
        # Deduplication is done with a set, so this is load-bearing.
        assert len({Pair(1, 2), Pair(1, 2), Pair(3, 4)}) == 2

    def test_ordering_is_by_x_then_y(self):
        assert Pair(1, 9) < Pair(2, 0)
        assert Pair(1, 1) < Pair(1, 2)

    def test_it_is_immutable(self):
        with pytest.raises(dataclasses.FrozenInstanceError):
            Pair(1, 2).x = 3

    def test_str(self):
        assert str(Pair(1, 2)) == "Pair{x=1, y=2}"


class TestTriple:
    def test_sum(self):
        assert Triple(-1, -2, 3).sum() == 0

    def test_equality_and_hashing(self):
        assert Triple(1, 2, 3) == Triple(1, 2, 3)
        assert len({Triple(1, 2, 3), Triple(1, 2, 3)}) == 1

    def test_ordering_is_by_x_then_y_then_z(self):
        assert Triple(1, 2, 3) < Triple(1, 2, 4)
        assert Triple(1, 2, 9) < Triple(1, 3, 0)
        assert Triple(0, 9, 9) < Triple(1, 0, 0)

    def test_str(self):
        assert str(Triple(1, 2, 3)) == "Triple{x=1, y=2, z=3}"


class TestSource:
    def test_it_gives_exactly_n_values(self):
        assert len(Source(20, 10, seed=0).ints_supplier(10)()) == 20

    def test_the_values_are_distinct(self):
        # This is the whole reason for the safety factor: generating n values
        # directly would leave far fewer than n after deduplication.
        ints = Source(15, 10, seed=0).ints_supplier(5)()
        assert len(set(ints)) == 15

    def test_the_values_are_sorted(self):
        ints = Source(25, 20, seed=12345).ints_supplier(8)()
        assert ints == sorted(ints)

    def test_the_same_seed_gives_the_same_values(self):
        supplier1 = Source(10, 5, seed=98765).ints_supplier(3)
        supplier2 = Source(10, 5, seed=98765).ints_supplier(3)
        assert supplier1() == supplier2()

    def test_different_seeds_give_different_values(self):
        assert Source(20, 50, seed=1).ints_supplier(10)() \
               != Source(20, 50, seed=2).ints_supplier(10)()

    def test_the_values_straddle_zero(self):
        # They have to: a three-sum over positive values only has no solutions.
        ints = Source(50, 100, seed=3).ints_supplier(10)()
        assert min(ints) < 0 < max(ints)

    def test_the_supplier_gives_fresh_values_each_time(self):
        # Each benchmark run must sort its own data, not data an earlier run
        # already put in order.
        supplier = Source(20, 50, seed=4).ints_supplier(10)
        assert supplier() != supplier()

    def test_an_explicit_random_takes_precedence_over_a_seed(self):
        first = Source(10, 20, seed=1, random=Random(7)).ints_supplier(10)()
        second = Source(10, 20, seed=2, random=Random(7)).ints_supplier(10)()
        assert first == second


class TestThreeSumCubic:
    """The reference implementation: slow, but it assumes nothing."""

    def test_a_known_list(self):
        ints = sorted([30, -40, -20, -10, 40, 0, 10, 5])
        assert len(ThreeSumCubic(ints).get_triples()) == 4

    def test_it_finds_the_same_solutions_in_an_unsorted_list(self):
        unsorted = [30, -40, -20, -10, 40, 0, 10, 5]
        assert {frozenset((t.x, t.y, t.z)) for t in ThreeSumCubic(unsorted).get_triples()} \
               == {frozenset((t.x, t.y, t.z))
                   for t in ThreeSumCubic(sorted(unsorted)).get_triples()}

    def test_but_the_triples_themselves_are_only_canonical_when_sorted(self):
        # A Triple records its elements in the order they were found, so an
        # unsorted list gives Triple(-40, 40, 0) where a sorted one gives
        # Triple(-40, 0, 40). They are the same solution but not the same Triple,
        # and deduplication will not collapse them -- so "may be randomly ordered"
        # means the solutions are all found, not that the answer looks the same.
        unsorted = [30, -40, -20, -10, 40, 0, 10, 5]
        assert Triple(-40, 40, 0) in ThreeSumCubic(unsorted).get_triples()
        assert Triple(-40, 0, 40) in ThreeSumCubic(sorted(unsorted)).get_triples()

    def test_duplicates_are_reported_once(self):
        # Three ways to pick the same values, but one Triple.
        assert ThreeSumCubic([-2, -2, 0, 2, 2]).get_triples() == [Triple(-2, 0, 2)]

    def test_an_empty_list(self):
        assert ThreeSumCubic([]).get_triples() == []

    def test_a_list_with_no_solution(self):
        assert ThreeSumCubic([1, 2, 3, 4]).get_triples() == []

    def test_three_zeros(self):
        assert ThreeSumCubic([0, 0, 0]).get_triples() == [Triple(0, 0, 0)]

    def test_the_result_is_ordered(self):
        ints = sorted_distinct(60, seed=1)
        triples = ThreeSumCubic(ints).get_triples()
        assert triples == sorted(triples)

    def test_every_triple_sums_to_zero(self):
        for triple in ThreeSumCubic(sorted_distinct(60, seed=2)).get_triples():
            assert triple.sum() == 0


class TestThreeSumQuadratic:
    """
    NOTE get_triples_with_middle is an exercise, so most of these are reported as
    skipped until it is written.
    """

    def test_the_middle_of_three(self):
        assert len(ThreeSumQuadratic([-2, 0, 2]).get_triples_with_middle(1)) == 1

    def test_a_middle_with_two_solutions(self):
        ints = sorted([30, -40, -20, -10, 40, 0, 10, 5])
        # ints[3] is 0, and both -40 + 40 and -10 + 10 straddle it.
        assert len(ThreeSumQuadratic(ints).get_triples_with_middle(3)) == 2

    def test_a_middle_at_the_end_has_no_solutions(self):
        ints = [-2, 0, 2]
        assert ThreeSumQuadratic(ints).get_triples_with_middle(0) == []
        assert ThreeSumQuadratic(ints).get_triples_with_middle(2) == []

    def test_a_known_list(self):
        ints = sorted([30, -40, -20, -10, 40, 0, 10, 5])
        assert len(ThreeSumQuadratic(ints).get_triples()) == 4

    def test_a_list_with_one_solution(self):
        ints = [-38, -23, -15, -12, -6, 17, 18, 37, 42, 43]
        assert ThreeSumQuadratic(ints).get_triples() == [Triple(-12, -6, 18)]

    def test_an_empty_list(self):
        assert ThreeSumQuadratic([]).get_triples() == []

    def test_it_agrees_with_the_cubic_implementation(self):
        # The real test: same answer as the implementation which assumes nothing,
        # over data neither was written against.
        for seed in range(5):
            ints = sorted_distinct(80, seed=seed)
            assert ThreeSumQuadratic(ints).get_triples() \
                   == ThreeSumCubic(ints).get_triples()


class TestThreeSumQuadrithmic:
    """
    NOTE get_triple is an exercise, so most of these are reported as skipped until
    it is written.
    """

    def test_the_smallest_list_with_a_solution(self):
        assert ThreeSumQuadrithmic([-2, 0, 2]).get_triples() == [Triple(-2, 0, 2)]

    def test_a_known_list(self):
        ints = sorted([30, -40, -20, -10, 40, 0, 10, 5])
        assert len(ThreeSumQuadrithmic(ints).get_triples()) == 4

    def test_an_empty_list(self):
        assert ThreeSumQuadrithmic([]).get_triples() == []

    def test_a_list_with_no_solution(self):
        assert ThreeSumQuadrithmic([1, 2, 3, 4]).get_triples() == []

    def test_the_third_element_must_lie_beyond_the_second(self):
        # -2 + 0 + 2 is found from the pair (-2, 0); the pair (0, 2) must not
        # find it again by searching backwards to -2.
        target = ThreeSumQuadrithmic([-2, 0, 2])
        assert target.get_triple(0, 1) == Triple(-2, 0, 2)
        assert target.get_triple(1, 2) is None

    def test_a_pair_with_no_completion(self):
        assert ThreeSumQuadrithmic([1, 2, 3, 4]).get_triple(0, 1) is None

    def test_it_agrees_with_the_cubic_implementation(self):
        for seed in range(5):
            ints = sorted_distinct(80, seed=seed)
            assert ThreeSumQuadrithmic(ints).get_triples() \
                   == ThreeSumCubic(ints).get_triples()


class TestTwoSumQuadratic:
    def test_a_known_list(self):
        # There is only one zero, and a pair needs two indices, so (0, 0) is not
        # among them.
        assert TwoSumQuadratic([-2, -1, 0, 1, 2]).get_pairs() \
               == [Pair(-2, 2), Pair(-1, 1)]

    def test_the_pairs_sum_to_zero(self):
        for pair in TwoSumQuadratic(sorted_distinct(60, seed=6)).get_pairs():
            assert pair.sum() == 0

    def test_it_finds_the_same_solutions_in_an_unsorted_list(self):
        # As with ThreeSumCubic, a Pair records its elements in the order found,
        # so the answer here is Pair(3, -3) rather than Pair(-3, 3).
        assert TwoSumQuadratic([3, -1, -3, 1, 7]).get_pairs() \
               == [Pair(-1, 1), Pair(3, -3)]
        assert TwoSumQuadratic([-3, -1, 1, 3, 7]).get_pairs() \
               == [Pair(-3, 3), Pair(-1, 1)]

    def test_an_empty_list(self):
        assert TwoSumQuadratic([]).get_pairs() == []

    def test_no_solution(self):
        assert TwoSumQuadratic([1, 2, 3]).get_pairs() == []

    def test_duplicates_are_reported_once(self):
        assert TwoSumQuadratic([-1, -1, 1, 1]).get_pairs() == [Pair(-1, 1)]


class TestTwoSumWithCalipers:
    """
    NOTE calipers is an exercise, so most of these are reported as skipped until
    it is written.
    """

    def test_a_known_list(self):
        assert TwoSumWithCalipers([-3, -1, 1, 3, 7]).get_pairs() \
               == [Pair(-3, 3), Pair(-1, 1)]

    def test_an_empty_list(self):
        assert TwoSumWithCalipers([]).get_pairs() == []

    def test_a_single_value(self):
        assert TwoSumWithCalipers([5]).get_pairs() == []

    def test_no_solution(self):
        assert TwoSumWithCalipers([1, 2, 3]).get_pairs() == []

    def test_it_agrees_with_the_quadratic_implementation(self):
        for seed in range(5):
            ints = sorted_distinct(80, seed=seed)
            assert TwoSumWithCalipers(ints).get_pairs() \
                   == TwoSumQuadratic(ints).get_pairs()

    def test_the_function_decides_what_is_wanted(self):
        # The calipers are not specific to summing to zero: any function which
        # says "too big", "too small" or "just right" will do. Here it looks for
        # pairs summing to 10.
        found = calipers([1, 2, 3, 7, 8, 9], lambda p: p.sum() - 10)
        assert [(p.x, p.y) for p in found] == [(1, 9), (2, 8), (3, 7)]

    def test_the_result_is_already_in_order(self):
        # It is, because the lower index only ever rises -- which is why the Java
        # gets away with sorting its result list before filling it.
        assert calipers([-3, -1, 1, 3], Pair.sum) == [Pair(-3, 3), Pair(-1, 1)]


class TestThreeSumBenchmark:
    """
    NOTE benchmark_three_sum is an exercise, so the tests which actually run a
    benchmark are reported as skipped until it is written.
    """

    def test_it_runs_on_a_small_input(self):
        ThreeSumBenchmark(2, 40, 40, seed=0).run_benchmarks()

    def test_the_cubic_implementation_is_skipped_above_its_limit(self):
        # This passes even before the exercise is written, because the size check
        # comes first -- which is the behaviour being tested.
        ThreeSumBenchmark(1, 8000, 8000, seed=0).benchmark_three_sum(
            "ThreeSumCubic", lambda xs: None, 8000, TIME_LOGGERS_CUBIC)

    def test_each_set_of_loggers_has_a_raw_and_a_normalized_entry(self):
        for loggers in [TIME_LOGGERS_CUBIC, TIME_LOGGERS_QUADRITHMIC,
                        TIME_LOGGERS_QUADRATIC]:
            assert len(loggers) == 2

    def test_the_loggers_do_not_raise(self):
        for loggers in [TIME_LOGGERS_CUBIC, TIME_LOGGERS_QUADRITHMIC,
                        TIME_LOGGERS_QUADRATIC]:
            for logger in loggers:
                logger.log("3-SUM", 1.5, 1000)


class TestTwoSumBenchmark:
    """
    NOTE benchmark_two_sum is an exercise, so the tests which actually run a
    benchmark are reported as skipped until it is written.
    """

    def test_it_runs_on_a_small_input(self):
        TwoSumBenchmark(2, 50, 50, seed=0).run_benchmarks()

    def test_nothing_is_benchmarked_above_the_limit(self):
        # As in the Java, this passes even before the exercise is written.
        TwoSumBenchmark(1, 16000, 16000, seed=0).run_benchmarks()
