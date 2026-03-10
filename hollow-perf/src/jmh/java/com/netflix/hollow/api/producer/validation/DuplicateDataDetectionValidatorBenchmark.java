package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
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

/**
 * Compares three approaches to duplicate key detection:
 * <ul>
 *   <li>{@code snapshotFullScan} — getDuplicateKeys() with no max, full hash table scan (baseline)</li>
 *   <li>{@code snapshotBounded} — getDuplicateKeys(100) on a snapshot, iterates all populated ordinals</li>
 *   <li>{@code deltaBounded} — getDuplicateKeys(100) after a delta, iterates only newly added ordinals</li>
 * </ul>
 *
 * All three use the same numRecords. The snapshot benchmarks are unaffected by deltaFraction
 * (they'll show constant cost across that axis), which makes the delta speedup easy to see.
 */
public class DuplicateDataDetectionValidatorBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"1000000", "5000000"})
        public int numRecords;

        @Param({"0.001", "0.01", "0.1"})
        public double deltaFraction;

        private HollowPrimaryKeyIndex snapshotIndex;
        private HollowPrimaryKeyIndex deltaIndex;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            snapshotIndex = buildSnapshotIndex();
            deltaIndex = buildDeltaIndex();
        }

        private HollowPrimaryKeyIndex buildSnapshotIndex() throws IOException {
            HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
            HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
            for (int i = 0; i < numRecords; i++) {
                mapper.add(new Movie(i, "Title" + i, i % 100));
            }
            HollowReadStateEngine readEngine = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);
            return new HollowPrimaryKeyIndex(readEngine, "Movie", "id");
        }

        private HollowPrimaryKeyIndex buildDeltaIndex() throws IOException {
            HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
            HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
            for (int i = 0; i < numRecords; i++) {
                mapper.add(new Movie(i, "Title" + i, i % 100));
            }

            HollowReadStateEngine readEngine = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);

            writeEngine.prepareForNextCycle();
            int deltaSize = Math.max(1, (int) (numRecords * deltaFraction));
            for (int i = 0; i < numRecords; i++) {
                String title = (i < deltaSize) ? "Updated" + i : "Title" + i;
                mapper.add(new Movie(i, title, i % 100));
            }
            StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

            return new HollowPrimaryKeyIndex(readEngine, "Movie", "id");
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public Collection<?> snapshotFullScan(BenchmarkState state) {
        return state.snapshotIndex.getDuplicateKeys();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public Collection<?> snapshotBounded(BenchmarkState state) {
        return state.snapshotIndex.getDuplicateKeys(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public Collection<?> deltaBounded(BenchmarkState state) {
        return state.deltaIndex.getDuplicateKeys(100);
    }

    @HollowPrimaryKey(fields = {"id"})
    static class Movie {
        int id;
        String title;
        int rating;

        Movie(int id, String title, int rating) {
            this.id = id;
            this.title = title;
            this.rating = rating;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DuplicateDataDetectionValidatorBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
