package com.phasmidsoftware.dsaipg.util.benchmark;


import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.phasmidsoftware.dsaipg.util.general.Utilities.formatWhole;


/**
 * A utility class designed to provide helper methods for benchmarking and testing sorting algorithms.
 * This class includes methods for generating test data,
 * extracting words from text resources, and logging utility results.
 * It is structured with static methods and a private constructor to ensure it operates as a singleton helper.
 */
public class SortBenchmarkHelper {
    /**
     * A static, lazily initialized logger instance used for logging messages within the {@code SortBenchmarkHelper} class.
     * This logger facilitates debug, informational, and error messages that help track the behavior of the utility methods.
     */
    final static LazyLogger logger = new LazyLogger(SortBenchmarkHelper.class);

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
    public static String[] getWords(final String resource, Function<String, Collection<String>> getStrings) throws FileNotFoundException {
        // getResourceAsStream resolves a name WITHOUT a leading slash relative to this class's
        // package — com/phasmidsoftware/dsaipg/util/benchmark — rather than the classpath root,
        // where these files actually live. Normalising here rather than at each call site is
        // deliberate: when this method moved from FileReader to getResourceAsStream, the slash was
        // added at one of six call sites and the other five were left throwing the very
        // FileNotFoundException the change was made to fix.
        final String absolute = resource.startsWith("/") ? resource : "/" + resource;
        // The missing-resource check is deliberately OUTSIDE the try. FileNotFoundException
        // extends IOException, so throwing it inside would be caught by the handler below and
        // rewrapped as a RuntimeException — making this method's declared `throws
        // FileNotFoundException` unreachable, and the `catch (FileNotFoundException)` blocks in
        // SortBenchmark, BSTBenchmark and SymbolTableBenchmark dead code.
        final InputStream inputStream = SortBenchmarkHelper.class.getResourceAsStream(absolute);
        if (inputStream == null) throw new FileNotFoundException(resource);
        try (inputStream) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            List<String> words = new ArrayList<>();
            for (Object line : bufferedReader.lines().toArray()) words.addAll(getStrings.apply((String) line));
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    // `getFile` was removed here. It returned `url.getFile()`, which does NOT URL-decode, so a
    // space anywhere in the path arrived as "%20" and the file was not found — the bug this
    // family of changes exists to fix. Nothing calls it now that getWords reads the stream
    // directly, which also works inside a jar, where there is no file to name.

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

    private static final Pattern PATTERN = Pattern.compile("[\\s\\p{Punct}\\uFF0C]");
    public final static Pattern regexLeipzig = Pattern.compile("[~\\t]*\\t(([\\s\\p{Punct}\\uFF0C]*\\p{L}+)*)");


}
