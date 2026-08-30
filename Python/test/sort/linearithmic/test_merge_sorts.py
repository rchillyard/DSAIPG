import random

import pytest

from src.sort.generic.sort_exception import SortException
from src.sort.helper.helper import natural_comparison
from src.sort.helper.helper_factory import create
from src.sort.linearithmic.merge_sort import MergeSort, get_config_string
from src.sort.linearithmic.merge_sort_basic import MergeSortBasic
from src.sort.linearithmic.tim_sort import TimSort
from src.util.config.config_benchmark import setup_config, setup_config2

PLAIN = setup_config("false", "", "0", "0", "", "")
INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")

#: MergeSort carries an exercise, so its rows here are reported as skipped until
#: _sort is written.
SORTS = [MergeSort, MergeSortBasic]


def sorter(cls, n, config=PLAIN, comparator=None):
    return cls(create(cls.__name__, n, config, comparator))


class TestEveryMergeSortSorts:
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
        xs = list(range(200))
        assert sorter(cls, 200).sort(xs) == xs

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_reversed_list(self, cls):
        assert sorter(cls, 200).sort(list(range(199, -1, -1))) == list(range(200))

    @pytest.mark.parametrize("cls", SORTS)
    def test_duplicates(self, cls):
        rng = random.Random(2)
        xs = [rng.randint(0, 5) for _ in range(200)]
        assert sorter(cls, 200).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_random_list(self, cls):
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(500)]
        assert sorter(cls, 500).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_it_sorts_when_instrumented_too(self, cls):
        rng = random.Random(7)
        xs = [rng.randint(0, 999) for _ in range(200)]
        assert sorter(cls, 200, INSTRUMENTED).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    @pytest.mark.parametrize("instrumented", [False, True])
    def test_a_custom_comparator_is_honoured(self, cls, instrumented):
        words = ["Arab", "abroad", "British", "bear", "French", "fair", "Italian",
                 "idea", "Muslim", "mask", "Olympic", "object", "apple", "army",
                 "art", "about", "above", "abuse", "academic", "accept"]

        def case_insensitive(v, w):
            return natural_comparison(v.lower(), w.lower())

        config = INSTRUMENTED if instrumented else PLAIN
        result = sorter(cls, len(words), config, case_insensitive).sort(words)
        for i in range(1, len(result)):
            assert case_insensitive(result[i - 1], result[i]) <= 0

    @pytest.mark.parametrize("cls", SORTS)
    def test_it_is_stable(self, cls):
        # Merge sort's other virtue. Equal keys must keep their order.
        by_key = lambda v, w: natural_comparison(v[0], w[0])  # noqa: E731
        pairs = [(1, "a"), (0, "b"), (1, "c"), (0, "d"), (1, "e")]
        result = sorter(cls, 5, PLAIN, by_key).sort(pairs)
        assert result == [(0, "b"), (0, "d"), (1, "a"), (1, "c"), (1, "e")]


class TestMergeSortOptions:
    """
    The no-copy and insurance options. Both must reach the [mergesort] section,
    which is where MergeSort reads them; anywhere else and all four combinations
    below are the same run.
    """

    @pytest.mark.parametrize("insurance", ["false", "true"])
    @pytest.mark.parametrize("no_copy", ["false", "true"])
    def test_every_combination_sorts(self, insurance, no_copy):
        config = setup_config2("true", "0", "0", "", "", insurance, no_copy)
        rng = random.Random(5)
        xs = [rng.randint(0, 999) for _ in range(200)]
        assert sorter(MergeSort, 200, config).sort(xs) == sorted(xs)

    def test_insurance_saves_work_on_ordered_input(self):
        # The whole point: one comparison per level tells it the halves are
        # already in order, so the merge is skipped.
        xs = list(range(500))
        without = sorter(MergeSort, 500, setup_config2("true", "0", "0", "", "", "false", "false"))
        without.sort(xs)
        with_ = sorter(MergeSort, 500, setup_config2("true", "0", "0", "", "", "true", "false"))
        with_.sort(xs)
        assert with_.get_helper().get_copies() < without.get_helper().get_copies()


