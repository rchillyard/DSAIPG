from __future__ import annotations
from typing import TypeVar, Generic, Optional, Iterator, Iterable
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
            p: Optional[DList.DElement[Item]] = None,
            n: Optional[DList.DElement[Item]] = None,
        ):
            self.item: Item = x
            self.prev: Optional[DList.DElement[Item]] = p
            self.next: Optional[DList.DElement[Item]] = n

        def __iter__(self) -> Iterator[Item]:
            cursor = self
            while cursor is not None:
                yield cursor.item
                cursor = cursor.next

    def __init__(self, item: Optional[Item] = None):
        """
        Constructor which creates an empty DList or seeds it with one item.
        """
        self.head: Optional[DList.DElement[Item]] = None
        self.tail: Optional[DList.DElement[Item]] = None
        self.count: int = 0
        if item is not None:
            self.add_before_element(item, None)

    def add_before(self, item: Item, next_item: Optional[Item]) -> None:
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
        self, item: Item, next_element: Optional[DElement[Item]]
    ) -> None:
        """
        Add an item immediately before the given element
        """
        # TO BE IMPLEMENTED
        raise RuntimeError("implementation missing")

    def add_after_element(self, item: Item, prev_element: DElement[Item]) -> None:
        """
        Add an item immediately after the given element
        """
        # TO BE IMPLEMENTED
        raise RuntimeError("implementation missing")

    def remove_element(self, element: DElement[Item]) -> None:
        """
        Remove the element given from this DList
        """
        # TO BE IMPLEMENTED
        raise RuntimeError("implementation missing")

    def find_first(self, item: Item) -> Optional[DElement[Item]]:
        # TO BE IMPLEMENTED
        return None

    def find_last(self, item: Item) -> Optional[DElement[Item]]:
        # TO BE IMPLEMENTED
        return None

    def is_empty(self) -> bool:
        return self.head is None

    def size(self) -> int:
        return self.count

    def __str__(self) -> str:
        return ", ".join(str(i) for i in self)

    def __iter__(self) -> Iterator[Item]:
        return iter(self.head) if self.head else iter([])
