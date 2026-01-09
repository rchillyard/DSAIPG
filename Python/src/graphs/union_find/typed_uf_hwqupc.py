from __future__ import annotations

from typing import Dict, Generic, Iterable, TypeVar

from .uf_hwqupc import UF_HWQUPC
from .uf_exception import UFException
from .typed_uf import TypedUF


T = TypeVar("T")


class TypedUF_HWQUPC(UF_HWQUPC, TypedUF[T], Generic[T]):
    """Typed union-find using HWQUPC internally for elements of type T."""

    def __init__(self, ts: Iterable[T]):
        items = list(ts)
        super().__init__(len(items))
        self._map: Dict[T, int] = {t: i for i, t in enumerate(items)}

    def connected(self, t1: T, t2: T) -> bool:
        return super().is_connected(self._lookup(t1), self._lookup(t2))

    def union(self, t1: T, t2: T) -> None:
        super().union(self._lookup(t1), self._lookup(t2))

    def _lookup(self, t: T) -> int:
        x = self._map.get(t)
        if x is None:
            raise UFException(f"Element {t} does not exist")
        return x
