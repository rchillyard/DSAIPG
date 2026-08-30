"""
Ported from misc/reduction/Point.java.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class Point:
    """
    A pair of integers, and whether both are positive.
    """

    x: int
    y: int

    def valid(self) -> bool:
        """
        :return: whether both coordinates are positive.
        """
        return self.x > 0 and self.y > 0

    def __str__(self) -> str:
        return f"Point{{x={self.x}, y={self.y}}}"
