package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.index.AbstractHollowIndexBenchmark;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class HollowPrimaryKeyIndexBenchmark extends AbstractHollowIndexBenchmark<HollowPrimaryKeyIndex> {
    @Override
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    public HollowPrimaryKeyIndex createIndex() {
        return new HollowPrimaryKeyIndex(readStateEngine, IntType.class.getSimpleName(), matchFields);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int getMatchingOrdinal() {
        return index.getMatchingOrdinal(nextKeys());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int getMatchingOrdinalMissing() {
        return index.getMatchingOrdinal(missingKeys());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HollowPrimaryKeyIndexBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(1)
                .measurementTime(TimeValue.seconds(3))
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