class TestConfigString:
    def test_it_names_the_options(self):
        config = setup_config2("true", "0", "0", "", "", "true", "true")
        described = get_config_string(config)
        assert "insurance" in described
        assert "no copy" in described

    def test_it_says_nothing_when_nothing_is_set(self):
        assert get_config_string(setup_config2("true", "0", "0", "", "", "false", "false")) == ""

    def test_it_names_a_cutoff(self):
        config = setup_config("false", "", "0", "0", "7", "")
        assert "cutoff 7" in get_config_string(config)

    def test_a_cutoff_of_one_means_none(self):
        config = setup_config("false", "", "0", "0", "1", "")
        assert "no cutoff" in get_config_string(config)


class TestMemory:
    def test_the_memory_factor(self):
        s = sorter(MergeSortBasic, 100)
        s.set_array_memory(100)
        assert s.get_memory_factor() >= 1.0

    def test_it_complains_if_the_size_was_never_set(self):
        with pytest.raises(SortException, match="Array memory has not been set"):
            sorter(MergeSortBasic, 100).get_memory_factor()


class TestSortRangeDirectly:
    def test_merge_sort_basic_allocates_its_own_auxiliary_list(self):
        # sort_range is the method the Sort interface requires, so it must work
        # without sort having been called first. In the Java this threw a
        # NullPointerException.
        xs = [5, 3, 1, 4, 2]
        s = sorter(MergeSortBasic, 5)
        s.sort_range(xs, 0, 5)
        assert xs == [1, 2, 3, 4, 5]


class TestTimSort:
    """
    Timsort delegates to Python's own sort, which IS Timsort, so these check that
    it sorts and is stable rather than checking any statistics: it reports none,
    deliberately.
    """

    def test_a_random_list(self):
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(500)]
        assert sorter(TimSort, 500).sort(xs) == sorted(xs)

    def test_an_empty_list(self):
        assert sorter(TimSort, 0).sort([]) == []

    def test_it_leaves_the_original_alone(self):
        xs = [3, 1, 2]
        sorter(TimSort, 3).sort(xs)
        assert xs == [3, 1, 2]

    def test_a_custom_comparator_is_honoured(self):
        def case_insensitive(v, w):
            return natural_comparison(v.lower(), w.lower())

        words = ["Arab", "abroad", "British", "bear", "apple", "Zulu", "zebra"]
        result = sorter(TimSort, len(words), PLAIN, case_insensitive).sort(words)
        for i in range(1, len(result)):
            assert case_insensitive(result[i - 1], result[i]) <= 0

    def test_it_is_stable(self):
        by_key = lambda v, w: natural_comparison(v[0], w[0])  # noqa: E731
        pairs = [(1, "a"), (0, "b"), (1, "c"), (0, "d"), (1, "e")]
        assert sorter(TimSort, 5, PLAIN, by_key).sort(pairs) \
               == [(0, "b"), (0, "d"), (1, "a"), (1, "c"), (1, "e")]

    def test_it_sorts_only_the_given_range(self):
        xs = [99, 5, 3, 4, 1, 2, 99]
        sorter(TimSort, 7).sort_range(xs, 1, 6)
        assert xs == [99, 1, 2, 3, 4, 5, 99]

    def test_it_reports_no_statistics(self):
        # Deliberate: it is Python's sort, so there is nothing to count. Better
        # than the Java, which reports figures below the information-theoretic
        # floor because its instrumentation was never finished.
        rng = random.Random(1)
        xs = [rng.randint(0, 999) for _ in range(200)]
        s = sorter(TimSort, 200, INSTRUMENTED)
        s.sort(xs)
        assert s.get_helper().get_compares() == 0
        assert s.get_helper().get_hits() == 0
