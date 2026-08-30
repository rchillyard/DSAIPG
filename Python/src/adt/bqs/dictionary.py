from abc import ABC, abstractmethod
from typing import Any, Generic, TypeVar

K = TypeVar("K")
V = TypeVar("V")


class Dictionary(Generic[K, V], ABC):
    """
    The purpose of defining this interface is really just to illustrate the use of an interface for encapsulation purposes.
    """

    @abstractmethod
    def put(self, k: K, v: V) -> None:
        """
        Inserts a key-value pair into the dictionary. If the key already exists, the associated value is updated.
        """
        pass

    @abstractmethod
    def get(self, k: K) -> V | None:
        """
        Retrieves the value associated with the given key in the dictionary.
        """
        pass

    @abstractmethod
    def size(self) -> int:
        """
        Returns the number of elements present.
        """
        pass

    @abstractmethod
    def is_empty(self) -> bool:
        """
        Determines if the collection or data structure is empty.
        """
        pass

    @abstractmethod
    def contains_key(self, key: Any) -> bool:
        """
        Checks if the dictionary contains a mapping for the specified key.
        """
        pass

    @abstractmethod
    def clear(self) -> None:
        """
        Removes all elements from the structure, leaving it empty.
        """
        pass

    @abstractmethod
    def key_set(self) -> set[K]:
        """
        Returns a Set view of the keys contained in the dictionary.
        """
        pass
