import pytest
from src.adt.bqs.d_list import DList


class TestDList:
    def test_init(self):
        d = DList[int]()
        assert d.is_empty()
        assert d.size() == 0

    def test_init_with_item(self):
        # Since add_before_element is not implemented, this might raise RuntimeError
        with pytest.raises(RuntimeError, match="implementation missing"):
            DList[int](1)
