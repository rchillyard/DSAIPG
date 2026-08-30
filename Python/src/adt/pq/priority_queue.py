from abc import ABC, abstractmethod
from typing import Generic, TypeVar

K = TypeVar('K')

class PriorityQueue(ABC, Generic[K]):
    """
    An interface that represents a priority queue structure, allowing efficient
    addition and extraction of elements based on their priority.
    The priority queue may support maximum or minimum priority orders depending
    on its implementation.
    """

    @abstractmethod
    def is_empty(self) -> bool:
        """
        Checks if the priority queue is empty.

        Returns:
            True if the priority queue contains no elements, False otherwise.
        """
        pass

    @abstractmethod
    def size(self) -> int:
        """
        Retrieves the number of elements currently stored in the priority queue.

        Returns:
            The number of elements in the priority queue.
        """
        pass

    @abstractmethod
    def give(self, key: K) -> None:
        """
        Attempts to add the specified element to the priority queue.

        Args:
            key: the element to be added to the priority queue.
        """
        pass

    @abstractmethod
    def take(self) -> K:
        """
        Removes and returns the highest-priority element from the priority queue.
        If max is False (it's a minimum PQ), then this will result in the smallest item.
        If the queue is empty, throws a PQException.

        Returns:
            The highest-priority element in the priority queue.

        Throws:
            PQException: if the priority queue is empty.
        """
        pass

    @abstractmethod
    def heap_constructor(self) -> None:
        """
        Constructs or initializes the underlying data structure for the Priority Queue
        in order to prepare it for efficient insertion and extraction operations.
        Typically used to build the heap representation of the priority queue based
        on its current elements.
        """
        pass

    @abstractmethod
    def peek(self, k: int) -> K | None:
        """
        Retrieves the element at the specified position in the priority queue without removing it.
        WARNING: this is primarily for testing -- not recommended for general use.

        Args:
            k: the position index of the element to peek at.

        Returns:
            The element at the specified position in the priority queue.
        """
        pass
