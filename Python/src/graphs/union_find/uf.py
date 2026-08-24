from __future__ import annotations

from abc import ABC, abstractmethod

from src.graphs.union_find.connections import Connections


class UF(Connections, ABC):
    """Union-Find interface modeled after the Java API."""

    @abstractmethod
    def components(self) -> int:
        """Return the number of components."""
        raise NotImplementedError

    @abstractmethod
    def find(self, p: int) -> int:
        """Return identifier for the component containing p."""
        raise NotImplementedError

    @abstractmethod
    def union(self, p: int, q: int) -> None:
        """Merge the components containing p and q."""
        raise NotImplementedError

    def is_connected(self, p: int, q: int) -> bool:
        """Return True if two sites are in the same component."""
        return self.find(p) == self.find(q)

    @abstractmethod
    def size(self) -> int:
        """Return the number of sites."""
        raise NotImplementedError
