/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.StringTokenizer;

/**
 * The ShuntingYard class implements the Shunting Yard algorithm for evaluating mathematical expressions
 * in infix notation. This algorithm converts infix expressions to postfix notation and evaluates them
 * using operator precedence and associativity.
 * <p>
 * The class supports the basic mathematical operators: addition (+), subtraction (-), multiplication (*),
 * division (/), and parentheses for grouping. It uses stacks to manage operators and operands during the evaluation.
 * <p>
 * Main features of the class:
 * - Parsing tokens from an infix expression.
 * - Handling operator precedence and associativity.
 * - Evaluating the expression to produce a numeric result.
 * - Detecting and throwing exceptions for invalid expressions such as mismatched parentheses or unrecognized operators.
 * <p>
 * Dependencies:
 * - StringTokenizer for breaking down the input infix expression into tokens.
 * - Custom BQSException for error handling specific to the algorithm.
 * - Stack_LinkedList for managing stacks of operators and operands.
 */
public class ShuntingYard {

    /**
     * The main method serves as the entry point of the program. It demonstrates the functionality
     * of the ShuntingYard class by evaluating a mathematical expression provided in infix notation.
     *
     * @param args the command-line arguments passed to the program (not used in this method).
     */
    public static void main(String[] args) {
        try {
            ShuntingYard twoStack = new ShuntingYard("2 * ( 4 - 3 )");
            System.out.println(twoStack.evaluate());
        } catch (BQSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evaluates an expression in infix notation using the Shunting Yard algorithm.
     * This method processes each token of the expression, manages operators, values, and parentheses,
     * and performs operations to compute the result. The method ensures the expression is well-formed
     * and throws an exception if there are superfluous parentheses, operators, or operands.
     *
     * @return the result of evaluating the expression as a Number.
     * @throws BQSException if the expression contains unmatched parentheses, unrecognized operators,
     *                      or if the operand stack has leftover values after evaluation.
     */
    public Number evaluate() throws BQSException {
        while (tokenizer.hasMoreTokens())
            processToken(tokenizer.nextToken());
        if (parentheses != 0)
            throw new BQSException("there are " + parentheses + " superfluous parentheses (net)");
        while (!opStack.isEmpty())
            operate();
        Number result = valStack.pop();
        if (!valStack.isEmpty())
            throw new BQSException("there are superfluous values");
        return result;
    }

    /**
     * Primary constructor to initialize the ShuntingYard instance with a given tokenizer.
     * The tokenizer provides the sequence of tokens (operands, operators, and parentheses)
     * that will be processed by the Shunting Yard algorithm for evaluation.
     *
     * @param tokenizer the StringTokenizer instance containing the tokens
     *                  to be processed for computation using the Shunting Yard algorithm.
     */
    public ShuntingYard(StringTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Secondary constructor to initialize the ShuntingYard instance with an infix expression.
     * The infix expression will be tokenized and processed using the Shunting Yard algorithm.
     *
     * @param infix the mathematical expression in infix notation, as a String.
     */
    public ShuntingYard(String infix) {
        this(new StringTokenizer(infix));
    }

    /**
     * Processes a single token in the given mathematical expression.
     * Updates operator stack, value stack, and parentheses count as necessary,
     * and performs operations based on the token provided.
     *
     * @param s the token to be processed, which can be an operator, operand, or parenthesis.
     * @throws BQSException if an error occurs during token processing, such as an
     *                      unrecognized operator or a mismatched parenthesis in the expression.
     */
    private void processToken(String s) throws BQSException {
        if (s.equals("("))
            parentheses++;
        else if (s.equals(")")) {
            parentheses--;
            operate();
        } else if ("+-*/^%".contains(s))
            opStack.push(s);
        else {
            try {
                Number n = Integer.parseInt(s);
                valStack.push(n);
            } catch (NumberFormatException e) {
                throw new BQSException(e);
            }
        }
    }

    /**
     * Performs an operation based on the operator and operands from respective stacks.
     *
     * This method pops two integers from the value stack (valStack) and an operator from
     * the operator stack (opStack). Based on the operator, it performs the corresponding
     * arithmetic operation (addition, subtraction, multiplication, or division) on the
     * two operands and pushes the result back onto the value stack. If the operator is
     * not recognized, it throws a BQSException.
     *
     * @throws BQSException if:
     *                      - the value stack does not contain enough elements to perform
     *                        an operation.
     *                      - an unrecognized operator is encountered.
     */
    private void operate() throws BQSException {
        Integer y = (Integer) valStack.pop();
        Integer x = (Integer) valStack.pop();
        switch (opStack.pop()) {
            case "+":
                valStack.push(x + y);
                break;
            case "-":
                valStack.push(x - y);
                break;
            case "*":
                valStack.push(x * y);
                break;
            case "/":
                valStack.push(x / y);
                break;
            default:
                throw new BQSException("operator not recognized: " + opStack.pop());
        }
    }

    private final StringTokenizer tokenizer;
    private int parentheses = 0;
    private final Stack<String> opStack = new Stack_LinkedList<>();
    private final Stack<Number> valStack = new Stack_LinkedList<>();
}