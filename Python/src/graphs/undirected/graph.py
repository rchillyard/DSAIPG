from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Collection, Iterable
from typing import Generic, TypeVar

V = TypeVar("V")
Adj = TypeVar("Adj")


class Graph(Generic[V, Adj], ABC):
    @abstractmethod
    def vertices(self) -> Collection[V]:
        pass

    @abstractmethod
    def adjacent(self, vertex: V) -> Iterable[Adj]:
        pass
