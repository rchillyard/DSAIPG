class PQException(Exception):
    """
    Represents a custom exception used in scenarios related to Priority Queue operations.
    This exception is typically thrown to indicate specific errors or issues
    that occur during Priority Queue processing.
    """
    def __init__(self, msg: str):
        """
        Constructs a new PQException with the specified detail message.

        Args:
            msg: the detail message.
        """
        super().__init__(msg)
