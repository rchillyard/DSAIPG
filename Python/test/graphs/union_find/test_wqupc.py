import pytest

from src.graphs.union_find.wqupc import WQUPC


def test_initial_state():
    uf = WQUPC(5)
    assert uf.count == 5
    for i in range(5):
        assert uf.find(i) == i
        assert uf.size_of(i) == 1


def test_union_and_connected():
    uf = WQUPC(10)
    uf.union(1, 2)
    uf.union(2, 3)
    uf.union(4, 5)
    assert uf.connected(1, 3)
    assert not uf.connected(1, 4)
    assert uf.count == 7
    uf.union(3, 4)
    assert uf.connected(1, 5)
    assert uf.count == 6


def test_path_compression_effect():
    uf = WQUPC(6)
    uf.union(0, 1)
    uf.union(1, 2)
    uf.union(2, 3)
    uf.union(3, 4)
    r_before = uf.find(4)
    assert uf.connected(0, 4)
    r_after = uf.find(4)
    assert r_before == r_after
    assert uf.size_of(0) == 5


def test_invalid_index():
    uf = WQUPC(3)
    with pytest.raises(IndexError):
        uf.find(-1)
    with pytest.raises(IndexError):
        uf.union(0, 3)