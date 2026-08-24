import unittest

from src.selection.entropy import Entropy


class TestEntropy(unittest.TestCase):

    def test_get_random_with_seed(self):
        # Python's random seeding/behavior matches Java's only if we use the exact same algorithm.
        # Entropy.java uses SecureRandom or System.currentTimeMillis or just passed bytes.
        # The Java test sets Entropy.seed.
        # Porting the logic check:
        Entropy.seed = 123456789 # Arbitrary fixed seed
        entropy = Entropy(40)
        # We can't expect the exact same random number as Java given different RNG implementations.
        # But we can verify it returns a value within range.
        actual = entropy.get_random(1024)
        self.assertTrue(0 <= actual < 1024)
        Entropy.seed = 0

    def test_get_entropy_0(self):
        entropy = Entropy(25)
        # Expected bits in Python implementation logic:
        # bytearray length will be ceil(25/8) = 4 bytes = 32 bits.
        self.assertEqual(32, entropy.get_bits())
        entropy.get_entropy(8)
        entropy.get_entropy(8)
        entropy.get_entropy(8)
        actual = entropy.get_entropy(8)
        # Last 8 bits might be anything depending on RNG, but check it returns something valid (int)
        self.assertIsInstance(actual, int)

    def test_get_entropy_1(self):
        # EntropyTest.testGetEntropy1 in Java also asserts the *values* drawn,
        # but those assertions are seeded from the clock and, as its comment
        # says, are "only good for about 66 2/3 hours" -- which is why the Java
        # test is commented out.  Only the bit accounting is deterministic, so
        # that is all this test checks.
        entropy = Entropy(25) # 32 bits
        self.assertEqual(32, entropy.get_bits())

        # Taking 6 bits leaves 32 - 6 = 26
        entropy.get_entropy(6)
        self.assertEqual(26, entropy.get_bits())

        entropy.get_entropy(8) # 18
        entropy.get_entropy(8) # 10
        entropy.get_entropy(8) # 2

        entropy.get_entropy(2)
        self.assertEqual(0, entropy.get_bits())

    def test_get_entropy_2(self):
        raw_bits = bytearray([
            0xF0, 0xB5, 0xA1, 0x51, 0x78, 0x3B, 0xEB, 0xCB, 0x07, 0x39, 0x3D, 0xF4, 0xF4, 0x9B, 0x5E, 0x6B,
            0xB1, 0xBE, 0x94, 0xAA, 0x5B, 0x18, 0x12, 0xFD, 0xCF, 0x50, 0x7F, 0x19, 0xB4, 0xBF, 0x09, 0x9F
        ])
        entropy = Entropy(raw_bits)
        # 32 bytes * 8 = 256 bits
        self.assertEqual(256, entropy.get_bits())
        
        val1 = entropy.get_entropy(1)
        # 0x9F is 1001 1111. Last bit is 1.
        self.assertEqual(1, val1)
        
        val2 = entropy.get_entropy(2)
        # Remaining: 1001 111 (7 bits in last byte).
        # extract 2 bits: 11 (3).
        self.assertEqual(3, val2)

    def test_get_entropy_valid_inputs(self):
        Entropy.seed = 0xFFFFFFFFFFFFFFFF
        entropy = Entropy(32)
        self.assertEqual(32, entropy.get_bits())
        
        res1 = entropy.get_entropy(8)
        res2 = entropy.get_entropy(16)
        res3 = entropy.get_entropy(8)
        
        # In java test it asserts bit count of result. Since seed is all 1s (approx), results might be high?
        # Java seed logic: (seed >> (i * 8)) & 0xFF.
        # If seed is -1 (all 1s), then every byte is 0xFF.
        # So entropy is all 1s.
        # getEntropy(8) should return 0xFF (255). Bit count 8.
        self.assertEqual(8, res1.bit_count())
        self.assertEqual(16, res2.bit_count())
        self.assertEqual(8, res3.bit_count())
        
        Entropy.seed = 0

    def test_get_entropy_invalid_inputs(self):
        entropy = Entropy(32)
        with self.assertRaises(ValueError):
            entropy.get_entropy(-1)
        with self.assertRaises(ValueError):
            entropy.get_entropy(0)
        with self.assertRaises(ValueError):
            entropy.get_entropy(65)
        with self.assertRaises(ValueError):
            entropy.get_entropy(100) # Exceeds available

    def test_log2(self):
        self.assertEqual(0, Entropy.log2(1))
        self.assertEqual(1, Entropy.log2(2))
        self.assertEqual(2, Entropy.log2(3))
        self.assertEqual(2, Entropy.log2(4))
        self.assertEqual(3, Entropy.log2(5))
        self.assertEqual(3, Entropy.log2(8))

if __name__ == '__main__':
    unittest.main()
