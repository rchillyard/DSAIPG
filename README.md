# DSAIPG
## Introduction and Provenance
Companion repository to:
"Data Structures, Algorithms, and Invariants--A Practical Guide"
by Robin Hillyard, College of Engineering, Northeastern University, Boston, MA, USA.
Published by Cognella.

This is version 1-1 (first revision of the first edition).

## Installation
This project is designed to be cloned from https://github.com/rchillyard/DSAIPG.git
Alternatively, if you will be submitting assignments based on the repository,
then you might want to fork it instead.

This repository contains a Maven project (see the `pom.xnl` file in the top level).
Ideally, you will use an IDE that is suited to Maven projects.
I recommend IntelliJ IDEA.

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

## Building and Testing
If you have cloned (or forked) the repository into IDEA, it should build the project
for you without much intervention on your part.
You will need at least Java 17 as your SDK.
Recommended: Oracle OpenJDK 18.0.2

To test the installation, run all the tests in `src/test/java`.
There are about a thousand active tests, of which two-thirds should run green.
Don't worry about the failing tests--they fail because there are stubs in the code
that you need to replace with functioning code in many places
(see above in Navigation).

There are also functional tests in the `src/it/java` directory.
However, these take significantly longer to run and are really not necessary. 

