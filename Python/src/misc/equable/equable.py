"""
Ported from misc/equable/Equable.java and ComparableEquable.java.
"""

from __future__ import annotations

from collections.abc import Collection


class Equable:
    """
    A sequence of elements which stands in for an object's identity.

    A class defines what makes two of its instances equal by handing back an
    Equable of the fields that count, rather than writing ``__eq__`` and
    ``__hash__`` out longhand every time. See ``BaseEquable``.
    """

    def __init__(self, elements: Collection[object]) -> None:
        """
        :param elements: the values that decide equality, in order.
        """
        self.elements = list(elements)

    def __eq__(self, other: object) -> bool:
        """
        Two Equables are equal when they hold the same elements in the same order.
        Note the length check: equality must be symmetric, so a shorter Equable
        matching a prefix of a longer one is NOT equal to it.

        :param other: what to compare with.
        :return: whether both hold the same elements in the same order.
        """
        if type(other) is not type(self):
            return NotImplemented
        return self.elements == other.elements

    def __hash__(self) -> int:
        result = 0
        for element in self.elements:
            result = 31 * result + hash(element)
        return result

    def __repr__(self) -> str:
        return f"{type(self).__name__}({self.elements})"


class ComparableEquableException(Exception):
    """
    Raised when two Equables cannot meaningfully be ordered.
    """


class ComparableEquable(Equable):
    """
    An Equable whose elements can be put in order, so that the Equables themselves
    can be: lexicographically, first element first.
    """

    def compare_to(self, other: ComparableEquable) -> int:
        """
        NOTE the length check, for the same reason as in ``Equable.__eq__``: the
        Java's rule that both must be the same length only ever fired when THIS one
        was the longer, so the shorter compared with the longer reported 0 -- equal
        -- where the reverse raised.

        :param other: the Equable to compare with.
        :return: negative, zero or positive as this sorts before, with, or after it.
        :raises ComparableEquableException: if the two are of different lengths, or
                                            an element cannot be ordered.
        """
        if len(self.elements) != len(other.elements):
            raise ComparableEquableException(
                "ComparableEquable can only compare Equables of the same length"
            )
        for mine, theirs in zip(self.elements, other.elements):
            try:
                if mine < theirs:
                    return -1
                if theirs < mine:
                    return 1
            except TypeError as e:
                raise ComparableEquableException(
                    "ComparableEquable can only compare elements which are themselves comparable"
                ) from e
        return 0

    def __lt__(self, other: ComparableEquable) -> bool:
        return self.compare_to(other) < 0
