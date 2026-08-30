import random
import time
from collections.abc import Callable
from typing import TypeVar

from src.adt.symbol_table.tree.bst import BST
from src.adt.symbol_table.tree.bst_opt_del import BSTOptimisedDeletion

K = TypeVar('K')
V = TypeVar('V')

class BSTBenchmark:
    """
    Benchmarks BST operations.
    """

    class Stats:
        def __init__(self, nodes: int):
            self.initial_nodes = nodes
            self.nodes = 0
            self.initial_mean_depth = 0.0
            self.mean_depth = 0.0

        def set_mean_depth(self, nodes: int, mean_depth: float):
            if self.initial_mean_depth == 0:
                self.initial_mean_depth = mean_depth
            self.mean_depth = mean_depth
            self.nodes = nodes

        def __str__(self):
            return (f"initialNodes: {self.initial_nodes}, nodes: {self.nodes}, "
                    f"initialMeanDepth: {self.initial_mean_depth:.3f}, meanDepth: {self.mean_depth:.3f}")

    def __init__(self, bst: BST[K, V], n_runs: int, stats: 'BSTBenchmark.Stats'):
        self.bst = bst
        self.n_runs = n_runs
        self.stats = stats

    def run_benchmark(self, supplier: Callable[[], list[K]]) -> float:
        total_time = 0
        for _ in range(self.n_runs):
            input_data = supplier()
            start_time = time.time()
            self._experiment(input_data)
            end_time = time.time()
            total_time += (end_time - start_time) * 1000 # Convert to ms
            self._post_processor(input_data)
        
        return total_time / self.n_runs

    def _experiment(self, xs: list[K]):
        # Delete all elements
        for x in xs:
            self.bst.delete(x)
        # Re-insert all elements
        for x in xs:
            self.bst.put(x, None) # Value doesn't matter for this benchmark

    def _post_processor(self, xs: list[K]):
        mean_depth = self.bst.mean_depth()
        self.stats.set_mean_depth(self.bst.size, mean_depth)

def run_benchmark_demo():
    print("Running BST Benchmark Demo...")
    
    # Setup
    n_words = 1000
    words = [f"word{i}" for i in range(n_words)]
    random.shuffle(words)
    
    bst = BSTOptimisedDeletion(mode=2)
    for w in words:
        bst.put(w, len(w))
        
    stats = BSTBenchmark.Stats(bst.size)
    benchmark = BSTBenchmark(bst, n_runs=10, stats=stats)
    
    def supplier():
        # Return a random subset of words to delete/re-insert
        sample_size = int(n_words * 0.1)
        return random.sample(words, sample_size)
    
    avg_time = benchmark.run_benchmark(supplier)
    print(f"Stats: {stats}")
    print(f"Average time: {avg_time:.3f} ms")

if __name__ == "__main__":
    run_benchmark_demo()
