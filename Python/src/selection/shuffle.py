from typing import List, TypeVar, Generic, Optional
from .entropy import Entropy

X = TypeVar('X')

class Shuffle(Generic[X]):
    """
    The `Shuffle` class provides utility methods for shuffling an array of objects.
    It implements the Fisher-Yates (Knuth) shuffle algorithm.
    """

    def __init__(self, a: List[X], entropy: Optional[Entropy] = None):
        """
        Constructs a new instance of the Shuffle class.
        
        Args:
            a: the list of objects to be shuffled.
            entropy: optional entropy source. If None, it is calculated based on list size.
        """
        self.a = a
        if entropy is None:
            self.entropy = self.get_entropy(len(a))
        else:
            self.entropy = entropy

    def shuffle_list(self) -> List[X]:
        """
        Shuffles the elements of the initial list in random order using the Knuth shuffle algorithm.
        
        Returns:
            a new list containing the elements of the initial list, randomized in order.
        """
        result = list(self.a)
        for i in range(1, len(self.a)):
            random_idx = int(self.entropy.get_random(i + 1))
            self._swap(result, i, random_idx)
        return result

    @staticmethod
    def get_entropy(n: int) -> Entropy:
        return Entropy(Shuffle.calculate_n_bits(n))

    @staticmethod
    def calculate_n_bits(n: int) -> int:
        bits = 0
        k = 0
        m = 1
        while m < n:
            j = Shuffle._powers_of_2(k)
            m += j
            k += 1
            bits += k * j
        return bits - (m - n) * k

    @staticmethod
    def shuffle(a: List[object]):
        """
        Knuth shuffle in-place.
        """
        length = len(a)
        if length < 2:
            return
        shuffler = Shuffle(a)
        shuffled = shuffler.shuffle_list()
        for i in range(length):
            a[i] = shuffled[i]

    @staticmethod
    def _powers_of_2(k: int) -> int:
        return 1 << k

    @staticmethod
    def _swap(a: List[object], i: int, j: int):
        if i == j:
            return
        a[i], a[j] = a[j], a[i]
