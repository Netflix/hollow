package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;

import com.netflix.hollow.core.memory.EncodedByteBuffer;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.VariableLengthData;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.ExperimentalFeature;
import com.netflix.hollow.core.read.engine.HollowReadConfiguration;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class HollowObjectTypeReadStateBytesTest {

    @Parameters(name = "{0}, recycling={1}, fastPaths={2}")
    public static Collection<Object[]> memoryModes() {
        return Arrays.asList(new Object[][] {
                { MemoryMode.ON_HEAP, true, false }, { MemoryMode.ON_HEAP, true, true },
                { MemoryMode.ON_HEAP, false, false }, { MemoryMode.ON_HEAP, false, true },
                { MemoryMode.SHARED_MEMORY_LAZY, false, false }, { MemoryMode.SHARED_MEMORY_LAZY, false, true }
        });
    }

    private final MemoryMode memoryMode;
    private final boolean recycling;
    private final boolean fastPaths;

    public HollowObjectTypeReadStateBytesTest(MemoryMode memoryMode, boolean recycling, boolean fastPaths) {
        this.memoryMode = memoryMode;
        this.recycling = recycling;
        this.fastPaths = fastPaths;
    }

    @HollowShardLargeType(numShards = 1)
    public static class BytesHolder {
        byte[] value;

        BytesHolder(byte[] value) {
            this.value = value;
        }
    }

    @Test
    public void readsByteFieldsWithBulkCopies() throws Exception {
        byte[][] values = {
                null,
                new byte[0],
                new byte[] { 1, 2, 3 },
                bytes(100)
        };
        assertRoundTrip(values, true);
    }

    @Test
    public void readsEmptyAndNullByteFieldsWithoutVariableLengthStorage() throws Exception {
        assertRoundTrip(new byte[][] { null, new byte[0] }, false);
    }

    private void assertRoundTrip(byte[][] values, boolean hasVariableLengthStorage) throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(BytesHolder.class);

        int[] ordinals = new int[values.length];
        for(int i = 0; i < values.length; i++)
            ordinals[i] = objectMapper.add(new BytesHolder(values[i]));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeStateEngine).writeSnapshot(baos);
        byte[] snapshot = baos.toByteArray();

        HollowReadStateEngine readStateEngine = memoryMode == MemoryMode.ON_HEAP
                ? readOnHeap(snapshot) : readSharedMemory(snapshot);
        assertReads(readStateEngine, values, ordinals, hasVariableLengthStorage);
    }

    private HollowReadStateEngine readOnHeap(byte[] snapshot) throws Exception {
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(new HollowReadConfiguration(memoryMode, recycling
                ? new RecyclingRecycler() : WastefulRecycler.SMALL_ARRAY_RECYCLER, features()));
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        try(HollowBlobInput in = HollowBlobInput.serial(new ByteArrayInputStream(snapshot))) {
            reader.readSnapshot(in);
        }
        return readStateEngine;
    }

    private ExperimentalFeature[] features() {
        return fastPaths ? new ExperimentalFeature[] { ExperimentalFeature.SHARD_READ_FAST_PATHS } : new ExperimentalFeature[0];
    }

    private HollowReadStateEngine readSharedMemory(byte[] snapshot) throws Exception {
        File blobFile = File.createTempFile("readbytes-shm-snapshot", ".bin");
        blobFile.deleteOnExit();
        try(FileOutputStream fos = new FileOutputStream(blobFile)) {
            fos.write(snapshot);
        }

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(new HollowReadConfiguration(memoryMode,
                new RecyclingRecycler(), features()));
        HollowBlobReader reader = new HollowBlobReader(readStateEngine, MemoryMode.SHARED_MEMORY_LAZY);
        try(HollowBlobInput in = HollowBlobInput.randomAccess(blobFile)) {
            reader.readSnapshot(in);
        }
        return readStateEngine;
    }

    private void assertReads(HollowReadStateEngine readStateEngine, byte[][] values,
                        int[] ordinals, boolean hasVariableLengthStorage) {
        HollowObjectTypeReadState readState =
                (HollowObjectTypeReadState)readStateEngine.getTypeState("BytesHolder");
        int fieldIndex = readState.getSchema().getPosition("value");
        HollowObjectTypeReadStateShard shard =
                ((HollowObjectTypeReadStateShard[])readState.getShardsVolatile().getShards())[0];

        if(!hasVariableLengthStorage)
            assertNull(shard.dataElements.varLengthData[fieldIndex]);
        else if(memoryMode == MemoryMode.SHARED_MEMORY_LAZY)
            assertTrue(shard.dataElements.varLengthData[fieldIndex] instanceof EncodedByteBuffer);
        else
            assertTrue(shard.dataElements.varLengthData[fieldIndex] instanceof SegmentedByteArray);

        VariableLengthData storage = hasVariableLengthStorage ? spy(shard.dataElements.varLengthData[fieldIndex]) : null;
        shard.dataElements.varLengthData[fieldIndex] = storage;
        int nonEmpty = 0;
        for(int i = 0; i < values.length; i++) {
            assertArrayEquals(values[i], readState.readBytes(ordinals[i], fieldIndex));
            if(values[i] != null && values[i].length > 0)
                nonEmpty++;
        }
        if(storage != null)
            verify(storage, times(fastPaths ? nonEmpty : 0)).copyTo(anyLong(), any(byte[].class), anyInt(), anyInt());
    }

    private static byte[] bytes(int length) {
        byte[] value = new byte[length];
        for(int i = 0; i < length; i++)
            value[i] = (byte)(i * 31);
        return value;
    }
}
