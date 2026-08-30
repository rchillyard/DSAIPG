from abc import abstractmethod
from typing import TypeVar

from src.adt.symbol_table.st import ST

K = TypeVar('K')
V = TypeVar('V')

class BST(ST[K, V]):
    """
    Abstract Base Class for Binary Search Tree.
    Combines BST and BstDetail interfaces from Java.
    """

    @abstractmethod
    def put_all(self, map_data: dict[K, V]) -> None:
        """
        Insert all key-value pairs from the map into the BST.
        """
        pass

    @abstractmethod
    def depth(self, key: K | None = None) -> int:
        """
        Return the depth of the tree (if key is None) or the depth of the key.
        """
        pass

    @abstractmethod
    def mean_depth(self) -> float:
        """
        Return the mean depth of the nodes in the BST.
        """
        pass
