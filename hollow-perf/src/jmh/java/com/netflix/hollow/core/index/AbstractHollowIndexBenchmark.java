package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
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

/**
 * Abstract benchmark class for Hollow indexes. Uses integer keys to avoid mixing in the overhead of individual hash functions.
 */

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public abstract class AbstractHollowIndexBenchmark<T> {
    private static final int TARGET_SIZE_MB = 30 * 1024 * 1024;
    private static final int ENTRY_OVERHEAD_BYTES = 16;
    private HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
    private HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
    private ThreadLocalRandom random = ThreadLocalRandom.current();
    private T[] indexes;
    protected HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
    protected String[] matchFields;

    //@Param( {"1", "10", "100", "1000", "10000", "100000", "1000000"})
    @Param({"1000"})
    public int size;

    //@Param( {"1", "2", "3", "5", "8"})
    @Param({"1"})
    public int querySize = 1;

    //@Param( {"false", "true"})
    @Param({"false"})
    public boolean nested = false;

    @SuppressWarnings("unchecked")
    @Setup
    public void setup() throws IOException {
        LogManager.getLogManager().reset();
        for(int i = 0; i < size; i++) {
            // Make field values unique for a given object, otherwise index build performance worsens significantly
            int key = getKey(8 * i);
            mapper.add(new IntType(key, new NestedIntType(key)));
        }
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        matchFields = new String[querySize];
        for(int i = 0; i < querySize; i++) {
            int fieldNum = i + 1;
            String fieldName = "field" + fieldNum;
            matchFields[i] = nested ? "nested." + fieldName : fieldName;
        }
        if(!shouldCreateIndexes()) {
            return;
        }
        int length = TARGET_SIZE_MB / (ENTRY_OVERHEAD_BYTES * size);
        T index = createIndex();
        indexes = (T[]) Array.newInstance(index.getClass(), length);
        for(int i = 0; i < indexes.length; i++) {
            indexes[i] = createIndex();
        }
    }

    protected boolean shouldCreateIndexes() {
        return true;
    }

    protected T nextIndex() {
        return indexes[random.nextInt(indexes.length)];
    }

    protected Object[] nextKeys() {
        int key = getKey(random.nextInt(size));
        Integer[] keys = new Integer[querySize];
        for(int i = 0; i < querySize; i++) {
            keys[i] = key + i;
        }
        return keys;
    }

    protected Object[] missingKeys() {
        Integer[] keys = new Integer[querySize];
        for(int i = 0; i < querySize; i++) {
            keys[i] = -1;
        }
        return keys;
    }

    protected abstract T createIndex();

    protected int cardinality() {
        return 1;
    }

    protected int getKey(int key) {
        int cardinality = cardinality();
        return key - key % cardinality;
    }

    protected static class IntType {
        private int field1;
        private int field2;
        private int field3;
        private int field4;
        private int field5;
        private int field6;
        private int field7;
        private int field8;
        private NestedIntType nested;

        public IntType(int i, NestedIntType nested) {
            this.field1 = i;
            this.field2 = i + 1;
            this.field3 = i + 2;
            this.field4 = i + 3;
            this.field5 = i + 4;
            this.field6 = i + 5;
            this.field7 = i + 6;
            this.field8 = i + 7;
            this.nested = nested;
        }
    }

    private static class NestedIntType {
        private int field1;
        private int field2;
        private int field3;
        private int field4;
        private int field5;
        private int field6;
        private int field7;
        private int field8;

        public NestedIntType(int i) {
            this.field1 = i;
            this.field2 = i + 1;
            this.field3 = i + 2;
            this.field4 = i + 3;
            this.field5 = i + 4;
            this.field6 = i + 5;
            this.field7 = i + 6;
            this.field8 = i + 7;
        }
    }
}
