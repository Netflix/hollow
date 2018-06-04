/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSetSampler;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.EmptyOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowSetOrdinalIterator;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for OBJECT type records. 
 */
public class HollowSetTypeReadState extends HollowCollectionTypeReadState implements HollowSetTypeDataAccess {

    private final HollowSetSampler sampler;
    
    private final int shardNumberMask;
    private final int shardOrdinalShift;
    private final HollowSetTypeReadStateShard shards[];
    
    private HollowPrimaryKeyValueDeriver keyDeriver;
    
    private int maxOrdinal;

    public HollowSetTypeReadState(HollowReadStateEngine stateEngine, HollowSetSchema schema, int numShards) {
        super(stateEngine, schema);
        this.sampler = new HollowSetSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
        this.shardNumberMask = numShards - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        
        if(numShards < 1 || 1 << shardOrdinalShift != numShards)
            throw new IllegalArgumentException("Number of shards must be a power of 2!");
        
        HollowSetTypeReadStateShard shards[] = new HollowSetTypeReadStateShard[numShards];
        for(int i=0;i<shards.length;i++)
            shards[i] = new HollowSetTypeReadStateShard();
        
        this.shards = shards;

    }

    @Override
    public void readSnapshot(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        if(shards.length > 1)
            maxOrdinal = VarInt.readVInt(dis);
        
        for(int i=0;i<shards.length;i++) {
            HollowSetTypeDataElements snapshotData = new HollowSetTypeDataElements(memoryRecycler);
            snapshotData.readSnapshot(dis);
            shards[i].setCurrentData(snapshotData);
        }
        
        if(shards.length == 1)
            maxOrdinal = shards[0].currentDataElements().maxOrdinal;
        
        SnapshotPopulatedOrdinalsReader.readOrdinals(dis, stateListeners);
    }

    @Override
    public void applyDelta(DataInputStream dis, HollowSchema schema, ArraySegmentRecycler memoryRecycler) throws IOException {
        if(shards.length > 1)
            maxOrdinal = VarInt.readVInt(dis);

        for(int i=0;i<shards.length;i++) {
            HollowSetTypeDataElements deltaData = new HollowSetTypeDataElements(memoryRecycler);
            HollowSetTypeDataElements nextData = new HollowSetTypeDataElements(memoryRecycler);
            deltaData.readDelta(dis);
            HollowSetTypeDataElements oldData = shards[i].currentDataElements();
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

    public static void discardSnapshot(DataInputStream dis, int numShards) throws IOException {
        discardType(dis, numShards, false);
    }

    public static void discardDelta(DataInputStream dis, int numShards) throws IOException {
        discardType(dis, numShards, true);
    }

    public static void discardType(DataInputStream dis, int numShards, boolean delta) throws IOException {
        HollowSetTypeDataElements.discardFromStream(dis, numShards, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(dis);
    }

    @Override
    public int maxOrdinal() {
        return maxOrdinal;
    }

    @Override
    public int size(int ordinal) {
        sampler.recordSize();
        return shards[ordinal & shardNumberMask].size(ordinal >> shardOrdinalShift);
    }

    @Override
    public boolean contains(int ordinal, int value) {
        return contains(ordinal, value, value);
    }

    @Override
    public boolean contains(int ordinal, int value, int hashCode) {
        sampler.recordGet();
        return shards[ordinal & shardNumberMask].contains(ordinal >> shardOrdinalShift, value, hashCode);
    }
    
    @Override
    public int findElement(int ordinal, Object... hashKey) {
        sampler.recordGet();
        
        if(keyDeriver == null)
            return ORDINAL_NONE;
        
        FieldType[] fieldTypes = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return ORDINAL_NONE;

        return shards[ordinal & shardNumberMask].findElement(ordinal >> shardOrdinalShift, hashKey);
    }
    

    @Override
    public int relativeBucketValue(int setOrdinal, int bucketIndex) {
        return shards[setOrdinal & shardNumberMask].relativeBucketValue(setOrdinal >> shardOrdinalShift, bucketIndex);
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
        for(int i=0;i<shards.length;i++)
            shards[i].invalidate();
    }

    HollowSetTypeDataElements[] currentDataElements() {
        HollowSetTypeDataElements currentDataElements[] = new HollowSetTypeDataElements[shards.length];
        
        for(int i=0;i<shards.length;i++)
            currentDataElements[i] = shards[i].currentDataElements();
        
        return currentDataElements;
    }

    void setCurrentData(HollowSetTypeDataElements data) {
        if(shards.length > 1)
            throw new UnsupportedOperationException("Cannot directly set data on sharded type state");
        shards[0].setCurrentData(data);
        maxOrdinal = data.maxOrdinal;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowSetTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        for(int i=0;i<shards.length;i++)
            shards[i].applyToChecksum(checksum, populatedOrdinals, i, shards.length);
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
        long totalApproximateHeapFootprintInBytes = 0;
        
        for(int i=0;i<shards.length;i++)
            totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
        
        return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
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
	    if(getSchema().getHashKey() != null)
	        this.keyDeriver = new HollowPrimaryKeyValueDeriver(getSchema().getHashKey(), getStateEngine());
	    
	    for(int i=0;i<shards.length;i++)
	        shards[i].setKeyDeriver(keyDeriver);
	}

    @Override
    public int numShards() {
        return shards.length;
    }

}