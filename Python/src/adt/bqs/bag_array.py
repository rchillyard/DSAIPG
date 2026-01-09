from __future__ import annotations
from typing import Iterable, Iterator, Optional
from random import Random
from .bag import Bag, Item


class BagArray(Bag[Item]):
    __slots__ = ("_items", "_random")

    def __init__(
        self, items: Iterable[Item] = (), rnd: Optional[Random] = None
    ) -> None:
        self._items = list(items)
        self._random = rnd if rnd is not None else Random()

    def add(self, item: Item) -> None:
        self._items.append(item)

    def is_empty(self) -> bool:
        return not self._items

    def contains(self, item: Item) -> bool:
        return item in self._items

    def __contains__(self, item: Item) -> bool:
        return item in self._items

    def multiplicity(self, item: Item) -> int:
        return sum(1 for i in self._items if i == item)

    def as_array(self) -> list[Item]:
        return list(self._items)

    def clear(self) -> None:
        self._items.clear()

    def __len__(self) -> int:
        return len(self._items)

    def __iter__(self) -> Iterator[Item]:
        if not self._items:
            return iter(())
        indices = list(range(len(self._items)))
        self._random.shuffle(indices)
        for i in indices:
            yield self._items[i]

    def __repr__(self) -> str:
        return f"BagArray(items={self._items!r}, count={len(self)})"
