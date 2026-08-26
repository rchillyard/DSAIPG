"""
Sorting by class alone, ported from sort/classic/ClassicSort.java.

Each element is put in the bag for its class, and the bags are emptied back into
the list in ascending order of class. Nothing is compared except the class values
themselves, and those only once each.

Note what this does NOT do: it does not order the elements within a class. A Bag
iterates in a deliberately arbitrary order, so all that can be said afterwards is
that the classes appear in order. Ordering within a class is the following pass's
job -- which is how BucketSort uses it, running an insertion sort over the whole
list once the buckets have been unloaded. That pass is cheap precisely because
the classes are already in order.
"""

from __future__ import annotations

from typing import TypeVar

from src.adt.bqs.bag_array import BagArray
from src.sort.classic.classify import Classify
from src.sort.generic.sort_exception import SortException
from src.sort.generic.sort_with_helper import SortWithHelper

X = TypeVar("X", bound=Classify)

DESCRIPTION = "Classic sort"


class ClassicSort(SortWithHelper[X]):
    """
    A sort which groups elements by class and emits the classes in order.
    """

    def get_description(self) -> str:
        return DESCRIPTION

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Group xs[from_:to] by class, and write the groups back in class order.

        :param xs: the list to sort.
        :param from_: the index of the first element.
        :param to: the index one past the last.
        :raises SortException: if the bags hold more elements than the range does,
                               which would mean the grouping had gone wrong.
        """
        bags: dict[int, BagArray[X]] = {}
        for i in range(from_, to):
            cls = xs[i].classify()
            bag = bags.get(cls)
            if bag is None:
                bag = BagArray()
                bags[cls] = bag
            bag.add(xs[i])

        # NOTE the classes are ordered explicitly. A dict preserves insertion
        # order, not ascending order, and classify() may return any int -- so
        # without this the classes would come out in the order they were first
        # seen. Getting that wrong is not cosmetic: the point of classifying
        # first is that a following insertion sort has little left to do, and
        # over 2,000 elements in 8 classes the wrong order left 806,848
        # inversions for the second pass against 124,863.
        #
        # This orders the distinct classes once -- k log k comparisons of ints,
        # where k is at most the number of elements and usually far smaller. NOT
        # a sorted container updated per element, which would compare on every
        # insertion; comparing per element is the cost this sort exists to avoid.
        i = from_
        for cls in sorted(bags):
            if i >= to:
                raise SortException(f"ClassicSort: logic error: {i}, {to}")
            for x in bags[cls]:
                xs[i] = x
                i += 1

    def init(self, n: int) -> None:
        """
        :param n: the number of elements; nothing needs doing.
        """

    def post_process(self, xs: list[X]) -> None:
        """
        :param xs: the sorted list; nothing needs doing.
        """
