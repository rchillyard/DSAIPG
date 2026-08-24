from __future__ import annotations

from collections.abc import Iterable, Iterator
from typing import Generic, TypeVar

from .bqs_exception import BQSException

Item = TypeVar("Item")


class DList(Generic[Item], Iterable[Item]):
    """
    Implementation of a doubly-linked list
    """

    class DElement(Generic[Item], Iterable[Item]):
        """
        DElement represents a node in a doubly-linked list.
        """

        def __init__(
            self,
            x: Item,
            p: DList.DElement[Item] | None = None,
            n: DList.DElement[Item] | None = None,
        ):
            self.item: Item = x
            self.prev: DList.DElement[Item] | None = p
            self.next: DList.DElement[Item] | None = n

        def __iter__(self) -> Iterator[Item]:
            cursor = self
            while cursor is not None:
                yield cursor.item
                cursor = cursor.next

    def __init__(self, item: Item | None = None):
        """
        Constructor which creates an empty DList or seeds it with one item.
        """
        self.head: DList.DElement[Item] | None = None
        self.tail: DList.DElement[Item] | None = None
        self.count: int = 0
        if item is not None:
            self.add_before_element(item, None)

    def add_before(self, item: Item, next_item: Item | None) -> None:
        """
        Add an item immediately before the given element
        """
        if next_item is None:
            self.add_before_element(item, None)
        else:
            first = self.find_first(next_item)
            if first is not None:
                self.add_before_element(item, first)
            else:
                raise BQSException(f"item not found: {next_item}")

    def add_after(self, item: Item, prev_item: Item) -> None:
        """
        Add an item immediately after the given element
        """
        last = self.find_last(prev_item)
        if last is not None:
            self.add_after_element(item, last)
        else:
            raise BQSException(f"item not found: {prev_item}")

    def remove(self, item: Item) -> None:
        """
        Remove the first element matching item from this DList
        """
        last = self.find_last(item)
        if last is not None:
            self.remove_element(last)
        else:
            raise BQSException(f"item not found: {item}")

    def add_before_element(
        self, item: Item, next_element: DElement[Item] | None
    ) -> None:
        """
        Add an item immediately before the given element
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def add_after_element(self, item: Item, prev_element: DElement[Item]) -> None:
        """
        Add an item immediately after the given element
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def remove_element(self, element: DElement[Item]) -> None:
        """
        Remove the element given from this DList
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def find_first(self, item: Item) -> DElement[Item] | None:
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def find_last(self, item: Item) -> DElement[Item] | None:
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def is_empty(self) -> bool:
        return self.head is None

    def size(self) -> int:
        return self.count

    def __str__(self) -> str:
        return ", ".join(str(i) for i in self)

    def __iter__(self) -> Iterator[Item]:
        return iter(self.head) if self.head else iter([])
