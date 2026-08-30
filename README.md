# DSAIPG
## Introduction and Provenance
Companion repository to:
"Data Structures, Algorithms, and Invariants--A Practical Guide"
by Robin Hillyard, College of Engineering, Northeastern University, Boston, MA, USA.
Published by Cognella.

This is version 1-2 (second revision of the first edition).

## Installation
There are two major directories within this repository:
* Java
* Python

The repository is designed to be cloned from https://github.com/rchillyard/DSAIPG.git
Alternatively, if you will be submitting assignments based on the repository,
then you might want to fork it instead.

The Java repository contains a Maven project (see the `pom.xnl` file in the top level).
Ideally, you will use an IDE that is suited to Maven projects.
I recommend IntelliJ IDEA for Java work.

## Navigation
The simplest way to find code is just to use the `Navigate/Class` menu.
It is good at recognizing what you're looking for.
However, note that the first character of all classes is upper case (and you will need to search thus).

Exercises and code relating to the various chapters can be found as given below and under the package `com.phasmidsoftware.dsaipg`:
* Chapters 1 and 2: `misc` and `util`; (if any)
* Chapters 3 through 6: `adt`;
* Chapters 7 and 8: `sort` (and `select`);
* Chapters 9 and 10: `graphs`;
* Team Project: `projects`.

Other package directories contain other code.

In order to find TODOs, etc. you should use the TODO tool window.
I recommend adding the following pattern to be recognized as needing attention
(TODO is already a known pattern):
* \bTO BE IMPLEMENTED\b.*

This will make it easy to see where you have to write code.

## Building and Testing (Java)
If you have cloned (or forked) the repository into IDEA, it should build the Java project
for you without much intervention on your part.
You will need at least Java 21 as your SDK.
The `pom.xml` sets both the source and target release to 21,
so an earlier JDK will not build the project.

To test the installation, run all the tests in `src/test/java`.
You should see about 1350 tests: rather over a thousand green,
close to 300 skipped, and **nothing red**.

A skipped test is one which reached a method you have not written yet--
a stub you are meant to replace with working code (see above in Navigation).
It will turn green once you have written it.
Run with `-Dsurefire.useFile=false`, or read the reports in `target/surefire-reports`,
and each skip names the file and line waiting for you.

Anything red is a real problem, not an unfinished exercise:
either a mistake in your own code or something wrong with your installation.
That is the point of the skips--they keep red and green meaning something
while the work is unfinished.

There are also functional tests in the `src/it/java` directory.
However, these take significantly longer to run and are really not necessary. 

## Building and Testing (Python)
The `Python` directory is a self-contained project managed by [uv](https://docs.astral.sh/uv/).
You will need Python 3.10 or later.
Everything below is run from inside the `Python` directory.

If you have `uv` installed, that is all you need:

    uv sync
    uv run pytest

`uv sync` creates a virtual environment and installs the pinned dependencies from `uv.lock`.

If you would rather not use `uv`, a plain virtual environment works just as well:

    python3 -m venv .venv
    .venv/bin/pip install pytest ruff
    .venv/bin/python -m pytest

Either way you should see 1855 tests: 1607 green and 248 skipped, and nothing red.
A skipped test is one which reached a method you have not written yet,
and it will turn green once you have written it.
Anything red is a real problem, not an unfinished exercise.

To see what each skip is waiting for, add `-rs`:

    uv run pytest -rs

which reports, for each, the file and line to go and write.

There is a linter configured, which is worth running before you submit anything:

    uv run ruff check .

It should report no problems on a clean checkout.

The Python tree mirrors the Java one: `adt` (including `threesum`), `compression`,
`graphs`, `misc`, `projects` (the team project), `select`, `sort` and `util`.
Every package in the Java tree has a counterpart.
The package names are the Python spelling of the Java ones:
`adt.symbol_table` for `adt.symbolTable`, `selection` for `select`, and so on.

Where the two trees differ on purpose, the Python says so in its own docstring,
with the reason. The Java tree remains the reference: where they differ by
accident, Java is what the book describes.

## Acknowledgements

The Python tree exists because of work by four students, and this section is here
because a merged branch or a closed pull request is an easy place for a name to
get lost:

* **Ashish Nevan** — the original Python port, from which the current tree descends.
* **Rakshith Narayanaswamy** — the Python port, and bringing it forward so that it
  was there to build on.
* **Neha Devarapalli** — the Python port, and in the Java tree the ThreeSum test
  corrections.
* **Gaurav Popat Gunjal** — the Python port, and in the Java tree the fix for
  reading the corpus files, which failed for anyone whose home directory contained
  a space.

## Comparing the two

Having the same algorithms twice makes it possible to ask what survives a change
of language and what does not.
`docs/Java vs Python.md` records some measurements and what they show:
that a growth rate belongs to the algorithm rather than to the language,
that a constant factor of forty buys you about five hundred elements,
that there is no single ratio between the two languages -- these range from 5 to 100 --
and that an optimisation which halves the time in one language can be worth nothing in the other.
It also says how to repeat the measurements for yourself, which is more use than reading them.
