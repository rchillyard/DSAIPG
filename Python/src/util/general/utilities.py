"""
Assorted helpers, ported from util/general/Utilities.java.
"""

import math
from collections.abc import Callable, Collection
from random import Random
from typing import TypeVar

T = TypeVar("T")


def as_array(ts: Collection[T]) -> list[T]:
    """
    Return the given collection as a list.

    NOTE this exists only for parity with the Java original, where the caller has
    to say what component type the array should have -- Java erases the element
    type, so ``Collection<T>`` carries nothing to build a ``T[]`` from. Python
    lists are heterogeneous and have no component type, so there is nothing to
    pass and a caller may as well write ``list(ts)``.

    NOTE an empty collection is fine, and gives an empty list. The Java needs a
    component type to make an array of and takes it as a parameter; Python has no
    such need, which is the whole of the difference.

    :param ts: a collection.
    :return: the elements of ts, as a list.
    """
    return list(ts)


def round_half_up(x: float) -> int:
    """
    Round x to the nearest integer, rounding a half away from zero's side and
    towards positive infinity -- that is, exactly as ``Math.round`` does.

    NOTE do not substitute the built-in ``round`` here.  Python rounds a half to
    the nearest *even* integer, so ``round(2.5)`` is 2, where ``Math.round(2.5)``
    is 3.  They agree on 3.5 (both 4) and on -2.5 (both -2), which is precisely
    what makes the difference easy to miss.

    :param x: the value to round.
    :return: the nearest integer, halves going towards positive infinity.
    """
    return math.floor(x + 0.5)


def format_decimal_3_places(x: float) -> str:
    """
    Format x to three decimal places.

    :param x: the value to format.
    :return: x as a string with three decimal places.
    """
    scale_factor = 1000.0
    return f"{round_half_up(x * scale_factor) / scale_factor:.3f}"


def format_whole_with_commas(x: int) -> str:
    """
    Format x as a whole number with thousands separators.

    :param x: the value to format.
    :return: x as a string, for example "1,024".
    """
    return f"{x:,d}"


def format_whole(x: int) -> str:
    """
    Format x as a whole number, without separators.

    NOTE Java overloads this name, and the two overloads disagree: the ``long``
    form gives "5000" while the ``int`` form gives "5,000".  Which you get
    depends on the static type of the argument, so ``asInt`` -- which passes a
    ``long`` -- never sees separators, while a caller passing an array length
    always does.  Python cannot overload, and the separator behaviour already has
    its own name, so this function is the ``long`` form and callers wanting
    separators should say `format_whole_with_commas`.

    :param x: the value to format.
    :return: x as a string, for example "1024".
    """
    return f"{x:d}"


def as_int(x: float) -> str:
    """
    Round x and format it as a whole number.

    :param x: the value to round and format.
    :return: the rounded value as a string, without separators.
    """
    return format_whole(round_half_up(x))


def fill_random_array(random: Random, n: int, f: Callable[[Random], T]) -> list[T]:
    """
    Build a list of n elements, each produced by applying f to the given source
    of randomness.

    NOTE the Java signature also takes the element class, which it needs in order
    to instantiate a typed array.  Python needs no such thing, so the parameter
    is dropped rather than carried along as a decoration.

    :param random: the source of randomness.
    :param n: the number of elements required.
    :param f: a function from the source of randomness to an element.
    :return: a list of n elements.
    """
    return [f(random) for _ in range(n)]


def lg(n: float) -> float:
    """
    The base-two logarithm of n.

    :param n: a positive number.
    :return: log2(n).
    """
    return math.log(n) / math.log(2)
