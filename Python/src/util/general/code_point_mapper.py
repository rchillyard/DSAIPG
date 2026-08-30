"""
Mapping characters into a small range, ported from
util/general/CodePointMapper.java.

A radix sort needs one bucket per possible character, so the cost of a pass is
the size of the alphabet. Unicode has over a million code points, which would be
absurd; mapping them into a small range first makes the sort practical.

The mapper and the comparator must agree, and that is the whole subtlety here. A
radix sort groups by the MAPPED character, so if two characters map to the same
value the sort treats them as equal -- and then any check that the result is
sorted must treat them as equal too, or it will report an order that is not
wrong. Each mapper therefore comes with the comparator that matches it.
"""

from __future__ import annotations

from collections.abc import Callable


class CodePointMapper:
    """
    A mapping of characters into a small range, with the ordering that matches.
    """

    def __init__(self, name: str, mapper: Callable[[int], int], range_: int,
                 comparator: Callable[[str, str], int]) -> None:
        """
        :param name: what to call this mapping.
        :param mapper: maps a code point into 0 .. range_ - 1.
        :param range_: the number of buckets a radix sort will need.
        :param comparator: orders strings consistently with the mapper.
        """
        self.name = name
        self.mapper = mapper
        self.range = range_
        self.comparator = comparator

    def map_code_point(self, code_point: int) -> int:
        """
        :param code_point: the character to map.
        :return: its mapped value.
        :raises RuntimeError: if the mapper produces something outside the range,
                              which would mean an out-of-bounds bucket.
        """
        result = self.mapper(code_point)
        if self.in_range(result):
            return result
        raise RuntimeError(f"CodePointMapper {self}: result out of range: {result}")

    def map_string(self, s: str) -> str:
        """
        :param s: the string to map.
        :return: the string with every character mapped.
        """
        return "".join(chr(self.map_code_point(ord(c))) for c in s)

    def in_range(self, x: int) -> bool:
        """
        :param x: a mapped value.
        :return: true if it names a bucket.
        """
        return 0 <= x < self.range

    def compare(self, o1: str, o2: str) -> int:
        """
        :param o1: the first string.
        :param o2: the second.
        :return: -1, 0 or 1, by the ordering that matches this mapping.
        """
        return self.comparator(o1, o2)

    def __call__(self, x: int) -> int:
        return self.mapper(x)

    def __str__(self) -> str:
        return f"CodePointMapper {self.name}: with range {self.range}"


def _english_mapper(x: int) -> int:
    """
    Map a letter to 1..26 and everything else to 0.

    NOTE the low five bits of a letter's code point are its position in the
    alphabet, and are the same for upper and lower case -- which is why this
    folds case for free, and why the matching comparator must fold case too.

    :param x: a code point.
    :return: 0 for anything that is not a letter below 256, else 1 to 26.
    """
    return x & 0x1F if x < 256 and chr(x).isalpha() else 0


def _mapped_comparison(mapper: Callable[[int], int]) -> Callable[[str, str], int]:
    """
    Build the comparator that matches a mapper: compare the mapped characters, a
    missing character counting as zero so that a shorter string sorts first.

    :param mapper: the character mapping.
    :return: the matching comparator.
    """
    def compare(o1: str, o2: str) -> int:
        for i in range(min(len(o1), len(o2)) + 1):
            char1 = ord(o1[i]) if i < len(o1) else 0
            char2 = ord(o2[i]) if i < len(o2) else 0
            cf = mapper(char1) - mapper(char2)
            if cf != 0:
                return cf
        return 0

    return compare


def _ascii_ext_mapper(x: int) -> int:
    """:param x: a code point. :return: its low eight bits."""
    return x & 0xFF


def _ascii_mapper(x: int) -> int:
    """:param x: a code point. :return: its low seven bits."""
    return x & 0x7F


#: Letters only, folded to 1..26 with everything else 0. 32 buckets.
English = CodePointMapper("English", _english_mapper, 32, _mapped_comparison(_english_mapper))

#: The low eight bits. 256 buckets.
ASCIIExt = CodePointMapper("ASCII (Ext)", _ascii_ext_mapper, 256,
                           _mapped_comparison(_ascii_ext_mapper))

#: The low seven bits. 128 buckets.
ASCII = CodePointMapper("ASCII", _ascii_mapper, 128, _mapped_comparison(_ascii_mapper))
