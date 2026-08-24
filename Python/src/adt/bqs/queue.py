from abc import ABC, abstractmethod
from collections.abc import Iterator
from typing import Generic, TypeVar

Item = TypeVar("Item")


class Queue(Generic[Item], ABC):
    """
    A Queue represents a First-In-First-Out (FIFO) collection of elements.
    """

    @abstractmethod
    def offer(self, item: Item) -> None:
        """
        Update this Queue by adding an item on the "newest" end.
        """
        pass

    @abstractmethod
    def poll(self) -> Item | None:
        """
        Update this Queue by taking the oldest item off the queue.

        Returns:
            the item or None if there is no such item.
        """
        pass

    @abstractmethod
    def is_empty(self) -> bool:
        """
        Returns:
            true if this queue is empty
        """
        pass

    @abstractmethod
    def __iter__(self) -> Iterator[Item]:
        pass
