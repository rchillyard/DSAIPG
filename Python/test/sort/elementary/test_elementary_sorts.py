import random

import pytest

from src.sort.elementary.bubble_sort import BubbleSort
from src.sort.elementary.insertion_sort import InsertionSort, InsertionSortOpt
from src.sort.elementary.insertion_sort_basic import InsertionSortBasic
from src.sort.elementary.insertion_sort_comparator import (
    InsertionSortComparator,
    count_inversions_by_sorting,
    string_sorter_case_insensitive,
)
from src.sort.elementary.selection_sort import SelectionSort
from src.sort.helper.helper import natural_comparison
from src.sort.helper.helper_exception import HelperException
from src.sort.helper.instrumented_helper import count_inversions
from src.util.config.config_benchmark import setup_config, setup_config_fixes

# These mirror InsertionSortTest, BubbleSortTest, SelectionSortTest and
# InsertionSortComparatorTest.

INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")
PLAIN = setup_config("false", "", "0", "0", "", "")

#: Every sort in this tranche that sorts through a Helper.
SORTS = [InsertionSort, InsertionSortOpt, InsertionSortComparator, BubbleSort, SelectionSort]


def sorter(cls, n, config=PLAIN, comparator=None):
    """Build a sorter of the given class from configuration."""
    return cls.from_config(cls.__name__, config, n, comparator=comparator)


class TestEverySortSorts:
    """
    The same cases for every sort, because a sort that gets any of these wrong is
    not a sort. InsertionSortComparator carries an exercise, so until it is
    written its rows here are reported as skipped rather than failed.
    """

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_small_list(self, cls):
        xs = [3, 1, 4, 1, 5, 9, 2, 6]
        assert sorter(cls, len(xs)).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_it_leaves_the_original_alone(self, cls):
        xs = [3, 1, 2]
        sorter(cls, 3).sort(xs)
        assert xs == [3, 1, 2]

    @pytest.mark.parametrize("cls", SORTS)
    def test_an_empty_list(self, cls):
        assert sorter(cls, 0).sort([]) == []

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_single_element(self, cls):
        assert sorter(cls, 1).sort([1]) == [1]

    @pytest.mark.parametrize("cls", SORTS)
    def test_an_already_sorted_list(self, cls):
        xs = list(range(20))
        assert sorter(cls, 20).sort(xs) == xs

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_reversed_list(self, cls):
        assert sorter(cls, 20).sort(list(range(19, -1, -1))) == list(range(20))

    @pytest.mark.parametrize("cls", SORTS)
    def test_all_equal(self, cls):
        assert sorter(cls, 10).sort([7] * 10) == [7] * 10

    @pytest.mark.parametrize("cls", SORTS)
    def test_duplicates(self, cls):
        xs = [5, 1, 5, 1, 3, 3, 5]
        assert sorter(cls, len(xs)).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_random_list(self, cls):
        rng = random.Random(42)
        xs = [rng.randint(0, 999) for _ in range(100)]
        assert sorter(cls, 100).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_strings(self, cls):
        xs = ["pear", "apple", "fig", "date"]
        assert sorter(cls, 4).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_sorting_a_range_leaves_the_rest_alone(self, cls):
        xs = [9, 3, 1, 2, 9]
        sorter(cls, 5).sort_range(xs, 1, 4)
        assert xs == [9, 1, 2, 3, 9]

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_custom_comparator_is_honoured(self, cls):
        # Descending. This is the case that caught QuickSort_3way ignoring the
        # comparator on its uninstrumented path.
        reverse = lambda v, w: natural_comparison(w, v)  # noqa: E731
        s = sorter(cls, 5, comparator=reverse)
        assert s.sort([1, 5, 3, 2, 4]) == [5, 4, 3, 2, 1]

    @pytest.mark.parametrize("cls", SORTS)
    def test_it_sorts_when_instrumented_too(self, cls):
        xs = [3, 1, 4, 1, 5]
        assert sorter(cls, len(xs), INSTRUMENTED).sort(xs) == sorted(xs)


