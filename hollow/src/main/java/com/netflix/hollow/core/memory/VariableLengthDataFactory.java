package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.util.logging.Logger;

public class VariableLengthDataFactory {

    private static final Logger LOG = Logger.getLogger(VariableLengthDataFactory.class.getName());

    public static VariableLengthData get(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {

        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new SegmentedByteArray(memoryRecycler);

        } else if (memoryMode.equals(MemoryMode.SHARED_MEMORY_LAZY)) {
            /// list pointer array
            return new EncodedByteBuffer();
        } else {
            throw new UnsupportedOperationException("Memory mode " + memoryMode.name() + " not supported");
        }
    }

    public static VariableLengthData allocate(MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) {
        if (memoryMode.equals(MemoryMode.ON_HEAP)) {
            return new SegmentedByteArray(memoryRecycler);
        } else {
            // File targetFile = provisionTargetFile(numBytes, "/tmp/delta-target-" + target.schema.getName() + "_"
            //         + target.schema.getFieldType(i) + "_"
            //         + target.schema.getFieldName(i) + "_"
            //         + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+ "_" + UUID.randomUUID());
            EncodedByteBuffer targetByteBuffer = new EncodedByteBuffer();
            // TODO: resize file as needed
            // target.varLengthData[i] = targetByteBuffer;
            throw new UnsupportedOperationException("Shared memory mode doesnt support delta transitions for var length types (String and byte[])");
            // SNAP: TODO: support writing to EncodedByteBuffers to support var length types like strings and byte arrays
        }
    }

    public static void destroy(VariableLengthData vld) {
        if (vld instanceof SegmentedByteArray) {
            ((SegmentedByteArray) vld).destroy();
        } else if (vld instanceof EncodedByteBuffer) {
            LOG.warning("Destroy operation is a not implemented for shared memory mode");
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
    }
}
