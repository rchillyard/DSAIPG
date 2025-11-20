from __future__ import annotations
from typing import Iterator, Optional
from numbers import Number
from .stack import Stack
from .stack_linked_list import StackLinkedList
from .bqs_exception import BQSException


class ShuntingYard:
    """
    The ShuntingYard class implements the Shunting Yard algorithm for evaluating mathematical expressions
    in infix notation.
    """

    def __init__(self, infix: str):
        """
        Constructor to initialize the ShuntingYard instance with an infix expression.
        """
        self.tokenizer: Iterator[str] = iter(infix.split())
        self.parentheses: int = 0
        self.op_stack: Stack[str] = StackLinkedList()
        self.val_stack: Stack[Number] = StackLinkedList()

    def evaluate(self) -> Number:
        """
        Evaluates an expression in infix notation using the Shunting Yard algorithm.
        """
        for token in self.tokenizer:
            self.process_token(token)
        
        if self.parentheses != 0:
            raise BQSException(f"there are {self.parentheses} superfluous parentheses (net)")
        
        while not self.op_stack.is_empty():
            self.operate()
            
        result = self.val_stack.pop()
        
        if not self.val_stack.is_empty():
            raise BQSException("there are superfluous values")
            
        return result

    def process_token(self, s: str) -> None:
        """
        Processes a single token in the given mathematical expression.
        """
        if s == "(":
            self.parentheses += 1
        elif s == ")":
            self.parentheses -= 1
            self.operate()
        elif s in "+-*/^%":
            self.op_stack.push(s)
        else:
            try:
                # Try integer first, then float? Java code uses Integer.parseInt
                # But Number implies it could be generic.
                # The Java code specifically does Integer.parseInt(s).
                # So I will stick to int.
                n = int(s)
                self.val_stack.push(n)
            except ValueError as e:
                raise BQSException(str(e))

    def operate(self) -> None:
        """
        Performs an operation based on the operator and operands from respective stacks.
        """
        y = self.val_stack.pop()
        x = self.val_stack.pop()
        op = self.op_stack.pop()
        
        if not isinstance(x, int) or not isinstance(y, int):
             # Should not happen based on process_token
             pass

        if op == "+":
            self.val_stack.push(x + y)
        elif op == "-":
            self.val_stack.push(x - y)
        elif op == "*":
            self.val_stack.push(x * y)
        elif op == "/":
            # Java integer division?
            # Java code: valStack.push(x / y); where x, y are Integer.
            # So it is integer division.
            self.val_stack.push(int(x / y)) 
        else:
            raise BQSException(f"operator not recognized: {op}")

if __name__ == "__main__":
    try:
        two_stack = ShuntingYard("2 * ( 4 - 3 )")
        print(two_stack.evaluate())
    except BQSException as e:
        print(e)
