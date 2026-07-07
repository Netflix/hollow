package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.EncodedLongBuffer;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
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
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Verifies {@link HollowObjectTypeReadStateShard#readLong} returns identical values whether the
 * field's bitsPerField lands on the fast single-read path (getElementValue, {@code <= 56} bits) or
 * the wide two-word path (getLargeElementValue). The bitsPerField of a long field is derived from
 * the widest zig-zag-encoded value in the type, so we drive each path by controlling the maximum
 * magnitude present, and assert the actual path taken by inspecting bitsPerField.
 *
 * Each dataset is checked in both consumer memory modes, since {@code readLong} reads through the
 * {@link com.netflix.hollow.core.memory.FixedLengthData} interface which is a
 * {@link FixedLengthElementArray} on-heap and an {@link EncodedLongBuffer} in shared-memory mode.
 */
public class HollowObjectTypeReadStateReadLongTest {

    // Pinned to a single shard so that ordinals never span shards: bitsPerField is then uniform
    // across the (only) shard, making the shard[0] path assertions below valid and deterministic.
    @HollowShardLargeType(numShards = 1)
    public static class LongHolder {
        long value;
        LongHolder(long value) { this.value = value; }
    }

    @Test
    public void readLong_fastPath_matches() throws Exception {
        // All magnitudes small enough that zig-zag encoding stays within 56 bits -> fast path.
        List<Long> values = new ArrayList<>();
        values.add(0L);
        values.add(1L);
        values.add(-1L);
        values.add(Long.MIN_VALUE >> 12); // large negative, still well under 56 encoded bits
        values.add(Long.MAX_VALUE >> 12); // large positive
        for (int shift = 0; shift <= 54; shift++) {
            values.add(1L << shift);
            values.add(-(1L << shift));
        }
        assertReadLongRoundTrips(values, /*expectFastPath=*/true);
    }

    @Test
    public void readLong_widePath_matches() throws Exception {
        // Presence of full-width values forces bitsPerField > 56 -> wide path, unchanged behavior.
        List<Long> values = new ArrayList<>();
        values.add(0L);
        values.add(1L);
        values.add(-1L);
        values.add(Long.MAX_VALUE);
        values.add(Long.MIN_VALUE);
        values.add(Long.MAX_VALUE - 1);
        values.add(1L << 57);
        values.add(-(1L << 57));
        assertReadLongRoundTrips(values, /*expectFastPath=*/false);
    }

    private void assertReadLongRoundTrips(List<Long> values, boolean expectFastPath) throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(LongHolder.class);

        int[] ordinals = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            ordinals[i] = objectMapper.add(new LongHolder(values.get(i)));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeStateEngine).writeSnapshot(baos);
        byte[] snapshot = baos.toByteArray();

        verify(readOnHeap(snapshot), values, ordinals, expectFastPath, /*sharedMemory=*/false);
        verify(readSharedMemory(snapshot), values, ordinals, expectFastPath, /*sharedMemory=*/true);
    }

    private static HollowReadStateEngine readOnHeap(byte[] snapshot) throws Exception {
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        try (HollowBlobInput in = HollowBlobInput.serial(new ByteArrayInputStream(snapshot))) {
            reader.readSnapshot(in);
        }
        return readStateEngine;
    }

    private static HollowReadStateEngine readSharedMemory(byte[] snapshot) throws Exception {
        File blobFile = File.createTempFile("readlong-shm-snapshot", ".bin");
        blobFile.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(blobFile)) {
            fos.write(snapshot);
        }
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine, MemoryMode.SHARED_MEMORY_LAZY);
        try (HollowBlobInput in = HollowBlobInput.randomAccess(blobFile)) {
            reader.readSnapshot(in);
        }
        return readStateEngine;
    }

    private static void verify(HollowReadStateEngine readStateEngine, List<Long> values, int[] ordinals,
                              boolean expectFastPath, boolean sharedMemory) {
        HollowObjectTypeReadState readState =
                (HollowObjectTypeReadState) readStateEngine.getTypeState("LongHolder");
        int fieldIndex = readState.getSchema().getPosition("value");

        HollowObjectTypeReadStateShard shard =
                ((HollowObjectTypeReadStateShard[]) readState.getShardsVolatile().getShards())[0];

        // Confirm this run actually exercises the intended FixedLengthData implementation.
        if (sharedMemory) {
            assertTrue("expected EncodedLongBuffer in shared-memory mode",
                    shard.dataElements.fixedLengthData instanceof EncodedLongBuffer);
        } else {
            assertTrue("expected FixedLengthElementArray on-heap",
                    shard.dataElements.fixedLengthData instanceof FixedLengthElementArray);
        }

        int bitsPerField = shard.dataElements.bitsPerField[fieldIndex];
        if (expectFastPath) {
            assertTrue("expected fast path but bitsPerField=" + bitsPerField, bitsPerField <= 56);
        } else {
            assertTrue("expected wide path but bitsPerField=" + bitsPerField, bitsPerField > 56);
        }

        for (int i = 0; i < values.size(); i++) {
            assertEquals("value at ordinal " + ordinals[i] + " sharedMemory=" + sharedMemory,
                    (long) values.get(i), readState.readLong(ordinals[i], fieldIndex));
        }
    }
}
