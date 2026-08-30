"""
Merge sort, ported from sort/linearithmic/MergeSort.java.

Merge sort is the reliable one: n log n whatever the input, and stable. Its cost
is the auxiliary list, and the two options here are both about reducing the work
that list implies.

- **no-copy** alternates the roles of the two lists at each level instead of
  copying between them, which removes one copy of the whole range per level.
- **insurance** checks whether the two halves are already in order -- the last of
  the left no greater than the first of the right -- and if so skips the merge
  entirely. One comparison can save a whole pass, and on partly-ordered input it
  usually does.

Both are read from the [mergesort] section of the configuration.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.elementary.insertion_sort import InsertionSort
from src.sort.generic.has_additional_memory import HasAdditionalMemory
from src.sort.generic.sort_exception import SortException
from src.sort.generic.sort_with_helper import SortWithHelper
from src.util.config.config_benchmark import (
    CUTOFF,
    CUTOFF_DEFAULT,
    HELPER,
    INSURANCE,
    MERGESORT,
    NOCOPY,
)

X = TypeVar("X")

DESCRIPTION = "MergeSort"


class MergeSort(SortWithHelper[X], HasAdditionalMemory):
    """
    Merge sort, with the no-copy and insurance options.
    """

    def __init__(self, helper) -> None:
        """
        :param helper: the Helper to sort through.
        """
        super().__init__(helper)
        self.insertion_sort = InsertionSort(
            helper.clone("MergeSort: insertion sort", share_instrumenter=True))
        self.array_memory = -1
        self.additional = 0
        self.max_memory = 0

    def get_description(self) -> str:
        return DESCRIPTION + get_config_string(self.get_helper().get_config())

    def sort(self, xs: list[X], make_copy: bool = True) -> list[X]:
        """
        Sort a list, accounting for the auxiliary memory used.

        :param xs: the list to sort.
        :param make_copy: if true, sort a copy and return it.
        :return: the sorted list.
        """
        self.get_helper().init(len(xs))
        self.additional_memory(len(xs))
        result = list(xs) if make_copy else xs
        self.sort_range(result, 0, len(result))
        self.additional_memory(-len(xs))
        return result

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()
        no_copy = helper.get_config().get_boolean(MERGESORT, NOCOPY)
        aux = helper.copy_array(xs) if no_copy else [None] * len(xs)
        self._sort(xs, aux, from_, to, 0)

    def _sort(self, primary: list[X], secondary: list[X], from_: int, to: int,
              depth: int) -> None:
        """
        Sort primary[from_:to], using secondary as working space.

        :param primary: the list whose range must end up sorted.
        :param secondary: the working list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param depth: the recursion depth.
        """
        helper = self.get_helper()
        helper.register_depth(depth)
        config = helper.get_config()
        # NOTE both are part of the skeleton; the exercise below uses them.
        no_copy = config.get_boolean(MERGESORT, NOCOPY)  # noqa: F841
        insurance = config.get_boolean(MERGESORT, INSURANCE)  # noqa: F841
        if to <= from_ + helper.cutoff():
            self.insertion_sort.sort_range(primary, from_, to)
            return
        # TO BE IMPLEMENTED : implement merge sort with the no-copy and insurance
        # optimizations (use helper.not_inverted and helper.copy_block)
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _merge(self, sorted_: list[X], result: list[X], from_: int, mid: int, to: int) -> None:
        """
        Merge the two sorted halves of sorted_ into result.

        :param sorted_: the list holding the two sorted halves.
        :param result: the list to merge into.
        :param from_: the index of the first element.
        :param mid: the index at which the second half begins.
        :param to: one past the index of the last element.
        """
        helper = self.get_helper()
        i = from_
        j = mid
        helper.increment_lookups(to - from_)  # NOTE this is optimistic
        v = helper.get(sorted_, i)
        w = helper.get(sorted_, j)
        for k in range(from_, to):
            if i >= mid:
                helper.copy(w, result, k)
                j += 1
                if j < to:
                    w = helper.get(sorted_, j)
            elif j >= to:
                helper.copy(v, result, k)
                i += 1
                if i < mid:
                    v = helper.get(sorted_, i)
            elif helper.inverted(v, w):
                # every element still to come from the left half is greater than
                # w, so taking w now fixes that many inversions at once.
                helper.increment_fixes(mid - i)
                helper.copy(w, result, k)
                j += 1
                if j < to:
                    w = helper.get(sorted_, j)
            else:
                helper.copy(v, result, k)
                i += 1
                if i < mid:
                    v = helper.get(sorted_, i)

    # ---- HasAdditionalMemory ---------------------------------------------

    def set_array_memory(self, n: int) -> None:
        if self.array_memory == -1:
            self.array_memory = n
            self.additional_memory(n)

    def additional_memory(self, n: int) -> None:
        self.additional += n
        if self.max_memory < self.additional:
            self.max_memory = self.additional

    def get_memory_factor(self) -> float:
        """
        :return: the peak additional memory as a multiple of the size of the list.
        :raises SortException: if the size was never recorded.
        """
        if self.array_memory == -1:
            raise SortException("Array memory has not been set")
        return 1.0 * self.max_memory / self.array_memory


def get_config_string(config) -> str:
    """
    Describe the options a configuration switches on, for the sort's description.

    :param config: the configuration.
    :return: a description, possibly empty.
    """
    parts = []
    if config.get_boolean(MERGESORT, INSURANCE):
        parts.append(" with insurance comparison")
    if config.get_boolean(MERGESORT, NOCOPY):
        parts.append(" with no copy")
    cutoff = config.get_int(HELPER, CUTOFF, CUTOFF_DEFAULT)
    if cutoff != CUTOFF_DEFAULT:
        parts.append(" with no cutoff" if cutoff == 1 else f" with cutoff {cutoff}")
    return "".join(parts)
