from __future__ import annotations

from collections.abc import Iterator
from random import Random

from .bag import Bag, Item
from .unordered_iterator import UnorderedIterator

#: The capacity a bag starts with. Growth doubles this, and only that doubling
#: goes through _grow_from.
INITIAL_CAPACITY = 32


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

    def __init__(self, rnd: Random | None = None) -> None:
        """
        Construct an empty BagArray with an initial capacity of 32.

        Args:
            rnd: a random source, passed to any UnorderedIterator. If omitted, a
                 new Random is used. NOTE: a Random is mutable and therefore
                 unpredictable.
        """
        self._count: int = 0
        self._items: list[Item | None] | None = [None] * INITIAL_CAPACITY
        self._random: Random = rnd if rnd is not None else Random()

    @classmethod
    def of(cls, *items: Item, rnd: Random | None = None) -> BagArray[Item]:
        """
        Construct a BagArray containing the given items.

        NOTE this deliberately does not go through _grow_from: it allocates
        storage large enough at the outset. That matters because _grow_from is an
        exercise. A bag which could not even be constructed until the exercise was
        written made every test that merely uses a bag depend on it -- of the
        tests blocked that way, two thirds were testing graphs and classification
        sorts, not bags. Now only genuine growth, past the initial capacity, needs
        the exercise.

        The new bag has room to spare, so adding one more item does not
        immediately force a growth.

        Args:
            items: the items to put in the bag.
            rnd: a random source, as for the constructor.

        Returns:
            a BagArray containing exactly those items.
        """
        result = cls(rnd)
        if len(items) * 2 > INITIAL_CAPACITY:
            result._items = [None] * (len(items) * 2)
        assert result._items is not None
        result._items[: len(items)] = items
        result._count = len(items)
        return result

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
        # NOTE the first _count entries only. Scanning the whole backing list
        # meant that after clear(), which resets the count and nothing else, this
        # still answered True for items the bag no longer held -- while
        # multiplicity, which happens to start with an is_empty() guard, answered
        # zero. Two methods disagreeing about the same question.
        assert self._items is not None
        for i in self._items[: self._count]:
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
        return sum(1 for i in self._items[: self._count] if i is not None and i == item)

    def as_array(self) -> list[Item]:
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

    def _grow(self, source: list[Item | None], size: int) -> None:
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
    def _grow_from(source: list[Item | None], size: int) -> list[Item | None]:
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
