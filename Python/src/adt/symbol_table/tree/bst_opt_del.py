from typing import TypeVar, Generic, Optional, Set, Iterable, Any, Dict, List, Callable
import random
from src.adt.symbol_table.tree.bst import BST

K = TypeVar('K')
V = TypeVar('V')

class BSTOptimisedDeletion(BST[K, V]):
    """
    Binary Search Tree that is not simple and which has optimized deletion mechanism.
    """

    class _Node:
        def __init__(self, key: K, value: V, depth: int, mode: int):
            self.key = key
            self.value = value
            self.depth = depth
            self.count = 1
            self.smaller: Optional['BSTOptimisedDeletion._Node'] = None
            self.larger: Optional['BSTOptimisedDeletion._Node'] = None
            self.mode = mode

        def __str__(self):
            sb = [f"Node: {self.key}:{self.value}@{self.depth} with count={self.count}"]
            if self.smaller:
                sb.append(f", smaller: {self.smaller.key}")
            if self.larger:
                sb.append(f", larger: {self.larger.key}")
            return "".join(sb)

        def navigate(self, k: Optional[K], 
                     function1: Callable[['BSTOptimisedDeletion._Node', Optional['BSTOptimisedDeletion._Node']], Optional['BSTOptimisedDeletion._Node']], 
                     function2: Callable[['BSTOptimisedDeletion._Node', Optional['BSTOptimisedDeletion._Node']], Optional['BSTOptimisedDeletion._Node']]) -> Optional['BSTOptimisedDeletion._Node']:
            if k is not None:
                if k == self.key:
                    return function1(self, self)
                elif k < self.key:
                    return self._navigate_subtree(self.smaller, k, function1, function2)
                else:
                    return self._navigate_subtree(self.larger, k, function1, function2)
            else:
                self._navigate_subtree(self.smaller, None, function1, function2)
                self._navigate_subtree(self.larger, None, function1, function2)
                return None

        def _navigate_subtree(self, subtree: Optional['BSTOptimisedDeletion._Node'], k: Optional[K],
                              function1: Callable, function2: Callable) -> Optional['BSTOptimisedDeletion._Node']:
            node = subtree.navigate(k, function1, function2) if subtree else function1(self, None)
            return function2(self, node)

        def update_count(self, other: Optional['BSTOptimisedDeletion._Node']) -> Optional['BSTOptimisedDeletion._Node']:
            self.count = 1 + (self.smaller.count if self.smaller else 0) + (self.larger.count if self.larger else 0)
            return other

        def delete(self, k: K) -> Optional['BSTOptimisedDeletion._Node']:
            # CONSIDER using navigate
            # TO BE IMPLEMENTED
            return None

        def reduce_depth(self) -> 'BSTOptimisedDeletion._Node':
            self.depth -= 1
            if self.smaller:
                self.smaller.reduce_depth()
            if self.larger:
                self.larger.reduce_depth()
            return self

        def validate(self, d: int) -> None:
            assert self.depth == d, f"At node {self.key}: incorrect depth value: {self.depth} but should be {d}"
            if self.smaller:
                assert self.smaller.key < self.key, "Symmetric order violation"
                self.smaller.validate(d + 1)
            if self.larger:
                assert self.larger.key > self.key, "Symmetric order violation"
                self.larger.validate(d + 1)

    def __init__(self, map_data: Optional[Dict[K, V]] = None, mode: int = 0):
        self.root: Optional[BSTOptimisedDeletion._Node] = None
        self.mode = mode
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
        if not self.root:
            return None
        do_get = lambda node1, node2: node2
        result = self.root.navigate(key, do_get, do_get)
        return result.value if result else None

    def put(self, key: K, value: V) -> Optional[V]:
        if self.root:
            result = self.root.navigate(key, self._do_put(key, value), lambda n1, n2: n1.update_count(n2))
            return result.value if result else None
        else:
            self.root = self._make_node(key, value, 0)
            return value

    def delete(self, key: K) -> Optional[V]:
        if self.root:
            self.root = self.root.delete(key)
        return None

    def keys(self) -> Iterable[K]:
        # TODO: Implement properly
        return []

    def depth(self, key: Optional[K] = None) -> int:
        if key is None:
            return self._depth_node(self.root)
        else:
            try:
                return self._depth_key(self.root, key)
            except ValueError:
                return -1

    def mean_depth(self) -> float:
        depth_stats = self._Depth()
        if self.root:
            self._measure_depth(self.root, depth_stats)
        return depth_stats.get_mean_depth()

    def _measure_depth(self, node: Optional[_Node], stats: '_Depth'):
        if not node:
            return
        stats.increment(node.depth)
        self._measure_depth(node.smaller, stats)
        self._measure_depth(node.larger, stats)

    def validate(self) -> None:
        if self.root:
            self.root.validate(0)

    # --- Helper Methods ---

    def _do_put(self, key: K, value: V) -> Callable:
        def func(node1: Optional['BSTOptimisedDeletion._Node'], node2: Optional['BSTOptimisedDeletion._Node']):
            if node2:
                node2.value = value
                return node2
            else:
                if node1:
                    node1.count += 1
                    node = self._make_node(key, value, node1.depth + 1)
                    if key < node1.key:
                        node1.smaller = node
                    elif key > node1.key:
                        node1.larger = node
                    else:
                        raise RuntimeError("put: Logic error")
                    return node
                else:
                    assert False, "this is impossible"
        return func

    def _make_node(self, key: K, value: V, depth: int) -> _Node:
        return self._Node(key, value, depth, self.mode)

    def _depth_node(self, node: Optional[_Node]) -> int:
        if not node:
            return 0
        depth_s = self._depth_node(node.smaller)
        depth_l = self._depth_node(node.larger)
        return 1 + max(depth_l, depth_s)

    def _depth_key(self, node: Optional[_Node], key: K) -> int:
        if not node:
            raise ValueError("Key not found")
        if key < node.key:
            return 1 + self._depth_key(node.smaller, key)
        elif key > node.key:
            return 1 + self._depth_key(node.larger, key)
        else:
            return 0

    class _Depth:
        def __init__(self):
            self.nodes = 0
            self.total_depth = 0

        def increment(self, depth: int):
            self.nodes += 1
            self.total_depth += depth

        def get_mean_depth(self) -> float:
            return self.total_depth / self.nodes if self.nodes > 0 else 0.0

    def __str__(self):
        sb = []
        self._show(self.root, sb, 0)
        return "".join(sb)

    def _show(self, node: Optional[_Node], sb: List[str], indent: int):
        if not node:
            return
        sb.append("  " * max(0, indent))
        sb.append(f"{node.key}: {node.value} @ depth {node.depth} with count {node.count}\n")
        if node.smaller:
            sb.append("  " * max(0, indent + 1))
            sb.append("smaller: ")
            self._show(node.smaller, sb, indent + 1)
        if node.larger:
            sb.append("  " * max(0, indent + 1))
            sb.append("larger: ")
            self._show(node.larger, sb, indent + 1)
