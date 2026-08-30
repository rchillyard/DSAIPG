"""
Least-significant-digit radix sort for strings, ported from
sort/counting/LSDStringSort.java.

Every string is sorted by its last character, then its second-last, and so on to
the first. Each pass is a stable counting sort, and stability is the whole
mechanism: a later pass may only reorder strings that differ at that position,
so the order the earlier passes established survives.

It needs the strings to be the same length, which is why they are padded: a
character position past the end reads as zero, which sorts first.
"""

from __future__ import annotations

from src.sort.classic.classification_sorter import ClassificationSorter
from src.util.general.code_point_mapper import ASCII

DESCRIPTION = "LSD string sort"

#: The number of buckets: one per ASCII character, plus one for the accumulation.
ASCII_RANGE = 128


class LSDStringSort(ClassificationSorter[str, int]):
    """
    LSD radix sort for strings.
    """

    def __init__(self, helper, w: int = 0) -> None:
        """
        :param helper: the Helper to sort through.
        :param w: the number of characters to sort by; 0 means use the longest
                  string.
        """
        super().__init__(helper, lambda x, d: char_at(x, d))
        self.w = w

    def get_description(self) -> str:
        return DESCRIPTION

    def sort_range(self, xs: list[str], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element.
        :param to: the index one past the last.
        """
        # NOTE the strings are first reduced to ASCII, since there is one bucket
        # per character and the full range of Unicode would need over a million.
        # Not counted as hits, following the Java.
        for i in range(from_, to):
            xs[i] = ASCII.map_string(xs[i])
        max_length = self.w if self.w > 0 else find_max_length(xs, from_, to)
        for d in range(max_length, 0, -1):
            self._char_sort(xs, d - 1, from_, to)

    def _char_sort(self, xs: list[str], char_position: int, from_: int, to: int) -> None:
        """
        Sort the range by one character position, stably.

        :param xs: the list.
        :param char_position: which character to sort by.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        """
        helper = self.get_helper()
        count = [0] * (ASCII_RANGE + 1)
        self._classify_and_count(xs, char_position, from_, to, count)
        self._accumulate_counts(count)
        auxiliary = self._distribute(xs, char_position, from_, to, count)
        helper.copy_block(auxiliary, 0, xs, from_, to - from_)

    def _classify_and_count(self, xs: list[str], char_position: int, from_: int, to: int,
                            count: list[int]) -> None:
        """
        Count how many strings have each character at the given position.

        :param xs: the list.
        :param char_position: which character to look at.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param count: the counts, updated in place.
        """
        helper = self.get_helper()
        for i in range(from_, to):
            x = helper.get(xs, i)
            c = self.classify(x, char_position)
            helper.increment_hits(1)  # for the count.
            if c < 0 or c >= ASCII_RANGE:
                raise RuntimeError(f"LSDStringSort: character out of range: {c}")
            count[c + 1] += 1

    def _accumulate_counts(self, count: list[int]) -> None:
        """
        Turn the counts into the index at which each character's block begins.

        :param count: the counts, updated in place.
        """
        helper = self.get_helper()
        count_r = count[0]
        for r in range(1, ASCII_RANGE + 1):
            helper.increment_hits(1)  # for the count.
            count[r] += count_r
            count_r = count[r]

    def _distribute(self, xs: list[str], char_position: int, from_: int, to: int,
                    count: list[int]) -> list[str]:
        """
        Place each string where its character says it belongs.

        :param xs: the list.
        :param char_position: which character to look at.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param count: where each character's block begins, updated as it fills.
        :return: the strings in their new order.
        """
        result = [""] * (to - from_)

        def place(x: str) -> int:
            cls = self.classify(x, char_position)
            index = count[cls]
            count[cls] += 1
            return index

        self.get_helper().distribute_block(xs, from_, to, result, place)
        return result


def char_at(s: str, d: int) -> int:
    """
    :param s: the string.
    :param d: the position wanted.
    :return: the code point at d, or 0 if the string is shorter -- so a short
             string sorts before a longer one sharing its prefix.
    """
    return ord(s[d]) if d < len(s) else 0


def find_max_length(xs: list[str], from_: int = 0, to: int | None = None) -> int:
    """
    :param xs: the strings.
    :param from_: the index of the first to consider.
    :param to: one past the index of the last, defaulting to the end.
    :return: the length of the longest, or 0 if there are none.
    """
    if to is None:
        to = len(xs)
    return max((len(x) for x in xs[from_:to]), default=0)
