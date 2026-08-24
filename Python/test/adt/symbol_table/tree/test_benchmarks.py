import unittest

from src.adt.symbol_table.tree.benchmarks import BSTBenchmark
from src.adt.symbol_table.tree.bst_opt_del import BSTOptimisedDeletion


class TestBSTBenchmark(unittest.TestCase):

    def test_run_benchmark_with_valid_supplier(self):
        bst = BSTOptimisedDeletion(mode=2)
        bst.put("a", 1)
        bst.put("b", 2)
        
        data = ["a", "b", "c"]
        def supplier():
            return data
        
        stats = BSTBenchmark.Stats(3)
        benchmark = BSTBenchmark(bst, 10, stats)
        
        result = benchmark.run_benchmark(supplier)
        self.assertGreater(result, 0)

    def test_run_benchmark_with_empty_array(self):
        bst = BSTOptimisedDeletion(mode=2)
        data = []
        def supplier():
            return data
        
        stats = BSTBenchmark.Stats(0)
        benchmark = BSTBenchmark(bst, 10, stats)
        
        result = benchmark.run_benchmark(supplier)
        # In Python, time.time() might not be precise enough to capture very fast execution as > 0,
        # but for empty array it should be very close to 0. 
        # However, the loop runs n_runs times, so it might take some time.
        # But since the array is empty, the experiment does nothing.
        # We expect it to run without error.
        self.assertGreaterEqual(result, 0)

    def test_run_benchmark_with_single_element_array(self):
        bst = BSTOptimisedDeletion(mode=2)
        bst.put("a", 1)
        
        data = ["a"]
        def supplier():
            return data
        
        stats = BSTBenchmark.Stats(1)
        benchmark = BSTBenchmark(bst, 10, stats)
        
        result = benchmark.run_benchmark(supplier)
        self.assertGreater(result, 0)

    def test_run_benchmark_with_large_input(self):
        bst = BSTOptimisedDeletion(mode=2)
        # Fill with some data
        for i in range(100):
            bst.put(f"word{i}", i)
            
        data = [f"word{i}" for i in range(100)]
        def supplier():
            return data
        
        stats = BSTBenchmark.Stats(100)
        benchmark = BSTBenchmark(bst, 10, stats)
        
        result = benchmark.run_benchmark(supplier)
        self.assertGreater(result, 0)

if __name__ == '__main__':
    unittest.main()
