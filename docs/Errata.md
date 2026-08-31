# Errata

The book is printed and a second edition is unlikely, so corrections live here.
Each entry says what the book claims, what is actually the case, and how that was
established, so you can check it rather than take it on trust.

## Table 8.1, p. 146 — the MSD row was measured at a cutoff of 20, not 256

The row is labelled **"MSD string sort (ext. ASCII with 256 cutoff)"**. The
measurements are sound; the caption is not. They describe a cutoff of **20**.

### Why it happened

MSD radix sort stops recursing when a sub-array gets small and hands the rest to
quicksort. The size at which it does that is `Helper.MSDCutoff()`. That method was
overridden only on `InstrumentedComparatorHelper` — and a timing run is
uninstrumented, because instrumentation counts comparisons and swaps and would be
measured along with the sort. So every timed run took `Helper`'s default of 20,
whatever the configuration said.

This is a fact about the code rather than an inference from the numbers: it is
recorded in commit `22f9f0e4`, "MSDCutoff() was overridden only on
InstrumentedComparatorHelper, so every other Helper took Helper's default of
CUTOFF_DEFAULT (20)". The cutoff is honoured on both paths now.

### What the measurements show

Re-measured 2026-08-31 on an Apple M1, JDK 21, uninstrumented, sorting distinct
words from `eng-uk_web_2002_100K-sentences.txt`. Best of 12 after 20 warm-up runs;
every result checked against `Arrays.sort` with the same comparator. All four
columns come from the same machine, corpus and run, so they can be compared with
each other.

| n | MSD, cutoff 20 | MSD, cutoff 256 | quicksort | merge sort |
| ---: | ---: | ---: | ---: | ---: |
| 1,000 | 0.469 ms | 0.364 ms | 0.434 ms | 0.456 ms |
| 4,000 | 1.939 ms | 1.402 ms | 0.713 ms | 2.104 ms |
| 16,000 | **3.047 ms** | 5.285 ms | 3.254 ms | 3.531 ms |
| 64,000 | **14.911 ms** | 24.629 ms | 18.117 ms | 20.904 ms |

Compare the 16,000 row with the book's: MSD 3.1, quicksort 3.3, merge sort 3.5.
The cutoff-20 column reproduces all three to within a few percent. The cutoff-256
column does not, and is not close.

### Why the caption cannot simply be honoured

Suppose the row were re-measured at a true cutoff of 256, to match its label. At
16,000 MSD becomes 5.285 ms against quicksort's 3.254 and merge sort's 3.531 — so
MSD would be the *slowest* of the three, and the chapter's conclusion that MSD is
"consistently faster than other sorts... Only when used for problems of size 4k or
smaller is MSD not the fastest algorithm" would be false.

**The correction is to the caption, not to the data.** Read the row as
"MSD string sort (ext. ASCII with 20 cutoff)" and everything the chapter says
about it holds.

### A second, smaller correction: p. 141

"A cutoff of 256 works well" is true only for small inputs. On this corpus 256 is
about 10–20% faster below about 8,000 words, and from 16,000 upwards a cutoff of 20
is **1.6–1.7× faster**. Since Table 8.1 draws its conclusion at 16,000 and above,
the sentence is wrong exactly where it matters most.

### What is not established

- **Only n up to 64,000 was re-measured.** The corpus in this repository yields
  81,546 distinct words, and `getWords` takes distinct words, so the larger sizes
  in the published table cannot be reproduced here. The crossover might narrow or
  widen further out; nothing here says.
- **The two smallest rows do not match the book** — 0.469 ms against 0.16 at
  n = 1,000. At that size the sort takes well under a millisecond and fixed costs
  dominate, and the book's figures were produced by a different harness on a
  different machine. The agreement at 16,000 and 64,000 is the meaningful signal,
  and those are the sizes the chapter reasons about.
- **One corpus, one machine, one afternoon.** See `Java vs Python.md` for the same
  caveat at greater length.

### Reproducing it

Time `MSDStringSort` directly rather than through `SortBenchmark`, so that the
cutoff can be varied without anything else changing:

```java
Config config = Config.load()
        .copy("helper", "instrument", "false")   // a timing run is uninstrumented
        .copy("helper", "msdcutoff", "20");      // or "256"
try (SortWithHelper<String> sorter =
             new MSDStringSort(CodePointMapper.ASCIIExt, words.length, 1, config)) {
    sorter.sort(words, 0, words.length);
}
```

Assert `sorter.getHelper().MSDCutoff()` is the value you asked for before believing
any timing: that assertion is what would have caught this in the first place.
