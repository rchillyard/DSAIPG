from __future__ import annotations
from typing import Optional, Iterator, TypeVar
from .queue import Queue
from .element import Element

Item = TypeVar("Item")


class QueueElements(Queue[Item]):
    """
    A generic implementation of a queue using a singly linked list.
    The queue follows the FIFO (First In, First Out) principle, where elements are added at the tail (newest)
    and removed from the head (oldest).
    """

    def __init__(self) -> None:
        """
        Construct a new (empty) queue.
        """
        self.oldest: Optional[Element[Item]] = None
        self.newest: Optional[Element[Item]] = None

    def offer(self, item: Item) -> None:
        """
        Adds the specified item to the end of the queue.
        """
        # TO BE IMPLEMENTED
        pass

    def poll(self) -> Optional[Item]:
        """
        Retrieves and removes the oldest item from the queue.
        If the queue is empty, returns None.
        """
        if self.is_empty():
            return None
        else:
            # TO BE IMPLEMENTED
            return None

    def is_empty(self) -> bool:
        """
        Checks if the queue is empty.
        """
        return self.oldest is None

    def __iter__(self) -> Iterator[Item]:
        """
        Returns an iterator over the elements in this queue in proper sequence.
        """
        current = self.oldest
        while current is not None:
            yield current.item
            current = current.next

    def __len__(self) -> int:
        """
        Returns the number of elements in this queue.
        """
        count = 0
        for _ in self:
            count += 1
        return count

    def clear(self) -> None:
        """
        Removes all elements from the queue.
        """
        while not self.is_empty():
            self.poll()

    def __str__(self) -> str:
        return (
            f"Queue: next: {self.oldest}{' and others...' if self.oldest and self.oldest.next else ''}"
            if self.oldest
            else "empty"
        )
