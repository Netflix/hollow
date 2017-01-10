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

import com.netflix.hollow.core.memory.encoding.HashCodes;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSetSampler;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.iterator.EmptyOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowSetOrdinalIterator;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for OBJECT type records. 
 */
public class HollowSetTypeReadState extends HollowCollectionTypeReadState implements HollowSetTypeDataAccess {

    private HollowSetTypeDataElements currentData;
    private volatile HollowSetTypeDataElements currentDataVolatile;

    private final HollowSetSampler sampler;
    
    private HollowPrimaryKeyValueDeriver keyDeriver;

    public HollowSetTypeReadState(HollowReadStateEngine stateEngine, HollowSetSchema schema) {
        super(stateEngine, schema);
        this.sampler = new HollowSetSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
    }

    @Override
    public void readSnapshot(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowSetTypeDataElements currentData = new HollowSetTypeDataElements(memoryRecycler);
        currentData.readSnapshot(dis);
        setCurrentData(currentData);
        SnapshotPopulatedOrdinalsReader.readOrdinals(dis, stateListeners);
    }

    @Override
    public void applyDelta(DataInputStream dis, HollowSchema schema, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowSetTypeDataElements deltaData = new HollowSetTypeDataElements(memoryRecycler);
        HollowSetTypeDataElements nextData = new HollowSetTypeDataElements(memoryRecycler);
        deltaData.readDelta(dis);
        nextData.applyDelta(currentData, deltaData);
        HollowSetTypeDataElements oldData = currentData;
        setCurrentData(nextData);
        notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, 0, 1);
        deltaData.destroy();
        oldData.destroy();
    }

    public static void discardSnapshot(DataInputStream dis) throws IOException {
        discardType(dis, false);
    }

    public static void discardDelta(DataInputStream dis) throws IOException {
        discardType(dis, true);
    }

    public static void discardType(DataInputStream dis, boolean delta) throws IOException {
        HollowSetTypeDataElements.discardFromStream(dis, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(dis);
    }

    @Override
    public int maxOrdinal() {
        return currentData.maxOrdinal;
    }

    @Override
    public int size(int ordinal) {
        HollowSetTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentData;
            size = (int)currentData.setPointerAndSizeArray.getElementValue((long)(ordinal * currentData.bitsPerFixedLengthSetPortion) + currentData.bitsPerSetPointer, currentData.bitsPerSetSizeValue);
        } while(readWasUnsafe(currentData));

        return size;
    }

    @Override
    public boolean contains(int ordinal, int value) {
        return contains(ordinal, value, value);
    }

    @Override
    public boolean contains(int ordinal, int value, int hashCode) {
        HollowSetTypeDataElements currentData;
        boolean foundData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;

            do {
                currentData = this.currentData;

                startBucket = getAbsoluteBucketStart(currentData, ordinal);
                endBucket = currentData.setPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
            } while(readWasUnsafe(currentData));

            hashCode = HashCodes.hashInt(hashCode);
            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketOrdinal = absoluteBucketValue(currentData, bucket);

            while(bucketOrdinal != currentData.emptyBucketValue) {
                if(bucketOrdinal == value) {
                    foundData = true;
                    continue threadsafe;
                }
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketOrdinal = absoluteBucketValue(currentData, bucket);
            }

            foundData = false;
        } while(readWasUnsafe(currentData));

        return foundData;
    }
    
    @Override
    public int findElement(int ordinal, Object... hashKey) {
        if(keyDeriver == null)
            return -1;
        
        FieldType[] fieldTypes = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return -1;

        int hashCode = SetMapKeyHasher.hash(hashKey, fieldTypes);

        HollowSetTypeDataElements currentData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;

            do {
                currentData = this.currentData;

                startBucket = getAbsoluteBucketStart(currentData, ordinal);
                endBucket = currentData.setPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
            } while(readWasUnsafe(currentData));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketOrdinal = absoluteBucketValue(currentData, bucket);

            while(bucketOrdinal != currentData.emptyBucketValue) {
                if(readWasUnsafe(currentData))
                    continue threadsafe;
                
                if(keyDeriver.keyMatches(bucketOrdinal, hashKey))
                    return bucketOrdinal;
                
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketOrdinal = absoluteBucketValue(currentData, bucket);
            }

        } while(readWasUnsafe(currentData));

        return -1;
    }
    

    @Override
    public int relativeBucketValue(int setOrdinal, int bucketIndex) {
        HollowSetTypeDataElements currentData;
        int value;

        do {
            long startBucket;
            do {
                currentData = this.currentData;

                startBucket = getAbsoluteBucketStart(currentData, setOrdinal);
            } while(readWasUnsafe(currentData));

            value = absoluteBucketValue(currentData, startBucket + bucketIndex);

            if(value == currentData.emptyBucketValue)
                value = -1;
        } while(readWasUnsafe(currentData));

        return value;
    }

    private long getAbsoluteBucketStart(HollowSetTypeDataElements currentData, int ordinal) {
        return ordinal == 0 ? 0 : currentData.setPointerAndSizeArray.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthSetPortion, currentData.bitsPerSetPointer);
    }

    private int absoluteBucketValue(HollowSetTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.elementArray.getElementValue(absoluteBucketIndex * currentData.bitsPerElement, currentData.bitsPerElement);
    }

    @Override
    public HollowOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        if(size(ordinal) == 0)
            return EmptyOrdinalIterator.INSTANCE;
        return new PotentialMatchHollowSetOrdinalIterator(ordinal, this, hashCode);
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
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
        setCurrentData(null);
    }

    HollowSetTypeDataElements currentDataElements() {
        return currentData;
    }

    private boolean readWasUnsafe(HollowSetTypeDataElements data) {
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowSetTypeDataElements data) {
        this.currentData = data;
        this.currentDataVolatile = data;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowSetTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            int numBuckets = HashCodes.hashTableSize(size(ordinal));
            long offset = getAbsoluteBucketStart(currentData, ordinal);

            checksum.applyInt(ordinal);
            for(int i=0;i<numBuckets;i++) {
                int bucketValue = absoluteBucketValue(currentData, offset + i);
                if(bucketValue != currentData.emptyBucketValue) {
                    checksum.applyInt(i);
                    checksum.applyInt(bucketValue);
                }
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
		long requiredBitsForSetPointers = (long)currentData.bitsPerFixedLengthSetPortion * (currentData.maxOrdinal + 1);
		long requiredBitsForBuckets = (long)currentData.bitsPerElement * currentData.totalNumberOfBuckets;
		long requiredBits = requiredBitsForSetPointers + requiredBitsForBuckets;
		return requiredBits / 8;
	}
	
	@Override
	public long getApproximateHoleCostInBytes() {
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        
        return ((long)(populatedOrdinals.length() - populatedOrdinals.cardinality()) * (long)currentData.bitsPerFixedLengthSetPortion) / 8; 
	}
	
	public HollowPrimaryKeyValueDeriver getKeyDeriver() {
	    return keyDeriver;
	}
	
	public void buildKeyDeriver() {
	    if(getSchema().getHashKey() != null)
	        this.keyDeriver = new HollowPrimaryKeyValueDeriver(getSchema().getHashKey(), getStateEngine());
	}

}