from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Generic, TypeVar


T = TypeVar("T")


class TypedUF(Generic[T], ABC):
    """Typed Union-Find interface supporting generic elements."""

    @abstractmethod
    def connected(self, t1: T, t2: T) -> bool:
        raise NotImplementedError

    @abstractmethod
    def union(self, t1: T, t2: T) -> None:
        raise NotImplementedError
