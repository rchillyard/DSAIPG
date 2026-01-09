from __future__ import annotations
from typing import TypeVar, Generic, Callable, List, Optional

T = TypeVar("T")
U = TypeVar("U")


class LazyList(Generic[T]):
    """
    Class to demonstrate that it is possible to create a LazyList.
    NOTE: this does not represent a true lazy list because it does not memoize elements.
    """

    def __init__(self, head: T, tail_function: Callable[[], LazyList[T]]):
        self.head: T = head
        self.tail_function: Callable[[], LazyList[T]] = tail_function

    def prepend(self, t: T) -> LazyList[T]:
        """
        Method to prepend the value t to the head of this LazyList.
        """
        return LazyList(t, lambda: self)

    def take(self, n: int) -> List[T]:
        """
        Method to take a number (n) of elements from this LazyList.
        """
        result: List[T] = []
        cursor: Optional[LazyList[T]] = self
        while n > 0 and cursor is not None:
            result.append(cursor.head)
            cursor = cursor.tail_function()
            n -= 1
        return result

    def take_while(self, predicate: Callable[[T], bool]) -> List[T]:
        """
        Method to take elements from this LazyList as long as they satisfy the given predicate.
        """
        result: List[T] = []
        cursor: Optional[LazyList[T]] = self
        while cursor is not None and predicate(cursor.head):
            result.append(cursor.head)
            cursor = cursor.tail_function()
        return result

    @staticmethod
    def map(lazy_list: LazyList[T], f: Callable[[T], U]) -> LazyList[U]:
        """
        Method to map a LazyList given a function.
        """
        if lazy_list is None:
            raise ValueError("list is null")
        head = lazy_list.head
        if head is not None:
            return LazyList(f(head), lambda: LazyList.map(lazy_list.tail_function(), f))
        else:
            raise ValueError("head is null")

    @staticmethod
    def iterate(start: T, next_func: Callable[[T], T]) -> LazyList[T]:
        """
        Method to create a LazyList given a starting value and a function.
        """

        def supplier() -> LazyList[T]:
            return LazyList.iterate(next_func(start), next_func)

        return LazyList(start, supplier)

    @staticmethod
    def from_start(start: int, step: int = 1) -> LazyList[int]:
        """
        Method to create a LazyList of Integers given a starting value and an increment.
        """
        return LazyList.iterate(start, lambda x: x + step)
