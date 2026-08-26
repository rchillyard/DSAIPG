"""
Most-significant-digit radix sort for strings, ported from
sort/counting/MSDStringSort.java.

Strings are distributed by their first character, then each group is sorted by
its second, and so on. Unlike LSD, this can stop early: once a group holds one
string, or once the strings in it differ at a character already examined, there
is nothing left to do. That is what makes it suitable for strings of very
different lengths, where LSD would pad everything to the longest.

Below a cutoff a group is handed to a three-way quicksort, comparing from the
current depth onwards -- worthwhile because the constant factor of distributing
into buckets is large when the group is small.
"""

from __future__ import annotations

from src.sort.generic.sort_with_helper_and_additional_memory import (
    SortWithHelperAndAdditionalMemory,
)
from src.sort.linearithmic.quick_sort_3way import QuickSort3Way
from src.util.general.code_point_mapper import CodePointMapper
from src.util.general.suffix_comparator import SuffixComparator

DESCRIPTION = "MSD string sort"


def char_at(s: str, d: int) -> int:
    """
    :param s: the string.
    :param d: the position wanted.
    :return: the code point at d, or 0 if the string is shorter than that. Zero
             maps to bucket 0, which is why exhausted strings collect there and
             are not looked at again -- there is nothing left of them to compare.
    """
    return ord(s[d]) if d < len(s) else 0


class MSDStringSort(SortWithHelperAndAdditionalMemory[str]):
    """
    MSD radix sort for strings.
    """

    def __init__(self, helper, mapper: CodePointMapper) -> None:
        """
        :param helper: the Helper to sort through.
        :param mapper: maps a character into a small range, and supplies the
                       matching ordering.
        """
        super().__init__(helper, lambda x, d: mapper.map_code_point(char_at(x, d)))
        self.mapper = mapper

    def get_description(self) -> str:
        return DESCRIPTION

    def sort_range(self, xs: list[str], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element.
        :param to: the index one past the last.
        """
        self._do_sort(xs, from_, to, 0)

    def _do_sort(self, xs: list[str], from_: int, to: int, d: int) -> None:
        """
        Sort xs[from_:to], all of which agree on their first d characters.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param d: the number of leading characters already known to be equal.
        """
        n = to - from_
        if n <= 1:
            return
        # NOTE never at the top level: with d of 0 the strings have nothing in
        # common yet, so handing them to a comparison sort would be sorting the
        # whole problem rather than a sub-problem of it.
        if d > 0 and n <= self.get_helper().msd_cutoff():
            self._cut_to_quicksort(xs, from_, to, d, n)
        else:
            self._do_msd_recursive(xs, from_, to, d)

    def _cut_to_quicksort(self, xs: list[str], from_: int, to: int, d: int, n: int) -> None:
        """
        Finish a small group with a three-way quicksort, comparing from depth d.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param d: the depth from which to compare.
        :param n: the number of elements.
        """
        helper = self.get_helper()
        suffix_comparator = SuffixComparator(helper.get_comparator(), d)
        cloned = helper.clone("MSD 3-way quicksort", n, suffix_comparator, share_instrumenter=True)
        QuickSort3Way(cloned).sort_range(xs, from_, to)

    def _do_msd_recursive(self, xs: list[str], from_: int, to: int, d: int) -> None:
        """
        Distribute xs[from_:to] by the character at depth d, then sort each group
        by the next character.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param d: the character position to distribute by.
        """
        helper = self.get_helper()
        n = to - from_
        aux: list[str] = [""] * n
        self.additional_memory(n)
        count = [0] * (self.mapper.range + 1)
        self.additional_memory(self.mapper.range + 1)

        helper.increment_hits(n)  # for the count.
        for i in range(from_, to):
            count[self.classify_at(xs, i, d) + 1] += 1

        # turn the counts into the index at which each bucket begins
        count_r = count[0]
        for r in range(1, self.mapper.range):
            helper.increment_hits(1)  # for the count.
            count[r] += count_r
            count_r = count[r]

        def place(x: str) -> int:
            cls = self.classify(x, d)
            index = count[cls]
            count[cls] += 1
            return index

        helper.distribute_block(xs, from_, to, aux, place)
        helper.copy_block(aux, 0, xs, from_, n)
        self.additional_memory(-(n + self.mapper.range + 1))

        # NOTE count has been mutated by the distribution: count[r] is now the
        # index just past bucket r, which is where bucket r + 1 begins. So the
        # pair (count[r], count[r + 1]) delimits bucket r + 1, and bucket 0 is
        # deliberately not revisited -- it holds the strings that ran out at this
        # depth, which have nothing further to compare.
        # TO BE IMPLEMENTED : recurse into each bucket at the next character
        raise NotImplementedError("TO BE IMPLEMENTED")
