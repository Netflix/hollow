package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.EncodedByteBuffer;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.SegmentedByteArray;
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
import org.junit.Test;

public class HollowObjectTypeReadStateBytesTest {

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

        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(BytesHolder.class);

        int[] ordinals = new int[values.length];
        for(int i = 0; i < values.length; i++)
            ordinals[i] = objectMapper.add(new BytesHolder(values[i]));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeStateEngine).writeSnapshot(baos);
        byte[] snapshot = baos.toByteArray();

        verify(readOnHeap(snapshot), values, ordinals, false);
        verify(readSharedMemory(snapshot), values, ordinals, true);
    }

    private static HollowReadStateEngine readOnHeap(byte[] snapshot) throws Exception {
        HollowReadStateEngine readStateEngine =
                new HollowReadStateEngine(WastefulRecycler.SMALL_ARRAY_RECYCLER);
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

    private static void verify(HollowReadStateEngine readStateEngine, byte[][] values,
                               int[] ordinals, boolean sharedMemory) {
        HollowObjectTypeReadState readState =
                (HollowObjectTypeReadState)readStateEngine.getTypeState("BytesHolder");
        int fieldIndex = readState.getSchema().getPosition("value");
        HollowObjectTypeReadStateShard shard =
                ((HollowObjectTypeReadStateShard[])readState.getShardsVolatile().getShards())[0];

        if(sharedMemory)
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