class TestBubbleSort:
    def test_it_stops_early_on_a_sorted_list(self):
        # The early exit is the whole point of bubble sort: one pass over an
        # already sorted list, rather than n of them.
        s = sorter(BubbleSort, 20, INSTRUMENTED)
        s.sort(list(range(20)))
        helper = s.get_helper()
        assert helper.get_swaps() == 0
        assert helper.get_compares() == 19, "one pass, and no more"

    def test_a_reversed_list_needs_every_pass(self):
        s = sorter(BubbleSort, 10, INSTRUMENTED)
        s.sort(list(range(9, -1, -1)))
        # n(n-1)/2 inversions, and bubble sort fixes one per exchange.
        assert s.get_helper().get_swaps() == 45


class TestSelectionSort:
    def test_it_makes_at_most_n_minus_one_exchanges(self):
        # The point of selection sort: many comparisons, very few exchanges.
        s = sorter(SelectionSort, 20, INSTRUMENTED)
        s.sort(list(range(19, -1, -1)))
        assert s.get_helper().get_swaps() <= 19

    def test_it_makes_no_exchange_on_a_sorted_list(self):
        s = sorter(SelectionSort, 20, INSTRUMENTED)
        s.sort(list(range(20)))
        assert s.get_helper().get_swaps() == 0

    def test_locate_minimum(self):
        s = sorter(SelectionSort, 5)
        assert s.locate_minimum([5, 3, 1, 4], 0, 4, s.get_helper()) == 2

    def test_locate_minimum_over_a_range(self):
        s = sorter(SelectionSort, 5)
        assert s.locate_minimum([0, 3, 1, 4], 1, 4, s.get_helper()) == 2


class TestInsertionSortIsAdaptive:
    def test_a_sorted_list_costs_one_comparison_per_element(self):
        s = sorter(InsertionSort, 20, INSTRUMENTED)
        s.sort(list(range(20)))
        assert s.get_helper().get_swaps() == 0
        assert s.get_helper().get_compares() == 19

    def test_the_number_of_exchanges_is_the_number_of_inversions(self):
        # This is the property that makes insertion sort the natural way to
        # count inversions.
        rng = random.Random(7)
        xs = [rng.randint(0, 99) for _ in range(40)]
        s = sorter(InsertionSort, 40, INSTRUMENTED)
        s.sort(xs)
        assert s.get_helper().get_swaps() == count_inversions(xs, natural_comparison)


class TestInsertionSortOpt:
    def test_it_makes_fewer_comparisons_than_plain_insertion_sort(self):
        # Binary search finds the place in log n comparisons rather than n.
        rng = random.Random(11)
        xs = [rng.randint(0, 999) for _ in range(200)]
        plain = sorter(InsertionSort, 200, INSTRUMENTED)
        plain.sort(xs)
        opt = sorter(InsertionSortOpt, 200, INSTRUMENTED)
        opt.sort(xs)
        assert opt.get_helper().get_compares() < plain.get_helper().get_compares()

    def test_it_moves_the_same_number_of_elements(self):
        # It saves comparisons, not movement: the elements above the insertion
        # point still have to shift up, one move per inversion.
        rng = random.Random(11)
        xs = rng.sample(range(10000), 200)  # distinct, so equal elements do not confuse the count
        opt = sorter(InsertionSortOpt, 200, INSTRUMENTED)
        opt.sort(xs)
        assert opt.get_helper().get_copies() == count_inversions(xs, natural_comparison)

    def test_it_is_stable(self):
        # Insertion sort is stable, and the optimised version must be too. It was
        # not: swap_into_sorted placed each element before the run of elements
        # equal to it rather than after, so equal elements came out reversed.
        by_key = lambda v, w: natural_comparison(v[0], w[0])  # noqa: E731
        pairs = [(1, "a"), (0, "b"), (1, "c"), (0, "d"), (1, "e")]
        expected = [(0, "b"), (0, "d"), (1, "a"), (1, "c"), (1, "e")]
        assert sorter(InsertionSortOpt, 5, INSTRUMENTED, comparator=by_key).sort(pairs) == expected
        assert sorter(InsertionSort, 5, INSTRUMENTED, comparator=by_key).sort(pairs) == expected

    def test_equal_elements_cost_no_extra_moves(self):
        # Every element moved is one inversion fixed, and none is moved that need
        # not be -- so this holds with duplicates too. Before the sort was made
        # stable it moved 10116 elements against 8911 inversions here, because
        # each was moved past the ones equal to it.
        rng = random.Random(11)
        xs = [rng.randint(0, 9) for _ in range(200)]
        opt = sorter(InsertionSortOpt, 200, INSTRUMENTED)
        opt.sort(xs)
        assert opt.get_helper().get_copies() == count_inversions(xs, natural_comparison)


