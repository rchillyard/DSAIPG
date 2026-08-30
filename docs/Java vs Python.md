# The same algorithms in two languages

This repository holds the same algorithms twice, in Java and in Python.
That makes it possible to ask a question the book can only argue in the abstract:
what actually survives a change of language, and what does not?

Everything below was measured on one machine — an Apple M1, OpenJDK 21 and
CPython 3.14 — reading the same inputs into both trees, with the exercises
completed. Repeat it on your own machine and your numbers will differ.
The *shapes* should not.

Take the absolute times as approximate. "About 40 times" is a fair reading of
these measurements; "43.5 times" is not.

## 1. Growth rate is a property of the algorithm, not of the language

`adt/threesum` holds three solutions to the same problem, with running times
that grow as n³, n² log n and n². Timing each at a series of sizes, and taking
the base-2 logarithm of the ratio between successive doublings, recovers the
exponent:

| doubling | cubic (Java) | cubic (Python) | quadratic (Java) | quadratic (Python) |
|---|---:|---:|---:|---:|
| 250/125 | 2.75 | 2.97 | | |
| 500/250 | 2.99 | 3.17 | | |
| 1000/500 | 2.97 | 3.02 | 1.78 | 2.00 |
| 2000/1000 | 2.99 | | 2.02 | 1.98 |
| 4000/2000 | | | 2.10 | 2.01 |
| 8000/4000 | | | 2.10 | 2.04 |
| 16000/8000 | | | 1.97 | 1.99 |

Both languages recover 3 and 2. The exponent does not care what the program is
written in, which is the whole reason for caring about it in the first place.

**Python recovers it more cleanly than Java does.** Its quadratic column reads
2.00, 1.98, 2.01, 2.04, 1.99 — five doublings within 2% of the truth — while
Java's wanders between 1.78 and 2.10. This is not Python being better. It is
Java being *fast*: at these sizes the JIT is still compiling, caches are still
filling, and the clock has barely started, so the second-order effects are a
visible fraction of the total. The interpreter's large and very uniform
per-operation cost swamps all of that.

If you want to *see* n² in your own measurements, the Python tree will show it
to you more readily. If you want to know what a real system costs, measure the
Java.

## 2. A constant factor buys you 500 elements

Java is roughly 40 times faster than Python on this kind of code. Set Java's
*cubic* ThreeSum against Python's *quadratic* one, and see what that buys:

| n | Java, cubic | Python, quadratic |
|---:|---:|---:|
| 500 | 0.0214 s | 0.0208 s |
| 1000 | 0.1681 s | 0.0830 s |
| 2000 | 1.3336 s | 0.3268 s |
| 16000 | ~11 minutes (extrapolated) | 21 s |

They are level at n = 500. Past that the slower language with the better
algorithm wins, and its lead grows without limit — 2× at n = 1000, 4× at
n = 2000, around 30× by n = 16000.

A faster language moves the curve down. Only a better algorithm changes its
shape. That is worth remembering the next time someone proposes to fix a
performance problem by rewriting in something quicker.

## 3. There is no single "Python is N times slower"

n = 1,000,000 random integer keys, inserted and then looked up, in
`adt/symbolTable`:

| implementation | Java | Python | ratio |
|---|---:|---:|---:|
| `BSTSimple` | 1.54 s | 11.39 s | 7× |
| `HashTable_LP` | 0.056 s | 1.73 s | 31× |
| the platform's own (`HashMap` / `dict`) | 0.059 s | 0.29 s | 5× |

Three ratios from 7 to 31, on the same machine, on the same afternoon.

- **The BST ratio is low because Java is losing its advantage**, not because
  Python is doing well. A million tree nodes do not fit in cache, so the
  traversal spends its time waiting for memory — and a cache miss costs the same
  in either language.
- **The hash-table ratio is high** because that is the honest cost of an
  interpreted inner loop with nothing else in the way.
- **The platform ratio is low because neither inner loop is really in the
  source language.** Python's `dict` is written in C, and lands within 5× of
  `HashMap` — while the *same idea*, written out in Python as `HashTable_LP`,
  is 31× behind.

Those last two rows are the pair to think about. In Python, the difference
between writing an algorithm and calling one is a factor of 6. It is why
idiomatic Python leans so heavily on its libraries, and why "rewrite the inner
loop in C" is the standard Python optimisation.

The same effect appears inside a single algorithm. `ThreeSumQuadrithmic` needs a
binary search; writing the loop out takes 7.9 s at n = 4000, and calling
`bisect.bisect_left` instead takes 1.4 s — closing the gap to Java from 35× to
6× without changing the algorithm at all.

## 4. An optimisation can pay in one language and not the other

`graphs/union_find` has a `UF_HWQUPC` that can be built with path compression
on or off. Two million random pairs over a million sites:

| | without compression | with compression |
|---|---:|---:|
| Java | 0.067 s | 0.032 s |
| Python | 3.26 s | 3.18 s |

**Path compression halves the time in Java and does nothing at all in Python.**

Path compression trades work now — rewriting a parent pointer as you pass — for
shallower trees later. In Java the payment is two array reads and a write, which
next to the cache miss it saves is almost free, so the trade is strongly
favourable. In Python each of those reads and writes is an interpreted operation
costing about what the traversal step it saves costs. The trade comes out even.

The general statement is that an optimisation's payoff is the ratio of what it
saves to what it costs, and **that ratio is a property of the implementation, not
of the algorithm**. An optimisation that removes interpreted operations pays
enormously in Python. One that only improves memory locality may pay nothing.

This cuts against the previous section, and both are true. Do not carry a rule
of thumb between languages; measure it again on the other side.

## Summary

| workload | Python / Java |
|---|---:|
| `dict` against `HashMap` | 5× |
| `BSTSimple`, n = 10⁶ | 7× |
| Huffman encode | 20× |
| `HashTable_LP` | 31× |
| ThreeSum, all three algorithms | 35–50× |
| Huffman decode | 40× |
| union-find with path compression | ~100× |

Forty is a reasonable first guess for straight-line algorithmic code. This table
is wrong by more than an order of magnitude at both ends of it, and the reasons
it is wrong are more interesting than the guess.

## Trying it yourself

Nothing above needs special equipment: a loop, `System.nanoTime` or
`time.perf_counter`, and the patience to run each size a few times and keep the
best. Two things are worth getting right.

**Warm up before you measure.** The JVM interprets your code before it compiles
it, so an unwarmed Java measurement can be ten times too slow. Run the workload
at a small size a few dozen times first, and throw those timings away.

**Feed both languages the same input.** Generate it once into a file, or use
`QuickRandom` with the same seed — it is a faithful port, so it produces the same
sequence in both. Then check that both give the same answer before you believe
either time.
