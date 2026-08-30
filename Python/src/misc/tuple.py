"""
Ported from util/general/Tuple.java and misc/ComparableTuple.java.

NOTE the Java puts Tuple in util/general and ComparableTuple in misc, though they
are the same pair of fields with and without an ordering, and both are there to
exercise the equable package. They are together here, next to what they exercise.
"""

from __future__ import annotations

from src.misc.equable.base_equable import BaseComparableEquable, BaseEquable
from src.misc.equable.equable import ComparableEquable, Equable


class Tuple(BaseEquable):
    """
    An int and a float, equal to another Tuple when both fields match. A test
    harness for ``Equable``.
    """

    def __init__(self, x: int, y: float) -> None:
        """
        :param x: the first element.
        :param y: the second element.
        """
        self.x = x
        self.y = y

    def get_x(self) -> int:
        """
        :return: the first element.
        """
        return self.x

    def get_y(self) -> float:
        """
        :return: the second element.
        """
        return self.y

    def get_equable(self) -> Equable:
        """
        :return: the two fields, which are what decide equality.
        """
        return Equable([self.x, self.y])

    def __str__(self) -> str:
        return f"Tuple({self.x}, {self.y})"


class ComparableTuple(BaseComparableEquable):
    """
    The same pair, ordered by the first element and then the second.
    """

    def __init__(self, x: int, y: float) -> None:
        """
        :param x: the first element.
        :param y: the second element.
        """
        self.x = x
        self.y = y

    def get_equable(self) -> Equable:
        """
        :return: the two fields, as something that can be ordered.
        """
        return ComparableEquable([self.x, self.y])

    def __str__(self) -> str:
        return f"Tuple({self.x}, {self.y})"


def index(hash_value: int) -> int:
    """
    Fold a 32-bit hash down to 16 bits, by XOR-ing its halves together.

    NOTE the Java prints both the hash and the result to stdout, which makes it
    unusable anywhere but a demonstration. That printing is left out; the value is
    the same.

    :param hash_value: the hash to fold.
    :return: the lower 16 bits of the hash XOR its upper 16 bits.
    """
    return (hash_value & 0xFFFF0000) >> 16 ^ hash_value & 0x0000FFFF
