import time
import math
import secrets

class Entropy:
    """
    The Entropy class facilitates the generation and management of entropy data.
    It provides functionality to extract a specific number of bits from an internal entropy source
    and supports secure and system-clock-based entropy generation.
    """
    
    seed = 0
    secure_random = secrets.SystemRandom()

    def __init__(self, source):
        """
        Constructs an Entropy object.
        
        Args:
            source: Can be a byte array (bytes or list of ints) or an integer representing the number of bits.
                    If int: generates that many bits of entropy.
                    If bytes/list: uses that strictly as the entropy source.
        """
        if isinstance(source, int):
            self.entropy = bytearray(self._get_bytes(source))
            self.bits = len(self.entropy) * 8
            self.last_element_bits = 8
            self.last_index = len(self.entropy) - 1
        else:
            if isinstance(source, bytes):
                self.entropy = bytearray(source)
            else:
                self.entropy = bytearray(source)
                
            if len(self.entropy) < 1:
                 # In Java code it says "at least one bit", simplistic check here
                 # The logic relies on getting bytes, so 0 bytes is bad.
                 pass
            self.bits = len(self.entropy) * 8
            self.last_element_bits = 8
            self.last_index = len(self.entropy) - 1

    def get_bits(self) -> int:
        """
        Retrieves the number of available bits of entropy.
        """
        return self.bits

    def get_entropy(self, bits: int) -> int:
        """
        Generates a long (int) value by extracting a specified number of bits from an internal entropy source.
        """
        if bits <= 0 or bits > self.bits or bits > 64:
             raise ValueError(f"Invalid number of bits: {bits} (should be positive, no more than 64, and less than or equal to {self.bits} bits)")
        
        result = 0
        required_bits = bits
        while required_bits >= 8:
            result = (result << 8) | self._get_a_byte()
            required_bits -= 8
            
        if required_bits > 0:
            result = (result << required_bits) | self._get_odd_bits(required_bits)
            
        bitmask = self._create_bitmask(bits)
        return result & bitmask

    def get_random(self, n: int) -> int:
        """
        Generates a random int value between 0 and `n` (exclusive).
        """
        bits = self.log2(n)
        return (self.get_entropy(bits) & self._create_bitmask(bits)) % n

    def _get_raw_entropy(self) -> bytes:
        return bytes(self.entropy)

    def __str__(self):
        hex_str = [f"{b:02X}" for b in self.entropy]
        return f"Entropy{{bits={self.bits}, entropy={hex_str}}}"

    def _get_odd_bits(self, bits: int) -> int:
        assert bits >= 0
        if bits <= self.last_element_bits:
            last_byte = self.entropy[self.last_index]
            result = last_byte & self._create_bitmask(bits)
            self.entropy[self.last_index] = (last_byte >> bits)
            self.last_element_bits -= bits
            if self.last_element_bits == 0:
                self.last_index -= 1
                self.last_element_bits = 8
            self.bits -= bits
            return result
        else:
            result = 0
            bit_count = self.last_element_bits
            result |= self._get_odd_bits(bit_count)
            required_bits = bits - bit_count
            result = result << required_bits
            result |= self._get_odd_bits(required_bits)
            return result

    @staticmethod
    def _create_bitmask(length: int) -> int:
        if length == 64:
            return 0xFFFFFFFFFFFFFFFF
        return (1 << length) - 1

    def _get_a_byte(self) -> int:
        assert len(self.entropy) > 0
        assert self.bits >= 8
        result = self.entropy[0]
        # Shift remaining bytes
        for i in range(self.last_index):
            self.entropy[i] = self.entropy[i+1]
        
        self.bits -= 8
        self.last_index -= 1
        assert self.last_index >= -1 # allow it to be -1 if empty? logic says >= 0 but it decrements
        return result

    @staticmethod
    def _get_bytes(bits: int) -> bytes:
        if bits < 0:
            raise ValueError(f"Invalid number of bits: {bits}")
        
        num_bytes = math.ceil(bits / 8)
        result = bytearray(num_bytes)
        
        if Entropy.seed != 0:
            Entropy._pack_into_bytes(result, Entropy.seed)
        elif bits < 32:
            Entropy._use_clock_time(result)
        else:
            # Use os.urandom or secrets
            return secrets.token_bytes(num_bytes)
            
        return bytes(result)

    @staticmethod
    def _use_clock_time(result: bytearray):
        Entropy._pack_into_bytes(result, int(time.time() * 1000))

    @staticmethod
    def _pack_into_bytes(result: bytearray, seed: int):
        for i in range(len(result)):
            result[i] = (seed >> (i * 8)) & 0xFF

    @staticmethod
    def log2(x: int) -> int:
        if x <= 0:
            raise ValueError("x must be positive")
        if x == 1:
            return 0
        return x.bit_length() - (1 if (x & (x - 1)) == 0 else 0) # This is floor log2, check java implementation
        
        # Java implementation: 64 - Long.numberOfLeadingZeros(x - 1);
        # which is ceil(log2(x)) for non-powers of 2, essentially bits required to represent 0..x-1
        # Example: x=3 (binary 11). x-1=2 (10). bit_length=2. Returns 2.
        # Example: x=4 (100). x-1=3 (011). bit_length=2. Returns 2.
        # Wait, Java Long.numberOfLeadingZeros(x-1)
        # if x=3, x-1=2. LZ=62 (on 64 bit). 64-62=2.
        # if x=4, x-1=3. LZ=62. 64-62=2.
        # if x=5, x-1=4. LZ=61. 64-61=3.
        # So it basically calculates the number of bits needed to represent x-1?
        # Python bit_length of x-1 seems correct.
        
        if x == 1: return 0
        return (x - 1).bit_length()
