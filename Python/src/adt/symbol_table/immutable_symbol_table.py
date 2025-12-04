from abc import ABC, abstractmethod
from typing import TypeVar, Generic, Set, Optional, Callable, Any

Key = TypeVar('Key')
Value = TypeVar('Value')

class ImmutableSymbolTable(ABC, Generic[Key, Value]):
    """
    Interface to model an immutable symbol table.
    """

    @abstractmethod
    def get(self, key: Key) -> Optional[Value]:
        """
        Retrieve the value for a given key.

        Args:
            key: the key.

        Returns:
            the value, if key is present, else None.
        """
        pass

    @abstractmethod
    def keys(self) -> Set[Key]:
        """
        Get the set of keys in this symbol table.

        Returns:
            the Set of keys.
        """
        pass

    @abstractmethod
    def size(self) -> int:
        """
        Get the size of this ImmutableSymbolTable.

        Returns:
            the current size.
        """
        pass

    def is_empty(self) -> bool:
        """
        Tests if this ImmutableSymbolTable maps no keys to value.

        Returns:
            True if this ImmutableSymbolTable maps no keys to values; False otherwise.
        """
        return self.size() == 0

    def validate_key(self, key: Key) -> None:
        """
        Validates the provided key.
        Throws an ValueError if the key is None.

        Args:
            key: the key to validate.

        Raises:
            ValueError: if the key is None.
        """
        if key is None:
            raise ValueError("ST:get: key is None")

    def contains(self, key: Key) -> bool:
        """
        Determine if this symbol table contains key.

        Args:
            key: the key to find.

        Returns:
            True if this contains key.
        """
        if key is None:
            raise ValueError("Key cannot be None")
        return self.get(key) is not None

    def get_or_default(self, key: Key, default_value_function: Callable[[], Value]) -> Value:
        """
        Retrieves the value to which the specified key is mapped, or returns the default value if the key is not found.

        Args:
            key: the key whose associated value is to be returned.
            default_value_function: the (call-by-name) default value to return if the specified key is not present.

        Returns:
            the value associated with the specified key, or the provided default value if the key is not present.
        """
        val = self.get(key)
        if val is not None:
            return val
        else:
            return default_value_function()

    def __getitem__(self, key: Key) -> Value:
        """
        Pythonic access to get value by key.
        Raises KeyError if key is not found (unlike get which returns None).
        """
        val = self.get(key)
        if val is None:
            raise KeyError(f"Key not found: {key}")
        return val

    def __len__(self) -> int:
        return self.size()

    def __contains__(self, key: Key) -> bool:
        return self.contains(key)
