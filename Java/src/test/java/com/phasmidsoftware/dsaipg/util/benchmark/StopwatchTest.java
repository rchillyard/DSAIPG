package com.phasmidsoftware.dsaipg.util.benchmark;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class StopwatchTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void lap1() {
        final Random random = new Random();
        @SuppressWarnings("MismatchedReadAndWriteOfArray") int[] xs = new int[10000];
        for (int i = 0; i < xs.length; i++) xs[i] = random.nextInt();
        try (Stopwatch target = new Stopwatch()) {
            Arrays.sort(xs);
            System.out.println(target.lap());
        }
    }

    @Test
    public void lap1a() {
        try (Stopwatch target = new Stopwatch()) {
            Thread.sleep(10);
            long lap = target.lap();
            assertEquals(10, lap, 6);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void lap1b() {
        try (Stopwatch target = new Stopwatch("microseconds")) {
            Thread.sleep(10);
            long lap = target.lap();
            assertEquals(10000, lap, 2750);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void lap1c() {
        try (Stopwatch target = new Stopwatch("nanoseconds")) {
            Thread.sleep(10);
            long lap = target.lap();
            assertEquals(10000000, lap, 3500000);
        } catch (InterruptedException ignored) {
        }
    }

    @Test // Slow!
    public void lap1d() {
        try (Stopwatch target = new Stopwatch("seconds")) {
            Thread.sleep(2000);
            long lap = target.lap();
            assertEquals(2, lap, 0.15);
        } catch (InterruptedException ignored) {
        }
    }

    @Test(expected = Throwable.class)
    public void lap2() {
        try (Stopwatch target = new Stopwatch()) {
            target.close();
            Thread.sleep(10);
            System.out.println(target.lap());
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void close() {
        try {
            Stopwatch target = new Stopwatch();
            target.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}