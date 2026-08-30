from abc import ABC, abstractmethod
from collections.abc import Iterator
from typing import TypeVar

from .list_like import ListLike

Item = TypeVar("Item")


class LinkedList(ListLike[Item], ABC):
    """
    Interface to define the behavior of a linked list.
    """

    @abstractmethod
    def get_head(self) -> Item | None:
        """
        Method to get the head element of this list.

        Returns:
            the head of this list.
        """
        pass

    @abstractmethod
    def __iter__(self) -> Iterator[Item]:
        pass
