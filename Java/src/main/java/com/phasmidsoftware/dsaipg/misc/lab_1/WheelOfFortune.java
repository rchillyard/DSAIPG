package com.phasmidsoftware.dsaipg.misc.lab_1;

import java.util.Objects;
import java.util.Random;

/**
 * Wheel of Fortune class which will yield a value of T according to a probability distribution
 * given by the set of events.
 * <p>
 * In order to pass all the unit tests, you will also have to implement code in FrequencyCounter.
 * That bit of code is very simple.
 *
 * @param <T> the type yielded by get().
 */
public class WheelOfFortune<T> {

    /**
     * Method to get a randomly chosen value of T.
     * <p>
     * The result depends on the events and the next value of random.
     *
     * @return a T from one of the events, whose probability of being selected
     * is the quotient of the corresponding frequency and the value of total.
     */
    public T get() {
        int r = random.nextInt(total);
        // TO BE IMPLEMENTED 
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * Primary constructor.
     *
     * @param random an instance of Random.
     * @param events a variable number of Event instances.
     */
    @SafeVarargs
    public WheelOfFortune(Random random, Event<T>... events) {
        this.random = random;
        this.events = events;
        this.total = getTotal();
    }

    /**
     * Secondary constructor that uses a Random based on the given seed.
     *
     * @param seed   a long value which will be used as the seed for the random number generator.
     * @param events a variable number of Event instances.
     */
    @SafeVarargs
    public WheelOfFortune(long seed, Event<T>... events) {
        this(new Random(seed), events);
    }

    /**
     * Secondary constructor that uses a randomly-seeded Random.
     *
     * @param events a variable number of Event instances.
     */
    @SafeVarargs
    public WheelOfFortune(Event<T>... events) {
        this(new Random(), events);
    }

    // Private stuff...

    /**
     * Get the total of the frequencies of all the events.
     *
     * @return the total of all frequencies.
     */
    private int getTotal() {
        int total = 0;
        for (Event<T> event : events) total += event.frequency;
        return total;
    }

    /**
     * A final instance of {@link Random} used for generating random numbers
     * within the {@code WheelOfFortune} class. This variable is central
     * to the functionality of the class, as it ensures the probabilistic
     * selection of events based on their frequency.
     * <p>
     * The {@code random} instance is provided during the construction of
     * the {@code WheelOfFortune} object, either directly or initialized
     * based on a seed or default configuration.
     */
    private final Random random;
    /**
     * Represents an array of events that can be used within the context of the
     * WheelOfFortune class to determine outcomes based on their frequencies.
     *
     * Each event contains an associated value of type {@code T} and a frequency that
     * indicates the likelihood of that event being selected in probabilistic operations.
     *
     * This array is initialized through the class constructors and remains immutable
     * once assigned.
     *
     */
    private final Event<T>[] events;
    /**
     * The cumulative total of the frequencies of all events in the wheel.
     * This value is calculated during initialization and is used to determine
     * the probability of selecting each event during random choice operations.
     *
     * This variable is immutable and reflects the aggregated frequency sum
     * of the provided {@code Event} instances.
     */
    private final int total;

    /**
     * Represents an immutable event with a value of a generic type and a specific frequency.
     * This class is used for modeling events with associated probabilities based on their frequencies.
     *
     * @param <E> the type of the event.
     */
    public static class Event<E> {

        /**
         * Compares this Event object to the specified object for equality.
         * Two Event objects are considered equal if they belong to the same class,
         * and their respective event and frequency fields are equal.
         *
         * @param o the object to be compared for equality with this Event.
         * @return true if the specified object is equal to this Event, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Event<?> event1 = (Event<?>) o;
            return frequency == event1.frequency &&
                    Objects.equals(event, event1.event);
        }

        /**
         * Computes the hash code for this object based on its properties.
         *
         * @return an integer representing the hash code of the object, calculated using its "event" and "frequency" fields.
         */
        @Override
        public int hashCode() {
            return Objects.hash(event, frequency);
        }

        /**
         * Represents an event of a generic type.
         * This field holds the value of the event that the enclosing class represents.
         *
         * @param <E> the type of the event.
         */
        final E event;
        /**
         * Represents the frequency of an event.
         * This field stores an integer value denoting the occurrence count of a specific event.
         * It is immutable and used in calculations related to event probabilities or occurrences.
         */
        final int frequency;

        /**
         * Constructs an instance of the Event class with the specified event value and frequency.
         *
         * @param event     the value of the event of type E.
         * @param frequency the frequency associated with the event, representing its occurrence count.
         */
        Event(E event, int frequency) {
            this.event = event;
            this.frequency = frequency;
        }
    }

    /**
     * Creates an instance of {@link Event} with the specified event value and frequency.
     *
     * @param <E>       the type of the event value.
     * @param event     the event value for the new Event instance.
     * @param frequency the frequency associated with the event.
     * @return a new instance of {@link Event} containing the specified event and frequency.
     */
    public static <E> Event<E> valueOf(E event, int frequency) {
        return new Event<>(event, frequency);
    }

}
