from __future__ import annotations
from typing import Optional, Iterator, Any, TypeVar
from .stack import Stack
from .linked_list_elements import LinkedListElements

Item = TypeVar("Item")


class StackLinkedList(Stack[Item]):
    """
    StackLinkedList is a generic implementation of the Stack interface backed by a linked list.
    """

    def __init__(self, linked_list: Optional[LinkedListElements[Item]] = None):
        self.list = linked_list if linked_list is not None else LinkedListElements()

    def push(self, item: Item) -> None:
        self.list.add(item)

    def pop(self) -> Item:
        return self.list.remove()

    def peek(self) -> Optional[Item]:
        return self.list.get_head()

    def is_empty(self) -> bool:
        return self.list.is_empty()

    def __iter__(self) -> Iterator[Item]:
        return iter(self.list)

    def __eq__(self, other: Any) -> bool:
        if self is other:
            return True
        if not isinstance(other, StackLinkedList):
            return False
        return self.list == other.list

    def __hash__(self) -> int:
        return hash(self.list)

    def __str__(self) -> str:
        return f"StackLinkedList(list={self.list})"
