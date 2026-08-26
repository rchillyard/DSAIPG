"""
An immutable triple of ints, ported from adt/threesum/Triple.java.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True, order=True)
class Triple:
    """
    Three ints, compared on x, then y, then z.

    See Pair for why this is a frozen ordered dataclass rather than a hand-written
    equals/hashCode/compareTo.
    """

    x: int
    y: int
    z: int

    def sum(self) -> int:
        """
        :return: x + y + z.
        """
        return self.x + self.y + self.z

    def __str__(self) -> str:
        return f"Triple{{x={self.x}, y={self.y}, z={self.z}}}"
