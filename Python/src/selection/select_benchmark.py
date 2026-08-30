import os
import random
import statistics
import time
from collections.abc import Callable
from typing import TypeVar

from .quick_select import QuickSelect
from .select_base import Select
from .slow_select import SlowSelect

X = TypeVar('X')

class SelectBenchmark:
    """
    Class for benchmarking the performance of different selection algorithms.
    """

    def __init__(self, runs: int, n: int):
        self.runs = runs
        self.n = n
        self.safety_factor = 10
    
    def run_benchmarks(self) -> str:
        N = self.n * self.safety_factor
        print(f"SelectBenchmark: N={N}")
        return self.quick_and_slow_benchmarks(N)

    def quick_and_slow_benchmarks(self, N: int) -> str:
        k = N // 2
        quick_select = QuickSelect()
        slow_select = SlowSelect(k)
        quick_selector = "QuickSelect"
        slow_selector = "SlowSelect"
        results = []

        # Helper method to generate data
        def random_generator(): return [random.randint(0, N*100) for _ in range(N)]
        def ordered_generator(): return list(range(1, N + 1))
        def partial_ordered_generator(): 
            arr = list(range(1, N + 1))
            # Shuffle half
            for i in range(N // 2):
                j = random.randint(0, N - 1)
                arr[i], arr[j] = arr[j], arr[i]
            return arr
        def reverse_ordered_generator(): return list(range(N, 0, -1))

        # Benchmarks
        # Note: QuickSelect is currently unimplemented and will raise NotImplementedError. 
        # We catch it to allow the benchmark to proceed for SlowSelect or show error.
        
        try:
             results.append(self.result_message(f"{quick_selector},random", self.do_benchmark(f"{quick_selector}", quick_select, k, random_generator, self.runs), N))
        except NotImplementedError:
             results.append(f"{quick_selector},random,{self.runs},{N},NotImplemented\n")
        
        results.append(self.result_message(f"{slow_selector},random", self.do_benchmark(f"{slow_selector}", slow_select, k, random_generator, self.runs), N))

        try:
            results.append(self.result_message(f"{quick_selector},ordered", self.do_benchmark(f"{quick_selector}", quick_select, k, ordered_generator, self.runs), N))
        except NotImplementedError:
            results.append(f"{quick_selector},ordered,{self.runs},{N},NotImplemented\n")

        results.append(self.result_message(f"{slow_selector},ordered", self.do_benchmark(f"{slow_selector}", slow_select, k, ordered_generator, self.runs), N))

        try:
            results.append(self.result_message(f"{quick_selector},partially-ordered", self.do_benchmark(f"{quick_selector}", quick_select, k, partial_ordered_generator, self.runs), N))
        except NotImplementedError:
             results.append(f"{quick_selector},partially-ordered,{self.runs},{N},NotImplemented\n")

        results.append(self.result_message(f"{slow_selector},partially-ordered", self.do_benchmark(f"{slow_selector}", slow_select, k, partial_ordered_generator, self.runs), N))

        try:
            results.append(self.result_message(f"{quick_selector},reverse-ordered", self.do_benchmark(f"{quick_selector}", quick_select, k, reverse_ordered_generator, self.runs), N))
        except NotImplementedError:
             results.append(f"{quick_selector},reverse-ordered,{self.runs},{N},NotImplemented\n")

        results.append(self.result_message(f"{slow_selector},reverse-ordered", self.do_benchmark(f"{slow_selector}", slow_select, k, reverse_ordered_generator, self.runs), N))

        return "".join(results)

    def result_message(self, s: str, d: float, n: int) -> str:
        return f"{s},{self.runs},{n},{d:.2f}\n"

    def do_benchmark(self, description: str, select: Select, k: int, supplier: Callable[[], list[int]], runs: int) -> float:
        times = []
        for _ in range(runs):
            data = supplier()
            # Deep copy data if needed, or re-generate. Supplier generates new list each time.
            # Java version does Arrays.copyOf inside the benchmark. 
            # Here supplier returns a new list, so we are safe modifying it.
            
            start_time = time.time()
            select.select(data, k)
            end_time = time.time()
            times.append((end_time - start_time) * 1000) # milliseconds
            
        return statistics.mean(times)

if __name__ == "__main__":
    sb = []
    
    # Quick run for verification
    bench1 = SelectBenchmark(10, 100)
    sb.append(bench1.run_benchmarks())
    
    output_content = "".join(sb)
    
    # Write to CSV
    current_dir = os.getcwd()
    output_csv_filename = "SelectBenchmark.csv"
    path = os.path.join(current_dir, output_csv_filename)
    print(f"Output CSV File Path :-> {path}")
    
    header = "Method,Array-Ordering,Runs,N,Time\n"
    
    file_exists = os.path.isfile(path)
    if file_exists:
        try:
            os.remove(path)
        except OSError:
            pass
            
    with open(path, 'a') as f:
        f.write(header)
        f.write(output_content)
