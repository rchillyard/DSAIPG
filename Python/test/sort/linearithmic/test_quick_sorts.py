import random

import pytest

from src.sort.generic.sort_exception import SortException
from src.sort.helper.helper import natural_comparison
from src.sort.helper.helper_factory import create
from src.sort.linearithmic.intro_sort import SIZE_THRESHOLD, IntroSort, floor_lg
from src.sort.linearithmic.partition import Partition
from src.sort.linearithmic.quick_sort_3way import QuickSort3Way
from src.sort.linearithmic.quick_sort_classic import QuickSortClassic
from src.sort.linearithmic.quick_sort_dual_pivot import PartitionerDualPivot, QuickSortDualPivot
from src.sort.linearithmic.quick_sort_exp import QuickSortExp
from src.util.config.config_benchmark import setup_config

INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")
PLAIN = setup_config("false", "", "0", "0", "", "")

SORTS = [QuickSortClassic, QuickSort3Way, QuickSortDualPivot, QuickSortExp, IntroSort]


def sorter(cls, n, config=PLAIN, comparator=None):
    return cls(create(cls.__name__, n, config, comparator))


class TestEveryQuickSortSorts:
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
    def test_two_elements(self, cls):
        assert sorter(cls, 2).sort([2, 1]) == [1, 2]

    @pytest.mark.parametrize("cls", SORTS)
    def test_three_elements(self, cls):
        assert sorter(cls, 3).sort([3, 1, 2]) == [1, 2, 3]

    @pytest.mark.parametrize("cls", SORTS)
    def test_an_already_sorted_list(self, cls):
        # The case that makes a first-element pivot quadratic.
        xs = list(range(200))
        assert sorter(cls, 200).sort(xs) == xs

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_reversed_list(self, cls):
        assert sorter(cls, 200).sort(list(range(199, -1, -1))) == list(range(200))

    @pytest.mark.parametrize("cls", SORTS)
    def test_all_equal(self, cls):
        # The case three-way partitioning exists for.
        assert sorter(cls, 200).sort([7] * 200) == [7] * 200

    @pytest.mark.parametrize("cls", SORTS)
    def test_few_distinct_values(self, cls):
        rng = random.Random(1)
        xs = [rng.randint(0, 3) for _ in range(300)]
        assert sorter(cls, 300).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_random_list(self, cls):
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(500)]
        assert sorter(cls, 500).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_strings(self, cls):
        xs = ["pear", "apple", "fig", "date", "cherry", "banana"]
        assert sorter(cls, len(xs)).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_it_sorts_when_instrumented_too(self, cls):
        rng = random.Random(7)
        xs = [rng.randint(0, 999) for _ in range(200)]
        assert sorter(cls, 200, INSTRUMENTED).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    @pytest.mark.parametrize("instrumented", [False, True])
    def test_a_custom_comparator_is_honoured(self, cls, instrumented):
        # This is the case that five of the Java sorts got wrong: their
        # uninstrumented partition loop compared with compareTo, the natural
        # ordering, ignoring the comparator entirely. A test using ints cannot
        # detect it, because for ints the two agree -- so this uses strings whose
        # case-insensitive order differs from their natural one.
        words = ["Arab", "abroad", "British", "bear", "French", "fair", "Italian",
                 "idea", "Muslim", "mask", "Olympic", "object", "Republican",
                 "recording", "Spanish", "sensitive", "apple", "army", "art",
                 "about", "above", "abuse", "academic", "accept", "zebra", "Zulu"]

        def case_insensitive(v, w):
            return natural_comparison(v.lower(), w.lower())

        config = INSTRUMENTED if instrumented else PLAIN
        result = sorter(cls, len(words), config, case_insensitive).sort(words)
        for i in range(1, len(result)):
            assert case_insensitive(result[i - 1], result[i]) <= 0, \
                f"{cls.__name__} (instrumented={instrumented}) at {i}: " \
                f"{result[i - 1]!r} > {result[i]!r}"

    @pytest.mark.parametrize("cls", SORTS)
    def test_sorting_a_range_leaves_the_rest_alone(self, cls):
        xs = [99, 5, 3, 4, 1, 2, 99]
        sorter(cls, 7).sort_range(xs, 1, 6)
        assert xs == [99, 1, 2, 3, 4, 5, 99]


