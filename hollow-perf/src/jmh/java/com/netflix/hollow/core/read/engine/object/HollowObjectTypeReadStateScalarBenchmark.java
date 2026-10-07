/*
 *  Copyright 2026 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.ContiguousFixedLengthData;
import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.ExperimentalFeature;
import com.netflix.hollow.core.read.engine.HollowReadConfiguration;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/** Random scalar reads from a mixed-field object, normalized per read. */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 4, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
public class HollowObjectTypeReadStateScalarBenchmark {

    private static final int READS = 65536;
    private static final int SMALL_LONG_FIELD = 4;
    private static final int LARGE_LONG_FIELD = 5;
    private static final int BOOLEAN_FIELD = 6;

    @Param({ "65536", "1000000" })
    int records;

    @Param({ "contiguous", "segmented", "mapped" })
    String storage;

    @Param({ "1", "8" })
    int shards;

    @Param({ "false", "true" })
    boolean shardReadFastPaths;

    HollowObjectTypeReadState dataAccess;
    int[] readOrder;

    @Setup
    public void setUp() throws Exception {
        HollowObjectSchema targetSchema = new HollowObjectSchema("Target", 1);
        targetSchema.addField("id", FieldType.INT);
        HollowObjectSchema schema = new HollowObjectSchema("Record", 7);
        for(int i = 0; i < 4; i++)
            schema.addField("ref" + i, FieldType.REFERENCE, "Target");
        schema.addField("small", FieldType.LONG);
        schema.addField("large", FieldType.LONG);
        schema.addField("flag", FieldType.BOOLEAN);

        HollowWriteStateEngine writer = new HollowWriteStateEngine();
        writer.addTypeState(new HollowObjectTypeWriteState(targetSchema));
        writer.addTypeState(new HollowObjectTypeWriteState(schema, shards));
        HollowObjectWriteRecord target = new HollowObjectWriteRecord(targetSchema);
        for(int i = 0; i < records; i++) {
            target.reset();
            target.setInt("id", i);
            if(writer.add("Target", target) != i)
                throw new IllegalStateException("Unexpected target ordinal");
        }

        Random random = new Random(42);
        HollowObjectWriteRecord record = new HollowObjectWriteRecord(schema);
        for(int i = 0; i < records; i++) {
            record.reset();
            for(int j = 0; j < 4; j++) {
                if((i & 31) != 0)
                    record.setReference("ref" + j, (i * (j + 1)) % records);
            }
            // Pin the maxima to exercise the 40-bit single-load and 62-bit wide paths.
            record.setLong("small", i == 0 ? (1L << 39) - 1 : random.nextLong() & ((1L << 39) - 1));
            record.setLong("large", i == 0 ? (1L << 61) - 1 : random.nextLong() & ((1L << 61) - 1));
            if((i & 31) != 0)
                record.setBoolean("flag", (i & 1) == 0);
            if(writer.add("Record", record) != i)
                throw new IllegalStateException("Unexpected record ordinal");
        }

        HollowReadStateEngine reader;
        if("segmented".equals(storage)) {
            reader = new HollowReadStateEngine(new HollowReadConfiguration(MemoryMode.ON_HEAP,
                    new RecyclingRecycler(), features()));
        } else if("contiguous".equals(storage) || "mapped".equals(storage)) {
            reader = new HollowReadStateEngine(new HollowReadConfiguration("mapped".equals(storage)
                    ? MemoryMode.SHARED_MEMORY_LAZY : MemoryMode.ON_HEAP,
                    WastefulRecycler.DEFAULT_INSTANCE, features()));
        } else {
            throw new IllegalArgumentException("Unknown storage: " + storage);
        }
        if("mapped".equals(storage)) {
            File snapshot = File.createTempFile("hollow-scalar-reads", ".bin");
            snapshot.deleteOnExit();
            try(FileOutputStream output = new FileOutputStream(snapshot)) {
                new HollowBlobWriter(writer).writeSnapshot(output);
            }
            try(HollowBlobInput input = HollowBlobInput.randomAccess(snapshot)) {
                new HollowBlobReader(reader, MemoryMode.SHARED_MEMORY_LAZY).readSnapshot(input);
            }
        } else {
            StateEngineRoundTripper.roundTripSnapshot(writer, reader);
        }
        dataAccess = (HollowObjectTypeReadState)reader.getTypeState("Record");
        verifyStorage();

        readOrder = new int[READS];
        for(int i = 0; i < READS; i++)
            readOrder[i] = random.nextInt(records);
    }

    private ExperimentalFeature[] features() {
        return shardReadFastPaths ? new ExperimentalFeature[] { ExperimentalFeature.SHARD_READ_FAST_PATHS } : new ExperimentalFeature[0];
    }

    private void verifyStorage() {
        HollowObjectTypeReadStateShard[] actualShards = dataAccess.shardsVolatile.shards;
        if(actualShards.length != shards)
            throw new IllegalStateException("Unexpected shard count");
        for(HollowObjectTypeReadStateShard shard : actualShards) {
            Object data = shard.dataElements.fixedLengthData;
            boolean expectedStorage = "mapped".equals(storage) ? data instanceof EncodedLongBuffer
                    : "segmented".equals(storage) || !shardReadFastPaths ? data instanceof FixedLengthElementArray
                    : data instanceof ContiguousFixedLengthData;
            if(!expectedStorage || shard.dataElements.bitsPerField[SMALL_LONG_FIELD] > 56
                    || shard.dataElements.bitsPerField[LARGE_LONG_FIELD] <= 56
                    || shard.dataElements.bitsPerField[BOOLEAN_FIELD] != 2)
                throw new IllegalStateException("Unexpected storage or field width");
        }
    }

    @Benchmark
    @OperationsPerInvocation(READS)
    public int readBoolean() {
        int count = 0;
        HollowObjectTypeReadState data = dataAccess;
        for(int ordinal : readOrder)
            count += Boolean.TRUE.equals(data.readBoolean(ordinal, BOOLEAN_FIELD)) ? 1 : 0;
        return count;
    }

    @Benchmark
    @OperationsPerInvocation(READS)
    public long readOrdinal() {
        long sum = 0;
        HollowObjectTypeReadState data = dataAccess;
        for(int ordinal : readOrder)
            sum += data.readOrdinal(ordinal, 0);
        return sum;
    }

    @Benchmark
    @OperationsPerInvocation(READS)
    public long readSmallLong() {
        long sum = 0;
        HollowObjectTypeReadState data = dataAccess;
        for(int ordinal : readOrder)
            sum += data.readLong(ordinal, SMALL_LONG_FIELD);
        return sum;
    }

    @Benchmark
    @OperationsPerInvocation(READS)
    public long readLargeLong() {
        long sum = 0;
        HollowObjectTypeReadState data = dataAccess;
        for(int ordinal : readOrder)
            sum += data.readLong(ordinal, LARGE_LONG_FIELD);
        return sum;
    }
}
