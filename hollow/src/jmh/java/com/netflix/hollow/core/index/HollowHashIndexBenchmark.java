package com.netflix.hollow.core.index;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class HollowHashIndexBenchmark extends AbstractHollowIndexBenchmark<HollowHashIndex> {
    @Param({"1", "1000", "10000", "100000"})
    public int cardinality;

    @Override
    protected int cardinality() {
        return cardinality;
    }

    @Override
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    public HollowHashIndex createIndex() {
        return new HollowHashIndex(readStateEngine, IntType.class.getSimpleName(), "", matchFields);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public HollowHashIndexResult findMatches() {
        return index.findMatches(nextKeys());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public HollowHashIndexResult findMatchesMissing() {
        return index.findMatches(missingKeys());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HollowHashIndexBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(1)
                .measurementTime(TimeValue.seconds(3))
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
