from __future__ import annotations


class WQUPC:
    def __init__(self, n: int):
        if n < 0:
            raise ValueError("n must be non-negative")
        self._count = n
        self._parent: list[int] = list(range(n))
        self._size: list[int] = [1] * n

    @property
    def count(self) -> int:
        return self._count

    # Java-compatible API
    def components(self) -> int:
        return self._count

    def find(self, p: int) -> int:
        self._validate(p)
        root = p
        while root != self._parent[root]:
            root = self._parent[root]
        while p != root:
            newp = self._parent[p]
            self._parent[p] = root
            p = newp
        return root

    def connected(self, p: int, q: int) -> bool:
        return self.find(p) == self.find(q)

    # Java-compatible alias
    def is_connected(self, p: int, q: int) -> bool:
        return self.connected(p, q)

    def union(self, p: int, q: int) -> None:
        root_p = self.find(p)
        root_q = self.find(q)
        if root_p == root_q:
            return
        if self._size[root_p] < self._size[root_q]:
            self._parent[root_p] = root_q
            self._size[root_q] += self._size[root_p]
        else:
            self._parent[root_q] = root_p
            self._size[root_p] += self._size[root_q]
        self._count -= 1

    # Java-compatible convenience
    def connect(self, p: int, q: int) -> None:
        if not self.connected(p, q):
            self.union(p, q)

    def size(self) -> int:
        return len(self._parent)

    def size_of(self, p: int) -> int:
        r = self.find(p)
        return self._size[r]

    def _validate(self, p: int) -> None:
        n = len(self._parent)
        if p < 0 or p >= n:
            raise IndexError(f"index {p} is not between 0 and {n - 1}")
