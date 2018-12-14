package com.netflix.hollow.core.index;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class HollowHashIndexBenchmark {
    public static class BuildHollowHashIndexBenchmark extends AbstractHollowHashIndexBenchmark {
        @Override
        protected boolean shouldCreateIndexes() {
            return false;
        }

        @Benchmark
        public HollowHashIndex buildIndex() {
            return createIndex();
        }
    }

    public static class LoadHollowHashIndexBenchmark extends AbstractHollowHashIndexBenchmark {
        @Benchmark
        public HollowHashIndexResult findMatches() {
            return nextIndex().findMatches(nextKeys());
        }

        @Benchmark
        public HollowHashIndexResult findMatchesMissing() {
            return nextIndex().findMatches(missingKeys());
        }
    }

    public static class AbstractHollowHashIndexBenchmark extends AbstractHollowIndexBenchmark<HollowHashIndex> {
        //@Param( {"1", "1000", "10000", "100000"})
        @Param( {"1000"})
        public int cardinality;

        @Override
        protected int cardinality() {
            return cardinality;
        }

        @Override
        public HollowHashIndex createIndex() {
            return new HollowHashIndex(readStateEngine, IntType.class.getSimpleName(), "", matchFields);
        }
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