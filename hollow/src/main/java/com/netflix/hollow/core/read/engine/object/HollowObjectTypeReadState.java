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

    public static class ShardsHolder {  // TODO: drop public
        public final IHollowObjectTypeReadStateShard shards[];  // TODO: package private
        final int shardNumberMask;

        public ShardsHolder(int numShards) {
            this.shards = new HollowObjectTypeReadStateShard[numShards];
            this.shardNumberMask = numShards - 1;
            // for(int i=0;i<this.readStateShards.length;i++)
            //     this.readStateShards[i] = new HollowObjectTypeReadStateShard(schema, shardOrdinalShift); shardOrdinalShift different for different readStateShards
        }

        public IHollowObjectTypeReadStateShard[] getShards() {  // TODO: package private
            return shards;
        }
    }

    public volatile ShardsHolder shardsVolatile;    // TODO: package private


    private int maxOrdinal;

    public HollowObjectTypeReadState(HollowReadStateEngine fileEngine, HollowObjectSchema schema) {
        this(fileEngine, MemoryMode.ON_HEAP, schema, schema, 1);
    }

    public HollowObjectTypeReadState(HollowReadStateEngine fileEngine, MemoryMode memoryMode, HollowObjectSchema schema, HollowObjectSchema unfilteredSchema, int numShards) {
        super(fileEngine, memoryMode, schema);
        this.sampler = new HollowObjectSampler(schema, DisabledSamplingDirector.INSTANCE);
        this.unfilteredSchema = unfilteredSchema;

        int shardNumberMask = numShards - 1;
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);   // numShards = 4 => shardOrdinalShift = 2. Ordinal 4 = 100, shardOrdinal = 100 >> 2 == 1 (in shard 0). Ordinal 10 = 1010, shardOrdinal = 1010 >> 2 = 2 (in shard 2)
        if(numShards < 1 || 1 << shardOrdinalShift != numShards)
            throw new IllegalArgumentException("Number of shards must be a power of 2!");

        this.shardsVolatile = new ShardsHolder(numShards);
        for(int i=0;i<numShards;i++) {
            this.shardsVolatile.shards[i] = new HollowObjectTypeReadStateShard(schema, shardOrdinalShift, 0);
        }
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
        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i = 0; i< shardsVolatile.shards.length; i++) {
            HollowObjectTypeDataElements snapshotData = new HollowObjectTypeDataElements(getSchema(), memoryMode, memoryRecycler);
            snapshotData.readSnapshot(in, unfilteredSchema);
            shardsVolatile.shards[i].setCurrentData(shardsVolatile, snapshotData);
        }

        if(shardsVolatile.shards.length == 1)
            maxOrdinal = shardsVolatile.shards[0].currentDataElements().maxOrdinal;

        SnapshotPopulatedOrdinalsReader.readOrdinals(in, stateListeners);
    }

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler) throws IOException {
        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i=0;i<shardsVolatile.shards.length;i++) {
            HollowObjectTypeDataElements deltaData = new HollowObjectTypeDataElements((HollowObjectSchema)deltaSchema, memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);

                HollowObjectTypeDataElements currentData = shardsVolatile.shards[i].currentDataElements();
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
                HollowObjectTypeDataElements oldData = shardsVolatile.shards[i].currentDataElements();
                nextData.applyDelta(oldData, deltaData);
                shardsVolatile.shards[i].setCurrentData(shardsVolatile, nextData);
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

    @Override
    public void applyDelta(HollowBlobInput in, HollowSchema deltaSchema, ArraySegmentRecycler memoryRecycler, int newShardCount) throws IOException {

        if (newShardCount != shardsVolatile.shards.length) {
            int currShardCount = shardsVolatile.shards.length;
            boolean doublingTheNumOfShards = newShardCount  == 2* currShardCount;
            boolean halvingTheNumOfShards  = currShardCount == 2* newShardCount;
            if (newShardCount < 0 || !(doublingTheNumOfShards || halvingTheNumOfShards)) {
                throw new IllegalStateException("Invalid shard resizing, currShardCount= " + currShardCount + ", newShardCount= " + newShardCount);
            }

            ShardsHolder newShards = new ShardsHolder(newShardCount);
            if (doublingTheNumOfShards) { // split existing shards
                // Step 1:
                // ∀ i ∈ [0,currShardCount) { newShards.shard[i] and newShards.shard[i+currShardCount] will point to the
                //                            same underlying data elements as the current i-th shard but reference alternating ordinals }
                for(int i = 0; i< currShardCount; i++) {
                    newShards.shards[i] = new HollowObjectTypeReadStateShard((HollowObjectSchema) schema,
                            shardsVolatile.shards[i].shardOrdinalShift() - 1, 0);
                    newShards.shards[i].setCurrentData(newShards, shardsVolatile.shards[i].currentDataElements());

                    newShards = null; // SNAP: TODO: This wont work without incorporating shardOridnalOffset in underlying HollowObjectTypeReadStateShard
                    newShards.shards[currShardCount + i] = new HollowObjectTypeReadStateShard((HollowObjectSchema) schema,
                            shardsVolatile.shards[i].shardOrdinalShift() - 1, 1); // TODO: implement offset
                    newShards.shards[currShardCount + i].setCurrentData(newShards, shardsVolatile.shards[i].currentDataElements());
                }
                shardsVolatile = newShards; // serve up newShards

                // Step 2:
                // For each current shard, split or join the referenced data elements into a copy and discard the pre-split data elements
                for(int i = 0; i< currShardCount; i++) {
                    HollowObjectTypeDataElements preSplitDataElements = shardsVolatile.shards[i].currentDataElements();
                    int finalShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newShardCount);
                    // For e.g.
                    //  numShards = 4 => shardOrdinalShift = 2.
                    //      Ordinal 4 = 100, shardOrdinal = 100 >> 2 == 1 (in shard 0).
                    //      Ordinal 10 = 1010, shardOrdinal = 1010 >> 2 = 2 (in shard 2)
                    HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(preSplitDataElements, 2);
                    HollowObjectTypeDataElements[] splitDataElements = splitter.split();

                    IHollowObjectTypeReadStateShard finalShardLeft = new HollowObjectTypeReadStateShard((HollowObjectSchema) schema, finalShardOrdinalShift, 0);
                    finalShardLeft.setCurrentData(shardsVolatile, splitDataElements[0]);
                    shardsVolatile.shards[i] = finalShardLeft;
                    shardsVolatile = shardsVolatile;    // assignment of volatile array element to self is required

                    IHollowObjectTypeReadStateShard finalShardRight = new HollowObjectTypeReadStateShard((HollowObjectSchema) schema, finalShardOrdinalShift, 0);
                    finalShardRight.setCurrentData(shardsVolatile, splitDataElements[1]);
                    shardsVolatile.shards[i + currShardCount] = finalShardRight;

                    shardsVolatile = shardsVolatile;        // assignment of volatile array element to self is required

                    preSplitDataElements.destroy();         // its now safe to destroy pre-split data elements
                }
                // shardsVolatile now contains newShardCount shards with split/joined data from the original shards
                // this is the desired end state of splitting shards
            } else if (halvingTheNumOfShards) { // join existing shards
                throw new UnsupportedOperationException("Not yet implemented");
            }

            /**
            VirtualShard[] virtualShards = new VirtualShard[newShardCount];
            // -- -- -- -- -- -- -- --
            for (int i = 0; i< shardsVolatile.shards.length; i++) {
                // virtualize the physical shards, currentDataElements retained
                VirtualShard virtualShard = new VirtualShard((HollowObjectTypeReadStateShard) shardsVolatile.shards[i]);   // TODO: This should also initialize left and right
                // at this point virtual shard with split physical shards is fully capable of serving the same requests as earlier single physical shard
                // but some requests might still be on the old shard and old currentDataDataElements so how can we safely destroy that? maybe with a load fence
                // at the shard level, similar to how we do it at the currentDataElements level
                virtualShards[i] = virtualShard;
                // HollowObjectTypeDataElements oldData = shards.readStateShards[i].currentDataElements();
                // shards.readStateShards[i] = virtualShard;
                // oldData.destroy();
            } **/
        }

        if(shardsVolatile.shards.length > 1)
            maxOrdinal = VarInt.readVInt(in);

        for(int i = 0; i< shardsVolatile.shards.length; i++) {
            HollowObjectTypeDataElements deltaData = new HollowObjectTypeDataElements((HollowObjectSchema)deltaSchema, memoryMode, memoryRecycler);
            deltaData.readDelta(in);
            if(stateEngine.isSkipTypeShardUpdateWithNoAdditions() && deltaData.encodedAdditions.isEmpty()) {

                if(!deltaData.encodedRemovals.isEmpty())
                    notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, i, shardsVolatile.shards.length);

                HollowObjectTypeDataElements currentData = shardsVolatile.shards[i].currentDataElements();
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
                HollowObjectTypeDataElements oldData = shardsVolatile.shards[i].currentDataElements();
                nextData.applyDelta(oldData, deltaData);
                shardsVolatile.shards[i].setCurrentData(shardsVolatile, nextData);
                shardsVolatile = shardsVolatile;
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
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.isNull(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readOrdinal(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readInt(ordinal >> shard.shardOrdinalShift() + shard.shardOrdinalOffset(), fieldIndex);
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readFloat(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readDouble(ordinal >> shard.shardOrdinalShift() + shard.shardOrdinalOffset(), fieldIndex);
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readLong(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readBoolean(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readBytes(ordinal >> shard.shardOrdinalOffset(), fieldIndex);
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.readString(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.isStringFieldEqual(ordinal >> shard.shardOrdinalShift(), fieldIndex, testValue);
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        sampler.recordFieldAccess(fieldIndex);
        IHollowObjectTypeReadStateShard shard = shardsVolatile.shards[ordinal & shardsVolatile.shardNumberMask];
        return shard.findVarLengthFieldHashCode(ordinal >> shard.shardOrdinalShift(), fieldIndex);
    }

    /**
     * Warning:  Not thread-safe.  Should only be called within the update thread.
     * @param fieldName the field name
     * @return the number of bits required for the field
     */
    public int bitsRequiredForField(String fieldName) {
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
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
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
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
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
        HollowObjectTypeDataElements currentDataElements[] = new HollowObjectTypeDataElements[shards.length];
        
        for(int i=0;i<shards.length;i++)
            currentDataElements[i] = shards[i].currentDataElements();
        
        return currentDataElements;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
        if(!(withSchema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("HollowObjectTypeReadState can only calculate checksum with a HollowObjectSchema: " + getSchema().getName());

        BitSet populatedOrdinals = getPopulatedOrdinals();
        
        for(int i=0;i<shards.length;i++)
            shards[i].applyToChecksum(checksum, withSchema, populatedOrdinals, i, shards.length);
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	    long totalApproximateHeapFootprintInBytes = 0;
	    
	    for(int i=0;i<shards.length;i++)
	        totalApproximateHeapFootprintInBytes += shards[i].getApproximateHeapFootprintInBytes();
	    
	    return totalApproximateHeapFootprintInBytes;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	    long totalApproximateHoleCostInBytes = 0;
	    
	    BitSet populatedOrdinals = getPopulatedOrdinals();

	    for(int i=0;i<shards.length;i++)
	        totalApproximateHoleCostInBytes += shards[i].getApproximateHoleCostInBytes(populatedOrdinals, i, shards.length);
        
	    return totalApproximateHoleCostInBytes;
	}
	
	void setCurrentData(HollowObjectTypeDataElements data) {
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
	    if(shards.length > 1)
	        throw new UnsupportedOperationException("Cannot directly set data on sharded type state");
	    shards[0].setCurrentData(this.shardsVolatile, data);
	    maxOrdinal = data.maxOrdinal;
	}

    @Override
    public int numShards() {
        IHollowObjectTypeReadStateShard[] shards = this.shardsVolatile.shards;
        return shards.length;
    }
	
}
