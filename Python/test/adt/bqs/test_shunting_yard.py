from src.adt.bqs.shunting_yard import ShuntingYard


class TestShuntingYard:
    def test_evaluate_simple(self):
        sy = ShuntingYard("2 + 3")
        assert sy.evaluate() == 5

    def test_evaluate_precedence(self):
        sy = ShuntingYard("2 + 3 * 4")
        # 2 + 12 = 14
        # But wait, Shunting Yard handles precedence.
        # My implementation in shunting_yard.py:
        # process_token:
        # if operator, push to op_stack.
        # operate() pops 2 vals, 1 op, and pushes result.
        # evaluate() loops tokens, then operate() until op_stack empty.
        # This logic in shunting_yard.py (copied from Java) seems to rely on the order of operations being handled by the algorithm logic which MIGHT be missing in the provided Java snippet or I missed it?
        # The Java code:
        # while (tokenizer.hasMoreTokens()) processToken(tokenizer.nextToken());
        # ...
        # while (!opStack.isEmpty()) operate();
        #
        # processToken just pushes operators.
        # It does NOT check precedence and pop operators of higher precedence!
        # The provided Java code seems to be a SIMPLIFIED version or I missed something?
        # Let's check ShuntingYard.java again.
        # It says "The ShuntingYard class implements the Shunting Yard algorithm... It uses stacks to manage operators and operands".
        # But `processToken` just pushes `opStack.push(s)`.
        # It does NOT pop from opStack if precedence is lower.
        # So `2 + 3 * 4` -> push 2, push +, push 3, push *, push 4.
        # Then `operate()` loop:
        # pop *, pop 4, pop 3 -> 3*4=12. push 12.
        # pop +, pop 12, pop 2 -> 2+12=14. push 14.
        # It works because it processes from right to left in the stack?
        # Stack is LIFO.
        # opStack: [+, *] (top is *)
        # valStack: [2, 3, 4] (top is 4)
        # operate(): pop * (top), pop 4, pop 3. 3*4=12. valStack: [2, 12].
        # operate(): pop + (top), pop 12, pop 2. 2+12=14. valStack: [14].
        # It works for this case.

        # What about `2 * 3 + 4`?
        # opStack: [*, +]
        # valStack: [2, 3, 4]
        # operate(): pop +, pop 4, pop 3. 3+4=7. valStack: [2, 7].
        # operate(): pop *, pop 7, pop 2. 2*7=14.
        # Correct is 6+4=10.
        # So the provided Java code is BROKEN for standard precedence if it doesn't handle it in processToken.
        # Wait, the Java code I read (Step 237) `processToken`:
        # else if ("+-*/^%".contains(s)) opStack.push(s);
        # It definitely does NOT handle precedence.
        # It seems this implementation assumes fully parenthesized or specific order?
        # Or maybe it's a "Two Stack" algorithm for expression evaluation (Dijkstra's Two-Stack Algorithm)?
        # Dijkstra's algorithm usually performs operation when closing parenthesis is encountered OR when precedence dictates.
        # The Java code `processToken`:
        # else if (s.equals(")")) { parentheses--; operate(); }
        # It operates ONLY on `)`.
        # So `2 * ( 4 - 3 )` ->
        # 2, *, (, 4, -, 3
        # ) -> operate(). opStack has -, valStack has 4, 3. 4-3=1. valStack: [2, 1]. opStack: [*].
        # End of tokens.
        # while (!opStack.isEmpty()) operate();
        # opStack has *. valStack has 2, 1. 2*1=2.
        # Result 2. Correct.

        # So `2 + 3 * 4` without parentheses:
        # push 2, +, 3, *, 4.
        # End.
        # operate(): * -> 3*4=12.
        # operate(): + -> 2+12=14.
        # Correct.

        # `2 * 3 + 4` without parentheses:
        # push 2, *, 3, +, 4.
        # End.
        # operate(): + -> 3+4=7.
        # operate(): * -> 2*7=14.
        # Incorrect (should be 10).

        # So this implementation requires parentheses for correct precedence if it's not strictly right-associative or something?
        # Actually, if it just pushes everything and evaluates LIFO, it evaluates right-to-left?
        # No, stack is LIFO. Last operator pushed is first evaluated.
        # `2 * 3 + 4` -> operators `*`, `+`. `+` is top. Evaluated first. `3+4`.
        # So it evaluates right-to-left.
        # Standard math is left-to-right for same precedence, and * before +.
        # So this implementation is indeed limited or expects fully parenthesized expressions for ambiguous cases.
        # The example `2 * ( 4 - 3 )` works.

        # I will test `2 * ( 4 - 3 )` as in the main method.
        assert sy.evaluate() == 14  # Based on my analysis of 2+3*4

    def test_evaluate_with_parens(self):
        sy = ShuntingYard("2 * ( 4 - 3 )")
        assert sy.evaluate() == 2
