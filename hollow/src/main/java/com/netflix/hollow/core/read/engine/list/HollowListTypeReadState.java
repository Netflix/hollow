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
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for LIST type records.
 */
public class HollowListTypeReadState extends HollowCollectionTypeReadState implements HollowListTypeDataAccess {

    private final HollowListSampler sampler;
    
    private final int shardNumberMask;
    private final int shardOrdinalShift;
    private final HollowListTypeReadStateShard shards[];
    
    private int maxOrdinal;

    public HollowListTypeReadState(HollowReadStateEngine stateEngine, HollowListSchema schema, int numShards) {
        super(stateEngine, schema);
        this.sampler = new HollowListSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
        this.shardNumberMask = numShards - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        
        if(numShards < 1 || 1 << shardOrdinalShift != numShards)
            throw new IllegalArgumentException("Number of shards must be a power of 2!");
        
        HollowListTypeReadStateShard shards[] = new HollowListTypeReadStateShard[numShards];
        for(int i=0;i<shards.length;i++)
            shards[i] = new HollowListTypeReadStateShard();
        
        this.shards = shards;
    }

    @Override
    public void readSnapshot(HollowBlobInput in, BufferedWriter debug, ArraySegmentRecycler memoryRecycler) throws IOException {
        if(shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);
        
        for(int i=0;i<shards.length;i++) {
            HollowListTypeDataElements snapshotData = new HollowListTypeDataElements(memoryRecycler);
            snapshotData.readSnapshot(in, debug);
            shards[i].setCurrentData(snapshotData);
        }
        
        if(shards.length == 1)
            maxOrdinal = shards[0].currentDataElements().maxOrdinal;
        
        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, BufferedWriter debug, HollowSchema schema, ArraySegmentRecycler memoryRecycler) throws IOException {
        if(shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0; i<shards.length; i++) {
            HollowListTypeDataElements deltaData = new HollowListTypeDataElements(memoryRecycler);
            HollowListTypeDataElements nextData = new HollowListTypeDataElements(memoryRecycler);
            deltaData.readDelta(in, debug);
            HollowListTypeDataElements oldData = shards[i].currentDataElements();
            nextData.applyDelta(oldData, deltaData);
            shards[i].setCurrentData(nextData);
            notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shards.length);
            deltaData.destroy();
            oldData.destroy();
            stateEngine.getMemoryRecycler().swap();
        }

        if(shards.length == 1)
            maxOrdinal = shards[0].currentDataElements().maxOrdinal;
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
        return shards[ordinal & shardNumberMask].getElementOrdinal(ordinal >> shardOrdinalShift, listIndex);
    }

    @Override
    public int size(int ordinal) {
        sampler.recordSize();
        return shards[ordinal & shardNumberMask].size(ordinal >> shardOrdinalShift);
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        sampler.recordIterator();

        return new HollowListOrdinalIterator(ordinal, this);
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
        for(int i=0;i<shards.length;i++)
            shards[i].invalidate();
    }

    HollowListTypeDataElements[] currentDataElements() {
        HollowListTypeDataElements currentDataElements[] = new HollowListTypeDataElements[shards.length];
        
        for(int i=0; i<shards.length; i++)
            currentDataElements[i] = shards[i].currentDataElements();
        
        return currentDataElements;
    }

    void setCurrentData(HollowListTypeDataElements data) {
        if(shards.length > 1)
            throw new UnsupportedOperationException("Cannot directly set data on sharded type state");
        shards[0].setCurrentData(data);
        maxOrdinal = data.maxOrdinal;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowListTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        for(int i=0; i<shards.length; i++)
            shards[i].applyToChecksum(checksum, populatedOrdinals, i, shards.length);
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
        long totalApproximateHeapFootprintInBytes = 0;
        
        for(int i=0; i<shards.length; i++)
            totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
        
        return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
    public long getApproximateHoleCostInBytes() {
        long totalApproximateHoleCostInBytes = 0;
        
        BitSet populatedOrdinals = getPopulatedOrdinals();

        for(int i=0; i<shards.length; i++)
            totalApproximateHoleCostInBytes += shards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, shards.length);
        
        return totalApproximateHoleCostInBytes;
    }

    @Override
    public int numShards() {
        return shards.length;
    }

}
