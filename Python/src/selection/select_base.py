from abc import ABC, abstractmethod
from typing import List, TypeVar, Generic

X = TypeVar('X')

class Select(ABC, Generic[X]):
    """
    A functional interface for selecting the k-th smallest element from an array.
    """

    @abstractmethod
    def select(self, a: List[X], k: int) -> X:
        """
        Selects the k-th smallest element from the given array.

        Args:
            a: the input array of elements to search within. All elements must implement the Comparable interface.
            k: the index (0-based) of the smallest element to find; must be between 0 and a.length - 1.

        Returns:
            the k-th smallest element in the input array.

        Raises:
            ValueError: if the value of k is out of the valid range (0 to a.length - 1).
        """
        pass
