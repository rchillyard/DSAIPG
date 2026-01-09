from typing import List, TypeVar, Optional
from .select_base import Select
from .shuffle import Shuffle

X = TypeVar('X')

class SlowSelect(Select[X]):
    """
    Class SlowSelect
    """

    def __init__(self, k: int):
        """
        Args:
            k: represents how many smaller elements there are in the array.
        """
        self.k = k

    def select(self, a: List[X], k: int) -> X:
        """
        Selects the k-th smallest element.

        Args:
            a: the array
            k: the kth smallest index

        Returns:
            kth smallest value
        """
        Shuffle.shuffle(a)
        
        # In Java: X[] kArray = Arrays.copyOf(a, k); Arrays.fill(kArray, null);
        # Python doesn't have fixed size arrays filled with null exactly the same way, 
        # but we can simulate a list of size k.
        
        # However, the logic in Java uses this array to maintain the sorted k elements.
        # Let's try to mimic the logic as closely as possible to the Java implementation.
        
        k_array: List[Optional[X]] = [None] * k
        
        for x in a:
            for i in range(k - 1, -1, -1):
                current_k_val = k_array[i]
                if current_k_val is None or current_k_val > x: # type: ignore
                    if i < k - 1:
                        k_array[i + 1] = k_array[i]
                    k_array[i] = x
                else:
                    break
        
        result = k_array[k - 1]
        
        # In checking the original code: 
        # return kArray[k - 1];
        # If result is None, it means we didn't fill the array, which shouldn't happen if input size >= k
        
        if result is None:
             raise RuntimeError("Selection failed to find element (kArray not fully populated)")
             
        return result

    @staticmethod
    def shuffle(a: List[object]):
        Shuffle.shuffle(a)
