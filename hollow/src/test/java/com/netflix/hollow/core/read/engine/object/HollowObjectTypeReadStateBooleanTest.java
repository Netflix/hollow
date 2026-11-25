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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.FixedLengthData;
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
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class HollowObjectTypeReadStateBooleanTest {

    private static final String TYPE = "BooleanRecord";
    private static final String[] BOOLEAN_FIELDS = { "first", "second", "third" };
    private static final int RECORDS = 64;

    @Parameterized.Parameters(name = "{0}, recycling={1}, shards={2}, fastPaths={3}")
    public static Collection<Object[]> parameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        for(MemoryMode mode : new MemoryMode[] { MemoryMode.ON_HEAP, MemoryMode.SHARED_MEMORY_LAZY }) {
            for(boolean recycling : new boolean[] { false, true }) {
                for(int shards : new int[] { 1, 8 }) {
                    for(boolean fastPaths : new boolean[] { false, true })
                        parameters.add(new Object[] { mode, recycling, shards, fastPaths });
                }
            }
        }
        return parameters;
    }

    private final MemoryMode memoryMode;
    private final boolean recycling;
    private final int shards;
    private final boolean fastPaths;

    public HollowObjectTypeReadStateBooleanTest(MemoryMode memoryMode, boolean recycling, int shards, boolean fastPaths) {
        this.memoryMode = memoryMode;
        this.recycling = recycling;
        this.shards = shards;
        this.fastPaths = fastPaths;
    }

    @Test
    public void readsBooleanFieldsAcrossPackedBoundaries() throws Exception {
        HollowWriteStateEngine writer = newWriter();
        Map<Integer, Boolean[]> values = addRecords(writer, 0, false);
        HollowReadStateEngine reader = readSnapshot(writer);
        HollowObjectTypeReadState state = (HollowObjectTypeReadState)reader.getTypeState(TYPE);
        assertEquals(shards, state.numShards());
        verify(state, values);

        if(shards == 1) {
            // Seven id bits, 64 long bits and three two-bit Booleans give an odd record width.
            // The dataset visits every bit offset, including a Boolean straddling a word boundary.
            HollowObjectTypeReadStateShard shard = state.shardsVolatile.shards[0];
            for(String field : BOOLEAN_FIELDS) {
                int fieldIndex = state.getSchema().getPosition(field);
                BitSet offsets = new BitSet(64);
                for(int ordinal : values.keySet())
                    offsets.set((int)(shard.fieldOffset(ordinal, fieldIndex) & 63));
                assertEquals(field, 64, offsets.cardinality());
            }
        }
    }

    @Test
    public void selectsFixedWidthReadsOnlyWhenOptedIn() throws Exception {
        HollowWriteStateEngine writer = newWriter();
        addRecords(writer, 0, false);
        HollowObjectTypeReadState state = (HollowObjectTypeReadState)readSnapshot(writer).getTypeState(TYPE);
        HollowObjectTypeReadStateShard shard = state.shardsVolatile.shards[0];
        FixedLengthData tracking = mock(FixedLengthData.class, delegatesTo(shard.dataElements.fixedLengthData));
        shard.dataElements.fixedLengthData = tracking;
        state.readBoolean(0, state.getSchema().getPosition("first"));
        org.mockito.Mockito.verify(tracking, times(fastPaths ? 1 : 0)).getElementValue(anyLong(), eq(2), eq(3L));
        org.mockito.Mockito.verify(tracking, times(fastPaths ? 0 : 1)).getElementValue(anyLong(), anyInt());
    }

    @Test
    public void readsAllNullBooleanFields() throws Exception {
        HollowWriteStateEngine writer = newWriter();
        Map<Integer, Boolean[]> values = addRecords(writer, 0, true);
        verify((HollowObjectTypeReadState)readSnapshot(writer).getTypeState(TYPE), values);
    }

    @Test
    public void readsBooleanFieldsAfterDeltaUpdates() throws Exception {
        // Shared-memory consumers do not apply deltas.
        assumeTrue(memoryMode == MemoryMode.ON_HEAP);
        HollowWriteStateEngine writer = newWriter();
        Map<Integer, Boolean[]> values = addRecords(writer, 0, false);
        HollowReadStateEngine reader = readSnapshot(writer);
        verify((HollowObjectTypeReadState)reader.getTypeState(TYPE), values);
        for(int cycle = 1; cycle <= 3; cycle++) {
            values = addRecords(writer, cycle, false);
            StateEngineRoundTripper.roundTripDelta(writer, reader);
            verify((HollowObjectTypeReadState)reader.getTypeState(TYPE), values);
        }
    }

    @Test
    public void readsBooleanFieldsAfterSplittingAndJoining() throws Exception {
        // Resharding constructs writable storage and is only supported on heap.
        assumeTrue(memoryMode == MemoryMode.ON_HEAP);
        HollowWriteStateEngine writer = newWriter();
        Map<Integer, Boolean[]> values = addRecords(writer, 0, false);
        HollowObjectTypeReadState state = (HollowObjectTypeReadState)readSnapshot(writer).getTypeState(TYPE);
        for(int shardCount : new int[] { 16, 1, 8 }) {
            HollowTypeReshardingStrategy.getInstance(state).reshard(state, state.numShards(), shardCount);
            assertEquals(shardCount, state.numShards());
            verify(state, values);
        }
    }

    private HollowWriteStateEngine newWriter() {
        HollowObjectSchema schema = new HollowObjectSchema(TYPE, 5);
        schema.addField("id", FieldType.INT);
        schema.addField("first", FieldType.BOOLEAN);
        schema.addField("wide", FieldType.LONG);
        schema.addField("second", FieldType.BOOLEAN);
        schema.addField("third", FieldType.BOOLEAN);
        HollowWriteStateEngine writer = new HollowWriteStateEngine();
        writer.addTypeState(new HollowObjectTypeWriteState(schema, shards));
        return writer;
    }

    private static Map<Integer, Boolean[]> addRecords(HollowWriteStateEngine writer, int cycle, boolean allNull) {
        HollowObjectSchema schema = (HollowObjectSchema)writer.getSchema(TYPE);
        HollowObjectWriteRecord record = new HollowObjectWriteRecord(schema);
        Map<Integer, Boolean[]> values = new LinkedHashMap<>();
        for(int id = 0; id < RECORDS; id++) {
            record.reset();
            record.setInt("id", id);
            record.setLong("wide", Long.MAX_VALUE);
            Boolean[] booleans = new Boolean[BOOLEAN_FIELDS.length];
            for(int field = 0; field < BOOLEAN_FIELDS.length; field++) {
                // Keep half the records unchanged across deltas and update the rest.
                int value = (id + field + ((id & 1) == 0 ? cycle : 0)) % 3;
                booleans[field] = allNull || value == 0 ? null : value == 1;
                if(booleans[field] != null)
                    record.setBoolean(BOOLEAN_FIELDS[field], booleans[field]);
            }
            values.put(writer.add(TYPE, record), booleans);
        }
        return values;
    }

    private HollowReadStateEngine readSnapshot(HollowWriteStateEngine writer) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new HollowBlobWriter(writer).writeSnapshot(output);
        writer.prepareForNextCycle();
        HollowReadStateEngine reader = new HollowReadStateEngine(new HollowReadConfiguration(memoryMode, recycling
                ? new RecyclingRecycler() : WastefulRecycler.DEFAULT_INSTANCE, fastPaths
                ? new ExperimentalFeature[] { ExperimentalFeature.SHARD_READ_FAST_PATHS } : new ExperimentalFeature[0]));
        HollowBlobReader blobReader = new HollowBlobReader(reader, memoryMode);
        if(memoryMode == MemoryMode.SHARED_MEMORY_LAZY) {
            File snapshot = File.createTempFile("hollow-boolean-snapshot", ".bin");
            snapshot.deleteOnExit();
            try(FileOutputStream fileOutput = new FileOutputStream(snapshot)) {
                fileOutput.write(output.toByteArray());
            }
            try(HollowBlobInput input = HollowBlobInput.randomAccess(snapshot, 16)) {
                blobReader.readSnapshot(input);
            }
        } else {
            try(HollowBlobInput input = HollowBlobInput.serial(output.toByteArray())) {
                blobReader.readSnapshot(input);
            }
        }
        return reader;
    }

    private void verify(HollowObjectTypeReadState state, Map<Integer, Boolean[]> values) {
        assertEquals(RECORDS, values.size());
        assertEquals(RECORDS, state.getPopulatedOrdinals().cardinality());
        for(HollowObjectTypeReadStateShard shard : state.shardsVolatile.shards) {
            if(memoryMode == MemoryMode.SHARED_MEMORY_LAZY) {
                assertTrue(shard.dataElements.fixedLengthData instanceof EncodedLongBuffer);
            } else if(recycling || !fastPaths) {
                assertTrue(shard.dataElements.fixedLengthData instanceof FixedLengthElementArray);
            } else {
                assertTrue(shard.dataElements.fixedLengthData instanceof ContiguousFixedLengthData);
            }
            for(String field : BOOLEAN_FIELDS) {
                int fieldIndex = state.getSchema().getPosition(field);
                assertEquals(2, shard.dataElements.bitsPerField[fieldIndex]);
                assertEquals(3L, shard.dataElements.nullValueForField[fieldIndex]);
            }
        }
        for(Map.Entry<Integer, Boolean[]> entry : values.entrySet()) {
            int ordinal = entry.getKey();
            assertEquals(Long.MAX_VALUE, state.readLong(ordinal, state.getSchema().getPosition("wide")));
            for(int field = 0; field < BOOLEAN_FIELDS.length; field++) {
                int fieldIndex = state.getSchema().getPosition(BOOLEAN_FIELDS[field]);
                Boolean expected = entry.getValue()[field];
                assertEquals("ordinal=" + ordinal + " field=" + BOOLEAN_FIELDS[field],
                        expected, state.readBoolean(ordinal, fieldIndex));
                assertEquals(expected == null, state.isNull(ordinal, fieldIndex));
            }
        }
    }
}
