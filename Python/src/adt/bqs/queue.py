from abc import ABC, abstractmethod
from typing import Generic, TypeVar, Optional, Iterator

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
    def poll(self) -> Optional[Item]:
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
