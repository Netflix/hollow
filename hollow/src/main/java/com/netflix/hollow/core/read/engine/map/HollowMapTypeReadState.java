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
package com.netflix.hollow.core.read.engine.map;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import static com.netflix.hollow.core.index.FieldPaths.FieldPathException.ErrorKind.NOT_BINDABLE;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowMapSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.index.FieldPaths;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.ShardsHolder;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.EmptyMapOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIteratorImpl;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link HollowTypeReadState} for MAP type records. 
 */
public class HollowMapTypeReadState extends HollowTypeReadState implements HollowMapTypeDataAccess {
    private static final Logger LOG = Logger.getLogger(HollowMapTypeReadState.class.getName());
    private static final HollowTypeReshardingStrategy RESHARDING_STRATEGY = new HollowMapTypeReshardingStrategy();

    private final HollowMapSampler sampler;
    
    private HollowPrimaryKeyValueDeriver keyDeriver;
    
    private int maxOrdinal;

    volatile HollowMapTypeShardsHolder shardsVolatile;

    @Override
    public ShardsHolder getShardsVolatile() {
        return shardsVolatile;
    }

    @Override
    public void updateShardsVolatile(HollowTypeReadStateShard[] shards) {
        this.shardsVolatile = new HollowMapTypeShardsHolder(shards);
    }

    @Override
    public HollowTypeDataElements[] createTypeDataElements(int len) {
        return new HollowMapTypeDataElements[len];
    }

    @Override
    public HollowTypeReadStateShard createTypeReadStateShard(HollowSchema schema, HollowTypeDataElements dataElements, int shardOrdinalShift) {
        return new HollowMapTypeReadStateShard((HollowMapTypeDataElements) dataElements, shardOrdinalShift);
    }

    public HollowMapTypeReadState(HollowReadStateEngine stateEngine, MemoryMode memoryMode, HollowMapSchema schema) {
        super(stateEngine, memoryMode, schema);
        this.sampler = new HollowMapSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
        this.shardsVolatile = null;
    }

    public HollowMapTypeReadState(HollowMapSchema schema, HollowMapTypeDataElements dataElements) {
        super(null, MemoryMode.ON_HEAP, schema);
        this.sampler = new HollowMapSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);

