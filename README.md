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
There are about a thousand active tests, of which two-thirds should run green.
Don't worry about the failing tests--they fail because there are stubs in the code
that you need to replace with functioning code in many places
(see above in Navigation).

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

Either way you should see 362 tests, of which 298 run green.
As with the Java tests, don't worry about the failures.
Every one of the 64 is a stub raising `NotImplementedError("TO BE IMPLEMENTED")`,
and each is somewhere you need to write code.
If you see a failure that is *not* that, something is wrong with your installation.

There is a linter configured, which is worth running before you submit anything:

    uv run ruff check .

It should report no problems on a clean checkout.

Note that the Python tree covers only part of the material in the Java tree.
At present it mirrors `adt`, `compression`, `graphs` (union-find and Dijkstra only) and `select`.
There is no Python equivalent yet of `sort`, `util`, `misc`, `adt/threesum` or the team project,
so Chapters 7 and 8 in particular are Java-only.
The package names are the Python spelling of the Java ones:
`adt.symbol_table` for `adt.symbolTable`, `selection` for `select`, and so on.
