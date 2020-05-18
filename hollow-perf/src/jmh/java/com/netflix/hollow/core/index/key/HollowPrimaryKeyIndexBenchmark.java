package com.netflix.hollow.core.index.key;

import com.netflix.hollow.core.index.AbstractHollowIndexBenchmark;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class HollowPrimaryKeyIndexBenchmark {
    public static class BuildHollowPrimaryKeyIndexBenchmark extends AbstractHollowPrimaryKeyIndexBenchmark {
        @Override
        protected boolean shouldCreateIndexes() {
            return false;
        }

        @Benchmark
        public HollowPrimaryKeyIndex buildIndex() {
            return createIndex();
        }
    }

    public static class LoadHollowPrimaryKeyIndexBenchmark extends AbstractHollowPrimaryKeyIndexBenchmark {
        @Benchmark
        public int getMatchingOrdinal() {
            return nextIndex().getMatchingOrdinal(nextKeys());
        }

        @Benchmark
        public int getMatchingOrdinalMissing() {
            return nextIndex().getMatchingOrdinal(missingKeys());
        }
    }

    public static class AbstractHollowPrimaryKeyIndexBenchmark
            extends AbstractHollowIndexBenchmark<HollowPrimaryKeyIndex> {
        @Override
        public HollowPrimaryKeyIndex createIndex() {
            return new HollowPrimaryKeyIndex(readStateEngine, IntType.class.getSimpleName(), matchFields);
        }
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