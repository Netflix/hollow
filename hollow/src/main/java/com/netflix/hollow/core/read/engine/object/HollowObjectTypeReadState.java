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

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowObjectSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for OBJECT type records. 
 */
public class HollowObjectTypeReadState extends HollowTypeReadState implements HollowObjectTypeDataAccess {

    private final HollowObjectSchema unfilteredSchema;
    private final HollowObjectSampler sampler;

    private final int shardNumberMask;
    private final int shardOrdinalShift;
    private final HollowObjectTypeReadStateShard shards[];

    private int maxOrdinal;

    public HollowObjectTypeReadState(HollowReadStateEngine fileEngine, HollowObjectSchema schema) {
        this(fileEngine, MemoryMode.ON_HEAP, schema, schema, 1);
    }

    public HollowObjectTypeReadState(HollowReadStateEngine fileEngine, MemoryMode memoryMode, HollowObjectSchema schema, HollowObjectSchema unfilteredSchema, int numShards) {
        super(fileEngine, memoryMode, schema);
        this.sampler = new HollowObjectSampler(schema, DisabledSamplingDirector.INSTANCE);
        this.unfilteredSchema = unfilteredSchema;
        this.shardNumberMask = numShards - 1;
        this.shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);
        
        if(numShards < 1 || 1 << shardOrdinalShift != numShards)
            throw new IllegalArgumentException("Number of shards must be a power of 2!");
        
        HollowObjectTypeReadStateShard shards[] = new HollowObjectTypeReadStateShard[numShards];
        for(int i=0;i<shards.length;i++)
            shards[i] = new HollowObjectTypeReadStateShard(schema);
        
        this.shards = shards;
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
        if(shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0;i<shards.length;i++) {
            HollowObjectTypeDataElements snapshotData = new HollowObjectTypeDataElements(getSchema(), memoryMode, memoryRecycler);
            snapshotData.readSnapshot(in, unfilteredSchema);
            shards[i].setCurrentData(snapshotData);
        }

        if(shards.length == 1)
            maxOrdinal = shards[0].currentDataElements().maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }
    
    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler) throws IOException {
        if(shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0;i<shards.length;i++) {
            HollowObjectTypeDataElements deltaData = new HollowObjectTypeDataElements((HollowObjectSchema)deltaSchema, memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shards.length);

                HollowObjectTypeDataElements currentData = shards[i].currentDataElements();
                GapEncodedVariableLengthIntegerReader oldRemovals = currentData.encodedRemovals == null ? GapEncodedVariableLengthIntegerReader.EMPTY_READER : currentData.encodedRemovals;
                if(oldRemovals.isEmpty()) {
                    currentData.encodedRemovals = deltaData.encodedRemovals;
                    oldRemovals.destroy();
                } else {
                    if(!deltaData.encodedRemovals.isEmpty()) {
                        currentData.encodedRemovals = GapEncodedVariableLengthIntegerReader.combine(oldRemovals, deltaData.encodedRemovals, memoryMode, memoryRecycler);
                        oldRemovals.destroy();
                    }
                    deltaData.encodedRemovals.destroy();
                }

                deltaData.encodedAdditions.destroy();
            } else {
                HollowObjectTypeDataElements nextData = new HollowObjectTypeDataElements(getSchema(), memoryMode, memoryRecycler);
                HollowObjectTypeDataElements oldData = shards[i].currentDataElements();
                nextData.applyDelta(oldData, deltaData, i);
                shards[i].setCurrentData(nextData);
                // SNAP: TODO: BUG: deltaData.encodedAdditions has been destroyed when calling nextData.applyDelta(oldData, deltaData, i) above
                notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shards.length);
                deltaData.encodedAdditions.destroy();
                oldData.destroy();
            }
            deltaData.destroy();
            stateEngine.getMemoryRecycler().swap();
        }

        if(shards.length == 1)
            maxOrdinal = shards[0].currentDataElements().maxOrdinal;
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
        return shards[ordinal & shardNumberMask].isNull(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readOrdinal(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readInt(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readFloat(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readDouble(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readLong(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readBoolean(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readBytes(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].readString(ordinal >> shardOrdinalShift, fieldIndex);
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].isStringFieldEqual(ordinal >> shardOrdinalShift, fieldIndex, testValue);
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        return shards[ordinal & shardNumberMask].findVarLengthFieldHashCode(ordinal >> shardOrdinalShift, fieldIndex);
    }

    /**
     * Warning:  Not thread-safe.  Should only be called within the update thread.
     * @param fieldName the field name
     * @return the number of bits required for the field
     */
    public int bitsRequiredForField(String fieldName) {
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
        for(int i=0;i<shards.length;i++)
            shards[i].invalidate();
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
        HollowObjectTypeDataElements currentDataElements[] = new HollowObjectTypeDataElements[shards.length];
        
        for(int i=0;i<shards.length;i++)
            currentDataElements[i] = shards[i].currentDataElements();
        
        return currentDataElements;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!(withSchema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("HollowObjectTypeReadState can only calculate checksum with a HollowObjectSchema: " + getSchema().getName());

        BitSet populatedOrdinals = getPopulatedOrdinals();
        
        for(int i=0;i<shards.length;i++)
            shards[i].applyToChecksum(checksum, withSchema, populatedOrdinals, i, shards.length);
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
	
	void setCurrentData(HollowObjectTypeDataElements data) {
	    if(shards.length > 1)
	        throw new UnsupportedOperationException("Cannot directly set data on sharded type state");
	    shards[0].setCurrentData(data);
	    maxOrdinal = data.maxOrdinal;
	}

    @Override
    public int numShards() {
        return shards.length;
    }
	
}
