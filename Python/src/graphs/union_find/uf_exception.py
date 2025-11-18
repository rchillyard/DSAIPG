class UFException(Exception):
    """Exception for union-find operations."""

    def __init__(self, msg: str):
        super().__init__(msg)