from abc import abstractmethod
from typing import TypeVar

from .immutable_symbol_table import ImmutableSymbolTable

Key = TypeVar('Key')
Value = TypeVar('Value')

class ST(ImmutableSymbolTable[Key, Value]):
    """
    Interface to model a symbol table.
    This is similar but different to the java.util.Dictionary interface.
    """

    @abstractmethod
    def put(self, key: Key, val: Value) -> Value | None:
        """
        Insert a key/value pair.
        If the key already exists, then its value will simply be overwritten.

        Args:
            key: the key.
            val: the value.

        Returns:
            the original value, if any, otherwise None.
        """
        pass

    @abstractmethod
    def delete(self, key: Key) -> Value | None:
        """
        Delete a key.

        Args:
            key: the key.

        Returns:
            the original value, if any, otherwise None.
        """
        pass

    def __setitem__(self, key: Key, value: Value) -> None:
        """
        Pythonic access to set value by key.
        """
        self.put(key, value)

    def __delitem__(self, key: Key) -> None:
        """
        Pythonic access to delete key.
        """
        self.delete(key)
