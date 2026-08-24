from __future__ import annotations

from random import Random
from typing import Iterator, List, Optional

from .bag import Bag, Item
from .unordered_iterator import UnorderedIterator


class BagArray(Bag[Item]):
    """
    An implementation of the Bag interface using an array as the underlying storage.
    This class provides basic functionality such as adding elements, checking for
    containment, and retrieving the size of the bag. The bag does not maintain any
    specific order of elements. Internal capacity automatically grows when required
    to accommodate more items.

    NOTE: the backing store is deliberately a fixed-capacity list rather than a
    plain Python list. Growing it by hand is the point of the exercise in
    `_grow_from`; a list that grows itself would remove that exercise.
    """

    __slots__ = ("_items", "_count", "_random")

    def __init__(self, rnd: Optional[Random] = None) -> None:
        """
        Construct an empty BagArray with an initial capacity of 32.

        Args:
            rnd: a random source, passed to any UnorderedIterator. If omitted, a
                 new Random is used. NOTE: a Random is mutable and therefore
                 unpredictable.
        """
        self._count: int = 0
        self._items: Optional[List[Optional[Item]]] = None
        self._grow([], 32)
        self._random: Random = rnd if rnd is not None else Random()

    def add(self, item: Item) -> None:
        """
        Add the specified item to the bag, expanding the internal storage if it is full.
        """
        assert self._items is not None
        if self._full():
            self._grow(self._items, 2 * self._capacity())
        self._items[self._count] = item
        self._count += 1

    def is_empty(self) -> bool:
        return self._count == 0

    def __len__(self) -> int:
        return self._count

    def clear(self) -> None:
        self._count = 0

    def contains(self, item: Item) -> bool:
        assert self._items is not None
        for i in self._items:
            if i is not None and i == item:
                return True
        return False

    def __contains__(self, item: Item) -> bool:
        return self.contains(item)

    def multiplicity(self, item: Item) -> int:
        """
        Return the number of instances of item in this bag.
        """
        assert self._items is not None
        if self.is_empty():
            return 0
        return sum(1 for i in self._items if i is not None and i == item)

    def as_array(self) -> List[Item]:
        """
        Return this bag's items as a list, excluding the unused tail of the
        backing store.
        """
        assert self._items is not None
        return list(self._items[: self._count])

    def __iter__(self) -> Iterator[Item]:
        """
        Return a randomly ordered iterator over this bag.
        """
        return UnorderedIterator(self.as_array(), self._random)

    def __repr__(self) -> str:
        return f"BagArray(items={self.as_array()!r}, count={self._count})"

    def _grow(self, source: List[Optional[Item]], size: int) -> None:
        """
        Replace the internal storage with a copy of source expanded to size.
        """
        self._items = self._grow_from(source, size)

    def _capacity(self) -> int:
        assert self._items is not None
        return len(self._items)

    def _full(self) -> bool:
        return len(self) == self._capacity()

    @staticmethod
    def _grow_from(source: List[Optional[Item]], size: int) -> List[Optional[Item]]:
        """
        This fairly primitive grow method takes a list called source, creates a new
        list of the given size, copies all the elements of source into the start of
        the result, then returns the result. Unused slots must be None.

        Args:
            source: the source list.
            size: the size of the new list.
        """
        # TO BE IMPLEMENTED  grow array and copy
        raise NotImplementedError("TO BE IMPLEMENTED")
        # END SOLUTION
