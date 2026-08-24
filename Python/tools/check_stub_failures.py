"""
Run the test suite and assert that every failure is an unwritten exercise.

A plain `pytest` here is *meant* to be red: the `TO BE IMPLEMENTED` stubs are
the coursework, so their tests fail until a student writes the code. That makes
`pytest` useless as a CI gate on its own -- red is the correct result, so red
tells us nothing.

What we can usefully assert is the shape of the redness: every failing test
must be failing because it hit a stub, and nothing else. A genuine regression
-- a typo, a bad import, a broken algorithm -- shows up as a failure whose
exception is not `NotImplementedError("TO BE IMPLEMENTED")`, and that is what
this script fails on.

Exits 0 if all failures are stubs, 1 otherwise.
"""

from __future__ import annotations

import pathlib
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ElementTree

MARKER = "TO BE IMPLEMENTED"
PYTHON_DIR = pathlib.Path(__file__).resolve().parent.parent


def main() -> int:
    with tempfile.TemporaryDirectory() as tmp:
        report = pathlib.Path(tmp) / "report.xml"
        subprocess.run(
            [sys.executable, "-m", "pytest", "-q", f"--junit-xml={report}"],
            cwd=PYTHON_DIR,
            check=False,
        )
        if not report.is_file():
            print("ERROR: pytest produced no report; it probably failed to start.")
            return 1
        tree = ElementTree.parse(report)

    passed = 0
    skipped = 0
    stubbed: list[str] = []
    unexpected: list[tuple[str, str]] = []

    for case in tree.iter("testcase"):
        name = f"{case.get('classname')}::{case.get('name')}"
        problems = list(case.iter("failure")) + list(case.iter("error"))
        if not problems:
            if list(case.iter("skipped")):
                skipped += 1
            else:
                passed += 1
            continue
        detail = " ".join(
            (problem.get("message") or "") + " " + (problem.text or "")
            for problem in problems
        )
        if MARKER in detail:
            stubbed.append(name)
        else:
            first_line = next(
                (line.strip() for line in reversed(detail.splitlines()) if line.strip()),
                "(no detail)",
            )
            unexpected.append((name, first_line))

    total = passed + skipped + len(stubbed) + len(unexpected)
    print()
    print(f"  {total} tests: {passed} passed, {len(stubbed)} unwritten exercises, "
          f"{skipped} skipped, {len(unexpected)} unexpected failures")

    if unexpected:
        print()
        print(f"  {len(unexpected)} failure(s) are NOT unwritten exercises:")
        print()
        for name, detail in unexpected:
            print(f"    {name}")
            print(f"        {detail}")
        print()
        return 1

    print("  All failures are unwritten exercises, as expected.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
