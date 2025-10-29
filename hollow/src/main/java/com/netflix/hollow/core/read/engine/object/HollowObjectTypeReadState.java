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
package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowObjectSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.encoding.ZigZag;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for OBJECT type records.
 */
public class HollowObjectTypeReadState extends HollowTypeReadState implements HollowObjectTypeDataAccess {
    private final HollowObjectSchema unfilteredSchema;
    private final HollowObjectSampler sampler;

    private int maxOrdinal;

    volatile HollowObjectTypeShardsHolder shardsVolatile;

    // Multi-partition support
    private HollowObjectTypeReadStatePartition[] partitions;
    private int numPartitions = 1;  // default to 1 for backward compatibility
    private static final int PARTITION_INDEX_BITS = 3;  // supports up to 8 partitions
    private static final int PARTITION_INDEX_MASK = (1 << PARTITION_INDEX_BITS) - 1;  // 0b111

    @Override
    public HollowObjectTypeShardsHolder getShardsVolatile() {
        return shardsVolatile;
    }

    @Override
    public void updateShardsVolatile(HollowTypeReadStateShard[] shards) {
        this.shardsVolatile = new HollowObjectTypeShardsHolder(shards);
    }

    @Override
    public HollowTypeDataElements[] createTypeDataElements(int len) {
        return new HollowObjectTypeDataElements[len];
    }

    @Override
    public HollowTypeReadStateShard createTypeReadStateShard(HollowSchema schema, HollowTypeDataElements dataElements, int shardOrdinalShift) {
        return new HollowObjectTypeReadStateShard((HollowObjectSchema) schema, (HollowObjectTypeDataElements) dataElements, shardOrdinalShift);
    }

    public HollowObjectTypeReadState(HollowReadStateEngine fileEngine, MemoryMode memoryMode, HollowObjectSchema schema, HollowObjectSchema unfilteredSchema) {
        super(fileEngine, memoryMode, schema);
        this.sampler = new HollowObjectSampler(schema, DisabledSamplingDirector.INSTANCE);
        this.unfilteredSchema = unfilteredSchema;
        this.shardsVolatile = null;
    }

