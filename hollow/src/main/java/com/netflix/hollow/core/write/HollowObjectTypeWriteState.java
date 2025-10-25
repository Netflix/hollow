/*
 *  Copyright 2016-2019 Netflix, Inc.
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
 *
 */
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.ThreadSafeBitSet;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HollowObjectTypeWriteState extends HollowTypeWriteState {
    private static final Logger LOG = Logger.getLogger(HollowObjectTypeWriteState.class.getName());

    /// statistics required for writing fixed length set data
    private FieldStatistics fieldStats;

    /// data required for writing snapshot or delta
    private FixedLengthElementArray fixedLengthLongArray[];
    private ByteDataArray varLengthByteArrays[][];
    private long recordBitOffset[];

    /// additional data required for writing delta
    private ByteDataArray deltaAddedOrdinals[];
    private ByteDataArray deltaRemovedOrdinals[];

    public HollowObjectTypeWriteState(HollowObjectSchema schema) {
        this(schema, -1);
    }

    public HollowObjectTypeWriteState(HollowObjectSchema schema, int numShards) {
        super(schema, numShards);
    }

    public HollowObjectTypeWriteState(HollowObjectSchema schema, int numShards, int numPartitions) {
        super(schema, numShards, numPartitions);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema)schema;
    }

    /**
     * Called to perform a state transition.<p>
     *
     * Precondition: We are adding objects to this state engine.<br>
     * Postcondition: We are writing the previously added objects to a FastBlob.
     *
     */
    @Override
    public void prepareForWrite(boolean canReshard) {
        super.prepareForWrite(canReshard);

        if(numPartitions == 1) {
            // Single partition: use main ordinalMap
            maxOrdinal = ordinalMap.maxOrdinal();
            gatherFieldStats();
            gatherShardingStats(maxOrdinal, canReshard);
        } else {
            // Multi-partition: gather stats from all partitions
            gatherFieldStatsMultiPartition();
            gatherShardingStatsMultiPartition(canReshard);
        }
    }

    private void gatherFieldStats() {
        fieldStats = new FieldStatistics(getSchema());
        for(int i=0;i<=maxOrdinal;i++) {
            discoverObjectFieldStatisticsForRecord(fieldStats, i);
        }
        fieldStats.completeCalculations();
    }

    private void gatherFieldStatsMultiPartition() {
        fieldStats = new FieldStatistics(getSchema());
        for(int p=0; p<numPartitions; p++) {
            HollowTypeWriteStatePartition partition = partitions[p];
            int partitionMaxOrdinal = partition.getMaxOrdinal();
            for(int i=0; i<=partitionMaxOrdinal; i++) {
                discoverObjectFieldStatisticsForRecordInPartition(fieldStats, partition, i);
            }
        }
        fieldStats.completeCalculations();
    }

    private void discoverObjectFieldStatisticsForRecordInPartition(FieldStatistics fieldStats, HollowTypeWriteStatePartition partition, int ordinal) {
        if(partition.getCurrentCyclePopulated().get(ordinal) || partition.getPreviousCyclePopulated().get(ordinal)) {
            long pointer = partition.getOrdinalMap().getPointerForData(ordinal);

            for(int fieldIndex=0; fieldIndex<((HollowObjectSchema)schema).numFields(); fieldIndex++) {
                pointer = discoverObjectFieldStatisticsForFieldInPartition(fieldStats, partition, pointer, fieldIndex);
            }
        }
    }

    private long discoverObjectFieldStatisticsForFieldInPartition(FieldStatistics fieldStats, HollowTypeWriteStatePartition partition, long pointer, int fieldIndex) {
        ByteData data = partition.getOrdinalMap().getByteData().getUnderlyingArray();

        switch(getSchema().getFieldType(fieldIndex)) {
        case BOOLEAN:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 2);
            pointer += 1;
            break;
        case FLOAT:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 32);
            pointer += 4;
            break;
        case DOUBLE:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 64);
            pointer += 8;
            break;
        case INT:
        case LONG:
            long value = VarInt.readVLong(data, pointer);
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, (int)value);
            pointer += VarInt.sizeOfVLong(value);
            break;
        case REFERENCE:
            long refValue = VarInt.readVLong(data, pointer);
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, (int)refValue);
            pointer += VarInt.sizeOfVLong(refValue);
            break;
        case BYTES:
        case STRING:
            int length = VarInt.readVInt(data, pointer);
            pointer += VarInt.sizeOfVInt(length);
            addVarLengthFieldSizeInBytes(fieldStats, fieldIndex, length);
            pointer += length;
            break;
        }

        return pointer;
    }

    private void gatherShardingStatsMultiPartition(boolean canReshard) {
        // For multi-partition, numShards is uniform across all partitions
        // Calculate based on largest partition
        int maxPartitionOrdinal = -1;
        for(int p=0; p<numPartitions; p++) {
            if(partitions[p].getMaxOrdinal() > maxPartitionOrdinal) {
                maxPartitionOrdinal = partitions[p].getMaxOrdinal();
            }
        }
        maxOrdinal = maxPartitionOrdinal;  // Set for partition writing logic
        gatherShardingStats(maxPartitionOrdinal, canReshard);
    }

    @Override
    protected int typeStateNumShards(int maxOrdinal) {
        long projectedSizeOfType = ((long)fieldStats.getNumBitsPerRecord() * (maxOrdinal + 1)) / 8;
        projectedSizeOfType += fieldStats.getTotalSizeOfAllVarLengthData();

        int targetNumShards = 1;
        while(stateEngine.getTargetMaxTypeShardSize() * targetNumShards < projectedSizeOfType)
            targetNumShards *= 2;

        return targetNumShards;
    }

    private void discoverObjectFieldStatisticsForRecord(FieldStatistics fieldStats, int ordinal) {
        if(currentCyclePopulated.get(ordinal) || previousCyclePopulated.get(ordinal)) {
            long pointer = ordinalMap.getPointerForData(ordinal);

            for(int fieldIndex=0; fieldIndex<((HollowObjectSchema)schema).numFields(); fieldIndex++) {
                pointer = discoverObjectFieldStatisticsForField(fieldStats, pointer, fieldIndex);
            }
        }
    }

    private long discoverObjectFieldStatisticsForField(FieldStatistics fieldStats, long pointer, int fieldIndex) {
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        switch(getSchema().getFieldType(fieldIndex)) {
        case BOOLEAN:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 2);
            pointer += 1;
            break;
        case FLOAT:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 32);
            pointer += 4;
            break;
        case DOUBLE:
            addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 64);
            pointer += 8;
            break;
        case LONG:
        case INT:
        case REFERENCE:
            if(VarInt.readVNull(data, pointer)) {
               addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 1);
               pointer += 1;
            } else {
                long vLong = VarInt.readVLong(data, pointer);
                int requiredBitsForFieldValue = 64 - Long.numberOfLeadingZeros(vLong + 1);
                addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, requiredBitsForFieldValue);
                pointer += VarInt.sizeOfVLong(vLong);
            }
            break;
        case BYTES:
        case STRING:
            if(VarInt.readVNull(data, pointer)) {
                addFixedLengthFieldRequiredBits(fieldStats, fieldIndex, 1);
                pointer += 1;
            } else {
                int length = VarInt.readVInt(data, pointer);
                addVarLengthFieldSizeInBytes(fieldStats, fieldIndex, length);
                pointer += length + VarInt.sizeOfVInt(length);
            }
            break;
        }
        return pointer;
    }

    private void addFixedLengthFieldRequiredBits(FieldStatistics fieldStats, int fieldIndex, int numBits) {
        fieldStats.addFixedLengthFieldRequiredBits(fieldIndex, numBits);
    }

    private void addVarLengthFieldSizeInBytes(FieldStatistics fieldStats, int fieldIndex, int numBytes) {
        fieldStats.addVarLengthFieldSize(fieldIndex, numBytes);
    }

    @Override
    public void prepareForNextCycle() {
        super.prepareForNextCycle();

        fieldStats = null;
    }

    @Override
    public void calculateSnapshot() {
        int numBitsPerRecord = fieldStats.getNumBitsPerRecord();

        if(numPartitions == 1) {
            // Single partition - use legacy calculation
            calculateSnapshotSinglePartition(numBitsPerRecord);
        } else {
            // Multi-partition - calculate per partition
            calculateSnapshotMultiPartition(numBitsPerRecord);
        }
    }

    private void calculateSnapshotSinglePartition(int numBitsPerRecord) {
        fixedLengthLongArray = new FixedLengthElementArray[numShards];
        varLengthByteArrays = new ByteDataArray[numShards][];
        recordBitOffset = new long[numShards];

        for(int i=0;i<numShards;i++) {
            fixedLengthLongArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numBitsPerRecord * (maxShardOrdinal[i] + 1));
            varLengthByteArrays[i] = new ByteDataArray[getSchema().numFields()];
        }

        int shardMask = numShards - 1;

        for(int i=0;i<=maxOrdinal;i++) {
            int shardNumber = i & shardMask;
            if(currentCyclePopulated.get(i)) {
                addRecord(i, recordBitOffset[shardNumber], fixedLengthLongArray[shardNumber], varLengthByteArrays[shardNumber]);
            } else {
                addNullRecord(i, recordBitOffset[shardNumber], fixedLengthLongArray[shardNumber], varLengthByteArrays[shardNumber]);
            }
            recordBitOffset[shardNumber] += numBitsPerRecord;
        }
    }

    private void calculateSnapshotMultiPartition(int numBitsPerRecord) {
        // For multi-partition, we'll calculate data on-demand during writePartitionShard
        // This avoids allocating memory for all partitions at once
        // The calculation will be done per-partition in writePartitionShard
    }

    public void writeSnapshot(DataOutputStream os) throws IOException {
        LOG.log(Level.FINE, String.format("Writing snapshot with numPartitions=%s, numShards=%s, revNumShards=%s, max shard ordinals=%s",
                numPartitions, numShards, revNumShards, Arrays.toString(maxShardOrdinal)));

        if(numPartitions == 1) {
            // Single partition - use legacy format for backward compatibility
            writeSinglePartitionSnapshot(os);
        } else {
            // Multi-partition format
            writeMultiPartitionSnapshot(os);
        }

        // Cleanup
        fixedLengthLongArray = null;
        varLengthByteArrays = null;
        recordBitOffset = null;
    }

    private void writeSinglePartitionSnapshot(DataOutputStream os) throws IOException {
        /// for unsharded blobs, support pre v2.1.0 clients
        if(numShards == 1) {
            writeSnapshotShard(os, 0);
        } else {
            /// overall max ordinal
            VarInt.writeVInt(os, maxOrdinal);
            for(int i=0;i<numShards;i++) {
                writeSnapshotShard(os, i);
            }
        }

        /// Populated bits
        currentCyclePopulated.serializeBitsTo(os);
    }

    private void writeMultiPartitionSnapshot(DataOutputStream os) throws IOException {
        // Write each partition sequentially
        for(int p=0; p<numPartitions; p++) {
            HollowTypeWriteStatePartition partition = partitions[p];

            // Write partition index
            VarInt.writeVInt(os, p);

            // Write partition data with shards
            writePartitionSnapshotData(os, p, partition);

            // Write partition populated bits
            partition.getCurrentCyclePopulated().serializeBitsTo(os);
        }
    }

    private void writePartitionSnapshotData(DataOutputStream os, int partitionIndex, HollowTypeWriteStatePartition partition) throws IOException {
        int partMaxOrdinal = partition.getMaxOrdinal();

        // Temporarily swap to partition's data and calculate snapshot using existing logic
        ByteArrayOrdinalMap savedOrdinalMap = this.ordinalMap;
        ThreadSafeBitSet savedPopulated = this.currentCyclePopulated;
        int savedMaxOrdinal = this.maxOrdinal;

        this.ordinalMap = partition.getOrdinalMap();
        this.currentCyclePopulated = partition.getCurrentCyclePopulated();
        this.maxOrdinal = partMaxOrdinal;

        // Reuse existing calculateSnapshotSinglePartition logic
        calculateSnapshotSinglePartition(fieldStats.getNumBitsPerRecord());

        // Write using existing logic
        if(numShards == 1) {
            writeSnapshotShard(os, 0);
        } else {
            VarInt.writeVInt(os, partMaxOrdinal);
            for(int i=0; i<numShards; i++) {
                writeSnapshotShard(os, i);
            }
        }

        // Restore original references
        this.ordinalMap = savedOrdinalMap;
        this.currentCyclePopulated = savedPopulated;
        this.maxOrdinal = savedMaxOrdinal;

        // Clean up temporary arrays
        fixedLengthLongArray = null;
        varLengthByteArrays = null;
        recordBitOffset = null;
    }

    private void writeSnapshotShard(DataOutputStream os, int shardNumber) throws IOException {
        /// 1) shard max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) FixedLength field sizes
        for(int i=0;i<getSchema().numFields();i++) {
            VarInt.writeVInt(os, fieldStats.getMaxBitsForField(i));
        }

        /// 3) FixedLength data
        long numBitsRequired = recordBitOffset[shardNumber];
        long numLongsRequired = recordBitOffset[shardNumber] == 0 ? 0 : ((numBitsRequired - 1) / 64) + 1;
        fixedLengthLongArray[shardNumber].writeTo(os, numLongsRequired);

        /// 4) VarLength data
        for(int i=0;i<varLengthByteArrays[shardNumber].length;i++) {
            if(varLengthByteArrays[shardNumber][i] == null) {
                VarInt.writeVLong(os, 0);
            } else {
                VarInt.writeVLong(os, varLengthByteArrays[shardNumber][i].length());
                varLengthByteArrays[shardNumber][i].getUnderlyingArray().writeTo(os, 0, varLengthByteArrays[shardNumber][i].length());
            }
        }
    }

    @Override
    public void calculateDelta(ThreadSafeBitSet fromCyclePopulated, ThreadSafeBitSet toCyclePopulated, boolean isReverse) {
        int numShards = this.numShards;
        if (isReverse && this.numShards != this.revNumShards) {
            numShards = this.revNumShards;
        }

        int numBitsPerRecord = fieldStats.getNumBitsPerRecord();

        ThreadSafeBitSet deltaAdditions = toCyclePopulated.andNot(fromCyclePopulated);

        fixedLengthLongArray = new FixedLengthElementArray[numShards];
        deltaAddedOrdinals = new ByteDataArray[numShards];
        deltaRemovedOrdinals = new ByteDataArray[numShards];
        varLengthByteArrays = new ByteDataArray[numShards][];
        recordBitOffset = new long[numShards];
        int numAddedRecordsInShard[] = new int[numShards];

        int shardMask = numShards - 1;

        int addedOrdinal = deltaAdditions.nextSetBit(0);
        while(addedOrdinal != -1) {
            numAddedRecordsInShard[addedOrdinal & shardMask]++;
            addedOrdinal = deltaAdditions.nextSetBit(addedOrdinal + 1);
        }

        for(int i=0;i<numShards;i++) {
            fixedLengthLongArray[i] = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, (long)numAddedRecordsInShard[i] * numBitsPerRecord);
            deltaAddedOrdinals[i] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
            deltaRemovedOrdinals[i] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
            varLengthByteArrays[i] = new ByteDataArray[getSchema().numFields()];
        }

        int previousRemovedOrdinal[] = new int[numShards];
        int previousAddedOrdinal[] = new int[numShards];

        for(int i=0;i<=maxOrdinal;i++) {
            int shardNumber = i & shardMask;
            if(deltaAdditions.get(i)) {
                addRecord(i, recordBitOffset[shardNumber], fixedLengthLongArray[shardNumber], varLengthByteArrays[shardNumber]);
                recordBitOffset[shardNumber] += numBitsPerRecord;
                int shardOrdinal = i / numShards;
                VarInt.writeVInt(deltaAddedOrdinals[shardNumber], shardOrdinal - previousAddedOrdinal[shardNumber]);
                previousAddedOrdinal[shardNumber] = shardOrdinal;
            } else if(fromCyclePopulated.get(i) && !toCyclePopulated.get(i)) {
                int shardOrdinal = i / numShards;
                VarInt.writeVInt(deltaRemovedOrdinals[shardNumber], shardOrdinal - previousRemovedOrdinal[shardNumber]);
                previousRemovedOrdinal[shardNumber] = shardOrdinal;
            }
        }
    }

    @Override
    public void writeCalculatedDelta(DataOutputStream os, boolean isReverse, int[] maxShardOrdinal) throws IOException {
        int numShards = this.numShards;
        if (isReverse && this.numShards != this.revNumShards) {
            numShards = this.revNumShards;
        }

        /// for unsharded blobs, support pre v2.1.0 clients
        if(numShards == 1) {
            writeCalculatedDeltaShard(os, 0, maxShardOrdinal);
        } else {
            /// overall max ordinal
            VarInt.writeVInt(os, maxOrdinal);

            for(int i=0;i<numShards;i++) {
                writeCalculatedDeltaShard(os, i, maxShardOrdinal);
            }
        }

        fixedLengthLongArray = null;
        varLengthByteArrays = null;
        deltaAddedOrdinals = null;
        deltaRemovedOrdinals = null;
        recordBitOffset = null;
    }

    private void writeCalculatedDeltaShard(DataOutputStream os, int shardNumber, int[] maxShardOrdinal) throws IOException {

        /// 1) max ordinal
        VarInt.writeVInt(os, maxShardOrdinal[shardNumber]);

        /// 2) removal / addition ordinals.
        VarInt.writeVLong(os, deltaRemovedOrdinals[shardNumber].length());
        deltaRemovedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaRemovedOrdinals[shardNumber].length());
        VarInt.writeVLong(os, deltaAddedOrdinals[shardNumber].length());
        deltaAddedOrdinals[shardNumber].getUnderlyingArray().writeTo(os, 0, deltaAddedOrdinals[shardNumber].length());

        /// 3) FixedLength field sizes
        for(int i=0;i<getSchema().numFields();i++) {
            VarInt.writeVInt(os, fieldStats.getMaxBitsForField(i));
        }

        /// 4) FixedLength data
        long numBitsRequired = recordBitOffset[shardNumber];
        long numLongsRequired = numBitsRequired == 0 ? 0 : ((numBitsRequired - 1) / 64) + 1;
        fixedLengthLongArray[shardNumber].writeTo(os, numLongsRequired);

        /// 5) VarLength data
        for(int i=0;i<varLengthByteArrays[shardNumber].length;i++) {
            if(varLengthByteArrays[shardNumber][i] == null) {
                VarInt.writeVLong(os, 0);
            } else {
                VarInt.writeVLong(os, varLengthByteArrays[shardNumber][i].length());
                varLengthByteArrays[shardNumber][i].getUnderlyingArray().writeTo(os, 0, varLengthByteArrays[shardNumber][i].length());
            }
        }
    }

    /// here we need to add the offsets for the variable-length field endings, as they will be read as the start position for the following record.
    private void addNullRecord(int ordinal, long recordBitOffset, FixedLengthElementArray fixedLengthLongArray, ByteDataArray varLengthByteArrays[]) {
        for(int fieldIndex=0; fieldIndex < getSchema().numFields(); fieldIndex++) {
            if(getSchema().getFieldType(fieldIndex) == FieldType.STRING || getSchema().getFieldType(fieldIndex) == FieldType.BYTES) {
                long fieldBitOffset = recordBitOffset + fieldStats.getFieldBitOffset(fieldIndex);
                int bitsPerElement = fieldStats.getMaxBitsForField(fieldIndex);
                long currentPointer = varLengthByteArrays[fieldIndex] == null ? 0 : varLengthByteArrays[fieldIndex].length();
                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, currentPointer);
            }
        }
    }

    private void addRecord(int ordinal, long recordBitOffset, FixedLengthElementArray fixedLengthLongArray, ByteDataArray varLengthByteArrays[]) {
        long pointer = ordinalMap.getPointerForData(ordinal);

        for(int fieldIndex=0; fieldIndex < getSchema().numFields(); fieldIndex++) {
            pointer = addRecordField(pointer, recordBitOffset, fieldIndex, fixedLengthLongArray, varLengthByteArrays);
        }
    }

    private long addRecordField(long readPointer, long recordBitOffset, int fieldIndex, FixedLengthElementArray fixedLengthLongArray, ByteDataArray varLengthByteArrays[]) {
        FieldType fieldType = getSchema().getFieldType(fieldIndex);
        long fieldBitOffset = recordBitOffset + fieldStats.getFieldBitOffset(fieldIndex);
        int bitsPerElement = fieldStats.getMaxBitsForField(fieldIndex);
        ByteData data = ordinalMap.getByteData().getUnderlyingArray();

        switch(fieldType) {
        case BOOLEAN:
            if(VarInt.readVNull(data, readPointer)) {
                fixedLengthLongArray.setElementValue(fieldBitOffset, 2, 3);
            } else {
                fixedLengthLongArray.setElementValue(fieldBitOffset, 2, data.get(readPointer));
            }
            readPointer += 1;
            break;
        case FLOAT:
            long intValue = data.readIntBits(readPointer) & 0xFFFFFFFFL;
            fixedLengthLongArray.setElementValue(fieldBitOffset, 32, intValue);
            readPointer += 4;
            break;
        case DOUBLE:
            long longValue = data.readLongBits(readPointer);
            fixedLengthLongArray.setElementValue(fieldBitOffset, 64, longValue);
            readPointer += 8;
            break;
        case LONG:
        case INT:
        case REFERENCE:
            if(VarInt.readVNull(data, readPointer)) {
               fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, fieldStats.getNullValueForField(fieldIndex));
               readPointer += 1;
            } else {
                long vLong = VarInt.readVLong(data, readPointer);
                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, vLong);
                readPointer += VarInt.sizeOfVLong(vLong);
            }
            break;
        case BYTES:
        case STRING:
            ByteDataArray varLengthBuf = getByteArray(varLengthByteArrays, fieldIndex);

            if(VarInt.readVNull(data, readPointer)) {
                long offset = varLengthBuf.length();

                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, offset | (1L << (bitsPerElement - 1))); // write offset with set null bit
                readPointer += 1;
            } else {
                int length = VarInt.readVInt(data, readPointer);
                readPointer += VarInt.sizeOfVInt(length);
                varLengthBuf.copyFrom(data, readPointer, length);

                long offset = varLengthBuf.length();

                fixedLengthLongArray.setElementValue(fieldBitOffset, bitsPerElement, offset);
                readPointer += length;
            }
            break;
        }
        return readPointer;
    }

    private ByteDataArray getByteArray(ByteDataArray buffers[], int index) {
        if(buffers[index] == null) {
            buffers[index] = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        }
        return buffers[index];
    }

}
