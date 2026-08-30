package com.phasmidsoftware.dsaipg.misc.greedy;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A generic class implementing a greedy algorithm.
 * <p>
 * This class models a problem-solving approach where local optimizations
 * are made at each step in order to find the global solution,
 * assuming that the global solution can be constructed from these local optimizations.
 *
 * @param <T> the type of the greedy candidate and intermediary states.
 * @param <R> the type of the result produced by the greedy algorithm.
 *            <p>
 *            The class is initialized with the following functional parameters:
 *            - A function applied iteratively to select the next "greedy" candidate.
 *            - A function to adjust the state after selecting the "greedy" candidate.
 *            - A function to aggregate the current result with the "greedy" candidate.
 *            - A function to determine completion of the algorithm.
 *            These parameters define the behavior of the greedy algorithm.
 */
public class Greedy<T, R> {

    /**
     * Executes the greedy algorithm using the supplied functions to determine the next
     * greedy candidate, adjust the state, compute results, and check for completion.
     *
     * @param t the initial state or input for the algorithm.
     * @param r the initial result, which will be updated iteratively.
     * @return the final result after applying the greedy algorithm.
     */
    public R run(T t, R r) {
        while (!fDone.apply(t)) {
            T greedy = fGreedy.apply(t);
            r = fResult.apply(greedy, r);
            t = fAdjust.apply(t, greedy);
        }
        return r;
    }

    /**
     * Constructs a Greedy instance with the specified functional parameters.
     * The implementation of the greedy algorithm is defined by these parameters.
     *
     * @param fGreedy a function applied iteratively to determine the next "greedy" candidate from the current state.
     * @param fAdjust a function to adjust the current state based on the "greedy" candidate selected.
     * @param fResult a function to compute and update the current result based on the selected "greedy" candidate.
     * @param fDone a function to determine if the algorithm has completed its execution based on the current state.
     */
    public Greedy(final Function<T, T> fGreedy, final BiFunction<T, T, T> fAdjust, final BiFunction<T, R, R> fResult, final Function<T, Boolean> fDone) {
        this.fGreedy = fGreedy;
        this.fAdjust = fAdjust;
        this.fResult = fResult;
        this.fDone = fDone;
    }

    /**
     * A functional parameter representing the greedy selection function in the greedy algorithm.
     * <p>
     * This function is applied iteratively to the current state to determine the next "greedy" candidate.
     * The greedy candidate is typically the optimal choice at the current step according to the specific
     * criteria of the problem being solved.
     *
     * @param <T> the type of the state and the greedy candidate used in the algorithm.
     */
    private final Function<T, T> fGreedy;
    /**
     * A BiFunction that adjusts the current state based on the selected "greedy" candidate.
     * This function is used within the Greedy algorithm to update the state after each step.
     * <p>
     * It takes two arguments:
     * - The current state of the algorithm.
     * - The greedy candidate selected at the current step.
     * <p>
     * The function returns the new state after applying the adjustment logic.
     * This adjustment ensures progress in subsequent iterations of the algorithm.
     */
    private final BiFunction<T, T, T> fAdjust;
    /**
     * A functional parameter in the greedy algorithm representing the result aggregation function.
     * <p>
     * This function is used to compute and update the cumulative result during each step of the algorithm.
     * It takes the currently selected "greedy" candidate of type {@code T} and the current result of type {@code R}
     * to produce a new result of type {@code R}.
     * <p>
     * This behavior is essential in scenarios where the algorithm iteratively combines partial results generated
     * from each step to arrive at the final output.
     * <p>
     * Example applications may include summing, concatenating, or any other form of aggregation on the intermediate
     * results produced during the execution of the algorithm.
     */
    private final BiFunction<T, R, R> fResult;
    /**
     * A functional parameter defining the termination condition for a greedy algorithm.
     *
     * This function takes the current state as input and returns a Boolean value
     * indicating whether the algorithm should terminate.
     * It acts as a stopping criterion by evaluating the completion state of the algorithm.
     *
     * @param <T> the type of the current state or intermediary state used by the algorithm.
     */
    private final Function<T, Boolean> fDone;
}
