import pytest
from src.adt.bqs.dictionary_hash import DictionaryHash


class TestDictionaryHash:
    def test_put_and_get(self):
        d = DictionaryHash[str, int]()
        d.put("one", 1)
        assert d.get("one") == 1
        assert d.get("two") is None

    def test_size_and_is_empty(self):
        d = DictionaryHash[str, int]()
        assert d.is_empty()
        assert d.size() == 0
        d.put("one", 1)
        assert not d.is_empty()
        assert d.size() == 1

    def test_contains_key(self):
        d = DictionaryHash[str, int]()
        d.put("one", 1)
        assert d.contains_key("one")
        assert not d.contains_key("two")

    def test_clear(self):
        d = DictionaryHash[str, int]()
        d.put("one", 1)
        d.clear()
        assert d.is_empty()
        assert d.size() == 0

    def test_key_set(self):
        d = DictionaryHash[str, int]()
        d.put("one", 1)
        d.put("two", 2)
        keys = d.key_set()
        assert "one" in keys
        assert "two" in keys
        assert len(keys) == 2
