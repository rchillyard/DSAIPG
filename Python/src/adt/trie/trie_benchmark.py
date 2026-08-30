import os
import time

from .trie import Trie


def main():
    """
    Benchmark the Trie implementation against linear search and measure performance of operations.
    """
    # Path to the dictionary file in the Java resources directory
    # Determine path relative to this script: .../Python/src/adt/trie/trie_benchmark.py
    current_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.abspath(os.path.join(current_dir, "../../../../"))
    resource_path = os.path.join(project_root, "Java/src/main/resources/3000-common-words.txt")
    
    if not os.path.exists(resource_path):
        print(f"Error: Dictionary file not found at {resource_path}")
        return

    try:
        with open(resource_path) as f:
            words = [line.strip() for line in f if line.strip()]
    except Exception as e:
        print(f"Error reading dictionary file: {e}")
        return

    print(f"Loaded {len(words)} words from dictionary.")

    trie = Trie()

    # Benchmark Insert
    start = time.perf_counter_ns()
    for word in words:
        trie.insert(word)
    trie_insert_time = time.perf_counter_ns() - start

    # Benchmark Autocomplete
    prefix = "ap"
    start = time.perf_counter_ns()
    # Ignoring result for benchmark, just timing
    trie.autocomplete(prefix)
    trie_search_time = time.perf_counter_ns() - start

    # Benchmark Linear Search
    start = time.perf_counter_ns()
    # Linear search equivalent
    _ = [word for word in words if word.startswith(prefix)]
    linear_search_time = time.perf_counter_ns() - start

    # Benchmark Delete
    start = time.perf_counter_ns()
    for word in words:
        trie.delete(word)
    trie_delete_time = time.perf_counter_ns() - start

    print(f"Trie insert time: {trie_insert_time} ns")
    print(f"Trie search time: {trie_search_time} ns")
    print(f"Linear search time: {linear_search_time} ns")
    print(f"Trie delete time: {trie_delete_time} ns")

if __name__ == "__main__":
    main()
