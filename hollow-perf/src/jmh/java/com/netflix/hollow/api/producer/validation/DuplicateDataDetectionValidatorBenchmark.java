package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
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
 * Benchmarks for the DuplicateDataDetectionValidator and the full producer cycle
 * with large datasets. Measures:
 * <ul>
 *   <li>{@code validatorOnly} - just the validator's onValidate call against a pre-built read state</li>
 *   <li>{@code fullCycleWithValidator} - full runCycle including populate, publish, integrity check, validate, announce</li>
 *   <li>{@code fullCycleWithoutValidator} - full runCycle without the duplicate validator, as a baseline</li>
 *   <li>{@code getDuplicateKeysBounded} - getDuplicateKeys(100) on a pre-built index (optimized)</li>
 *   <li>{@code getDuplicateKeysUnbounded} - getDuplicateKeys() on a pre-built index (old behavior)</li>
 * </ul>
 */
public class DuplicateDataDetectionValidatorBenchmark {

    @State(Scope.Benchmark)
    public static class CycleState {
        @Param({"1000000", "5000000", "10000000"})
        public int numRecords;

        private InMemoryBlobStore blobStore;

        @Setup(Level.Iteration)
        public void setUp() {
            blobStore = new InMemoryBlobStore();
        }
    }

    @State(Scope.Benchmark)
    public static class ValidatorState {
        @Param({"1000000", "5000000", "10000000"})
        public int numRecords;

        private DuplicateDataDetectionValidator validator;
        private HollowProducer.ReadState readState;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
            HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
            for (int i = 0; i < numRecords; i++) {
                mapper.add(new Movie(i, "Title" + i, i % 100));
            }

            HollowReadStateEngine readEngine = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);

            validator = new DuplicateDataDetectionValidator(Movie.class);
            readState = new HollowProducer.ReadState() {
                @Override public long getVersion() { return 1L; }
                @Override public HollowReadStateEngine getStateEngine() { return readEngine; }
            };
        }
    }

    @State(Scope.Benchmark)
    public static class IndexState {
        @Param({"1000000", "5000000", "10000000"})
        public int numRecords;

        @Param({"false", "true"})
        public boolean withDuplicates;

        private HollowPrimaryKeyIndex index;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
            HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
            for (int i = 0; i < numRecords; i++) {
                // When withDuplicates=true, use id = i % 1000 so there are many
                // duplicate keys (each key has ~numRecords/1000 records)
                int id = withDuplicates ? (i % 1000) : i;
                mapper.add(new Movie(id, "Title" + i, i % 100));
            }

            HollowReadStateEngine readEngine = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);

            index = new HollowPrimaryKeyIndex(readEngine, "Movie", "id");
        }
    }

    @State(Scope.Benchmark)
    public static class DeltaIndexState {
        @Param({"1000000", "5000000", "10000000"})
        public int numRecords;

        @Param({"0.001", "0.01", "0.1"})
        public double deltaFraction;

        private HollowPrimaryKeyIndex index;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
            HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
            for (int i = 0; i < numRecords; i++) {
                mapper.add(new Movie(i, "Title" + i, i % 100));
            }

            // First cycle: snapshot
            HollowReadStateEngine readEngine = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(writeEngine, readEngine);

            // Second cycle: delta with a fraction of records changed
            writeEngine.prepareForNextCycle();
            int deltaSize = Math.max(1, (int) (numRecords * deltaFraction));
            for (int i = 0; i < numRecords; i++) {
                // Change title for the first deltaSize records to produce a delta
                String title = (i < deltaSize) ? "Updated" + i : "Title" + i;
                mapper.add(new Movie(i, title, i % 100));
            }
            StateEngineRoundTripper.roundTripDelta(writeEngine, readEngine);

            // Build index on the post-delta state (previousOrdinals is now populated)
            index = new HollowPrimaryKeyIndex(readEngine, "Movie", "id");
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    @Fork(1)
    public long fullCycleWithValidator(CycleState state) {
        HollowProducer producer = HollowProducer.withPublisher(state.blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new DuplicateDataDetectionValidator(Movie.class))
                .build();

        return producer.runCycle(writeState -> {
            for (int i = 0; i < state.numRecords; i++) {
                writeState.add(new Movie(i, "Title" + i, i % 100));
            }
        });
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    @Fork(1)
    public long fullCycleWithoutValidator(CycleState state) {
        HollowProducer producer = HollowProducer.withPublisher(state.blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.initializeDataModel(Movie.class);

        return producer.runCycle(writeState -> {
            for (int i = 0; i < state.numRecords; i++) {
                writeState.add(new Movie(i, "Title" + i, i % 100));
            }
        });
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public ValidationResult validatorOnly(ValidatorState state) {
        return state.validator.onValidate(state.readState);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public Collection<?> getDuplicateKeysBounded(IndexState state) {
        return state.index.getDuplicateKeys(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public Collection<?> getDuplicateKeysUnbounded(IndexState state) {
        return state.index.getDuplicateKeys();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 3)
    @Measurement(iterations = 5, time = 3)
    @Fork(1)
    public Collection<?> getDuplicateKeysDelta(DeltaIndexState state) {
        return state.index.getDuplicateKeys(100);
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
