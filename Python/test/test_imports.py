"""
Guard against modules that cannot be imported at all.

Three modules in the original port were dead on arrival -- two in
graphs/undirected imported `adt.bqs...` without the `src.` prefix, and
compression/codelength/code_length.py had kept the Java package path
(`com.phasmidsoftware.dsaipg...`) verbatim. None of the three is covered by a
test, so nothing noticed. This test imports every module under `src` so that a
bad import is a failure rather than a surprise.
"""

import importlib
import pathlib

import pytest

_SRC = pathlib.Path(__file__).resolve().parent.parent / "src"


def _module_names() -> list[str]:
    names = []
    for path in sorted(_SRC.rglob("*.py")):
        relative = path.relative_to(_SRC.parent).with_suffix("")
        names.append(".".join(relative.parts))
    return names


@pytest.mark.parametrize("module_name", _module_names())
def test_module_is_importable(module_name: str) -> None:
    importlib.import_module(module_name)
