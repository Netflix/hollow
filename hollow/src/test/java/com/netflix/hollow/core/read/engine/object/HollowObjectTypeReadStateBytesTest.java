package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.EncodedByteBuffer;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
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

    @Parameters(name = "{0}, recycling={1}")
    public static Collection<Object[]> memoryModes() {
        return Arrays.asList(new Object[][] {
                { MemoryMode.ON_HEAP, true },
                { MemoryMode.ON_HEAP, false },
                { MemoryMode.SHARED_MEMORY_LAZY, false }
        });
    }

    private final MemoryMode memoryMode;
    private final boolean recycling;

    public HollowObjectTypeReadStateBytesTest(MemoryMode memoryMode, boolean recycling) {
        this.memoryMode = memoryMode;
        this.recycling = recycling;
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
        verify(readStateEngine, values, ordinals, hasVariableLengthStorage);
    }

    private HollowReadStateEngine readOnHeap(byte[] snapshot) throws Exception {
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(recycling
                ? new RecyclingRecycler() : WastefulRecycler.SMALL_ARRAY_RECYCLER);
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        try(HollowBlobInput in = HollowBlobInput.serial(new ByteArrayInputStream(snapshot))) {
            reader.readSnapshot(in);
        }
        return readStateEngine;
    }

    private static HollowReadStateEngine readSharedMemory(byte[] snapshot) throws Exception {
        File blobFile = File.createTempFile("readbytes-shm-snapshot", ".bin");
        blobFile.deleteOnExit();
        try(FileOutputStream fos = new FileOutputStream(blobFile)) {
            fos.write(snapshot);
        }

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine, MemoryMode.SHARED_MEMORY_LAZY);
        try(HollowBlobInput in = HollowBlobInput.randomAccess(blobFile)) {
            reader.readSnapshot(in);
        }
        return readStateEngine;
    }

    private void verify(HollowReadStateEngine readStateEngine, byte[][] values,
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

        for(int i = 0; i < values.length; i++)
            assertArrayEquals(values[i], readState.readBytes(ordinals[i], fieldIndex));
    }

    private static byte[] bytes(int length) {
        byte[] value = new byte[length];
        for(int i = 0; i < length; i++)
            value[i] = (byte)(i * 31);
        return value;
    }
}
