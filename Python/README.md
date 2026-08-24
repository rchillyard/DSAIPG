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

You should see 362 tests, 298 of them green.

## The failing tests are the point

The 64 failures are not a broken installation.
Each one is a method whose body reads:

    # TO BE IMPLEMENTED
    raise NotImplementedError("TO BE IMPLEMENTED")

Replacing those bodies with working code is the exercise, and the tests
already assert what the finished code should do.
So a test that fails now should pass once you have done the work,
and if a test still fails after you believe you are finished, trust the test.

To find the stubs, search the tree for `TO BE IMPLEMENTED`.
In PyCharm or IntelliJ, adding `\bTO BE IMPLEMENTED\b.*` as a TODO pattern
makes them all show up in the TODO tool window.

They currently live in:

| Module | What is missing |
| --- | --- |
| `adt/bqs/bag_array.py` | `_grow_from`: allocate a larger backing list and copy into it |
| `adt/bqs/d_list.py` | insertion, removal and search on a doubly-linked list |
| `adt/bqs/queue_elements.py` | `offer` and `poll` |
| `adt/symbol_table/hashtable/frequency_counter.py` | `increment` |
| `adt/symbol_table/tree/bst_simple.py` | Hibbard deletion, and `mean_depth` |
| `adt/symbol_table/tree/bst_opt_del.py` | optimised deletion |
| `compression/huffman_coding.py` | building the codebook, encoding and decoding |
| `graphs/union_find/uf_hwqupc.py` | `find`, `_merge_components`, `_do_path_compression` |
| `selection/quick_select.py` | the QuickSelect loop |

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

The Python tree is not yet a complete port. It currently covers:

| Package | Chapters |
| --- | --- |
| `adt` (array, bal_search_tree, bqs, pq, symbol_table, trie) | 3-6 |
| `compression` | - |
| `graphs` (union_find, dijkstra, undirected) | 9-10, in part |
| `selection` | - |

Not yet ported: `sort` (Chapters 7 and 8), `util`, `misc` (Chapters 1 and 2),
`adt/threesum`, the remaining `graphs` subpackages, and the team project.
Use the Java tree for those.

## Linting

    uv run ruff check .

A clean checkout reports no problems, so anything it flags is yours.
`ruff check --fix` will correct most of what it finds.
