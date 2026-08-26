"""
Comparing strings from a given depth, ported from
util/general/SuffixComparator.java.

An MSD radix sort hands a small group to a comparison sort once the group is
small enough. Every string in that group already shares its first d characters,
so comparing them again would be waste: this compares from index d onwards.
"""

from __future__ import annotations

from collections.abc import Callable

from src.sort.helper.helper import discriminate_string


class SuffixComparator:
    """
    Compares two strings from a fixed depth onwards.
    """

    def __init__(self, string_comparator: Callable[[str, str], int],
                 prefix_length: int = 0) -> None:
        """
        :param string_comparator: how to compare the suffixes.
        :param prefix_length: how many leading characters to ignore.
        """
        self.string_comparator = string_comparator
        self.prefix_length = prefix_length

    def __call__(self, o1: str, o2: str) -> int:
        """
        :param o1: the first string.
        :param o2: the second string.
        :return: -1, 0 or 1, comparing from prefix_length onwards.
        """
        return self.string_comparator(discriminate_string(o1, self.prefix_length),
                                      discriminate_string(o2, self.prefix_length))
