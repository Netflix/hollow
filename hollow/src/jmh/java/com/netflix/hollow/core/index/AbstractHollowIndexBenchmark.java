package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.LogManager;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Abstract benchmark class for Hollow indexes. Uses integer keys to avoid mixing in the overhead of individual hash functions.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
public abstract class AbstractHollowIndexBenchmark<T> {
    private HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
    private HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    protected HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
    protected T index;
    protected String[] matchFields;

    @Param({"1", "10000", "100000", "1000000", "10000000", "100000000"})
    public int size;

    @Param({"1", "2", "3", "5", "8"})
    public int querySize;

    @Param({"false", "true"})
    public boolean nested;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        LogManager.getLogManager().reset();

        for (int i = 0; i < size; i++) {
            // Make field values unique for a given object, otherwise index build performance worsens significantly
            int key = getKey(8 * i);
            mapper.add(new IntType(key, new NestedIntType(key)));
        }

        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);

        matchFields = new String[querySize];
        for (int i = 0; i < querySize; i++) {
            int fieldNum = i + 1;
            String fieldName = "field" + fieldNum;
            matchFields[i] = nested ? "nested." + fieldName : fieldName;
        }

        index = createIndex();
    }

    protected Object[] nextKeys() {
        int key = getKey(random.nextInt(size));
        Integer[] keys = new Integer[querySize];
        for (int i = 0; i < querySize; i++) {
            keys[i] = key + i;
        }
        return keys;
    }

    protected Object[] missingKeys() {
        Integer[] keys = new Integer[querySize];
        for (int i = 0; i < querySize; i++) {
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
