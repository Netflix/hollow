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
package com.netflix.hollow.core.read.engine.set;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static com.netflix.hollow.core.index.FieldPaths.FieldPathException.ErrorKind.NOT_BINDABLE;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSetSampler;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.core.read.engine.ShardsHolder;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.EmptyOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowSetOrdinalIterator;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link HollowTypeReadState} for OBJECT type records. 
 */
public class HollowSetTypeReadState extends HollowCollectionTypeReadState implements HollowSetTypeDataAccess {
    private static final Logger LOG = Logger.getLogger(HollowSetTypeReadState.class.getName());

    private final HollowSetSampler sampler;
    

    private int maxOrdinal;

    private volatile HollowPrimaryKeyValueDeriver keyDeriver;
    volatile HollowSetTypeShardsHolder shardsVolatile;

    @Override
    public ShardsHolder getShardsVolatile() {
        return shardsVolatile;
    }

    @Override
    public void updateShardsVolatile(HollowTypeReadStateShard[] shards) {
        this.shardsVolatile = new HollowSetTypeShardsHolder(shards);
    }

    @Override
    public HollowTypeDataElements[] createTypeDataElements(int len) {
        return new HollowSetTypeDataElements[len];
    }

    @Override
    public HollowTypeReadStateShard createTypeReadStateShard(HollowSchema schema, HollowTypeDataElements dataElements, int shardOrdinalShift) {
        return new HollowSetTypeReadStateShard((HollowSetTypeDataElements)dataElements, shardOrdinalShift);
    }

    public HollowSetTypeReadState(HollowReadStateEngine stateEngine, MemoryMode memoryMode, HollowSetSchema schema) {
        super(stateEngine, memoryMode, schema);
        this.sampler = new HollowSetSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
        this.shardsVolatile = null;
    }

    public HollowSetTypeReadState(HollowSetSchema schema, HollowSetTypeDataElements dataElements) {
        super(null, MemoryMode.ON_HEAP, schema);
        this.sampler = new HollowSetSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);