class TestInsertionSortBasic:
    """
    NOTE insert() is an exercise, so these are reported as skipped until it is
    written.
    """

    def test_it_sorts(self):
        xs = [3, 1, 4, 1, 5]
        InsertionSortBasic.create().sort(xs)
        assert xs == [1, 1, 3, 4, 5]

    def test_it_sorts_a_range(self):
        xs = [9, 3, 1, 2, 9]
        InsertionSortBasic.create().sort(xs, 1, 4)
        assert xs == [9, 1, 2, 3, 9]

    def test_an_empty_list(self):
        xs = []
        InsertionSortBasic.create().sort(xs)
        assert xs == []

    def test_it_uses_the_comparator(self):
        xs = [1, 5, 3]
        InsertionSortBasic(lambda v, w: natural_comparison(w, v)).sort(xs)
        assert xs == [5, 3, 1]

    def test_it_is_stable(self):
        # Insertion sort must not move an element past an equal one.
        xs = [(1, "a"), (0, "b"), (1, "c"), (0, "d")]
        InsertionSortBasic(lambda v, w: natural_comparison(v[0], w[0])).sort(xs)
        assert xs == [(0, "b"), (0, "d"), (1, "a"), (1, "c")]


class TestCountInversions:
    """
    The two ways of counting inversions must agree. The Java runs an instrumented
    insertion sort and reads its fix count; we also compute the same number while
    merging, in n log n. They agree because insertion sort fixes exactly one
    inversion per exchange -- which is worth knowing, not just worth testing.
    """

    @pytest.mark.parametrize("xs", [
        [], [1], [1, 2, 3], [3, 2, 1], [1, 3, 2], [5, 2, 9, 1, 7, 3], [1, 1, 1], [2, 1, 2, 1],
    ])
    def test_the_two_methods_agree(self, xs):
        assert count_inversions_by_sorting(list(xs), natural_comparison) \
               == count_inversions(list(xs), natural_comparison)

    def test_they_agree_on_random_lists(self):
        rng = random.Random(3)
        for _ in range(5):
            xs = [rng.randint(0, 20) for _ in range(30)]
            assert count_inversions_by_sorting(list(xs), natural_comparison) \
                   == count_inversions(list(xs), natural_comparison)

    def test_counting_leaves_the_list_alone(self):
        xs = [3, 1, 2]
        count_inversions_by_sorting(xs, natural_comparison)
        assert xs == [3, 1, 2]

    def test_a_reversed_list_has_them_all(self):
        assert count_inversions_by_sorting([4, 3, 2, 1], natural_comparison) == 6


class TestCaseInsensitiveStringSorter:
    def test_it_ignores_case(self):
        xs = ["banana", "Apple", "cherry", "apple"]
        result = string_sorter_case_insensitive(len(xs), setup_config_fixes()).sort(xs)
        assert [s.lower() for s in result] == ["apple", "apple", "banana", "cherry"]


class TestLifecycle:
    def test_get_description(self):
        assert sorter(InsertionSort, 5).get_description() == "InsertionSort"

    def test_is_sorted(self):
        s = sorter(InsertionSort, 3)
        assert s.is_sorted([1, 2, 3])
        assert not s.is_sorted([1, 3, 2])

    def test_it_closes_at_the_end_of_a_with_block(self):
        with sorter(InsertionSort, 3) as s:
            s.sort([2, 1, 3])

    def test_closing_twice_is_harmless(self):
        s = sorter(InsertionSort, 3)
        s.close()
        s.close()

    def test_post_process_accepts_a_sorted_list(self):
        s = sorter(InsertionSort, 3, INSTRUMENTED)
        s.init(3)
        s.post_process([1, 2, 3])

    def test_post_process_raises_on_an_unsorted_list(self):
        # A sort which did not sort must not pass quietly: SortWithHelper
        # re-raises the HelperException rather than logging it, so the Helper's
        # check can fail a caller.
        s = sorter(InsertionSort, 3, INSTRUMENTED)
        s.init(3)
        with pytest.raises(HelperException, match="not sorted"):
            s.post_process([1, 3, 2])

