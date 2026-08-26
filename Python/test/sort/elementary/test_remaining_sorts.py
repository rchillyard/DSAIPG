import random

import pytest

from src.sort.elementary import insertion_sort_msd
from src.sort.elementary.heap_sort import HeapSort
from src.sort.elementary.random_sort import CUTOFF, RandomSort
from src.sort.elementary.shell_sort import H, ShellSort, sedgewick
from src.sort.helper.helper import natural_comparison
from src.util.config.config_benchmark import setup_config
from src.util.general.quick_random import QuickRandom

INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")
PLAIN = setup_config("false", "", "0", "0", "", "")

#: HeapSort and RandomSort take a Helper; ShellSort also takes a mode.
SORTS = [HeapSort, ShellSort, RandomSort]


def sorter(cls, n, config=PLAIN, comparator=None):
    return cls.from_config(cls.__name__, config, n, comparator=comparator)


class TestEverySortSorts:
    @pytest.mark.parametrize("cls", SORTS)
    def test_a_small_list(self, cls):
        xs = [3, 1, 4, 1, 5, 9, 2, 6]
        assert sorter(cls, len(xs)).sort(xs) == sorted(xs)

    # NOTE RandomSort is absent: it cannot sort an empty list. See TestRandomSort.
    @pytest.mark.parametrize("cls", [HeapSort, ShellSort])
    def test_an_empty_list(self, cls):
        assert sorter(cls, 0).sort([]) == []

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_single_element(self, cls):
        assert sorter(cls, 1).sort([1]) == [1]

    @pytest.mark.parametrize("cls", SORTS)
    def test_an_already_sorted_list(self, cls):
        xs = list(range(30))
        assert sorter(cls, 30).sort(xs) == xs

    @pytest.mark.parametrize("cls", SORTS)
    def test_a_reversed_list(self, cls):
        assert sorter(cls, 30).sort(list(range(29, -1, -1))) == list(range(30))

    @pytest.mark.parametrize("cls", SORTS)
    def test_duplicates(self, cls):
        xs = [5, 1, 5, 1, 3, 3, 5]
        assert sorter(cls, len(xs)).sort(xs) == sorted(xs)

    @pytest.mark.parametrize("cls", SORTS)
    def test_all_equal(self, cls):
        assert sorter(cls, 10).sort([7] * 10) == [7] * 10

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
    def test_a_custom_comparator_is_honoured(self, cls):
        reverse = lambda v, w: natural_comparison(w, v)  # noqa: E731
        assert sorter(cls, 5, comparator=reverse).sort([1, 5, 3, 2, 4]) == [5, 4, 3, 2, 1]

    @pytest.mark.parametrize("cls", SORTS)
    def test_it_sorts_when_instrumented_too(self, cls):
        xs = [3, 1, 4, 1, 5]
        assert sorter(cls, len(xs), INSTRUMENTED).sort(xs) == sorted(xs)

    # NOTE HeapSort is deliberately absent from the sub-range test below: it
    # ignores from and to. See TestHeapSort.
    @pytest.mark.parametrize("cls", [ShellSort, RandomSort])
    def test_sorting_a_range_leaves_the_rest_alone(self, cls):
        xs = [9, 3, 1, 2, 9]
        sorter(cls, 5).sort_range(xs, 1, 4)
        assert xs == [9, 1, 2, 3, 9]


class TestHeapSort:
    def test_it_ignores_the_range_it_is_given(self):
        # RECORDED, NOT ENDORSED. HeapSort uses len(xs) throughout and never
        # looks at from_ or to, so asking it to sort part of a list silently
        # sorts all of it. Every other sort here honours the range. Making it
        # work means offsetting every index in the heap arithmetic, which is a
        # change worth making deliberately rather than in passing.
        xs = [9, 3, 1, 2, 9]
        sorter(HeapSort, 5).sort_range(xs, 1, 4)
        assert xs == [1, 2, 3, 9, 9], "the whole list was sorted, not xs[1:4]"

    def test_it_is_n_log_n_ish_on_a_reversed_list(self):
        # Heap sort's worth is that its worst case is still n log n.
        n = 256
        s = sorter(HeapSort, n, INSTRUMENTED)
        s.sort(list(range(n - 1, -1, -1)))
        compares = s.get_helper().get_compares()
        assert compares < 4 * n * 8, f"{compares} should be well within a small multiple of n lg n"


