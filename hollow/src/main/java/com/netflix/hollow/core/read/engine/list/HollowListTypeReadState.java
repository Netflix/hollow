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
package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowListSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.memory.HollowUnsafeHandle;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.ShardsHolder;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for LIST type records.
 */
public class HollowListTypeReadState extends HollowCollectionTypeReadState implements HollowListTypeDataAccess {
    private final HollowListSampler sampler;
    
    private int maxOrdinal;

    volatile HollowListTypeShardsHolder shardsVolatile;

    @Override
    public ShardsHolder getShardsVolatile() {
        return shardsVolatile;
    }

    @Override
    public void updateShardsVolatile(HollowTypeReadStateShard[] shards) {
        this.shardsVolatile = new HollowListTypeShardsHolder(shards);
    }

    @Override
    public HollowTypeDataElements[] createTypeDataElements(int len) {
        return new HollowListTypeDataElements[len];
    }

    @Override
    public HollowTypeReadStateShard createTypeReadStateShard(HollowSchema schema, HollowTypeDataElements dataElements, int shardOrdinalShift) {
        return new HollowListTypeReadStateShard((HollowListTypeDataElements) dataElements, shardOrdinalShift);
    }

    public HollowListTypeReadState(HollowReadStateEngine stateEngine, MemoryMode memoryMode, HollowListSchema schema) {
        super(stateEngine, memoryMode, schema);
        this.sampler = new HollowListSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
        this.shardsVolatile = null;
    }

    public HollowListTypeReadState(HollowListSchema schema, HollowListTypeDataElements dataElements) {
        super(null, MemoryMode.ON_HEAP, schema);
        this.sampler = new HollowListSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);

        HollowListTypeReadStateShard newShard = new HollowListTypeReadStateShard(dataElements, 0);
        this.shardsVolatile = new HollowListTypeShardsHolder(new HollowListTypeReadStateShard[] {newShard});
        this.maxOrdinal = dataElements.maxOrdinal;
    }

    @Override
    public void readSnapshot(HollowBlobInput in, ArraySegmentRecycler memoryRecycler, int numShards) throws IOException {
        if(numShards > 1)
            maxOrdinal = VarInt.readVInt(in);

        HollowListTypeReadStateShard[] newShards = new HollowListTypeReadStateShard[numShards];
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        for(int i=0; i<numShards; i++) {
            HollowListTypeDataElements shardDataElements = new HollowListTypeDataElements(memoryMode, memoryRecycler);
            shardDataElements.readSnapshot(in);
            newShards[i] = new HollowListTypeReadStateShard(shardDataElements, shardOrdinalShift);
        }
        shardsVolatile = new HollowListTypeShardsHolder(newShards);

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].dataElements.maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema schema, ArraySegmentRecycler memoryRecycler, int deltaNumShards) throws IOException {
        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0; i<shardsVolatile.shards.length; i++) {
            HollowListTypeDataElements deltaData = new HollowListTypeDataElements(memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);

                HollowListTypeDataElements currentData = shardsVolatile.shards[i].dataElements;
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
                HollowListTypeDataElements nextData = new HollowListTypeDataElements(memoryMode, memoryRecycler);
                HollowListTypeDataElements oldData = shardsVolatile.shards[i].dataElements;
                nextData.applyDelta(oldData, deltaData);

                HollowListTypeReadStateShard newShard = new HollowListTypeReadStateShard(nextData, shardsVolatile.shards[i].shardOrdinalShift);
                shardsVolatile = new HollowListTypeShardsHolder(shardsVolatile.shards, newShard, i);

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
        HollowListTypeDataElements.discardFromStream(in, numShards, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(in);
    }

    @Override
    public HollowListSchema getSchema() {
        return (HollowListSchema) schema;
    }

    @Override
    public int maxOrdinal() {
        return maxOrdinal;
    }

    @Override
    public int getElementOrdinal(int ordinal, int listIndex) {
        sampler.recordGet();

        HollowListTypeShardsHolder shardsHolder;
        HollowListTypeReadStateShard shard;
        int shardOrdinal;
        int elementOrdinal;
        long startElement;
        long endElement;

        do {
            do {
                shardsHolder = this.shardsVolatile;
                shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];
                shardOrdinal = ordinal >> shard.shardOrdinalShift;

                startElement = shard.dataElements.getStartElement(shardOrdinal);
                endElement = shard.dataElements.getEndElement(shardOrdinal);
            } while(readWasUnsafe(shardsHolder, ordinal, shard));

            elementOrdinal = shard.getElementOrdinal(startElement, endElement, listIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        return elementOrdinal;
    }

    @Override
    public int size(int ordinal) {
        sampler.recordSize();

        HollowListTypeShardsHolder shardsHolder;
        HollowListTypeReadStateShard shard;
        int size;

        do {
            shardsHolder = this.shardsVolatile;
            shard = shardsHolder.shards[ordinal & shardsHolder.shardNumberMask];

            size = shard.size(ordinal >> shard.shardOrdinalShift);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));
        return size;
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        sampler.recordIterator();
        return new HollowListOrdinalIterator(ordinal, this);
    }

    private boolean readWasUnsafe(HollowListTypeShardsHolder shardsHolder, int ordinal, HollowListTypeReadStateShard shard) {
        HollowUnsafeHandle.getUnsafe().loadFence();
        HollowListTypeShardsHolder currShardsHolder = shardsVolatile;
        return shardsHolder != currShardsHolder
            && (shard != currShardsHolder.shards[ordinal & currShardsHolder.shardNumberMask]);
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
        final HollowListTypeReadStateShard[] shards = this.shardsVolatile.shards;
        int numShards = shards.length;
        HollowListTypeReadStateShard[] newShards = new HollowListTypeReadStateShard[numShards];
        for (int i=0;i<numShards;i++) {
            newShards[i] = new HollowListTypeReadStateShard(null, shards[i].shardOrdinalShift);
        }
        this.shardsVolatile = new HollowListTypeShardsHolder(newShards);
    }

    HollowListTypeDataElements[] currentDataElements() {
        final HollowListTypeReadStateShard[] shards = this.shardsVolatile.shards;
        HollowListTypeDataElements[] elements = new HollowListTypeDataElements[shards.length];
        for (int i=0;i<shards.length;i++) {
            elements[i] = shards[i].dataElements;
        }
        return elements;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        final HollowListTypeReadStateShard[] shards = this.shardsVolatile.shards;
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowListTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        for(int i=0; i<shards.length; i++)
            shards[i].applyShardToChecksum(checksum, populatedOrdinals, i, shards.length);
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
        final HollowListTypeReadStateShard[] shards = this.shardsVolatile.shards;
        long totalApproximateHeapFootprintInBytes = 0;
        
        for(int i=0; i<shards.length; i++)
            totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
        
        return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
    public long getApproximateHoleCostInBytes() {
        final HollowListTypeReadStateShard[] shards = this.shardsVolatile.shards;
        long totalApproximateHoleCostInBytes = 0;
        
        BitSet populatedOrdinals = getPopulatedOrdinals();

        for(int i=0; i<shards.length; i++)
            totalApproximateHoleCostInBytes += shards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, shards.length);
        
        return totalApproximateHoleCostInBytes;
    }

    @Override
    public int numShards() {
        return this.shardsVolatile.shards.length;
    }
}
