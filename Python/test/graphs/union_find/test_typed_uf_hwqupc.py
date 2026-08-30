import pytest

from src.graphs.union_find.typed_uf_hwqupc import TypedUF_HWQUPC
from src.graphs.union_find.uf_exception import UFException


def test_connected_true():
    elements = ["A", "B", "C", "D"]
    tuf = TypedUF_HWQUPC(elements)
    tuf.union("A", "B")
    assert tuf.connected("A", "B")


def test_connected_false():
    elements = ["A", "B", "C", "D"]
    tuf = TypedUF_HWQUPC(elements)
    assert not tuf.connected("A", "C")


def test_connected_transitive():
    elements = ["A", "B", "C", "D"]
    tuf = TypedUF_HWQUPC(elements)
    tuf.union("A", "B")
    tuf.union("B", "C")
    assert tuf.connected("A", "C")


def test_connected_with_non_existent_element():
    elements = ["A", "B", "C", "D"]
    tuf = TypedUF_HWQUPC(elements)
    with pytest.raises(UFException):
        tuf.connected("A", "X")


def test_connected_both_non_existent_elements():
    elements = ["A", "B", "C", "D"]
    tuf = TypedUF_HWQUPC(elements)
    with pytest.raises(UFException):
        tuf.connected("X", "Y")


def test_connected_true_uf_hwqupc():
    from src.graphs.union_find.uf_hwqupc import UF_HWQUPC

    uf = UF_HWQUPC(4)
    uf.union(0, 1)
    assert uf.is_connected(0, 1)


def test_connected_false_uf_hwqupc():
    from src.graphs.union_find.uf_hwqupc import UF_HWQUPC

    uf = UF_HWQUPC(4)
    assert not uf.is_connected(0, 2)


def test_connected_transitive_uf_hwqupc():
    from src.graphs.union_find.uf_hwqupc import UF_HWQUPC

    uf = UF_HWQUPC(4)
    uf.union(0, 1)
    uf.union(1, 2)
    assert uf.is_connected(0, 2)


def test_connected_out_of_bounds_uf_hwqupc():
    from src.graphs.union_find.uf_hwqupc import UF_HWQUPC

    uf = UF_HWQUPC(4)
    with pytest.raises(IndexError):
        uf.is_connected(0, 5)