        HollowSetTypeReadStateShard newShard = new HollowSetTypeReadStateShard(dataElements, 0);
        this.shardsVolatile = new HollowSetTypeShardsHolder(new HollowSetTypeReadStateShard[] {newShard});
        this.maxOrdinal = dataElements.maxOrdinal;
    }

    @Override
    public void readSnapshot(HollowBlobInput in, ArraySegmentRecycler memoryRecycler, int numShards) throws IOException {
        if(numShards > 1)
            maxOrdinal = VarInt.readVInt(in);

        HollowSetTypeReadStateShard[] newShards = new HollowSetTypeReadStateShard[numShards];
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        for(int i=0; i<numShards; i++) {
            HollowSetTypeDataElements shardDataElements = new HollowSetTypeDataElements(memoryMode, memoryRecycler);
            shardDataElements.readSnapshot(in);
            newShards[i] = new HollowSetTypeReadStateShard(shardDataElements, shardOrdinalShift);
        }
        shardsVolatile = new HollowSetTypeShardsHolder(newShards);

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].dataElements.maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema schema, ArraySegmentRecycler memoryRecycler, int deltaNumShards) throws IOException {
        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0;i<shardsVolatile.shards.length;i++) {
            HollowSetTypeDataElements deltaData = new HollowSetTypeDataElements(memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);

                HollowSetTypeDataElements currentData = shardsVolatile.shards[i].dataElements;
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
                HollowSetTypeDataElements nextData = new HollowSetTypeDataElements(memoryMode, memoryRecycler);
                HollowSetTypeDataElements oldData = shardsVolatile.shards[i].dataElements;
                nextData.applyDelta(oldData, deltaData);

                HollowSetTypeReadStateShard newShard = new HollowSetTypeReadStateShard(nextData, shardsVolatile.shards[i].shardOrdinalShift);
                shardsVolatile = new HollowSetTypeShardsHolder(shardsVolatile.shards, newShard, i);

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

    public static void discardSnapshot(HollowBlobInput in, int numShards) throws IOException {
        discardType(in, numShards, false);
    }

    public static void discardDelta(HollowBlobInput in, int numShards) throws IOException {
        discardType(in, numShards, true);
    }

    public static void discardType(HollowBlobInput in, int numShards, boolean delta) throws IOException {
        HollowSetTypeDataElements.discardFromStream(in, numShards, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(in);
    }

    @Override
    public int maxOrdinal() {
        return maxOrdinal;
    }

    @Override
    public int size(int ordinal) {
        sampler.recordSize();

        HollowSetTypeShardsHolder shardsHolder;
        HollowSetTypeReadStateShard shard;
        int size;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];

            size = shard.size(ordinal >> shard.shardOrdinalShift);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return size;
    }

    @Override
    public boolean contains(int ordinal, int value) {
        return contains(ordinal, value, value);
    }

    @Override
    public boolean contains(int ordinal, int value, int hashCode) {
        sampler.recordGet();
        HollowSetTypeShardsHolder shardsHolder;
        HollowSetTypeReadStateShard shard;
        int shardOrdinal;
        boolean foundData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;

            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                startBucket = shard.dataElements.getStartBucket(shardOrdinal);
                endBucket = shard.dataElements.getEndBucket(shardOrdinal);
            } while(readWasUnsafe(shardsHolder, ordinal, shard));

            hashCode = HashCodes.hashInt(hashCode);
            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketOrdinal = shard.dataElements.getBucketValue(bucket);

            while(bucketOrdinal != shard.dataElements.emptyBucketValue) {
                if(bucketOrdinal == value) {
                    foundData = true;
                    continue threadsafe;
                }
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketOrdinal = shard.dataElements.getBucketValue(bucket);
            }

            foundData = false;
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return foundData;
    }
    
    @Override
    public int findElement(int ordinal, Object... hashKey) {
        HollowSetTypeShardsHolder shardsHolder;
        HollowSetTypeReadStateShard shard;
        int shardOrdinal;

        sampler.recordGet();
        
        if(keyDeriver == null)
            return ORDINAL_NONE;

        FieldType[] fieldTypes = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return ORDINAL_NONE;

        int hashCode = SetMapKeyHasher.hash(hashKey, keyDeriver.getFieldTypes());

        threadsafe:
        do {
            long startBucket;
            long endBucket;

            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                startBucket = shard.dataElements.getStartBucket(shardOrdinal);
                endBucket = shard.dataElements.getEndBucket(shardOrdinal);
            } while(readWasUnsafe(shardsHolder, ordinal, shard));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketOrdinal = shard.dataElements.getBucketValue(bucket);

            while(bucketOrdinal != shard.dataElements.emptyBucketValue) {
                if(readWasUnsafe(shardsHolder, ordinal, shard))
                    continue threadsafe;

                if(keyDeriver.keyMatches(bucketOrdinal, hashKey))
                    return bucketOrdinal;

                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketOrdinal = shard.dataElements.getBucketValue(bucket);
            }

        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return ORDINAL_NONE;
    }
    

    @Override
    public int relativeBucketValue(int setOrdinal, int bucketIndex) {
        HollowSetTypeShardsHolder shardsHolder;
        HollowSetTypeReadStateShard shard;
        int value;

        do {
            long startBucket;
            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[setOrdinal & shardsHolder.shardNumberMask];

                startBucket = shard.dataElements.getStartBucket(setOrdinal >> shard.shardOrdinalShift);
            } while (readWasUnsafe(shardsHolder, setOrdinal, shard));

            value = shard.dataElements.getBucketValue(startBucket + bucketIndex);

            if(value == shard.dataElements.emptyBucketValue)
                value = ORDINAL_NONE;
        } while (readWasUnsafe(shardsHolder, setOrdinal, shard));

        return value;
    }

    @Override
    public HollowOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        sampler.recordGet();
        if(size(ordinal) == 0)
            return EmptyOrdinalIterator.INSTANCE;
        return new PotentialMatchHollowSetOrdinalIterator(ordinal, this, hashCode);
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        sampler.recordIterator();
        if(size(ordinal) == 0)
            return EmptyOrdinalIterator.INSTANCE;
        return new HollowSetOrdinalIterator(ordinal, this);
    }

    private boolean readWasUnsafe(HollowSetTypeShardsHolder shardsHolder, int ordinal, HollowSetTypeReadStateShard shard) {
        HollowUnsafeHandle.getUnsafe().loadFence();
        HollowSetTypeShardsHolder currShardsHolder = shardsVolatile;
        return shardsHolder != currShardsHolder
                && (shard != currShardsHolder.shards[ordinal & currShardsHolder.shardNumberMask]);
    }

    @Override
    public HollowSetSchema getSchema() {
        return (HollowSetSchema)schema;
    }

    @Override
    public HollowSampler getSampler() {
        return sampler;
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

    @Override
    protected void invalidate() {
        stateListeners = EMPTY_LISTENERS;
        final HollowSetTypeReadStateShard[] shards = this.shardsVolatile.shards;
        int numShards = shards.length;
        HollowSetTypeReadStateShard[] newShards = new HollowSetTypeReadStateShard[numShards];
        for (int i=0;i<numShards;i++) {
            newShards[i] = new HollowSetTypeReadStateShard(null, shards[i].shardOrdinalShift);
        }
        this.shardsVolatile = new HollowSetTypeShardsHolder(newShards);
    }

    HollowSetTypeDataElements[] currentDataElements() {
        final HollowSetTypeReadStateShard[] shards = this.shardsVolatile.shards;
        HollowSetTypeDataElements[] elements = new HollowSetTypeDataElements[shards.length];
        for (int i=0;i<shards.length;i++) {
            elements[i] = shards[i].dataElements;
        }
        return elements;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        final HollowSetTypeShardsHolder shardsHolder = this.shardsVolatile;
        final HollowSetTypeReadStateShard[] shards = shardsHolder.shards;
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowSetTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        for(int i=0;i<shards.length;i++)
            shards[i].applyShardToChecksum(checksum, populatedOrdinals, i, shards.length);
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
        final HollowSetTypeReadStateShard[] shards = this.shardsVolatile.shards;
        long totalApproximateHeapFootprintInBytes = 0;

        for(int i=0;i<shards.length;i++)
            totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
        
        return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
        final HollowSetTypeReadStateShard[] shards = this.shardsVolatile.shards;
        long totalApproximateHoleCostInBytes = 0;
        
        BitSet populatedOrdinals = getPopulatedOrdinals();

        for(int i=0;i<shards.length;i++)
            totalApproximateHoleCostInBytes += shards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, shards.length);
        
        return totalApproximateHoleCostInBytes;
	}
	
	public HollowPrimaryKeyValueDeriver getKeyDeriver() {
	    return keyDeriver;
	}
	
	public void buildKeyDeriver() {
        final HollowSetTypeReadStateShard[] shards = this.shardsVolatile.shards;
        if(getSchema().getHashKey() != null) {
            try {
                this.keyDeriver = new HollowPrimaryKeyValueDeriver(getSchema().getHashKey(), getStateEngine());
            } catch (FieldPaths.FieldPathException e) {
                if (e.error == NOT_BINDABLE) {
                    LOG.log(Level.WARNING, "Failed to create a key value deriver for " + getSchema().getHashKey() +
                        " because a field could not be bound to a type in the state");
                } else {
                    throw e;
                }
            }
        }
	}

    @Override
    public int numShards() {
        return this.shardsVolatile.shards.length;
    }

}