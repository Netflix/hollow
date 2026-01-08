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
import java.util.Map;

/**
 * A {@link HollowTypeReadState} for OBJECT type records.
 */
public class HollowObjectTypeReadState extends HollowTypeReadState implements HollowObjectTypeDataAccess {
    private final HollowObjectSchema unfilteredSchema;
    private final HollowObjectSampler sampler;

    private int maxOrdinal;

    volatile HollowObjectTypeShardsHolder shardsVolatile;

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

    @Override
    public HollowObjectSchema getSchema() {
        return (HollowObjectSchema)schema;
    }

    @Override
    public int maxOrdinal() {
        return maxOrdinal;
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

        // Prepare data elements for appended field writes if delta schema append is enabled
        if (stateEngine != null && stateEngine.getDeltaSchemaAppendConfig() != null &&
            stateEngine.getDeltaSchemaAppendConfig().isEnabled()) {

            // Prepare all shards for potential appended field writes
            for (HollowObjectTypeReadStateShard shard : shardsVolatile.shards) {
                HollowObjectTypeDataElements dataElements = shard.dataElements;

                // Check if data elements need write preparation
                if (dataElements.bitsPerRecord == 0) {
                    dataElements.prepareForWrite();
                }
            }
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
