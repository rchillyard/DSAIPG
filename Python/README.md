# DSAIPG (Python)

Python companion code to:
"Data Structures, Algorithms, and Invariants--A Practical Guide"
by Robin Hillyard, College of Engineering, Northeastern University, Boston, MA, USA.
Published by Cognella.

This is the Python half of the [DSAIPG repository](https://github.com/rchillyard/DSAIPG).
The Java half, in the sibling `Java` directory, is the reference implementation:
where the two differ, Java is what the book describes.

## Getting started

You will need Python 3.10 or later.
Run everything from this directory.

With [uv](https://docs.astral.sh/uv/):

    uv sync
    uv run pytest

Without it:

    python3 -m venv .venv
    .venv/bin/pip install pytest ruff
    .venv/bin/python -m pytest

You should see 1617 tests: 1420 green and 197 skipped, and nothing red.

## The skipped tests are the point

A skipped test is one which reached a method you have not written yet.
Each is a method whose body reads:

    # TO BE IMPLEMENTED
    raise NotImplementedError("TO BE IMPLEMENTED")

Replacing those bodies with working code is the exercise, and the tests
already assert what the finished code should do.
So a skipped test should turn green once you have done the work.

**Anything red is a real problem**--either a mistake in your code,
or something wrong with your installation.
That is the whole point of the skips:
they keep the red/green signal meaning something while the work is unfinished.

To find the work, run the tests with `-rs`, which lists the reason for each skip:

    uv run pytest -rs

Each names the file and the line waiting for you, for example
`You need to implement the code at src/adt/bqs/d_list.py:87`.
That is more useful than hunting for the stubs,
because it also tells you how many tests each one is holding up--
`uf_hwqupc.py` accounts for 31 of them, `bag_array.py` for 26,
and `insertion_sort_comparator.py` for 25.

You can also search the tree for `TO BE IMPLEMENTED`.
In PyCharm or IntelliJ, adding `\bTO BE IMPLEMENTED\b.*` as a TODO pattern
makes them all show up in the TODO tool window.

They currently live in:

The count in brackets is how many tests each one is holding up,
which is a reasonable order to work in.

| Module | What is missing |
| --- | --- |
| `graphs/union_find/uf_hwqupc.py` (31) | `find`, `_merge_components`, `_do_path_compression` |
| `adt/bqs/bag_array.py` (26) | `_grow_from`: allocate a larger backing list and copy into it |
| `sort/elementary/insertion_sort_comparator.py` (25) | `sort_range`: the insertion sort over a sub-range |
| `util/benchmark/timer.py` (24) | `_get_clock`, `_to_millisecs` and `_do_repeat_for_iteration` |
| `sort/linearithmic/merge_sort.py` (10) | `_sort`: the recursive split, and the merge back |
| `adt/bqs/queue_elements.py` (9) | `offer` and `poll` |
| `adt/symbol_table/hashtable/frequency_counter.py` (9) | `increment` |
| `sort/counting/msd_string_sort.py` (7) | `place`: which bucket a string falls in at a given character |
| `sort/par/par_sort.py` (7) | `sort` and `sort_recursive`: the parallel split, and joining the halves |
| `selection/quick_select.py` (7) | the QuickSelect loop |
| `adt/bqs/d_list.py` (7) | insertion, removal and search on a doubly-linked list |
| `adt/threesum/two_sum_with_calipers.py` (7) | `calipers`: the two-pointer sweep |
| `adt/threesum/three_sum_quadratic.py` (6) | `get_triples_with_middle` |
| `adt/threesum/three_sum_quadrithmic.py` (6) | `get_triple` |
| `compression/huffman_coding.py` (5) | building the codebook, encoding and decoding |
| `sort/elementary/insertion_sort_basic.py` (4) | `insert`: put one element into its place |
| `adt/symbol_table/tree/bst_opt_del.py` (3) | optimised deletion |
| `adt/symbol_table/tree/bst_simple.py` (2) | Hibbard deletion, and `mean_depth` |
| `adt/threesum/three_sum_benchmark.py`, `two_sum_benchmark.py` (1 each) | `benchmark_three_sum`, `benchmark_two_sum` |

Some of these hold up tests a long way from themselves.
`uf_hwqupc.py` is wanted by Kruskal and Boruvka, and so by the campus tunnel
network in `graphs/tunnels`; `bag_array.py` is wanted by every graph that grows
past its initial capacity. That is not a mistake in the tests--
it is what it means for these to be the foundations.

## Layout

    src/    the code, mirroring the Java package structure
    test/   the tests, mirroring src/

Package names are the Python spelling of the Java ones:
`adt.symbol_table` for `adt.symbolTable`, `selection` for `select`.

Imports of project code are written from the top: `from src.adt.bqs.bag import Bag`.
`conftest.py` puts this directory on `sys.path`, so that resolves
no matter which directory you run `pytest` from,
including when your IDE runs a single test on its own.

## Coverage relative to Java

The Python tree is nearly a complete port. It currently covers:

| Package | Chapters |
| --- | --- |
| `adt` (array, bal_search_tree, bqs, pq, symbol_table, threesum, trie) | 3-6 |
| `compression` | - |
| `graphs` (dag, dijkstra, dynamic_programming, gis, traversal, tunnels, undirected, union_find) | 9-10 |
| `selection` | - |
| `sort` (classic, counting, elementary, generic, helper, linearithmic, par) | 7-8 |
| `util` | - |

Not yet ported: `misc` (Chapters 1 and 2) and the team project (`projects/mcts`).
Use the Java tree for those.

A few classes are deliberately absent rather than pending, and say so in their
own docstrings where a reader would look for them. The clearest case is
`graphs/tunnels`: the Java has four classes there, three of which are copies of
`Tunnels_Northeastern` with the MST algorithm hard-coded, and it already takes
that algorithm as a parameter--so only the general one is here.

## Linting

    uv run ruff check .

A clean checkout reports no problems, so anything it flags is yours.
`ruff check --fix` will correct most of what it finds.
