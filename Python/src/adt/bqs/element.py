from __future__ import annotations

from typing import Any, Generic, TypeVar

Item = TypeVar("Item")


class Element(Generic[Item]):
    """
    This class represents a node in a singly linked data structure.
    It is generic and used to store elements of type Item.
    Each node contains a reference to an item and a reference to the next node in the structure.
    """

    def __init__(self, x: Item, n: Element[Item] | None = None):
        """
        Constructs a new Element with the given item and reference to the next element.

        Args:
            x: the item to be stored in this element
            n: the next element in the linked structure, or None if this is the last element
        """
        self.item: Item = x
        self.next: Element[Item] | None = n

    def __eq__(self, other: Any) -> bool:
        """
        Indicates whether some other object is "equal to" this one.
        """
        if self is other:
            return True
        if not isinstance(other, Element):
            return False
        return self.item == other.item and self.next == other.next

    def __hash__(self) -> int:
        """
        Computes the hash code for this element using its item and next reference.
        """
        return hash((self.item, self.next))

    def __str__(self) -> str:
        return f"{self.item}{' (last)' if self.next is None else ''}"

    def __repr__(self) -> str:
        return f"Element({self.item!r}, next={self.next!r})"
