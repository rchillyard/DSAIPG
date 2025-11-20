from typing import Generic, TypeVar, Optional, Set, Any, Dict
from .dictionary import Dictionary

K = TypeVar("K")
V = TypeVar("V")


class DictionaryHash(Dictionary[K, V]):
    """
    This is an implementation of Dictionary which delegates to Python's built-in dict.
    """

    def __init__(self) -> None:
        self._map: Dict[K, V] = {}

    def put(self, k: K, v: V) -> None:
        self._map[k] = v

    def get(self, k: K) -> Optional[V]:
        return self._map.get(k)

    def size(self) -> int:
        return len(self._map)

    def is_empty(self) -> bool:
        return len(self._map) == 0

    def contains_key(self, key: Any) -> bool:
        return key in self._map

    def clear(self) -> None:
        self._map.clear()

    def key_set(self) -> Set[K]:
        return set(self._map.keys())
