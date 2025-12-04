from abc import abstractmethod
from typing import TypeVar, Generic, Optional, Any
from ..st import ST

Key = TypeVar('Key')
Value = TypeVar('Value')

class HashTable(ST[Key, Value]):
    """
    HashTable is an abstract hash table implementation using buckets for collision resolution.
    This class supports basic operations such as insertion, retrieval, and key set generation.
    """

    class KeyValuePair(Generic[Key, Value]):
        """
        Represents a key-value pair used internally within the containing data structure.
        """
        def __init__(self, key: Key, value: Value):
            self.key = key
            self.value = value

        def __str__(self):
            return f"{self.key}->{self.value}"

        def __repr__(self):
            return str(self)

    def __init__(self, m: int):
        """
        Constructs a HashTable with a specified number of buckets.

        Args:
            m: the initial number of buckets for the hash table.
        """
        self.m = m

    def put(self, key: Key, value: Value) -> Optional[Value]:
        self.validate_key(key)
        index = self.get_index(key)
        optional_node = self.find_key_value_pair(key, index)
        
        if optional_node is not None:
            key_value_pair = optional_node
            old_value = key_value_pair.value
            key_value_pair.value = value
            return old_value
        else:
            self.insert_key_value_pair(self.KeyValuePair(key, value), index)
            return None

    def get(self, key: Key) -> Optional[Value]:
        self.validate_key(key)
        kv = self.find_key_value_pair(key, self.get_index(key))
        return kv.value if kv else None

    def get_index(self, key: Key) -> int:
        """
        Computes the index for the given key based on its hash code.
        """
        return (hash(key) & 0x7FFFFFFF) % self.m

    @abstractmethod
    def insert_key_value_pair(self, kv: 'HashTable.KeyValuePair[Key, Value]', index: int) -> None:
        pass

    @abstractmethod
    def find_key_value_pair(self, key: Key, index: int) -> Optional['HashTable.KeyValuePair[Key, Value]']:
        pass

    @abstractmethod
    def find_node_by_key(self, key: Key, index: int) -> Optional[Any]:
        pass
