from __future__ import annotations
from typing import Optional, Iterator, List, TypeVar
from .queue import Queue

Item = TypeVar("Item")


class QueueArray(Queue[Item]):
    """
    Class to represent a circular queue.
    """

    def __init__(self, capacity: int = 32):
        self.n = capacity
        self.items: List[Optional[Item]] = [None] * self.n
        self.i = 0  # head
        self.j = 0  # tail

    def offer(self, item: Item) -> None:
        self.items[self.j] = item
        self.j = (self.j + 1) % self.n
        self._ensure_room()

    def poll(self) -> Optional[Item]:
        if self.is_empty():
            return None
        result = self.items[self.i]
        self.items[self.i] = None  # Avoid memory leak
        self.i = (self.i + 1) % self.n
        return result

    def is_empty(self) -> bool:
        return self.i == self.j

    def __iter__(self) -> Iterator[Item]:
        if self.is_empty():
            return
        
        curr = self.i
        count = self.size()
        for _ in range(count):
            item = self.items[curr]
            if item is not None:
                yield item
            curr = (curr + 1) % self.n

    def size(self) -> int:
        return (self.n + self.j - self.i) % self.n

    def _ensure_room(self) -> None:
        if self.i == self.j:
            new_n = self.n * 2
            new_items: List[Optional[Item]] = [None] * new_n
            
            # Copy elements to the new array
            # If j <= i, it means we wrapped around
            if self.j <= self.i:
                # Copy from i to end of old array
                part1_len = self.n - self.i
                new_items[0:part1_len] = self.items[self.i : self.n]
                # Copy from 0 to j
                new_items[part1_len : part1_len + self.j] = self.items[0 : self.j]
            else:
                # No wrap around (shouldn't happen if full, but for safety)
                new_items[0 : self.n] = self.items[0 : self.n]

            self.items = new_items
            self.i = 0
            self.j = self.n  # The old capacity is the new tail
            self.n = new_n

    def __str__(self) -> str:
        return f"QueueArray(size={self.size()}, items={list(self)})"
