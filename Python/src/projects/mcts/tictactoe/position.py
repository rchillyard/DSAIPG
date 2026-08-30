"""
Ported from projects/mcts/tictactoe/Position.java.
"""

from __future__ import annotations

#: The side of the board.
GRID_SIZE = 3

#: What a cell holds: 0 for O, 1 for X, -1 for empty.
BLANK = -1


class Position:
    """
    A 3x3 board of 0s, 1s and -1s, for O, X and empty.

    Immutable: every method that would change the board returns a new Position.

    NOTE the grid is held as a tuple of tuples rather than a list of lists, so that
    equality and hashing come for free -- the Java needs Arrays.deepEquals and
    deepHashCode for the same reason.
    """

    def __init__(self, grid, count: int, last: int) -> None:
        """
        :param grid: the cells, as any nested sequence.
        :param count: how many cells are occupied.
        :param last: the player who moved last, or -1 if none has.
        """
        self.grid = tuple(tuple(row) for row in grid)
        self.count = count
        self.last = last
        self.xxx = (last, last, last)

    @staticmethod
    def parse_position(grid: str, last: int) -> Position:
        """
        :param grid: three rows of three cells, rows separated by newlines and
                     cells by spaces; each cell X, O or anything else for empty.
        :param last: the player who moved last, or -1 if none has.
        :return: the Position it describes.
        """
        matrix = []
        count = 0
        for row in grid.split("\n", GRID_SIZE)[:GRID_SIZE]:
            cells = []
            for cell in row.split(" ", GRID_SIZE)[:GRID_SIZE]:
                value = Position.parse_cell(cell.strip())
                if value >= 0:
                    count += 1
                cells.append(value)
            matrix.append(cells)
        return Position(matrix, count, last)

    @staticmethod
    def parse_cell(cell: str) -> int:
        """
        :param cell: one cell of a rendered board.
        :return: 0 for O, 1 for X, -1 for anything else.
        """
        match cell.upper():
            case "O" | "0":
                return 0
            case "X" | "1":
                return 1
            case _:
                return BLANK

    def move(self, player: int, x: int, y: int) -> Position:
        """
        :param player: the player moving.
        :param x: the row.
        :param y: the column.
        :return: the Position after that move.
        :raises RuntimeError: if the board is full, the same player has just moved,
                              or the cell is taken.
        """
        if self.full():
            raise RuntimeError("Position is full")
        if player == self.last:
            raise RuntimeError(f"consecutive moves by same player: {player}")
        matrix = self.copy_grid()
        if matrix[x][y] < 0:
            # TO BE IMPLEMENTED
            raise NotImplementedError("TO BE IMPLEMENTED")
        raise RuntimeError(f"Position is occupied: {x}, {y}")

    def moves(self, player: int) -> list[list[int]]:
        """
        :param player: the player to move.
        :return: the coordinates of every empty cell, as [row, column] pairs.
        :raises RuntimeError: if the same player has just moved.
        """
        if player == self.last:
            raise RuntimeError(f"consecutive moves by same player: {player}")
        result: list[list[int]] = []
        for i in range(GRID_SIZE):
            for j in range(GRID_SIZE):
                if self.grid[i][j] < 0:
                    # TO BE IMPLEMENTED
                    raise NotImplementedError("TO BE IMPLEMENTED")
        return result

    def three_in_a_row(self) -> bool:
        """
        :return: whether the player who moved last has three in a row, in any row,
                 column or diagonal.
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def reflect(self, axis: int) -> Position:
        """
        :param axis: 0 to reflect about the middle row, 1 about the middle column.
        :return: the reflected Position.
        :raises RuntimeError: for any other axis.
        """
        matrix = self.copy_grid()
        if axis == 0:
            for j in range(GRID_SIZE):
                self._swap(matrix, 0, j, 2, j)
        elif axis == 1:
            for i in range(GRID_SIZE):
                self._swap(matrix, i, 0, i, 2)
        else:
            raise RuntimeError(f"reflect not implemented for {axis}")
        return Position(matrix, self.count, self.last)

    def rotate(self) -> Position:
        """
        :return: the Position turned through a quarter turn.
        """
        matrix = [[self.grid[j][GRID_SIZE - i - 1] for j in range(GRID_SIZE)]
                  for i in range(GRID_SIZE)]
        return Position(matrix, self.count, self.last)

    def winner(self) -> int | None:
        """
        NOTE fewer than five moves cannot have produced a line, so the check is
        skipped until then.

        :return: the winning player, or None if there is not one yet.
        """
        if self.count > 4 and self.three_in_a_row():
            return self.last
        return None

    def project_row(self, i: int) -> tuple[int, ...]:
        """
        :param i: which row.
        :return: its cells.
        """
        return self.grid[i]

    def project_col(self, j: int) -> tuple[int, ...]:
        """
        :param j: which column.
        :return: its cells.
        """
        return tuple(self.grid[i][j] for i in range(GRID_SIZE))

    def project_diag(self, b: bool) -> tuple[int, ...]:
        """
        :param b: True for the leading diagonal, False for the other.
        :return: its cells.
        """
        return tuple(self.grid[j if b else GRID_SIZE - j - 1][j] for j in range(GRID_SIZE))

    def full(self) -> bool:
        """
        :return: whether every cell is taken.
        """
        return self.count == GRID_SIZE * GRID_SIZE

    def render(self) -> str:
        """
        :return: the board as X, O and ., rows separated by newlines.
        """
        return "\n".join(" ".join(self._render_cell(c) for c in row) for row in self.grid)

    def copy_grid(self) -> list[list[int]]:
        """
        :return: the cells as a nested list, which a caller may change.
        """
        return [list(row) for row in self.grid]

    @staticmethod
    def _swap(matrix: list[list[int]], i1: int, j1: int, i2: int, j2: int) -> None:
        """
        Exchange two cells in place.

        :param matrix: the cells.
        :param i1: the first row.
        :param j1: the first column.
        :param i2: the second row.
        :param j2: the second column.
        """
        matrix[i1][j1], matrix[i2][j2] = matrix[i2][j2], matrix[i1][j1]

    @staticmethod
    def _render_cell(x: int) -> str:
        """
        :param x: a cell.
        :return: "O", "X" or ".".
        """
        return {0: "O", 1: "X"}.get(x, ".")

    def __eq__(self, other: object) -> bool:
        return isinstance(other, Position) and self.grid == other.grid

    def __hash__(self) -> int:
        return hash(self.grid)

    def __str__(self) -> str:
        return "\n".join(",".join(str(c) for c in row) for row in self.grid)
