from abc import ABC, abstractmethod
from typing import Generic, TypeVar, Optional, Iterator

Item = TypeVar("Item")


class Stack(Generic[Item], ABC):
    """
    Interface representing a generic Stack (LIFO - Last In, First Out) data structure.
    """

    @abstractmethod
    def push(self, item: Item) -> None:
        """
        Update this Stack by adding an item on the top.
        """
        pass

    @abstractmethod
    def pop(self) -> Item:
        """
        Update this Stack by taking the top item of this Stack.

        Returns:
            the item.
        Raises:
            BQSException: if this Stack is empty.
        """
        pass

    @abstractmethod
    def peek(self) -> Optional[Item]:
        """
        Take a peek at the item on top of this Stack.

        Returns:
            the item.
        """
        pass

    @abstractmethod
    def is_empty(self) -> bool:
        """
        Returns:
            true if this stack is empty
        """
        pass

    @abstractmethod
    def __iter__(self) -> Iterator[Item]:
        pass
