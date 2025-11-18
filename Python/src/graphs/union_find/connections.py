from __future__ import annotations

from abc import ABC, abstractmethod


class Connections(ABC):
    """Connection interface for sites identified by integers."""

    @abstractmethod
    def is_connected(self, p: int, q: int) -> bool:
        """Return True if there is a (direct or indirect) connection between p and q."""
        raise NotImplementedError

    @abstractmethod
    def connect(self, p: int, q: int) -> None:
        """Connect site p with site q."""
        raise NotImplementedError