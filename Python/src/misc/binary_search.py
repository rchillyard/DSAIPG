"""
Ported from misc/BinarySearch.java.
"""

from __future__ import annotations


def binary_search(a: list[int], from_: int, to: int, key: int) -> int:
    """
    Find a key in a sorted list, by halving the range it could be in.

    NOTE the range is [from_, to) -- to is exclusive, as it is throughout this
    repository. That is why the miss narrows ``hi`` to ``mid`` and not to
    ``mid - 1``: with an exclusive hi, mid - 1 drops a candidate at every step from
    the low end. The Java had it that way and could not find the first element of
    an array; fixed in both trees.

    :param a: a list in ascending order.
    :param from_: the first index to consider.
    :param to: one past the last index to consider.
    :param key: the value to look for.
    :return: an index at which key appears, or -1 if it does not.
    """
    lo, hi = from_, to
    while hi > lo:
        # TO BE IMPLEMENTED  implement binary search
        raise NotImplementedError("TO BE IMPLEMENTED")
    return -1
