from typing import TypeVar, Generic, Optional, Set, Iterable, Any, Dict, List
import random
from src.adt.symbol_table.tree.bst import BST

K = TypeVar('K')
V = TypeVar('V')

class BSTSimple(BST[K, V]):
    """
    A simple implementation of a Binary Search Tree (BST).
    """

    class _Node:
        def __init__(self, key: K, value: V, count: int):
            self.key = key
            self.value = value
            self.count = count
            self.depth = 0
            self.smaller: Optional['BSTSimple._Node'] = None
            self.larger: Optional['BSTSimple._Node'] = None

        def __str__(self):
            sb = [f"Node: {self.key}:{self.value}"]
            if self.smaller:
                sb.append(f", smaller: {self.smaller.key}")
            if self.larger:
                sb.append(f", larger: {self.larger.key}")
            return "".join(sb)

        def min_node(self) -> 'BSTSimple._Node':
            return self.smaller.min_node() if self.smaller else self

        def get_depth(self) -> int:
            depth_s = self.smaller.get_depth() if self.smaller else 0
            depth_l = self.larger.get_depth() if self.larger else 0
            return 1 + max(depth_l, depth_s)

    def __init__(self, map_data: Optional[Dict[K, V]] = None):
        self.root: Optional[BSTSimple._Node] = None
        if map_data:
            self.put_all(map_data)

    def put_all(self, map_data: Dict[K, V]) -> None:
        keys = list(map_data.keys())
        random.shuffle(keys)
        for k in keys:
            self.put(k, map_data[k])

    @property
    def size(self) -> int:
        return self.root.count if self.root else 0

    def get(self, key: K) -> Optional[V]:
        node = self._get_node(self.root, key)
        return node.value if node else None

    def put(self, key: K, value: V) -> Optional[V]:
        # Note: Java implementation returns previous value, but standard BST put usually doesn't or returns new value.
        # The Java code returns the previous value if key existed, or null.
        # Here we will try to mimic that behavior but it's tricky with recursion if we don't pass it back.
        # The Java code uses a NodeValue tuple to return both Node and Value.
        
        # We will use a helper that returns (Node, OldValue)
        self.root, old_value = self._put(self.root, key, value)
        return old_value

    def delete(self, key: K) -> Optional[V]:
        node = self._get_node(self.root, key)
        if not node:
            return None
        original_value = node.value
        self.root = self._delete(self.root, key)
        return original_value

    def keys(self) -> Iterable[K]:
        keys_set = set()
        def visitor(k, v):
            keys_set.add(k)
        self._in_order_traverse(self.root, visitor)
        return keys_set

    def depth(self, key: Optional[K] = None) -> int:
        if key is None:
            return self.root.get_depth() if self.root else 0
        else:
            return self._depth(self.root, key)

    def mean_depth(self) -> float:
        # TODO: Implement me
        return 0.0

    # --- Helper Methods ---

    def _get_node(self, node: Optional[_Node], key: K) -> Optional[_Node]:
        if not node:
            return None
        if key < node.key:
            return self._get_node(node.smaller, key)
        elif key > node.key:
            return self._get_node(node.larger, key)
        else:
            return node

    def _put(self, node: Optional[_Node], key: K, value: V) -> tuple[Optional[_Node], Optional[V]]:
        if not node:
            return self._Node(key, value, 1), None
        
        if key == node.key:
            old_value = node.value
            node.value = value
            return node, old_value
        elif key < node.key:
            node.smaller, old_val = self._put(node.smaller, key, value)
        else:
            node.larger, old_val = self._put(node.larger, key, value)
        
        self._evaluate_count(node)
        return node, old_val

    def _delete(self, x: Optional[_Node], key: K) -> Optional[_Node]:
        if not x:
            return None
        
        if key < x.key:
            x.smaller = self._delete(x.smaller, key)
        elif key > x.key:
            x.larger = self._delete(x.larger, key)
        else:
            if not x.larger:
                return x.smaller
            if not x.smaller:
                return x.larger
            x = self._hibbard_deletion(x)
        
        self._evaluate_count(x)
        return x

    def _hibbard_deletion(self, x: _Node) -> _Node:
        # TO BE IMPLEMENTED
        return x

    def _delete_min(self, x: _Node) -> Optional[_Node]:
        if not x.smaller:
            return x.larger
        x.smaller = self._delete_min(x.smaller)
        self._evaluate_count(x)
        return x

    def _evaluate_count(self, x: Optional[_Node]) -> None:
        if not x:
            return
        count = 1
        if x.smaller:
            count += x.smaller.count
        if x.larger:
            count += x.larger.count
        x.count = count

    def _depth(self, node: Optional[_Node], key: K) -> int:
        if not node:
            raise ValueError("Key not found")
        if key < node.key:
            return 1 + self._depth(node.smaller, key)
        elif key > node.key:
            return 1 + self._depth(node.larger, key)
        else:
            return 0

    def _in_order_traverse(self, node: Optional[_Node], visitor):
        if not node:
            return
        self._in_order_traverse(node.smaller, visitor)
        visitor(node.key, node.value)
        self._in_order_traverse(node.larger, visitor)

    def __str__(self):
        sb = []
        self._show(self.root, sb, 0)
        return "".join(sb)

    def _show(self, node: Optional[_Node], sb: List[str], indent: int):
        if not node:
            return
        sb.append("  " * max(0, indent))
        sb.append(f"{node.key}: {node.value}\n")
        if node.smaller:
            sb.append("  " * max(0, indent + 1))
            sb.append("smaller: ")
            self._show(node.smaller, sb, indent + 1)
        if node.larger:
            sb.append("  " * max(0, indent + 1))
            sb.append("larger: ")
            self._show(node.larger, sb, indent + 1)
