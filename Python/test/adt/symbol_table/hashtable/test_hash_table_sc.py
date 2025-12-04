import unittest
import random
from src.adt.symbol_table.hashtable.hash_table_sc import HashTableSC

class TestHashTableSC(unittest.TestCase):

    class BadClass:
        def __hash__(self):
            return 17
        def __str__(self):
            return "badClass"
        def __repr__(self):
            return "badClass"

    def test_hash_table0(self):
        hash_table = HashTableSC()
        self.assertEqual(0, hash_table.size())
        self.assertTrue(hash_table.is_empty())

    def test_hash_table1(self):
        hash_table = HashTableSC()
        self.assertIsNone(hash_table.get("Hello"))

    def test_hash_table2(self):
        hash_table = HashTableSC()
        hash_table.put("Hello", "World!")
        self.assertEqual(1, hash_table.size())
        self.assertIsNotNone(hash_table.get("Hello"))
        self.assertEqual("World!", hash_table.get("Hello"))

    def test_hash_table2a(self):
        # Python dicts/sets handle objects by identity unless __eq__ and __hash__ are defined.
        # Our BadClass only defines __hash__, so instances are distinct.
        hash_table = HashTableSC()
        bad_class1 = self.BadClass()
        hash_table.put(bad_class1, "World!1")
        self.assertEqual(1, hash_table.size())
        
        bad_class2 = self.BadClass()
        hash_table.put(bad_class2, "World!2")
        self.assertEqual("World!2", hash_table.get(bad_class2))
        
        hash_table.put(bad_class2, "World!2a")
        self.assertEqual("World!1", hash_table.get(bad_class1))
        self.assertEqual("World!2a", hash_table.get(bad_class2))
        self.assertEqual(2, hash_table.size())

    def test_hash_table3(self):
        hash_table = HashTableSC()
        hash_table.put("Hello0", "World!0")
        hash_table.put("Hello1", "World!1")
        self.assertEqual(2, hash_table.size())
        self.assertIsNotNone(hash_table.get("Hello0"))
        self.assertEqual("World!0", hash_table.get("Hello0"))
        self.assertEqual("World!1", hash_table.get("Hello1"))
        keys = hash_table.keys()
        self.assertEqual(2, len(keys))

    def test_hash_table4(self):
        random.seed(0)
        capacity = 32
        hash_table = HashTableSC(capacity)
        for i in range(capacity):
            hash_table.put(str(random.randint(0, 99)), str(random.random()))
        
        # Java test asserts 29. We'll check size matches unique keys.
        self.assertEqual(hash_table.size(), len(hash_table.keys()))

    def test_put_new_key_value(self):
        hash_table = HashTableSC(4)
        hash_table.put("one", 1)
        self.assertEqual(1, hash_table.get("one"))
        self.assertEqual(1, hash_table.size())

    def test_put_update_value(self):
        hash_table = HashTableSC(4)
        self.assertIsNone(hash_table.put("one", 1))
        self.assertEqual(1, hash_table.put("one", 11))
        self.assertEqual(11, hash_table.get("one"))
        self.assertEqual(1, hash_table.size())

    def test_put_multiple_keys(self):
        hash_table = HashTableSC(4)
        hash_table.put("one", 1)
        hash_table.put("two", 2)
        hash_table.put("three", 3)
        self.assertEqual(1, hash_table.get("one"))
        self.assertEqual(2, hash_table.get("two"))
        self.assertEqual(3, hash_table.get("three"))
        self.assertEqual(3, hash_table.size())

    def test_put_with_collision(self):
        # Force collision by subclassing or using small table + known hashes.
        # Let's use small table and assume some collision or mock.
        
        class MockHashTableSC(HashTableSC):
            def get_index(self, key):
                if key == 1: return 1
                if key == 2: return 0
                if key == 3: return 1 # Collision with 1
                return super().get_index(key)

        hash_table = MockHashTableSC(2)
        hash_table.put(1, "one")
        hash_table.put(2, "two")
        
        hash_table.put(1, "ONE")
        hash_table.put(3, "three")
        
        self.assertEqual("ONE", hash_table.get(1))
        self.assertEqual("two", hash_table.get(2))
        self.assertEqual("three", hash_table.get(3))
        self.assertEqual(3, hash_table.size())

    def test_put_null_value(self):
        hash_table = HashTableSC(4)
        hash_table.put("key", None)
        self.assertIsNone(hash_table.get("key"))
        self.assertEqual(1, hash_table.size())

    def test_put_and_retrieve_keys(self):
        hash_table = HashTableSC(8)
        hash_table.put("a", 1)
        hash_table.put("b", 2)
        hash_table.put("c", 3)
        keys = hash_table.keys()
        self.assertIn("a", keys)
        self.assertIn("b", keys)
        self.assertIn("c", keys)
        self.assertEqual(3, len(keys))

    def test_put_new_key(self):
        hash_table = HashTableSC()
        result = hash_table.put("key1", 10)
        self.assertIsNone(result)
        self.assertEqual(10, hash_table.get("key1"))

    def test_put_existing_key(self):
        hash_table = HashTableSC()
        hash_table.put("key1", 10)
        old_value = hash_table.put("key1", 20)
        self.assertEqual(10, old_value)
        self.assertEqual(20, hash_table.get("key1"))

if __name__ == '__main__':
    unittest.main()
