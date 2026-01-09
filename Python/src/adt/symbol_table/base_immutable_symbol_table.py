from typing import TypeVar, Set, Callable, Optional
from .immutable_symbol_table import ImmutableSymbolTable
from .st import ST

Key = TypeVar('Key')
Value = TypeVar('Value')

class BaseImmutableSymbolTable(ImmutableSymbolTable[Key, Value]):
    """
    Abstract base class for implementing an immutable symbol table with default value.
    """

    def __init__(self, map_st: ST[Key, Value], default_supplier: Callable[[], Value]):
        """
        Constructs an instance of BaseImmutableSymbolTable with a specified map and a default value supplier.

        Args:
            map_st: the symbol table (ST[Key, Value]) used to store key-value pairs.
            default_supplier: the supplier used to provide default values when a requested key is not present.
        """
        self.map = map_st
        self.default_supplier = default_supplier

    def get(self, key: Key) -> Optional[Value]:
        """
        Retrieves the value associated with the given key from the symbol table.
        If the key is not present in the symbol table, the method returns a default value.
        """
        self.validate_key(key)
        val = self.map.get(key)
        if val is not None:
            return val
        return self.default_supplier()

    def keys(self) -> Set[Key]:
        return self.map.keys()

    def size(self) -> int:
        return self.map.size()
