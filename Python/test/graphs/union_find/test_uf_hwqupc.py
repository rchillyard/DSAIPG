import pytest

from src.graphs.union_find.uf_hwqupc import UF_HWQUPC


def test_to_string():
    h = UF_HWQUPC(2)
    expected = (
        "UF_HWQUPC:\n"
        "  count: 2\n"
        "  path compression? True\n"
        "  parents: [0, 1]\n"
        "  heights: [1, 1]"
    )
    assert str(h) == expected


def test_is_connected_01():
    h = UF_HWQUPC(2)
    assert not h.is_connected(0, 1)


def test_is_connected_02_out_of_bounds():
    h = UF_HWQUPC(1)
    with pytest.raises(IndexError):
        h.is_connected(0, 1)


def test_is_connected_03_update_parent():
    h = UF_HWQUPC(2)
    h._update_parent(0, 1)
    assert h.is_connected(0, 1)


def test_connect_01():
    h = UF_HWQUPC(2)
    h.connect(0, 1)
    assert h.is_connected(0, 1)


def test_connect_02_idempotent():
    h = UF_HWQUPC(2)
    h.connect(0, 1)
    h.connect(0, 1)
    assert h.is_connected(0, 1)


def test_find_0():
    h = UF_HWQUPC(1)
    assert h.find(0) == 0


def test_find_1():
    h = UF_HWQUPC(2)
    h.connect(0, 1)
    assert h.find(0) == 0
    assert h.find(1) == 0


def test_find_2_no_path_compression():
    h = UF_HWQUPC(3, path_compression=False)
    h.connect(0, 1)
    assert h.find(0) == 0
    assert h.find(1) == 0
    h.connect(2, 1)
    assert h.find(2) == 0


def test_find_3_no_path_compression():
    h = UF_HWQUPC(6, path_compression=False)
    h.connect(0, 1)
    h.connect(0, 2)
    h.connect(3, 4)
    h.connect(3, 5)
    assert h.find(0) == 0
    assert h.find(1) == 0
    assert h.find(2) == 0
    assert h.find(3) == 3
    assert h.find(4) == 3
    assert h.find(5) == 3
    h.connect(0, 3)
    assert h.find(4) == 0
    assert h._get_parent(4) in (0, 3)
    assert h._get_parent(5) in (0, 3)


def test_find_4_with_path_compression():
    h = UF_HWQUPC(6)
    h.connect(0, 1)
    h.connect(0, 2)
    h.connect(3, 4)
    h.connect(3, 5)
    assert h.find(0) == 0
    assert h.find(1) == 0
    assert h.find(2) == 0
    assert h.find(3) == 3
    assert h.find(4) == 3
    assert h.find(5) == 3
    h.connect(0, 3)
    assert h.find(4) == 0
    assert h._get_parent(4) == 0
    assert h.find(5) == 0
    assert h._get_parent(5) == 0


def test_find_5_out_of_bounds():
    h = UF_HWQUPC(1)
    with pytest.raises(IndexError):
        h.find(1)


def test_connected_01():
    h = UF_HWQUPC(10)
    assert not h.is_connected(0, 1)
