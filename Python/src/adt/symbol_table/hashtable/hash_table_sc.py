from collections.abc import Iterator
from typing import Optional, TypeVar

from .hash_table import HashTable

Key = TypeVar('Key')
Value = TypeVar('Value')

class HashTableSC(HashTable[Key, Value]):
    """
    HashTable_SC is a hash table implementation using separate chaining (SC) for collision resolution.
    """

    class Node:
        def __init__(self, key_value_pair: 'HashTable.KeyValuePair[Key, Value]', next_node: Optional['HashTableSC.Node'] = None):
            self.key_value_pair = key_value_pair
            self.next = next_node

        def __str__(self):
            return str(self.key_value_pair)

    def __init__(self, m: int = 16):
        super().__init__(m)
        self._buckets: list[HashTableSC.Node | None] = [None] * m

    def size(self) -> int:
        result = 0
        for bucket in self._buckets:
            result += sum(1 for _ in self._nodes_as_stream(bucket))
        return result

    def keys(self) -> set[Key]:
        result = set()
        for bucket in self._buckets:
            for node in self._nodes_as_stream(bucket):
                result.add(node.key_value_pair.key)
        return result

    def insert_key_value_pair(self, kv: 'HashTable.KeyValuePair[Key, Value]', index: int) -> None:
        bucket = self._buckets[index]
        node = self.Node(kv, bucket)
        self._buckets[index] = node

    def find_key_value_pair(self, key: Key, index: int) -> Optional['HashTable.KeyValuePair[Key, Value]']:
        optional_node = self.find_node_by_key(key, index)
        return optional_node.key_value_pair if optional_node else None

    def find_node_by_key(self, key: Key, index: int) -> Optional['HashTableSC.Node']:
        bucket = self._buckets[index]
        matches = [node for node in self._nodes_as_stream(bucket) if node.key_value_pair.key == key]
        if len(matches) > 1:
            raise Exception(f"HashTable:findNode: logic error: more than one matching key: {key} at index: {index}")
        return matches[0] if matches else None

    def delete(self, key: Key) -> Value | None:
        self.validate_key(key)
        index = self.get_index(key)
        optional_node = self.find_node_by_key(key, index)
        
        if optional_node:
            node_to_delete = optional_node
            bucket = self._buckets[index]
            
            # Reconstruct the list without the deleted node
            # This is a bit inefficient compared to pointer manipulation but safer in Python
            # Actually, let's do pointer manipulation to match Java logic
            
            if bucket == node_to_delete:
                self._buckets[index] = node_to_delete.next
            else:
                prev = None
                curr = bucket
                while curr and curr != node_to_delete:
                    prev = curr
                    curr = curr.next
                
                if prev and curr:
                    prev.next = curr.next
            
            return node_to_delete.key_value_pair.value
        else:
            return None

    def _nodes_as_stream(self, bucket: Optional['HashTableSC.Node']) -> Iterator['HashTableSC.Node']:
        curr = bucket
        while curr:
            yield curr
            curr = curr.next
