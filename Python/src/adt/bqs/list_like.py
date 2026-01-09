from abc import ABC, abstractmethod
from typing import Generic, TypeVar

Item = TypeVar("Item")


class ListLike(Generic[Item], ABC):
    """
    Interface to model the behavior of generic list-like object.
    This interface does not specify where elements should be added or removed.
    """

    @abstractmethod
    def add(self, item: Item) -> None:
        """Method to add an element to this list."""
        pass

    @abstractmethod
    def remove(self) -> Item:
        """
        Method to remove an element from this list

        Returns:
            the item removed from the list.
        Raises:
            BQSException: the list was empty or the item to remove was otherwise undefined.
        """
        pass

    @abstractmethod
    def is_empty(self) -> bool:
        """
        Method to determine if this list is empty.

        Returns:
            true if this list is empty.
        """
        pass
