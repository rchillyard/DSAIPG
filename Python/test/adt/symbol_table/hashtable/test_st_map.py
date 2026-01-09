import unittest
from src.adt.symbol_table.hashtable.st_map import STMap

class TestSTMap(unittest.TestCase):

    def test_put_adds_new_key_value_pair(self):
        st_map = STMap()
        st_map.put("key1", 10)
        self.assertEqual(10, st_map.get("key1"))
        self.assertEqual(1, st_map.size())

    def test_put_replaces_value_for_existing_key(self):
        st_map = STMap()
        st_map.put("key1", 10)
        st_map.put("key1", 20)
        self.assertEqual(20, st_map.get("key1"))
        self.assertEqual(1, st_map.size())

    def test_put_allows_null_values(self):
        st_map = STMap()
        st_map.put("key1", None)
        self.assertIsNone(st_map.get("key1"))
        self.assertEqual(1, st_map.size())

    def test_put_adds_multiple_key_value_pairs(self):
        st_map = STMap()
        st_map.put("key1", 10)
        st_map.put("key2", 20)
        st_map.put("key3", 30)
        self.assertEqual(10, st_map.get("key1"))
        self.assertEqual(20, st_map.get("key2"))
        self.assertEqual(30, st_map.get("key3"))
        self.assertEqual(3, st_map.size())

    def test_put_with_pre_initialized_map(self):
        initial_map = {"key1": 10}
        st_map = STMap(initial_map)
        st_map.put("key2", 20)
        self.assertEqual(10, st_map.get("key1"))
        self.assertEqual(20, st_map.get("key2"))
        self.assertEqual(2, st_map.size())

if __name__ == '__main__':
    unittest.main()
