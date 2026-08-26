"""
An immutable pair of ints, ported from adt/threesum/Pair.java.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True, order=True)
class Pair:
    """
    Two ints, compared first on x and then on y.

    The Java writes equals, hashCode and compareTo out by hand. Here
    ``frozen=True`` gives ``__eq__`` and ``__hash__``, and ``order=True`` gives
    ``__lt__`` and its siblings, comparing the fields in the order declared --
    which is exactly what the Java's compareTo does.

    NOTE the Java's compareTo returns ``this.x - o.x``, which overflows for
    values far apart: Integer.MIN_VALUE - 1 is positive. It is safe here because
    the values come from Source and are bounded, but Integer.compare would have
    been the better habit. Python's ints do not overflow, so the hazard does not
    survive the port.
    """

    x: int
    y: int

    def sum(self) -> int:
        """
        :return: x + y.
        """
        return self.x + self.y

    def __str__(self) -> str:
        return f"Pair{{x={self.x}, y={self.y}}}"
