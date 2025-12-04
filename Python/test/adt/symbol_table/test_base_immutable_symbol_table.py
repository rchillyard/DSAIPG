import pytest
from typing import TypeVar, Optional, Set, Callable, Dict, Any
from src.adt.symbol_table.st import ST
from src.adt.symbol_table.base_immutable_symbol_table import BaseImmutableSymbolTable

Key = TypeVar('Key')
Value = TypeVar('Value')

class MockST(ST[Key, Value]):
    def __init__(self):
        self.internal_map: Dict[Key, Value] = {}

    def get(self, key: Key) -> Optional[Value]:
        return self.internal_map.get(key)

    def put(self, key: Key, value: Value) -> Optional[Value]:
        old_value = self.internal_map.get(key)
        self.internal_map[key] = value
        return old_value

    def delete(self, key: Key) -> Optional[Value]:
        return self.internal_map.pop(key, None)

    def keys(self) -> Set[Key]:
        return set(self.internal_map.keys())

    def size(self) -> int:
        return len(self.internal_map)
    
    def __len__(self) -> int:
        return self.size()

class MockBaseImmutableSymbolTable(BaseImmutableSymbolTable[Key, Value]):
    def __init__(self, map_st: ST[Key, Value], default_supplier: Callable[[], Value]):
        super().__init__(map_st, default_supplier)

def test_get_existing_key():
    # Setup
    mock_st = MockST[str, str]()
    mock_st.put("key1", "value1")
    table = MockBaseImmutableSymbolTable(mock_st, lambda: "default")

    # Test
    result = table.get("key1")

    # Assert
    assert result == "value1"

def test_get_non_existent_key_with_default_value():
    # Setup
    mock_st = MockST[str, str]()
    table = MockBaseImmutableSymbolTable(mock_st, lambda: "default")

    # Test
    result = table.get("nonExistentKey")

    # Assert
    assert result == "default"

def test_get_null_key():
    # Setup
    mock_st = MockST[str, str]()
    table = MockBaseImmutableSymbolTable(mock_st, lambda: "default")

    # Test & Assert
    with pytest.raises(ValueError):
        table.get(None)

def test_get_with_empty_table():
    # Setup
    mock_st = MockST[str, str]()
    table = MockBaseImmutableSymbolTable(mock_st, lambda: "default")

    # Test
    result = table.get("key1")

    # Assert
    assert result == "default"

def test_get_with_null_default_value():
    # Setup
    mock_st = MockST[str, str]()
    table = MockBaseImmutableSymbolTable(mock_st, lambda: None)

    # Test
    result = table.get("nonExistentKey")

    # Assert
    assert result is None
