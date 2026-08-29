"""
Ported from misc/BinarySearch.java.
"""

from __future__ import annotations


def binary_search(a: list[int], from_: int, to: int, key: int) -> int:
    """
    Find a key in a sorted list, by halving the range it could be in.

    The range is [from_, to): from_ is the first index that could hold the key and
    to is one PAST the last, which is why a miss below the middle narrows to to mid
    rather than to mid - 1.

    :param a: a list in ascending order.
    :param from_: the first index to consider.
    :param to: one past the last index to consider.
    :param key: the value to look for.
    :return: an index at which key appears, or -1 if it does not.
    """
    while to > from_:
        # TO BE IMPLEMENTED  implement binary search
        raise NotImplementedError("TO BE IMPLEMENTED")
    return -1
