from abc import ABC, abstractmethod
from typing import TypeVar, Generic
from collections.abc import Iterable, Iterator

Item = TypeVar("Item")


class Bag(Generic[Item], ABC, Iterable[Item]):
    """
    A Bag is a collection that allows multiple occurrences of items.
    Also known as a multi-set.
    It extends the Iterable interface, enabling iteration over its elements.
    The items in the Bag have no guaranteed order when iterated over.
    """

    @abstractmethod
    def __iter__(self) -> Iterator[Item]:
        pass

    @abstractmethod
    def __len__(self) -> int:
        pass

    @abstractmethod
    def add(self, item: Item) -> None:
        """
        Update this Bag by adding item.
        No guarantee is made regarding the ordering of Items in the iterator
        """
        pass

    @abstractmethod
    def is_empty(self) -> bool:
        """Return True if this bag is empty"""
        pass

    @abstractmethod
    def contains(self, item: Item) -> bool:
        """
        Return True if the item has at least one instance in this Bag.
        """
        pass

    @abstractmethod
    def __contains__(self, item: Item) -> bool:
        pass

    @abstractmethod
    def multiplicity(self, item: Item) -> int:
        """
        Return the multiplicity of item, that's to say the number of instances of item there are in this Bag.
        """
        pass

    @abstractmethod
    def as_array(self) -> list[Item]:
        """
        Return this Bag as a list.
        """
        pass

    @abstractmethod
    def clear(self) -> None:
        """Empty out this Bag"""
        pass
