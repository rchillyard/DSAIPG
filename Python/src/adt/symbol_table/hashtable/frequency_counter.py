from typing import TypeVar, Optional
from ..base_immutable_symbol_table import BaseImmutableSymbolTable
from ..st import ST
from .st_map import STMap

Key = TypeVar('Key')

class FrequencyCounter(BaseImmutableSymbolTable[Key, int]):
    """
    This class defines a specialized type of symbol table where the value corresponding to a key
    is the count of the number of times increment has been called for that key.
    """

    def __init__(self, map_st: Optional[ST[Key, int]] = None):
        super().__init__(map_st if map_st is not None else STMap(), lambda: 0)
        self._total = 0

    def increment(self, key: Key) -> None:
        """
        Increments the frequency count of the specified key.
        """
        self.validate_key(key)
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def relative_frequency(self, key: Key) -> float:
        if self._total == 0:
            return 0.0
        return self.get(key) / self._total

    def relative_frequency_as_percentage(self, key: Key) -> float:
        return 100.0 * self.relative_frequency(key)

    def total(self) -> int:
        return self._total

    def __str__(self):
        return str(self.map)
