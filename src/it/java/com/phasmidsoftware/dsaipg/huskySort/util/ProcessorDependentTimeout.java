package com.phasmidsoftware.dsaipg.huskySort.util;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ProcessorDependentTimeout extends org.junit.rules.Timeout {

    /**
     * Create a {@code Timeout} instance with the timeout specified
     * at the timeUnit of granularity of the provided {@code TimeUnit}.
     *
     * @param timeout  the maximum time to allow the test to run
     *                 before it should time out.
     * @param timeUnit the time unit for the {@code timeout}
     * @param config   the configuration (containing the processor details).
     * @since 4.12
     */
    public ProcessorDependentTimeout(final long timeout, final TimeUnit timeUnit, final Config config) {
        super(getFactoredTimeout(timeout, timeUnit, config, MILLISECONDS), MILLISECONDS);
    }

    public static long getFactoredTimeout(final long timeoutGiven, final TimeUnit timeUnit, final Config config, final TimeUnit timeoutRequired) {
        final Map<String, Double> processorSpeeds = getProcessorSpeeds();
        String processor = config.get("tests", "processor");
        final Double processorSpeed = processorSpeeds.getOrDefault(processor, 1.0);
        final long result = Math.round(timeoutRequired.convert(timeoutGiven, timeUnit) * GRANULARITY / processorSpeed / GRANULARITY);
        logger.info("setting timeout to " + result + " " + timeoutRequired);
        return result;
    }

    private static Map<String, Double> getProcessorSpeeds() {
        final Map<String, Double> map = new HashMap<>();
        map.put("MacBookPro 2.8 GHz Quad Core Intel Core i7", 0.92);
        map.put("MacBookAir 1.6 GH Dual Core Intel Core i5", 0.68);
        map.put("MacBookPro Apple M1", 1.36); // It might be as high as 1.45, but sometimes it slows down.
        map.put("Intel Core i7-8700K", 1.55);
        map.put("Intel Core i7-8550U", 0.71);
        return map;
    }

    private static final Logger logger = new LazyLogger(ProcessorDependentTimeout.class);
    private static final int GRANULARITY = 10000;

}