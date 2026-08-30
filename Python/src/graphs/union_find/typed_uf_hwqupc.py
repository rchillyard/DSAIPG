from __future__ import annotations

from collections.abc import Iterable
from typing import Generic, TypeVar

from .typed_uf import TypedUF
from .uf_exception import UFException
from .uf_hwqupc import UF_HWQUPC

T = TypeVar("T")


class TypedUF_HWQUPC(UF_HWQUPC, TypedUF[T], Generic[T]):
    """Typed union-find using HWQUPC internally for elements of type T."""

    def __init__(self, ts: Iterable[T]):
        items = list(ts)
        super().__init__(len(items))
        self._map: dict[T, int] = {t: i for i, t in enumerate(items)}

    def connected(self, t1: T, t2: T) -> bool:
        return super().is_connected(self._lookup(t1), self._lookup(t2))

    def union(self, t1: T, t2: T) -> None:
        super().union(self._lookup(t1), self._lookup(t2))

    def _lookup(self, t: T) -> int:
        x = self._map.get(t)
        if x is None:
            raise UFException(f"Element {t} does not exist")
        return x
