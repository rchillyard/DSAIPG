"""
Insertion sort on string suffixes, ported from
sort/elementary/InsertionSortMSD.java.

This is what an MSD radix sort hands its small sub-arrays to. Every string in
such a sub-array already shares its first d characters, so only what follows
matters, and comparing from index d onwards saves re-examining the prefix that
put them in the same bucket.

It uses no Helper and counts nothing.
"""

from __future__ import annotations


def sort(a: list[str], from_: int, to: int, d: int) -> None:
    """
    Sort a[from_:to] by comparing each string from index d onwards.

    :param a: the list of strings.
    :param from_: the index of the first element to sort.
    :param to: the index one past the last element to sort.
    :param d: the number of leading characters to ignore, which the caller has
              already established are equal.
    """
    for i in range(from_, to):
        j = i
        while j > from_ and _less(a[j], a[j - 1], d):
            _swap(a, j, j - 1)
            j -= 1


def _less(v: str, w: str, d: int) -> bool:
    """
    :param v: the first string.
    :param w: the second string.
    :param d: the number of leading characters to ignore.
    :return: true if v comes before w from index d onwards.
    """
    return v[d:] < w[d:]


def _swap(a: list[str], j: int, i: int) -> None:
    """
    Exchange a[i] and a[j].

    :param a: the list.
    :param j: one index.
    :param i: the other index.
    """
    a[j], a[i] = a[i], a[j]
