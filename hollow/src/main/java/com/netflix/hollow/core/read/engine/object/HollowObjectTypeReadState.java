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
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for OBJECT type records. 
 */
public class HollowObjectTypeReadState extends HollowTypeReadState implements HollowObjectTypeDataAccess {

    private final HollowObjectSchema unfilteredSchema;
    private final HollowObjectSampler sampler;
    private int maxOrdinal;
    volatile ShardsHolder shardsVolatile;

    static class ShardsHolder {
        final HollowObjectTypeReadStateShard shards[];
        final int shardNumberMask;

        private ShardsHolder(HollowObjectTypeReadStateShard[] fromShards) {
            this.shards = fromShards;
            this.shardNumberMask = fromShards.length - 1;
        }

        private ShardsHolder(HollowObjectTypeReadStateShard[] oldShards, HollowObjectTypeReadStateShard newShard, int newShardIndex) {
            int numShards = oldShards.length;
            HollowObjectTypeReadStateShard[] shards = new HollowObjectTypeReadStateShard[numShards];
            for (int i=0; i<numShards; i++) {
                if (i == newShardIndex) {
                    shards[i] = newShard;
                } else {
                    shards[i] = oldShards[i];
                }
            }
            this.shards = shards;
            this.shardNumberMask = numShards - 1;
        }
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
        this.shardsVolatile = new ShardsHolder(new HollowObjectTypeReadStateShard[] {newShard});
        this.maxOrdinal = dataElements.maxOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema)schema;
    }

    @Override
    public int maxOrdinal() {
        return maxOrdinal;
    }

    @Override
    public void readSnapshot(HollowBlobInput in, ArraySegmentRecycler memoryRecycler) throws IOException {
        throw new IllegalStateException("Object type read state requires numShards when reading snapshot");
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
        shardsVolatile = new ShardsHolder(newShards);

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].dataElements.maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler, int deltaNumShards) throws IOException {
        if (shouldReshard(shardsVolatile.shards.length, deltaNumShards)) {
            reshard(deltaNumShards);
        }
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
                shardsVolatile = new ShardsHolder(shardsVolatile.shards, newShard, i);

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

    /**
     * Given old and new numShards, this method returns the shard resizing multiplier.
     */
    static int shardingFactor(int oldNumShards, int newNumShards) {
        if (newNumShards <= 0 || oldNumShards <= 0 || newNumShards == oldNumShards) {
            throw new IllegalStateException("Invalid shard resizing, oldNumShards=" + oldNumShards + ", newNumShards=" + newNumShards);
        }

        boolean isNewGreater = newNumShards > oldNumShards;
        int dividend = isNewGreater ? newNumShards : oldNumShards;
        int divisor = isNewGreater ? oldNumShards : newNumShards;

        if (dividend % divisor != 0) {
            throw new IllegalStateException("Invalid shard resizing, oldNumShards=" + oldNumShards + ", newNumShards=" + newNumShards);
        }
        return dividend / divisor;
    }

    /**
     * Reshards this type state to the desired shard count using O(shard size) space while supporting concurrent reads
     * into the underlying data elements.
     *
     * @param newNumShards The desired number of shards
     */
    void reshard(int newNumShards) {
        int prevNumShards = shardsVolatile.shards.length;
        int shardingFactor = shardingFactor(prevNumShards, newNumShards);
        HollowObjectTypeDataElements[] newDataElements;
        int[] shardOrdinalShifts;

        if (newNumShards>prevNumShards) { // split existing shards
            // Step 1:  Grow the number of shards. Each original shard will result in N child shards where N is the sharding factor.
            // The child shards will reference into the existing data elements as-is, and reuse existing shardOrdinalShift.
            // However since the shards array is resized, a read will map into the new shard index, as a result a subset of
            // ordinals in each shard will be accessed. In the next "splitting" step, the data elements in these new shards
            // will be filtered to only retain the subset of ordinals that are actually accessed.
            //
            // This is an atomic update to shardsVolatile: full construction happens-before the store to shardsVolatile,
            // in other words a fully constructed object as visible to this thread will be visible to other threads that
            // load the new shardsVolatile.
            shardsVolatile = expandWithOriginalDataElements(shardsVolatile, shardingFactor);

            // Step 2: Split each original data element into N child data elements where N is the sharding factor.
            // Then update each of the N child shards with the respective split of data element, this will be
            // sufficient to serve all reads into this shard. Once all child shards for a pre-split parent
            // shard have been assigned the split data elements, the parent data elements can be discarded.
            for(int i=0; i<prevNumShards; i++) {
                HollowObjectTypeDataElements originalDataElements = shardsVolatile.shards[i].dataElements;

                shardsVolatile = splitDataElementsForOneShard(shardsVolatile, i, prevNumShards, shardingFactor);

                destroyOriginalDataElements(originalDataElements);
            }
            // Re-sharding done.
            // shardsVolatile now contains newNumShards shards where each shard contains
            // a split of original data elements.

        } else { // join existing shards
            // Step 1: Join N data elements to create one, where N is the sharding factor. Then update each of the
            //         N shards to reference the joined result, but with a new shardOrdinalShift.
            //         Reads will continue to reference the same shard index as before, but the new shardOrdinalShift
            //         will help these reads land at the right ordinal in the joined shard. When all N old shards
            //         corresponding to one new shard have been updated, the N pre-join data elements can be destroyed.
            for (int i=0; i<newNumShards; i++) {
                HollowObjectTypeDataElements destroyCandidates[] = joinCandidates(shardsVolatile.shards, i, shardingFactor);

                shardsVolatile = joinDataElementsForOneShard(shardsVolatile, i, shardingFactor);  // atomic update to shardsVolatile

                for (int j = 0; j < shardingFactor; j ++) {
                    destroyOriginalDataElements(destroyCandidates[j]);
                };
            }

            // Step 2: Resize the shards array to only keep the first newNumShards shards.
            newDataElements = new HollowObjectTypeDataElements[shardsVolatile.shards.length];
            shardOrdinalShifts = new int[shardsVolatile.shards.length];
            copyShardElements(shardsVolatile, newDataElements, shardOrdinalShifts);
            shardsVolatile = new ShardsHolder(Arrays.copyOfRange(shardsVolatile.shards, 0, newNumShards));

            // Re-sharding done.
            // shardsVolatile now contains newNumShards shards where each shard contains
            // a join of original data elements.
        }
    }

    private void copyShardElements(ShardsHolder from, HollowObjectTypeDataElements[] newDataElements, int[] shardOrdinalShifts) {
        for (int i=0; i<from.shards.length; i++) {
            newDataElements[i] = from.shards[i].dataElements;
            shardOrdinalShifts[i] = from.shards[i].shardOrdinalShift;
        }
    }

    private HollowObjectTypeDataElements[] joinCandidates(HollowObjectTypeReadStateShard[] shards, int indexIntoShards, int shardingFactor) {
        HollowObjectTypeDataElements[] result = new HollowObjectTypeDataElements[shardingFactor];
        int newNumShards = shards.length / shardingFactor;
        for (int i=0; i<shardingFactor; i++) {
            result[i] = shards[indexIntoShards + (newNumShards*i)].dataElements;
        };
        return result;
    }

    ShardsHolder joinDataElementsForOneShard(ShardsHolder shardsHolder, int currentIndex, int shardingFactor) {
        int newNumShards = shardsHolder.shards.length / shardingFactor;
        int newShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newNumShards);

        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();
        HollowObjectTypeDataElements[] joinCandidates = joinCandidates(shardsHolder.shards, currentIndex, shardingFactor);
        HollowObjectTypeDataElements joined = joiner.join(joinCandidates);

        HollowObjectTypeReadStateShard[] newShards = Arrays.copyOf(shardsHolder.shards, shardsHolder.shards.length);
        for (int i=0; i<shardingFactor; i++) {
            newShards[currentIndex + (newNumShards*i)] = new HollowObjectTypeReadStateShard(getSchema(), joined, newShardOrdinalShift);
        }
        return new ShardsHolder(newShards);
    }

    ShardsHolder expandWithOriginalDataElements(ShardsHolder shardsHolder, int shardingFactor) {
        int prevNumShards = shardsHolder.shards.length;
        int newNumShards = prevNumShards * shardingFactor;
        HollowObjectTypeReadStateShard[] newShards = new HollowObjectTypeReadStateShard[newNumShards];

        for(int i=0; i<prevNumShards; i++) {
            for (int j=0; j<shardingFactor; j++) {
                newShards[i+(prevNumShards*j)] = shardsHolder.shards[i];
            }
        }
        return new ShardsHolder(newShards);
    }

    ShardsHolder splitDataElementsForOneShard(ShardsHolder shardsHolder, int currentIndex, int prevNumShards, int shardingFactor) {
        int newNumShards = shardsHolder.shards.length;
        int newShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newNumShards);

        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();
        HollowObjectTypeDataElements dataElementsToSplit = shardsHolder.shards[currentIndex].dataElements;
        HollowObjectTypeDataElements[] splits = splitter.split(dataElementsToSplit, shardingFactor);

        HollowObjectTypeReadStateShard[] newShards = Arrays.copyOf(shardsHolder.shards, shardsHolder.shards.length);
        for (int i = 0; i < shardingFactor; i ++) {
            newShards[currentIndex + (prevNumShards*i)] = new HollowObjectTypeReadStateShard(getSchema(), splits[i], newShardOrdinalShift);
        }
        return new ShardsHolder(newShards);
    }

    private void destroyOriginalDataElements(HollowObjectTypeDataElements dataElements) {
        dataElements.destroy();
        if (dataElements.encodedRemovals != null) {
            dataElements.encodedRemovals.destroy();
        }
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
        HollowObjectTypeReadStateShard shard;
        long fixedLengthValue;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
            fixedLengthValue = shard.isNull(ordinal >> shard.shardOrdinalShift, fieldIndex);
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

        HollowObjectTypeReadState.ShardsHolder shardsHolder;
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

    private boolean readWasUnsafe(ShardsHolder shardsHolder, int ordinal, HollowObjectTypeReadStateShard shard) {
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
        ShardsHolder currShardsHolder = shardsVolatile;
        // Validate against the underlying shard so that, during re-sharding, the maximum times a read will be invalidated
        // is 3: when shards are expanded or truncated, when a shard is affected by a split or join, and finally when
        // delta is applied to a shard. If only shardsHolder was checked here, the worst-case scenario could lead to
        // read invalidation (numShards+2) times: once for shards expansion/truncation, once for split/join on any shard, and
        // then once when delta is applied.
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
        this.shardsVolatile = new ShardsHolder(newShards);
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
        return Arrays.stream(shards)
                .map(shard -> shard.dataElements)
                .toArray(HollowObjectTypeDataElements[]::new);
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        final ShardsHolder shardsHolder = this.shardsVolatile;
        final HollowObjectTypeReadStateShard[] shards = shardsHolder.shards;
        int shardNumberMask = shardsHolder.shardNumberMask;
        if(!(withSchema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("HollowObjectTypeReadState can only calculate checksum with a HollowObjectSchema: " + getSchema().getName());

        BitSet populatedOrdinals = getPopulatedOrdinals();

        for(int i=0;i<shards.length;i++) {
            shards[i].applyShardToChecksum(checksum, withSchema, populatedOrdinals, i, shardNumberMask);
        }
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
        final HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	    long totalApproximateHeapFootprintInBytes = 0;
	    
	    for(int i=0;i<shards.length;i++)
            totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
	    
	    return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
        final HollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	    long totalApproximateHoleCostInBytes = 0;
	    
	    BitSet populatedOrdinals = getPopulatedOrdinals();

	    for(int i=0;i<shards.length;i++)
	        totalApproximateHoleCostInBytes += shards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, shards.length);
        
	    return totalApproximateHoleCostInBytes;
	}

    @Override
    public int numShards() {
        return this.shardsVolatile.shards.length;
    }
	
}
