"""
Ported from misc/Counter.java.
"""

from __future__ import annotations


class Counter:
    """
    A named tally. About the smallest mutable object there is, which is what makes
    it useful for showing that a method call can change something.
    """

    def __init__(self, id: str) -> None:
        """
        :param id: what this counter counts.
        """
        self.id = id
        self.count = 0

    def increment(self) -> None:
        """
        Add one to the tally.
        """
        self.count += 1

    def tally(self) -> int:
        """
        :return: the tally so far.
        """
        return self.count

    def __str__(self) -> str:
        return f"{self.id}: {self.count}"