    public HollowObjectTypeReadState(HollowObjectSchema schema, HollowObjectTypeDataElements dataElements) {
        super(null, MemoryMode.ON_HEAP, schema);
        this.sampler = new HollowObjectSampler(schema, DisabledSamplingDirector.INSTANCE);
        this.unfilteredSchema = schema;

        HollowObjectTypeReadStateShard newShard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);
        this.shardsVolatile = new HollowObjectTypeShardsHolder(new HollowObjectTypeReadStateShard[] {newShard});
        this.maxOrdinal = dataElements.maxOrdinal;
    }

    /**
     * Sets the partition array for multi-partition support.
     *
     * @param partitions the array of partitions
     */
    public void setPartitions(HollowObjectTypeReadStatePartition[] partitions) {
        this.partitions = partitions;
        this.numPartitions = partitions.length;

        if(numPartitions == 1) {
            this.shardsVolatile = new HollowObjectTypeShardsHolder(partitions[0].getShards());
        }

        // Calculate maxOrdinal as max encoded ordinal across all partitions
        int maxEncodedOrdinal = 0;
        for(int p = 0; p < numPartitions; p++) {
            int encodedOrdinal = encodeOrdinal(p, partitions[p].getMaxOrdinal());
            if(encodedOrdinal > maxEncodedOrdinal) {
                maxEncodedOrdinal = encodedOrdinal;
            }
        }
        this.maxOrdinal = maxEncodedOrdinal;
    }

    /**
     * Gets the number of partitions.
     *
     * @return the number of partitions
     */
    public int getNumPartitions() {
        return numPartitions;
    }

    /**
     * Extracts the partition index from an encoded ordinal.
     * The partition index is stored in the first 3 bits.
     *
     * @param encodedOrdinal the encoded ordinal
     * @return the partition index (0-7)
     */
    private int extractPartitionIndex(int encodedOrdinal) {
        return encodedOrdinal & PARTITION_INDEX_MASK;
    }

    /**
     * Extracts the partition-local ordinal from an encoded ordinal.
     * The partition ordinal is stored in bits beyond the first 3 bits.
     *
     * @param encodedOrdinal the encoded ordinal
     * @return the ordinal within the partition
     */
    private int extractPartitionOrdinal(int encodedOrdinal) {
        return encodedOrdinal >> PARTITION_INDEX_BITS;
    }

    /**
     * Encodes a partition index and partition-local ordinal into a single encoded ordinal.
     * Format: [partition ordinal][partition index]
     *         [bits 29-3]        [bits 2-0]
     *
     * @param partitionIndex the partition index (0-7)
     * @param partitionOrdinal the ordinal within the partition
     * @return the encoded ordinal
     */
    private int encodeOrdinal(int partitionIndex, int partitionOrdinal) {
        return (partitionOrdinal << PARTITION_INDEX_BITS) | partitionIndex;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema)schema;
    }

    @Override
    public int maxOrdinal() {
        return maxOrdinal;
    }

    /**
     * Computes the hash code for a primary key lookup.
     * Delegates to {@link com.netflix.hollow.core.index.key.HollowPartitionSelector} for consistency.
     *
     * @param primaryKey the primary key definition
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return hash code for the primary key
     */
    public int computePrimaryKeyHashForLookup(com.netflix.hollow.core.index.key.PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        return com.netflix.hollow.core.index.key.HollowPartitionSelector.computePrimaryKeyHash(primaryKey, primaryKeyFieldValues);
    }

    /**
     * Determines which partition contains a record with the given primary key.
     * Delegates to {@link com.netflix.hollow.core.index.key.HollowPartitionSelector} for consistency.
     *
     * @param primaryKey the primary key definition
     * @param primaryKeyFieldValues the values of the primary key fields in order
     * @return the partition index (0 to numPartitions-1) where this record would be stored
     */
    public int getPartitionForPrimaryKey(com.netflix.hollow.core.index.key.PrimaryKey primaryKey, Object... primaryKeyFieldValues) {
        return com.netflix.hollow.core.index.key.HollowPartitionSelector.getPartitionForPrimaryKey(numPartitions, primaryKey, primaryKeyFieldValues);
    }

    /**
     * Static helper to read partition snapshot data from input stream.
     * Used by HollowBlobReader to read multi-partition snapshots.
     */
    public static HollowObjectTypeReadStatePartition readPartitionSnapshotData(HollowBlobInput in, HollowObjectSchema filteredSchema,
                                                                         HollowObjectSchema unfilteredSchema, int numShards,
                                                                         MemoryMode memoryMode, ArraySegmentRecycler memoryRecycler) throws IOException {
        // Read max ordinal if multiple shards
        int maxOrdinal = 0;
        if(numShards > 1) {
            maxOrdinal = VarInt.readVInt(in);
        }

        // Read shards for this partition
        HollowObjectTypeReadStateShard[] shards = new HollowObjectTypeReadStateShard[numShards];
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);

        for(int i=0; i<numShards; i++) {
            HollowObjectTypeDataElements shardDataElements = new HollowObjectTypeDataElements(filteredSchema, memoryMode, memoryRecycler);
            shardDataElements.readSnapshot(in, unfilteredSchema);
            shards[i] = new HollowObjectTypeReadStateShard(filteredSchema, shardDataElements, shardOrdinalShift);

            // For single shard, get maxOrdinal from the shard
            if(numShards == 1) {
                maxOrdinal = shardDataElements.maxOrdinal;
            }
        }

        return new HollowObjectTypeReadStatePartition(shards, maxOrdinal);
    }

    @Override
    public void readSnapshot(HollowBlobInput in, ArraySegmentRecycler memoryRecycler, int numShards) throws IOException {
        if(numShards > 1)
            maxOrdinal = VarInt.readVInt(in);

        HollowObjectTypeReadStateShard[] newShards = new HollowObjectTypeReadStateShard[numShards];
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        for(int i=0; i<numShards; i++) {
            HollowObjectTypeDataElements shardDataElements = new HollowObjectTypeDataElements(getSchema(), memoryMode, memoryRecycler);
            shardDataElements.readSnapshot(in, unfilteredSchema);
            newShards[i] = new HollowObjectTypeReadStateShard(getSchema(), shardDataElements, shardOrdinalShift);
        }
        shardsVolatile = new HollowObjectTypeShardsHolder(newShards);

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].dataElements.maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler, int deltaNumShards) throws IOException {
        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0; i<shardsVolatile.shards.length; i++) {
            HollowObjectTypeDataElements deltaData = new HollowObjectTypeDataElements((HollowObjectSchema)deltaSchema, memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);

                HollowObjectTypeDataElements currentData = shardsVolatile.shards[i].dataElements;
                GapEncodedVariableLengthIntegerReader oldRemovals = currentData.encodedRemovals == null ? GapEncodedVariableLengthIntegerReader.EMPTY_READER : currentData.encodedRemovals;
                if(oldRemovals.isEmpty()) {
                    currentData.encodedRemovals = deltaData.encodedRemovals;
                    oldRemovals.destroy();
                } else {
                    if(!deltaData.encodedRemovals.isEmpty()) {
                        currentData.encodedRemovals = GapEncodedVariableLengthIntegerReader.combine(oldRemovals, deltaData.encodedRemovals, memoryRecycler);
                        oldRemovals.destroy();
                    }
                    deltaData.encodedRemovals.destroy();
                }

                deltaData.encodedAdditions.destroy();
            } else {
                HollowObjectTypeDataElements nextData = new HollowObjectTypeDataElements(getSchema(), memoryMode, memoryRecycler);
                HollowObjectTypeDataElements oldData = shardsVolatile.shards[i].dataElements;
                nextData.applyDelta(oldData, deltaData);

                HollowObjectTypeReadStateShard newShard = new HollowObjectTypeReadStateShard(getSchema(), nextData, shardsVolatile.shards[i].shardOrdinalShift);
                shardsVolatile = new HollowObjectTypeShardsHolder(shardsVolatile.shards, newShard, i);

                notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);
                deltaData.encodedAdditions.destroy();
                oldData.destroy();
            }
            deltaData.destroy();
            stateEngine.getMemoryRecycler().swap();
        }

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].dataElements.maxOrdinal;
    }

    public static void discardSnapshot(HollowBlobInput in, HollowObjectSchema schema, int numShards) throws IOException {
        discardType(in, schema, numShards, false);
    }

    public static void discardDelta(HollowBlobInput in, HollowObjectSchema schema, int numShards) throws IOException {
        discardType(in, schema, numShards, true);
    }

    public static void discardType(HollowBlobInput in, HollowObjectSchema schema, int numShards, boolean delta) throws IOException {
        HollowObjectTypeDataElements.discardFromInput(in, schema, numShards, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(in);
    }

    @Override
    public boolean isNull(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return isNullSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);
        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);

        long fixedLengthValue = shard.readValue(partitionOrdinal >> shard.shardOrdinalShift, fieldIndex);

        switch(((HollowObjectSchema) schema).getFieldType(fieldIndex)) {
            case BYTES:
            case STRING:
                int numBits = shard.dataElements.bitsPerField[fieldIndex];
                return (fixedLengthValue & (1L << (numBits - 1))) != 0;
            case FLOAT:
                return (int)fixedLengthValue == HollowObjectWriteRecord.NULL_FLOAT_BITS;
            case DOUBLE:
                return fixedLengthValue == HollowObjectWriteRecord.NULL_DOUBLE_BITS;
            default:
                return fixedLengthValue == shard.dataElements.nullValueForField[fieldIndex];
        }
    }

    private boolean isNullSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long fixedLengthValue;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            fixedLengthValue = shard.readValue(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        switch(((HollowObjectSchema) schema).getFieldType(fieldIndex)) {
            case BYTES:
            case STRING:
                int numBits = shard.dataElements.bitsPerField[fieldIndex];
                return (fixedLengthValue & (1L << (numBits - 1))) != 0;
            case FLOAT:
                return (int)fixedLengthValue == HollowObjectWriteRecord.NULL_FLOAT_BITS;
            case DOUBLE:
                return fixedLengthValue == HollowObjectWriteRecord.NULL_DOUBLE_BITS;
            default:
                return fixedLengthValue == shard.dataElements.nullValueForField[fieldIndex];
        }
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readOrdinalSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);
        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);

        long refOrdinal = shard.readOrdinal(partitionOrdinal >> shard.shardOrdinalShift, fieldIndex);

        if(refOrdinal == shard.dataElements.nullValueForField[fieldIndex])
            return ORDINAL_NONE;
        return (int)refOrdinal;
    }

    private int readOrdinalSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long refOrdinal;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            refOrdinal = shard.readOrdinal(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        if(refOrdinal == shard.dataElements.nullValueForField[fieldIndex])
            return ORDINAL_NONE;
        return (int)refOrdinal;
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readIntSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);
        int shardOrdinal = partitionOrdinal >> shard.shardOrdinalShift;

        long value = shard.readInt(shardOrdinal, fieldIndex);

        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return Integer.MIN_VALUE;
        return ZigZag.decodeInt((int)value);
    }

    private int readIntSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long value;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            value = shard.readInt(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return Integer.MIN_VALUE;
        return ZigZag.decodeInt((int)value);
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readFloatSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal and delegate to partition shard
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);

        int value = shard.readFloat(partitionOrdinal >> shard.shardOrdinalShift, fieldIndex);

        if(value == HollowObjectWriteRecord.NULL_FLOAT_BITS)
            return Float.NaN;
        return Float.intBitsToFloat(value);
    }

    private float readFloatSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        int value;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            value = shard.readFloat(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        if(value == HollowObjectWriteRecord.NULL_FLOAT_BITS)
            return Float.NaN;
        return Float.intBitsToFloat(value);
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readDoubleSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal and delegate to partition shard
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);

        long value = shard.readDouble(partitionOrdinal >> shard.shardOrdinalShift, fieldIndex);

        if(value == HollowObjectWriteRecord.NULL_DOUBLE_BITS)
            return Double.NaN;
        return Double.longBitsToDouble(value);
    }

    private double readDoubleSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long value;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            value = shard.readDouble(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        if(value == HollowObjectWriteRecord.NULL_DOUBLE_BITS)
            return Double.NaN;
        return Double.longBitsToDouble(value);
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readLongSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal and delegate to partition shard
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);

        long value = shard.readLong(partitionOrdinal >> shard.shardOrdinalShift, fieldIndex);

        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return Long.MIN_VALUE;
        return ZigZag.decodeLong(value);
    }

    private long readLongSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long value;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            value = shard.readLong(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return Long.MIN_VALUE;
        return ZigZag.decodeLong(value);
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readBooleanSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal and delegate to partition shard
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);

        long value = shard.readBoolean(partitionOrdinal >> shard.shardOrdinalShift, fieldIndex);

        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return null;
        return value == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private Boolean readBooleanSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long value;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            value = shard.readBoolean(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return null;
        return value == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readBytesSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal and delegate to partition shard
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard shard = partition.getShard(partitionOrdinal);
        int shardOrdinal = partitionOrdinal >> shard.shardOrdinalShift;

        int numBitsForField = shard.dataElements.bitsPerField[fieldIndex];
        long currentBitOffset = shard.fieldOffset(shardOrdinal, fieldIndex);
        long endByte = shard.dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
        long startByte = shardOrdinal != 0 ? shard.dataElements.fixedLengthData.getElementValue(currentBitOffset - shard.dataElements.bitsPerRecord, numBitsForField) : 0;

        return shard.readBytes(startByte, endByte, numBitsForField, fieldIndex);
    }

    private byte[] readBytesSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        byte[] result;
        int numBitsForField;
        long currentBitOffset;
        long endByte;
        long startByte;
        int shardOrdinal;

        do {
            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                numBitsForField = shard.dataElements.bitsPerField[fieldIndex];
                currentBitOffset = shard.fieldOffset(shardOrdinal, fieldIndex);
                endByte = shard.dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = shardOrdinal != 0 ? shard.dataElements.fixedLengthData.getElementValue(currentBitOffset - shard.dataElements.bitsPerRecord, numBitsForField) : 0;
            } while (readWasUnsafe(shardsHolder, ordinal, shard));

            result = shard.readBytes(startByte, endByte, numBitsForField, fieldIndex);
        } while (readWasUnsafe(shardsHolder, ordinal, shard));

        return result;
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        // Fast path for single partition (backward compatible)
        if(numPartitions == 1) {
            return readStringSinglePartition(ordinal, fieldIndex);
        }

        // Multi-partition path: decode ordinal and delegate to partition shard
        int partitionIndex = extractPartitionIndex(ordinal);
        int partitionOrdinal = extractPartitionOrdinal(ordinal);

        HollowObjectTypeReadStatePartition partition = partitions[partitionIndex];
        HollowObjectTypeReadStateShard[] partitionShards = partition.getShards();
        int shardNumberMask = partition.shardNumberMask;

        // Use the same pattern as single partition for thread safety
        HollowObjectTypeReadStateShard shard;
        String result;
        int numBitsForField;
        long currentBitOffset;
        long endByte;
        long startByte;
        int shardOrdinal;

        do {
            do {
                shard = partitionShards[partitionOrdinal & shardNumberMask];
                shardOrdinal = partitionOrdinal >> shard.shardOrdinalShift;

                numBitsForField = shard.dataElements.bitsPerField[fieldIndex];
                currentBitOffset = shard.fieldOffset(shardOrdinal, fieldIndex);
                endByte = shard.dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = shardOrdinal != 0 ? shard.dataElements.fixedLengthData.getElementValue(currentBitOffset - shard.dataElements.bitsPerRecord, numBitsForField) : 0;
            } while (readWasUnsafeForPartition(partition, partitionOrdinal, shard));

            result = shard.readString(startByte, endByte, numBitsForField, fieldIndex);
        } while (readWasUnsafeForPartition(partition, partitionOrdinal, shard));

        return result;
    }

    private boolean readWasUnsafeForPartition(HollowObjectTypeReadStatePartition partition, int partitionOrdinal, HollowObjectTypeReadStateShard shard) {
        // For now, partitions are immutable after construction (no delta support yet)
        // So no need for volatile checks - just verify shard matches
        HollowObjectTypeReadStateShard expectedShard = partition.getShards()[partitionOrdinal & partition.shardNumberMask];
        return shard != expectedShard;
    }

    private String readStringSinglePartition(int ordinal, int fieldIndex) {
        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        String result;
        int numBitsForField;
        long currentBitOffset;
        long endByte;
        long startByte;
        int shardOrdinal;

        do {
            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                numBitsForField = shard.dataElements.bitsPerField[fieldIndex];
                currentBitOffset = shard.fieldOffset(shardOrdinal, fieldIndex);
                endByte = shard.dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = shardOrdinal != 0 ? shard.dataElements.fixedLengthData.getElementValue(currentBitOffset - shard.dataElements.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(shardsHolder, ordinal, shard));

            result = shard.readString(startByte, endByte, numBitsForField, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return result;
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        boolean result;
        int numBitsForField;
        long currentBitOffset;
        long endByte;
        long startByte;
        int shardOrdinal;

        do {
            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                numBitsForField = shard.dataElements.bitsPerField[fieldIndex];
                currentBitOffset = shard.fieldOffset(shardOrdinal, fieldIndex);
                endByte = shard.dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = shardOrdinal != 0 ? shard.dataElements.fixedLengthData.getElementValue(currentBitOffset - shard.dataElements.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(shardsHolder, ordinal, shard));

            result = shard.isStringFieldEqual(startByte, endByte, numBitsForField, fieldIndex, testValue);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return result;
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);

        HollowObjectTypeShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        int hashCode;
        int numBitsForField;
        long currentBitOffset;
        long endByte;
        long startByte;
        int shardOrdinal;

        do {
            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                numBitsForField = shard.dataElements.bitsPerField[fieldIndex];
                currentBitOffset = shard.fieldOffset(shardOrdinal, fieldIndex);
                endByte = shard.dataElements.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
                startByte = shardOrdinal != 0 ? shard.dataElements.fixedLengthData.getElementValue(currentBitOffset - shard.dataElements.bitsPerRecord, numBitsForField) : 0;
            } while(readWasUnsafe(shardsHolder, ordinal, shard));

            hashCode = shard.findVarLengthFieldHashCode(startByte, endByte, numBitsForField, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return hashCode;
    }

    private boolean readWasUnsafe(HollowObjectTypeShardsHolder shardsHolder, int ordinal, HollowObjectTypeReadStateShard shard) {
        // Use a load (acquire) fence to constrain the compiler reordering prior plain loads so
        // that they cannot "float down" below the volatile load of shardsVolatile.
        // This ensures data is checked against current shard holder *after* optimistic calculations
        // have been performed on data.
        //
        // Note: the Java Memory Model allows for the reordering of plain loads and stores
        // before a volatile load (those plain loads and stores can "float down" below the
        // volatile load), but forbids the reordering of plain loads after a volatile load
        // (those plain loads are not allowed to "float above" the volatile load).
        // Similar reordering also applies to plain loads and stores and volatile stores.
        // In effect the ordering of volatile loads and stores is retained and plain loads
        // and stores can be shuffled around and grouped together, which increases
        // optimization opportunities.
        // This is why locks can be coarsened; plain loads and stores may enter the lock region
        // from above (float down the acquire) or below (float above the release) but existing
        // loads and stores may not exit (a "lock roach motel" and why there is almost universal
        // misunderstanding of, and many misguided attempts to optimize, the infamous double
        // checked locking idiom).
        //
        // Note: the fence provides stronger ordering guarantees than a corresponding non-plain
        // load or store since the former affects all prior or subsequent loads and stores,
        // whereas the latter is scoped to the particular load or store.
        //
        // For more details see http://gee.cs.oswego.edu/dl/html/j9mm.html
        // [Comment credit: Paul Sandoz]
        //
        HollowUnsafeHandle.getUnsafe().loadFence();
        HollowObjectTypeShardsHolder currShardsHolder = shardsVolatile;
        // Validate against the underlying shard so that, during a delta application that involves re-sharding the worst
        // case no. of times a read will be invalidated is 3: when shards are expanded or truncated, when a shard is affected
        // by a split or join, and finally when delta is applied to a shard. If only shardsHolder was checked here, the
        // worst-case scenario could lead to read invalidation (numShards+2) times: once for shards expansion/truncation, o
        // nce for split/join on any shard, and then once when delta is applied.
        return shardsHolder != currShardsHolder
            && (shard != currShardsHolder.shards[ordinal & currShardsHolder.shardNumberMask]);
    }

    /**
     * Warning:  Not thread-safe.  Should only be called within the update thread.
     * @param fieldName the field name
     * @return the number of bits required for the field
     */
    public int bitsRequiredForField(String fieldName) {
        final HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
        int maxBitsRequiredForField = shards[0].bitsRequiredForField(fieldName);
        
        for(int i=1;i<shards.length;i++) {
            int shardRequiredBits = shards[i].bitsRequiredForField(fieldName);
            if(shardRequiredBits > maxBitsRequiredForField)
                maxBitsRequiredForField = shardRequiredBits;
        }
        
        return maxBitsRequiredForField;
    }
    
    @Override
    public HollowSampler getSampler() {
        return sampler;
    }

    @Override
    protected void invalidate() {
        stateListeners = EMPTY_LISTENERS;
        HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
        int numShards = shards.length;
        HollowObjectTypeReadStateShard[] newShards = new HollowObjectTypeReadStateShard[numShards];
        for (int i=0;i<numShards;i++) {
            newShards[i] = new HollowObjectTypeReadStateShard(getSchema(), null, shards[i].shardOrdinalShift);
        }
        this.shardsVolatile = new HollowObjectTypeShardsHolder(newShards);
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        sampler.setSamplingDirector(director);
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        sampler.setFieldSpecificSamplingDirector(fieldSpec, director);
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
        sampler.setUpdateThread(t);
    }

    HollowObjectTypeDataElements[] currentDataElements() {
        final HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
        HollowObjectTypeDataElements[] elements = new HollowObjectTypeDataElements[shards.length];
        for (int i=0;i<shards.length;i++) {
            elements[i] = shards[i].dataElements;
        }
        return elements;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        final HollowObjectTypeShardsHolder shardsHolder = this.shardsVolatile;
        final HollowObjectTypeReadStateShard[] shards = shardsHolder.shards;
        int shardNumberMask = shardsHolder.shardNumberMask;
        if(!(withSchema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("HollowObjectTypeReadState can only calculate checksum with a HollowObjectSchema: " + getSchema().getName());

        BitSet populatedOrdinals = getPopulatedOrdinals();
        for (int i = 0; i < shards.length; i++) {
            shards[i].applyShardToChecksum(checksum, withSchema, populatedOrdinals, i, shardNumberMask);
        }
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
	    long totalApproximateHeapFootprintInBytes = 0;

	    if(numPartitions > 1) {
	        // Multi-partition path: iterate over all partition shards
	        for(int p = 0; p < numPartitions; p++) {
	            HollowObjectTypeReadStateShard[] partitionShards = partitions[p].getShards();
	            for(int i = 0; i < partitionShards.length; i++) {
	                totalApproximateHeapFootprintInBytes += partitionShards[i].getApproximateHeapFootprintInBytes();
	            }
	        }
	    } else {
	        // Single partition path: use shardsVolatile
	        final HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	        for(int i=0;i<shards.length;i++)
                totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
	    }

	    return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
	    long totalApproximateHoleCostInBytes = 0;
	    BitSet populatedOrdinals = getPopulatedOrdinals();

	    if(numPartitions > 1) {
	        // Multi-partition path: iterate over all partition shards
	        for(int p = 0; p < numPartitions; p++) {
	            HollowObjectTypeReadStateShard[] partitionShards = partitions[p].getShards();
	            for(int i = 0; i < partitionShards.length; i++) {
	                totalApproximateHoleCostInBytes += partitionShards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, partitionShards.length);
	            }
	        }
	    } else {
	        // Single partition path: use shardsVolatile
	        final HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	        for(int i=0;i<shards.length;i++)
	            totalApproximateHoleCostInBytes += shards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, shards.length);
	    }

	    return totalApproximateHoleCostInBytes;
	}

    @Override
    public int numShards() {// SNAP: TODO: at some point in future we'll make numShards partition-level (when we support delta changes)
        if(numPartitions > 1) {
            // Multi-partition path: return number of shards from first partition (all partitions have same number of shards)
            return partitions[0].getShards().length;
        } else {
            // Single partition path: use shardsVolatile
            return this.shardsVolatile.shards.length;
        }
    }
	
}
