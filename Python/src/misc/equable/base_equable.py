"""
Ported from misc/equable/BaseEquable.java and BaseComparableEquable.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod

from .equable import ComparableEquable, Equable


class BaseEquable(ABC):
    """
    A class which decides equality by handing back an ``Equable`` of the fields
    that count, instead of writing ``__eq__`` and ``__hash__`` out each time.

    NOTE two instances of different classes are never equal, even where their
    Equables match. That is the Java's ``getClass() != o.getClass()``, and it is
    why a Tuple never equals a ComparableTuple holding the same pair.
    """

    @abstractmethod
    def get_equable(self) -> Equable:
        """
        :return: the elements that decide this object's equality.
        """

    def __eq__(self, other: object) -> bool:
        if type(other) is not type(self):
            return NotImplemented
        return self.get_equable() == other.get_equable()

    def __hash__(self) -> int:
        return hash(tuple(self.get_equable().elements))


class BaseComparableEquable(BaseEquable):
    """
    A BaseEquable whose Equable can be ordered, so that the objects themselves can.
    """

    def compare_to(self, other: BaseEquable) -> int:
        """
        :param other: the object to compare with.
        :return: negative, zero or positive as this sorts before, with, or after it.
        """
        mine = self.get_equable()
        assert isinstance(mine, ComparableEquable), "get_equable must give a ComparableEquable"
        theirs = other.get_equable()
        assert isinstance(theirs, ComparableEquable)
        return mine.compare_to(theirs)

    def __lt__(self, other: BaseEquable) -> bool:
        return self.compare_to(other) < 0
