from typing import TypeVar, Optional, Set, List, Any
from .hash_table import HashTable

Key = TypeVar('Key')
Value = TypeVar('Value')

class HashTableLP(HashTable[Key, Value]):
    """
    Class which implements ST (symbol table) by using Linear Probing (Open Addressing).
    """

    class HashTableException(Exception):
        pass

    def __init__(self, capacity: int):
        super().__init__(capacity)
        self._elements: List[Optional[HashTable.KeyValuePair[Key, Value]]] = [None] * capacity
        self._size = 0

    def size(self) -> int:
        return self._size

    def is_empty(self) -> bool:
        return self._size == 0

    def put(self, key: Key, value: Value) -> Optional[Value]:
        if self._size >= self.m - 1:
            raise self.HashTableException("table is full")
        
        self.validate_key(key)
        index = self._find_matching_index(key, self.get_index(key))
        
        element = self._elements[index]
        if element is not None:
            # Update existing
            old_value = element.value
            element.value = value
            return old_value
        else:
            # Insert new
            self._elements[index] = self.KeyValuePair(key, value)
            self._size += 1
            return None

    def get(self, key: Key) -> Optional[Value]:
        self.validate_key(key)
        index = self.get_index(key)
        maybe_element = self.find_node_by_key(key, index)
        return maybe_element.value if maybe_element else None

    def delete(self, key: Key) -> Optional[Value]:
        # Java implementation returns null (not implemented?)
        return None

    def keys(self) -> Set[Key]:
        result = set()
        for elem in self._elements:
            if elem is not None:
                result.add(elem.key)
        return result

    def insert_key_value_pair(self, kv: 'HashTable.KeyValuePair[Key, Value]', index: int) -> None:
        # Not used directly in put override, but required by abstract base
        pass

    def find_key_value_pair(self, key: Key, index: int) -> Optional['HashTable.KeyValuePair[Key, Value]']:
        # Not used directly in put/get override, but required by abstract base
        return None

    def find_node_by_key(self, key: Key, index: int) -> Optional['HashTable.KeyValuePair[Key, Value]']:
        i = self._find_matching_index(key, index)
        return self._elements[i]

    def _find_matching_index(self, key: Key, index: int) -> int:
        result = index
        while self._elements[result] is not None:
            if self._elements[result].key == key:
                return result
            else:
                result += 1
                if result == self.m:
                    result = 0
        return result
