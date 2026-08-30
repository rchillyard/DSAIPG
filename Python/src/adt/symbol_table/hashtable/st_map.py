from typing import TypeVar

from ..st import ST

Key = TypeVar('Key')
Value = TypeVar('Value')

class STMap(ST[Key, Value]):
    """
    Provides an implementation of the ST interface using a generic map backend (Python dict).
    """

    def __init__(self, initial_map: dict[Key, Value] | None = None):
        self.map = initial_map if initial_map is not None else {}

    def size(self) -> int:
        return len(self.map)

    def get(self, key: Key) -> Value | None:
        return self.map.get(key)

    def keys(self) -> set[Key]:
        return set(self.map.keys())

    def put(self, key: Key, val: Value) -> Value | None:
        old_val = self.map.get(key)
        self.map[key] = val
        return old_val

    def delete(self, key: Key) -> Value | None:
        return self.map.pop(key, None)

    def __str__(self):
        return str(self.map)
