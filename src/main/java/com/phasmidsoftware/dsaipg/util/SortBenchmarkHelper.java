/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;


import com.phasmidsoftware.dsaipg.sort.SortException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.phasmidsoftware.dsaipg.util.Utilities.formatWhole;


/**
 * A utility class designed to provide helper methods for benchmarking and testing sorting algorithms.
 * This class includes methods for generating test data,
 * extracting words from text resources, and logging utility results.
 * It is structured with static methods and a private constructor to ensure it operates as a singleton helper.
 */
public class SortBenchmarkHelper {

    /**
     * Generates an array of random LocalDateTime instances.
     * <p>
     * Each LocalDateTime in the array is generated with a random epoch second and nanosecond
     * based on the current system time, using ThreadLocalRandom for randomness.
     *
     * @param number the number of LocalDateTime instances to generate.
     * @return an array of randomly generated LocalDateTime instances.
     */
    public static LocalDateTime[] generateRandomLocalDateTimeArray(int number) {
        LocalDateTime[] result = new LocalDateTime[number];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < number; i++) {
            result[i] = LocalDateTime.ofEpochSecond(random.nextLong(new Date().getTime()), random.nextInt(0, 1000000000), ZoneOffset.UTC);
        }
        return result;
    }

    /**
     * Reads words from a specified resource,
     * processes them using a provided function to extract collections of strings,
     * removes duplicate words, filters out words shorter than a defined minimum length,
     * and returns the resulting distinct words as an array.
     *
     * @param resource   the name of the resource file containing the lines to be read
     * @param getStrings a function that processes each line of text and returns a collection of strings
     * @return an array containing the distinct words from the resource, each having at least the minimum required length
     * @throws FileNotFoundException if the specified resource file cannot be found
     */
    // TEST
    public static String[] getWords(String resource, Function<String, Collection<String>> getStrings) throws FileNotFoundException {
        List<String> words = new ArrayList<>();
        final FileReader fr = new FileReader(getFile(resource, SortBenchmarkHelper.class));
        for (Object line : new BufferedReader(fr).lines().toArray()) words.addAll(getStrings.apply((String) line));
        words = words.stream().distinct().filter(new Predicate<>() {
            private static final int MINIMUM_LENGTH = 2;

            public boolean test(String s) {
                return s.length() >= MINIMUM_LENGTH;
            }
        }).collect(Collectors.toList());
        logger.info("Testing with words: " + formatWhole(words.size()) + " from " + resource);
        String[] result = new String[words.size()];
        result = words.toArray(result);
        return result;
    }

    /**
     * CONSIDER making this more efficient. It takes uses a lot of time!
     *
     * @param pattern the (regex) pattern to match.
     * @param line    the line to be matched.
     * @return a Collection of String.
     */
    static Collection<String> getWords(Pattern pattern, String line) {
        final Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            final String[] strings = PATTERN.split(matcher.group(1));
            return Arrays.asList(strings);
        } else
            return new ArrayList<>();
    }

    /**
     * Logs a normalized time value along with a specified prefix.
     * The time is first normalized using the provided normalizer function,
     * and then logged at the INFO level.
     *
     * @param time       the time value to be normalized and logged
     * @param prefix     the prefix to be added before the normalized time in the log message
     * @param normalizer a function that takes a {@code Double} time value and returns its normalized value
     */
    static void logNormalizedTime(double time, String prefix, Function<Double, Double> normalizer) {
        logger.info(prefix + normalizer.apply(time));
    }

    /**
     * Generates an array of random strings by selecting elements from a given lookup array.
     *
     * @param lookupArray the array of strings to randomly select elements from; must not be empty
     * @param number      the number of random strings to generate
     * @return an array containing the randomly generated strings
     * @throws SortException if the lookupArray is empty
     */
    // TEST
    static String[] generateRandomStringArray(String[] lookupArray, int number) {
        if (lookupArray.length == 0) throw new SortException("lookupArray is empty");
        Random r = new Random();
        String[] result = new String[number];
        for (int i = 0; i < number; i++) result[i] = getRandomElement(lookupArray, r);
        return result;
    }

    /**
     * Retrieves the file path of a resource from the class loader of the specified class.
     *
     * @param resource the name of the resource file to locate
     * @param clazz    the class whose class loader is used to locate the resource
     * @return the file path of the located resource
     * @throws FileNotFoundException if the specified resource cannot be found
     */
    // TEST
    private static String getFile(String resource, @SuppressWarnings("SameParameterValue") Class<?> clazz) throws FileNotFoundException {
        final URL url = clazz.getClassLoader().getResource(resource);
        if (url != null) return url.getFile();
        throw new FileNotFoundException(resource + " in " + clazz);
    }

    /**
     * Returns a random element from the provided array of strings.
     *
     * @param strings the array of strings from which a random element is to be selected
     * @param length the number of elements in the array to consider for selection
     * @param r the Random instance to use for generating a random index
     * @return a random string from the provided array within the specified length
     */
    private static String getRandomElement(String[] strings, int length, Random r) {
        return strings[r.nextInt(length)];
    }

    /**
     * Selects a random element from the given array of strings.
     *
     * @param strings the array of strings from which a random element is to be chosen
     * @param r       the instance of Random used to generate the random index
     * @return a randomly selected string from the provided array
     */
    private static String getRandomElement(String[] strings, Random r) {
        return getRandomElement(strings, strings.length, r);
    }

    /**
     * Utility class that provides helper methods specifically designed for benchmarking sorting algorithms and related tasks.
     * This class is implemented as a singleton and cannot be instantiated directly, ensuring its sole purpose is to provide utility methods.
     */
    // NOTE private constructor (singleton pattern)
    private SortBenchmarkHelper() {
    }

    final static LazyLogger logger = new LazyLogger(SortBenchmarkHelper.class);
    private static final Pattern PATTERN = Pattern.compile("[\\s\\p{Punct}\\uFF0C]");
    public final static Pattern regexLeipzig = Pattern.compile("[~\\t]*\\t(([\\s\\p{Punct}\\uFF0C]*\\p{L}+)*)");


}