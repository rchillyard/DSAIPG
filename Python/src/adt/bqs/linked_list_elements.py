from __future__ import annotations
from typing import Optional, Iterator, Any, TypeVar
from .element import Element
from .linked_list import LinkedList
from .bqs_exception import BQSException

Item = TypeVar("Item")


class LinkedListElements(LinkedList[Item]):
    """
    Concrete class which implements LinkedList of Item as a sequence of Elements.
    """

    def __init__(self) -> None:
        self.head: Optional[Element[Item]] = None

    def add(self, item: Item) -> None:
        """
        Add the given element to the head of this list.
        """
        tail = self.head
        self.head = Element(item, tail)

    def remove(self) -> Item:
        """
        Remove the element at the head of this list.
        """
        if self.head is None:
            raise BQSException("collection is empty")
        result = self.head.item
        self.head = self.head.next
        return result

    def get_head(self) -> Optional[Item]:
        """
        Method to get the element at the head of this list without any mutation.
        Equivalent to add(remove()).
        """
        return None if self.is_empty() else self.head.item  # type: ignore

    def is_empty(self) -> bool:
        return self.head is None

    def __iter__(self) -> Iterator[Item]:
        """
        Method to yield an iterator for this list.
        """
        current = self.head
        while current is not None:
            yield current.item
            current = current.next

    def __str__(self) -> str:
        return str(list(self))

    def __eq__(self, other: Any) -> bool:
        if self is other:
            return True
        if not isinstance(other, LinkedListElements):
            return False
        return self.head == other.head

    def __hash__(self) -> int:
        return hash(self.head)
