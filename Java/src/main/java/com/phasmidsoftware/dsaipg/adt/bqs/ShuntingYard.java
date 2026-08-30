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

    /**
     * A StringTokenizer instance used to parse and process a sequence of tokens
     * representing a mathematical expression in infix notation.
     * This variable provides the tokens (operands, operators, and parentheses)
     * required for computation using the Shunting Yard algorithm.
     */
    private final StringTokenizer tokenizer;

    /**
     * The variable "parentheses" is used to track the count of parentheses within the Shunting Yard algorithm.
     * It is incremented or decremented based on encountering opening or closing parentheses while processing tokens.
     * This variable ensures the expression being evaluated is well-formed in terms of parentheses matching.
     * <p>
     * A value of 0 indicates that all parentheses opened so far have been properly closed.
     * A non-zero value suggests unmatched opening or closing parentheses, which may result in evaluation errors.
     */
    private int parentheses = 0;

    /**
     * Represents a stack used for managing operators in the Shunting Yard algorithm.
     * This stack stores operators as strings and facilitates their processing
     * during the conversion of infix expressions to postfix notation and expression evaluation.
     *
     * This stack supports typical stack operations such as pushing operators onto
     * the stack, popping operators off the stack, and peeking at the top operator
     * without removal. The stack ensures a Last-In-First-Out (LIFO) behavior.
     *
     * Used internally by the {@code ShuntingYard} class to manage operator precedence
     * and association during expression parsing and evaluation.
     */
    private final Stack<String> opStack = new Stack_LinkedList<>();

    /**
     * A stack data structure used to store numeric values during the evaluation of an expression
     * in the Shunting Yard algorithm.
     *
     * The stack operates in a Last In, First Out (LIFO) manner. During the processing of an
     * expression, numeric operands are added to this stack as they are encountered, and results
     * of operations are pushed back onto it after computation. At the end of the evaluation,
     * this stack holds the final computed value of the expression.
     *
     * This implementation uses a linked list as the underlying data structure for the stack,
     * allowing dynamic resizing and efficient operations without a fixed storage limit.
     */
    private final Stack<Number> valStack = new Stack_LinkedList<>();
}
