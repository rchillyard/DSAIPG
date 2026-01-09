from __future__ import annotations
from typing import TypeVar, Generic, Iterator, Iterable, List, Optional
from random import Random

T = TypeVar("T")


class UnorderedIterator(Generic[T], Iterator[T]):
    """
    Class to implement an Iterator of T based on an Iterable of T.
    The order of elements in the iterator is random.
    """

    def __init__(self, iterable: Iterable[T], random: Optional[Random] = None):
        """
        Constructor which takes an iterable.
        """
        self.list: List[T] = list(iterable)
        self.random: Random = random if random is not None else Random()

    def __iter__(self) -> Iterator[T]:
        return self

    def __next__(self) -> T:
        if not self.list:
            raise StopIteration

        # Java implementation removes a random element.
        # We can do the same.
        i = self.random.randint(0, len(self.list) - 1)
        return self.list.pop(i)

    @staticmethod
    def create_deterministic(iterable: Iterable[T], seed: int) -> UnorderedIterator[T]:
        """
        Creates a deterministic UnorderedIterator from the given iterable and seed value.
        """
        return UnorderedIterator(iterable, Random(seed))
