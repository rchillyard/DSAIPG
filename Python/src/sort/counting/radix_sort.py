"""
Radix sort for non-negative integers, ported from
sort/counting/RadixSort.java.

One counting sort per decimal digit, least significant first. Each pass is
linear and stable, and stability is what makes the whole thing work: a later
pass may only reorder elements that differ at that digit, leaving the order the
earlier passes established for everything else.

NOTE ``to`` is EXCLUSIVE, as everywhere else in this tree. It used to be
inclusive here, alone among the sorts, so a caller following the usual
convention silently left the last element of the range unsorted. Note also that
only non-negative values work: a negative number gives a negative digit and so a
negative bucket.
"""

from __future__ import annotations

#: Ten buckets, one per decimal digit.
RADIX = 10


def find_max_int(num_arr: list[int], from_: int, to: int) -> int:
    """
    :param num_arr: the values.
    :param from_: the first index to consider.
    :param to: one past the last index to consider, i.e. EXCLUSIVE.
    :return: the largest value in the range.
    """
    return max(num_arr[from_:to])


def count_sort(num_arr: list[int], exp: int, from_: int, to: int) -> None:
    """
    Sort the range by one digit, stably.

    :param num_arr: the values, rearranged in place.
    :param exp: the power of ten selecting the digit: 1, 10, 100, ...
    :param from_: the first index to sort.
    :param to: one past the last index to sort, i.e. EXCLUSIVE.
    """
    result = [0] * len(num_arr)
    count = [0] * RADIX
    for i in range(from_, to):
        count[(num_arr[i] // exp) % RADIX] += 1
    # turn the counts into the position just past each digit's block
    for i in range(1, RADIX):
        count[i] += count[i - 1]
    # NOTE backwards, which is what makes the pass stable: the last element with
    # a given digit is placed last, so equal digits keep the order they were in.
    for i in range(to - 1, from_ - 1, -1):
        digit = (num_arr[i] // exp) % RADIX
        count[digit] -= 1
        result[count[digit] + from_] = num_arr[i]
    if to - from_ >= 0:
        num_arr[from_:to] = result[from_:to]


def sort(num_arr: list[int], from_: int, to: int) -> None:
    """
    Sort the range, one decimal digit at a time.

    :param num_arr: the values, rearranged in place.
    :param from_: the first index to sort.
    :param to: one past the last index to sort, i.e. EXCLUSIVE.
    :raises ValueError: if from_ is greater than to.
    :raises IndexError: if either index is outside the list.
    """
    if num_arr is None or len(num_arr) == 1 or from_ == to:
        return
    if from_ > to:
        raise ValueError("From value should be less than to")
    if from_ < 0 or from_ > len(num_arr) - 1:
        raise IndexError(f"From should be between 0 and {len(num_arr) - 1}")
    if to > len(num_arr):
        raise IndexError(f"To should be between 0 and {len(num_arr)}")
    max_val = find_max_int(num_arr, from_, to)
    exp = 1
    while max_val // exp > 0:
        count_sort(num_arr, exp, from_, to)
        exp *= RADIX