class TestShellSort:
    @pytest.mark.parametrize("m", [1, 2, 3, 4, 5])
    def test_every_gap_sequence_sorts(self, m):
        rng = random.Random(m)
        xs = [rng.randint(0, 999) for _ in range(200)]
        helper_sort = ShellSort(
            ShellSort.from_config("shell", PLAIN, 200).get_helper(), m)
        assert helper_sort.sort(xs) == sorted(xs)

    def test_the_description_names_the_mode(self):
        s = ShellSort(ShellSort.from_config("shell", PLAIN, 10).get_helper(), 3)
        assert s.get_description() == "Shell sort in mode 3"

    def test_an_invalid_mode_is_rejected(self):
        with pytest.raises(ValueError, match="invalid m value"):
            H(100, 6)

    def test_mode_1_is_just_insertion_sort(self):
        h = H(100, 1)
        assert h.first() == 1
        assert h.next() == 0

    def test_the_gaps_descend_to_one(self):
        for m in (2, 3, 4, 5):
            h = H(1000, m)
            gaps = [h.first()]
            while gaps[-1] > 0:
                gaps.append(h.next())
            gaps = [g for g in gaps if g > 0]
            assert gaps == sorted(gaps, reverse=True), f"mode {m} gaps must descend: {gaps}"
            assert gaps[-1] == 1, f"mode {m} must finish with a gap of 1, got {gaps}"

    def test_first_cannot_be_called_twice(self):
        h = H(100, 3)
        h.first()
        with pytest.raises(RuntimeError, match="cannot call first more than once"):
            h.first()

    def test_sedgewick(self):
        # 1, 5, 19, 41, 109, ... the published sequence.
        assert [sedgewick(k) for k in range(5)] == [1, 5, 19, 41, 109]

    def test_sedgewick_below_zero(self):
        assert sedgewick(-1) == 0


class TestRandomSort:
    def test_a_list_below_the_cutoff_skips_the_random_phase(self):
        xs = list(range(CUTOFF - 1, -1, -1))
        assert sorter(RandomSort, len(xs)).sort(xs) == sorted(xs)

    def test_it_cannot_sort_an_empty_list(self):
        # RECORDED, NOT ENDORSED. RandomSort builds its QuickRandom before
        # testing the cutoff, and QuickRandom rejects a range of zero, so an
        # empty list raises instead of sorting trivially. The Java does the same.
        # Every other sort in the tree handles an empty list.
        with pytest.raises(ValueError, match="N must be positive"):
            sorter(RandomSort, 0).sort([])

    def test_a_list_above_the_cutoff_uses_it(self):
        rng = random.Random(5)
        xs = [rng.randint(0, 999) for _ in range(100)]
        assert sorter(RandomSort, 100).sort(xs) == sorted(xs)


class TestInsertionSortMSD:
    def test_it_sorts_from_the_given_depth(self):
        # All four share the prefix "ab", so only what follows matters.
        xs = ["abZ", "abA", "abM"]
        insertion_sort_msd.sort(xs, 0, 3, 2)
        assert xs == ["abA", "abM", "abZ"]

    def test_it_ignores_the_prefix(self):
        # The prefixes are in the wrong order, but from index 2 they are sorted,
        # so nothing moves.
        xs = ["zzA", "aaB", "mmC"]
        insertion_sort_msd.sort(xs, 0, 3, 2)
        assert xs == ["zzA", "aaB", "mmC"]

    def test_it_sorts_only_the_given_range(self):
        xs = ["zz", "cc", "aa", "bb", "zz"]
        insertion_sort_msd.sort(xs, 1, 4, 0)
        assert xs == ["zz", "aa", "bb", "cc", "zz"]

    def test_it_is_stable(self):
        # Insertion sort must not move an element past an equal one; from depth
        # 1 these are all equal.
        xs = ["ax", "bx", "cx"]
        insertion_sort_msd.sort(xs, 0, 3, 1)
        assert xs == ["ax", "bx", "cx"]

    def test_an_empty_range(self):
        xs = ["b", "a"]
        insertion_sort_msd.sort(xs, 0, 0, 0)
        assert xs == ["b", "a"]


class TestQuickRandom:
    def test_it_stays_within_range(self):
        q = QuickRandom(100, 42)
        assert all(0 <= q.get() < 100 for _ in range(500))

    def test_the_lower_bound(self):
        q = QuickRandom(100, 42)
        assert all(10 <= q.get(10) < 100 for _ in range(500))

    def test_it_is_repeatable(self):
        assert [QuickRandom(100, 7).get() for _ in range(5)] \
               == [QuickRandom(100, 7).get() for _ in range(5)]

    def test_it_matches_the_java(self):
        # Measured against the Java QuickRandom, which this must track exactly:
        # a different sequence would make results incomparable between the trees.
        q = QuickRandom(100, 0)
        assert [q.get() for _ in range(12)] == [13, 75, 8, 85, 20, 5, 18, 84, 74, 81, 95, 21]
        q = QuickRandom(100, 42)
        assert [q.get() for _ in range(12)] == [21, 43, 8, 59, 83, 26, 57, 57, 1, 36, 15, 98]
        q = QuickRandom(1000, 7)
        assert [q.get(10) for _ in range(8)] == [528, 356, 74, 984, 196, 303, 699, 651]

    def test_a_seed_of_zero_still_works(self):
        # The seed is mixed with 0xAAAAAAAA first, because a raw zero gives a
        # degenerate sequence.
        q = QuickRandom(1000, 0)
        assert len({q.get() for _ in range(50)}) > 1

    def test_n_must_be_positive(self):
        with pytest.raises(ValueError, match="N must be positive"):
            QuickRandom(0, 1)

    def test_m_must_not_be_negative(self):
        with pytest.raises(ValueError, match="m must be non-negative"):
            QuickRandom(100, 1).get(-1)
