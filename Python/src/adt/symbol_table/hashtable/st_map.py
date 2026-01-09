from typing import TypeVar, Dict, Set, Optional
from ..st import ST

Key = TypeVar('Key')
Value = TypeVar('Value')

class STMap(ST[Key, Value]):
    """
    Provides an implementation of the ST interface using a generic map backend (Python dict).
    """

    def __init__(self, initial_map: Optional[Dict[Key, Value]] = None):
        self.map = initial_map if initial_map is not None else {}

    def size(self) -> int:
        return len(self.map)

    def get(self, key: Key) -> Optional[Value]:
        return self.map.get(key)

    def keys(self) -> Set[Key]:
        return set(self.map.keys())

    def put(self, key: Key, val: Value) -> Optional[Value]:
        old_val = self.map.get(key)
        self.map[key] = val
        return old_val

    def delete(self, key: Key) -> Optional[Value]:
        return self.map.pop(key, None)

    def __str__(self):
        return str(self.map)
