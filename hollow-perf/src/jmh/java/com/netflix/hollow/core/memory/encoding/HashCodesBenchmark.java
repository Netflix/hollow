package com.netflix.hollow.core.memory.encoding;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class HashCodesBenchmark {
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @Param( {"1", "10", "1000"})
    int length;

    byte[] charData;
    byte[] multibyteCharData;
    int intKey;
    long longKey;
    String stringKey;
    String multibyteStringKey;

    @Setup
    public void setup() {
        charData = new byte[length];
        multibyteCharData = new byte[length];
        for (int i = 0; i < length; i++) {
            charData[i] = (byte) random.nextInt(0x80);
            multibyteCharData[i] = (byte) random.nextInt(Character.MAX_VALUE);
        }
        stringKey = new String(charData);
        multibyteStringKey = new String(multibyteCharData);
    }

    @Benchmark
    public int hashInt() {
        return HashCodes.hashInt(intKey);
    }

    @Benchmark
    public int hashLong() {
        return HashCodes.hashLong(longKey);
    }

    @Benchmark
    public int hashString() {
        return HashCodes.hashCode(stringKey);
    }

    @Benchmark
    public int hashStringMultibyte() {
        return HashCodes.hashCode(multibyteStringKey);
    }

    @Benchmark
    public int hashBytes() {
        return HashCodes.hashCode(charData);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashCodesBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(1)
                .measurementTime(TimeValue.seconds(3))
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}