# Position as an immutable value type

from dataclasses import dataclass


@dataclass(frozen=True)
class Position:
    x: float
    y: float
