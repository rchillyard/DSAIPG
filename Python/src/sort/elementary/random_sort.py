"""
Random sort, ported from sort/elementary/RandomSort.java.

This is an experiment rather than a sort anyone would use. It first exchanges
random pairs that happen to be out of order -- roughly 2.5 n lg n of them -- and
then finishes with an ordinary insertion sort.

The question it asks is whether cheap, undirected repairs beforehand leave few
enough inversions to pay for themselves. Insertion sort costs one exchange per
inversion, so anything that removes inversions faster than it costs is a win;
the point of the exercise is to find out whether this does.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.elementary.insertion_sort import InsertionSort
from src.sort.generic.sort_with_helper import SortWithHelper
from src.util.general.quick_random import QuickRandom
from src.util.general.utilities import lg
from src.util.logging.lazy_logger import LazyLogger

X = TypeVar("X")

DESCRIPTION = "Random sort"

#: How many random exchanges to attempt, as a multiple of n lg n.
FACTOR = 2.5

#: Below this many elements the random phase is skipped entirely.
CUTOFF = 16

logger = LazyLogger(__name__)


class RandomSort(SortWithHelper[X]):
    """
    Random pre-processing, followed by insertion sort.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        n = to - from_
        helper = self.get_helper()
        instrumented = helper.instrumented()
        inversions = helper.inversions(xs) if instrumented else 0
        if n > CUTOFF:
            # NOTE built here, not above. QuickRandom rejects a range of zero, so
            # building it before this test meant an empty list raised rather than
            # sorting trivially.
            r = QuickRandom(n, 0)
            m = int(FACTOR * lg(n) * n)
            for _ in range(m):
                helper.swap_conditional(xs, r.get() + from_, r.get())
            if instrumented:
                current = helper.inversions(xs)
                fixed = inversions - current
                inversions = current
                logger.info(lambda: f"pre-processor: inversions={current}, "
                                    f"fixes={fixed}, comparisons={m}")
        InsertionSort(helper).sort_range(xs, from_, to)
        if instrumented:
            stats = helper.show_stats()
            current = helper.inversions(xs)
            fixed = inversions - current
            logger.info(lambda: f"after insertion sort: {stats}")
            logger.info(lambda: f"insertion sort: inversions={current}, fixes={fixed}")