class TestPartition:
    def test_it_reports_its_range(self):
        p = Partition([1, 2, 3, 4], 1, 3)
        assert "from=1" in str(p)
        assert "to=3" in str(p)
        assert "[2, 3]" in str(p)

    def test_an_empty_partition(self):
        assert "Empty Partition" in str(Partition([1, 2], 1, 1))

    def test_is_sorted(self):
        helper = create("test", 4, PLAIN)
        assert Partition([9, 1, 2, 9], 1, 3).is_sorted(helper)
        assert not Partition([9, 2, 1, 9], 1, 3).is_sorted(helper)


class TestDualPivot:
    def test_it_needs_at_least_three_elements(self):
        helper = create("test", 2, PLAIN)
        with pytest.raises(SortException, match="less than 3"):
            PartitionerDualPivot(helper).partition(Partition([2, 1], 0, 2))

    def test_it_returns_three_partitions(self):
        helper = create("test", 6, PLAIN)
        parts = PartitionerDualPivot(helper).partition(Partition([5, 3, 1, 4, 2, 6], 0, 6))
        assert len(parts) == 3

    def test_the_pivots_end_up_in_place(self):
        # Everything below the first pivot is smaller, everything above the
        # second is larger, so both pivots are final.
        helper = create("test", 7, PLAIN)
        xs = [4, 7, 1, 6, 2, 5, 3]
        parts = PartitionerDualPivot(helper).partition(Partition(xs, 0, 7))
        low, high = parts[0].to, parts[2].from_ - 1
        for k in range(parts[0].from_, low):
            assert xs[k] <= xs[low]
        for k in range(parts[2].from_, parts[2].to):
            assert xs[k] >= xs[high]


class TestIntroSort:
    def test_floor_lg(self):
        assert floor_lg(1) == 0
        assert floor_lg(2) == 1
        assert floor_lg(1023) == 9
        assert floor_lg(1024) == 10

    def test_floor_lg_of_zero(self):
        assert floor_lg(0) == 0

    def test_below_the_size_threshold_it_uses_insertion_sort(self):
        xs = list(range(SIZE_THRESHOLD, 0, -1))
        assert sorter(IntroSort, len(xs)).sort(xs) == sorted(xs)

    def test_it_falls_back_to_heap_sort_when_it_recurses_too_deep(self):
        # Forcing the threshold to zero makes every range heap-sort, which
        # exercises the fallback directly.
        s = sorter(IntroSort, 100)
        rng = random.Random(3)
        xs = [rng.randint(0, 999) for _ in range(100)]
        s.get_helper().init(100)
        s.depth_threshold = 0
        result = list(xs)
        s.sort_range(result, 0, len(result), 0)
        assert result == sorted(xs)

    def test_heap_sort_alone_sorts(self):
        s = sorter(IntroSort, 50)
        rng = random.Random(9)
        xs = [rng.randint(0, 99) for _ in range(50)]
        result = list(xs)
        s._heap_sort(result, 0, len(result))
        assert result == sorted(xs)

    def test_heap_sort_over_a_range(self):
        s = sorter(IntroSort, 7)
        xs = [99, 5, 3, 4, 1, 2, 99]
        s._heap_sort(xs, 1, 6)
        assert xs == [99, 1, 2, 3, 4, 5, 99]


class TestDepth:
    def test_it_records_how_deep_it_went(self):
        rng = random.Random(11)
        xs = [rng.randint(0, 9999) for _ in range(500)]
        s = sorter(QuickSortDualPivot, 500, INSTRUMENTED)
        s.sort(xs)
        assert s.get_helper().max_depth() > 0

    def test_three_way_is_shallow_on_few_distinct_values(self):
        # The point of three-way partitioning: equal elements are placed once and
        # never recursed into, so a list of few distinct values finishes quickly.
        xs = [1] * 400
        s = sorter(QuickSort3Way, 400, INSTRUMENTED)
        s.sort(xs)
        assert s.get_helper().max_depth() <= 2