        HollowMapTypeReadStateShard newShard = new HollowMapTypeReadStateShard(dataElements, 0);
        this.shardsVolatile = new HollowMapTypeShardsHolder(new HollowMapTypeReadStateShard[] {newShard});
        this.maxOrdinal = dataElements.maxOrdinal;
    }

    @Override
    public void readSnapshot(HollowBlobInput in, ArraySegmentRecycler memoryRecycler, int numShards) throws IOException {
        if(numShards > 1)
            maxOrdinal = VarInt.readVInt(in);

        HollowMapTypeReadStateShard[] newShards = new HollowMapTypeReadStateShard[numShards];
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        for(int i=0; i<numShards; i++) {
            HollowMapTypeDataElements shardDataElements = new HollowMapTypeDataElements(memoryMode, memoryRecycler);
            shardDataElements.readSnapshot(in);
            newShards[i] = new HollowMapTypeReadStateShard(shardDataElements, shardOrdinalShift);
        }
        shardsVolatile = new HollowMapTypeShardsHolder(newShards);

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].dataElements.maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema schema, ArraySegmentRecycler memoryRecycler, int deltaNumShards) throws IOException {
        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0; i<shardsVolatile.shards.length; i++) {
            HollowMapTypeDataElements deltaData = new HollowMapTypeDataElements(memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);

                HollowMapTypeDataElements currentData = shardsVolatile.shards[i].currentDataElements();
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
                HollowMapTypeDataElements nextData = new HollowMapTypeDataElements(memoryMode, memoryRecycler);
                HollowMapTypeDataElements oldData = shardsVolatile.shards[i].currentDataElements();
                nextData.applyDelta(oldData, deltaData);

                HollowMapTypeReadStateShard newShard = new HollowMapTypeReadStateShard(nextData, shardsVolatile.shards[i].shardOrdinalShift);
                shardsVolatile = new HollowMapTypeShardsHolder(shardsVolatile.shards, newShard, i);

                notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);
                deltaData.encodedAdditions.destroy();
                oldData.destroy();
            }
            deltaData.destroy();
            stateEngine.getMemoryRecycler().swap();
        }

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].currentDataElements().maxOrdinal;
    }

    public static void discardSnapshot(HollowBlobInput in, int numShards) throws IOException {
        discardType(in, numShards, false);
    }

    public static void discardDelta(HollowBlobInput in, int numShards) throws IOException {
        discardType(in, numShards, true);
    }

    public static void discardType(HollowBlobInput in, int numShards, boolean delta) throws IOException {
        HollowMapTypeDataElements.discardFromInput(in, numShards, delta);
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

        return shards[ordinal & shardNumberMask].size(ordinal >> shardOrdinalShift);
    }

    @Override
    public int get(int ordinal, int keyOrdinal) {
        return get(ordinal, keyOrdinal, keyOrdinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal, int hashCode) {
        sampler.recordGet();
        
        return shards[ordinal & shardNumberMask].get(ordinal >> shardOrdinalShift, keyOrdinal, hashCode);
    }
    
    @Override
    public int findKey(int ordinal, Object... hashKey) {
        sampler.recordGet();
        
        if(keyDeriver == null)
            return ORDINAL_NONE;
        
        FieldType fieldTypes[] = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return ORDINAL_NONE;

        return shards[ordinal & shardNumberMask].findKey(ordinal >> shardOrdinalShift, hashKey);
    }

    @Override
    public int findValue(int ordinal, Object... hashKey) {
        return (int)findEntry(ordinal, hashKey);
    }
    
    @Override
    public long findEntry(int ordinal, Object... hashKey) {
        sampler.recordGet();
        
        if(keyDeriver == null)
            return -1L;
        
        FieldType fieldTypes[] = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return -1L;

        return shards[ordinal & shardNumberMask].findEntry(ordinal >> shardOrdinalShift, hashKey);
    }

    @Override
    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        sampler.recordGet();

        if(size(ordinal) == 0)
            return EmptyMapOrdinalIterator.INSTANCE;
        return new PotentialMatchHollowMapEntryOrdinalIteratorImpl(ordinal, this, hashCode);
    }

    @Override
    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal) {
        sampler.recordIterator();

        if(size(ordinal) == 0)
            return EmptyMapOrdinalIterator.INSTANCE;
        return new HollowMapEntryOrdinalIteratorImpl(ordinal, this);
    }

    @Override
    public long relativeBucket(int ordinal, int bucketIndex) {
        return shards[ordinal & shardNumberMask].relativeBucket(ordinal >> shardOrdinalShift, bucketIndex);
    }

    @Override
    public HollowMapSchema getSchema() {
        return (HollowMapSchema)schema;
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
        for(int i=0; i<shards.length; i++)
            shards[i].invalidate();
    }

    HollowMapTypeDataElements[] currentDataElements() {
        HollowMapTypeDataElements currentDataElements[] = new HollowMapTypeDataElements[shards.length];
        
        for(int i=0; i<shards.length; i++)
            currentDataElements[i] = shards[i].currentDataElements();
        
        return currentDataElements;
    }

    void setCurrentData(HollowMapTypeDataElements data) {
        if(shards.length > 1)
            throw new UnsupportedOperationException("Cannot directly set data on sharded type state");
        shards[0].setCurrentData(data);
        maxOrdinal = data.maxOrdinal;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowMapTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
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
    
    public HollowPrimaryKeyValueDeriver getKeyDeriver() {
        return keyDeriver;
    }
    
    public void buildKeyDeriver() {
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

        for(int i=0; i<shards.length; i++)
            shards[i].setKeyDeriver(keyDeriver);
    }

    @Override
    public int numShards() {
        return shards.length;
    }

}
