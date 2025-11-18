from __future__ import annotations

from typing import List

from src.graphs.union_find.uf import UF


class UF_HWQUPC(UF):
    """Height-weighted Quick Union with optional path compression."""

    def __init__(self, n: int, path_compression: bool = True):
        if n < 0:
            raise ValueError("n must be non-negative")
        self._count = n
        self._parent: List[int] = list(range(n))
        self._height: List[int] = [1] * n
        self._path_compression = path_compression

    def connect(self, p: int, q: int) -> None:
        if not self.is_connected(p, q):
            self.union(p, q)

    def components(self) -> int:
        return self._count

    def find(self, p: int) -> int:
        self._validate(p)
        root = p
        while root != self._parent[root]:
            if self._path_compression:
                self._do_path_compression(root)
            root = self._parent[root]
        return root

    def is_connected(self, p: int, q: int) -> bool:
        return self.find(p) == self.find(q)

    def union(self, p: int, q: int) -> None:
        i = self.find(p)
        j = self.find(q)
        if i == j:
            return
        self._merge_components(i, j)
        self._count -= 1

    def size(self) -> int:
        return len(self._parent)

    def set_path_compression(self, enabled: bool) -> None:
        self._path_compression = enabled

    def show(self) -> str:
        return (
            f"UF_HWQUPC:\n  count: {self._count}\n  path compression? {self._path_compression}\n"
            f"  parents: {self._parent}\n  heights: {self._height}"
        )

    def __str__(self) -> str:
        return self.show()

    def _validate(self, p: int) -> None:
        n = len(self._parent)
        if p < 0 or p >= n:
            raise IndexError(f"index {p} is not between 0 and {n - 1}")

    def _update_parent(self, p: int, x: int) -> None:
        self._parent[p] = x

    def _update_height(self, p: int, x: int) -> None:
        self._height[p] += self._height[x]

    def _get_parent(self, i: int) -> int:
        return self._parent[i]

    def _merge_components(self, i: int, j: int) -> None:
        if self._height[i] < self._height[j]:
            self._update_parent(i, j)
            self._update_height(j, i)
        else:
            self._update_parent(j, i)
            self._update_height(i, j)

    def _do_path_compression(self, i: int) -> None:
        self._update_parent(i, self._parent[self._parent[i]])