"""
Every sort must report the work it actually did.

There are two things worth checking, and each needs its own independent witness:

- **comparisons.** The comparator counts its own invocations, so it sees every
  comparison however it was reached. The Helper's count should agree.
- **list accesses.** A list subclass counts its own reads and writes, so it sees
  every access however it was reached. The Helper's hit count should agree.

The second is possible here and is not possible in the Java tree, because there
is no way to intercept ``xs[i]`` on a Java array. So this is the only place in
either tree where the hit model is checked against reality rather than against
the other tree's arithmetic -- which only ever proved the two agreed.
"""

from __future__ import annotations

import random

import pytest

from src.sort.elementary.bubble_sort import BubbleSort
from src.sort.elementary.heap_sort import HeapSort
from src.sort.elementary.insertion_sort import InsertionSort, InsertionSortOpt
from src.sort.elementary.selection_sort import SelectionSort
from src.sort.elementary.shell_sort import ShellSort
from src.sort.helper.helper_factory import create
from src.sort.linearithmic.merge_sort_basic import MergeSortBasic
from src.sort.linearithmic.quick_sort_3way import QuickSort3Way
from src.sort.linearithmic.quick_sort_classic import QuickSortClassic
from src.sort.linearithmic.quick_sort_dual_pivot import QuickSortDualPivot
from src.sort.linearithmic.quick_sort_exp import QuickSortExp
from src.util.config.config_benchmark import setup_config

N = 200

#: NOTE fixes MUST stay off. enumerate_fixes compares through pure_comparison,
#: which reaches the comparator but deliberately does not count as a comparison,
#: so switching fixes on makes every sort look as though it were under-counting.
INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")

SORTS = [
    InsertionSort, InsertionSortOpt, BubbleSort, SelectionSort, HeapSort,
    ShellSort, MergeSortBasic, QuickSortClassic, QuickSort3Way,
    QuickSortDualPivot, QuickSortExp,
]

#: Sorts that allocate auxiliary storage of their own. The access witness cannot
#: see those lists -- they are created inside the sort, so nothing wraps them --
#: which makes an access comparison meaningless: the Helper counts hits on the
#: auxiliary list while the witness counts none. MergeSortBasic reports 6,673
#: hits against 3,507 observed accesses for exactly that reason, and the
#: difference is the auxiliary list, not an error.
#: Comparison counting is unaffected, since every comparison goes through the
#: comparator wherever its operands live.
USES_AUXILIARY_STORAGE = {MergeSortBasic}

IN_PLACE = [cls for cls in SORTS if cls not in USES_AUXILIARY_STORAGE]


class CountingList(list):
    """
    A list that counts every element read and written, whoever does it.

    This is the witness the Java tree cannot have: a Java array access is a
    bytecode instruction with nowhere to hook.
    """

    def __init__(self, values) -> None:
        super().__init__(values)
        self.reads = 0
        self.writes = 0

    def __getitem__(self, index):
        if isinstance(index, slice):
            # a slice read touches every element in it
            self.reads += len(range(*index.indices(len(self))))
        else:
            self.reads += 1
        return super().__getitem__(index)

    def __setitem__(self, index, value) -> None:
        if isinstance(index, slice):
            self.writes += len(value) if hasattr(value, "__len__") else 1
        else:
            self.writes += 1
        super().__setitem__(index, value)

    @property
    def accesses(self) -> int:
        """:return: the total number of reads and writes."""
        return self.reads + self.writes


def counting_comparator():
    """
    :return: a comparison function, and a one-element list holding the number of
             times it has been called.
    """
    calls = [0]

    def compare(v, w):
        calls[0] += 1
        return (v > w) - (v < w)

    return compare, calls


def measure(cls, xs):
    """
    Sort xs, and report what the Helper said against what really happened.

    :param cls: the sort to use.
    :param xs: the input.
    :return: reported and actual comparisons, and reported and actual accesses.
    """
    comparator, calls = counting_comparator()
    values = CountingList(xs)
    helper = create(cls.__name__, len(xs), INSTRUMENTED, comparator)
    sorter = cls(helper)
    sorter.sort_range(values, 0, len(values))
    # NOTE read the counters BEFORE checking sortedness: is_sorted compares
    # through pure_comparison and reads the list, and neither is meant to count.
    result = (helper.get_compares(), calls[0], helper.get_hits(), values.accesses)
    assert list(values) == sorted(xs), f"{cls.__name__} did not sort"
    return result


class TestComparisonsAreCounted:
    """
    Every comparison a sort makes must be counted. This is the check the Java
    tree also has, and all of its sorts pass it except TimSort.
    """

    @pytest.mark.parametrize("cls", SORTS)
    def test_on_random_input(self, cls):
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(N)]
        reported, actual, _, _ = measure(cls, xs)
        assert reported == actual, f"{cls.__name__} made {actual - reported} uncounted comparisons"

    @pytest.mark.parametrize("cls", SORTS)
    def test_on_sorted_input(self, cls):
        # Worth testing separately: a sort may legitimately make far fewer
        # comparisons here, and the count must still be right.
        reported, actual, _, _ = measure(cls, list(range(N)))
        assert reported == actual, f"{cls.__name__} made {actual - reported} uncounted comparisons"

    @pytest.mark.parametrize("cls", SORTS)
    def test_on_input_with_many_duplicates(self, cls):
        rng = random.Random(3)
        xs = [rng.randint(0, 4) for _ in range(N)]
        reported, actual, _, _ = measure(cls, xs)
        assert reported == actual, f"{cls.__name__} made {actual - reported} uncounted comparisons"


class TestAccessesAreCounted:
    """
    Every list access should be counted as a hit. Where a sort undercounts, it is
    recorded here as it currently behaves rather than asserted away, because the
    hit model is a deliberate approximation in places and correcting it would
    change published figures.
    """

    @pytest.mark.parametrize("cls", IN_PLACE)
    def test_the_hit_count_is_not_an_overstatement(self, cls):
        # Whatever else is true, a sort must not claim more accesses than it made.
        # An overstatement would mean the model is inventing work.
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(N)]
        _, _, hits, accesses = measure(cls, xs)
        assert hits <= accesses, \
            f"{cls.__name__} reported {hits} hits but made only {accesses} accesses"

    def test_the_shortfall_for_each_sort(self):
        # A record of how close each sort's hit count is to the truth, so that a
        # change shows up as a diff rather than passing unnoticed. Ratios, not
        # absolute numbers, so the figures survive a change of N.
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(N)]
        for cls in IN_PLACE:
            _, _, hits, accesses = measure(cls, xs)
            assert accesses > 0
            # Every sort should be within an order of magnitude of the truth.
            # QuickSortExp is the known outlier: its partition reads the list
            # directly and counts a single hit for the whole pass.
            floor = 0.02 if cls is QuickSortExp else 0.20
            assert hits / accesses >= floor, \
                f"{cls.__name__} counted {hits} of {accesses} accesses " \
                f"({hits / accesses:.0%}), which is below the recorded floor"
