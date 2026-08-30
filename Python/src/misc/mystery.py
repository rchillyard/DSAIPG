"""
Ported from misc/Mystery.java.
"""

from __future__ import annotations


def mystery(s: str) -> str:
    """
    Split the string in half, do the same to each half, and put the second before
    the first.

    The Java leaves it to the reader what this computes; it reverses the string.
    Each level swaps two halves, and the swaps compose all the way down to single
    characters, so every character ends up as far from its end as it began from the
    other.

    :param s: the string to transform.
    :return: the string reversed.
    """
    n = len(s)
    if n <= 1:
        return s
    return mystery(s[n // 2:]) + mystery(s[: n // 2])
