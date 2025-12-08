from typing import TypeVar, Generic, List, Iterable, Iterator
import bisect

T = TypeVar('T')

class OrderedArray(Generic[T]):
    """
    Abstract Data Type for an ordered array.
    Regardless of the elements that are passed to the constructor, or to the add_elements method,
    this ordered array will always be in order.
    
    Port of Java class: com.phasmidsoftware.dsaipg.adt.array.OrderedArray
    """

    def __init__(self, items: Iterable[T] = None, make_copy: bool = True):
        """
        Primary constructor.
        
        Args:
            items: The input items (list, tuple, etc.).
            make_copy: If True, creates a copy of the input items. 
                       If False and items is a list, uses the list in-place.
                       Defaults to True.
                       
        Java equivalent: 
            OrderedArray(K[] array, boolean makeCopy, Comparator<Object> comparator)
            OrderedArray(K[] array)
            OrderedArray(K[] array, boolean makeCopy)
            OrderedArray(Collection<K> input, boolean makeCopy)
            TODO: a lambda function could be used in-place of a comparator.
        """
        if items is None:
            self._array: List[T] = []
        elif not make_copy and isinstance(items, list):
            self._array = items
        else:
            self._array = list(items)
            
        self._update()

    @classmethod
    def from_values(cls, *args: T) -> 'OrderedArray[T]':
        """
        Class method to create a new OrderedArray from a varargs list of elements.
        Java equivalent: OrderedArray.from(T... args)
        """
        return cls(args)

    def get(self, i: int) -> T:
        """
        Method to get the ith element in order.
        Java equivalent: get(int i)
        """
        return self._array[i]

    def __getitem__(self, i: int) -> T:
        """
        Pythonic alias for get(i). Allows usage like arr[i].
        """
        return self.get(i)

    def add_elements(self, addition: Iterable[T]) -> None:
        """
        Method to add additional elements to this OrderedArray.
        Java equivalent: addElements(K[] addition) or addElements(Collection<K> addition)
        """
        self._array.extend(addition)
        self._update()

    def extend(self, addition: Iterable[T]) -> None:
        """
        Pythonic alias for add_elements.
        """
        self.add_elements(addition)

    def __iter__(self) -> Iterator[T]:
        """
        Returns an iterator over elements.
        Java equivalent: iterator()
        """
        return iter(self._array)

    def index_of(self, k: T) -> int:
        """
        Method to get the index of an element.
        Since the array is always ordered, we can use binary search.
        
        Returns:
            The index of the element (if found), otherwise -1.
            
        Java equivalent: indexOf(K k)
        """
        i = bisect.bisect_left(self._array, k)
        if i != len(self._array) and self._array[i] == k:
            return i
        return -1

    def index(self, k: T) -> int:
        """
        Pythonic index method.
        Returns the index of the element if found, otherwise raises ValueError.
        """
        idx = self.index_of(k)
        if idx == -1:
            raise ValueError(f"{k} is not in OrderedArray")
        return idx

    def __contains__(self, k: T) -> bool:
        """
        Pythonic containment check. Allows usage like 'k in arr'.
        Uses binary search (via index_of) for O(log n) performance.
        """
        return self.index_of(k) != -1

    @property
    def size(self) -> int:
        """
        Method to get the size of this OrderedArray.
        Java equivalent: getSize()
        """
        return len(self._array)

    def _update(self) -> None:
        """
        Sorts the internal array.
        Java equivalent: update(int length) (private)
        """
        self._array.sort()

    def __len__(self) -> int:
        """
        Pythonic length.
        """
        return len(self._array)

    def __str__(self) -> str:
        return str(self._array)
