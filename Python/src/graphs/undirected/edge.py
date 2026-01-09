from __future__ import annotations
from dataclasses import dataclass
from typing import Generic, TypeVar

V = TypeVar("V")
E = TypeVar("E")


@dataclass(frozen=True)
class Edge(Generic[V, E]):
    a: V
    b: V
    attribute: E

    def get(self) -> V:
        return self.a

    def get_other(self, v: V) -> V:
        return self.b if v == self.a else self.a

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Edge):
            return False
        return (
            frozenset((self.a, self.b)) == frozenset((other.a, other.b))
            and self.attribute == other.attribute
        )

    def __hash__(self) -> int:
        return hash((frozenset((self.a, self.b)), self.attribute))

    def __str__(self) -> str:
        return f"{self.a}-{self.b}: {self.attribute}"
